#import "JCommonCrashBacktrace.h"
#import "JCommonCrashStacktrace.h"
#import "RecoveredModels.h"
#import <mach-o/dyld.h>
#import <mach-o/nlist.h>
#import <pthread.h>

static thread_t jc_main_thread_id;

bool JCommon_fillThreadStateIntoMachineContext(thread_t thread, mcontext_t machineContext) {
#if defined(__arm64__)
    mach_msg_type_number_t count = ARM_THREAD_STATE64_COUNT;
    return thread_get_state(thread, ARM_THREAD_STATE64, (thread_state_t)&machineContext->__ss, &count) ==
           KERN_SUCCESS;
#elif defined(__x86_64__)
    mach_msg_type_number_t count = x86_THREAD_STATE64_COUNT;
    return thread_get_state(thread, x86_THREAD_STATE64, (thread_state_t)&machineContext->__ss, &count) ==
           KERN_SUCCESS;
#endif
}
uintptr_t JCommon_mach_instructionAddress(const mcontext_t machineContext) {
#if defined(__arm64__)
    return machineContext->__ss.__pc;
#elif defined(__x86_64__)
    return machineContext->__ss.__rip;
#endif
}
uintptr_t JCommon_mach_linkRegister(const mcontext_t machineContext) {
#if defined(__arm64__)
    return machineContext->__ss.__lr;
#elif defined(__x86_64__)
    return 0;
#endif
}
uintptr_t JCommon_mach_framePointer(const mcontext_t machineContext) {
#if defined(__arm64__)
    return machineContext->__ss.__fp;
#elif defined(__x86_64__)
    return machineContext->__ss.__rbp;
#endif
}
uintptr_t JCommon_mach_stackPointer(const mcontext_t machineContext) {
#if defined(__arm64__)
    return machineContext->__ss.__sp;
#elif defined(__x86_64__)
    return machineContext->__ss.__rsp;
#endif
}
kern_return_t JCommon_mach_copyMem(const void *source, void *destination, size_t length) {
    vm_size_t written = 0;
    return vm_read_overwrite(mach_task_self(), (vm_address_t)source, length, (vm_address_t)destination,
                             &written);
}
const char *JCommon_lastPathEntry(const char *path) {
    if (path) {
        const char *last = strrchr(path, '/');
        if (last)
            path = last + 1;
    }
    return path;
}
uintptr_t JCommon_firstCmdAfterHeader(const struct mach_header *header) {
    switch (header->magic) {
    case MH_MAGIC:
    case MH_CIGAM:
        return (uintptr_t)(header + 1);
    case MH_MAGIC_64:
    case MH_CIGAM_64:
        return (uintptr_t)((const struct mach_header_64 *)header + 1);
    default:
        return 0;
    }
}
uintptr_t JCommon_segmentBaseOfImageIndex(uint32_t imageIndex) {
    const struct mach_header *header = _dyld_get_image_header(imageIndex);
    uintptr_t command = JCommon_firstCmdAfterHeader(header);
    if (!command)
        return 0;
    for (uint32_t i = 0; i < header->ncmds; i++) {
        const struct load_command *load = (const void *)command;
        if (load->cmd == LC_SEGMENT_64) {
            const struct segment_command_64 *segment = (const void *)load;
            if (strcmp(segment->segname, "__LINKEDIT") == 0)
                return segment->vmaddr - segment->fileoff;
        } else if (load->cmd == LC_SEGMENT) {
            const struct segment_command *segment = (const void *)load;
            if (strcmp(segment->segname, "__LINKEDIT") == 0)
                return (uint32_t)(segment->vmaddr - segment->fileoff);
        }
        command += load->cmdsize;
    }
    return 0;
}
uint32_t JCommon_imageIndexContainingAddress(uintptr_t address) {
    uint32_t count = _dyld_image_count();
    for (uint32_t i = 0; i < count; i++) {
        const struct mach_header *header = _dyld_get_image_header(i);
        if (!header)
            continue;
        intptr_t slide = _dyld_get_image_vmaddr_slide(i);
        uintptr_t command = JCommon_firstCmdAfterHeader(header);
        if (!command)
            continue;
        uintptr_t unslid = address - slide;
        for (uint32_t j = 0; j < header->ncmds; j++) {
            const struct load_command *load = (const void *)command;
            if (load->cmd == LC_SEGMENT_64) {
                const struct segment_command_64 *segment = (const void *)load;
                if (segment->vmaddr <= unslid && unslid < segment->vmaddr + segment->vmsize)
                    return i;
            } else if (load->cmd == LC_SEGMENT) {
                const struct segment_command *segment = (const void *)load;
                if (segment->vmaddr <= unslid && unslid < (uint32_t)(segment->vmaddr + segment->vmsize))
                    return i;
            }
            command += load->cmdsize;
        }
    }
    return UINT32_MAX;
}
bool JCommon_dladdr(uintptr_t address, Dl_info *info) {
    memset(info, 0, sizeof(*info));
    uint32_t index = JCommon_imageIndexContainingAddress(address);
    if (index == UINT32_MAX)
        return false;
    const struct mach_header *header = _dyld_get_image_header(index);
    intptr_t slide = _dyld_get_image_vmaddr_slide(index);
    uintptr_t base = JCommon_segmentBaseOfImageIndex(index) + slide;
    if (!base)
        return false;
    info->dli_fname = _dyld_get_image_name(index);
    info->dli_fbase = (void *)header;
    uintptr_t command = JCommon_firstCmdAfterHeader(header);
    if (!command)
        return false;
    uintptr_t bestDistance = UINTPTR_MAX;
    for (uint32_t i = 0; i < header->ncmds; i++) {
        const struct load_command *load = (const void *)command;
        if (load->cmd == LC_SYMTAB) {
            const struct symtab_command *symtab = (const void *)load;
            const struct nlist_64 *symbols = (const void *)(base + symtab->symoff);
            const struct nlist_64 *best = NULL;
            uintptr_t unslid = address - slide;
            for (uint32_t j = 0; j < symtab->nsyms; j++) {
                uintptr_t value = symbols[j].n_value;
                if (value && value <= unslid && unslid - value <= bestDistance) {
                    bestDistance = unslid - value;
                    best = symbols + j;
                }
            }
            if (best) {
                info->dli_saddr = (void *)(best->n_value + slide);
                info->dli_sname = (const char *)(base + symtab->stroff + best->n_un.n_strx);
                if (*info->dli_sname == '_')
                    info->dli_sname++;
                if (info->dli_saddr == (void *)header && best->n_type == (N_ABS | N_EXT))
                    info->dli_sname = NULL;
                return true;
            }
        }
        command += load->cmdsize;
    }
    return true;
}
void JCommon_symbolicate(const uintptr_t *backtraceBuffer, Dl_info *symbolsBuffer, int numEntries,
                         int skippedEntries) {
    int i = 0;
    if (skippedEntries == 0 && numEntries > 0) {
        JCommon_dladdr(backtraceBuffer[0], symbolsBuffer);
        i = 1;
    }
    for (; i < numEntries; i++) {
#if defined(__arm64__)
        uintptr_t address = (backtraceBuffer[i] & ~(uintptr_t)3) - 1;
#elif defined(__x86_64__)
        uintptr_t address = backtraceBuffer[i] - 1;
#endif
        JCommon_dladdr(address, symbolsBuffer + i);
    }
}
JCommonCrashFrame *JCommon_logBacktraceEntry(int entryNum, uintptr_t address, const Dl_info *info) {
    char filename[20], symbol[20];
    const char *package = info->dli_fname;
    if (!package) {
        sprintf(filename, "0x%016lx", (unsigned long)info->dli_fbase);
        package = filename;
    }
    if (!info->dli_sname) {
        sprintf(symbol, "0x%lx", (unsigned long)(address - (uintptr_t)info->dli_saddr));
        return nil;
    }
    NSNumber *instruction = @((unsigned long long)address);
    NSNumber *symbolAddress = @((unsigned long long)info->dli_saddr);
    NSNumber *imageAddress = @((unsigned long long)info->dli_fbase);
    NSString *packageString = [NSString stringWithCString:package encoding:NSUTF8StringEncoding];
    JCommonCrashFrame *frame = [[JCommonCrashFrame alloc] init];
    frame.package = packageString;
    frame.symbolAddress = [NSString stringWithFormat:@"0x%016llx", symbolAddress.unsignedLongLongValue];
    frame.function = [NSString stringWithCString:info->dli_sname encoding:NSUTF8StringEncoding];
    frame.imageAddress = [NSString stringWithFormat:@"0x%016llx", imageAddress.unsignedLongLongValue];
    frame.instructionAddress = [NSString stringWithFormat:@"0x%016llx", instruction.unsignedLongLongValue];
    NSDictionary *infoDictionary = NSBundle.mainBundle.infoDictionary;
    if (@available(iOS 8.0, *))
        frame.inApp = @([packageString.lastPathComponent containsString:infoDictionary[@"CFBundleName"]]);
    return frame;
}
thread_t JCommon_machThreadFromNSThread(NSThread *thread) {
    thread_act_array_t threads;
    mach_msg_type_number_t count;
    task_threads(mach_task_self(), &threads, &count);
    NSTimeInterval time = NSDate.date.timeIntervalSince1970;
    NSString *oldName = thread.name;
    thread.name = [NSString stringWithFormat:@"%f", time];
    if (thread.isMainThread)
        return jc_main_thread_id;
    for (uint32_t i = 0; i < count; i++) {
        pthread_t pthread = pthread_from_mach_thread_np(threads[i]);
        if (thread.isMainThread && threads[i] == jc_main_thread_id)
            return threads[i];
        if (pthread) {
            char name[256] = {0};
            pthread_getname_np(pthread, name, sizeof(name));
            if (strcmp(name, thread.name.UTF8String) == 0) {
                thread.name = oldName;
                return threads[i];
            }
        }
    }
    thread.name = oldName;
    return mach_thread_self();
}
static NSArray *_JCommon_backtraceOfThread(thread_t thread) {
    struct __darwin_mcontext64 context;
    if (!JCommon_fillThreadStateIntoMachineContext(thread, &context))
        return @[];
    uintptr_t addresses[50];
    addresses[0] = JCommon_mach_instructionAddress(&context);
    uintptr_t link = JCommon_mach_linkRegister(&context);
    int count = 1;
    if (link)
        addresses[count++] = link;
    if (!addresses[0])
        return @[];
    struct {
        uintptr_t previous;
        uintptr_t returnAddress;
    } frame = {0, 0};
    uintptr_t pointer = JCommon_mach_framePointer(&context);
    if (!pointer || JCommon_mach_copyMem((const void *)pointer, &frame, sizeof(frame)) != KERN_SUCCESS ||
        !frame.previous)
        return @[];
    addresses[count] = frame.returnAddress;
    if (frame.returnAddress) {
        for (;;) {
            if (JCommon_mach_copyMem((const void *)frame.previous, &frame, sizeof(frame)) != KERN_SUCCESS)
                break;
            if (count == 49) {
                count = 50;
                break;
            }
            addresses[count + 1] = frame.returnAddress;
            count++;
            if (!frame.returnAddress || !frame.previous)
                break;
        }
    }
    Dl_info symbols[count];
    JCommon_symbolicate(addresses, symbols, count, 0);
    NSMutableArray *result = [NSMutableArray array];
    for (int i = 0; i < count; i++) {
        JCommonCrashFrame *entry = JCommon_logBacktraceEntry(0, addresses[i], symbols + i);
        if (entry)
            [result addObject:entry];
    }
    return [result copy];
}
@implementation JCommonCrashBacktrace
+ (void)load {
    jc_main_thread_id = mach_thread_self();
}
+ (NSArray *)JCommon_backtraceOfNSThread:(NSThread *)thread {
    thread_t machThread = JCommon_machThreadFromNSThread(thread);
    (void)thread.name.UTF8String;
    return _JCommon_backtraceOfThread(machThread);
}
+ (NSArray *)JCommon_backtraceOfCurrentThread {
    return [self JCommon_backtraceOfNSThread:NSThread.currentThread];
}
+ (NSArray *)JCommon_backtraceOfMainThread {
    return [self JCommon_backtraceOfNSThread:NSThread.mainThread];
}
+ (NSArray *)JCommon_CallStackFromException:(NSArray *)callStack {
    NSUInteger count = callStack.count;
    uintptr_t addresses[count];
    for (NSUInteger i = 0; i < callStack.count; i++) {
        NSArray *parts = [callStack[i] componentsSeparatedByString:@" "];
        BOOL seen = NO;
        for (NSString *part in parts) {
            if (part.length > 2 && seen) {
                unsigned long long address;
                sscanf([part cStringUsingEncoding:NSUTF8StringEncoding], "0x%016llx", &address);
                addresses[i] = (uintptr_t)address;
                break;
            }
            seen = part.length > 2 || seen;
        }
    }
    Dl_info symbols[(uint32_t)count];
    JCommon_symbolicate(addresses, symbols, (int)count, 0);
    NSMutableArray *result = [NSMutableArray array];
    for (int i = 0; i < (int)count; i++) {
        JCommonCrashFrame *frame = JCommon_logBacktraceEntry(0, addresses[i], symbols + i);
        if (frame)
            [result addObject:frame];
    }
    return result;
}
+ (long long)getThreadId:(NSThread *)thread {
    thread_act_array_t threads;
    mach_msg_type_number_t count;
    task_threads(mach_task_self(), &threads, &count);
    NSTimeInterval time = NSDate.date.timeIntervalSince1970;
    NSString *oldName = thread.name;
    thread.name = [NSString stringWithFormat:@"%f", time];
    if (thread.isMainThread)
        return 0;
    uint32_t result = 0;
    for (uint32_t i = 0; i < count; i++) {
        pthread_t pthread = pthread_from_mach_thread_np(threads[i]);
        if (thread.isMainThread && threads[i] == jc_main_thread_id)
            return i;
        if (pthread) {
            char name[256] = {0};
            pthread_getname_np(pthread, name, sizeof(name));
            if (strcmp(name, thread.name.UTF8String) == 0) {
                result = i;
                break;
            }
        }
    }
    thread.name = oldName;
    return result;
}
+ (NSArray *)JCommon_backtraceOfAllThread {
    thread_act_array_t threads;
    mach_msg_type_number_t count = 0;
    if (task_threads(mach_task_self(), &threads, &count) != KERN_SUCCESS)
        return @[];
    NSMutableArray *result = [NSMutableArray array];
    for (uint32_t i = 0; i < count; i++) {
        JCommonCrashThread *thread = [[JCommonCrashThread alloc] initWithThreadId:@((int)i)];
        char name[256] = {0};
        pthread_getname_np(pthread_from_mach_thread_np(threads[i]), name, sizeof(name));
        thread.name = [NSString stringWithCString:name encoding:NSUTF8StringEncoding];
        JCommonCrashStacktrace *stacktrace = [[JCommonCrashStacktrace alloc] init];
        NSArray *frames = _JCommon_backtraceOfThread(threads[i]);
        if (!frames || frames.count != 0) {
            stacktrace.frames = frames;
            [stacktrace fixDuplicateFrames];
            thread.stacktrace = stacktrace;
            [result addObject:thread];
        }
    }
    return [result copy];
}
@end
