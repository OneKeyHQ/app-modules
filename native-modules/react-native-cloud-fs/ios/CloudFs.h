#import <RNCloudFsSpec/RNCloudFsSpec.h>

@interface CloudFs : NativeCloudFsSpecBase <NativeCloudFsSpec>

@property (nonatomic, strong) NSMetadataQuery *query;

@end
