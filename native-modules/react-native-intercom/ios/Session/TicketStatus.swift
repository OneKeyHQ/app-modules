import UIKit

struct TicketStatus: Codable, Equatable, DictionaryInitializable {
    let title: String
    let isCurrentStatus: Bool
    let stage: Stage
    let incomplete: Bool
    let slaDetails: String?
    let createdDate: Date?
    let statusDetail: String?

    enum CodingKeys: String, CodingKey {
        case title, createdDate, statusDetail, slaDetails, isCurrentStatus, incomplete
        case stage = "type"
    }

    enum Stage: String, Codable {
        case submitted, inProgress = "in_progress", waitingOnYou = "waiting_on_customer", resolved
        init(from decoder: Decoder) throws {
            let raw = try decoder.singleValueContainer().decode(String.self)
            self = Stage(rawValue: raw) ?? .submitted
        }
        var details: String {
            let key: String
            switch self {
            case .submitted: key = "tickets_status_submitted"
            case .inProgress: key = "tickets_status_in_progress"
            case .waitingOnYou: key = "tickets_status_waiting_on_you"
            case .resolved: key = "tickets_status_resolved"
            }
            return TranslationManager.messenger.translation(forKey: key)
        }
    }

    init(title: String, isCurrentStatus: Bool, stage: Stage, incomplete: Bool, slaDetails: String?, createdDate: Date?, statusDetail: String?) {
        self.title = title
        self.isCurrentStatus = isCurrentStatus
        self.stage = stage
        self.incomplete = incomplete
        self.slaDetails = slaDetails
        self.createdDate = createdDate
        self.statusDetail = statusDetail
    }

    init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        title = try container.decode(String.self, forKey: .title)
        stage = try container.decode(Stage.self, forKey: .stage)
        createdDate = try container.decodeIfPresent(Date.self, forKey: .createdDate)
        statusDetail = try container.decodeIfPresent(String.self, forKey: .statusDetail)
        slaDetails = try container.decodeIfPresent(String.self, forKey: .slaDetails)
        isCurrentStatus = try container.decode(Bool.self, forKey: .isCurrentStatus)
        incomplete = try container.decode(Bool.self, forKey: .incomplete)
    }

    func encode(to encoder: Encoder) throws {
        var container = encoder.container(keyedBy: CodingKeys.self)
        try container.encode(title, forKey: .title)
        try container.encodeIfPresent(createdDate, forKey: .createdDate)
        try container.encodeIfPresent(statusDetail, forKey: .statusDetail)
        try container.encodeIfPresent(slaDetails, forKey: .slaDetails)
        try container.encode(isCurrentStatus, forKey: .isCurrentStatus)
        try container.encode(incomplete, forKey: .incomplete)
        try container.encode(stage, forKey: .stage)
    }
}


extension TicketStatus {
    func titleAndTimestamp(for direction: UIUserInterfaceLayoutDirection) -> String {
        guard let date = createdDate else { return title }
        guard let timestamp = TimestampCalculator.shared.timestampText(for: date, format: .short, recentTimeMessage: .justNow) else { return title }
        if direction == .rightToLeft { return timestamp + " • " + title }
        return title + " • " + timestamp
    }
}
