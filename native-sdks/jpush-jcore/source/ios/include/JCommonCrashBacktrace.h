#pragma once
#import <Foundation/Foundation.h>
#import <dlfcn.h>
#import <mach-o/loader.h>
#import <mach/mach.h>
#import <sys/ucontext.h>
@class JCommonCrashFrame;
@interface JCommonCrashBacktrace : NSObject
+ (void)load;
+ (NSArray *)JCommon_backtraceOfNSThread:(NSThread *)thread;
+ (NSArray *)JCommon_backtraceOfCurrentThread;
+ (NSArray *)JCommon_CallStackFromException:(NSArray *)callStack;
+ (long long)getThreadId:(NSThread *)thread;
+ (NSArray *)JCommon_backtraceOfMainThread;
+ (NSArray *)JCommon_backtraceOfAllThread;
@end
FOUNDATION_EXPORT bool JCommon_fillThreadStateIntoMachineContext(thread_t thread, mcontext_t machineContext);
FOUNDATION_EXPORT uintptr_t JCommon_mach_instructionAddress(const mcontext_t machineContext);
FOUNDATION_EXPORT uintptr_t JCommon_mach_linkRegister(const mcontext_t machineContext);
FOUNDATION_EXPORT uintptr_t JCommon_mach_framePointer(const mcontext_t machineContext);
FOUNDATION_EXPORT uintptr_t JCommon_mach_stackPointer(const mcontext_t machineContext);
FOUNDATION_EXPORT kern_return_t JCommon_mach_copyMem(const void *source, void *destination, size_t length);
FOUNDATION_EXPORT const char *JCommon_lastPathEntry(const char *path);
FOUNDATION_EXPORT uintptr_t JCommon_firstCmdAfterHeader(const struct mach_header *header);
FOUNDATION_EXPORT uintptr_t JCommon_segmentBaseOfImageIndex(uint32_t imageIndex);
FOUNDATION_EXPORT uint32_t JCommon_imageIndexContainingAddress(uintptr_t address);
FOUNDATION_EXPORT bool JCommon_dladdr(uintptr_t address, Dl_info *info);
FOUNDATION_EXPORT void JCommon_symbolicate(const uintptr_t *backtraceBuffer, Dl_info *symbolsBuffer,
                                           int numEntries, int skippedEntries);
FOUNDATION_EXPORT JCommonCrashFrame *JCommon_logBacktraceEntry(int entryNum, uintptr_t address,
                                                               const Dl_info *info);
FOUNDATION_EXPORT thread_t JCommon_machThreadFromNSThread(NSThread *thread);
