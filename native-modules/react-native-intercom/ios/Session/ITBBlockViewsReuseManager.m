#import "ITBBlockViewsReuseManager.h"
#import "ITBTextBlockView.h"
@implementation ITBBlockViewsReuseManager
+ (instancetype)sharedInstance {
    static ITBBlockViewsReuseManager *shared;
    static dispatch_once_t once;
    dispatch_once(&once, ^{ shared = [ITBBlockViewsReuseManager new]; });
    return shared;
}
- (instancetype)init {
    self = [super init];
    if (self) {
        _reusableDict = [NSMutableDictionary new];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receivedMemoryWarning) name:UIApplicationDidReceiveMemoryWarningNotification object:nil];
    }
    return self;
}
- (void)dealloc { [[NSNotificationCenter defaultCenter] removeObserver:self]; _reusableDict = nil; }
- (void)receivedMemoryWarning {
    for (NSArray *views in [_reusableDict allValues]) {
        for (ITBBlockView *view in views) [view prepareForReuse];
    }
}
- (void)tearDown {
    if (_reusableDict[NSStringFromClass([ITBTextBlockView class])]) [ITBTextBlockView tearDown];
    [_reusableDict removeAllObjects];
}
- (ITBBlockView *)dequeueReusableViewForBlock:(ITBBlock *)block {
    if (block) {
        NSString *key = NSStringFromClass(block.viewClass);
        NSMutableArray *views = _reusableDict[key];
        if (!views) _reusableDict[key] = [[NSMutableArray alloc] initWithCapacity:5];
        else if (views.count) {
            NSString *identifier = block.blockUniqueIdentifier;
            ITBBlockView *view;
            if (identifier.length) {
                __block ITBBlockView *matching = nil;
                [views enumerateObjectsUsingBlock:^(ITBBlockView *candidate, __unused NSUInteger index, BOOL *stop) {
                    if ([identifier isEqualToString:candidate.uniqueIdentifier]) { matching = candidate; *stop = YES; }
                }];
                view = matching ?: views.lastObject;
            } else view = views.lastObject;
            [views removeObject:view];
            [view prepareForReuse];
            if (view) return view;
        }
    }
    return [block.viewClass new];
}
- (void)enqueueReusableViewForReuse:(ITBBlockView *)view {
    NSMutableArray *views = _reusableDict[NSStringFromClass([view class])];
    if (!views || views.count > 4) return;
    if (view.uniqueIdentifier.length) {
        __block BOOL duplicate = NO;
        [views enumerateObjectsUsingBlock:^(ITBBlockView *candidate, __unused NSUInteger index, BOOL *stop) {
            if ([view.uniqueIdentifier isEqualToString:candidate.uniqueIdentifier]) { duplicate = YES; *stop = YES; }
        }];
        if (!duplicate) [views addObject:view];
    } else [views addObject:view];
}
@end
