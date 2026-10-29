import Foundation
import UIKit
import ReactNativeNativeLogger

/// Owns the app's custom main menu (the iPadOS menu bar).
///
/// JS supplies localized titles and Help links through `setMainMenuConfig`.
/// The app delegate forwards `buildMenu(with:)` to `buildMenu(withBuilder:)`
/// (it reaches this class through `NSClassFromString`, because the app target
/// cannot import Nitro modules). App commands are `UIKeyCommand`s whose action
/// lives on `UIApplication`, which is always in the responder chain, so the
/// app delegate needs no command handler.
public final class MainMenuController: NSObject {
    static let shared = MainMenuController()

    private struct Link {
        let title: String
        let url: URL
    }

    private struct Config {
        let aboutTitle: String
        let preferencesTitle: String
        let lockNowTitle: String
        let helpLinks: [Link]
    }

    private static let aboutMenuIdentifier = UIMenu.Identifier("so.onekey.mainMenu.about")
    private static let settingsMenuIdentifier = UIMenu.Identifier("so.onekey.mainMenu.settings")

    private let lock = NSLock()
    private var config: Config?
    private var listeners: [Double: (MainMenuCommand) -> Void] = [:]
    private var nextListenerId: Double = 1

    func setConfig(_ config: MainMenuConfig) {
        let links = config.helpLinks.compactMap { link -> Link? in
            guard let url = URL(string: link.url) else {
                OneKeyLog.warn("DeviceUtils", "Main menu: invalid help link URL")
                return nil
            }
            return Link(title: link.title, url: url)
        }
        let next = Config(
            aboutTitle: config.aboutTitle,
            preferencesTitle: config.preferencesTitle,
            lockNowTitle: config.lockNowTitle,
            helpLinks: links
        )
        lock.lock()
        self.config = next
        lock.unlock()
        DispatchQueue.main.async {
            UIMenuSystem.main.setNeedsRebuild()
        }
    }

    func addListener(_ callback: @escaping (MainMenuCommand) -> Void) -> Double {
        lock.lock()
        defer { lock.unlock() }
        let id = nextListenerId
        nextListenerId += 1
        listeners[id] = callback
        return id
    }

    func removeListener(_ id: Double) {
        lock.lock()
        defer { lock.unlock() }
        listeners.removeValue(forKey: id)
    }

    /// Called by the app delegate's `buildMenu(with:)` on the main thread.
    @objc(buildMenuWithBuilder:)
    public static func buildMenu(withBuilder builder: UIMenuBuilder) {
        shared.build(builder)
    }

    fileprivate func perform(commandName: String) {
        guard let command = MainMenuCommand(fromString: commandName) else { return }
        lock.lock()
        let callbacks = Array(listeners.values)
        lock.unlock()
        OneKeyLog.info("DeviceUtils", "Main menu command: \(commandName), listeners=\(callbacks.count)")
        callbacks.forEach { $0(command) }
    }

    private func build(_ builder: UIMenuBuilder) {
        guard builder.system == .main else { return }

        // A wallet has no documents to open or text to format.
        builder.remove(menu: .file)
        builder.remove(menu: .format)

        lock.lock()
        let config = self.config
        lock.unlock()
        // Keep the system app and Help menus until JS provides localized titles.
        guard let config else { return }

        let action = #selector(UIApplication.onekeyPerformMainMenuCommand(_:))
        let about = UICommand(
            title: config.aboutTitle,
            action: action,
            propertyList: MainMenuCommand.about.stringValue
        )
        let preferences = UIKeyCommand(
            title: config.preferencesTitle,
            action: action,
            input: ",",
            modifierFlags: .command,
            propertyList: MainMenuCommand.preferences.stringValue
        )
        let lockNow = UIKeyCommand(
            title: config.lockNowTitle,
            action: action,
            input: "l",
            modifierFlags: [.command, .shift],
            propertyList: MainMenuCommand.locknow.stringValue
        )
        let aboutMenu = UIMenu(
            title: "",
            identifier: MainMenuController.aboutMenuIdentifier,
            options: .displayInline,
            children: [about]
        )
        let settingsMenu = UIMenu(
            title: "",
            identifier: MainMenuController.settingsMenuIdentifier,
            options: .displayInline,
            children: [preferences, lockNow]
        )

        // The system Preferences item opens the app's page in the Settings app;
        // replace it with the in-app preferences, which own the same shortcut.
        if builder.menu(for: .preferences) != nil {
            builder.replace(menu: .preferences, with: settingsMenu)
        } else {
            builder.insertChild(settingsMenu, atStartOfMenu: .application)
        }
        // An empty About title removes the item (Travel Mode hides About).
        if config.aboutTitle.isEmpty {
            builder.remove(menu: .about)
        } else if builder.menu(for: .about) != nil {
            builder.replace(menu: .about, with: aboutMenu)
        } else {
            builder.insertSibling(aboutMenu, beforeMenu: MainMenuController.settingsMenuIdentifier)
        }

        if config.helpLinks.isEmpty {
            builder.remove(menu: .help)
        } else if builder.menu(for: .help) != nil {
            let links = config.helpLinks.map { link in
                UIAction(title: link.title) { _ in
                    UIApplication.shared.open(link.url)
                }
            }
            builder.replaceChildren(ofMenu: .help) { _ in links }
        }
    }
}

extension UIApplication {
    /// Action of the main menu app commands; the command name is the
    /// command's property list.
    @objc func onekeyPerformMainMenuCommand(_ sender: UICommand) {
        guard let name = sender.propertyList as? String else { return }
        MainMenuController.shared.perform(commandName: name)
    }
}
