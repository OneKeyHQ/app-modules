// Source recovery modifications: see docs/SURVEY_PROVENANCE.json.
#import <Foundation/Foundation.h>

@protocol ICMInputViewProtocol <NSObject>
// Original Objective-C protocol metadata uses strong/atomic; the separate Swift-visible record differs.
@property (strong, nullable) id inputValue;
@property (nonatomic) BOOL isRequired;
- (BOOL)isUserInputValid:(NSError * _Nullable * _Nullable)error;
- (void)configureErrorState;
- (void)configureDefaultState;
@end

@protocol ICMSubmittableInputViewProtocol <ICMInputViewProtocol>
- (void)configureSuccessState;
- (void)configureLoadingState;
- (void)configureDisabledState;
@end

@protocol ICMInputViewDelegate <NSObject>
- (void)entryCompleteButtonTapped;
- (void)cancelButtonTapped;
- (void)valueUpdated:(nullable id)value;
@optional
- (void)inputFieldDidBeginEditing;
- (void)inputFieldDidEndEditing;
@end
