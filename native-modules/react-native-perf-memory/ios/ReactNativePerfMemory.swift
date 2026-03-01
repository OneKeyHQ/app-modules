import NitroModules
import ReactNativeNativeLogger

class ReactNativePerfMemory: HybridReactNativePerfMemorySpec {

    func getMemoryUsage() throws -> Promise<MemoryUsage> {
        return Promise.async {
            // Prefer phys_footprint when available (closest to "real" memory impact)
            var vmInfo = task_vm_info_data_t()
            var count = mach_msg_type_number_t(MemoryLayout<task_vm_info_data_t>.size / MemoryLayout<natural_t>.size)

            let kr = withUnsafeMutablePointer(to: &vmInfo) { ptr in
                ptr.withMemoryRebound(to: integer_t.self, capacity: Int(count)) { intPtr in
                    task_info(mach_task_self_, task_flavor_t(TASK_VM_INFO), intPtr, &count)
                }
            }

            if kr == KERN_SUCCESS {
                let footprint = Double(vmInfo.phys_footprint)
                return MemoryUsage(rss: footprint)
            }

            // Fallback: resident size via mach_task_basic_info
            var basicInfo = mach_task_basic_info_data_t()
            var basicCount = mach_msg_type_number_t(MemoryLayout<mach_task_basic_info_data_t>.size / MemoryLayout<natural_t>.size)

            let basicKr = withUnsafeMutablePointer(to: &basicInfo) { ptr in
                ptr.withMemoryRebound(to: integer_t.self, capacity: Int(basicCount)) { intPtr in
                    task_info(mach_task_self_, task_flavor_t(MACH_TASK_BASIC_INFO), intPtr, &basicCount)
                }
            }

            if basicKr == KERN_SUCCESS {
                return MemoryUsage(rss: Double(basicInfo.resident_size))
            }

            OneKeyLog.warn("PerfMemory", "Failed to get memory usage, kern_return: \(kr)")
            return MemoryUsage(rss: 0)
        }
    }
}
