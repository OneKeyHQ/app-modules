#import "FirmwareArchiveMinizipBridge.h"

#include <minizip/mz_compat.h>
#include <minizip/mz_zip.h>
#include <unistd.h>

namespace {

static NSString *const OKFirmwareArchiveBridgeErrorDomain =
    @"ARTIFACT_ARCHIVE_MINIZIP";
static const NSUInteger OKMaximumEntryCount = 4096;
static const NSUInteger OKMaximumNameBytes = 1024;
static const size_t OKExtractionBufferBytes = 64 * 1024;
static const size_t OKEndOfCentralDirectoryBytes = 22;
static const size_t OKMaximumZipCommentBytes = 65'535;
static const uint32_t OKEndOfCentralDirectorySignature = 0x06054b50;

static NSError *OKArchiveError(NSInteger code, NSString *message) {
  return [NSError
      errorWithDomain:OKFirmwareArchiveBridgeErrorDomain
                 code:code
             userInfo:@{NSLocalizedDescriptionKey : message}];
}

static uint16_t OKReadUInt16(const uint8_t *bytes, size_t offset) {
  return (uint16_t)bytes[offset] | ((uint16_t)bytes[offset + 1] << 8);
}

static uint32_t OKReadUInt32(const uint8_t *bytes, size_t offset) {
  return (uint32_t)bytes[offset] | ((uint32_t)bytes[offset + 1] << 8) |
         ((uint32_t)bytes[offset + 2] << 16) |
         ((uint32_t)bytes[offset + 3] << 24);
}

static BOOL OKInspectZip64EndRecord(NSString *archivePath, BOOL *usesZip64,
                                    NSError **error) {
  FILE *archive = fopen(archivePath.fileSystemRepresentation, "rb");
  if (archive == nullptr || fseeko(archive, 0, SEEK_END) != 0) {
    if (archive != nullptr) {
      fclose(archive);
    }
    if (error != nullptr) {
      *error = OKArchiveError(20, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return NO;
  }
  off_t archiveSize = ftello(archive);
  size_t tailSize =
      archiveSize > 0
          ? (size_t)MIN((uint64_t)archiveSize,
                        OKEndOfCentralDirectoryBytes + OKMaximumZipCommentBytes)
          : 0;
  NSMutableData *tail = [NSMutableData dataWithLength:tailSize];
  BOOL readable =
      archiveSize >= (off_t)OKEndOfCentralDirectoryBytes &&
      fseeko(archive, archiveSize - (off_t)tailSize, SEEK_SET) == 0 &&
      fread(tail.mutableBytes, 1, tailSize, archive) == tailSize;
  int closeResult = fclose(archive);
  if (!readable || closeResult != 0) {
    if (error != nullptr) {
      *error = OKArchiveError(20, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return NO;
  }

  const uint8_t *bytes = static_cast<const uint8_t *>(tail.bytes);
  for (NSInteger offset =
           (NSInteger)tailSize - (NSInteger)OKEndOfCentralDirectoryBytes;
       offset >= 0; offset -= 1) {
    size_t index = (size_t)offset;
    if (OKReadUInt32(bytes, index) != OKEndOfCentralDirectorySignature) {
      continue;
    }
    uint16_t commentLength = OKReadUInt16(bytes, index + 20);
    if (index + OKEndOfCentralDirectoryBytes + commentLength != tailSize) {
      continue;
    }
    *usesZip64 = OKReadUInt16(bytes, index + 8) == UINT16_MAX ||
                 OKReadUInt16(bytes, index + 10) == UINT16_MAX ||
                 OKReadUInt32(bytes, index + 12) == UINT32_MAX ||
                 OKReadUInt32(bytes, index + 16) == UINT32_MAX;
    return YES;
  }
  if (error != nullptr) {
    *error = OKArchiveError(20, @"ARTIFACT_ARCHIVE_INVALID");
  }
  return NO;
}

static NSData *OKReadRawName(unzFile archive, unz_file_info64 *fileInfo,
                             NSError **error) {
  int result = unzGetCurrentFileInfo64(archive, fileInfo, nullptr, 0, nullptr,
                                       0, nullptr, 0);
  if (result != UNZ_OK || fileInfo->size_filename == 0 ||
      fileInfo->size_filename > OKMaximumNameBytes) {
    if (error != nullptr) {
      *error = OKArchiveError(2, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
    }
    return nil;
  }
  NSMutableData *rawName =
      [NSMutableData dataWithLength:fileInfo->size_filename];
  result = unzGetCurrentFileInfo64(
      archive, fileInfo, static_cast<char *>(rawName.mutableBytes),
      fileInfo->size_filename, nullptr, 0, nullptr, 0);
  if (result != UNZ_OK) {
    if (error != nullptr) {
      *error = OKArchiveError(3, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
    }
    return nil;
  }
  return rawName;
}

static BOOL OKCurrentEntryUsesZip64(unzFile archive, NSError **error) {
  mz_zip_file *fileInfo = nullptr;
  void *handle = unzGetHandle_MZ(archive);
  if (handle == nullptr ||
      mz_zip_entry_get_info(handle, &fileInfo) != MZ_OK ||
      fileInfo == nullptr) {
    if (error != nullptr) {
      *error = OKArchiveError(18, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
    }
    return YES;
  }
  size_t offset = 0;
  while (offset < fileInfo->extrafield_size) {
    if (offset + 4 > fileInfo->extrafield_size) {
      if (error != nullptr) {
        *error = OKArchiveError(18, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
      }
      return YES;
    }
    uint16_t headerId = OKReadUInt16(fileInfo->extrafield, offset);
    uint16_t dataSize = OKReadUInt16(fileInfo->extrafield, offset + 2);
    offset += 4;
    if (offset + dataSize > fileInfo->extrafield_size) {
      if (error != nullptr) {
        *error = OKArchiveError(18, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
      }
      return YES;
    }
    if (headerId == MZ_ZIP_EXTENSION_ZIP64) {
      return YES;
    }
    offset += dataSize;
  }
  return fileInfo->compressed_size >= UINT32_MAX ||
         fileInfo->uncompressed_size >= UINT32_MAX ||
         fileInfo->disk_offset >= UINT32_MAX;
}

static BOOL OKExtractCurrentEntry(unzFile archive, NSData *expectedRawName,
                                  unsigned long long expectedSize,
                                  NSString *outputPath, NSError **error) {
  unz_file_info64 fileInfo = {};
  NSData *rawName = OKReadRawName(archive, &fileInfo, error);
  if (rawName == nil || ![rawName isEqualToData:expectedRawName] ||
      fileInfo.uncompressed_size != expectedSize) {
    if (error != nullptr && *error == nil) {
      *error = OKArchiveError(8, @"ARTIFACT_ARCHIVE_ENTRY_MISMATCH");
    }
    return NO;
  }
  if (unzOpenCurrentFile(archive) != UNZ_OK) {
    if (error != nullptr) {
      *error = OKArchiveError(9, @"ARTIFACT_ARCHIVE_INTEGRITY_FAILED");
    }
    return NO;
  }

  BOOL shouldPersist = outputPath.length > 0;
  FILE *output = nullptr;
  if (shouldPersist) {
    output = fopen(outputPath.fileSystemRepresentation, "wb");
    if (output == nullptr) {
      unzCloseCurrentFile(archive);
      if (error != nullptr) {
        *error = OKArchiveError(10, @"ARTIFACT_STORAGE_FAILED");
      }
      return NO;
    }
  }

  void *buffer = malloc(OKExtractionBufferBytes);
  BOOL success = buffer != nullptr;
  unsigned long long written = 0;
  while (success) {
    int bytesRead =
        unzReadCurrentFile(archive, buffer, (uint32_t)OKExtractionBufferBytes);
    if (bytesRead < 0) {
      success = NO;
      break;
    }
    if (bytesRead == 0) {
      break;
    }
    if ((unsigned long long)bytesRead > expectedSize - written ||
        (shouldPersist &&
         fwrite(buffer, 1, (size_t)bytesRead, output) !=
             (size_t)bytesRead)) {
      success = NO;
      break;
    }
    written += (unsigned long long)bytesRead;
  }
  if (buffer != nullptr) {
    free(buffer);
  }

  if (success && written == expectedSize && shouldPersist) {
    success = fflush(output) == 0 && fsync(fileno(output)) == 0;
  } else if (written != expectedSize) {
    success = NO;
  }
  if (shouldPersist && fclose(output) != 0) {
    success = NO;
  }
  success = success && unzCloseCurrentFile(archive) == UNZ_OK;
  if (!success) {
    if (shouldPersist) {
      [[NSFileManager defaultManager] removeItemAtPath:outputPath error:nil];
    }
    if (error != nullptr) {
      *error = OKArchiveError(11, @"ARTIFACT_ARCHIVE_INTEGRITY_FAILED");
    }
  }
  return success;
}

}  // namespace

@interface OKFirmwareArchiveEntryInfo ()

- (instancetype)initWithIndex:(NSInteger)index
                      rawName:(NSData *)rawName
               compressedSize:(unsigned long long)compressedSize
             uncompressedSize:(unsigned long long)uncompressedSize
            compressionMethod:(NSInteger)compressionMethod
          generalPurposeFlags:(NSInteger)generalPurposeFlags
                versionMadeBy:(NSInteger)versionMadeBy
           externalAttributes:(unsigned int)externalAttributes
              diskNumberStart:(NSInteger)diskNumberStart
                    usesZip64:(BOOL)usesZip64;

@end

@implementation OKFirmwareArchiveEntryInfo

- (instancetype)initWithIndex:(NSInteger)index
                      rawName:(NSData *)rawName
               compressedSize:(unsigned long long)compressedSize
             uncompressedSize:(unsigned long long)uncompressedSize
            compressionMethod:(NSInteger)compressionMethod
          generalPurposeFlags:(NSInteger)generalPurposeFlags
                versionMadeBy:(NSInteger)versionMadeBy
           externalAttributes:(unsigned int)externalAttributes
              diskNumberStart:(NSInteger)diskNumberStart
                    usesZip64:(BOOL)usesZip64 {
  self = [super init];
  if (self != nil) {
    _index = index;
    _rawName = [rawName copy];
    _compressedSize = compressedSize;
    _uncompressedSize = uncompressedSize;
    _compressionMethod = compressionMethod;
    _generalPurposeFlags = generalPurposeFlags;
    _versionMadeBy = versionMadeBy;
    _externalAttributes = externalAttributes;
    _diskNumberStart = diskNumberStart;
    _usesZip64 = usesZip64;
  }
  return self;
}

@end

@implementation FirmwareArchiveMinizipBridge

+ (nullable NSArray<OKFirmwareArchiveEntryInfo *> *)scanArchiveAtPath:
    (NSString *)archivePath
    error:(NSError **)error {
  BOOL usesZip64EndRecord = NO;
  if (!OKInspectZip64EndRecord(archivePath, &usesZip64EndRecord, error) ||
      usesZip64EndRecord) {
    if (error != nullptr && *error == nil) {
      *error = OKArchiveError(19, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return nil;
  }
  unzFile archive = unzOpen64(archivePath.fileSystemRepresentation);
  if (archive == nullptr) {
    if (error != nullptr) {
      *error = OKArchiveError(1, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return nil;
  }

  unz_global_info64 globalInfo = {};
  int result = unzGetGlobalInfo64(archive, &globalInfo);
  if (result != UNZ_OK || globalInfo.number_disk_with_CD != 0 ||
      globalInfo.number_entry == 0 ||
      globalInfo.number_entry > OKMaximumEntryCount) {
    unzClose(archive);
    if (error != nullptr) {
      *error = OKArchiveError(4, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return nil;
  }

  NSMutableArray<OKFirmwareArchiveEntryInfo *> *entries =
      [NSMutableArray arrayWithCapacity:(NSUInteger)globalInfo.number_entry];
  result = unzGoToFirstFile(archive);
  for (NSUInteger index = 0; index < globalInfo.number_entry; index += 1) {
    if (result != UNZ_OK) {
      unzClose(archive);
      if (error != nullptr) {
        *error = OKArchiveError(5, @"ARTIFACT_ARCHIVE_INVALID");
      }
      return nil;
    }
    unz_file_info64 fileInfo = {};
    NSData *rawName = OKReadRawName(archive, &fileInfo, error);
    if (rawName == nil) {
      unzClose(archive);
      return nil;
    }
    BOOL usesZip64 = OKCurrentEntryUsesZip64(archive, error);
    if (error != nullptr && *error != nil) {
      unzClose(archive);
      return nil;
    }
    if (usesZip64) {
      unzClose(archive);
      if (error != nullptr) {
        *error = OKArchiveError(19, @"ARTIFACT_ARCHIVE_INVALID");
      }
      return nil;
    }
    [entries addObject:[[OKFirmwareArchiveEntryInfo alloc]
                           initWithIndex:(NSInteger)index
                                 rawName:rawName
                          compressedSize:fileInfo.compressed_size
                        uncompressedSize:fileInfo.uncompressed_size
                       compressionMethod:fileInfo.compression_method
                     generalPurposeFlags:fileInfo.flag
                           versionMadeBy:fileInfo.version
                      externalAttributes:fileInfo.external_fa
                         diskNumberStart:fileInfo.disk_num_start
                               usesZip64:usesZip64]];
    result = index + 1 < globalInfo.number_entry ? unzGoToNextFile(archive)
                                                  : UNZ_END_OF_LIST_OF_FILE;
  }

  if (unzClose(archive) != UNZ_OK) {
    if (error != nullptr) {
      *error = OKArchiveError(6, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return nil;
  }
  return entries;
}

+ (BOOL)extractEntriesFromArchivePath:(NSString *)archivePath
                         entryIndexes:(NSArray<NSNumber *> *)entryIndexes
                     expectedRawNames:(NSArray<NSData *> *)expectedRawNames
                        expectedSizes:(NSArray<NSNumber *> *)expectedSizes
                          outputPaths:(NSArray<NSString *> *)outputPaths
                                error:(NSError **)error {
  NSUInteger targetCount = entryIndexes.count;
  if (targetCount == 0 || targetCount > OKMaximumEntryCount ||
      expectedRawNames.count != targetCount ||
      expectedSizes.count != targetCount || outputPaths.count != targetCount) {
    if (error != nullptr) {
      *error = OKArchiveError(12, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
    }
    return NO;
  }
  NSInteger previousIndex = -1;
  NSMutableSet<NSString *> *uniquePaths =
      [NSMutableSet setWithCapacity:targetCount];
  for (NSUInteger position = 0; position < targetCount; position += 1) {
    NSInteger index = entryIndexes[position].integerValue;
    NSString *outputPath = outputPaths[position];
    if (index <= previousIndex ||
        (outputPath.length > 0 && [uniquePaths containsObject:outputPath])) {
      if (error != nullptr) {
        *error = OKArchiveError(13, @"ARTIFACT_ARCHIVE_INVALID_ENTRY");
      }
      return NO;
    }
    previousIndex = index;
    if (outputPath.length > 0) {
      [uniquePaths addObject:outputPath];
    }
  }

  unzFile archive = unzOpen64(archivePath.fileSystemRepresentation);
  if (archive == nullptr) {
    if (error != nullptr) {
      *error = OKArchiveError(14, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return NO;
  }
  unz_global_info64 globalInfo = {};
  if (unzGetGlobalInfo64(archive, &globalInfo) != UNZ_OK ||
      globalInfo.number_entry == 0 ||
      globalInfo.number_entry > OKMaximumEntryCount ||
      (NSUInteger)previousIndex >= globalInfo.number_entry ||
      unzGoToFirstFile(archive) != UNZ_OK) {
    unzClose(archive);
    if (error != nullptr) {
      *error = OKArchiveError(15, @"ARTIFACT_ARCHIVE_INVALID");
    }
    return NO;
  }

  NSUInteger targetPosition = 0;
  BOOL success = YES;
  for (NSUInteger index = 0;
       success && index < globalInfo.number_entry &&
       targetPosition < targetCount;
       index += 1) {
    NSInteger targetIndex = entryIndexes[targetPosition].integerValue;
    if ((NSInteger)index == targetIndex) {
      success = OKExtractCurrentEntry(
          archive, expectedRawNames[targetPosition],
          expectedSizes[targetPosition].unsignedLongLongValue,
          outputPaths[targetPosition], error);
      targetPosition += success ? 1 : 0;
    }
    if (success && index + 1 < globalInfo.number_entry &&
        targetPosition < targetCount &&
        unzGoToNextFile(archive) != UNZ_OK) {
      success = NO;
      if (error != nullptr) {
        *error = OKArchiveError(16, @"ARTIFACT_ARCHIVE_INVALID");
      }
    }
  }
  success = success && targetPosition == targetCount;
  if (unzClose(archive) != UNZ_OK) {
    success = NO;
  }
  if (!success && error != nullptr && *error == nil) {
    *error = OKArchiveError(17, @"ARTIFACT_ARCHIVE_INTEGRITY_FAILED");
  }
  return success;
}

@end
