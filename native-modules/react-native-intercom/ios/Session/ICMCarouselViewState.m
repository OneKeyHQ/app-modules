#import "ICMCarouselViewState.h"
@implementation ICMCarouselViewState
@synthesize currentIndex = _currentIndex, nextIndex = _nextIndex, progressIndex = _progressIndex;
- (void)setProgressIndex:(NSInteger)value { if (value > _progressIndex) { _progressIndex = value; } }
- (BOOL)isEqual:(id)object {
    if (object == self) { return YES; }
    if (!object || ![[object class] isEqual:[self class]]) { return NO; }
    return [self isEqualToCarouselViewState:object];
}
- (BOOL)isEqualToCarouselViewState:(ICMCarouselViewState *)state {
    return self.currentIndex == state.currentIndex && self.nextIndex == state.nextIndex && self.progressIndex == state.progressIndex;
}
- (NSUInteger)hash {
    uint32_t bits = (uint32_t)(int32_t)_currentIndex ^ (uint32_t)(int32_t)_nextIndex ^ (uint32_t)(int32_t)_progressIndex;
    return (NSUInteger)(NSInteger)(int32_t)bits;
}
+ (BOOL)supportsSecureCoding { return YES; }
- (void)encodeWithCoder:(NSCoder *)coder {
    [coder encodeInteger:self.currentIndex forKey:NSStringFromSelector(@selector(currentIndex))];
    [coder encodeInteger:self.nextIndex forKey:NSStringFromSelector(@selector(nextIndex))];
    [coder encodeInteger:self.progressIndex forKey:NSStringFromSelector(@selector(progressIndex))];
}
- (instancetype)initWithCoder:(NSCoder *)coder {
    self = [self init];
    if (self) {
        self.currentIndex = [coder decodeIntegerForKey:NSStringFromSelector(@selector(currentIndex))];
        self.nextIndex = [coder decodeIntegerForKey:NSStringFromSelector(@selector(nextIndex))];
        self.progressIndex = [coder decodeIntegerForKey:NSStringFromSelector(@selector(progressIndex))];
    }
    return self;
}
@end
