#import "ICMConversationPart.h"
#import "ICMConversationPartDependencies.h"
@implementation ICMConversationPart
@synthesize hasArticleCard = _hasArticleCard;
@synthesize seenState = _seenState;
@synthesize reactionSet = _reactionSet;
@synthesize createdAt = _createdAt;
@synthesize upload = _upload;
@synthesize blocks = _blocks;
@synthesize blockDictionaries = _blockDictionaries;
@synthesize sendFailureAllowsRetry = _sendFailureAllowsRetry;
@synthesize maxLayoutWidth = _maxLayoutWidth;
@synthesize estimatedSize = _estimatedSize;
@synthesize displayTimestamp = _displayTimestamp;
@synthesize participantIsAdmin = _participantIsAdmin;
@synthesize isInitialMessage = _isInitialMessage;
@synthesize deliveryOption = _deliveryOption;
@synthesize participantId = _participantId;
@synthesize summary = _summary;
@synthesize partId = _partId;
@synthesize attribution = _attribution;
@synthesize replyOptionsPrompt = _replyOptionsPrompt;
@synthesize expectationsText = _expectationsText;
@synthesize participant = _participant;
@synthesize eventData = _eventData;
@synthesize replyOptionsArray = _replyOptionsArray;
@synthesize selectedReplyOption = _selectedReplyOption;
@synthesize form = _form;
@synthesize quickReplyPartId = _quickReplyPartId;
@synthesize ticket = _ticket;
@synthesize isOnlyPart = _isOnlyPart;
@synthesize isProfilePart = _isProfilePart;
@synthesize isStreamingPart = _isStreamingPart;
@synthesize answerSources = _answerSources;
@synthesize clientAssignedUUID = _clientAssignedUUID;
@synthesize groupingPosition = _groupingPosition;
@synthesize hasSegmentBelow = _hasSegmentBelow;
@synthesize senderLabel = _senderLabel;
@synthesize isSenderLinePinned = _isSenderLinePinned;
@synthesize displayPartStatus = _displayPartStatus;
@synthesize displayTimestampTemporarily = _displayTimestampTemporarily;
@synthesize partStyle = _partStyle;
@synthesize partMetadata = _partMetadata;
@dynamic state, displayStyle, articleId;
- (instancetype)init {
    self = [super init];
    if (self) {
        _displayTimestamp = YES;
        _partStyle = 0;
        _state = 0;
        _sendFailureAllowsRetry = YES;
        _seenState = 1;
        _displayStyle = 0;
        _replyOptionsArray = @[];
        _groupingPosition = 3;
    }
    return self;
}
- (NSInteger)state { return _state; }
- (void)setState:(NSInteger)state {
    _state = state;
    if (state == 1 || state == 2) {
        [self.upload setUploadState:state];
    } else {
        [self.upload setUploadState:0];
        self.upload = nil;
    }
}
- (NSInteger)displayStyle { return _displayStyle; }
- (void)setDisplayStyle:(NSInteger)style {
    if (_displayStyle == style) return;
    _displayStyle = style;
    for (ITBBlock *block in self.blocks) {
        if (block.type != 12) [block updateAppearance:[ICMConversationPart blockAppearanceForPart:self]];
    }
}
- (void)setArticleId:(NSString *)articleId { _articleId = articleId; }
- (id)copyWithZone:(NSZone *)zone {
    ICMConversationPart *copy = [[[self class] allocWithZone:zone] init];
    copy.createdAt = self.createdAt;
    copy.upload = self.upload;
    copy.blocks = [[NSArray alloc] initWithArray:self.blocks copyItems:YES];
    copy.state = self.state;
    copy.sendFailureAllowsRetry = self.sendFailureAllowsRetry;
    copy.maxLayoutWidth = self.maxLayoutWidth;
    copy.estimatedSize = self.estimatedSize;
    copy.displayTimestamp = self.displayTimestamp;
    copy.participantIsAdmin = self.participantIsAdmin;
    copy.isInitialMessage = self.isInitialMessage;
    copy.deliveryOption = self.deliveryOption;
    copy.hasArticleCard = self.hasArticleCard;
    copy.seenState = self.seenState;
    copy.participantId = self.participantId;
    copy.partStyle = self.partStyle;
    copy.reactionSet = self.reactionSet;
    copy.summary = self.summary;
    copy.partId = self.partId;
    copy.quickReplyPartId = self.quickReplyPartId;
    copy.participant = self.participant;
    copy.eventData = self.eventData;
    copy.replyOptionsArray = [[NSArray alloc] initWithArray:self.replyOptionsArray copyItems:YES];
    copy.form = self.form;
    copy.expectationsText = self.expectationsText;
    copy.ticket = self.ticket;
    copy.isProfilePart = self.isProfilePart;
    copy.isStreamingPart = self.isStreamingPart;
    copy.clientAssignedUUID = self.clientAssignedUUID;
    copy.groupingPosition = self.groupingPosition;
    copy.hasSegmentBelow = self.hasSegmentBelow;
    copy.senderLabel = self.senderLabel;
    copy.isSenderLinePinned = self.isSenderLinePinned;
    copy.blockDictionaries = self.blockDictionaries;
    copy.selectedReplyOption = self.selectedReplyOption;
    copy.displayPartStatus = self.displayPartStatus;
    copy.displayTimestampTemporarily = self.displayTimestampTemporarily;
    copy.answerSources = self.answerSources;
    copy.partMetadata = self.partMetadata;
    copy.displayStyle = self.displayStyle;
    return copy;
}
@end
