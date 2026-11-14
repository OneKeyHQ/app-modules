package io.intercom.android.sdk.m5.preview.ui;

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
   "SMAP\nMediaPickerButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$MediaPickerButtonKt$lambda-2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,258:1\n1047#2,6:259\n*S KotlinDebug\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$MediaPickerButtonKt$lambda-2$1\n*L\n252#1:259,6\n*E\n"
)
final class ComposableSingletons$MediaPickerButtonKt$RecoveredInner7a24458254dd4cd9$1 implements Function2 {
   public static final ComposableSingletons$MediaPickerButtonKt$RecoveredInner7a24458254dd4cd9$1 INSTANCE = new ComposableSingletons$MediaPickerButtonKt$RecoveredInner7a24458254dd4cd9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C251@9802L12,251@9785L89:MediaPickerButton.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1873711459,
               var2,
               -1,
               "io.intercom.android.sdk.m5.preview.ui.ComposableSingletons$MediaPickerButtonKt.lambda-2.<anonymous> (MediaPickerButton.kt:251)"
            );
         }

         var1.startReplaceGroup(-1005435131);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaPickerButton.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10000;
         if (var7 == Composer.Companion.getEmpty()) {
            int var9 = 0;
            Object var10 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaPickerButtonKt$RecoveredInner7a24458254dd4cd9$1.invoke$lambda$1$lambda$0());
            var4.updateRememberedValue(var10);
            var10000 = var10;
         } else {
            var10000 = var7;
         }

         Function0 var3 = (Function0)var10000;
         var1.endReplaceGroup();
         ButtonKt.Button(
            var3,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            ComposableSingletons$MediaPickerButtonKt.INSTANCE.recovered$b26556cd5c386b55(),
            var1,
            805306374,
            510
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
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
