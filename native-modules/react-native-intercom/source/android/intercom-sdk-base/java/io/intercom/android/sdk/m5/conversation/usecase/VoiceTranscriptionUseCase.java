package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.errors.VoiceTranscriptionError;
import io.intercom.android.sdk.m5.conversation.errors.VoiceTranscriptionError$FileTooLarge;
import io.intercom.android.sdk.m5.conversation.errors.VoiceTranscriptionError$RecordingFailed;
import io.intercom.android.sdk.m5.conversation.errors.VoiceTranscriptionError$TranscriptionFailed;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Completed;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Error;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Processing;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Recording;
import io.intercom.android.sdk.models.TranscribedText;
import io.intercom.android.sdk.utilities.IoUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001c\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0086@¢\u0006\u0002\u0010\rJ\u001c\u0010\u000e\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0086@¢\u0006\u0002\u0010\rJ\u001c\u0010\u000f\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0086@¢\u0006\u0002\u0010\rJ$\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0082@¢\u0006\u0002\u0010\u0013J\u001e\u0010\u0014\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0002J\u0010\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/VoiceTranscriptionUseCase;",
         "",
         "audioRecordingUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/AudioRecordingUseCase;",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/usecase/AudioRecordingUseCase;Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;)V",
         "startRecording",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "stopRecordingAndTranscribe",
         "cancelRecording",
         "transcribeAudioFile",
         "audioFile",
         "Ljava/io/File;",
         "(Ljava/io/File;Lkotlinx/coroutines/flow/MutableStateFlow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "updateErrorState",
         "error",
         "Lio/intercom/android/sdk/m5/conversation/errors/VoiceTranscriptionError;",
         "isFileSizeValid",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nVoiceTranscriptionUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VoiceTranscriptionUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/VoiceTranscriptionUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,137:1\n230#2,5:138\n230#2,5:143\n230#2,5:148\n230#2,5:153\n230#2,5:158\n230#2,5:163\n*S KotlinDebug\n*F\n+ 1 VoiceTranscriptionUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/VoiceTranscriptionUseCase\n*L\n29#1:138,5\n60#1:143,5\n73#1:148,5\n90#1:153,5\n105#1:158,5\n128#1:163,5\n*E\n"
)
public final class VoiceTranscriptionUseCase {
   @NotNull
   private final AudioRecordingUseCase audioRecordingUseCase;
   @NotNull
   private final ConversationRepository conversationRepository;
   public static final int $stable;

   static { $stable = 8; }

   public VoiceTranscriptionUseCase(@NotNull AudioRecordingUseCase var1, @NotNull ConversationRepository var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "audioRecordingUseCase");
      Intrinsics.checkNotNullParameter(var2, "conversationRepository");
      this.audioRecordingUseCase = var1;
      this.conversationRepository = var2;
   }

   @Nullable
   public final Object startRecording(@NotNull MutableStateFlow var1, @NotNull Continuation var2) {
      try {
         MutableStateFlow var3 = var1;
         int var4 = 0;

         Object var11;
         ConversationClientState var13;
         do {
            var11 = var3.getValue();
            var13 = (ConversationClientState)var11;
            int var7 = 0;
            var13 = ConversationClientState.copy$default(
               (ConversationClientState)var13,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               0,
               0,
               null,
               null,
               false,
               false,
               null,
               VoiceTranscriptionState$Recording.INSTANCE,
               null,
               6291455,
               null
            );
         } while (!var3.compareAndSet(var11, var13));

         Object var9 = this.audioRecordingUseCase.recovered$60440b42dcfc0e3e();
         Throwable var10000 = Result.recovered$a1b169544506bc0c(var9);
         if (var10000 != null) {
            Throwable var10 = var10000;
            Throwable var12 = var10;
            int var14 = 0;
            VoiceTranscriptionError$RecordingFailed var15 = new VoiceTranscriptionError$RecordingFailed(var12);
            this.updateErrorState(var15, var1);
            return Unit.INSTANCE;
         }
      } catch (Exception var8) {
         VoiceTranscriptionError$RecordingFailed var5 = new VoiceTranscriptionError$RecordingFailed(var8);
         this.updateErrorState(var5, var1);
      }

      return Unit.INSTANCE;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Nullable
   public final Object stopRecordingAndTranscribe(@NotNull MutableStateFlow var1, @NotNull Continuation var2) {
      VoiceTranscriptionUseCase recoveredThis = this;
      VoiceTranscriptionUseCase$stopRecordingAndTranscribe$1 var10;
      label72: {
         if (var2 instanceof VoiceTranscriptionUseCase$stopRecordingAndTranscribe$1) {
            var10 = (VoiceTranscriptionUseCase$stopRecordingAndTranscribe$1)var2;
            if ((var10.label & -2147483648) != 0) {
               var10.label -= Integer.MIN_VALUE;
               break label72;
            }
         }

         var10 = new VoiceTranscriptionUseCase$stopRecordingAndTranscribe$1(recoveredThis, var2);
      }

      Object var9 = var10.result;
      Object var11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      RecordedAudioFile var3;
      switch (var10.label) {
         case 0:
            ResultKt.throwOnFailure(var9);

            Object var32;
            try {
               MutableStateFlow var16;
               Object recoveredStopRecordingResult = recoveredThis.audioRecordingUseCase.recovered$d404386be90d0798();
               var32 = Result.recovered$a1b169544506bc0c(recoveredStopRecordingResult);
               if (var32 != null) {
                  Throwable var23 = (Throwable)var32;
                  Throwable var25 = var23;
                  int var29 = 0;
                  recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
                  VoiceTranscriptionError$RecordingFailed var31 = new VoiceTranscriptionError$RecordingFailed(var25);
                  recoveredThis.updateErrorState(var31, var1);
                  return Unit.INSTANCE;
               }

               recoveredStopRecordingResult = recoveredStopRecordingResult;
               ResultKt.throwOnFailure(recoveredStopRecordingResult);
               var3 = (RecordedAudioFile)recoveredStopRecordingResult;
               if (var3.getDurationMs() < 1000L) {
                  IoUtils.safelyDelete(var3.getFile());
                  recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
                  var16 = var1;
                  int var22 = 0;

                  Object var24;
                  ConversationClientState var28;
                  do {
                     var24 = var16.getValue();
                     var28 = (ConversationClientState)var24;
                     int var30 = 0;
                     var28 = ConversationClientState.copy$default(
                        (ConversationClientState)var28,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        0,
                        0,
                        null,
                        null,
                        false,
                        false,
                        null,
                        VoiceTranscriptionState$Idle.INSTANCE,
                        null,
                        6291455,
                        null
                     );
                  } while (!var16.compareAndSet(var24, var28));

                  return Unit.INSTANCE;
               }

               if (!recoveredThis.isFileSizeValid(var3.getFile())) {
                  IoUtils.safelyDelete(var3.getFile());
                  recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
                  VoiceTranscriptionError$FileTooLarge var20 = new VoiceTranscriptionError$FileTooLarge(null, 1, null);
                  recoveredThis.updateErrorState(var20, var1);
                  return Unit.INSTANCE;
               }

               var16 = var1;
               int var5 = 0;

               Object var6;
               ConversationClientState var26;
               do {
                  var6 = var16.getValue();
                  var26 = (ConversationClientState)var6;
                  int var8 = 0;
                  var26 = ConversationClientState.copy$default(
                     (ConversationClientState)var26,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     null,
                     0,
                     0,
                     null,
                     null,
                     false,
                     false,
                     null,
                     VoiceTranscriptionState$Processing.INSTANCE,
                     null,
                     6291455,
                     null
                  );
               } while (!var16.compareAndSet(var6, var26));

               File var10001 = var3.getFile();
               var10.L$0 = recoveredThis;
               var10.L$1 = var1;
               var10.L$2 = var3;
               var10.label = 1;
               var32 = recoveredThis.transcribeAudioFile(var10001, var1, var10);
            } catch (Exception var14) {
               recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
               VoiceTranscriptionError$TranscriptionFailed var15 = new VoiceTranscriptionError$TranscriptionFailed(var14);
               recoveredThis.updateErrorState(var15, var1);
               return Unit.INSTANCE;
            }

            if (var32 == var11) {
               return var11;
            }
            break;
         case 1:
            var3 = (RecordedAudioFile)var10.L$2;
            var1 = (MutableStateFlow)var10.L$1;
            recoveredThis = (VoiceTranscriptionUseCase)var10.L$0;

            try {
               ResultKt.throwOnFailure(var9);
               break;
            } catch (Exception var13) {
               recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
               VoiceTranscriptionError$TranscriptionFailed var4 = new VoiceTranscriptionError$TranscriptionFailed(var13);
               recoveredThis.updateErrorState(var4, var1);
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         IoUtils.safelyDelete(var3.getFile());
         recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
      } catch (Exception var12) {
         recoveredThis.audioRecordingUseCase.cleanupCompletedRecording();
         VoiceTranscriptionError$TranscriptionFailed var19 = new VoiceTranscriptionError$TranscriptionFailed(var12);
         recoveredThis.updateErrorState(var19, var1);
      }

      return Unit.INSTANCE;
   }

   @Nullable
   public final Object cancelRecording(@NotNull MutableStateFlow var1, @NotNull Continuation var2) {
      this.audioRecordingUseCase.cancelRecording();
      MutableStateFlow var3 = var1;
      int var4 = 0;

      Object var5;
      ConversationClientState var8;
      do {
         var5 = var3.getValue();
         var8 = (ConversationClientState)var5;
         int var7 = 0;
         var8 = ConversationClientState.copy$default(
            (ConversationClientState)var8,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            VoiceTranscriptionState$Idle.INSTANCE,
            null,
            6291455,
            null
         );
      } while (!var3.compareAndSet(var5, var8));

      return Unit.INSTANCE;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   private final Object transcribeAudioFile(File var1, MutableStateFlow var2, Continuation var3) {
      VoiceTranscriptionUseCase recoveredThis = this;
      VoiceTranscriptionUseCase$transcribeAudioFile$1 var13;
      label58: {
         if (var3 instanceof VoiceTranscriptionUseCase$transcribeAudioFile$1) {
            var13 = (VoiceTranscriptionUseCase$transcribeAudioFile$1)var3;
            if ((var13.label & -2147483648) != 0) {
               var13.label -= Integer.MIN_VALUE;
               break label58;
            }
         }

         var13 = new VoiceTranscriptionUseCase$transcribeAudioFile$1(recoveredThis, var3);
      }

      Object var12 = var13.result;
      Object var14 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var13.label) {
         case 0:
            ResultKt.throwOnFailure(var12);

            try {
               ConversationRepository var24 = recoveredThis.conversationRepository;
               var13.L$0 = recoveredThis;
               var13.L$1 = var2;
               var13.label = 1;
               var10000 = var24.transcribe(var1, var13);
            } catch (Exception var16) {
               VoiceTranscriptionError$TranscriptionFailed var18 = new VoiceTranscriptionError$TranscriptionFailed(var16);
               recoveredThis.updateErrorState(var18, var2);
               return Unit.INSTANCE;
            }

            if (var10000 == var14) {
               return var14;
            }
            break;
         case 1:
            var2 = (MutableStateFlow)var13.L$1;
            recoveredThis = (VoiceTranscriptionUseCase)var13.L$0;

            try {
               ResultKt.throwOnFailure(var12);
               var10000 = var12;
               break;
            } catch (Exception var17) {
               VoiceTranscriptionError$TranscriptionFailed var5 = new VoiceTranscriptionError$TranscriptionFailed(var17);
               recoveredThis.updateErrorState(var5, var2);
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         NetworkResponse var4 = (NetworkResponse)var10000;
         if (var4 instanceof NetworkResponse$Success) {
            String var6 = ((TranscribedText)((NetworkResponse$Success)var4).getBody()).getText();
            MutableStateFlow var7 = var2;
            int var8 = 0;

            Object var9;
            ConversationClientState var23;
            do {
               var9 = var7.getValue();
               var23 = (ConversationClientState)var9;
               int var11 = 0;
               var23 = ConversationClientState.copy$default(
                  (ConversationClientState)var23,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  0,
                  0,
                  null,
                  null,
                  false,
                  false,
                  null,
                  new VoiceTranscriptionState$Completed(var6),
                  null,
                  6291455,
                  null
               );
            } while (!var7.compareAndSet(var9, var23));
         } else {
            NetworkResponse var21 = var4;
            Throwable var20 = var21 instanceof NetworkResponse$NetworkError
               ? ((NetworkResponse$NetworkError)var4).getError()
               : (var21 instanceof NetworkResponse$ClientError ? ((NetworkResponse$ClientError)var4).getError() : null);
            VoiceTranscriptionError$TranscriptionFailed var22 = new VoiceTranscriptionError$TranscriptionFailed(var20);
            recoveredThis.updateErrorState(var22, var2);
         }
      } catch (Exception var15) {
         VoiceTranscriptionError$TranscriptionFailed var19 = new VoiceTranscriptionError$TranscriptionFailed(var15);
         recoveredThis.updateErrorState(var19, var2);
      }

      return Unit.INSTANCE;
   }

   private final void updateErrorState(VoiceTranscriptionError var1, MutableStateFlow var2) {
      MutableStateFlow var3 = var2;
      int var4 = 0;

      Object var5;
      ConversationClientState var8;
      do {
         var5 = var3.getValue();
         var8 = (ConversationClientState)var5;
         int var7 = 0;
         var8 = ConversationClientState.copy$default(
            (ConversationClientState)var8,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0,
            0,
            null,
            null,
            false,
            false,
            null,
            new VoiceTranscriptionState$Error(var1),
            null,
            6291455,
            null
         );
      } while (!var3.compareAndSet(var5, var8));
   }

   private final boolean isFileSizeValid(File var1) {
      long var2 = var1.length();
      long var4 = 10485760L;
      return var2 <= var4;
   }

   // $VF: synthetic method
   public static final Object access$transcribeAudioFile(VoiceTranscriptionUseCase var0, File var1, MutableStateFlow var2, Continuation var3) {
      return var0.transcribeAudioFile(var1, var2, var3);
   }
}
