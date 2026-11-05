package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-9$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,255:1\n1047#2,6:256\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$TopActionBarKt$lambda-9$1\n*L\n245#1:256,6\n*E\n"
)
final class ComposableSingletons$TopActionBarKt$RecoveredInnerd0e289bb4054aaa2$1 implements Function3 {
   public static final ComposableSingletons$TopActionBarKt$RecoveredInnerd0e289bb4054aaa2$1 INSTANCE = new ComposableSingletons$TopActionBarKt$RecoveredInnerd0e289bb4054aaa2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$TopActionBar");
      ComposerKt.sourceInformation(var2, "C244@8477L2,244@8456L269:TopActionBar.kt#arqjag");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1961378572, var3, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$TopActionBarKt.lambda-9.<anonymous> (TopActionBar.kt:244)"
            );
         }

         var2.startReplaceGroup(1884980936);
         ComposerKt.sourceInformation(var2, "CC(remember):TopActionBar.kt#9igjgp");
         Composer var5 = var2;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10000;
         if (var8 == Composer.Companion.getEmpty()) {
            int var10 = 0;
            Object var11 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$TopActionBarKt$RecoveredInnerd0e289bb4054aaa2$1.invoke$lambda$1$lambda$0());
            var5.updateRememberedValue(var11);
            var10000 = var11;
         } else {
            var10000 = var8;
         }

         Function0 var4 = (Function0)var10000;
         var2.endReplaceGroup();
         IconButtonKt.IconButton(
            var4, null, false, null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$a427d7fccaa25a17(), var2, 1572870, 62
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
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
