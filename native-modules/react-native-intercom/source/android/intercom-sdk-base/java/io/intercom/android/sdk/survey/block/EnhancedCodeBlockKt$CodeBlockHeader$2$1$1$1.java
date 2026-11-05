package io.intercom.android.sdk.survey.block;

import android.content.ClipData;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.hapticfeedback.HapticFeedback;
import androidx.compose.ui.hapticfeedback.HapticFeedbackType;
import androidx.compose.ui.platform.ClipEntry;
import androidx.compose.ui.platform.Clipboard;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "EnhancedCodeBlock.kt",
   l = 281,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.block.EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final Clipboard $clipboard;
   // $VF: synthetic field
   final String $rawCode;
   // $VF: synthetic field
   final HapticFeedback $hapticFeedback;
   // $VF: synthetic field
   final MutableState $copied$delegate;

   EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1(Clipboard var1, String var2, HapticFeedback var3, MutableState var4, Continuation var5) {
      super(2, var5);
      this.$clipboard = var1;
      this.$rawCode = var2;
      this.$hapticFeedback = var3;
      this.$copied$delegate = var4;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);

            Object var7;
            try {
               Clipboard recoveredClipboard = this.$clipboard;
               ClipData var10003 = ClipData.newPlainText("code", this.$rawCode);
               Intrinsics.checkNotNullExpressionValue(var10003, "newPlainText(...)");
               ClipEntry var10001 = new ClipEntry(var10003);
               Continuation var10002 = this;
               this.label = 1;
               var7 = recoveredClipboard.setClipEntry(var10001, var10002);
            } catch (SecurityException var5) {
               EnhancedCodeBlockKt.access$getTWIG$p().e("Clipboard access denied due to user restrictions: " + var5.getMessage());
               return Unit.INSTANCE;
            }

            if (var7 == var3) {
               return var3;
            }
            break;
         case 1:
            try {
               ResultKt.throwOnFailure(var1);
               break;
            } catch (SecurityException var6) {
               EnhancedCodeBlockKt.access$getTWIG$p().e("Clipboard access denied due to user restrictions: " + var6.getMessage());
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         this.$hapticFeedback.recovered$056acba0544ddc17(HapticFeedbackType.Companion.recovered$ec62377ccea61570());
         EnhancedCodeBlockKt.access$CodeBlockHeader_RIQooxk$lambda$13(this.$copied$delegate, true);
      } catch (SecurityException var4) {
         EnhancedCodeBlockKt.access$getTWIG$p().e("Clipboard access denied due to user restrictions: " + var4.getMessage());
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1(this.$clipboard, this.$rawCode, this.$hapticFeedback, this.$copied$delegate, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((EnhancedCodeBlockKt$CodeBlockHeader$2$1$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
