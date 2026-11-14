#pragma once
#import "../Identity/ICMSafeNetworkModel.h"
#import <UIKit/UIKit.h>
@interface ICMScreenAction : ICMSafeNetworkModel <NSSecureCoding> {
    NSUInteger _type;
    NSString *_title;
    NSDictionary *_metadata;
    UIColor *_backgroundColor;
    UIColor *_textColor;
    NSURL *_uri;
    NSString *_screenId;
}
@property (nonatomic) NSUInteger type;
@property (nonatomic, copy) NSString *title;
@property (nonatomic, strong) NSDictionary *metadata;
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) UIColor *textColor;
@property (nonatomic, strong) NSURL *uri;
@property (nonatomic, strong) NSString *screenId;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
+ (NSUInteger)actionTypeForString:(NSString *)type;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (BOOL)isEqualToScreenAction:(ICMScreenAction *)action;
@end
