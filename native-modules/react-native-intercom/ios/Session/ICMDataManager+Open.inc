- (void)refreshOpenResponseWithSuccess:(void (^)(void))success error:(void (^)(NSError *))failure {
    [ICMHTTPBridge openMessenger:^(ICMRecoveredOpenResponse *response) {
        if (response) {
            if (response.teamPresenceDictionary) [ICMTeamPresence updateSharedInstanceWithDictionary:response.teamPresenceDictionary];
            self.conversationCTA = response.newConversation.cta;
            self.conversationCard = [[ICMRecoveredNewConversationCard alloc] initWithNewConversationHomeCard:response.newConversation.homeCard];
            self.initialConversation = [self conversationFromOpenResponse:response];
            self.openRequestId = response.requestId;
            self.poweredBy = response.poweredBy;
            self.privacyPolicyNotice = response.privacyPolicyNotice;
            self.recentOpenInboundConversation = response.recentOpenInboundConversation;
            self.resolutionBotBehaviourId = response.resolutionBotBehaviorVersionId;
            self.botIntroId = response.botIntroId;
            if (response.recentOpenInboundConversation) [self updateWithConversation:response.recentOpenInboundConversation];
        }
        if (success) success();
    } error:failure];
}
- (void)clearOpenResponseData {
    self.conversationCTA = nil;
    self.conversationCard = nil;
    self.initialConversation = nil;
    self.openRequestId = nil;
    self.poweredBy = nil;
    self.privacyPolicyNotice = nil;
    self.recentOpenInboundConversation = nil;
    self.resolutionBotBehaviourId = nil;
    self.botIntroId = nil;
    [[ICMTeamPresence sharedInstance] reset];
}
- (ICMConversation *)conversationFromOpenResponse:(ICMRecoveredOpenResponse *)response {
    ICMConversation *conversation = [ICMConversation new];
    conversation.conversationParts = response.newConversation.parts;
    conversation.header = response.newConversation.header;
    conversation.composerState = response.composerState;
    conversation.poweredBy = response.poweredBy;
    conversation.teamIntro = response.newConversation.teamIntro;
    conversation.specialNotice = response.newConversation.specialNotice;
    [self addParticipantsToParts:conversation.conversationParts from:response.newConversation.participants];
    [self updateConversation:conversation fromComposerSuggestions:response.composerSuggestions];
    return conversation;
}
