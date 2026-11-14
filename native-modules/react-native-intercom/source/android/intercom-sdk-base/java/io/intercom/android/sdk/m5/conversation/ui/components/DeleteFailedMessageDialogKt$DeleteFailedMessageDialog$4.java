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
   "SMAP\nDeleteFailedMessageDialog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeleteFailedMessageDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,66:1\n1047#2,6:67\n*S KotlinDebug\n*F\n+ 1 DeleteFailedMessageDialog.kt\nio/intercom/android/sdk/m5/conversation/ui/components/DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4\n*L\n31#1:67,6\n*E\n"
)
final class DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4 implements Function2 {
   // $VF: synthetic field
   final Function0 $onDelete;
   // $VF: synthetic field
   final Function0 $onDismiss;

   DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4(Function0 var1, Function0 var2) {
      this.$onDelete = var1;
      this.$onDismiss = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C30@1013L82,29@975L392:DeleteFailedMessageDialog.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1313773689,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.DeleteFailedMessageDialog.<anonymous> (DeleteFailedMessageDialog.kt:29)"
            );
         }

         var1.startReplaceGroup(1501166977);
         ComposerKt.sourceInformation(var1, "CC(remember):DeleteFailedMessageDialog.kt#9igjgp");
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
            final kotlin.jvm.functions.Function0 recoveredCapture_430_0 = var6;
            final kotlin.jvm.functions.Function0 recoveredCapture_430_1 = var7;
            Object var12 = ((kotlin.jvm.functions.Function0) () -> DeleteFailedMessageDialogKt$DeleteFailedMessageDialog$4.invoke$lambda$1$lambda$0(recoveredCapture_430_0, recoveredCapture_430_1));
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
            ComposableSingletons$DeleteFailedMessageDialogKt.INSTANCE.recovered$b26556cd5c386b55(),
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
