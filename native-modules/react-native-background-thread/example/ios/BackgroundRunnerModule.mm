//
//  BackgroundRunnerModule.m
//  BackgroundThreadExample
//
//  Created by hu on 2025/12/13.
//

#import "BackgroundRunnerModule.h"
#import <BackgroundThread/BackgroundThread.h>

NS_ASSUME_NONNULL_BEGIN

@implementation BackgroundRunnerModule

+ (void)startBackgroundRunner {
  [BackgroundThread.sharedInstance startBackgroundRunnerWithEntryURL:@"http://localhost:8082/index.bundle?platform=ios&dev=true&lazy=true&minify=false&inlineSourceMap=false&modulesOnly=false&runModule=true&excludeSource=true&sourcePaths=url-server&app=backgroundthread.example"];
}
@end

NS_ASSUME_NONNULL_END
