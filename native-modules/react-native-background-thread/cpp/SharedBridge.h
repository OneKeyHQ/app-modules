#pragma once

#include <jsi/jsi.h>
#include <atomic>
#include <mutex>
#include <queue>
#include <string>
#include <vector>

namespace jsi = facebook::jsi;

class SharedBridge : public jsi::HostObject {
public:
  jsi::Value get(jsi::Runtime &rt, const jsi::PropNameID &name) override;
  std::vector<jsi::PropNameID> getPropertyNames(jsi::Runtime &rt) override;

  /// Install a SharedBridge instance into the given runtime.
  /// @param rt The JSI runtime to install into.
  /// @param isMain true for the main (UI) runtime, false for background.
  static void install(jsi::Runtime &rt, bool isMain);

  /// Reset all queues and flags. Call when tearing down.
  static void reset();

private:
  explicit SharedBridge(bool isMain) : isMain_(isMain) {}

  bool isMain_;

  // Shared state across both runtimes (static, process-wide)
  static std::mutex mutex_;
  static std::queue<std::string> mainQueue_;  // messages destined for main
  static std::queue<std::string> bgQueue_;    // messages destined for background
  static std::atomic<bool> hasMainMsg_;
  static std::atomic<bool> hasBgMsg_;
};
