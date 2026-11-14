#import "ICMRecentConversationsViewController.h"
#import "ICMConversationDependencies.h"
#import "ICMIdentityStore.h"
#import "ICMConfig.h"

@interface ICMRecoveredDefaultThemeProvider (RecentCardsThemeProvider) <_TtP18IntercomSDKPrivate13ThemeProvider_>
@end

@implementation ICMRecentConversationsViewController
@synthesize delegate = _delegate, numberOfConversationsToDisplay = _numberOfConversationsToDisplay;
@synthesize tableView = _tableView, tableViewHeightConstraint = _tableViewHeightConstraint;
@synthesize shouldRefresh = _shouldRefresh, dataManager = _dataManager;
@synthesize recentConversationsArray = _recentConversationsArray, themeProvider = _themeProvider;
- (instancetype)initWithDataManager:(id<ICMDataManagerProtocol>)dataManager themeProvider:(id<_TtP18IntercomSDKPrivate13ThemeProvider_>)provider {
    self = [super init];
    if (self) {
        _dataManager = dataManager;
        _themeProvider = provider ?: [ICMRecoveredDefaultThemeProvider shared];
    }
    return self;
}
- (void)viewDidLoad {
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(inboxDidUpdate) name:@"intercom-sdk-inbox-updated" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshConversationsIfNeeded) name:@"IntercomRootViewControllerDidDismissPresentedController" object:nil];
    [self setupTableView];
    id<ICMDataManagerProtocol> manager = self.dataManager;
    self.recentConversationsArray = [manager getRecentConversations:self.numberOfConversationsToDisplay];
}
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    CGFloat height = [self heightOfConversationsTableView];
    self.tableViewHeightConstraint.constant = height;
}
- (void)setupTableView {
    self.tableView = [UITableView new];
    [self.tableView registerClass:[ICMRecoveredInboxCell class] forCellReuseIdentifier:@"InboxCellIdentifier"];
    [self.tableView registerClass:[ICMRecoveredTicketsListViewCell class] forCellReuseIdentifier:@"TicketCellIdentifier"];
    self.tableView.dataSource = self;
    self.tableView.delegate = self;
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    self.tableView.translatesAutoresizingMaskIntoConstraints = NO;
    self.tableView.separatorColor = UIColor.clearColor;
    self.tableView.scrollEnabled = NO;
    [self.view addSubview:self.tableView];
    [NSLayoutConstraint activateConstraints:@[
        [self.tableView.leadingAnchor constraintEqualToAnchor:self.view.leadingAnchor],
        [self.tableView.trailingAnchor constraintEqualToAnchor:self.view.trailingAnchor],
        [self.tableView.topAnchor constraintEqualToAnchor:self.view.topAnchor],
        [self.tableView.bottomAnchor constraintEqualToAnchor:self.view.bottomAnchor]
    ]];
    self.tableViewHeightConstraint = [self.tableView.heightAnchor constraintEqualToConstant:0];
    self.tableViewHeightConstraint.priority = 750;
    self.tableViewHeightConstraint.active = YES;
    self.tableView.accessibilityIdentifier = @"conversation card table view";
}
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    (void)tableView;
    return 1;
}
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    (void)tableView;
    (void)section;
    return self.recentConversationsArray.count;
}
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    NSInteger row = indexPath.row;
    NSInteger lastRow = [self.tableView numberOfRowsInSection:indexPath.section] - 1;
    ICMConversation *conversation = [self.recentConversationsArray objectAtIndexedSubscript:indexPath.row];
    if (conversation.ticket) {
        ICMRecoveredTicketsListViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"TicketCellIdentifier"];
        cell.themeProvider = self.themeProvider;
        [cell configureWithTicket:conversation.ticket showDividerLine:row != lastRow canShowUnreadDot:YES];
        return cell;
    }
    ICMRecoveredInboxCell *cell = [tableView dequeueReusableCellWithIdentifier:@"InboxCellIdentifier"];
    ICMRecoveredInboxCellViewItem *item = [[ICMRecoveredInboxCellViewItem alloc] initWithConversation:conversation currentUserIntercomId:[ICMIdentityStore currentIdentity].userIdentity.intercomId canShowUnreadDot:YES showDividerLine:row != lastRow ticketsSpaceEnabled:[ICMConfig sharedConfig].isTicketsSpaceEnabled];
    [cell configureFor:item];
    cell.accessibilityLabel = [[ICMTranslationManager messenger] translationForKey:@"conversation_number_and_description" params:@{@"conversation_number": [NSString stringWithFormat:@"%ld", (long)(indexPath.row + 1)], @"conversation_description": cell.accessibilityLabel}];
    return cell;
}
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    (void)tableView;
    ICMConversation *conversation = [self.recentConversationsArray objectAtIndexedSubscript:indexPath.row];
    [self.delegate didSelectConversation:conversation];
}
- (void)inboxDidUpdate {
    if (self.viewIfLoaded.window) { [self updateViewWithLatestConversations]; }
    else { self.shouldRefresh = YES; }
}
- (void)refreshConversationsIfNeeded {
    if (self.shouldRefresh) {
        [self updateViewWithLatestConversations];
        self.shouldRefresh = NO;
    }
}
- (void)updateViewWithLatestConversations {
    id<ICMDataManagerProtocol> manager = self.dataManager;
    self.recentConversationsArray = [manager getRecentConversations:self.numberOfConversationsToDisplay];
    [self reloadTableView];
}
- (void)reloadTableView {
    [self.tableView reloadData];
    CGFloat height = [self heightOfConversationsTableView];
    self.tableViewHeightConstraint.constant = height;
}
- (CGFloat)heightOfConversationsTableView {
    [self.tableView layoutIfNeeded];
    return self.tableView.contentSize.height;
}
- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    [self reloadTableView];
    [self.view layoutIfNeeded];
}
@end
