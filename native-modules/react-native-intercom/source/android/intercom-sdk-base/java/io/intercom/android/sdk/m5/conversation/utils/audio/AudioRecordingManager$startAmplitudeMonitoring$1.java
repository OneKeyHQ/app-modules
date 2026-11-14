package io.intercom.android.sdk.m5.conversation.utils.audio;

import android.media.MediaRecorder;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "AudioRecordingManager.kt",
   l = 364,
   i = 0,
   s = "L$0",
   n = "$this$launch",
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.conversation.utils.audio.AudioRecordingManager$startAmplitudeMonitoring$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class AudioRecordingManager$startAmplitudeMonitoring$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final MediaRecorder $recorder;
   // $VF: synthetic field
   final AudioRecordingManager this$0;

   AudioRecordingManager$startAmplitudeMonitoring$1(MediaRecorder var1, AudioRecordingManager var2, Continuation var3) {
      super(2, var3);
      this.$recorder = var1;
      this.this$0 = var2;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      CoroutineScope var2;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var2 = (CoroutineScope)this.L$0;
            break;
         case 1:
            var2 = (CoroutineScope)this.L$0;
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (CoroutineScopeKt.isActive(var2)) {
         try {
            int var3 = this.$recorder.getMaxAmplitude();
            float var4 = AudioRecordingManager.access$normalizeAmplitude(this.this$0, var3);
            AudioRecordingManager.access$get_amplitudeLevel$p(this.this$0).setValue(Boxing.boxFloat(var4));
         } catch (Exception var6) {
            AudioRecordingManager.access$get_amplitudeLevel$p(this.this$0).setValue(Boxing.boxFloat(0.0F));
         }

         Continuation var10001 = this;
         this.L$0 = var2;
         this.label = 1;
         if (DelayKt.delay(60L, var10001) == var5) {
            return var5;
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      AudioRecordingManager$startAmplitudeMonitoring$1 var3 = new AudioRecordingManager$startAmplitudeMonitoring$1(this.$recorder, this.this$0, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AudioRecordingManager$startAmplitudeMonitoring$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
