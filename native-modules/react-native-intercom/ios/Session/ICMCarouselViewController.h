#pragma once
#import <UIKit/UIKit.h>
#import "ICMCarousel.h"
#import "ICMScreenViewController.h"
#import "ICMContentViewController.h"
@interface ICMCarouselViewController : UIViewController <ICMContentViewController, ICMScreenDelegate, UIPageViewControllerDataSource, UIPageViewControllerDelegate>
@property (nonatomic, weak) id<ICMContentViewControllerDelegate> contentDelegate;
@property (nonatomic, strong) UIPageViewController *pageViewController;
@property (nonatomic, strong) NSArray<ICMScreen *> *screens;
@property (nonatomic, strong) NSMutableArray<ICMScreenViewController *> *screenViewControllerArray;
@property (nonatomic, strong) ICMCarousel *carousel;
@property (nonatomic, strong) UIPageControl *pageControl;
@property (nonatomic) NSInteger carouselFetchState;
@property (nonatomic) NSInteger currentIndex;
@property (nonatomic) NSInteger nextIndex;
@property (nonatomic, readonly) NSInteger progressIndex;
- (instancetype)initWithCarousel:(ICMCarousel *)carousel;
- (void)observeNotifications;
- (void)setupForCarousel;
- (void)setupForAutomaticCarousel;
- (void)setupForProgrammaticCarousel;
- (void)waitForConfigToLoadCarousel;
- (void)cancelCarouselLoadStillLoading;
- (void)setupScreens;
- (void)setupPageViewController;
- (void)addViewControllerAsChildViewController:(UIViewController *)controller;
- (void)setupScreenViewControllers;
- (void)setupPageControl;
- (void)configureForCarousel;
- (void)configureOverlayViewsColorsForScreenBackgroundColor:(UIColor *)color;
- (void)configurePageControlForScreenBackgroundColor:(UIColor *)color;
- (void)configureCarouselBackgroundColorForScreenBackgroundColor:(UIColor *)color;
- (void)updateOverlayViews;
- (void)showLoadingIndicator;
- (void)loadCarousel;
- (void)fetchCarousel;
- (void)carouselFetchDidFailWithStatusCode:(int)status;
- (void)configDidLoad;
- (void)pageControlValueChanged:(UIPageControl *)sender;
- (void)navigateToScreen:(ICMScreenViewController *)controller;
- (ICMScreenViewController *)fetchNextScreenViewControllerAtIndex:(NSInteger)index;
- (ICMScreenViewController *)viewControllerAtIndex:(NSInteger)index;
- (void)carouselDismissed:(NSInteger)context;
- (void)fetchedCarouselDismissed:(NSInteger)context;
- (void)recordCarouselStats;
- (BOOL)hasViewedLastScreen;
- (void)permissionSettingsWillOpen:(NSNotification *)notification;
@end
