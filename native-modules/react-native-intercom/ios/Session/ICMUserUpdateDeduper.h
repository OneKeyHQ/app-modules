#import <Foundation/Foundation.h>
@class ICMUserUpdateRequest;
@interface ICMUserUpdateDeduper : NSObject <NSSecureCoding> {
    BOOL _hasUnsavedChanges;
    NSDictionary *_previouslyUpdatedAttributes;
    NSTimeInterval _previouslyUpdatedAttributesStartTime;
}
@property (nonatomic, strong) NSDictionary *previouslyUpdatedAttributes;
@property (nonatomic) NSTimeInterval previouslyUpdatedAttributesStartTime;
@property (nonatomic) BOOL hasUnsavedChanges;
+ (instancetype)instance;
- (void)storeUserUpdate;
- (BOOL)isDuplicateUserUpdate:(ICMUserUpdateRequest *)update;
- (void)clearStoredUserUpdates;
- (NSTimeInterval)currentTimestamp;
- (void)savePreviouslyUpdatedUserAttributes:(NSDictionary *)attributes previouslyUpdatedAttributesStartTime:(NSTimeInterval)time;
+ (instancetype)deduperFromDisk;
+ (void)saveDeduper:(ICMUserUpdateDeduper *)deduper;
+ (NSString *)archivePath;
- (BOOL)isEqualToDeduper:(ICMUserUpdateDeduper *)deduper;
@end
