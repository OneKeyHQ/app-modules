#pragma once

#include <jsi/jsi.h>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <unordered_map>
#include <variant>
#include <vector>

namespace jsi = facebook::jsi;

using RPCValue = std::variant<bool, double, std::string>;

// Executes a callback on a specific runtime's JS thread.
// The implementation is platform-specific (iOS vs Android).
using RPCRuntimeExecutor = std::function<void(std::function<void(jsi::Runtime &)>)>;

struct RuntimeListener {
  std::string runtimeId;                   // "main" or "background"
  jsi::Runtime *runtime;
  RPCRuntimeExecutor executor;
  std::shared_ptr<jsi::Function> callback; // JS onWrite callback
};

class SharedRPC : public jsi::HostObject {
public:
  jsi::Value get(jsi::Runtime &rt, const jsi::PropNameID &name) override;
  std::vector<jsi::PropNameID> getPropertyNames(jsi::Runtime &rt) override;

  /// Legacy install without executor (no cross-runtime notification)
  static void install(jsi::Runtime &rt);

  /// Install with executor — enables cross-runtime write notifications
  /// runtimeId should be "main" or "background" — used for dedup on reload.
  static void install(jsi::Runtime &rt, RPCRuntimeExecutor executor,
                      const std::string &runtimeId);

  static void reset();

private:
  static RPCValue extractValue(jsi::Runtime &rt, const jsi::Value &val);
  static jsi::Value toJSI(jsi::Runtime &rt, const RPCValue &val);
  void notifyOtherRuntime(jsi::Runtime &callerRt, const std::string &callId);

  static std::mutex mutex_;
  static std::unordered_map<std::string, RPCValue> slots_;
  static std::vector<RuntimeListener> listeners_;
};
