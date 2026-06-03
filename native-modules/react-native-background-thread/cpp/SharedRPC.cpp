#include "SharedRPC.h"

#include "SharedStore.h"

#include <algorithm>

#ifdef __ANDROID__
#include <android/log.h>
#define RPC_LOG(...) __android_log_print(ANDROID_LOG_INFO, "SharedRPC", __VA_ARGS__)
#else
#define RPC_LOG(...)
#endif

std::mutex SharedRPC::mutex_;
std::vector<RuntimeListener> SharedRPC::listeners_;
std::unordered_map<std::string, std::string> SharedRPC::readinessKeys_;

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

  // Restart freshness: drop this runtime's latched readiness key from
  // SharedStore so a respawned reader can never observe a prior-life
  // "peer ready". Robust to crash-restart because it runs in the native
  // invalidate path, not a JS teardown hook. SharedStore::eraseKey takes its
  // own mutex (distinct from mutex_, only ever locked SharedRPC→SharedStore).
  auto keyIt = readinessKeys_.find(runtimeId);
  if (keyIt != readinessKeys_.end()) {
    SharedStore::eraseKey(keyIt->second);
  }
  return found;
}

void SharedRPC::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
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

void SharedRPC::notifyOtherRuntime(jsi::Runtime &callerRt,
                                   const std::string &callId, RPCValue value) {
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

  for (size_t i = 0; i < toNotify.size(); ++i) {
    auto &snap = toNotify[i];
    auto id = callId;
    RPC_LOG("  invoking executor for callId=%s", id.c_str());
    auto cb = snap.callback;
    auto alive = snap.alive;
    // The payload rides the dispatched lambda's capture. In practice there is
    // exactly one non-caller listener, so the last (only) iteration moves the
    // value; any earlier listener takes a copy. A captured RPCValue is a pure
    // C++ value type — its destructor is thread-agnostic, so dropping this
    // lambda during teardown is safe (unlike the per-runtime jsi::Function,
    // which is why `cb` is leaked on invalidate/reset). The jsi::String/value
    // is materialized via toJSI ONLY inside cb->call, i.e. on the target
    // runtime's JS thread where it is legal.
    RPCValue v = (i + 1 == toNotify.size()) ? std::move(value) : value;
    snap.executor([cb, alive, id, v = std::move(v)](jsi::Runtime &rt) {
      // Listener was invalidated between snapshot and dispatch — bail
      // before calling into a runtime that may already be torn down.
      if (!alive || !alive->load()) {
        RPC_LOG("  executor work skipped (listener invalidated) for callId=%s",
                id.c_str());
        return;
      }
      RPC_LOG("  executor work running for callId=%s", id.c_str());
      try {
        cb->call(rt, jsi::String::createFromUtf8(rt, id), toJSI(rt, v));
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
          RPCValue value = extractValue(rt, args[1]);
          // Value-inline: no slot map. The payload is handed straight to
          // notifyOtherRuntime, which moves it into the dispatched lambda's
          // capture and delivers it as the callback's 2nd argument on the
          // target runtime. No lock here — notify takes the lock internally.
          notifyOtherRuntime(rt, callId, std::move(value));
          return jsi::Value::undefined();
        });
  }

  // onWrite(callback: (callId: string, value: string|number|boolean) => void)
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

  // registerReadinessKey(key: string): void
  // The calling runtime declares which SharedStore key holds its readiness
  // payload, so invalidate() can clear exactly that key on teardown (restart
  // freshness). Identified by the calling runtime pointer → its listener's
  // runtimeId, mirroring how onWrite finds the listener.
  if (prop == "registerReadinessKey") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedRPC.registerReadinessKey expects (key: string)");
          }
          auto key = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            for (auto &listener : listeners_) {
              if (listener.runtime == &rt) {
                readinessKeys_[listener.runtimeId] = std::move(key);
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
  props.push_back(jsi::PropNameID::forUtf8(rt, "onWrite"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "registerReadinessKey"));
  return props;
}
