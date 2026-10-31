#include <type_traits>
#define NOMINMAX
#include <windows.h>
#include "v8.h"

using ExactSetter = decltype(&v8::V8::SetUnhandledExceptionCallback);
static_assert(std::is_same_v<ExactSetter,
              void (*)(v8::UnhandledExceptionCallback)>);

extern "C" int onekey_install_v8_callback(void* symbol,
                                            v8::UnhandledExceptionCallback callback) {
  if (symbol == nullptr || callback == nullptr) return 0;
  reinterpret_cast<ExactSetter>(symbol)(callback);
  return 1;
}

extern "C" __declspec(noinline) int onekey_crash_level_three() {
  void* allocation = VirtualAlloc(nullptr, 4096, MEM_COMMIT | MEM_RESERVE,
                                  PAGE_NOACCESS);
  if (allocation == nullptr) TerminateProcess(GetCurrentProcess(), 1);
  volatile int* page = reinterpret_cast<volatile int*>(allocation);
  return *page;
}

extern "C" __declspec(noinline) int onekey_crash_level_two() {
  int value = onekey_crash_level_three();
  volatile int after_call = value + 2;
  return after_call;
}

extern "C" __declspec(noinline) int onekey_crash_level_one() {
  int value = onekey_crash_level_two();
  volatile int after_call = value + 1;
  return after_call;
}
