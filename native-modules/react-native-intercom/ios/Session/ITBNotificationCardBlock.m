// Source recovery modifications: see docs/IOS_PROVENANCE.md.
#import "ITBNotificationCardBlock.h"
#import "ITBNotificationCardBlockView.h"
#import "ICMBlockAppearance.h"
@implementation ITBNotificationCardBlock
@synthesize title = _title, titleAttributedString = _titleAttributedString, notificationChannels = _notificationChannels;
@synthesize userInput = _userInput, userInputAttributedString = _userInputAttributedString;
@synthesize displayPlaceholder = _displayPlaceholder, insets = _insets;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) {
        [self setChannels:dictionary appearance:appearance];
        [self updateAppearance:appearance];
    }
    return self;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([[self class] isNonEmptyString:value] &&
        ([key isEqualToString:@"text"] || [key isEqualToString:@"title"]))
        [super setValue:value forKey:key];
}
- (void)setChannels:(NSDictionary *)dictionary appearance:(id)appearance {
    self.displayPlaceholder = YES;
    self.userInput = [@"youremail@domain.com" copy];
    if ([dictionary[@"channels"] isKindOfClass:[NSArray class]]) {
        NSMutableArray *channels = [NSMutableArray new];
        for (id item in dictionary[@"channels"]) {
            ITBNotificationChannel *channel = [[ITBNotificationChannel alloc] initWithDictionary:item appearance:appearance];
            if (channel) {
                channel.defaultChannel = [dictionary[@"channels"] indexOfObject:item] == 0;
                [channels addObject:channel];
                if ([[self class] isNonEmptyString:channel.value] && self.displayPlaceholder) {
                    self.displayPlaceholder = NO;
                    self.userInput = channel.value;
                }
            }
        }
        self.notificationChannels = channels;
    }
}
- (void)updateAppearance:(id)appearance {
    self.insets = [appearance textInsets];
    self.titleAttributedString = [[NSAttributedString alloc] initWithString:self.title attributes:@{
        NSForegroundColorAttributeName:[appearance headingTextColor], NSFontAttributeName:[appearance headingFont]
    }];
    for (ITBNotificationChannel *channel in self.notificationChannels) [channel updateAppearance:appearance];
    self.userInputAttributedString = [[NSAttributedString alloc] initWithString:self.userInput attributes:@{
        NSForegroundColorAttributeName:[appearance textColor], NSFontAttributeName:[appearance paragraphFont]
    }];
}
- (NSInteger)type { return 13; }
- (Class)viewClass { return [ITBNotificationCardBlockView class]; }
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] &&
           [dictionary[@"channels"] isKindOfClass:[NSArray class]] &&
           [dictionary[@"channels"] count] != 0 &&
           [ITBNotificationChannel validateBlockDictionary:[dictionary[@"channels"] firstObject]] &&
           [self isNonEmptyString:dictionary[@"text"]] && [self isNonEmptyString:dictionary[@"title"]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBNotificationCardBlock *other = object;
    if (!((!other.notificationChannels && !self.notificationChannels) || [other.notificationChannels isEqualToArray:self.notificationChannels])) return NO;
    if (!((!other.userInput && !self.userInput) || [other.userInput isEqualToString:self.userInput])) return NO;
    if (!((!other.userInputAttributedString && !self.userInputAttributedString) || [other.userInputAttributedString.string isEqualToString:self.userInputAttributedString.string])) return NO;
    return other.displayPlaceholder == self.displayPlaceholder;
}
- (id)copyWithZone:(NSZone *)zone {
    ITBNotificationCardBlock *copy = [super copyWithZone:zone];
    copy.titleAttributedString = self.titleAttributedString;
    copy.notificationChannels = self.notificationChannels;
    copy.userInput = self.userInput;
    copy.userInputAttributedString = self.userInputAttributedString;
    copy.displayPlaceholder = self.displayPlaceholder;
    copy.insets = self.insets;
    return copy;
}
@end
@implementation ITBNotificationChannel
@synthesize defaultChannel = _defaultChannel, label = _label, labelAttributedString = _labelAttributedString;
@synthesize name = _name, value = _value;
- (instancetype)initWithDictionary:(NSDictionary *)dictionary appearance:(id)appearance {
    self = [super initWithDictionary:dictionary appearance:appearance];
    if (self) [self updateAppearance:appearance];
    return self;
}
- (void)updateAppearance:(id)appearance {
    NSMutableAttributedString *label = [[NSMutableAttributedString alloc] initWithString:self.label];
    NSMutableDictionary *attributes = [NSMutableDictionary new];
    attributes[NSFontAttributeName] = [appearance subheadingFont];
    UIColor *color = self.defaultChannel ? [appearance linkTextColor] : [appearance subHeadingTextColor];
    attributes[NSForegroundColorAttributeName] = color;
    [label setAttributes:attributes range:NSMakeRange(0, label.length)];
    self.labelAttributedString = label;
}
- (void)setValue:(id)value forKey:(NSString *)key {
    if ([[self class] isNonEmptyString:value] &&
        ([key isEqualToString:@"name"] || [key isEqualToString:@"label"] || [key isEqualToString:@"value"]))
        [super setValue:value forKey:key];
}
+ (BOOL)validateBlockDictionary:(NSDictionary *)dictionary {
    return [super validateBlockDictionary:dictionary] &&
           [[self class] isNonEmptyString:dictionary[@"name"]] &&
           [[self class] isNonEmptyString:dictionary[@"label"]];
}
- (BOOL)isEqual:(id)object {
    if (self == object) return YES;
    if (![object isKindOfClass:[self class]]) return NO;
    if (![super isEqual:object]) return NO;
    ITBNotificationChannel *other = object;
    if (!((!other.name && !self.name) || [other.name isEqualToString:self.name])) return NO;
    if (!((!other.label && !self.label) || [other.label isEqualToString:self.label])) return NO;
    if (!((!other.value && !self.value) || [other.value isEqualToString:self.value])) return NO;
    return (!other.labelAttributedString && !self.labelAttributedString) ||
           [other.labelAttributedString.string isEqualToString:self.labelAttributedString.string];
}
- (id)copyWithZone:(NSZone *)zone {
    ITBNotificationChannel *copy = [super copyWithZone:zone];
    copy.name = self.name;
    copy.label = self.label;
    copy.value = self.value;
    copy.labelAttributedString = self.labelAttributedString;
    return copy;
}
@end
