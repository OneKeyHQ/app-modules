#import <UIKit/UIKit.h>
@class ITBBlock, ITBUpload;
@interface ICMBlockLoader : NSObject
+ (ITBBlock *)blockFromDictionary:(NSDictionary *)dictionary appearance:(id)appearance;
@end
@interface ITBBlockUtils : NSObject
+ (ITBBlock *)blockFromDictionary:(NSDictionary *)dictionary appearance:(id)appearance;
+ (ITBBlock *)blockForUpload:(ITBUpload *)upload appearance:(id)appearance;
+ (CGSize)calculateSizeForBlocks:(NSArray *)blocks withinWidth:(CGFloat)width constraintWidth:(BOOL)constraintWidth;
+ (void)prepareBlocks:(NSArray *)blocks completion:(void (^)(void))completion;
+ (NSBundle *)loadBlocksAssetsBundle;
@end
