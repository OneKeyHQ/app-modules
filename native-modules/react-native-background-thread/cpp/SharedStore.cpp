#include "SharedStore.h"

// Static member definitions
std::mutex SharedStore::mutex_;
std::unordered_map<std::string, StoreValue> SharedStore::data_;

void SharedStore::install(jsi::Runtime &rt) {
  auto store = std::make_shared<SharedStore>();
  auto obj = jsi::Object::createFromHostObject(rt, store);
  rt.global().setProperty(rt, "sharedStore", std::move(obj));
}

void SharedStore::reset() {
  std::lock_guard<std::mutex> lock(mutex_);
  data_.clear();
}

StoreValue SharedStore::extractValue(jsi::Runtime &rt,
                                     const jsi::Value &val) {
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
                     "SharedStore: unsupported value type. "
                     "Only bool, number, and string are supported.");
}

jsi::Value SharedStore::toJSI(jsi::Runtime &rt, const StoreValue &val) {
  if (std::holds_alternative<bool>(val)) {
    return jsi::Value(std::get<bool>(val));
  }
  if (std::holds_alternative<double>(val)) {
    return jsi::Value(std::get<double>(val));
  }
  if (std::holds_alternative<std::string>(val)) {
    return jsi::String::createFromUtf8(rt, std::get<std::string>(val));
  }
  return jsi::Value::undefined();
}

jsi::Value SharedStore::get(jsi::Runtime &rt, const jsi::PropNameID &name) {
  auto prop = name.utf8(rt);

  // set(key: string, value: bool | number | string): void
  if (prop == "set") {
    return jsi::Function::createFromHostFunction(
        rt, name, 2,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 2 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedStore.set expects (string key, value)");
          }
          auto key = args[0].getString(rt).utf8(rt);
          auto val = extractValue(rt, args[1]);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            data_[std::move(key)] = std::move(val);
          }
          return jsi::Value::undefined();
        });
  }

  // get(key: string): bool | number | string | undefined
  if (prop == "get") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedStore.get expects a string key");
          }
          auto key = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            auto it = data_.find(key);
            if (it == data_.end()) {
              return jsi::Value::undefined();
            }
            return toJSI(rt, it->second);
          }
        });
  }

  // has(key: string): boolean
  if (prop == "has") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedStore.has expects a string key");
          }
          auto key = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            return jsi::Value(data_.count(key) > 0);
          }
        });
  }

  // delete(key: string): boolean
  if (prop == "delete") {
    return jsi::Function::createFromHostFunction(
        rt, name, 1,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *args,
           size_t count) -> jsi::Value {
          if (count < 1 || !args[0].isString()) {
            throw jsi::JSError(
                rt, "SharedStore.delete expects a string key");
          }
          auto key = args[0].getString(rt).utf8(rt);
          {
            std::lock_guard<std::mutex> lock(mutex_);
            return jsi::Value(data_.erase(key) > 0);
          }
        });
  }

  // keys(): string[]
  if (prop == "keys") {
    return jsi::Function::createFromHostFunction(
        rt, name, 0,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *,
           size_t) -> jsi::Value {
          std::vector<std::string> allKeys;
          {
            std::lock_guard<std::mutex> lock(mutex_);
            allKeys.reserve(data_.size());
            for (const auto &pair : data_) {
              allKeys.push_back(pair.first);
            }
          }
          auto arr = jsi::Array(rt, allKeys.size());
          for (size_t i = 0; i < allKeys.size(); i++) {
            arr.setValueAtIndex(
                rt, i, jsi::String::createFromUtf8(rt, allKeys[i]));
          }
          return arr;
        });
  }

  // clear(): void
  if (prop == "clear") {
    return jsi::Function::createFromHostFunction(
        rt, name, 0,
        [](jsi::Runtime &rt, const jsi::Value &, const jsi::Value *,
           size_t) -> jsi::Value {
          {
            std::lock_guard<std::mutex> lock(mutex_);
            data_.clear();
          }
          return jsi::Value::undefined();
        });
  }

  // size: number (getter, not a function)
  if (prop == "size") {
    std::lock_guard<std::mutex> lock(mutex_);
    return jsi::Value(static_cast<double>(data_.size()));
  }

  return jsi::Value::undefined();
}

std::vector<jsi::PropNameID> SharedStore::getPropertyNames(jsi::Runtime &rt) {
  std::vector<jsi::PropNameID> props;
  props.push_back(jsi::PropNameID::forUtf8(rt, "set"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "get"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "has"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "delete"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "keys"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "clear"));
  props.push_back(jsi::PropNameID::forUtf8(rt, "size"));
  return props;
}
