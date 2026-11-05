#import <Foundation/Foundation.h>

@class NFCISO7816APDU;
@protocol NFCISO7816Tag;
@protocol NFCTag <NSObject>
- (id<NFCISO7816Tag>)asNFCISO7816Tag;
@end
@protocol NFCISO7816Tag <NFCTag>
- (void)sendCommandAPDU:(NFCISO7816APDU *)command completionHandler:(void (^)(NSData *, uint8_t, uint8_t, NSError *))completion;
@end
@protocol NFCTagReaderSessionDelegate <NSObject>
@end
@interface NFCISO7816APDU : NSObject
- (instancetype)initWithData:(NSData *)data;
@end
@interface NFCTagReaderSession : NSObject
@property (nonatomic, strong) id<NFCTag> connectedTag;
@property (nonatomic, copy) NSString *alertMessage;
- (instancetype)initWithPollingOption:(NSInteger)option delegate:(id)delegate queue:(dispatch_queue_t)queue;
- (void)beginSession;
- (void)invalidateSession;
- (void)invalidateSessionWithErrorMessage:(NSString *)message;
- (void)connectToTag:(id<NFCTag>)tag completionHandler:(void (^)(NSError *))completion;
@end
static const NSInteger NFCPollingISO14443 = 1;
static NSString *const NFCErrorDomain = @"CoreNFC";
