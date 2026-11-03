#pragma once
#import <Foundation/Foundation.h>
@class JPUSHBannerConfig, JPUSHFullConfig, JPUSHInterstitialConfig, NSLayoutConstraint;
@interface JPUSHInAppConfig : NSObject {
  NSString * _msg_id;
  long long _type;
  long long _bigType;
  NSArray * _target;
  NSString * _expired;
  long long _delay;
  long long _interval;
  NSString * _image;
  NSString * _localImagePath;
  long long _clickType;
  NSString * _clickAction;
  NSDictionary * _extras;
  JPUSHInterstitialConfig * _interstitialConfig;
  JPUSHFullConfig * _fullConfig;
  JPUSHBannerConfig * _bannerConfig;
  NSDictionary * _messageDic;
  double _height;
  double _width;
  double _left;
  double _landscapeLeft;
  double _right;
  double _landscapeRight;
  double _top;
  double _bottom;
  NSLayoutConstraint * _topBottomConstraint;
}
@property(nonatomic, copy) NSString * msg_id;
@property(nonatomic) long long type;
@property(nonatomic) long long bigType;
@property(nonatomic, strong) NSArray * target;
@property(nonatomic, copy) NSString * expired;
@property(nonatomic) long long delay;
@property(nonatomic) long long interval;
@property(nonatomic, copy) NSString * image;
@property(nonatomic, copy) NSString * localImagePath;
@property(nonatomic) long long clickType;
@property(nonatomic, copy) NSString * clickAction;
@property(nonatomic, strong) NSDictionary * extras;
@property(nonatomic, strong) JPUSHInterstitialConfig * interstitialConfig;
@property(nonatomic, strong) JPUSHFullConfig * fullConfig;
@property(nonatomic, strong) JPUSHBannerConfig * bannerConfig;
@property(nonatomic, strong) NSDictionary * messageDic;
@property(nonatomic) double height;
@property(nonatomic) double width;
@property(nonatomic) double left;
@property(nonatomic) double landscapeLeft;
@property(nonatomic) double right;
@property(nonatomic) double landscapeRight;
@property(nonatomic) double top;
@property(nonatomic) double bottom;
@property(nonatomic, strong) NSLayoutConstraint * topBottomConstraint;
@end
