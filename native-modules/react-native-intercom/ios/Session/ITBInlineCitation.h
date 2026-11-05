#import <Foundation/Foundation.h>
@interface ITBInlineCitation : NSObject {
    NSString *_sourceIndex;
    NSInteger _entityType;
    NSString *_href;
    NSString *_title;
}
@property (nonatomic, strong) NSString *sourceIndex;
@property (nonatomic) NSInteger entityType;
@property (nonatomic, strong) NSString *href;
@property (nonatomic, strong) NSString *title;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary;
@end

@interface ITBInlineCitationParser : NSObject
+ (NSArray *)parseInlineCitationsFromHTML:(NSString *)html;
@end
