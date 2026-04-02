/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Adapted to a proper TurboModule (no RCT_EXPORT_MODULE / RCT_EXPORT_METHOD)
 * so it works in both the main and background Hermes runtimes.
 */

#import "AsyncStorage.h"

#import <React/RCTConvert.h>
#import <React/RCTLog.h>
#import <React/RCTUtils.h>

static NSString *const RCTStorageDirectory = @"RCTAsyncLocalStorage_V1";
static NSString *const RCTOldStorageDirectory = @"RNCAsyncLocalStorage_V1";
static NSString *const RCTExpoStorageDirectory = @"RCTAsyncLocalStorage";
static NSString *const RCTManifestFileName = @"manifest.json";
static const NSUInteger RCTInlineValueThreshold = 1024;

#pragma mark - Static helper functions

static NSDictionary *RCTErrorForKey(NSString *key)
{
    if (![key isKindOfClass:[NSString class]]) {
        return RCTMakeAndLogError(@"Invalid key - must be a string.  Key: ", key, @{@"key": key});
    } else if (key.length < 1) {
        return RCTMakeAndLogError(
            @"Invalid key - must be at least one character.  Key: ", key, @{@"key": key});
    } else {
        return nil;
    }
}

static BOOL RCTAsyncStorageSetExcludedFromBackup(NSString *path, NSNumber *isExcluded)
{
    NSFileManager *fileManager = [[NSFileManager alloc] init];

    BOOL isDir;
    BOOL exists = [fileManager fileExistsAtPath:path isDirectory:&isDir];
    BOOL success = false;

    if (isDir && exists) {
        NSURL *pathUrl = [NSURL fileURLWithPath:path];
        NSError *error = nil;
        success = [pathUrl setResourceValue:isExcluded
                                     forKey:NSURLIsExcludedFromBackupKey
                                      error:&error];

        if (!success) {
            NSLog(@"Could not exclude AsyncStorage dir from backup %@", error);
        }
    }
    return success;
}

static void RCTAppendError(NSDictionary *error, NSMutableArray<NSDictionary *> **errors)
{
    if (error && errors) {
        if (!*errors) {
            *errors = [NSMutableArray new];
        }
        [*errors addObject:error];
    }
}

static NSString *RCTReadFile(NSString *filePath, NSString *key, NSDictionary **errorOut)
{
    if ([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
        NSError *error;
        NSStringEncoding encoding;
        NSString *entryString = [NSString stringWithContentsOfFile:filePath
                                                      usedEncoding:&encoding
                                                             error:&error];
        NSDictionary *extraData = @{@"key": RCTNullIfNil(key)};

        if (error) {
            if (errorOut) {
                *errorOut = RCTMakeError(@"Failed to read storage file.", error, extraData);
            }
            return nil;
        }

        if (encoding != NSUTF8StringEncoding) {
            if (errorOut) {
                *errorOut =
                    RCTMakeError(@"Incorrect encoding of storage file: ", @(encoding), extraData);
            }
            return nil;
        }
        return entryString;
    }

    return nil;
}

static NSString *RCTCreateStorageDirectoryPath_deprecated(NSString *storageDir)
{
    NSString *storageDirectoryPath;
#if TARGET_OS_TV
    storageDirectoryPath =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).firstObject;
#else
    storageDirectoryPath =
        NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES).firstObject;
#endif
    storageDirectoryPath = [storageDirectoryPath stringByAppendingPathComponent:storageDir];
    return storageDirectoryPath;
}

static NSString *RCTCreateStorageDirectoryPath(NSString *storageDir)
{
    NSString *storageDirectoryPath = @"";

#if TARGET_OS_TV
    storageDirectoryPath =
        NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES).firstObject;
#else
    storageDirectoryPath =
        NSSearchPathForDirectoriesInDomains(NSApplicationSupportDirectory, NSUserDomainMask, YES)
            .firstObject;
    storageDirectoryPath = [storageDirectoryPath
        stringByAppendingPathComponent:[[NSBundle mainBundle] bundleIdentifier]];
#endif

    storageDirectoryPath = [storageDirectoryPath stringByAppendingPathComponent:storageDir];

    return storageDirectoryPath;
}

static NSString *RCTGetStorageDirectory()
{
    static NSString *storageDirectory = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
#if TARGET_OS_TV
      RCTLogWarn(
          @"Persistent storage is not supported on tvOS, your data may be removed at any point.");
#endif
      storageDirectory = RCTCreateStorageDirectoryPath(RCTStorageDirectory);
    });
    return storageDirectory;
}

static NSString *RCTCreateManifestFilePath(NSString *storageDirectory)
{
    return [storageDirectory stringByAppendingPathComponent:RCTManifestFileName];
}

static NSString *RCTGetManifestFilePath()
{
    static NSString *manifestFilePath = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      manifestFilePath = RCTCreateManifestFilePath(RCTStorageDirectory);
    });
    return manifestFilePath;
}

// Only merges objects - all other types are just clobbered (including arrays)
static BOOL RCTMergeRecursive(NSMutableDictionary *destination, NSDictionary *source)
{
    BOOL modified = NO;
    for (NSString *key in source) {
        id sourceValue = source[key];
        id destinationValue = destination[key];
        if ([sourceValue isKindOfClass:[NSDictionary class]]) {
            if ([destinationValue isKindOfClass:[NSDictionary class]]) {
                if ([destinationValue classForCoder] != [NSMutableDictionary class]) {
                    destinationValue = [destinationValue mutableCopy];
                }
                if (RCTMergeRecursive(destinationValue, sourceValue)) {
                    destination[key] = destinationValue;
                    modified = YES;
                }
            } else {
                destination[key] = [sourceValue copy];
                modified = YES;
            }
        } else if (![source isEqual:destinationValue]) {
            destination[key] = [sourceValue copy];
            modified = YES;
        }
    }
    return modified;
}

static dispatch_queue_t RCTGetMethodQueue()
{
    // We want all instances to share the same queue since they will be reading/writing the same
    // files.
    static dispatch_queue_t queue;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      queue =
          dispatch_queue_create("com.facebook.react.AsyncLocalStorageQueue", DISPATCH_QUEUE_SERIAL);
    });
    return queue;
}

static NSCache *RCTGetCache()
{
    // We want all instances to share the same cache since they will be reading/writing the same
    // files.
    static NSCache *cache;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
      cache = [NSCache new];
      cache.totalCostLimit = 2 * 1024 * 1024;  // 2MB

#if !TARGET_OS_OSX
      // Clear cache in the event of a memory warning
      [[NSNotificationCenter defaultCenter]
          addObserverForName:UIApplicationDidReceiveMemoryWarningNotification
                      object:nil
                       queue:nil
                  usingBlock:^(__unused NSNotification *note) {
                    [cache removeAllObjects];
                  }];
#endif  // !TARGET_OS_OSX
    });
    return cache;
}

static BOOL RCTHasCreatedStorageDirectory = NO;

static NSDictionary *RCTDeleteStorageDirectory()
{
    NSError *error;
    [[NSFileManager defaultManager] removeItemAtPath:RCTGetStorageDirectory() error:&error];
    RCTHasCreatedStorageDirectory = NO;
    if (error && error.code != NSFileNoSuchFileError) {
        return RCTMakeError(@"Failed to delete storage directory.", error, nil);
    }
    return nil;
}

static NSDate *RCTManifestModificationDate(NSString *manifestFilePath)
{
    NSDictionary *attributes =
        [[NSFileManager defaultManager] attributesOfItemAtPath:manifestFilePath error:nil];
    return [attributes fileModificationDate];
}

static void RCTStorageDirectoryMigrationLogError(NSString *reason, NSError *error)
{
    RCTLogWarn(@"%@: %@", reason, error ? error.description : @"");
}

static void RCTStorageDirectoryCleanupOld(NSString *oldDirectoryPath)
{
    NSError *error;
    if (![[NSFileManager defaultManager] removeItemAtPath:oldDirectoryPath error:&error]) {
        RCTStorageDirectoryMigrationLogError(
            @"Failed to remove old storage directory during migration", error);
    }
}

static void _createStorageDirectory(NSString *storageDirectory, NSError **error)
{
    [[NSFileManager defaultManager] createDirectoryAtPath:storageDirectory
                              withIntermediateDirectories:YES
                                               attributes:nil
                                                    error:error];
}

static void RCTStorageDirectoryMigrate(NSString *oldDirectoryPath,
                                       NSString *newDirectoryPath,
                                       BOOL shouldCleanupOldDirectory)
{
    NSError *error;
    if (![[NSFileManager defaultManager] copyItemAtPath:oldDirectoryPath
                                                 toPath:newDirectoryPath
                                                  error:&error]) {
        if (error != nil && error.code == 4 &&
            [newDirectoryPath isEqualToString:RCTGetStorageDirectory()]) {
            error = nil;
            _createStorageDirectory(RCTCreateStorageDirectoryPath(@""), &error);
            if (error == nil) {
                RCTStorageDirectoryMigrate(
                    oldDirectoryPath, newDirectoryPath, shouldCleanupOldDirectory);
            } else {
                RCTStorageDirectoryMigrationLogError(
                    @"Failed to create storage directory during migration.", error);
            }
        } else {
            RCTStorageDirectoryMigrationLogError(
                @"Failed to copy old storage directory to new storage directory location during "
                @"migration",
                error);
        }
    } else if (shouldCleanupOldDirectory) {
        RCTStorageDirectoryCleanupOld(oldDirectoryPath);
    }
}

static NSString *RCTGetStoragePathForMigration()
{
    BOOL isDir;
    NSString *oldStoragePath = RCTCreateStorageDirectoryPath_deprecated(RCTOldStorageDirectory);
    NSString *expoStoragePath = RCTCreateStorageDirectoryPath_deprecated(RCTExpoStorageDirectory);
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL oldStorageDirectoryExists =
        [fileManager fileExistsAtPath:oldStoragePath isDirectory:&isDir] && isDir;
    BOOL expoStorageDirectoryExists =
        [fileManager fileExistsAtPath:expoStoragePath isDirectory:&isDir] && isDir;

    if (oldStorageDirectoryExists && expoStorageDirectoryExists) {
        if ([RCTManifestModificationDate(RCTCreateManifestFilePath(oldStoragePath))
                compare:RCTManifestModificationDate(RCTCreateManifestFilePath(expoStoragePath))] ==
            NSOrderedDescending) {
            RCTStorageDirectoryCleanupOld(expoStoragePath);
            return oldStoragePath;
        } else {
            RCTStorageDirectoryCleanupOld(oldStoragePath);
            return expoStoragePath;
        }
    } else if (oldStorageDirectoryExists) {
        return oldStoragePath;
    } else if (expoStorageDirectoryExists) {
        return expoStoragePath;
    } else {
        return nil;
    }
}

static void RCTStorageDirectoryMigrationCheck(NSString *fromStorageDirectory,
                                              NSString *toStorageDirectory,
                                              BOOL shouldCleanupOldDirectoryAndOverwriteNewDirectory)
{
    NSError *error;
    BOOL isDir;
    NSFileManager *fileManager = [NSFileManager defaultManager];
    if ([fileManager fileExistsAtPath:fromStorageDirectory isDirectory:&isDir] && isDir) {
        if ([fileManager fileExistsAtPath:toStorageDirectory]) {
            if ([RCTManifestModificationDate(RCTCreateManifestFilePath(toStorageDirectory))
                    compare:RCTManifestModificationDate(
                                RCTCreateManifestFilePath(fromStorageDirectory))] == 1) {
                if (shouldCleanupOldDirectoryAndOverwriteNewDirectory) {
                    RCTStorageDirectoryCleanupOld(fromStorageDirectory);
                }
            } else if (shouldCleanupOldDirectoryAndOverwriteNewDirectory) {
                if (![fileManager removeItemAtPath:toStorageDirectory error:&error]) {
                    RCTStorageDirectoryMigrationLogError(
                        @"Failed to remove new storage directory during migration", error);
                } else {
                    RCTStorageDirectoryMigrate(fromStorageDirectory,
                                               toStorageDirectory,
                                               shouldCleanupOldDirectoryAndOverwriteNewDirectory);
                }
            }
        } else {
            RCTStorageDirectoryMigrate(fromStorageDirectory,
                                       toStorageDirectory,
                                       shouldCleanupOldDirectoryAndOverwriteNewDirectory);
        }
    }
}

#pragma mark - AsyncStorage

@implementation AsyncStorage {
    BOOL _haveSetup;
    // The manifest is a dictionary of all keys with small values inlined.  Null values indicate
    // values that are stored in separate files (as opposed to nil values which don't exist).  The
    // manifest is read off disk at startup, and written to disk after all mutations.
    NSMutableDictionary<NSString *, NSString *> *_manifest;
}

+ (NSString *)moduleName
{
    return @"RNCAsyncStorage";
}

+ (BOOL)requiresMainQueueSetup
{
    return NO;
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRNCAsyncStorageSpecJSI>(params);
}

- (instancetype)init
{
    if (!(self = [super init])) {
        return nil;
    }

    NSString *oldStoragePath = RCTGetStoragePathForMigration();
    if (oldStoragePath != nil) {
        RCTStorageDirectoryMigrationCheck(
            oldStoragePath, RCTCreateStorageDirectoryPath_deprecated(RCTStorageDirectory), YES);
    }

    RCTStorageDirectoryMigrationCheck(RCTCreateStorageDirectoryPath_deprecated(RCTStorageDirectory),
                                      RCTCreateStorageDirectoryPath(RCTStorageDirectory),
                                      NO);

    return self;
}

- (NSString *)_filePathForKey:(NSString *)key
{
    NSString *safeFileName = RCTMD5Hash(key);
    return [RCTGetStorageDirectory() stringByAppendingPathComponent:safeFileName];
}

- (NSDictionary *)_ensureSetup
{
    NSError *error = nil;
    if (!RCTHasCreatedStorageDirectory) {
        _createStorageDirectory(RCTGetStorageDirectory(), &error);
        if (error) {
            return RCTMakeError(@"Failed to create storage directory.", error, nil);
        }
        RCTHasCreatedStorageDirectory = YES;
    }

    if (!_haveSetup) {
        NSNumber *isExcludedFromBackup =
            [[NSBundle mainBundle] objectForInfoDictionaryKey:@"RCTAsyncStorageExcludeFromBackup"];
        if (isExcludedFromBackup == nil) {
            isExcludedFromBackup = @YES;
        }
        RCTAsyncStorageSetExcludedFromBackup(RCTCreateStorageDirectoryPath(RCTStorageDirectory),
                                             isExcludedFromBackup);

        NSDictionary *errorOut = nil;
        NSString *serialized = RCTReadFile(RCTCreateStorageDirectoryPath(RCTGetManifestFilePath()),
                                           RCTManifestFileName,
                                           &errorOut);
        if (!serialized) {
            if (errorOut) {
                RCTLogError(
                    @"Could not open the existing manifest, perhaps data protection is "
                    @"enabled?\n\n%@",
                    errorOut);
                return errorOut;
            } else {
                _manifest = [NSMutableDictionary new];
            }
        } else {
            _manifest = RCTJSONParseMutable(serialized, &error);
            if (!_manifest) {
                RCTLogError(@"Failed to parse manifest - creating a new one.\n\n%@", error);
                _manifest = [NSMutableDictionary new];
            }
        }
        _haveSetup = YES;
    }

    return nil;
}

- (NSDictionary *)_writeManifest:(NSMutableArray<NSDictionary *> *__autoreleasing *)errors
{
    NSError *error;
    NSString *serialized = RCTJSONStringify(_manifest, &error);
    [serialized writeToFile:RCTCreateStorageDirectoryPath(RCTGetManifestFilePath())
                 atomically:YES
                   encoding:NSUTF8StringEncoding
                      error:&error];
    NSDictionary *errorOut;
    if (error) {
        errorOut = RCTMakeError(@"Failed to write manifest file.", error, nil);
        RCTAppendError(errorOut, errors);
    }
    return errorOut;
}

- (NSString *)_getValueForKey:(NSString *)key errorOut:(NSDictionary *__autoreleasing *)errorOut
{
    NSString *value = _manifest[key];
    if (value == (id)kCFNull) {
        value = [RCTGetCache() objectForKey:key];
        if (!value) {
            NSString *filePath = [self _filePathForKey:key];
            value = RCTReadFile(filePath, key, errorOut);
            if (value) {
                [RCTGetCache() setObject:value forKey:key cost:value.length];
            } else {
                [_manifest removeObjectForKey:key];
            }
        }
    }
    return value;
}

- (NSDictionary *)_writeEntry:(NSArray<NSString *> *)entry changedManifest:(BOOL *)changedManifest
{
    if (entry.count != 2) {
        return RCTMakeAndLogError(
            @"Entries must be arrays of the form [key: string, value: string], got: ", entry, nil);
    }
    NSString *key = entry[0];
    NSDictionary *errorOut = RCTErrorForKey(key);
    if (errorOut) {
        return errorOut;
    }
    NSString *value = entry[1];
    NSString *filePath = [self _filePathForKey:key];
    NSError *error;
    if (value.length <= RCTInlineValueThreshold) {
        if (_manifest[key] == (id)kCFNull) {
            [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
            [RCTGetCache() removeObjectForKey:key];
        }
        *changedManifest = YES;
        _manifest[key] = value;
        return nil;
    }
    [value writeToFile:filePath atomically:YES encoding:NSUTF8StringEncoding error:&error];
    [RCTGetCache() setObject:value forKey:key cost:value.length];
    if (error) {
        errorOut = RCTMakeError(@"Failed to write value.", error, @{@"key": key});
    } else if (_manifest[key] != (id)kCFNull) {
        *changedManifest = YES;
        _manifest[key] = (id)kCFNull;
    }
    return errorOut;
}

#pragma mark - TurboModule methods

- (void)multiGet:(NSArray<NSString *> *)keys
         resolve:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        NSDictionary *ensureSetupError = [self _ensureSetup];
        if (ensureSetupError) {
            reject(@"ASYNC_STORAGE_ERROR",
                   ensureSetupError[@"message"] ?: @"Storage setup failed",
                   nil);
            return;
        }

        NSMutableArray<NSArray *> *result = [NSMutableArray arrayWithCapacity:keys.count];
        for (NSString *key in keys) {
            NSDictionary *keyError = RCTErrorForKey(key);
            if (keyError) {
                [result addObject:@[RCTNullIfNil(key), (id)kCFNull]];
            } else {
                NSDictionary *errorOut = nil;
                NSString *value = [self _getValueForKey:key errorOut:&errorOut];
                [result addObject:@[key, RCTNullIfNil(value)]];
            }
        }
        resolve(result);
    });
}

- (void)multiSet:(NSArray<NSArray<NSString *> *> *)keyValuePairs
         resolve:(RCTPromiseResolveBlock)resolve
          reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        NSDictionary *ensureSetupError = [self _ensureSetup];
        if (ensureSetupError) {
            reject(@"ASYNC_STORAGE_ERROR",
                   ensureSetupError[@"message"] ?: @"Storage setup failed",
                   nil);
            return;
        }

        BOOL changedManifest = NO;
        NSMutableArray<NSDictionary *> *errors;
        for (NSArray<NSString *> *entry in keyValuePairs) {
            NSDictionary *keyError = [self _writeEntry:entry changedManifest:&changedManifest];
            RCTAppendError(keyError, &errors);
        }
        if (changedManifest) {
            [self _writeManifest:&errors];
        }
        if (errors.count > 0) {
            reject(@"ASYNC_STORAGE_ERROR",
                   @"One or more keys failed to set",
                   nil);
        } else {
            resolve(nil);
        }
    });
}

- (void)multiRemove:(NSArray<NSString *> *)keys
            resolve:(RCTPromiseResolveBlock)resolve
             reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        NSDictionary *ensureSetupError = [self _ensureSetup];
        if (ensureSetupError) {
            reject(@"ASYNC_STORAGE_ERROR",
                   ensureSetupError[@"message"] ?: @"Storage setup failed",
                   nil);
            return;
        }

        NSMutableArray<NSDictionary *> *errors;
        BOOL changedManifest = NO;
        for (NSString *key in keys) {
            NSDictionary *keyError = RCTErrorForKey(key);
            if (!keyError) {
                if (self->_manifest[key] == (id)kCFNull) {
                    NSString *filePath = [self _filePathForKey:key];
                    [[NSFileManager defaultManager] removeItemAtPath:filePath error:nil];
                    [RCTGetCache() removeObjectForKey:key];
                }
                if (self->_manifest[key]) {
                    changedManifest = YES;
                    [self->_manifest removeObjectForKey:key];
                }
            }
            RCTAppendError(keyError, &errors);
        }
        if (changedManifest) {
            [self _writeManifest:&errors];
        }
        if (errors.count > 0) {
            reject(@"ASYNC_STORAGE_ERROR",
                   @"One or more keys failed to remove",
                   nil);
        } else {
            resolve(nil);
        }
    });
}

- (void)multiMerge:(NSArray<NSArray<NSString *> *> *)keyValuePairs
           resolve:(RCTPromiseResolveBlock)resolve
            reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        NSDictionary *ensureSetupError = [self _ensureSetup];
        if (ensureSetupError) {
            reject(@"ASYNC_STORAGE_ERROR",
                   ensureSetupError[@"message"] ?: @"Storage setup failed",
                   nil);
            return;
        }

        BOOL changedManifest = NO;
        NSMutableArray<NSDictionary *> *errors;
        for (__strong NSArray<NSString *> *entry in keyValuePairs) {
            NSDictionary *keyError;
            NSString *value = [self _getValueForKey:entry[0] errorOut:&keyError];
            if (!keyError) {
                if (value) {
                    NSError *jsonError;
                    NSMutableDictionary *mergedVal = RCTJSONParseMutable(value, &jsonError);
                    NSDictionary *mergingValue = RCTJSONParse(entry[1], &jsonError);
                    if (!mergingValue.count || RCTMergeRecursive(mergedVal, mergingValue)) {
                        entry = @[entry[0], RCTNullIfNil(RCTJSONStringify(mergedVal, NULL))];
                    }
                    if (jsonError) {
                        keyError = RCTJSErrorFromNSError(jsonError);
                    }
                }
                if (!keyError) {
                    keyError = [self _writeEntry:entry changedManifest:&changedManifest];
                }
            }
            RCTAppendError(keyError, &errors);
        }
        if (changedManifest) {
            [self _writeManifest:&errors];
        }
        if (errors.count > 0) {
            reject(@"ASYNC_STORAGE_ERROR",
                   @"One or more keys failed to merge",
                   nil);
        } else {
            resolve(nil);
        }
    });
}

- (void)getAllKeys:(RCTPromiseResolveBlock)resolve
           reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        NSDictionary *ensureSetupError = [self _ensureSetup];
        if (ensureSetupError) {
            reject(@"ASYNC_STORAGE_ERROR",
                   ensureSetupError[@"message"] ?: @"Storage setup failed",
                   nil);
            return;
        }
        resolve(self->_manifest.allKeys);
    });
}

- (void)clear:(RCTPromiseResolveBlock)resolve
       reject:(RCTPromiseRejectBlock)reject
{
    dispatch_async(RCTGetMethodQueue(), ^{
        [self->_manifest removeAllObjects];
        [RCTGetCache() removeAllObjects];
        NSDictionary *error = RCTDeleteStorageDirectory();
        if (error) {
            reject(@"ASYNC_STORAGE_ERROR",
                   error[@"message"] ?: @"Failed to clear storage",
                   nil);
        } else {
            resolve(nil);
        }
    });
}

@end
