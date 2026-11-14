#import <Foundation/Foundation.h>

typedef NS_ENUM(NSInteger, ITBIntentType) {
    ITBIntentTypeUrl = 0,
    ITBIntentTypeArticle = 1,
};

@protocol ITBIntent <NSObject>
- (ITBIntentType)intentType;
@optional
- (NSURL * _Nullable)intentUrl;
- (NSString * _Nullable)intentArticleId;
- (NSString * _Nullable)messengerAppId;
@end
