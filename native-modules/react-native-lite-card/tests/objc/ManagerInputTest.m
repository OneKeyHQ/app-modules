#import "OKNFCManager.h"
#import "OKLiteV1.h"
#import "OKLiteV2.h"
#import "OKLiteCommandModal.h"
#import "OKNFCUtility.h"
#import "LCLogger.h"
#import "OKTools.h"

static NSUInteger assertions;
static void expect(BOOL value, NSString *scenario) {
    if (!value) { NSLog(@"FAILED: %@", scenario); abort(); }
    ++assertions;
}

// The production manager owns validation, retention, callbacks and operation dispatch.
// These card and NFC adapters never construct commands or touch a device.
@implementation NFCISO7816APDU
@end
@implementation NFCTagReaderSession
- (instancetype)initWithPollingOption:(NSInteger)option delegate:(id)delegate queue:(dispatch_queue_t)queue { return [super init]; }
- (void)beginSession { abort(); }
- (void)invalidateSession {}
- (void)invalidateSessionWithErrorMessage:(NSString *)message {}
- (void)connectToTag:(id<NFCTag>)tag completionHandler:(void (^)(NSError *))completion { abort(); }
@end
@implementation LCLogger
+ (void)info:(NSString *)message {}
+ (void)debug:(NSString *)message {}
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
@implementation OKLiteV1
@end
@implementation OKLiteV2
@end

@interface FakeCard : OKLiteV1
@property (nonatomic, assign) NSUInteger reads;
@property (nonatomic, assign) NSUInteger writes;
@property (nonatomic, copy) NSString *received;
@end
@implementation FakeCard
- (BOOL)syncLiteInfo { ++self.reads; return YES; }
- (void)setMnemonic:(NSString *)input withPin:(NSString *)pin overwrite:(BOOL)overwrite complete:(SetMnemonicCallback)complete {
    ++self.writes;
    self.received = input;
    complete(self, OKNFCLiteSetMncStatusSuccess);
}
@end

@interface OKNFCManager (TestEntryPoints)
- (void)nfcSessionComplete:(NFCTagReaderSession *)session;
- (void)beginNewNFCSession;
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

static void checkInput(NSString *input, BOOL v2, BOOL overwrite, BOOL valid, BOOL starts) {
    TestManager *manager = [TestManager new];
    FakeCard *card = [FakeCard new];
    card.version = v2 ? OKNFCLiteVersionV2 : OKNFCLiteVersionV1;
    card.status = overwrite ? OKNFCLiteStatusActivated : OKNFCLiteStatusNewCard;
    card.SN = @"synthetic-identity";
    manager.card = card;
    // An existing activated card must not turn a preflight failure into AlreadyInitialized.
    [manager setValue:card forKey:@"lite"];
    __block NSUInteger completions = 0;
    __block OKNFCLiteSetMncStatus result = OKNFCLiteSetMncStatusCancel;
    __block BOOL nilCard = NO;
    [manager setMnemonic:input withPin:@"000000" overwrite:overwrite expectedSerial:@"synthetic-identity"
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
}

int main() {
    @autoreleasepool {
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
        printf("ManagerInput: %lu assertions passed\n", (unsigned long)assertions);
    }
}
