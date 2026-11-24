#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, BundleDiffZipExtractionResult) {
    BundleDiffZipExtractionResultSuccess,
    BundleDiffZipExtractionResultInvalidArchive,
    BundleDiffZipExtractionResultStorageError,
    BundleDiffZipExtractionResultSecurityRejected,
};

@interface BundleDiffZipExtractor : NSObject

+ (BundleDiffZipExtractionResult)extractArchiveAtPath:(NSString *)archivePath
                                       toNewDirectory:(NSString *)destination
                                             maxBytes:(uint64_t)maxBytes
                                           maxEntries:(NSUInteger)maxEntries
    NS_SWIFT_NAME(extract(archivePath:destination:maxBytes:maxEntries:));

@end

NS_ASSUME_NONNULL_END
