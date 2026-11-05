#pragma once
#import <UIKit/UIKit.h>
#import "ICMWindowDependencies.h"
#import "ICMDataManagerRequestDependencies.h"
__attribute__((swift_name("ThemeProvider")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@protocol _TtP18IntercomSDKPrivate13ThemeProvider_
@property (nonatomic, readonly) ICMRecoveredTheme *theme;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate17InboxCellViewItem")))
__attribute__((swift_name("InboxCellViewItem")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredInboxCellViewItem : NSObject
- (instancetype)initWithConversation:(ICMConversation *)conversation currentUserIntercomId:(NSString *)identifier canShowUnreadDot:(BOOL)unread showDividerLine:(BOOL)divider ticketsSpaceEnabled:(BOOL)tickets;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate9InboxCell")))
__attribute__((swift_name("InboxCell")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredInboxCell : UITableViewCell
- (void)configureFor:(ICMRecoveredInboxCellViewItem *)item;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19TicketsListViewCell")))
__attribute__((swift_name("TicketsListViewCell")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketsListViewCell : UITableViewCell
@property (nonatomic, strong) id<_TtP18IntercomSDKPrivate13ThemeProvider_> themeProvider;
- (void)configureWithTicket:(ICMRecoveredTicket *)ticket showDividerLine:(BOOL)divider canShowUnreadDot:(BOOL)unread;
@end
