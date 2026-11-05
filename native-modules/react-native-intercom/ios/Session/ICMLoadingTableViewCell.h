#import <UIKit/UIKit.h>
__attribute__((objc_runtime_name("_TtC18IntercomSDKPrivate18LoadingSpinnerView")))
__attribute__((swift_name("LoadingSpinnerView")))
__attribute__((external_source_symbol(language="Swift", defined_in="Intercom", generated_declaration)))
@interface ICMNativeLoadingSpinnerView : UIView
- (void)startSpinning;
@end
@interface ICMLoadingTableViewCell : UITableViewCell
@property (nonatomic, strong) ICMNativeLoadingSpinnerView *loadingView;
- (void)setupLoadingView;
- (void)startSpinning;
@end
