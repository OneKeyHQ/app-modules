import Foundation
import Intents
import UserNotifications

extension ICMPushNotificationManager {
    @objc(communicationNotificationFrom:withAuthorName:avatarLocationURL:)
    static func communicationNotification(from content: UNNotificationContent, withAuthorName authorName: String, avatarLocationURL: URL?) -> UNNotificationContent? {
        let handle = INPersonHandle(value: authorName, type: .unknown)
        let image: INImage?
        if let url = avatarLocationURL, let data = try? Data(contentsOf: url, options: []) {
            image = INImage(imageData: data)
        } else { image = nil }
        let person = INPerson(personHandle: handle, nameComponents: nil, displayName: authorName, image: image, contactIdentifier: nil, customIdentifier: nil)
        let intent = INSendMessageIntent(recipients: nil, outgoingMessageType: .outgoingMessageText, content: nil, speakableGroupName: nil, conversationIdentifier: nil, serviceName: nil, sender: person, attachments: nil)
        if let image { intent.setImage(image, forParameterNamed: \INSendMessageIntent.sender) }
        let interaction = INInteraction(intent: intent, response: nil)
        interaction.direction = .incoming
        interaction.donate { error in
            if let error {
                ICMLogger.log("Failed to donate interaction for communication notification. Error: " + error.localizedDescription, type: .debug)
            }
        }
        do {
            return try content.updating(from: intent)
        } catch {
            ICMLogger.log("Failed to create communication notification. Error: " + error.localizedDescription + "\nTo display Communication Notifications, ensure the following are configured:\n1. Add the 'com.apple.developer.usernotifications.communication' entitlement to your app target.\n2. Add 'INSendMessageIntent' to 'NSUserActivityTypes' in your app's Info.plist.\n3. Add 'INSendMessageIntent' to 'IntentsSupported' in your Notification Service Extension's Info.plist.", type: .debug)
            return nil
        }
    }
}
