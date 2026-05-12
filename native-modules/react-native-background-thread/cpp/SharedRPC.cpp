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

  auto alive = std::make_shared<std::atomic<bool>>(true);

  std::lock_guard<std::mutex> lock(mutex_);
  // Defensive dedup: under the normal restart flow, invalidate() has already
  // run for this runtimeId and erased the entry, so this loop matches
  // nothing. The branch survives as a fallback for any path that re-installs
  // without first calling invalidate (e.g. legacy host integrations, partial
  // teardown). Same correctness invariants as invalidate(): flip alive=false
  // so any executor lambda already in flight short-circuits, and leak the
  // jsi::Function callback because destroying it on a wrong/dying thread
  // crashes (null deref in Pointer::~Pointer).
  for (auto &listener : listeners_) {
    if (listener.runtimeId == runtimeId) {
      if (listener.alive) {
        listener.alive->store(false);
      }
      if (listener.callback) {
        new std::shared_ptr<jsi::Function>(std::move(listener.callback));
      }
    }
  }
  listeners_.erase(
      std::remove_if(listeners_.begin(), listeners_.end(),
                     [&runtimeId](const RuntimeListener &l) {
                       return l.runtimeId == runtimeId;
                     }),
      listeners_.end());
  listeners_.push_back(
      {runtimeId, &rt, std::move(executor), nullptr, std::move(alive)});
}

bool SharedRPC::invalidate(const std::string &runtimeId) {
  std::lock_guard<std::mutex> lock(mutex_);
  bool found = false;
  for (auto &listener : listeners_) {
    if (listener.runtimeId != runtimeId) continue;
    if (listener.alive) {
      listener.alive->store(false);
    }
    if (listener.callback) {
      // Same rationale as install(): destroying a jsi::Function tied to a
      // torn-down runtime crashes. Leak it; the runtime is going away anyway.
      new std::shared_ptr<jsi::Function>(std::move(listener.callback));
    }
    // Drop the executor closure so nothing tries to dispatch via the dying
    // RCTInstance/CallInvoker after this point.
    listener.executor = nullptr;
    found = true;
  }
  // Erase the dead entries. Already-dispatched executor lambdas hold their
  // own shared_ptr<alive> snapshot, so erasing here does not affect them —
  // it only prevents NEW notifyOtherRuntime() snapshots from picking up the
  // dead listener. Without the erase, a mode='all' restart whose post-reload
  // re-install never fires would leave a permanently-dead entry in the
  // vector. The next install() for the same runtimeId pushes a fresh entry.
  listeners_.erase(
      std::remove_if(listeners_.begin(), listeners_.end(),
                     [&runtimeId](const RuntimeListener &l) {
                       return l.runtimeId == runtimeId;
                     }),
      listeners_.end());
  return found;
}

void SharedRPC::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
  slots_.clear();
  // Intentionally leak jsi::Function callbacks to avoid destroying them on the
  // wrong thread (same rationale as the leak in install() for reload scenarios).
  // Also flip alive=false so any executor lambda still in flight short-circuits
  // before touching a torn-down runtime.
  for (auto &listener : listeners_) {
    if (listener.alive) {
      listener.alive->store(false);
    }
    if (listener.callback) {
      new std::shared_ptr<jsi::Function>(std::move(listener.callback));
    }
  }
  listeners_.clear();
}

void SharedRPC::notifyOtherRuntime(jsi::Runtime &callerRt, const std::string &callId) {
  // Collect executors and callbacks under lock, then invoke outside lock
  // to avoid deadlock (executor may schedule work that also acquires mutex_).
  //
  // Each snapshot carries the listener's shared `alive` flag. The flag is
  // checked twice — once here (so an already-invalidated listener is never
  // even scheduled) and once again inside the dispatched lambda (so a
  // listener invalidated AFTER snapshot but BEFORE the lambda runs is also
  // short-circuited before touching the dying runtime).
  struct Snapshot {
    RPCRuntimeExecutor executor;
    std::shared_ptr<jsi::Function> callback;
    std::shared_ptr<std::atomic<bool>> alive;
  };
  std::vector<Snapshot> toNotify;
  {
    std::lock_guard<std::mutex> lock(mutex_);
    RPC_LOG("notifyOtherRuntime: callId=%s, listeners=%zu, callerRt=%p",
            callId.c_str(), listeners_.size(), &callerRt);
    for (auto &listener : listeners_) {
      RPC_LOG("  listener: id=%s, rt=%p, hasCallback=%d, alive=%d",
              listener.runtimeId.c_str(), listener.runtime,
              listener.callback != nullptr,
              listener.alive ? listener.alive->load() : 0);
      if (listener.runtime == &callerRt) continue;
      if (!listener.callback) continue;
      if (!listener.executor) continue;
      if (!listener.alive || !listener.alive->load()) continue;
      toNotify.push_back({listener.executor, listener.callback, listener.alive});
    }
    RPC_LOG("  toNotify count: %zu", toNotify.size());
  }

  for (auto &snap : toNotify) {
    auto id = callId;
    RPC_LOG("  invoking executor for callId=%s", id.c_str());
    auto cb = snap.callback;
    auto alive = snap.alive;
    snap.executor([cb, alive, id](jsi::Runtime &rt) {
      // Listener was invalidated between snapshot and dispatch — bail
      // before calling into a runtime that may already be torn down.
      if (!alive || !alive->load()) {
        RPC_LOG("  executor work skipped (listener invalidated) for callId=%s",
                id.c_str());
        return;
      }
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
