#import <RNZipArchiveSpec/RNZipArchiveSpec.h>
#import <SSZipArchive/SSZipArchive.h>

@interface ZipArchive : NativeZipArchiveSpecBase <NativeZipArchiveSpec, SSZipArchiveDelegate>

@property (nonatomic) NSString *processedFilePath;
@property (nonatomic) float progress;
@property (nonatomic, copy) void (^progressHandler)(NSUInteger entryNumber, NSUInteger total);

@end
