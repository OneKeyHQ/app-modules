//
//  RNWebViewCleaner.m
//  RNWebViewCleaner
//
//  Created by M.H. Kang on 05/12/2018.
//  Copyright © 2018 M.H. Kang. All rights reserved.
//

#import "RNWebViewCleaner.h"

#import <WebKit/WebKit.h>

@implementation RNWebViewCleaner

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(clearAll: (RCTPromiseResolveBlock)resolve rejecter: (RCTPromiseRejectBlock)reject)
{
    NSSet *websiteDataTypes = [WKWebsiteDataStore allWebsiteDataTypes];
    NSDate *dateFrom = [NSDate dateWithTimeIntervalSince1970:0];

    dispatch_async(dispatch_get_main_queue(), ^{
        [[WKWebsiteDataStore defaultDataStore] removeDataOfTypes:websiteDataTypes modifiedSince:dateFrom completionHandler:^{
            resolve(nil);
        }];
    });
}

@end
