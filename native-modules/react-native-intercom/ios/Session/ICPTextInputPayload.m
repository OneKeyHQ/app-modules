#import "ICPTextInputPayload.h"
@implementation ICPTextInputPayload
@synthesize title = _title, placeholder = _placeholder, currentText = _currentText;
@synthesize inputId = _inputId, messengerAppId = _messengerAppId, completionBlock = _completionBlock;
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (!object || ![[object class] isEqual:[self class]]) return NO;
    return [self isEqualToTextInputPayload:object];
}
- (BOOL)isEqualToTextInputPayload:(ICPTextInputPayload *)payload {
    if (self == payload) return YES;
    if (!payload) return NO;
    if (!(self.title == payload.title || [self.title isEqualToString:payload.title])) return NO;
    if (!(self.placeholder == payload.placeholder || [self.placeholder isEqualToString:payload.placeholder])) return NO;
    if (!(self.currentText == payload.currentText || [self.currentText isEqualToString:payload.currentText])) return NO;
    if (!(self.inputId == payload.inputId || [self.inputId isEqualToString:payload.inputId])) return NO;
    if (!(self.messengerAppId == payload.messengerAppId || [self.messengerAppId isEqualToString:payload.messengerAppId])) return NO;
    return YES;
}
- (NSUInteger)hash { return self.title.hash; }
@end
