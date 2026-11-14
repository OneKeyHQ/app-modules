#pragma once
#import "../Identity/ICMSafeNetworkModel.h"
#import "ICMBlockAppearance.h"
#import "ITBBlock.h"
#import "ICMPermissionScreenAction.h"
@interface ICMScreen : ICMSafeNetworkModel <NSSecureCoding> {
    NSString *_screenId;
    NSArray<ICMScreenAction *> *_actions;
    NSString *_backgroundColor;
    NSUInteger _alignment;
    NSArray<ITBBlock *> *_blocksList;
    NSString *_textColor;
    ICMBlockAppearance *_appearance;
}
@property (nonatomic, copy) NSString *textColor;
@property (nonatomic, strong) ICMBlockAppearance *appearance;
@property (nonatomic, strong) NSString *screenId;
@property (nonatomic, strong) NSArray<ICMScreenAction *> *actions;
@property (nonatomic, copy) NSString *backgroundColor;
@property (nonatomic) NSUInteger alignment;
@property (nonatomic, strong) NSArray<ITBBlock *> *blocksList;
+ (instancetype)instanceFromDictionary:(NSDictionary *)dictionary;
+ (NSUInteger)alignmentForString:(NSString *)string;
- (void)setAttributesFromDictionary:(NSDictionary *)dictionary;
- (void)setScreenBackgroundColor:(NSDictionary *)dictionary;
- (void)setId:(NSDictionary *)dictionary;
- (void)setBlocks:(NSArray *)blocks;
- (BOOL)doesBlockRequiresLoadingView:(ITBBlock *)block;
- (void)setLoadingViewColorType:(ITBBlock *)block;
- (void)setScreenActions:(NSArray *)actions;
- (void)setCustomAppearance:(NSDictionary *)dictionary;
- (ICMPermissionScreenAction *)fetchPermissionActionIfExists;
- (BOOL)isEqualToScreen:(ICMScreen *)screen;
@end
