package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPreviewActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPreviewActivity.kt\nio/intercom/android/sdk/m5/preview/ui/IntercomPreviewActivity$setupPreviewUI$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,190:1\n1047#2,6:191\n1047#2,6:197\n1047#2,6:203\n*S KotlinDebug\n*F\n+ 1 IntercomPreviewActivity.kt\nio/intercom/android/sdk/m5/preview/ui/IntercomPreviewActivity$setupPreviewUI$1$1\n*L\n146#1:191,6\n149#1:197,6\n152#1:203,6\n*E\n"
)
final class IntercomPreviewActivity$setupPreviewUI$1$1 implements Function2 {
   // $VF: synthetic field
   final IntercomPreviewActivity this$0;

   IntercomPreviewActivity$setupPreviewUI$1$1(IntercomPreviewActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C145@6050L89,148@6177L77,151@6290L89,142@5902L496:IntercomPreviewActivity.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1919074696,
               var2,
               -1,
               "io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity.setupPreviewUI.<anonymous>.<anonymous> (IntercomPreviewActivity.kt:142)"
            );
         }

         Object var10000 = null;
         IntercomPreviewArgs var10001 = IntercomPreviewActivity.access$getPreviewData(this.this$0);
         PreviewViewModel var10002 = IntercomPreviewActivity.access$getViewModel(this.this$0);
         var1.startReplaceGroup(455876692);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomPreviewActivity.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.this$0);
         IntercomPreviewActivity var6 = this.this$0;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var10003;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10003 = (Function1)var8;
         } else {
            PreviewViewModel var14 = var10002;
            IntercomPreviewArgs var13 = var10001;
            Object var12 = null;
            int var10 = 0;
            final io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity recoveredCapture_1130_0 = var6;
            Function1 var15 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1130_0) -> IntercomPreviewActivity$setupPreviewUI$1$1.invoke$lambda$1$lambda$0(recoveredCapture_1130_0, (java.util.List)recoveredArgument_1130_0));
            var10000 = var12;
            var10001 = var13;
            var10002 = var14;
            Object var11 = var15;
            var4.updateRememberedValue(var11);
            var10003 = (Function1)var11;
         }

         Function1 var3 = (kotlin.jvm.functions.Function1) var10003;
         var1.endReplaceGroup();
         var10003 = var3;
         var1.startReplaceGroup(455880744);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomPreviewActivity.kt#9igjgp");
         var4 = var1;
         var5 = var1.changedInstance(this.this$0);
         var6 = this.this$0;
         var7 = 0;
         var8 = var4.rememberedValue();
         var9 = 0;
         Object var10004;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10004 = (Function1)var8;
         } else {
            Function1 var42 = var3;
            PreviewViewModel var40 = var10002;
            IntercomPreviewArgs var38 = var10001;
            Object var36 = var10000;
            int var32 = 0;
            final io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity recoveredCapture_1128_0 = var6;
            Function1 var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1128_0) -> IntercomPreviewActivity$setupPreviewUI$1$1.invoke$lambda$3$lambda$2(recoveredCapture_1128_0, (io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile)recoveredArgument_1128_0));
            var10000 = var36;
            var10001 = var38;
            var10002 = var40;
            var10003 = var42;
            Object var34 = var16;
            var4.updateRememberedValue(var34);
            var10004 = (Function1)var34;
         }

         var3 = (kotlin.jvm.functions.Function1) var10004;
         var1.endReplaceGroup();
         var10004 = var3;
         var1.startReplaceGroup(455884372);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomPreviewActivity.kt#9igjgp");
         var4 = var1;
         var5 = var1.changedInstance(this.this$0);
         var6 = this.this$0;
         var7 = 0;
         var8 = var4.rememberedValue();
         var9 = 0;
         Object var10005;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10005 = var8;
         } else {
            Function1 var44 = var3;
            Function1 var43 = (kotlin.jvm.functions.Function1) var10003;
            PreviewViewModel var41 = var10002;
            IntercomPreviewArgs var39 = var10001;
            Object var37 = var10000;
            int var33 = 0;
            final io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity recoveredCapture_1129_0 = var6;
            Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1129_0) -> IntercomPreviewActivity$setupPreviewUI$1$1.invoke$lambda$5$lambda$4(recoveredCapture_1129_0, (java.util.List)recoveredArgument_1129_0));
            var10000 = var37;
            var10001 = var39;
            var10002 = var41;
            var10003 = var43;
            var10004 = var44;
            Object var35 = var17;
            var4.updateRememberedValue(var35);
            var10005 = var35;
         }

         var3 = (Function1)var10005;
         var1.endReplaceGroup();
         PreviewRootScreenKt.PreviewRootScreen((Modifier)var10000, var10001, var10002, (kotlin.jvm.functions.Function1) var10003, (kotlin.jvm.functions.Function1) var10004, var3, var1, 0, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(IntercomPreviewActivity var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "uris");
      IntercomPreviewActivity.access$finishWithResult(var0, -1, var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(IntercomPreviewActivity var0, IntercomPreviewFile var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      IntercomPreviewActivity.access$getViewModel(var0).onDeleteClicked$intercom_sdk_base_release(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(IntercomPreviewActivity var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "uris");
      IntercomPreviewActivity.access$finishWithResult(var0, -1, var1);
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
