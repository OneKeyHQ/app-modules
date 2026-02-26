import NitroModules
import CocoaLumberjack

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
            guard let files = try? fm.contentsOfDirectory(atPath: dir) else {
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
            guard let files = try? fm.contentsOfDirectory(atPath: dir) else { return }
            for file in files where file.hasSuffix(".log") {
                try? fm.removeItem(atPath: "\(dir)/\(file)")
            }
        }
    }
}
