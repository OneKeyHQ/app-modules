#pragma once
#import <UIKit/UIKit.h>
#import "ICMDataManagerProtocol.h"
#import "ICMRecentCardImports.h"
@class ICMConversation;
@protocol ICMRecentConversationsViewControllerDelegate <NSObject>
- (void)didSelectConversation:(ICMConversation *)conversation NS_SWIFT_NAME(didSelectConversation(_:));
@end
@interface ICMRecentConversationsViewController : UIViewController <UITableViewDelegate, UITableViewDataSource> {
    BOOL _shouldRefresh;
    __weak id<ICMRecentConversationsViewControllerDelegate> _delegate;
    NSInteger _numberOfConversationsToDisplay;
    UITableView *_tableView;
    NSLayoutConstraint *_tableViewHeightConstraint;
    id<ICMDataManagerProtocol> _dataManager;
    NSArray *_recentConversationsArray;
    id<_TtP18IntercomSDKPrivate13ThemeProvider_> _themeProvider;
}
@property (nonatomic, weak) id<ICMRecentConversationsViewControllerDelegate> delegate;
@property (nonatomic) NSInteger numberOfConversationsToDisplay;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSLayoutConstraint *tableViewHeightConstraint;
@property (nonatomic) BOOL shouldRefresh;
@property (nonatomic, strong) id<ICMDataManagerProtocol> dataManager;
@property (nonatomic, strong) NSArray *recentConversationsArray;
@property (nonatomic, strong) id<_TtP18IntercomSDKPrivate13ThemeProvider_> themeProvider;
- (instancetype)initWithDataManager:(id<ICMDataManagerProtocol>)dataManager themeProvider:(id<_TtP18IntercomSDKPrivate13ThemeProvider_>)provider;
- (void)setupTableView;
- (void)inboxDidUpdate;
- (void)refreshConversationsIfNeeded;
- (void)updateViewWithLatestConversations;
- (void)reloadTableView;
- (CGFloat)heightOfConversationsTableView;
@end
