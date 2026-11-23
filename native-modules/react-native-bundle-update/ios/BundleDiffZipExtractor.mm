#import "BundleDiffZipExtractor.h"
#import "mz_compat.h"

#include <cstdio>
#include <cstring>
#include <fcntl.h>
#include <memory>
#include <sys/stat.h>
#include <unistd.h>
#include <vector>

namespace {

BundleDiffZipExtractionResult extractFile(unzFile archive, NSString *path,
                                           uint64_t declaredBytes, uint64_t &remainingBytes) {
    if (unzOpenCurrentFile(archive) != UNZ_OK) {
        return BundleDiffZipExtractionResultInvalidArchive;
    }

    auto result = BundleDiffZipExtractionResultSuccess;
    int descriptor = open(path.fileSystemRepresentation, O_WRONLY | O_CREAT | O_EXCL | O_NOFOLLOW, 0600);
    FILE *output = descriptor < 0 ? nullptr : fdopen(descriptor, "wb");
    uint64_t written = 0;
    if (!output) {
        if (descriptor >= 0) close(descriptor);
        result = BundleDiffZipExtractionResultStorageError;
    } else {
        uint8_t buffer[64 * 1024];
        int count;
        while ((count = unzReadCurrentFile(archive, buffer, sizeof(buffer))) > 0) {
            if (static_cast<uint64_t>(count) > remainingBytes) {
                result = BundleDiffZipExtractionResultSecurityRejected;
                break;
            }
            if (fwrite(buffer, 1, count, output) != static_cast<size_t>(count)) {
                result = BundleDiffZipExtractionResultStorageError;
                break;
            }
            written += count;
            remainingBytes -= count;
        }
        if (result == BundleDiffZipExtractionResultSuccess && (count < 0 || written != declaredBytes)) {
            result = BundleDiffZipExtractionResultInvalidArchive;
        }
        if (fclose(output) != 0 && result == BundleDiffZipExtractionResultSuccess) {
            result = BundleDiffZipExtractionResultStorageError;
        }
    }

    if (unzCloseCurrentFile(archive) != UNZ_OK && result == BundleDiffZipExtractionResultSuccess) {
        result = BundleDiffZipExtractionResultInvalidArchive;
    }
    return result;
}

BundleDiffZipExtractionResult extractEntries(unzFile archive, NSString *destination,
                                              uint64_t maxBytes, NSUInteger maxEntries) {
    NSFileManager *fileManager = NSFileManager.defaultManager;
    NSMutableSet<NSString *> *directories = [NSMutableSet set];
    NSUInteger archiveEntries = 0;
    NSUInteger stagedEntries = 0;
    uint64_t remainingDeclaredBytes = maxBytes;
    uint64_t remainingActualBytes = maxBytes;
    int status = unzGoToFirstFile(archive);
    while (status == UNZ_OK) {
        @autoreleasepool {
            if (archiveEntries == maxEntries) {
                return BundleDiffZipExtractionResultSecurityRejected;
            }
            archiveEntries += 1;

            unz_file_info64 info = {};
            if (unzGetCurrentFileInfo64(archive, &info, nullptr, 0, nullptr, 0, nullptr, 0) != UNZ_OK ||
                info.size_filename == 0) {
                return BundleDiffZipExtractionResultInvalidArchive;
            }
            if (info.uncompressed_size > remainingDeclaredBytes) {
                return BundleDiffZipExtractionResultSecurityRejected;
            }
            remainingDeclaredBytes -= info.uncompressed_size;

            std::vector<char> nameBytes(info.size_filename);
            if (unzGetCurrentFileInfo64(archive, nullptr, nameBytes.data(), nameBytes.size(),
                                        nullptr, 0, nullptr, 0) != UNZ_OK) {
                return BundleDiffZipExtractionResultInvalidArchive;
            }
            if (std::memchr(nameBytes.data(), 0, nameBytes.size()) ||
                ((info.external_fa >> 16) & S_IFMT) == S_IFLNK) {
                return BundleDiffZipExtractionResultSecurityRejected;
            }
            NSString *name = [[NSString alloc] initWithBytes:nameBytes.data()
                                                      length:nameBytes.size()
                                                    encoding:NSUTF8StringEncoding];
            if (!name && !(info.flag & (1 << 11))) {
                NSStringEncoding dosEncoding = CFStringConvertEncodingToNSStringEncoding(kCFStringEncodingDOSLatinUS);
                name = [[NSString alloc] initWithBytes:nameBytes.data()
                                                length:nameBytes.size()
                                              encoding:dosEncoding];
            }
            if (!name) return BundleDiffZipExtractionResultInvalidArchive;

            BOOL isDirectory = [name hasSuffix:@"/"];
            NSString *relativePath = isDirectory ? [name substringToIndex:name.length - 1] : name;
            NSArray<NSString *> *components = [relativePath componentsSeparatedByString:@"/"];
            for (NSString *component in components) {
                if (!component.length || [component isEqualToString:@"."] ||
                    [component isEqualToString:@".."] || [component containsString:@"\\"]) {
                    return BundleDiffZipExtractionResultSecurityRejected;
                }
            }
            // Match the existing OTA extractor's handling of resource forks.
            if (![name hasPrefix:@"__MACOSX/"]) {
                NSString *path = destination;
                for (NSUInteger index = 0; index < components.count; index += 1) {
                    path = [path stringByAppendingPathComponent:components[index]];
                    BOOL createDirectory = isDirectory || index < components.count - 1;
                    if (createDirectory && [directories containsObject:path]) continue;
                    if (stagedEntries == maxEntries) {
                        return BundleDiffZipExtractionResultSecurityRejected;
                    }
                    stagedEntries += 1;
                    if (createDirectory) {
                        if (![fileManager createDirectoryAtPath:path withIntermediateDirectories:NO
                                                    attributes:nil error:nil]) {
                            return BundleDiffZipExtractionResultStorageError;
                        }
                        [directories addObject:path];
                    } else {
                        auto result = extractFile(archive, path, info.uncompressed_size, remainingActualBytes);
                        if (result != BundleDiffZipExtractionResultSuccess) return result;
                    }
                }
            }
            status = unzGoToNextFile(archive);
        }
    }

    return status == UNZ_END_OF_LIST_OF_FILE && remainingActualBytes < maxBytes
        ? BundleDiffZipExtractionResultSuccess
        : BundleDiffZipExtractionResultInvalidArchive;
}

} // namespace

@implementation BundleDiffZipExtractor

+ (BundleDiffZipExtractionResult)extractArchiveAtPath:(NSString *)archivePath
                                       toNewDirectory:(NSString *)destination
                                             maxBytes:(uint64_t)maxBytes
                                           maxEntries:(NSUInteger)maxEntries {
    std::unique_ptr<void, decltype(&unzClose)> archive(unzOpen(archivePath.fileSystemRepresentation), unzClose);
    if (!archive) return BundleDiffZipExtractionResultInvalidArchive;

    NSFileManager *fileManager = NSFileManager.defaultManager;
    if (mkdir(destination.fileSystemRepresentation, 0700) != 0) {
        return BundleDiffZipExtractionResultStorageError;
    }

    auto result = extractEntries(archive.get(), destination, maxBytes, maxEntries);
    if (result != BundleDiffZipExtractionResultSuccess) {
        [fileManager removeItemAtPath:destination error:nil];
    }
    return result;
}

@end
