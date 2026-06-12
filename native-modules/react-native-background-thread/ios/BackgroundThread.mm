#import "BackgroundThread.h"
#import "BackgroundThreadManager.h"
#import "BTLogger.h"


@implementation BackgroundThread


- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    [BTLogger info:@"BackgroundThread module initialized"];
    return std::make_shared<facebook::react::NativeBackgroundThreadSpecJSI>(params);
}

- (void)startBackgroundRunner {
    [[BackgroundThreadManager sharedInstance] startBackgroundRunner];
}

- (void)startBackgroundRunnerWithEntryURL:(NSString *)entryURL {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    [manager startBackgroundRunnerWithEntryURL:entryURL];
}

- (void)installSharedBridge {
    // On iOS, SharedBridge is installed from AppDelegate's hostDidStart: callback
    // via +[BackgroundThreadManager installSharedBridgeInMainRuntime:].
    // This is a no-op here — kept for API parity with Android.
    [BTLogger info:@"installSharedBridge called (no-op on iOS, installed from AppDelegate)"];
}

- (void)loadSegmentInBackground:(double)segmentId
                           path:(NSString *)path
                        resolve:(RCTPromiseResolveBlock)resolve
                         reject:(RCTPromiseRejectBlock)reject {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    [manager registerSegmentInBackground:@((int)segmentId)
                                    path:path
                              completion:^(NSError * _Nullable error) {
        if (error) {
            // Fix 1/E: map the manager's DISTINCT NSError.code to its own JS
            // reject code (matching SplitBundleLoader's main-runtime mapping and
            // the shared error-code contract) so JS can classify
            // retryable-vs-fatal. EVERY EBgMgrSegmentEvalError case is mapped
            // EXPLICITLY here — there is no silent `default → NO_RUNTIME` that
            // could MISCLASSIFY a fatal failure (e.g. a missing segment file)
            // as a transient runtime-not-ready that gets retried/masked. The old
            // code both collapsed bg failures into one opaque
            // `BG_SEGMENT_LOAD_ERROR` and (before that) let raw codes 1/2 fall
            // through `default` to retryable NO_RUNTIME.
            NSString *rejectCode;
            switch ((EBgMgrSegmentEvalError)error.code) {
                case EBgMgrSegmentEvalErrorNotStarted:
                case EBgMgrSegmentEvalErrorNilInstance:
                    // Bg runtime not started / RCTInstance nil — TRANSIENT: the
                    // bg host simply wasn't ready yet; a later attempt may win.
                    rejectCode = @"SPLIT_BUNDLE_NO_RUNTIME";        // retryable
                    break;
                case EBgMgrSegmentEvalErrorFileNotFound:
                    // Segment file missing — FATAL packaging/OTA corruption.
                    rejectCode = @"SPLIT_BUNDLE_NOT_FOUND";         // fatal
                    break;
                case EBgMgrSegmentEvalErrorIvarMissing:
                    // `_rctInstance` ivar reflection failed — STRUCTURAL/PERMANENT
                    // (an RN bump renamed the private field). Retrying is futile.
                    rejectCode = @"SPLIT_BUNDLE_NATIVE_UNAVAILABLE"; // fatal
                    break;
                case EBgMgrSegmentEvalErrorIORead:
                    rejectCode = @"SPLIT_BUNDLE_IO_ERROR";          // fatal
                    break;
                case EBgMgrSegmentEvalErrorEvalThrow:
                    rejectCode = @"SPLIT_BUNDLE_EVAL_ERROR";        // fatal (segment bug)
                    break;
                case EBgMgrSegmentEvalErrorTimeout:
                    rejectCode = @"SPLIT_BUNDLE_TIMEOUT";           // retryable
                    break;
                default:
                    // Defensive LAST RESORT only: every real EBgMgrSegmentEvalError
                    // case is handled above, so reaching here means the manager
                    // emitted an unmapped code (a bug). Choose retryable
                    // NO_RUNTIME so a stale/unknown native build degrades safely
                    // rather than permanently poisoning a segment — but log it
                    // loudly so the unmapped code is caught and named.
                    [BTLogger warn:[NSString stringWithFormat:@"[SplitBundle] bg loadSegment: UNMAPPED EBgMgrSegmentEvalError code=%ld — defaulting to retryable NO_RUNTIME. This is a native bug: add an explicit case.", (long)error.code]];
                    rejectCode = @"SPLIT_BUNDLE_NO_RUNTIME";        // retryable (defensive)
                    break;
            }
            reject(rejectCode, error.localizedDescription, error);
        } else {
            resolve(nil);
        }
    }];
}

- (void)restart:(NSString *)mode
         reason:(NSString *)reason
        resolve:(RCTPromiseResolveBlock)resolve
         reject:(RCTPromiseRejectBlock)reject {
    BackgroundThreadManager *manager = [BackgroundThreadManager sharedInstance];
    [manager restartWithMode:mode
                      reason:reason
                  completion:^(NSError * _Nullable error) {
        if (error) {
            reject(@"BG_RESTART_ERROR", error.localizedDescription, error);
        } else {
            resolve(nil);
        }
    }];
}

+ (NSString *)moduleName
{
  return @"BackgroundThread";
}

@end
