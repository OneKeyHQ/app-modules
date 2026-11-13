#include "onekey_wrapper/capture.h"

#include <windows.h>
#include <aclapi.h>
#include "base/logging.h"
#include <algorithm>
#include <cstring>
#include <limits>
#include <vector>

#include "minidump/minidump_file_writer.h"
#include "snapshot/win/process_snapshot_win.h"
#include "util/file/file_writer.h"
#include "util/win/registration_protocol_win_structs.h"

static_assert(sizeof(crashpad::ExceptionInformation) == 12);
static_assert(offsetof(crashpad::ExceptionInformation, thread_id) == 8);

namespace {
struct AclState {
  std::vector<uint8_t> bytes;
  bool protected_dacl = false;
};
bool ReadAcl(HANDLE target, AclState* state) {
  PACL acl = nullptr;
  PSECURITY_DESCRIPTOR descriptor = nullptr;
  const DWORD error = GetSecurityInfo(target, SE_KERNEL_OBJECT,
      DACL_SECURITY_INFORMATION, nullptr, nullptr, &acl, nullptr, &descriptor);
  if (error != ERROR_SUCCESS || !descriptor) return false;
  SECURITY_DESCRIPTOR_CONTROL control = 0;
  DWORD revision = 0;
  bool valid = acl && IsValidAcl(acl) &&
      GetSecurityDescriptorControl(descriptor, &control, &revision);
  if (valid) {
    const auto* begin = reinterpret_cast<const uint8_t*>(acl);
    state->bytes.assign(begin, begin + acl->AclSize);
    state->protected_dacl = (control & SE_DACL_PROTECTED) != 0;
  }
  LocalFree(descriptor);
  return valid;
}
class BoundedWriter final : public crashpad::FileWriterInterface {
 public:
  BoundedWriter(uint8_t* output, size_t capacity)
      : output_(output), capacity_(capacity) {}
  bool Write(const void* data, size_t size) override {
    if (position_ > capacity_ || size > capacity_ - position_) {
      exhausted_ = true;
      return false;
    }
    if (size && !data) return false;
    if (position_ > size_) std::memset(output_ + size_, 0, position_ - size_);
    if (size) std::memcpy(output_ + position_, data, size);
    position_ += size;
    size_ = std::max(size_, position_);
    return true;
  }
  bool WriteIoVec(std::vector<crashpad::WritableIoVec>* vectors) override {
    size_t total = 0;
    for (const auto& part : *vectors) {
      if (part.iov_len > capacity_ - total) {
        exhausted_ = true;
        return false;
      }
      total += part.iov_len;
    }
    if (total > capacity_ - position_) {
      exhausted_ = true;
      return false;
    }
    for (const auto& part : *vectors) {
      if (!Write(part.iov_base, part.iov_len)) return false;
    }
    return true;
  }
  crashpad::FileOffset Seek(crashpad::FileOffset offset, int whence) override {
    size_t base;
    if (whence == SEEK_SET) base = 0;
    else if (whence == SEEK_CUR) base = position_;
    else if (whence == SEEK_END) base = size_;
    else return -1;
    size_t next;
    if (offset < 0) {
      const uint64_t magnitude = static_cast<uint64_t>(-(offset + 1)) + 1;
      if (magnitude > base) return -1;
      next = base - static_cast<size_t>(magnitude);
    } else {
      if (static_cast<uint64_t>(offset) > capacity_ - base) {
        exhausted_ = true;
        return -1;
      }
      next = base + static_cast<size_t>(offset);
    }
    position_ = next;
    return static_cast<crashpad::FileOffset>(next);
  }
  size_t size() const { return size_; }
  bool exhausted() const { return exhausted_; }
  void Erase() {
    if (size_) SecureZeroMemory(output_, size_);
    size_ = 0;
  }
 private:
  uint8_t* output_;
  size_t capacity_;
  size_t position_ = 0;
  size_t size_ = 0;
  bool exhausted_ = false;
};

int CaptureFaultThread(HANDLE target, uint64_t exception_information,
                  BoundedWriter* writer) {
  crashpad::ProcessSnapshotWin snapshot;
  // The pinned local Crashpad patch reads only the fault thread's saved CONTEXT.
  // It never suspends a target thread, so the crash callback can keep its deadline.
  if (!snapshot.Initialize(target, crashpad::ProcessSuspensionState::kRunning,
                           exception_information, 0)) return 3;
  if (!snapshot.Exception()) return 4;
  crashpad::MinidumpFileWriter minidump;
  minidump.InitializeFromSnapshot(&snapshot);
  if (!minidump.WriteEverything(writer)) return writer->exhausted() ? 5 : 6;
  return 0;
}
}  // namespace

extern "C" int onekey_crashpad_capture(void* target,
    uint64_t exception_information, uint8_t* output, size_t capacity,
    size_t* output_size) {
  if (!output_size) return 1;
  *output_size = 0;
  if (!target || target == INVALID_HANDLE_VALUE || !exception_information ||
      !output || capacity < 32 || capacity > 128u * 1024u * 1024u ||
      GetProcessId(target) == GetCurrentProcessId()) return 1;
  logging::LoggingSettings settings;
  settings.logging_dest = logging::LOG_NONE;
  logging::InitLogging(settings);
  AclState before;
  AclState after;
  BoundedWriter writer(output, capacity);
  int result = ReadAcl(target, &before)
      ? CaptureFaultThread(target, exception_information, &writer) : 8;
  if (!ReadAcl(target, &after) || before.bytes != after.bytes ||
      before.protected_dacl != after.protected_dacl) result = 8;
  if (result || writer.size() < 32 || std::memcmp(output, "MDMP", 4) != 0) {
    writer.Erase();
    return result ? result : 6;
  }
  *output_size = writer.size();
  return 0;
}
