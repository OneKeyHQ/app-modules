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
   "SMAP\nUploadErrorDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt$UploadErrorDialog$6\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,107:1\n1047#2,6:108\n*S KotlinDebug\n*F\n+ 1 UploadErrorDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/UploadErrorDialogKt$UploadErrorDialog$6\n*L\n52#1:108,6\n*E\n"
)
final class UploadErrorDialogKt$UploadErrorDialog$6 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDelete;
   // $VF: synthetic field
   final Function0 $onDismiss;

   UploadErrorDialogKt$UploadErrorDialog$6(Function0 var1, Function0 var2) {
      this.$onDelete = var1;
      this.$onDismiss = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C51@1756L82,50@1718L392:UploadErrorDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2126135748, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UploadErrorDialog.<anonymous> (UploadErrorDialog.kt:50)"
            );
         }

         var1.startReplaceGroup(1143598793);
         ComposerKt.sourceInformation(var1, "CC(remember):UploadErrorDialog.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changed(this.$onDelete) | var1.changed(this.$onDismiss);
         Function0 var6 = this.$onDelete;
         Function0 var7 = this.$onDismiss;
         int var8 = 0;
         Object var9 = var4.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (!var5 && var9 != Composer.Companion.getEmpty()) {
            var10000 = var9;
         } else {
            int var11 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_496_0 = var6;
            final kotlin.jvm.functions.Function0 recoveredCapture_496_1 = var7;
            Object var12 = ((kotlin.jvm.functions.Function0) () -> UploadErrorDialogKt$UploadErrorDialog$6.invoke$lambda$1$lambda$0(recoveredCapture_496_0, recoveredCapture_496_1));
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
            ComposableSingletons$UploadErrorDialogKt.INSTANCE.recovered$6532409fab43c00f(),
            var1,
            805306368,
            510
         );
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
