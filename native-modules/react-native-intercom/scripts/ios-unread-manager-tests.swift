import Foundation

@main struct UnreadManagerChecks {
    static func main() throws {
        let center = NotificationCenter()
        let manager = UnreadManager(notificationCenter: center)
        var events: [String] = []
        var count = 0
        func check(_ condition: @autoclosure () -> Bool) {
            precondition(condition()); count += 1
        }
        let names = ["IntercomUnreadConversationCountDidChangeNotification", "IntercomUnreadTicketCountDidChangeNotification", "ICMConversationMarkedAsReadNotification", "ICMTicketMarkedAsReadNotification"]
        let tokens = names.map { name in
            center.addObserver(forName: Notification.Name(name), object: nil, queue: nil) { notification in
                events.append(name + ":" + (notification.object as? String ?? "nil"))
                if name == names[0] { precondition(manager.unreadConversationIds.count <= 2) }
            }
        }
        defer { tokens.forEach(center.removeObserver) }
        check(manager.unreadConversationIds.count == 0 && manager.unreadTicketIds.count == 0)
        let mutable = NSMutableOrderedSet(array: ["a", "b"])
        manager.unreadConversationIds = mutable
        check(manager.unreadConversationIds === mutable)
        check(events == [names[0] + ":nil"])
        mutable.replaceObject(at: 0, with: "c")
        check(manager.unreadConversationIds.contains("c"))
        manager.unreadConversationIds = NSOrderedSet(array: ["x", "y"])
        check(events.count == 1)
        manager.conversationIdRead("missing")
        check(events.count == 1)
        manager.conversationIdRead("x")
        check(manager.unreadConversationIds.array as? [String] == ["y"])
        check(events.suffix(2) == [names[0] + ":nil", names[2] + ":x"])
        manager.conversationIdRead("x")
        check(events.count == 3)
        manager.unreadTicketIds = NSOrderedSet(array: ["ticket"])
        manager.ticketIdRead("ticket")
        check(manager.unreadTicketIds.count == 0)
        check(events.suffix(3) == [names[1] + ":nil", names[1] + ":nil", names[3] + ":ticket"])
        let before = events.count
        manager.unreadTicketIds = NSOrderedSet()
        check(events.count == before)
        let data = try JSONSerialization.data(withJSONObject: ["testCount": count, "status": "PASS", "events": events], options: [.sortedKeys])
        print(String(decoding: data, as: UTF8.self))
    }
}
