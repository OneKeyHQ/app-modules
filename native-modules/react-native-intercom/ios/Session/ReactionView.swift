import SwiftUI

enum FeedbackReaction: String, CaseIterable, Identifiable, Hashable {
    case disappointed = "😞", neutral = "😐", happy = "😃"
    var id: String { rawValue }
    func hash(into hasher: inout Hasher) { hasher.combine(rawValue) }
}

struct ReactionView: View {
    let onReactionSelected: (FeedbackReaction) -> Void
    let onStartConversation: () -> Void
    let config: any ICMConfigProtocol
    let existingConversationId: String?
    let themeProvider: any ThemeProvider
    @State private var _selectedReaction: FeedbackReaction?
    var body: some View {
        VStack(alignment: .center, spacing: 16) {
            Text(TranslationManager.helpCenter.translation(forKey: "article_question"))
                .font(.callout).foregroundColor(Color(uiColor: themeProvider.theme.textMuted)).multilineTextAlignment(.center)
            HStack(alignment: .center, spacing: 24) {
                ForEach(FeedbackReaction.allCases) { reaction in
                    Button {
                        withAnimation(.spring(response: 0.3, dampingFraction: 0.6, blendDuration: 0.2)) { _selectedReaction = reaction }
                        onReactionSelected(reaction)
                    } label: {
                        Text(reaction.rawValue)
                            .font(Font(UIFontMetrics.default.scaledFont(for: .preferredFont(forTextStyle: _selectedReaction == reaction ? .largeTitle : .title1))))
                            .scaleEffect(_selectedReaction == reaction ? 1.2 : 1, anchor: .center)
                            .grayscale(_selectedReaction != nil && _selectedReaction != reaction ? 1 : 0)
                            .opacity(_selectedReaction != nil && _selectedReaction != reaction ? 0.4 : 1)
                            .accessibilityLabel(Text(TranslationManager.helpCenter.translation(forKey: accessibilityKey(reaction))))
                            .accessibilityAddTraits(.isButton)
                    }.buttonStyle(.plain)
                }
            }
            conversationButtonSection
        }.frame(maxWidth: .infinity)
            .clipShape(RoundedRectangle(cornerRadius: 12, style: .continuous)).padding(.vertical, 24)
    }
    private func accessibilityKey(_ reaction: FeedbackReaction) -> String {
        switch reaction {
        case .disappointed: return "disappointed_face_emoji"
        case .neutral: return "neutral_face_emoji"
        case .happy: return "grinning_face_with_big_eyes_emoji"
        }
    }
    private var shouldShowConversationButton: Bool {
        guard _selectedReaction == .disappointed else { return false }
        let enabled = config.canStartConversation() && config.articleAutoReactionEnabled
        return enabled || existingConversationId != nil
    }
    private var conversationButtonTitle: String {
        if existingConversationId != nil { return TranslationManager.helpCenter.translation(forKey: "continue_the_conversation") }
        return ICMDataManager.shared().conversationCTA?.label ?? ""
    }
    private var conversationButtonIcon: UIImage? {
        if existingConversationId != nil { return nil }
        return ICMDataManager.shared().conversationCTA?.icon.image()
    }
    private var conversationButtonSection: some View {
        VStack(alignment: .center, spacing: 16) {
            Button(action: onStartConversation) {
                HStack(alignment: .center, spacing: nil) {
                    Text(conversationButtonTitle)
                    if let icon = conversationButtonIcon { Image(uiImage: icon) }
                }.padding().font(.callout.bold()).foregroundColor(buttonForeground)
                    .background(RoundedRectangle(cornerRadius: 12, style: .continuous).fill(Color(uiColor: themeProvider.theme.primaryBackground)))
            }.buttonStyle(.plain)
            if existingConversationId == nil,
               let subtitle = ICMDataManager.shared().conversationCTA?.subtitle, shouldShowConversationButton {
                Text(subtitle).foregroundStyle(Color(uiColor: themeProvider.theme.textMuted)).font(.caption)
            }
        }.frame(maxHeight: shouldShowConversationButton ? nil : 0)
            .opacity(shouldShowConversationButton ? 1 : 0).clipped()
    }
    private var buttonForeground: Color {
        let theme = themeProvider.theme
        return Color(uiColor: ColorHelper.suitableDynamicForegroundColor(for: theme.primaryBackground, theme: theme))
    }
}
