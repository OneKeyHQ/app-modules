import NitroModules

class NativeLogger: HybridNativeLoggerSpec {

    func write(level: Double, msg: String) {
        switch Int(level) {
        case 0: OneKeyLog.debug("JS", msg)
        case 1: OneKeyLog.info("JS", msg)
        case 2: OneKeyLog.warn("JS", msg)
        case 3: OneKeyLog.error("JS", msg)
        default: OneKeyLog.info("JS", msg)
        }
    }

    func getLogFilePaths() throws -> Promise<[String]> {
        return Promise.async {
            let dir = OneKeyLog.logsDirectory
            let fm = FileManager.default
            let files: [String]
            do {
                files = try fm.contentsOfDirectory(atPath: dir)
            } catch {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory: \(error.localizedDescription)")
                return []
            }
            return files
                .filter { $0.hasSuffix(".log") }
                .map { "\(dir)/\($0)" }
                .sorted()
        }
    }

    func deleteLogFiles() throws -> Promise<Void> {
        return Promise.async {
            let dir = OneKeyLog.logsDirectory
            let fm = FileManager.default
            let files: [String]
            do {
                files = try fm.contentsOfDirectory(atPath: dir)
            } catch {
                OneKeyLog.warn("NativeLogger", "Failed to list log directory for deletion: \(error.localizedDescription)")
                return
            }
            for file in files where file.hasSuffix(".log") {
                do {
                    try fm.removeItem(atPath: "\(dir)/\(file)")
                } catch {
                    OneKeyLog.warn("NativeLogger", "Failed to delete log file \(file): \(error.localizedDescription)")
                }
            }
        }
    }
}
