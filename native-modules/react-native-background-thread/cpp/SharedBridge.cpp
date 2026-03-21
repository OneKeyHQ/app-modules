#include "SharedBridge.h"

// Static member definitions
std::mutex SharedBridge::mutex_;
std::queue<std::string> SharedBridge::mainQueue_;
std::queue<std::string> SharedBridge::bgQueue_;
std::atomic<bool> SharedBridge::hasMainMsg_{false};
std::atomic<bool> SharedBridge::hasBgMsg_{false};

void SharedBridge::install(jsi::Runtime &rt, bool isMain) {
  auto bridge = std::make_shared<SharedBridge>(isMain);
  auto obj = jsi::Object::createFromHostObject(rt, bridge);
  rt.global().setProperty(rt, "sharedBridge", std::move(obj));
}

void SharedBridge::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
  std::queue<std::string>().swap(mainQueue_);
  std::queue<std::string>().swap(bgQueue_);
  hasMainMsg_.store(false);
  hasBgMsg_.store(false);
}

jsi::Value SharedBridge::get(jsi::Runtime &rt, const jsi::PropNameID &name) {
  auto prop = name.utf8(rt);

  // send(message: string): void
  // Pushes a string message into the OTHER runtime's queue.
  if (prop == "send") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [this](jsi::Runtime &rt, const jsi::Value &,
               const jsi::Value *args, size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(rt,
                               "SharedBridge.send expects a string argument");
          }
          auto msg = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            if (isMain_) {
              bgQueue_.push(std::move(msg));
              hasBgMsg_.store(true);
            } else {
              mainQueue_.push(std::move(msg));
              hasMainMsg_.store(true);
            }
          }
          return jsi::Value::undefined();
        });
  }

  // drain(): string[]
  // Pulls all pending messages from this runtime's queue.
  if (prop == "drain") {
    return jsi::Function::createFromHostFunction(
        rt, name, 0,
        [this](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *,
               size_t) -> jsi::Value {
          std::vector<std::string> msgs;
          {
            std::lock_guard<std::mutex> lock(mutex_);
            auto &q = isMain_ ? mainQueue_ : bgQueue_;
            auto &flag = isMain_ ? hasMainMsg_ : hasBgMsg_;
            while (!q.empty()) {
              msgs.push_back(std::move(q.front()));
              q.pop();
            }
            flag.store(false);
          }
          auto arr = jsi::Array(rt, msgs.size());
          for (size_t i = 0; i < msgs.size(); i++) {
            arr.setValueAtIndex(
                rt, i, jsi::String::createFromUtf8(rt, msgs[i]));
          }
          return arr;
        });
  }

  // hasMessages: boolean (getter)
  // Returns true if there are pending messages for this runtime.
  // Uses atomic load — essentially zero overhead.
  if (prop == "hasMessages") {
    bool has = isMain_ ? hasMainMsg_.load(std::memory_order_relaxed)
                       : hasBgMsg_.load(std::memory_order_relaxed);
    return jsi::Value(has);
  }

  // isMain: boolean (getter)
  if (prop == "isMain") {
    return jsi::Value(isMain_);
  }

  return jsi::Value::undefined();
}

std::vector<jsi::PropNameID> SharedBridge::getPropertyNames(jsi::Runtime &rt) {
  std::vector<jsi::PropNameID> props;
  props.push_back(jsi::PropNameID::forUtf8(rt, "send"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "drain"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "hasMessages"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "isMain"));
  return props;
}
