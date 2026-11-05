package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nUploadSizeLimitDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadSizeLimitDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadSizeLimitDialogKt$UploadSizeLimitDialog$3\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,67:1\n1047#2,6:68\n*S KotlinDebug\n*F\n+ 1 UploadSizeLimitDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadSizeLimitDialogKt$UploadSizeLimitDialog$3\n*L\n40#1:68,6\n*E\n"
)
final class UploadSizeLimitDialogKt$UploadSizeLimitDialog$3 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDismiss;

   UploadSizeLimitDialogKt$UploadSizeLimitDialog$3(Function0 var1) {
      this.$onDismiss = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C39@1225L51,38@1187L364:UploadSizeLimitDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               582096881, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadSizeLimitDialog.<anonymous> (UploadSizeLimitDialog.kt:38)"
            );
         }

         var1.startReplaceGroup(-891282052);
         ComposerKt.sourceInformation(var1, "CC(remember):UploadSizeLimitDialog.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changed(this.$onDismiss);
         Function0 var6 = this.$onDismiss;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var10000;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10000 = var8;
         } else {
            int var10 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_501_0 = var6;
            Object var11 = ((kotlin.jvm.functions.Function0) () -> UploadSizeLimitDialogKt$UploadSizeLimitDialog$3.invoke$lambda$1$lambda$0(recoveredCapture_501_0));
            var4.updateRememberedValue(var11);
            var10000 = var11;
         }

         Function0 var3 = (Function0)var10000;
         var1.endReplaceGroup();
         ButtonKt.TextButton(
            var3,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$UploadSizeLimitDialogKt.INSTANCE.recovered$b26556cd5c386b55(),
            var1,
            805306368,
            510
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
