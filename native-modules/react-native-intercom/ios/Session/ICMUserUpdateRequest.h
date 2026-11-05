#import <Foundation/Foundation.h>
@interface ICMUserUpdateRequest : NSObject {
    BOOL _newSession;
    BOOL _internalUpdate;
    BOOL _sentFromBackgroundAppState;
    NSString *_sessionId;
    NSDictionary *_attributes;
}
@property (nonatomic) BOOL newSession;
@property (nonatomic, copy) NSString *sessionId;
@property (nonatomic, getter=isInternalUpdate) BOOL internalUpdate;
@property (nonatomic, strong) NSDictionary *attributes;
@property (nonatomic) BOOL sentFromBackgroundAppState;
- (instancetype)initWithNewSession:(BOOL)newSession internalUpdate:(BOOL)internalUpdate sentFromBackgroundAppState:(BOOL)background;
- (instancetype)initWithNewSession:(BOOL)newSession sessionId:(NSString *)sessionId internalUpdate:(BOOL)internalUpdate sentFromBackgroundAppState:(BOOL)background;
- (instancetype)initWithAttributes:(NSDictionary *)attributes sentFromBackgroundAppState:(BOOL)background;
- (void)updateWithUserUpdate:(ICMUserUpdateRequest *)update;
- (BOOL)canBeUpdatedWithUpdate:(ICMUserUpdateRequest *)update;
- (BOOL)isEmptyUpdate;
- (NSTimeInterval)maxDelay;
- (void)addUserAttributes:(NSDictionary *)attributes;
- (BOOL)isEqualToUpdate:(ICMUserUpdateRequest *)update;
+ (NSDictionary *)addUserAttributes:(NSDictionary *)attributes toAttributes:(NSDictionary *)previous;
+ (BOOL)userAttributes:(NSDictionary *)attributes willOverwriteAttributes:(NSDictionary *)previous;
@end
