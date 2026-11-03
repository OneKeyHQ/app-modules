#import <TargetConditionals.h>
#if !TARGET_OS_SIMULATOR
#import "JPUSHNotiInMessageH5Manager.h"
#import "JCOREThread.h"
#import "JCORELog.h"
#import "RecoveredCache.h"
#import "JPUSHHelper.h"
#define H5_LOG(lineValue, message, ...)                                         \
  [JCORELog log:YES level:0x3ff flag:0x100 context:0                             \
      file:"/Users/jiguang/.jenkins/workspace/ios-core-push-dev/"               \
           "ios-jpush/JPush/JPush/Controller/NotiInApp/JPUSHNotiInMessageH5Manager.m" \
      function:__PRETTY_FUNCTION__ line:lineValue tag:nil                        \
      format:message, __PRETTY_FUNCTION__, ##__VA_ARGS__]
@implementation JPUSHNotiInMessageH5Manager
@synthesize maxCountLRU = _maxCountLRU, imageLocalpaths = _imageLocalpaths;
@synthesize finshiImageCount = _finshiImageCount, imageblock = _imageblock;
@synthesize beyondTime = _beyondTime, downimageing = _downimageing, count = _count;
- (instancetype)initWithMaxCount:(unsigned long long)count {
  if ((self = [super init])) {
    _maxCountLRU = count;
    dowloadQueue = dispatch_queue_create("io.jiguang.hxhg.inapp", DISPATCH_QUEUE_CONCURRENT);
  }
  return self;
}
- (void)filepath:(NSString *)mouldID networkPath:(NSString *)path block:(JPUSHH5FilePathCompletion)block {
  if (!mouldID.length || !path.length || !block) {
    H5_LOG(0x30, @"[%s] mouldID or networkpath is nil");
    return;
  }
  if ([JCOREThread isMainThread])
    [self h5FilePathForMouldID:mouldID withNetWorkPath:path filePathBlock:block];
  else
    [self performSelector:@selector(filePathWithParam:) onThread:[JCOREThread mainThread]
        withObject:@{@"mouldID" : mouldID, @"networkpath" : path, @"block" : block}
        waitUntilDone:NO];
}
- (void)filePathWithParam:(id)param {
  [self h5FilePathForMouldID:[param objectForKey:@"mouldID"]
      withNetWorkPath:[param objectForKey:@"networkpath"]
      filePathBlock:[param objectForKey:@"block"]];
}
- (void)h5FilePathForMouldID:(NSString *)mouldID withNetWorkPath:(NSString *)path
    filePathBlock:(JPUSHH5FilePathCompletion)block {
  if (!mouldID || !mouldID.length) {
    H5_LOG(0x49, @"[%s] mouldID is nil");
    return;
  }
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x4d, @"[%s] thread is not JCOREThread");
    return;
  }
  NSString *cached = [self filePathWithMoulID:mouldID];
  if (!cached) {
    H5_LOG(0x5c, @"[%s] mould is not exist");
    [self downloadNetworkPath:path mouldID:mouldID filePathBlock:block];
    return;
  }
  H5_LOG(0x53, @"[%s] mould is exist");
  [self adjustPositionLRU:mouldID];
  if (block)
    block(cached);
  else
    H5_LOG(0x59, @"[%s] block is nil");
}
- (void)downloadNetworkPath:(NSString *)path mouldID:(NSString *)mouldID
    filePathBlock:(JPUSHH5FilePathCompletion)block {
  if (!path.length || !mouldID.length || !block) {
    H5_LOG(0x64, @"[%s]path or mouldID or block is nil");
    return;
  }
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x68, @"[%s] thread is not JCOREThread");
    return;
  }
  if (dowloadQueue)
    dispatch_async(dowloadQueue, ^{
      NSURL *url = [NSURL URLWithString:path];
      if (!url) {
        H5_LOG(0x7c, @"[%s] url is nil");
        if (block)
          block(nil);
      } else {
        NSData *data = [NSData dataWithContentsOfURL:url] ?: [NSData data];
        [self performSelector:@selector(setH5Dic:) onThread:[JCOREThread mainThread]
            withObject:@{@"H5Data" : data, @"mouldID" : mouldID, @"block" : block}
            waitUntilDone:NO];
      }
    });
}
- (void)setH5Dic:(id)param {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x8c, @"[%s] thread is not JCOREThread");
    return;
  }
  if (!param) {
    H5_LOG(0x90, @"[%s] h5dic is nil");
    return;
  }
  @try {
    id data = [param objectForKey:@"H5Data"];
    id mouldID = [param objectForKey:@"mouldID"];
    JPUSHH5FilePathCompletion block = [param objectForKey:@"block"];
    if (![data isKindOfClass:NSData.class] || ![data length]) {
      H5_LOG(0x98, @"[%s] h5data is nil");
      block(nil);
      return;
    }
    if ([self setH5Data:data withMouldID:mouldID])
      [self addObjectLRU:mouldID];
    else
      H5_LOG(0xa2, @"[%s] writeResult is false");
    if (block)
      block([self filePathWithMoulID:mouldID]);
  } @catch (NSException *exception) {
    H5_LOG(0xa9, @"[bomb][%s] is exception");
  }
}
- (BOOL)setH5Data:(id)data withMouldID:(id)mouldID {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0xb0, @"[%s] thread is not JCOREThread");
    return NO;
  }
  if (!data || !mouldID) {
    H5_LOG(0xb4, @"[%s] h5data or mouldID is nil");
    return NO;
  }
  BOOL result = NO;
  @try {
    NSString *directory = [self directoryPath];
    if (!directory.length) {
      H5_LOG(0xc2, @"[%s] dirPath is nil");
      return NO;
    }
    NSString *path = [NSString stringWithFormat:@"%@/%@.html", directory, mouldID];
    result = [data writeToFile:path atomically:YES];
    if (result)
      H5_LOG(0xbd, @"[%s] H5Data writeTofile success");
    else
      H5_LOG(0xbf, @"[%s] H5Data writeTofile failure");
  } @catch (NSException *exception) {
    H5_LOG(0xc5, @"[bomb][%s] is exception");
  }
  return result;
}
- (void)removeAllImage {
  if ([JCOREThread isMainThread])
    [self removeAllFilePath];
  else
    [self performSelector:@selector(removeAllFilePath) onThread:[JCOREThread mainThread]
        withObject:nil waitUntilDone:NO];
}
- (BOOL)removeAllFilePath {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0xd5, @"[%s] thread is not JCOREThread");
    return NO;
  }
  @try {
    NSString *path = [self imageTempPath];
    if (!path)
      H5_LOG(0xe5, @"[%s] dirpath is not exist");
    else if ([NSFileManager.defaultManager removeItemAtPath:path error:NULL])
      H5_LOG(0xdf, @"[%s] remove dirpath is success");
    else
      H5_LOG(0xe2, @"[%s] remove dirpath is failure");
  } @catch (NSException *exception) {
    H5_LOG(0xe8, @"[bomb][%s] is exception");
  }
  return NO;
}
- (void)dowloadImage:(id)paths block:(JPUSHH5ImageCompletion)block {
  if (![paths isKindOfClass:NSArray.class] || ![paths count] || !block) {
    H5_LOG(0xf0, @"[%s] imagepaths or block is nil");
    return;
  }
  if ([JCOREThread isMainThread]) {
    H5_LOG(0xfa, @"[%s] thread is not mainthread");
    return;
  }
  [self performSelector:@selector(dowloadImageInJCoreThread:)
      onThread:[JCOREThread mainThread] withObject:@{@"imagepaths" : paths, @"block" : block}
      waitUntilDone:NO];
}
- (void)dowloadImageInJCoreThread:(id)param {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x100, @"[%s] thread is not JCoreThread");
    return;
  }
  id paths = [param objectForKey:@"imagepaths"];
  id block = [param objectForKey:@"block"];
  if (![paths isKindOfClass:NSArray.class] || ![paths count] || !block)
    H5_LOG(0x106, @"[%s] imagepaths or block is nil");
  else
    [self dowloadImageInJCoreThread:paths block:block];
}
- (void)dowloadImageInJCoreThread:(id)paths block:(JPUSHH5ImageCompletion)block {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x10e, @"[%s] thread is not mainthread");
    return;
  }
  if (![paths count]) {
    H5_LOG(0x112, @"[%s] imagepaths is nil");
    if (block)
      block(NO, nil);
    return;
  }
  if (self.downimageing) {
    H5_LOG(0x119, @"[%s] is downloading");
    if (block)
      block(NO, nil);
    return;
  }
  @try {
    self.downimageing = YES;
    [self stopTimer];
    self.beyondTime = [NSTimer scheduledTimerWithTimeInterval:60 target:self
        selector:@selector(downloadImagetimeout) userInfo:nil repeats:NO];
    self.finshiImageCount = 0;
    if (block)
      self.imageblock = block;
    self.imageLocalpaths = [[NSMutableArray alloc] initWithArray:paths copyItems:YES];
    for (NSUInteger index = 0; index < [paths count]; index++)
      [self downLoadImage:[paths objectAtIndex:index] withIndex:(long long)index];
  } @catch (NSException *exception) {
    H5_LOG(0x131, @"[bomb][%s] is exception");
  }
}
- (void)downLoadImage:(id)path withIndex:(long long)index {
  if (![path isKindOfClass:NSString.class] || ![path length]) {
    H5_LOG(0x137, @"[%s] filepath is nil");
    return;
  }
  if (dowloadQueue)
    dispatch_async(dowloadQueue, ^{
      NSURL *url = [NSURL URLWithString:path];
      if (url) {
        NSData *data = [[NSData alloc] initWithContentsOfURL:url];
        NSNumber *number = [NSNumber numberWithInteger:(NSInteger)index];
        number = number ? [NSNumber numberWithInteger:(NSInteger)index] : @0;
        [self performSelector:@selector(handleDownloadImageResultParam:)
            onThread:[JCOREThread mainThread]
            withObject:@{@"filepath" : path ?: @"", @"index" : number,
                         @"data" : data ?: [NSData data]}
            waitUntilDone:NO];
      }
    });
}
- (void)handleDownloadImageResultParam:(id)param {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x150, @"[%s] thread is not mainThread");
    return;
  }
  @try {
    id data = [param objectForKey:@"data"];
    if (!data || ![data isKindOfClass:NSData.class]) {
      data = [NSData data];
      H5_LOG(0x157, @"[%s] data is nil");
    }
    id path = [param objectForKey:@"filepath"];
    if (!path || ![path isKindOfClass:NSString.class]) {
      H5_LOG(0x15c, @"[%s] filepath is nil");
      path = @"";
    }
    id index = [param objectForKey:@"index"];
    if (!index || ![index isKindOfClass:NSNumber.class]) {
      index = @0;
      H5_LOG(0x161, @"[%s] indexnum is nil");
    }
    [self handleDownloadImageResult:data withFilePath:path withIndex:[index integerValue]];
  } @catch (NSException *exception) {
    H5_LOG(0x166, @"[bomb][%s] is exception");
  }
}
- (void)handleDownloadImageResult:(id)data withFilePath:(id)path withIndex:(long long)index {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x16c, @"[%s] thread is not JCorethread");
    return;
  }
  if (!self.downimageing) {
    H5_LOG(0x171, @"[%s] downimageing is end");
    return;
  }
  @try {
    if (![self.imageLocalpaths containsObject:path]) {
      H5_LOG(0x176, @"[%s] imageLocalpaths is not containsObject");
      return;
    }
    if (![data length]) {
      H5_LOG(0x17c, @"[%s] image data is bad");
      self.finshiImageCount = (long long)((unsigned long long)self.finshiImageCount + 1);
      [self checkWhetherImagesFinsh];
      return;
    }
    NSString *local = [self setImageDataWithData:data withimageName:[path lastPathComponent]];
    if (local.length) {
      if ((NSUInteger)index < self.imageLocalpaths.count) {
        [self.imageLocalpaths replaceObjectAtIndex:(NSUInteger)index withObject:local];
        self.finshiImageCount = (long long)((unsigned long long)self.finshiImageCount + 1);
        [self checkWhetherImagesFinsh];
        return;
      }
      [self endDownloadImage:NO];
      H5_LOG(0x190, @"[%s] index beyond array");
      return;
    }
    self.finshiImageCount = (long long)((unsigned long long)self.finshiImageCount + 1);
    [self checkWhetherImagesFinsh];
    H5_LOG(0x196, @"[%s] localpath is bad");
  } @catch (NSException *exception) {
    H5_LOG(0x199, @"[bomb][%s] is exception");
  }
}
- (void)checkWhetherImagesFinsh {
  if ((unsigned long long)self.finshiImageCount == self.imageLocalpaths.count) {
    H5_LOG(0x19f, @"[%s] downloadimage is finsh");
    [self endDownloadImage:YES];
  } else {
    H5_LOG(0x1a2, @"[%s] downloadimage coun is %ld", (long)self.finshiImageCount);
  }
}
- (NSString *)setImageDataWithData:(id)data withimageName:(id)name {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x1a9, @"[%s] thread is not JcoreThread");
    return nil;
  }
  NSString *result = nil;
  @try {
    NSString *directory = [self imageTempPath];
    if (!directory) {
      H5_LOG(0x1bd, @"[%s] dirpath is nil");
      return nil;
    }
    result = [NSString stringWithFormat:@"%@/%@", directory, name];
    if (!result) {
      H5_LOG(0x1ba, @"[%s] filepath is nil");
      return nil;
    }
    if ([data writeToFile:result atomically:YES])
      H5_LOG(0x1b3, @"[%s] imagename write is success");
    else {
      H5_LOG(0x1b5, @"[%s] imagename write is failure");
      result = @"";
    }
  } @catch (NSException *exception) {
    H5_LOG(0x1c0, @"[bomb] [%s] is exception");
  }
  return result;
}
- (void)endDownloadImage:(BOOL)success {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x1c7, @"[%s] thread is not Jcorethread");
    return;
  }
  [self stopTimer];
  if (!self.downimageing) {
    H5_LOG(0x1ce, @"[%s] downloadImage already is end");
    return;
  }
  self.downimageing = NO;
  if (self.imageblock)
    self.imageblock(success, success ? self.imageLocalpaths : nil);
  self.imageLocalpaths = [NSMutableArray array];
  self.finshiImageCount = 0;
  self.imageblock = nil;
}
- (void)downloadImagetimeout {
  [self endDownloadImage:NO];
}
- (void)stopTimer {
  if (self.beyondTime.isValid) {
    [self.beyondTime invalidate];
    self.beyondTime = nil;
  }
}
- (void)removeTempImagepaths:(id)paths {
  if ([JCOREThread isMainThread])
    [self removeTempImagepathsInJCorethread:paths];
  else
    [self performSelector:@selector(removeTempImagepathsInJCorethread:)
        onThread:[JCOREThread mainThread] withObject:paths waitUntilDone:NO];
}
- (void)removeTempImagepathsInJCorethread:(id)paths {
  H5_LOG(0x1f7, @"[%s] begin removeTempImagepaths");
  if (![paths isKindOfClass:NSArray.class] || ![paths count]) {
    H5_LOG(0x1f9, @"[%s] imagepaths is nil");
    return;
  }
  @try {
    for (id path in [paths copy])
      [self removeFileWithpPath:path];
  } @catch (NSException *exception) {
    H5_LOG(0x202, @"[bomb][%s] is exception");
  }
}
- (void)downloadSingleImage:(id)path successBlock:(JPUSHH5ImageSuccess)success
    failBlock:(JPUSHH5ImageFailure)failure {
  if (![path isKindOfClass:NSString.class] || ![path length]) {
    H5_LOG(0x209, @"[%s] imagepath is nil");
    return;
  }
  JPUSHH5ImageSuccess successWrapper = ^(NSString *local) {
    if (success)
      success(local);
  };
  JPUSHH5ImageFailure failureWrapper = ^(NSError *error) {
    if (failure)
      failure(error);
  };
  if ([JCOREThread isMainThread]) {
    H5_LOG(0x21f, @"[%s] thread is not mainthread");
    return;
  }
  [self performSelector:@selector(downloadSingleImage:) onThread:[JCOREThread mainThread]
      withObject:@{@"imagepath" : path, @"successBlock" : successWrapper,
                   @"failBlock" : failureWrapper} waitUntilDone:NO];
}
- (void)downloadSingleImage:(id)param {
  if (![JCOREThread isMainThread])
    return;
  id path = [param valueForKey:@"imagepath"];
  JPUSHH5ImageSuccess success = [param valueForKey:@"successBlock"];
  JPUSHH5ImageFailure failure = [param valueForKey:@"failBlock"];
  if (dowloadQueue)
    dispatch_async(dowloadQueue, ^{
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
      NSString *escaped = [path stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
#pragma clang diagnostic pop
      [self downloadFile:[NSURL URLWithString:escaped] timeout:5.0
          completion:^(NSData *data, NSError *error) {
            NSMutableDictionary *result = [NSMutableDictionary dictionary];
            [result setObject:path ?: @"" forKey:@"imagepath"];
            [result setObject:data ?: [NSData data] forKey:@"data"];
            if (success)
              [result setObject:success forKey:@"successBlock"];
            if (failure)
              [result setObject:failure forKey:@"failBlock"];
            if (error)
              [result setObject:error forKey:@"error"];
            if ([JCOREThread isMainThread])
              [self handleDownloadImageResultOnJcoreMainThread:result];
            else
              [self performSelector:@selector(handleDownloadImageResultOnJcoreMainThread:)
                  onThread:[JCOREThread mainThread] withObject:result waitUntilDone:NO];
          }];
    });
}
- (void)handleDownloadImageResultOnJcoreMainThread:(id)param {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x248, @"[%s] thread is not JCOREThread");
    return;
  }
  id path = [param valueForKey:@"imagepath"];
  id data = [param valueForKey:@"data"];
  id error = [param valueForKey:@"error"];
  JPUSHH5ImageSuccess success = [param valueForKey:@"successBlock"];
  JPUSHH5ImageFailure failure = [param valueForKey:@"failBlock"];
  if (error || !data || ![data length]) {
    if (failure)
      failure(error);
    return;
  }
  NSString *local = [self setImageDataWithData:data withimageName:[path lastPathComponent]];
  if (local && [local isKindOfClass:NSString.class] && local.length) {
    if (success)
      success(local);
  } else if (failure) {
    failure(nil);
  }
}
- (void)downloadFile:(id)url timeout:(double)timeout completion:(JPUSHH5DownloadCompletion)completion {
  if (!url || ![url isKindOfClass:NSURL.class]) {
    if (completion)
      completion(nil, nil);
    return;
  }
  NSURLSessionConfiguration *configuration = NSURLSessionConfiguration.defaultSessionConfiguration;
  configuration.timeoutIntervalForRequest = timeout;
  NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
  NSURLSessionDownloadTask *task = [session downloadTaskWithURL:url
      completionHandler:^(NSURL *location, NSURLResponse *response, NSError *error) {
        if (!error && location) {
          if (![response isKindOfClass:NSHTTPURLResponse.class] ||
              [(NSHTTPURLResponse *)response statusCode] == 200) {
            NSData *data = [NSData dataWithContentsOfURL:location];
            BOOL supported = [JPUSHHelper isPngOrJpgImage:data];
            NSData *result = supported && data.length <= 0x300000 ? data : nil;
            if (completion)
              completion(result, nil);
            return;
          }
          if (completion)
            completion(nil, nil);
        } else if (completion) {
          completion(nil, error);
        }
      }];
  [task resume];
}
- (NSString *)filePathWithMoulID:(id)mouldID {
  NSString *result = nil;
  @try {
    if (![mouldID length]) {
      H5_LOG(0x29e, @"[%s] mould is nil");
      return nil;
    }
    NSString *directory = [self directoryPath];
    if (!directory.length) {
      H5_LOG(0x2aa, @"[%s] dirPath is no exist");
      return nil;
    }
    NSString *path = [NSString stringWithFormat:@"%@/%@.html", directory, mouldID];
    if ([NSFileManager.defaultManager fileExistsAtPath:path])
      result = path;
    else
      H5_LOG(0x2a7, @"[%s] filepath is no exist");
  } @catch (NSException *exception) {
    H5_LOG(0x2ad, @"[bomb][%s] is exception");
  }
  return result;
}
- (NSString *)directoryPath {
  NSString *result = nil;
  @try {
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
    NSString *directory = [NSString stringWithFormat:@"%@/%@", paths.firstObject, @"JPUSHH5"];
    result = directory;
    BOOL isDirectory = NO;
    BOOL exists = [NSFileManager.defaultManager fileExistsAtPath:directory isDirectory:&isDirectory];
    if (exists && isDirectory)
      return directory;
    NSError *error = nil;
    BOOL created = [NSFileManager.defaultManager createDirectoryAtPath:directory
        withIntermediateDirectories:YES attributes:nil error:&error];
    if (error) {
      H5_LOG(0x2be, @"[%s] error is %@", error);
    } else if (created) {
      H5_LOG(0x2c2, @"[%s] createDirectory is success");
      return result;
    } else {
      H5_LOG(0x2c4, @"[%s] createDirectory is failure");
    }
    result = nil;
  } @catch (NSException *exception) {
    H5_LOG(0x2c9, @"[bomb] [%s] is exception");
  }
  return result;
}
- (NSString *)imageTempPath {
  NSString *result = nil;
  @try {
    NSString *directory = [NSString stringWithFormat:@"%@/%@", NSTemporaryDirectory(), @"JPUSHInMessageImage"];
    result = directory;
    BOOL isDirectory = NO;
    BOOL exists = [NSFileManager.defaultManager fileExistsAtPath:directory isDirectory:&isDirectory];
    if (exists && isDirectory)
      return directory;
    NSError *error = nil;
    BOOL created = [NSFileManager.defaultManager createDirectoryAtPath:directory
        withIntermediateDirectories:YES attributes:nil error:&error];
    if (error) {
      H5_LOG(0x2da, @"[%s] error is %@", error);
    } else if (created) {
      H5_LOG(0x2de, @"[%s] createDirectory is success");
      return result;
    } else {
      H5_LOG(0x2e0, @"[%s] createDirectory is failure");
    }
    result = nil;
  } @catch (NSException *exception) {
    H5_LOG(0x2e5, @"[bomb][%s] is exception");
  }
  return result;
}
- (BOOL)removeFilePathWithMouldID:(id)mouldID {
  if (!mouldID) {
    H5_LOG(0x2ed, @"[%s] mouldID is nil");
    return NO;
  }
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x2f1, @"[%s] thread is not JCOREThread");
    return NO;
  }
  BOOL result = NO;
  @try {
    NSString *path = [self filePathWithMoulID:mouldID];
    if (!path.length) {
      H5_LOG(0x2fe, @"[%s] removeFilePath filePath is no isExist");
      return NO;
    }
    result = [NSFileManager.defaultManager removeItemAtPath:path error:NULL];
    if (result)
      H5_LOG(0x2f9, @"[%s] removeFilePath success");
    else
      H5_LOG(0x2fb, @"[%s] removeFilePath failure");
  } @catch (NSException *exception) {
    H5_LOG(0x301, @"[bomb][%s] is exception");
  }
  return result;
}
- (void)removeFileWithpPath:(id)path {
  if (![path isKindOfClass:NSString.class] || ![path length]) {
    H5_LOG(0x308, @"[%s] path is nil");
    return;
  }
  @try {
    if (![NSFileManager.defaultManager fileExistsAtPath:path]) {
      H5_LOG(0x315, @"[%s] path is not isexist");
    } else if ([NSFileManager.defaultManager removeItemAtPath:path error:NULL]) {
      H5_LOG(0x310, @"[%s] removeFilePath success");
    } else {
      H5_LOG(0x312, @"[%s] removeFilePath failure");
    }
  } @catch (NSException *exception) {
    H5_LOG(0x318, @"[bomb][%s] is exception");
  }
}
- (NSMutableArray *)lruArray {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x31e, @"[%s] thread is not JCOREThread");
    return nil;
  }
  NSMutableArray *result = [NSMutableArray array];
  @try {
    id cached = [JCORECacheUtilities cacheObjectForKey:@"JPushLruArray"];
    if ([cached isKindOfClass:NSArray.class])
      [result addObjectsFromArray:cached];
  } @catch (NSException *exception) {
    H5_LOG(0x328, @"[bomb][%s] is exception");
  }
  return result;
}
- (void)cacheLruArray:(id)array {
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x32f, @"[%s] thread is not JCOREThread");
    return;
  }
  if (!array) {
    H5_LOG(0x333, @"[%s] lruArray is nil");
    return;
  }
  @try {
    if ([array isKindOfClass:NSArray.class] && [array count])
      [JCORECacheUtilities setCacheObject:array forKey:@"JPushLruArray"];
    else
      H5_LOG(0x33a, @"[%s] lruArray is bad");
  } @catch (NSException *exception) {
    H5_LOG(0x33d, @"[bomb][%s] is exception");
  }
}
- (void)adjustPositionLRU:(id)mouldID {
  if (![mouldID length]) {
    H5_LOG(0x347, @"[%s] mouldID is nil");
    return;
  }
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x34b, @"[%s] thread is not JCOREThread");
    return;
  }
  @try {
    NSMutableArray *array = [self lruArray];
    NSUInteger index = [array indexOfObject:mouldID];
    if (index == NSNotFound)
      H5_LOG(0x355, @"[%s] mouldID is no exiest");
    else {
      [array removeObjectAtIndex:index];
      [array insertObject:mouldID atIndex:0];
    }
    [self cacheLruArray:array];
  } @catch (NSException *exception) {
    H5_LOG(0x359, @"[bomb][%s] is exception");
  }
}
- (void)addObjectLRU:(id)mouldID {
  if (![mouldID length]) {
    H5_LOG(0x360, @"[%s] mouldID is nil");
    return;
  }
  if (![JCOREThread isMainThread]) {
    H5_LOG(0x364, @"[%s] thread is not JCOREThread");
    return;
  }
  @try {
    NSMutableArray *array = [self lruArray];
    if ([array containsObject:mouldID]) {
      H5_LOG(0x36c, @"[%s] arrayForLRU containt mouldID");
      [self adjustPositionLRU:mouldID];
      return;
    }
    H5_LOG(0x36f, @"[%s] arrayForLRU not containt mouldID");
    [array insertObject:mouldID atIndex:0];
    if (_maxCountLRU && _maxCountLRU < array.count) {
      id last = array.lastObject;
      if ([last isKindOfClass:NSString.class] && [last length]) {
        [self removeFilePathWithMouldID:last];
        [array removeLastObject];
      }
    }
    [self cacheLruArray:array];
  } @catch (NSException *exception) {
    H5_LOG(0x37e, @"[bomb][%s] is exception");
  }
}
@end
#endif
