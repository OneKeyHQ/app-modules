// Genuine original controller/translation interfaces; no substitute bodies.
#import <UIKit/UIKit.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate24HomescreenViewController")))
__attribute__((swift_name("HomescreenViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredHomescreenViewController : UIViewController
- (instancetype)initWithDelegate:(id)delegate;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate26ConversationViewController")))
__attribute__((swift_name("ConversationViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationViewController : UIViewController
- (instancetype)initWithConversation:(id)conversation;
- (void)updateComposer;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate31ConversationSpaceViewController")))
__attribute__((swift_name("ConversationSpaceViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredConversationSpaceViewController : UIViewController @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate33HelpCenterContainerViewController")))
__attribute__((swift_name("HelpCenterContainerViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredHelpCenterContainerViewController : UIViewController
- (instancetype)initWithDelegate:(id)delegate;
- (instancetype)initFromSearchBrowse:(BOOL)search delegate:(id)delegate;
- (instancetype)initWithCollections:(NSArray *)collections delegate:(id)delegate;
- (instancetype)initWithCollection:(id)collection delegate:(id)delegate;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19InboxViewController")))
__attribute__((swift_name("InboxViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredInboxViewController : UIViewController @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate25TicketsListViewController")))
__attribute__((swift_name("TicketsListViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketsListViewController : UIViewController @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate19ErrorViewController")))
__attribute__((swift_name("ErrorViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredErrorViewController : UIViewController
- (instancetype)initWithTitle:(NSString *)title subtitle:(NSString *)subtitle showActionButton:(BOOL)show actionButtonTitle:(NSString *)buttonTitle;
@property (nonatomic, weak) id errorDelegate;
@end
__attribute__((swift_name("TranslationManager")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMTranslationManager : NSObject
+ (instancetype)messenger;
- (NSString *)translationForKey:(NSString *)key;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate23HelpCenterMetricService")))
__attribute__((swift_name("HelpCenterMetricService")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredHelpCenterMetricService : NSObject @end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate24ArticleHostingController")))
__attribute__((swift_name("ArticleHostingController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredArticleHostingController : UIViewController
- (instancetype)initWithArticleID:(NSString *)identifier metricService:(ICMRecoveredHelpCenterMetricService *)service allowsReactions:(BOOL)reactions anchorLink:(NSString *)anchor;
@end
#import "ICMCarouselViewController.h"
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate30TicketCreateFormViewController")))
__attribute__((swift_name("TicketCreateFormViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketCreateFormViewController : UIViewController
- (instancetype)initWithTicketType:(id)type conversation:(id)conversation;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate27TicketDetailsViewController")))
__attribute__((swift_name("TicketDetailsViewController")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketDetailsViewController : UIViewController
@property (nonatomic, strong) id viewModel;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate22TicketDetailsViewModel")))
__attribute__((swift_name("TicketDetailsViewModel")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredTicketDetailsViewModel : NSObject
- (instancetype)initWithViewController:(UIViewController *)controller ticket:(id)ticket showSubmittedView:(BOOL)submitted triggerSource:(NSUInteger)source;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate20SurveyViewController")))
@interface ICMRecoveredSurveyViewController : UIViewController
- (instancetype)initWith:(id)survey;
@end
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate17HomeMetricService")))
__attribute__((swift_name("HomeMetricService")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMRecoveredHomeMetricService : NSObject
+ (void)addLauncherClickedMetric;
@end
