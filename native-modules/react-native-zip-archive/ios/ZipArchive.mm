#import "ZipArchive.h"

@implementation ZipArchive
{
    bool hasListeners;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeZipArchiveSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNZipArchive";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_queue_create("com.onekey.ZipArchiveQueue", DISPATCH_QUEUE_SERIAL);
}

// MARK: - isPasswordProtected

- (void)isPasswordProtected:(NSString *)file
                    resolve:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
    BOOL isPasswordProtected = [SSZipArchive isFilePasswordProtectedAtPath:file];
    resolve([NSNumber numberWithBool:isPasswordProtected]);
}

// MARK: - unzip

- (void)unzip:(NSString *)from
           to:(NSString *)to
      resolve:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject
{
    self.progress = 0.0;
    self.processedFilePath = @"";

    NSError *error = nil;
    BOOL success = [SSZipArchive unzipFileAtPath:from
                                  toDestination:to
                             preserveAttributes:NO
                                      overwrite:YES
                                       password:nil
                                          error:&error
                                       delegate:self];

    self.progress = 1.0;

    if (success) {
        resolve(to);
    } else {
        reject(@"unzip_error", [error localizedDescription], error);
    }
}

// MARK: - unzipWithPassword

- (void)unzipWithPassword:(NSString *)from
                       to:(NSString *)to
                 password:(NSString *)password
                  resolve:(RCTPromiseResolveBlock)resolve
                   reject:(RCTPromiseRejectBlock)reject
{
    self.progress = 0.0;
    self.processedFilePath = @"";

    NSError *error = nil;
    BOOL success = [SSZipArchive unzipFileAtPath:from
                                  toDestination:to
                             preserveAttributes:NO
                                      overwrite:YES
                                       password:password
                                          error:&error
                                       delegate:self];

    self.progress = 1.0;

    if (success) {
        resolve(to);
    } else {
        reject(@"unzip_error", @"unable to unzip", error);
    }
}

// MARK: - zipFolder

- (void)zipFolder:(NSString *)from
               to:(NSString *)to
          resolve:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    self.progress = 0.0;
    self.processedFilePath = @"";
    [self setProgressHandler];

    BOOL success = [SSZipArchive createZipFileAtPath:to
                             withContentsOfDirectory:from
                                 keepParentDirectory:NO
                                       withPassword:nil
                                  andProgressHandler:self.progressHandler];

    self.progress = 1.0;

    if (success) {
        resolve(to);
    } else {
        reject(@"zip_error", @"unable to zip", nil);
    }
}

// MARK: - zipFiles

- (void)zipFiles:(NSArray *)files
              to:(NSString *)to
         resolve:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject
{
    self.progress = 0.0;
    self.processedFilePath = @"";
    [self setProgressHandler];

    BOOL success = [SSZipArchive createZipFileAtPath:to withFilesAtPaths:files];

    self.progress = 1.0;

    if (success) {
        resolve(to);
    } else {
        reject(@"zip_error", @"unable to zip", nil);
    }
}

// MARK: - getUncompressedSize

- (void)getUncompressedSize:(NSString *)path
                    resolve:(RCTPromiseResolveBlock)resolve
                     reject:(RCTPromiseRejectBlock)reject
{
    NSError *error = nil;
    NSNumber *wantedFileSize = [SSZipArchive payloadSizeForArchiveAtPath:path error:&error];

    if (error == nil) {
        resolve(wantedFileSize);
    } else {
        resolve(@-1);
    }
}

// MARK: - SSZipArchiveDelegate

- (void)zipArchiveDidUnzipFileAtIndex:(NSInteger)fileIndex
                           totalFiles:(NSInteger)totalFiles
                          archivePath:(NSString *)archivePath
                      unzippedFilePath:(NSString *)processedFilePath
{
    self.processedFilePath = processedFilePath;
}

// MARK: - Progress helper

- (void)setProgressHandler
{
    __weak ZipArchive *weakSelf = self;
    self.progressHandler = ^(NSUInteger entryNumber, NSUInteger total) {
        if (total > 0) {
            weakSelf.progress = (float)entryNumber / (float)total;
        }
    };
}

@end
