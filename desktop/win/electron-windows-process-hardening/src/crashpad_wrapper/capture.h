#pragma once
#include <stddef.h>
#include <stdint.h>
#ifdef ONEKEY_CAPTURE_DLL
#define CAPTURE_API __declspec(dllexport)
#else
#define CAPTURE_API
#endif
#ifdef __cplusplus
extern "C" {
#endif
// target is a preauthorized real process HANDLE; no PID-based reopen is exposed.
// exception_information is a target address: packed u64 EXCEPTION_POINTERS + u32 TID.
// Success = 0; failures leave *output_size = 0 and erase any partial output.
// 1 arguments, 3 snapshot, 4 missing exception, 5 capacity,
// 6 serialization, 8 DACL read/equality. Caller owns all arguments and output memory.
CAPTURE_API int onekey_crashpad_capture(void* target, uint64_t exception_information,
  uint8_t* output, size_t capacity, size_t* output_size);
#ifdef __cplusplus
}
#endif
