// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation

struct UserDefaultsManager {
    func item<T>(forKey key: String) -> T? {
        UserDefaults.standard.object(forKey: key) as? T
    }
}
