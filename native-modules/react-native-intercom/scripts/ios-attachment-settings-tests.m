#import <Foundation/Foundation.h>
#import <objc/runtime.h>
#import "ICMConfigDependencies.h"
#import "ICMLogger.h"
@interface ICMRecoveredAttachmentSettings (FixtureInspection)
@property (nonatomic, readonly) BOOL gifsEnabled, cameraEnabled, filesEnabled, mediaEnabled, voiceTranscriptionEnabled;
@property (nonatomic, readonly) NSInteger uploadSizeLimit;
@property (nonatomic, readonly) NSArray *allowedFileExtensions;
+ (NSInteger)defaultUploadSize;
- (instancetype)initWithGifsEnabled:(BOOL)gifs cameraEnabled:(BOOL)camera filesEnabled:(BOOL)files mediaEnabled:(BOOL)media voiceTranscriptionEnabled:(BOOL)voice uploadSizeLimit:(NSInteger)limit allowedFileExtensions:(NSArray *)extensions;
@end
static NSMutableArray *passed,*logs,*queries;
static void check(BOOL value,NSString *name){if(!value){fprintf(stderr,"FAIL: %s\n",name.UTF8String);exit(1);}[passed addObject:name];}
@interface FixtureAttachmentBundle:NSObject
@property(nonatomic,strong)NSDictionary *info;
@end
@implementation FixtureAttachmentBundle
- (id)objectForInfoDictionaryKey:(NSString *)key{[queries addObject:key];return self.info[key];}
@end
@interface FixtureAttachmentDefaults:NSObject
@property(nonatomic)BOOL override;
@end
@implementation FixtureAttachmentDefaults
- (BOOL)boolForKey:(NSString *)key{if(![key isEqual:@"isVoiceTranscriptionEnabled"])abort();[queries addObject:key];return self.override;}
@end
int main(void){
 @autoreleasepool{
    passed=[NSMutableArray new];logs=[NSMutableArray new];queries=[NSMutableArray new];
    FixtureAttachmentBundle *bundle=[FixtureAttachmentBundle new];FixtureAttachmentDefaults *defaults=[FixtureAttachmentDefaults new];
    Method bm=class_getClassMethod(NSBundle.class,@selector(mainBundle)),dm=class_getClassMethod(NSUserDefaults.class,@selector(standardUserDefaults)),lm=class_getClassMethod(ICMLogger.class,@selector(log:type:));
    IMP oldBundle=method_setImplementation(bm,imp_implementationWithBlock(^id(id cls){(void)cls;return bundle;}));
    IMP oldDefaults=method_setImplementation(dm,imp_implementationWithBlock(^id(id cls){(void)cls;return defaults;}));
    IMP oldLog=method_setImplementation(lm,imp_implementationWithBlock(^(id cls,NSString *message,os_log_type_t type){(void)cls;[logs addObject:@{@"message":message,@"type":@(type)}];}));
    bundle.info=@{@"NSCameraUsageDescription":@"synthetic",@"NSMicrophoneUsageDescription":@"synthetic"};
    ICMRecoveredAttachmentSettings *settings=[ICMRecoveredAttachmentSettings new];
    check(settings.gifsEnabled&&settings.cameraEnabled&&settings.filesEnabled&&settings.mediaEnabled&&!settings.voiceTranscriptionEnabled&&settings.uploadSizeLimit==104857600&&settings.allowedFileExtensions.count==0&&[ICMRecoveredAttachmentSettings defaultUploadSize]==104857600,@"original defaults true media flags false voice 100MiB and empty extensions");
    check(logs.count==0&&[queries isEqual:@[@"NSCameraUsageDescription",@"NSCameraUsageDescription",@"NSMicrophoneUsageDescription",@"isVoiceTranscriptionEnabled"]],@"original camera lookup repetition and voice defaults read order");
    bundle.info=@{};[logs removeAllObjects];[queries removeAllObjects];settings=[ICMRecoveredAttachmentSettings new];
    check(!settings.cameraEnabled&&!settings.voiceTranscriptionEnabled&&logs.count==1&&[logs[0][@"type"] isEqual:@16]&&[logs[0][@"message"] hasPrefix:@"Camera input has been disabled"],@"missing camera description disables camera and logs original error");
    bundle.info=@{@"NSCameraUsageDescription":@""};[logs removeAllObjects];settings=[ICMRecoveredAttachmentSettings new];
    check(settings.cameraEnabled&&logs.count==1&&[logs[0][@"type"] isEqual:@1]&&[logs[0][@"message"] hasPrefix:@"Photo capture only"],@"empty camera description counts as present and missing mic logs photo-only info");
    bundle.info=@{@"NSCameraUsageDescription":[NSNull null],@"NSMicrophoneUsageDescription":@42};defaults.override=YES;[logs removeAllObjects];settings=[ICMRecoveredAttachmentSettings new];
    check(settings.cameraEnabled&&settings.voiceTranscriptionEnabled&&logs.count==0,@"nonnull nonstring usage descriptions are accepted and preference enables voice");
    bundle.info=@{@"NSCameraUsageDescription":@"synthetic"};[logs removeAllObjects];settings=[[ICMRecoveredAttachmentSettings alloc] initWithGifsEnabled:NO cameraEnabled:NO filesEnabled:NO mediaEnabled:NO voiceTranscriptionEnabled:YES uploadSizeLimit:-7 allowedFileExtensions:@[@"EXE",@"exe",@"EXE"]];
    check(!settings.cameraEnabled&&!settings.voiceTranscriptionEnabled&&settings.uploadSizeLimit==-7&&[settings.allowedFileExtensions isEqual:@[@"EXE",@"exe",@"EXE"]]&&logs.count==1&&[logs[0][@"message"] hasPrefix:@"Voice transcription has been disabled"],@"direct initializer retains arbitrary byte limit extensions and disables missing-microphone voice");
    defaults.override=NO;bundle.info=@{@"NSCameraUsageDescription":@"synthetic",@"NSMicrophoneUsageDescription":@"synthetic"};
    NSDictionary *input=@{@"conversation_gifs_enabled":@YES,@"conversation_camera_enabled":@NO,@"conversation_files_enabled":@YES,@"conversation_media_enabled":@NO,@"conversation_voice_notes_enabled":@YES,@"upload_size_limit":@(-123),@"file_upload_extension_trusted_list":@[@".zip",@".png"]};
    settings=[[ICMRecoveredAttachmentSettings alloc] initWithDictionary:input];
    check(settings&&settings.gifsEnabled&&!settings.cameraEnabled&&settings.filesEnabled&&!settings.mediaEnabled&&settings.voiceTranscriptionEnabled&&settings.uploadSizeLimit==-123&&[settings.allowedFileExtensions isEqual:@[@".zip",@".png"]],@"required server dictionary preserves all exact model values");
    for(NSString *key in input){NSMutableDictionary *missing=[input mutableCopy];[missing removeObjectForKey:key];check([[ICMRecoveredAttachmentSettings alloc] initWithDictionary:missing]==nil,[NSString stringWithFormat:@"missing required attachment field %@",key]);NSMutableDictionary *wrong=[input mutableCopy];wrong[key]=[NSNull null];check([[ICMRecoveredAttachmentSettings alloc] initWithDictionary:wrong]==nil,[NSString stringWithFormat:@"null required attachment field %@",key]);}
    NSMutableArray *mutable=[NSMutableArray arrayWithObject:@".a"];ICMRecoveredAttachmentSettings *copied=[[ICMRecoveredAttachmentSettings alloc] initWithGifsEnabled:YES cameraEnabled:NO filesEnabled:YES mediaEnabled:YES voiceTranscriptionEnabled:NO uploadSizeLimit:3 allowedFileExtensions:mutable];[mutable addObject:@".b"];
    check([copied.allowedFileExtensions isEqual:@[@".a"]],@"Objective-C extensions bridge to Swift value array without mutable alias");
    NSData *data=[NSKeyedArchiver archivedDataWithRootObject:settings requiringSecureCoding:YES error:nil];
    bundle.info=@{};[logs removeAllObjects];ICMRecoveredAttachmentSettings *decoded=[NSKeyedUnarchiver unarchivedObjectOfClass:ICMRecoveredAttachmentSettings.class fromData:data error:nil];
    check(decoded&&decoded.gifsEnabled&&!decoded.cameraEnabled&&decoded.filesEnabled&&!decoded.mediaEnabled&&!decoded.voiceTranscriptionEnabled&&decoded.uploadSizeLimit==-123&&[decoded.allowedFileExtensions isEqual:@[@".zip",@".png"]],@"genuine decoder preserves fields and array while rechecking voice against current usage description");
    bundle.info=@{@"NSCameraUsageDescription":@"synthetic",@"NSMicrophoneUsageDescription":@"synthetic"};ICMRecoveredAttachmentSettings *camera=[ICMRecoveredAttachmentSettings new];data=[NSKeyedArchiver archivedDataWithRootObject:camera requiringSecureCoding:YES error:nil];bundle.info=@{};decoded=[NSKeyedUnarchiver unarchivedObjectOfClass:ICMRecoveredAttachmentSettings.class fromData:data error:nil];
    check(decoded.cameraEnabled,@"decoder preserves archived camera without rerunning construction camera checks");
    check(![settings isEqual:nil]&&![settings isEqual:[NSObject new]],@"model equality rejects nil and wrong class");
    method_setImplementation(bm,oldBundle);method_setImplementation(dm,oldDefaults);method_setImplementation(lm,oldLog);
    NSData *json=[NSJSONSerialization dataWithJSONObject:@{@"status":@"passed",@"testCount":@(passed.count),@"tests":passed,@"scope":@"Actual required Config attachment model/NSCoding; diagnostic bundle/defaults/logger only, no permissions or default preference access"} options:NSJSONWritingPrettyPrinted error:nil];fwrite(json.bytes,1,json.length,stdout);fputc('\n',stdout);
 }
 return 0;
}
