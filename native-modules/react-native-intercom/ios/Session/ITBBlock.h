#import <UIKit/UIKit.h>
#import "ICMBlockAppearance.h"
@interface ITBBlock : NSObject <NSCopying, NSSecureCoding> {
    BOOL _isRequired;
    BOOL _isPartOfAIAnswerCard;
    NSString *_text;
    NSString *_align;
    NSInteger _displayMode;
    CGSize _estimatedSize;
}
@property (nonatomic, copy) NSString *text;
@property (nonatomic, copy) NSString *align;
@property (nonatomic) CGSize estimatedSize;
@property (nonatomic) NSInteger displayMode;
@property (nonatomic) BOOL isRequired;
@property (nonatomic) BOOL isPartOfAIAnswerCard;
#if defined(__swift__)
// Preserve Objective-C inherited init without synthesizing an unavailable Swift override.
- (instancetype)init NS_SWIFT_UNAVAILABLE("Use initWithDictionary:appearance:");
#endif
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id<ITBBlockAppearance>)appearance;
- (void)prepare:(void (^)(void))completion;
- (void)updateAppearance:(id<ITBBlockAppearance>)appearance;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
+ (NSSet *)declaredPropertyNames;
- (NSInteger)type;
- (NSString *)plainTextRepresentation;
- (Class)viewClass;
- (NSString *)blockUniqueIdentifier;
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary;
+ (BOOL)isNonEmptyString:(id)value;
@end
