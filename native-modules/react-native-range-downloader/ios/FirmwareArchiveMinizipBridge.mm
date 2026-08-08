#import "FirmwareArchiveMinizipBridge.h"

#import "mz.h"
#import "mz_strm.h"
#import "mz_zip.h"
#import "mz_zip_rw.h"

#include <cstring>

static NSString *const FirmwareArchiveBridgeErrorDomain =
    @"so.onekey.firmware.archive.minizip";

static NSError *FirmwareArchiveBridgeError(NSString *message, int32_t code) {
  return [NSError errorWithDomain:FirmwareArchiveBridgeErrorDomain
                             code:code
                         userInfo:@{NSLocalizedDescriptionKey : message}];
}

@interface FirmwareArchiveEntryInfo ()

@property(nonatomic, readwrite) NSString *name;
@property(nonatomic, readwrite) int64_t compressedSize;
@property(nonatomic, readwrite) int64_t uncompressedSize;
@property(nonatomic, readwrite) uint32_t crc32;
@property(nonatomic, readwrite) uint16_t flags;
@property(nonatomic, readwrite) uint16_t compressionMethod;
@property(nonatomic, readwrite) uint16_t versionMadeBy;
@property(nonatomic, readwrite) uint32_t externalAttributes;
@property(nonatomic, readwrite) uint32_t diskNumber;
@property(nonatomic, readwrite, nullable) NSString *linkName;

@end

@implementation FirmwareArchiveEntryInfo
@end

@implementation FirmwareArchiveMinizipBridge

+ (nullable NSArray<FirmwareArchiveEntryInfo *> *)scanArchiveAtPath:
    (NSString *)path
    error:(NSError * _Nullable * _Nullable)error {
  void *reader = nullptr;
  mz_zip_reader_create(&reader);
  if (reader == nullptr) {
    if (error != nullptr) {
      *error = FirmwareArchiveBridgeError(@"Archive reader allocation failed",
                                          MZ_MEM_ERROR);
    }
    return nil;
  }

  int32_t result = mz_zip_reader_open_file(reader, path.fileSystemRepresentation);
  if (result != MZ_OK) {
    mz_zip_reader_delete(&reader);
    if (error != nullptr) {
      *error = FirmwareArchiveBridgeError(@"Archive open failed", result);
    }
    return nil;
  }

  NSMutableArray<FirmwareArchiveEntryInfo *> *entries =
      [NSMutableArray array];
  result = mz_zip_reader_goto_first_entry(reader);
  while (result == MZ_OK) {
    mz_zip_file *fileInfo = nullptr;
    result = mz_zip_reader_entry_get_info(reader, &fileInfo);
    if (result != MZ_OK || fileInfo == nullptr ||
        fileInfo->filename == nullptr) {
      result = result == MZ_OK ? MZ_FORMAT_ERROR : result;
      break;
    }
    const size_t filenameLength = std::strlen(fileInfo->filename);
    if (filenameLength == 0 || filenameLength > 4096) {
      result = MZ_FORMAT_ERROR;
      break;
    }
    if ((fileInfo->flag & (1 << 11)) == 0) {
      const unsigned char *filenameBytes =
          reinterpret_cast<const unsigned char *>(fileInfo->filename);
      bool isAscii = true;
      for (size_t index = 0; index < filenameLength; index += 1) {
        if (filenameBytes[index] >= 0x80) {
          isAscii = false;
          break;
        }
      }
      if (!isAscii) {
        result = MZ_FORMAT_ERROR;
        break;
      }
    }
    NSString *name = [NSString stringWithUTF8String:fileInfo->filename];
    if (name == nil) {
      result = MZ_FORMAT_ERROR;
      break;
    }
    NSString *linkName = nil;
    if (fileInfo->linkname != nullptr) {
      linkName = [NSString stringWithUTF8String:fileInfo->linkname];
      if (linkName == nil) {
        result = MZ_FORMAT_ERROR;
        break;
      }
    }

    FirmwareArchiveEntryInfo *entry =
        [[FirmwareArchiveEntryInfo alloc] init];
    entry.name = name;
    entry.compressedSize = fileInfo->compressed_size;
    entry.uncompressedSize = fileInfo->uncompressed_size;
    entry.crc32 = fileInfo->crc;
    entry.flags = fileInfo->flag;
    entry.compressionMethod = fileInfo->compression_method;
    entry.versionMadeBy = fileInfo->version_madeby;
    entry.externalAttributes = fileInfo->external_fa;
    entry.diskNumber = fileInfo->disk_number;
    entry.linkName = linkName;
    [entries addObject:entry];
    if (entries.count > 4096) {
      result = MZ_FORMAT_ERROR;
      break;
    }
    result = mz_zip_reader_goto_next_entry(reader);
  }

  if (result == MZ_END_OF_LIST) {
    result = MZ_OK;
  }
  int32_t closeResult = mz_zip_reader_close(reader);
  mz_zip_reader_delete(&reader);
  if (result != MZ_OK || closeResult != MZ_OK) {
    if (error != nullptr) {
      *error = FirmwareArchiveBridgeError(@"Archive scan failed",
                                          result != MZ_OK ? result
                                                          : closeResult);
    }
    return nil;
  }
  return entries;
}

+ (BOOL)extractEntryNamed:(NSString *)entryName
            archivePath:(NSString *)archivePath
               consumer:(BOOL (^)(NSData *chunk))consumer
                  error:(NSError * _Nullable * _Nullable)error {
  void *reader = nullptr;
  bool archiveOpened = false;
  bool entryOpened = false;
  mz_zip_reader_create(&reader);
  if (reader == nullptr) {
    if (error != nullptr) {
      *error = FirmwareArchiveBridgeError(@"Archive reader allocation failed",
                                          MZ_MEM_ERROR);
    }
    return NO;
  }

  int32_t result =
      mz_zip_reader_open_file(reader, archivePath.fileSystemRepresentation);
  if (result == MZ_OK) {
    archiveOpened = true;
  }
  if (result == MZ_OK) {
    result = mz_zip_reader_locate_entry(
        reader, entryName.UTF8String, 0);
  }
  if (result == MZ_OK) {
    result = mz_zip_reader_entry_open(reader);
    if (result == MZ_OK) {
      entryOpened = true;
    }
  }

  uint8_t buffer[64 * 1024];
  while (result == MZ_OK) {
    int32_t count =
        mz_zip_reader_entry_read(reader, buffer, (int32_t)sizeof(buffer));
    if (count < 0) {
      result = count;
      break;
    }
    if (count == 0) {
      break;
    }
    NSData *chunk = [NSData dataWithBytes:buffer length:(NSUInteger)count];
    if (!consumer(chunk)) {
      result = MZ_WRITE_ERROR;
      break;
    }
  }

  int32_t entryCloseResult =
      entryOpened ? mz_zip_reader_entry_close(reader) : MZ_OK;
  int32_t archiveCloseResult =
      archiveOpened ? mz_zip_reader_close(reader) : MZ_OK;
  mz_zip_reader_delete(&reader);
  if (result != MZ_OK || entryCloseResult != MZ_OK ||
      archiveCloseResult != MZ_OK) {
    if (error != nullptr) {
      int32_t failure = result != MZ_OK
                            ? result
                            : (entryCloseResult != MZ_OK
                                   ? entryCloseResult
                                   : archiveCloseResult);
      *error = FirmwareArchiveBridgeError(
          @"Archive entry extraction or CRC validation failed", failure);
    }
    return NO;
  }
  return YES;
}

@end
