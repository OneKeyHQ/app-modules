#include "SharedRPC.h"

// Static member definitions
std::mutex SharedRPC::mutex_;
std::unordered_map<std::string, RPCValue> SharedRPC::slots_;

void SharedRPC::install(jsi::Runtime &rt) {
  auto rpc = std::make_shared<SharedRPC>();
  auto obj = jsi::Object::createFromHostObject(rt, rpc);
  rt.global().setProperty(rt, "sharedRPC", std::move(obj));
}

void SharedRPC::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
  slots_.clear();
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
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 2 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedRPC.write expects (callId: string, value)");
          }
          auto callId = args[0].getString(rt).utf8(rt);
          auto value = extractValue(rt, args[1]);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            slots_.insert_or_assign(std::move(callId), std::move(value));
          }
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

  return jsi::Value::undefined();
}

std::vector<jsi::PropNameID> SharedRPC::getPropertyNames(jsi::Runtime &rt) {
  std::vector<jsi::PropNameID> props;
  props.push_back(jsi::PropNameID::forUtf8(rt, "write"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "read"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "has"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "pendingCount"));
  return props;
}
