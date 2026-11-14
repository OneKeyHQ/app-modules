import Foundation

struct UserDefaultsManager {
    func item<T>(forKey key: String) -> T? {
        UserDefaults.standard.object(forKey: key) as? T
    }
}
