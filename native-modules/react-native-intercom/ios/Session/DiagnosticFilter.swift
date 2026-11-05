// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation

enum SDKAttribution: String {
    case confirmed
    case unverified
}

struct DiagnosticFilter {
    let binaryIdentifier: BinaryIdentifier

    struct FilterResult {
        let data: Data
        let matchedBinaryNames: Set<String>
        let sdkAttribution: SDKAttribution
    }

    func filterCallStackJSON(_ data: Data) -> FilterResult? {
        if binaryIdentifier.linkage == .static {
            return FilterResult(data: data, matchedBinaryNames: [binaryIdentifier.binaryName],
                                sdkAttribution: .unverified)
        }
        guard var root = (try? JSONSerialization.jsonObject(with: data, options: [])) as? [String: Any],
              let callStacks = root["callStacks"] as? [[String: Any]] else { return nil }
        return filterDynamic(root: &root, callStacks: callStacks)
    }

    private func filterDynamic(root: inout [String: Any], callStacks: [[String: Any]]) -> FilterResult? {
        var matchedBinaryNames = Set<String>()
        var attributedMatch = false
        for stack in callStacks {
            guard let frames = stack["callStackRootFrames"] as? [[String: Any]] else { continue }
            var names = Set<String>()
            collectSDKBinaryNames(frames: frames, into: &names)
            if stack["threadAttributed"] as? Bool == true, !names.isEmpty {
                attributedMatch = true
            }
            matchedBinaryNames.formUnion(names)
        }
        guard attributedMatch else { return nil }
        root["callStacks"] = callStacks.map { stack in
            var result = stack
            if let frames = stack["callStackRootFrames"] as? [[String: Any]] {
                result["callStackRootFrames"] = stripNonSDKUUIDs(frames: frames)
            }
            return result
        }
        guard let data = try? JSONSerialization.data(withJSONObject: root, options: []) else { return nil }
        return FilterResult(data: data, matchedBinaryNames: matchedBinaryNames, sdkAttribution: .confirmed)
    }

    private func matchesSDK(_ name: String) -> Bool {
        binaryIdentifier.linkage == .static ? name == binaryIdentifier.binaryName : name.hasPrefix("Intercom")
    }

    private func collectSDKBinaryNames(frames: [[String: Any]], into names: inout Set<String>) {
        for frame in frames {
            if let name = frame["binaryName"] as? String, matchesSDK(name) { names.insert(name) }
            if let children = frame["subFrames"] as? [[String: Any]] {
                collectSDKBinaryNames(frames: children, into: &names)
            }
        }
    }

    private func stripNonSDKUUIDs(frames: [[String: Any]]) -> [[String: Any]] {
        frames.map { frame in
            var result = frame
            if let name = frame["binaryName"] as? String, !matchesSDK(name) {
                result["binaryUUID"] = "00000000-0000-0000-0000-000000000000"
            }
            if let children = frame["subFrames"] as? [[String: Any]] {
                result["subFrames"] = stripNonSDKUUIDs(frames: children)
            }
            return result
        }
    }
}
