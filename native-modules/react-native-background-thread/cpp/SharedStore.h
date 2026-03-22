#pragma once

#include <jsi/jsi.h>
#include <mutex>
#include <string>
#include <unordered_map>
#include <variant>
#include <vector>

namespace jsi = facebook::jsi;

using StoreValue = std::variant<bool, double, std::string>;

class SharedStore : public jsi::HostObject {
public:
  jsi::Value get(jsi::Runtime &rt, const jsi::PropNameID &name) override;
  std::vector<jsi::PropNameID> getPropertyNames(jsi::Runtime &rt) override;

  static void install(jsi::Runtime &rt);
  static void reset();

private:
  static StoreValue extractValue(jsi::Runtime &rt, const jsi::Value &val);
  static jsi::Value toJSI(jsi::Runtime &rt, const StoreValue &val);

  static std::mutex mutex_;
  static std::unordered_map<std::string, StoreValue> data_;
};
