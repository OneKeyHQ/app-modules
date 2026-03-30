#include "SharedRPC.h"

#ifdef __ANDROID__
#include <android/log.h>
#define RPC_LOG(...) __android_log_print(ANDROID_LOG_INFO, "SharedRPC", __VA_ARGS__)
#else
#define RPC_LOG(...)
#endif

std::mutex SharedRPC::mutex_;
std::unordered_map<std::string, RPCValue> SharedRPC::slots_;
std::vector<RuntimeListener> SharedRPC::listeners_;

void SharedRPC::install(jsi::Runtime &rt) {
  auto rpc = std::make_shared<SharedRPC>();
  auto obj = jsi::Object::createFromHostObject(rt, rpc);
  rt.global().setProperty(rt, "sharedRPC", std::move(obj));
}

void SharedRPC::install(jsi::Runtime &rt, RPCRuntimeExecutor executor,
                        const std::string &runtimeId) {
  auto rpc = std::make_shared<SharedRPC>();
  auto obj = jsi::Object::createFromHostObject(rt, rpc);
  rt.global().setProperty(rt, "sharedRPC", std::move(obj));

  std::lock_guard<std::mutex> lock(mutex_);
  // Remove any existing listener with the same runtimeId (reload scenario).
  // IMPORTANT: The old listener's callback is a jsi::Function tied to the old
  // runtime. On reload, that runtime is already destroyed, so calling
  // ~jsi::Function() would crash (null deref in Pointer::~Pointer).
  // We intentionally leak the callback to avoid this.
  for (auto &listener : listeners_) {
    if (listener.runtimeId == runtimeId && listener.callback) {
      new std::shared_ptr<jsi::Function>(std::move(listener.callback));
    }
  }
  listeners_.erase(
      std::remove_if(listeners_.begin(), listeners_.end(),
                     [&runtimeId](const RuntimeListener &l) {
                       return l.runtimeId == runtimeId;
                     }),
      listeners_.end());
  listeners_.push_back({runtimeId, &rt, std::move(executor), nullptr});
}

void SharedRPC::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
  slots_.clear();
  listeners_.clear();
}

void SharedRPC::notifyOtherRuntime(jsi::Runtime &callerRt, const std::string &callId) {
  // Collect executors and callbacks under lock, then invoke outside lock
  // to avoid deadlock (executor may schedule work that also acquires mutex_).
  std::vector<std::pair<RPCRuntimeExecutor, std::shared_ptr<jsi::Function>>> toNotify;
  {
    std::lock_guard<std::mutex> lock(mutex_);
    RPC_LOG("notifyOtherRuntime: callId=%s, listeners=%zu, callerRt=%p",
            callId.c_str(), listeners_.size(), &callerRt);
    for (auto &listener : listeners_) {
      RPC_LOG("  listener: id=%s, rt=%p, hasCallback=%d",
              listener.runtimeId.c_str(), listener.runtime, listener.callback != nullptr);
      if (listener.runtime == &callerRt) continue;
      if (!listener.callback) continue;
      toNotify.emplace_back(listener.executor, listener.callback);
    }
    RPC_LOG("  toNotify count: %zu", toNotify.size());
  }

  for (auto &[executor, cb] : toNotify) {
    auto id = callId;
    RPC_LOG("  invoking executor for callId=%s", id.c_str());
    executor([cb, id](jsi::Runtime &rt) {
      RPC_LOG("  executor work running for callId=%s", id.c_str());
      try {
        cb->call(rt, jsi::String::createFromUtf8(rt, id));
        RPC_LOG("  cb->call succeeded for callId=%s", id.c_str());
      } catch (const jsi::JSError &e) {
        RPC_LOG("  JSError in cb->call: %s", e.getMessage().c_str());
      } catch (...) {
        RPC_LOG("  Unknown error in cb->call");
      }
    });
  }
}

RPCValue SharedRPC::extractValue(jsi::Runtime &rt, const jsi::Value &val) {
  if (val.isBool()) {
    return val.getBool();
  }
  if (val.isNumber()) {
    return val.getNumber();
  }
  if (val.isString()) {
    return val.getString(rt).utf8(rt);
  }
  throw jsi::JSError(rt,
                     "SharedRPC: unsupported value type. "
                     "Only bool, number, and string are supported.");
}

jsi::Value SharedRPC::toJSI(jsi::Runtime &rt, const RPCValue &val) {
  if (std::holds_alternative<bool>(val)) {
    return jsi::Value(std::get<bool>(val));
  }
  if (std::holds_alternative<double>(val)) {
    return jsi::Value(std::get<double>(val));
  }
  // std::string
  return jsi::String::createFromUtf8(rt, std::get<std::string>(val));
}

jsi::Value SharedRPC::get(jsi::Runtime &rt, const jsi::PropNameID &name) {
  auto prop = name.utf8(rt);

  // write(callId: string, value: bool | number | string): void
  if (prop == "write") {
    return jsi::Function::createFromHostFunction(
        rt, name, 2,
        [this](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
               size_t count) -> jsi::Value {
          if (count < 2 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedRPC.write expects (callId: string, value)");
          }
          auto callId = args[0].getString(rt).utf8(rt);
          auto value = extractValue(rt, args[1]);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            slots_.insert_or_assign(callId, std::move(value));
          }
          // Notify OUTSIDE the lock
          notifyOtherRuntime(rt, callId);
          return jsi::Value::undefined();
        });
  }

  // read(callId: string): bool | number | string | undefined
  // Deletes the entry after reading (read-and-delete semantics).
  if (prop == "read") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedRPC.read expects (callId: string)");
          }
          auto callId = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            auto it = slots_.find(callId);
            if (it == slots_.end()) {
              return jsi::Value::undefined();
            }
            auto value = std::move(it->second);
            slots_.erase(it);
            return toJSI(rt, value);
          }
        });
  }

  // has(callId: string): boolean
  if (prop == "has") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedRPC.has expects (callId: string)");
          }
          auto callId = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            return jsi::Value(slots_.count(callId) > 0);
          }
        });
  }

  // pendingCount: number (getter, not a function)
  if (prop == "pendingCount") {
    std::lock_guard<std::mutex> lock(mutex_);
    return jsi::Value(static_cast<double>(slots_.size()));
  }

  // onWrite(callback: (callId: string) => void): void
  if (prop == "onWrite") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isObject() ||
              !args[0].asObject(rt).isFunction(rt)) {
            throw jsi::JSError(rt, "SharedRPC.onWrite expects a function");
          }
          auto fn = std::make_shared<jsi::Function>(
              args[0].asObject(rt).asFunction(rt));
          {
            std::lock_guard<std::mutex> lock(mutex_);
            for (auto &listener : listeners_) {
              if (listener.runtime == &rt) {
                listener.callback = std::move(fn);
                break;
              }
            }
          }
          return jsi::Value::undefined();
        });
  }

  return jsi::Value::undefined();
}

std::vector<jsi::PropNameID> SharedRPC::getPropertyNames(jsi::Runtime &rt) {
  std::vector<jsi::PropNameID> props;
  props.push_back(jsi::PropNameID::forUtf8(rt, "write"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "read"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "has"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "pendingCount"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "onWrite"));
  return props;
}
