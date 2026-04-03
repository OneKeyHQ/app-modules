#import "CloudFs.h"
#import <UIKit/UIKit.h>
#import <AssetsLibrary/AssetsLibrary.h>

@implementation CloudFs

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeCloudFsSpecJSI>(params);
}

+ (NSString *)moduleName
{
    return @"RNCloudFs";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

- (dispatch_queue_t)methodQueue
{
    return dispatch_queue_create("com.onekey.CloudFs.queue", DISPATCH_QUEUE_SERIAL);
}

// MARK: - isAvailable

- (void)isAvailable:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
    NSURL *ubiquityURL = [self icloudDirectory];
    if (ubiquityURL != nil) {
        return resolve(@YES);
    }
    return resolve(@NO);
}

// MARK: - createFile

- (void)createFile:(NSDictionary *)options
           resolve:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject
{
    NSString *destinationPath = [options objectForKey:@"targetPath"];
    NSString *content = [options objectForKey:@"content"];
    NSString *scope = [options objectForKey:@"scope"];
    bool documentsFolder = !scope || [scope caseInsensitiveCompare:@"visible"] == NSOrderedSame;

    NSString *tempFile = [NSTemporaryDirectory() stringByAppendingPathComponent:[[NSUUID UUID] UUIDString]];

    NSError *error;
    [content writeToFile:tempFile atomically:YES encoding:NSUTF8StringEncoding error:&error];
    if (error) {
        return reject(@"error", error.description, nil);
    }

    [self moveToICloudDirectory:documentsFolder tempFile:tempFile destinationPath:destinationPath resolve:resolve reject:reject];
}

// MARK: - fileExists

- (void)fileExists:(NSDictionary *)options
           resolve:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject
{
    NSString *destinationPath = [options objectForKey:@"targetPath"];
    NSString *scope = [options objectForKey:@"scope"];
    bool documentsFolder = !scope || [scope caseInsensitiveCompare:@"visible"] == NSOrderedSame;

    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *ubiquityURL = documentsFolder ? [self icloudDocumentsDirectory] : [self icloudDirectory];

    if (ubiquityURL) {
        NSURL *dir = [ubiquityURL URLByAppendingPathComponent:destinationPath];
        NSString *dirPath = [dir.path stringByStandardizingPath];
        bool exists = [fileManager fileExistsAtPath:dirPath];
        return resolve(@(exists));
    } else {
        return reject(@"error", [NSString stringWithFormat:@"could not access iCloud drive '%@'", destinationPath], nil);
    }
}

// MARK: - listFiles

- (void)listFiles:(NSDictionary *)options
          resolve:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    NSString *destinationPath = [options objectForKey:@"targetPath"];
    NSString *scope = [options objectForKey:@"scope"];
    bool documentsFolder = !scope || [scope caseInsensitiveCompare:@"visible"] == NSOrderedSame;

    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd'T'HH:mm:ssZZZ"];

    NSURL *ubiquityURL = documentsFolder ? [self icloudDocumentsDirectory] : [self icloudDirectory];

    if (ubiquityURL) {
        NSURL *target = [ubiquityURL URLByAppendingPathComponent:destinationPath];
        NSMutableArray<NSDictionary *> *fileData = [NSMutableArray new];
        NSError *error = nil;

        BOOL isDirectory;
        [fileManager fileExistsAtPath:[target path] isDirectory:&isDirectory];

        NSURL *dirPath;
        NSArray *contents;
        if (isDirectory) {
            contents = [fileManager contentsOfDirectoryAtPath:[target path] error:&error];
            dirPath = target;
        } else {
            contents = @[[target lastPathComponent]];
            dirPath = [target URLByDeletingLastPathComponent];
        }

        if (error) {
            return reject(@"error", error.description, nil);
        }

        [contents enumerateObjectsUsingBlock:^(id object, NSUInteger idx, BOOL *stop) {
            NSURL *fileUrl = [dirPath URLByAppendingPathComponent:object];
            NSError *attrError;
            NSDictionary *attributes = [fileManager attributesOfItemAtPath:[fileUrl path] error:&attrError];
            if (attrError) {
                return;
            }

            NSFileAttributeType type = [attributes objectForKey:NSFileType];
            bool isDir = type == NSFileTypeDirectory;
            bool isFile = type == NSFileTypeRegular;

            if (!isDir && !isFile) return;

            NSDate *modDate = [attributes objectForKey:NSFileModificationDate];
            NSError *shareError;
            NSURL *shareUrl = [fileManager URLForPublishingUbiquitousItemAtURL:fileUrl expirationDate:nil error:&shareError];

            [fileData addObject:@{
                @"name": object,
                @"path": [fileUrl path],
                @"uri": shareUrl ? [shareUrl absoluteString] : [NSNull null],
                @"size": [attributes objectForKey:NSFileSize],
                @"lastModified": [dateFormatter stringFromDate:modDate],
                @"isDirectory": @(isDir),
                @"isFile": @(isFile)
            }];
        }];

        NSString *relativePath = [[dirPath path] stringByReplacingOccurrencesOfString:[ubiquityURL path] withString:@"."];

        return resolve(@{
            @"files": fileData,
            @"path": relativePath
        });
    } else {
        return reject(@"error", [NSString stringWithFormat:@"could not list iCloud drive '%@'", destinationPath], nil);
    }
}

// MARK: - getIcloudDocument

- (void)getIcloudDocument:(NSString *)filename
                  resolve:(RCTPromiseResolveBlock)resolve
                   reject:(RCTPromiseRejectBlock)reject
{
    __block bool resolved = NO;
    _query = [[NSMetadataQuery alloc] init];
    [_query setSearchScopes:@[NSMetadataQueryUbiquitousDocumentsScope, NSMetadataQueryUbiquitousDataScope]];

    NSPredicate *pred = [NSPredicate predicateWithFormat:@"%K == %@", NSMetadataItemFSNameKey, filename];
    [_query setPredicate:pred];

    [[NSNotificationCenter defaultCenter] addObserverForName:NSMetadataQueryDidFinishGatheringNotification
                                                      object:_query
                                                       queue:[NSOperationQueue currentQueue]
                                                  usingBlock:^(NSNotification __strong *notification) {
        NSMetadataQuery *query = [notification object];
        [query disableUpdates];
        [query stopQuery];
        for (NSMetadataItem *item in query.results) {
            if ([[item valueForAttribute:NSMetadataItemFSNameKey] isEqualToString:filename]) {
                resolved = YES;
                NSURL *url = [item valueForAttribute:NSMetadataItemURLKey];
                bool fileIsReady = [self downloadFileIfNotAvailable:item];
                if (fileIsReady) {
                    NSData *data = [NSData dataWithContentsOfURL:url];
                    NSString *content = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                    return resolve(content);
                } else {
                    [self getIcloudDocument:filename resolve:resolve reject:reject];
                }
            }
        }
        if (!resolved) {
            return reject(@"error", [NSString stringWithFormat:@"item not found '%@'", filename], nil);
        }
    }];

    dispatch_async(dispatch_get_main_queue(), ^{
        [self->_query startQuery];
    });
}

// MARK: - deleteFromCloud

- (void)deleteFromCloud:(NSDictionary *)item
                resolve:(RCTPromiseResolveBlock)resolve
                 reject:(RCTPromiseRejectBlock)reject
{
    NSError *error;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    [fileManager removeItemAtPath:item[@"path"] error:&error];
    if (error) {
        return reject(@"error", error.description, nil);
    }
    return resolve(@YES);
}

// MARK: - copyToCloud

- (void)copyToCloud:(NSDictionary *)options
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
    NSDictionary *source = [options objectForKey:@"sourcePath"];
    NSString *destinationPath = [options objectForKey:@"targetPath"];
    NSString *scope = [options objectForKey:@"scope"];
    bool documentsFolder = !scope || [scope caseInsensitiveCompare:@"visible"] == NSOrderedSame;

    NSFileManager *fileManager = [NSFileManager defaultManager];

    NSString *sourceUri = [source objectForKey:@"uri"];
    if (!sourceUri) {
        sourceUri = [source objectForKey:@"path"];
    }

    if ([sourceUri hasPrefix:@"assets-library"]) {
        ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
        [library assetForURL:[NSURL URLWithString:sourceUri] resultBlock:^(ALAsset *asset) {
            ALAssetRepresentation *rep = [asset defaultRepresentation];
            Byte *buffer = (Byte *)malloc(rep.size);
            NSUInteger buffered = [rep getBytes:buffer fromOffset:0.0 length:rep.size error:nil];
            NSData *data = [NSData dataWithBytesNoCopy:buffer length:buffered freeWhenDone:YES];
            if (data) {
                NSString *filename = [sourceUri lastPathComponent];
                NSString *tempFile = [NSTemporaryDirectory() stringByAppendingPathComponent:filename];
                [data writeToFile:tempFile atomically:YES];
                [self moveToICloudDirectory:documentsFolder tempFile:tempFile destinationPath:destinationPath resolve:resolve reject:reject];
            } else {
                return reject(@"error", [NSString stringWithFormat:@"failed to copy asset '%@'", sourceUri], nil);
            }
        } failureBlock:^(NSError *error) {
            return reject(@"error", error.description, nil);
        }];
    } else if ([sourceUri hasPrefix:@"file:/"] || [sourceUri hasPrefix:@"/"]) {
        NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:@"^file:/+" options:NSRegularExpressionCaseInsensitive error:nil];
        NSString *modifiedSourceUri = [regex stringByReplacingMatchesInString:sourceUri options:0 range:NSMakeRange(0, [sourceUri length]) withTemplate:@"/"];

        if ([fileManager fileExistsAtPath:modifiedSourceUri isDirectory:nil]) {
            NSURL *sourceURL = [NSURL fileURLWithPath:modifiedSourceUri];
            NSString *filename = [sourceUri lastPathComponent];
            NSString *tempFile = [NSTemporaryDirectory() stringByAppendingPathComponent:filename];

            NSError *error;
            if ([fileManager fileExistsAtPath:tempFile]) {
                [fileManager removeItemAtPath:tempFile error:&error];
                if (error) {
                    return reject(@"error", error.description, nil);
                }
            }

            [fileManager copyItemAtPath:[sourceURL path] toPath:tempFile error:&error];
            if (error) {
                return reject(@"error", error.description, nil);
            }

            [self moveToICloudDirectory:documentsFolder tempFile:tempFile destinationPath:destinationPath resolve:resolve reject:reject];
        } else {
            return reject(@"error", [NSString stringWithFormat:@"no such file or directory, open '%@'", sourceUri], nil);
        }
    } else {
        NSURL *url = [NSURL URLWithString:sourceUri];
        NSData *urlData = [NSData dataWithContentsOfURL:url];
        if (urlData) {
            NSString *filename = [sourceUri lastPathComponent];
            NSString *tempFile = [NSTemporaryDirectory() stringByAppendingPathComponent:filename];
            [urlData writeToFile:tempFile atomically:YES];
            [self moveToICloudDirectory:documentsFolder tempFile:tempFile destinationPath:destinationPath resolve:resolve reject:reject];
        } else {
            return reject(@"error", [NSString stringWithFormat:@"cannot download '%@'", sourceUri], nil);
        }
    }
}

// MARK: - syncCloud

- (void)syncCloud:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    _query = [[NSMetadataQuery alloc] init];
    [_query setSearchScopes:@[NSMetadataQueryUbiquitousDocumentsScope, NSMetadataQueryUbiquitousDataScope]];
    [_query setPredicate:[NSPredicate predicateWithFormat:@"%K LIKE '*'", NSMetadataItemFSNameKey]];

    dispatch_async(dispatch_get_main_queue(), ^{
        BOOL startedQuery = [self->_query startQuery];
        if (!startedQuery) {
            reject(@"error", @"Failed to start query.\n", nil);
        }
    });

    [[NSNotificationCenter defaultCenter] addObserverForName:NSMetadataQueryDidFinishGatheringNotification
                                                      object:_query
                                                       queue:[NSOperationQueue currentQueue]
                                                  usingBlock:^(NSNotification __strong *notification) {
        NSMetadataQuery *query = [notification object];
        [query disableUpdates];
        [query stopQuery];
        for (NSMetadataItem *item in query.results) {
            [self downloadFileIfNotAvailable:item];
        }
        return resolve(nil);
    }];
}

// MARK: - Private helpers

- (void)moveToICloudDirectory:(bool)documentsFolder
                     tempFile:(NSString *)tempFile
              destinationPath:(NSString *)destinationPath
                      resolve:(RCTPromiseResolveBlock)resolve
                       reject:(RCTPromiseRejectBlock)reject
{
    NSURL *ubiquityURL = documentsFolder ? [self icloudDocumentsDirectory] : [self icloudDirectory];
    [self moveToICloud:ubiquityURL tempFile:tempFile destinationPath:destinationPath resolve:resolve reject:reject];
}

- (void)moveToICloud:(NSURL *)ubiquityURL
            tempFile:(NSString *)tempFile
     destinationPath:(NSString *)destinationPath
             resolve:(RCTPromiseResolveBlock)resolve
              reject:(RCTPromiseRejectBlock)reject
{
    NSString *destPath = destinationPath;
    while ([destPath hasPrefix:@"/"]) {
        destPath = [destPath substringFromIndex:1];
    }

    NSFileManager *fileManager = [NSFileManager defaultManager];

    if (ubiquityURL) {
        NSURL *targetFile = [ubiquityURL URLByAppendingPathComponent:destPath];
        NSURL *dir = [targetFile URLByDeletingLastPathComponent];
        NSURL *uniqueFile = targetFile;

        if ([fileManager fileExistsAtPath:uniqueFile.path]) {
            NSError *error;
            [fileManager removeItemAtPath:uniqueFile.path error:&error];
            if (error) {
                return reject(@"error", error.description, nil);
            }
        }

        if (![fileManager fileExistsAtPath:dir.path]) {
            [fileManager createDirectoryAtURL:dir withIntermediateDirectories:YES attributes:nil error:nil];
        }

        NSError *error;
        [fileManager setUbiquitous:YES itemAtURL:[NSURL fileURLWithPath:tempFile] destinationURL:uniqueFile error:&error];
        if (error) {
            return reject(@"error", error.description, nil);
        }

        [fileManager removeItemAtPath:tempFile error:&error];
        return resolve(uniqueFile.path);
    } else {
        NSError *error;
        [fileManager removeItemAtPath:tempFile error:&error];
        return reject(@"error", [NSString stringWithFormat:@"could not copy '%@' to iCloud drive", tempFile], nil);
    }
}

- (NSURL *)icloudDocumentsDirectory
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *rootDirectory = [[self icloudDirectory] URLByAppendingPathComponent:@"Documents"];
    if (rootDirectory) {
        if (![fileManager fileExistsAtPath:rootDirectory.path isDirectory:nil]) {
            [fileManager createDirectoryAtURL:rootDirectory withIntermediateDirectories:YES attributes:nil error:nil];
        }
    }
    return rootDirectory;
}

- (NSURL *)icloudDirectory
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSURL *rootDirectory = [fileManager URLForUbiquityContainerIdentifier:nil];
    return rootDirectory;
}

- (BOOL)downloadFileIfNotAvailable:(NSMetadataItem *)item
{
    if ([[item valueForAttribute:NSMetadataUbiquitousItemDownloadingStatusKey] isEqualToString:NSMetadataUbiquitousItemDownloadingStatusCurrent]) {
        return YES;
    }
    NSFileManager *fm = [NSFileManager defaultManager];
    NSError *downloadError = nil;
    [fm startDownloadingUbiquitousItemAtURL:[item valueForAttribute:NSMetadataItemURLKey] error:&downloadError];
    [NSThread sleepForTimeInterval:0.3];
    return NO;
}

@end
