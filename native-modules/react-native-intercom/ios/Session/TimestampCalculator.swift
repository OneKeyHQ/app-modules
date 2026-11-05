import Foundation

// Raw behavior is recovered; these two private enum case spellings are transparent source labels.
@objc enum TimeAgoFormat: UInt { case short = 0, long = 1 }
@objc enum RecentTimeMessage: UInt { case justNow = 0, delivered = 1 }

@objc(ICMTimestampCalculator)
class TimestampCalculator: NSObject {
    private let translationManager: TranslationManager
    private let dateProvider: () -> Date
    private let fixedLocale: Locale?
    private let usesFixedLocale: Bool
    private enum DeltaType: Hashable { case justNow, delivered, minutes, hours, days, weeks }
    private static let shortKeys: [DeltaType: String] = [.minutes: "time_minute_ago", .hours: "time_hour_ago", .days: "time_day_ago", .weeks: "time_week_ago"]
    private static let longKeys: [DeltaType: String] = [.minutes: "time_minute_ago", .hours: "time_hour_ago", .days: "time_day_ago", .weeks: "time_week_ago"]

    @objc static let shared = TimestampCalculator(translationManager: .messenger, locale: nil, dateProvider: { Date() })
    @objc class func sharedInstance() -> TimestampCalculator { shared }

    @objc init(translationManager: TranslationManager, locale: Locale?, dateProvider: @escaping () -> Date) {
        self.translationManager = translationManager
        self.dateProvider = dateProvider
        fixedLocale = locale
        usesFixedLocale = locale != nil
        super.init()
    }
    override init() { fatalError("init() has not been implemented") }

    private func timeComponents(since date: Date) -> DateComponents {
        let calendar = Calendar.current
        let components: Set<Calendar.Component> = [.second, .minute, .hour, .day, .weekday]
        let now = dateProvider()
        return calendar.dateComponents(components, from: date, to: now)
    }
    private func nilDate() { ICMLogger.privateDebugLog("'date' parameter shouldn't be nil") }
    private func format(_ type: DeltaType, delta: Int, keys: [DeltaType: String]) -> String {
        if let key = keys[type] {
            return translationManager.translation(forKey: key).replacingOccurrences(of: "{delta}", with: String(delta))
        }
        return translationManager.translation(forKey: type == .justNow ? "time_just_now" : "delivered")
    }
    @objc(timestampTextFor:format:recentTimeMessage:)
    func timestampText(for date: Date?, format: TimeAgoFormat, recentTimeMessage: RecentTimeMessage) -> String? {
        guard let date else { nilDate(); return nil }
        let keys = UInt32(truncatingIfNeeded: format.rawValue) == 0 ? Self.shortKeys : Self.longKeys
        let components = timeComponents(since: date)
        let second = components.second
        let minute = max(components.minute ?? 0, 0)
        let hour = max(components.hour ?? 0, 0)
        let day = max(components.day ?? 0, 0)
        let type: DeltaType
        let delta: Int
        if day >= 7 { type = .weeks; delta = day / 7 }
        else if day != 0 { type = .days; delta = day }
        else if hour != 0 { type = .hours; delta = hour }
        else if minute >= 3 { type = .minutes; delta = minute }
        else if minute != 0 || (second.map { $0 > 10 } ?? false) || UInt32(truncatingIfNeeded: recentTimeMessage.rawValue) == 0 {
            type = .justNow; delta = 0
        } else { type = .delivered; delta = 0 }
        return self.format(type, delta: delta, keys: keys)
    }
    @objc func timestampTextForUpdatedDate(_ date: Date?) -> String? {
        guard let date else { nilDate(); return nil }
        let formats: [DeltaType: String] = [.minutes: "Updated %ld minutes ago", .hours: "Updated %ld hours ago", .days: "Updated %ld days ago", .weeks: "Updated %ld weeks ago"]
        let components = timeComponents(since: date)
        _ = components.second
        let minute = max(components.minute ?? 0, 0)
        let hour = max(components.hour ?? 0, 0)
        let day = max(components.day ?? 0, 0)
        let type: DeltaType
        let delta: Int
        if day >= 7 { type = .weeks; delta = day / 7 }
        else if day != 0 { type = .days; delta = day }
        else if hour != 0 { type = .hours; delta = hour }
        else if minute >= 3 { type = .minutes; delta = minute }
        else { type = .justNow; delta = 0 }
        if let format = formats[type] { return String(format: format, delta) }
        return translationManager.translation(forKey: "Just now")
    }
    @objc func textForLastActiveDate(_ date: Date?) -> String? {
        guard let date else { nilDate(); return nil }
        let minute = Int(dateProvider().timeIntervalSince(date) / 60)
        if minute >= 8641 { return translationManager.translation(forKey: "active_week_ago") }
        if minute >= 1411 {
            let days = ((((((minute * 0x8889) >> 21) - 13) & 0xff) * 0xab) >> 12) + 1
            return translationManager.translation(forKey: "active_day_ago").replacingOccurrences(of: "{days}", with: String(days))
        }
        if minute >= 53 {
            let hours = ((((minute - 31) & 0xffff) * 0x8889) >> 21) + 1
            return translationManager.translation(forKey: "active_hour_ago").replacingOccurrences(of: "{hours}", with: String(hours))
        }
        if minute >= 38 { return translationManager.translation(forKey: "active_minute_ago").replacingOccurrences(of: "{minutes}", with: "45") }
        if minute >= 16 { return translationManager.translation(forKey: "active_minute_ago").replacingOccurrences(of: "{minutes}", with: "30") }
        return translationManager.translation(forKey: "active_15m_ago").replacingOccurrences(of: "{minutes}", with: "15")
    }
    @objc func lastActiveMinutesAgo(_ date: Date?) -> String {
        guard let date else { nilDate(); return "0 minutes" }
        return String(Int(dateProvider().timeIntervalSince(date) / 60)) + " minutes"
    }
    @objc(secondsUntilTimestampExpiresFor:)
    func secondsUntilTimestampExpires(for date: Date?) -> Double {
        guard let date else { nilDate(); return 10 }
        let components = timeComponents(since: date)
        let second = max(components.second ?? 0, 0)
        let hour = max(components.hour ?? 0, 0)
        if (components.day ?? 0) > 0 || hour != 0 { return 0 }
        if !((components.minute ?? 0) > 0) && second < 11 { return Double(11 - second) }
        return Double(max(61 - second, 0))
    }
    @objc func part(_ first: ICMConversationPart, isFromDifferentDayAs second: ICMConversationPart) -> Bool {
        guard let firstDate = first.createdAt else { return false }
        guard let secondDate = second.createdAt else { return false }
        return !Calendar.current.isDate(firstDate, inSameDayAs: secondDate)
    }
    @objc func partWasCreatedBeforeToday(_ part: ICMConversationPart) -> Bool {
        guard let date = part.createdAt else { return false }
        let calendar = Calendar.current
        return !calendar.isDate(date, inSameDayAs: dateProvider())
    }
    @objc(appendSeenStateToTimestampText:for:)
    func appendSeenState(toTimestampText text: String, for part: ICMConversationPart) -> String {
        if part.participantIsAdmin { return text }
        let state = part.seenState
        if state == 0 { return text + " • " + translationManager.translation(forKey: "message_seen") }
        if state == 1 { return text + " • " + translationManager.translation(forKey: "message_unseen") }
        return text
    }
    @objc(dayTextFor:)
    func dayText(for part: ICMConversationPart) -> String? {
        guard let date = part.createdAt else { return nil }
        let formatter = DateFormatter()
        if usesFixedLocale { formatter.locale = fixedLocale }
        else { formatter.locale = ICMConfig.shared().intercomLocale as Locale? }
        formatter.dateStyle = .medium
        return formatter.string(from: date)
    }
}
