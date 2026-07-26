#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OKFirmwareArchiveEntryInfo : NSObject

@property(nonatomic, readonly) NSInteger index;
@property(nonatomic, readonly) NSData *rawName;
@property(nonatomic, readonly) unsigned long long compressedSize;
@property(nonatomic, readonly) unsigned long long uncompressedSize;
@property(nonatomic, readonly) NSInteger compressionMethod;
@property(nonatomic, readonly) NSInteger generalPurposeFlags;
@property(nonatomic, readonly) NSInteger versionMadeBy;
@property(nonatomic, readonly) unsigned int externalAttributes;
@property(nonatomic, readonly) NSInteger diskNumberStart;
@property(nonatomic, readonly) BOOL usesZip64;

- (instancetype)init NS_UNAVAILABLE;

@end

@interface FirmwareArchiveMinizipBridge : NSObject

+ (nullable NSArray<OKFirmwareArchiveEntryInfo *> *)scanArchiveAtPath:
    (NSString *)archivePath
    error:(NSError **)error;

// An empty output path validates and drains the entry without persisting it.
+ (BOOL)extractEntriesFromArchivePath:(NSString *)archivePath
                         entryIndexes:(NSArray<NSNumber *> *)entryIndexes
                     expectedRawNames:(NSArray<NSData *> *)expectedRawNames
                        expectedSizes:(NSArray<NSNumber *> *)expectedSizes
                          outputPaths:(NSArray<NSString *> *)outputPaths
                                error:(NSError **)error;

@end

NS_ASSUME_NONNULL_END
