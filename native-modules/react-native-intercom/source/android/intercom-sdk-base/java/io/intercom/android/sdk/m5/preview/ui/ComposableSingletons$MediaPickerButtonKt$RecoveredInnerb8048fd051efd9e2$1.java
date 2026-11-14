package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.Indication;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaPickerButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$MediaPickerButtonKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,258:1\n1047#2,6:259\n*S KotlinDebug\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$MediaPickerButtonKt$lambda-3$1\n*L\n248#1:259,6\n*E\n"
)
final class ComposableSingletons$MediaPickerButtonKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$MediaPickerButtonKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$MediaPickerButtonKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C247@9651L15,245@9579L305:MediaPickerButton.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1119994382,
               var2,
               -1,
               "io.intercom.android.sdk.m5.preview.ui.ComposableSingletons$MediaPickerButtonKt.lambda-3.<anonymous> (MediaPickerButton.kt:245)"
            );
         }

         byte var10000 = 1;
         Object var10001 = null;
         Object var10002 = null;
         Object var10003 = null;
         var1.startReplaceGroup(502111849);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var13 = null;
            Object var12 = null;
            Object var11 = null;
            byte var10 = 1;
            int var9 = 0;
            Function1 var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1124_0) -> ComposableSingletons$MediaPickerButtonKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0((java.util.List)recoveredArgument_1124_0));
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            Object var15 = var14;
            var4.updateRememberedValue(var15);
            var10004 = var15;
         } else {
            var10004 = var7;
         }

         Function1 var3 = (Function1)var10004;
         var1.endReplaceGroup();
         MediaPickerButtonKt.MediaPickerButton(
            var10000,
            (Indication)var10001,
            (MediaType)var10002,
            (Set)var10003,
            var3,
            new MediaPickerButtonCTAStyle$TextButton("Open Picker"),
            false,
            null,
            ComposableSingletons$MediaPickerButtonKt.INSTANCE.recovered$1400af5e34386102(),
            var1,
            100687878,
            206
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
