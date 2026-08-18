#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface FirmwareArchiveEntryInfo : NSObject

@property(nonatomic, readonly) NSString *name;
@property(nonatomic, readonly) int64_t compressedSize;
@property(nonatomic, readonly) int64_t uncompressedSize;
@property(nonatomic, readonly) uint32_t crc32;
@property(nonatomic, readonly) uint16_t flags;
@property(nonatomic, readonly) uint16_t compressionMethod;
@property(nonatomic, readonly) uint16_t versionMadeBy;
@property(nonatomic, readonly) uint32_t externalAttributes;
@property(nonatomic, readonly) uint32_t diskNumber;
@property(nonatomic, readonly, nullable) NSString *linkName;

@end

@interface FirmwareArchiveMinizipBridge : NSObject

+ (nullable NSArray<FirmwareArchiveEntryInfo *> *)scanArchiveAtPath:
    (NSString *)path
    error:(NSError * _Nullable * _Nullable)error;

+ (BOOL)extractEntryNamed:(NSString *)entryName
            archivePath:(NSString *)archivePath
               consumer:(BOOL (^)(NSData *chunk))consumer
                  error:(NSError * _Nullable * _Nullable)error;

@end

NS_ASSUME_NONNULL_END
