package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.material3.ButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.m5.conversation.data.MediaUploadRepository$UploadError;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nUploadErrorDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt$UploadErrorDialog$5\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,107:1\n1047#2,6:108\n*S KotlinDebug\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt$UploadErrorDialog$5\n*L\n37#1:108,6\n*E\n"
)
final class UploadErrorDialogKt$UploadErrorDialog$5 implements Function2 {
   // $VF: synthetic field
   final MediaUploadRepository$UploadError $error;
   // $VF: synthetic field
   final Function0 $onRetry;
   // $VF: synthetic field
   final Function0 $onDismiss;

   UploadErrorDialogKt$UploadErrorDialog$5(MediaUploadRepository$UploadError var1, Function0 var2, Function0 var3) {
      this.$error = var1;
      this.$onRetry = var2;
      this.$onDismiss = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C36@1258L93,35@1216L438:UploadErrorDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1477542978, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialog.<anonymous> (UploadErrorDialog.kt:34)"
            );
         }

         if (this.$error.getCanRetry()) {
            var1.startReplaceGroup(1143582868);
            ComposerKt.sourceInformation(var1, "CC(remember):UploadErrorDialog.kt#9igjgp");
            Composer var4 = var1;
            boolean var5 = var1.changed(this.$onRetry) | var1.changed(this.$onDismiss);
            Function0 var6 = this.$onRetry;
            Function0 var7 = this.$onDismiss;
            int var8 = 0;
            Object var9 = var4.rememberedValue();
            int var10 = 0;
            Object var10000;
            if (!var5 && var9 != Composer.Companion.getEmpty()) {
               var10000 = var9;
            } else {
               int var11 = 0;
               final kotlin.jvm.functions.Function0 recoveredCapture_495_0 = var6;
               final kotlin.jvm.functions.Function0 recoveredCapture_495_1 = var7;
               Object var12 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt$UploadErrorDialog$5.invoke$lambda$1$lambda$0(recoveredCapture_495_0, recoveredCapture_495_1));
               var4.updateRememberedValue(var12);
               var10000 = var12;
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
               ComposableSingletons$UploadErrorDialogKt.INSTANCE.recovered$b26556cd5c386b55(),
               var1,
               805306368,
               510
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(Function0 var0, Function0 var1) {
      var0.invoke();
      var1.invoke();
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
