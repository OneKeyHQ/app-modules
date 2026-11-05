// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import MachO
import Darwin

enum SDKLinkage: String {
    case dynamic
    case `static`
}

struct BinaryIdentifier {
    let binaryName: String
    let binaryUUID: String?
    let linkage: SDKLinkage

    static let current: BinaryIdentifier = {
        var info = Dl_info()
        guard dladdr(#dsohandle, &info) != 0, let imagePath = info.dli_fname else {
            return BinaryIdentifier(binaryName: "unknown", binaryUUID: nil, linkage: .dynamic)
        }
        let path = String(cString: imagePath)
        let name = (path as NSString).lastPathComponent.replacingOccurrences(of: ".dylib", with: "")
        return BinaryIdentifier(binaryName: name, binaryUUID: findUUID(forImagePath: path),
                                linkage: name.hasPrefix("Intercom") ? .dynamic : .static)
    }()

    private static func findUUID(forImagePath path: String) -> String? {
        for index in 0..<_dyld_image_count() {
            guard let name = _dyld_get_image_name(index), String(cString: name) == path,
                  let header = _dyld_get_image_header(index) else { continue }
            return extractUUID(from: header)
        }
        return nil
    }

    private static func extractUUID(from header: UnsafePointer<mach_header>) -> String? {
        var command = UnsafeRawPointer(header).advanced(by: header.pointee.magic == MH_MAGIC_64 ? 32 : 28)
        for _ in 0..<header.pointee.ncmds {
            let loadCommand = command.load(as: load_command.self)
            if loadCommand.cmd == LC_UUID {
                let bytes = command.advanced(by: 8).assumingMemoryBound(to: UInt8.self)
                return String(format: "%02X%02X%02X%02X-%02X%02X-%02X%02X-%02X%02X-%02X%02X%02X%02X%02X%02X",
                              bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7],
                              bytes[8], bytes[9], bytes[10], bytes[11], bytes[12], bytes[13], bytes[14], bytes[15])
            }
            command = command.advanced(by: Int(loadCommand.cmdsize))
        }
        return nil
    }
}
