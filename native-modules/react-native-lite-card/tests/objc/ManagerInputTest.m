#import "OKNFCManager.h"
#import "OKLiteV1.h"
#import "OKLiteV2.h"
#import "OKLiteCommandModal.h"
#import "OKNFCUtility.h"
#import "LCLogger.h"
#import "OKTools.h"
#import "NFCConfig.h"

extern const unsigned char GPChannelSDKCoreVersionString[];
static NSMutableArray<NSString *> *messages;
static BOOL loggerThrows;

@interface OneKeyLog : NSObject
+ (void)info:(NSString *)tag :(NSString *)message;
+ (void)debug:(NSString *)tag :(NSString *)message;
@end
@implementation OneKeyLog
+ (void)info:(NSString *)tag :(NSString *)message {
    if (loggerThrows) @throw [NSException exceptionWithName:@"PRIVATE-LOGGER" reason:@"PRIVATE-EXCEPTION" userInfo:nil];
    [messages addObject:message];
}
+ (void)debug:(NSString *)tag :(NSString *)message { [messages addObject:message]; }
@end

static NSUInteger assertions;
static void expect(BOOL value, NSString *scenario) {
    if (!value) { NSLog(@"FAILED: %@", scenario); abort(); }
    ++assertions;
}

// The production manager owns validation, retention, callbacks and operation dispatch.
// These card and NFC adapters never construct commands or touch a device.
@implementation NFCISO7816APDU
- (instancetype)initWithData:(NSData *)data { return [super init]; }
@end
@implementation NFCTagReaderSession
- (instancetype)initWithPollingOption:(NSInteger)option delegate:(id)delegate queue:(dispatch_queue_t)queue { return [super init]; }
- (void)beginSession { abort(); }
- (void)invalidateSession {}
- (void)invalidateSessionWithErrorMessage:(NSString *)message {}
- (void)connectToTag:(id<NFCTag>)tag completionHandler:(void (^)(NSError *))completion { abort(); }
@end
@implementation NFCConfig
+ (NSString *)envFor:(NSString *)key { abort(); }
@end
@implementation OKTools
+ (BOOL)isChineseLan { return NO; }
@end
@implementation OKNFCUtility
+ (void)logAPDU:(NSString *)name response:(NSData *)responseData sw1:(uint8_t)sw1 sw2:(uint8_t)sw2 error:(NSError *)error {}
@end
@implementation OKLiteCommandModal
- (instancetype)initWithCommand:(OKLiteCommand)command version:(OKNFCLiteVersion)version { return [super init]; }
- (NFCISO7816APDU *)buildAPDU { abort(); }
@end
@implementation OKLiteCommandTool
- (void)sendCommandWithAPDU:(NFCISO7816APDU *)apdu modal:(OKLiteCommandModal *)modal
         completionHandler:(void (^)(NSData *, uint8_t, uint8_t, NSError *, NSString *))completion { abort(); }
@end

static void checkCardInfo(Class cardClass, OKNFCLiteStatus status, BOOL isNewCard) {
    OKLiteV1 *card = [[cardClass alloc] initWithDelegate:nil];
    card.SN = @"fixture-card";
    card.pinRTL = 10;
    card.status = status;
    NSDictionary *info = [card cardInfo];
    // React Native maps NSNumber instances of the @YES class to JS booleans.
    expect([info[@"isNewCard"] isKindOfClass:[@YES class]], @"Card info exposes a boolean instead of numeric 0/1");
    expect([info[@"isNewCard"] boolValue] == isNewCard, @"New and activated card states retain their values");
    expect([info[@"serialNum"] isEqualToString:card.SN], @"Card info preserves the serial for identity checks");
}

@interface FakeCard : OKLiteV1
@property (nonatomic, assign) NSUInteger reads;
@property (nonatomic, assign) NSUInteger writes;
@property (nonatomic, copy) NSString *received;
@property (nonatomic, assign) BOOL failRead;
@property (nonatomic, copy) NSString *readSerial;
@end
@implementation FakeCard
- (BOOL)syncLiteInfo {
    ++self.reads;
    if (self.readSerial) self.SN = self.readSerial;
    return !self.failRead;
}
- (void)setMnemonic:(NSString *)input withPin:(NSString *)pin overwrite:(BOOL)overwrite complete:(SetMnemonicCallback)complete {
    ++self.writes;
    self.received = input;
    complete(self, OKNFCLiteSetMncStatusSuccess);
}
- (void)changePin:(NSString *)oldPin to:(NSString *)newPin complete:(ChangePinCallback)complete {
    ++self.writes;
    expect([oldPin isEqualToString:@"synthetic-old-pin"] && [newPin isEqualToString:@"synthetic-new-pin"], @"PINs reach only the guarded operation");
    complete(self, OKNFCLiteChangePinStatusSuccess);
}
@end

@interface OKNFCManager (TestEntryPoints)
- (void)nfcSessionComplete:(NFCTagReaderSession *)session;
- (void)beginNewNFCSession;
- (void)finishPendingRequestAsCancel:(BOOL)asCancel sessionError:(NSError *)sessionError;
@end
@interface TestManager : OKNFCManager
@property (nonatomic, strong) FakeCard *card;
@property (nonatomic, assign) NSUInteger starts;
@property (nonatomic, assign) NSUInteger ends;
@end
@implementation TestManager
- (void)beginNewNFCSession { ++self.starts; }
- (BOOL)checkLiteVersion { [self setValue:self.card forKey:@"lite"]; return YES; }
- (void)endNFCSessionWithError:(BOOL)isError { ++self.ends; }
@end

static void checkPinIdentity(BOOL v2, NSString *expectedSerial, NSString *actualSerial, BOOL failRead, BOOL cancel) {
    TestManager *manager = [TestManager new];
    FakeCard *card = [FakeCard new];
    card.version = v2 ? OKNFCLiteVersionV2 : OKNFCLiteVersionV1;
    card.status = OKNFCLiteStatusActivated;
    card.SN = @"verified-card";
    card.readSerial = actualSerial;
    card.failRead = failRead;
    manager.card = card;
    __block NSUInteger completions = 0;
    __block OKNFCLiteChangePinStatus result = OKNFCLiteChangePinStatusError;
    [manager changePin:@"synthetic-old-pin" to:@"synthetic-new-pin" expectedSerial:expectedSerial
             complete:^(OKLiteV1 *lite, OKNFCLiteChangePinStatus status) {
        ++completions; result = status;
    }];
    if (cancel) [manager finishPendingRequestAsCancel:YES sessionError:nil];
    [manager nfcSessionComplete:nil];
    BOOL guarded = expectedSerial != nil;
    BOOL allowed = !cancel && (!guarded || (!failRead && actualSerial.length && [expectedSerial isEqualToString:actualSerial]));
    OKNFCLiteChangePinStatus expected = cancel ? OKNFCLiteChangePinStatusCancel : allowed ? OKNFCLiteChangePinStatusSuccess : failRead ? OKNFCLiteChangePinStatusError : OKNFCLiteChangePinStatusSNNotMatch;
    expect(result == expected && completions == 1, @"PIN identity returns the expected status exactly once");
    expect(card.reads == (!cancel && guarded ? 1 : 0), @"Guard refreshes identity before PIN dispatch");
    expect(card.writes == (allowed ? 1 : 0), @"A mismatching, unreadable or cancelled card is never mutated");
    expect([manager valueForKey:@"expectedSerial"] == nil, @"PIN identity is cleared after dispatch or cancellation");
}

static void checkInput(NSString *input, BOOL v2, BOOL overwrite, BOOL valid, BOOL starts) {
    [messages removeAllObjects];
    TestManager *manager = [TestManager new];
    FakeCard *card = [FakeCard new];
    card.version = v2 ? OKNFCLiteVersionV2 : OKNFCLiteVersionV1;
    card.status = overwrite ? OKNFCLiteStatusActivated : OKNFCLiteStatusNewCard;
    card.SN = @"PRIVATE-SERIAL-WALLET-IDENTITY";
    manager.card = card;
    // An existing activated card must not turn a preflight failure into AlreadyInitialized.
    [manager setValue:card forKey:@"lite"];
    __block NSUInteger completions = 0;
    __block OKNFCLiteSetMncStatus result = OKNFCLiteSetMncStatusCancel;
    __block BOOL nilCard = NO;
    [manager setMnemonic:input withPin:@"p!N789" overwrite:overwrite expectedSerial:card.SN
               complete:^(OKLiteV1 *lite, OKNFCLiteSetMncStatus status) {
        ++completions; result = status; nilCard = lite == nil;
    }];
    expect(manager.starts == (starts ? 1 : 0), @"Syntax rejection precedes NFC session creation");
    if (starts) [manager nfcSessionComplete:nil];
    expect(completions == 1, @"Exactly one callback completes the operation");
    expect(result == (valid ? OKNFCLiteSetMncStatusSuccess : OKNFCLiteSetMncStatusError), @"Existing result status is preserved");
    expect(nilCard == !valid, @"Input failure has nil card for existing generic error mapping");
    expect(card.reads == (valid ? 1 : 0), @"Input failure precedes identity reads");
    expect(card.writes == (valid ? 1 : 0), @"Input failure dispatches no PIN, reset or backup operation");
    expect([manager valueForKey:@"pin"] == nil && [manager valueForKey:@"exportMnemonic"] == nil &&
           [manager valueForKey:@"expectedSerial"] == nil, @"Rejected/completed input is not retained");
    if (valid) expect([card.received isEqualToString:input], @"Odd/even input reaches existing operation unchanged");
    if (!loggerThrows) {
        NSString *reason = !input ? @"missing" : !input.length ? @"empty" : input.length > 131070 ? @"too_large" : !starts ? @"non_hex" : @"accepted";
        NSString *entry = [NSString stringWithFormat:@"operation=setMnemonic stage=input_preflight_entry inputPresent=%d inputChars=%lu odd=%d versionKnown=0 cardVersion=0 overwrite=%d reason=%@ accepted=%d", input != nil, (unsigned long)input.length, input.length % 2 != 0, overwrite, reason, starts];
        NSMutableArray *expected = [NSMutableArray arrayWithObject:entry];
        if (starts) [expected addObject:[NSString stringWithFormat:@"operation=setMnemonic stage=input_preflight_detected inputPresent=1 inputChars=%lu odd=%d versionKnown=1 cardVersion=%d overwrite=%d reason=%@ accepted=%d", (unsigned long)input.length, input.length % 2 != 0, v2 ? 2 : 1, overwrite, valid ? @"accepted" : @"too_large", valid]];
        expect([messages isEqualToArray:expected], @"Every preflight field is allowlisted with actual decision/count/version");
    }
    for (NSString *line in messages) {
        for (NSString *secret in @[@"p!N789", card.SN, @"PRIVATE-SEED", @"/path/user@host"])
            expect([line rangeOfString:secret].location == NSNotFound, @"No sensitive fixture or path appears in native logs");
        if (input.length >= 16) expect([line rangeOfString:input].location == NSNotFound, @"No encoded backup appears in logs");
    }
}

int main(int argc, const char *argv[]) {
    @autoreleasepool {
        messages = [NSMutableArray array];
        for (Class cardClass in @[[OKLiteV1 class], [OKLiteV2 class]]) {
            checkCardInfo(cardClass, OKNFCLiteStatusNewCard, YES);
            checkCardInfo(cardClass, OKNFCLiteStatusActivated, NO);
        }
        for (NSNumber *v2 in @[@NO, @YES]) {
            checkPinIdentity(v2.boolValue, @"verified-card", @"verified-card", NO, NO);
            checkPinIdentity(v2.boolValue, @"verified-card", @"other-card", NO, NO);
            checkPinIdentity(v2.boolValue, @"verified-card", @"", NO, NO);
            checkPinIdentity(v2.boolValue, @"", @"", NO, NO);
            checkPinIdentity(v2.boolValue, @"", @"verified-card", NO, NO);
            checkPinIdentity(v2.boolValue, @"verified-card", @"verified-card", YES, NO);
            checkPinIdentity(v2.boolValue, nil, @"other-card", NO, NO);
            checkPinIdentity(v2.boolValue, @"verified-card", @"verified-card", NO, YES);
        }
        NSString *nul = [NSString stringWithCharacters:(unichar[]){'1', 0} length:2];
        for (NSNumber *v2 in @[@NO, @YES]) {
            NSUInteger maxDigits = (v2.boolValue ? 65511 : 65535) * 2;
            for (NSNumber *overwrite in @[@NO, @YES]) {
                checkInput(nil, v2.boolValue, overwrite.boolValue, NO, NO);
                for (NSString *input in @[@"", @"g", @"12g", @"0x1", @" 1", @"+1", @"1\n", @"１２", nul])
                    checkInput(input, v2.boolValue, overwrite.boolValue, NO, NO);
                NSString *oversized = [@"" stringByPaddingToLength:maxDigits + 1 withString:@"a" startingAtIndex:0];
                checkInput(oversized, v2.boolValue, overwrite.boolValue, NO, v2.boolValue);
                for (NSString *input in @[@"1", @"AbC", @"12", @"12345678901234567890123456789012345678900000001",
                    [@"" stringByPaddingToLength:maxDigits - 1 withString:@"a" startingAtIndex:0],
                    [@"" stringByPaddingToLength:maxDigits withString:@"a" startingAtIndex:0]])
                    checkInput(input, v2.boolValue, overwrite.boolValue, YES, YES);
            }
        }
        checkInput(@"PRIVATE-SEED-CERT-KEY\n/path/user@host", YES, YES, NO, NO);
        loggerThrows = YES;
        checkInput(@"PRIVATE-INVALID", YES, YES, NO, NO);
        checkInput(@"12345678901234567890123456789012345678900000001", YES, NO, YES, YES);
        loggerThrows = NO;

        NSString *sourceId = [NSString stringWithUTF8String:(const char *)GPChannelSDKCoreVersionString];
        expect([sourceId rangeOfString:@"^source-v1:[0-9a-f]{64}$" options:NSRegularExpressionSearch].location != NSNotFound, @"Build identity comes from linked native core symbol");
        NSMutableString *displayedSourceId = [sourceId mutableCopy];
        for (NSUInteger offset = 66; offset > 10; offset -= 8) [displayedSourceId insertString:@"-" atIndex:offset];
        NSMutableArray<NSString *> *capturedBuilds = [NSMutableArray arrayWithObject:sourceId];
        NSString *backup = @"12345678901234567890123456789012345678900000001";
        NSArray *cases = @[@[@NO, backup, @0], @[@NO, @"PRIVATE-KEY-SEED\n/path/user@host", @3], @[@YES, backup, @6]];
        for (NSArray *test in cases) {
            [messages removeAllObjects];
            BOOL safe = [test[0] boolValue];
            NFCISO7816APDU *apdu = [OKNFCBridge buildAPDUWith_cla:0x80 ins:0x3b p1:0 p2:0 data:test[1] encrypt:safe];
            NSInteger code = [test[2] integerValue];
            expect((apdu != nil) == (code == 0), @"Actual SDK build result is preserved");
            NSString *expected = [NSString stringWithFormat:@"stage=command_build nativeSourceId=%@ safe=%d sdkReturnCode=%ld outputPresent=%d", displayedSourceId, safe, (long)code, code == 0];
            expect([messages isEqualToArray:@[expected]], @"Actual bridge/LCLogger path contains only exact build metadata");
            [capturedBuilds addObjectsFromArray:messages];
            loggerThrows = YES;
            apdu = [OKNFCBridge buildAPDUWith_cla:0x80 ins:0x3b p1:0 p2:0 data:test[1] encrypt:safe];
            expect((apdu != nil) == (code == 0), @"Logger exception must preserve actual SDK result");
            loggerThrows = NO;
        }
        if (argc == 2) {
            NSString *path = [NSString stringWithUTF8String:argv[1]];
            expect([[capturedBuilds componentsJoinedByString:@"\n"] writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:NULL], @"Captured bridge metadata reaches the real logger regression");
        }
        printf("ManagerInput: %lu assertions passed\n", (unsigned long)assertions);
    }
}
