package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPoweredByBadge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$PoweredByBadgeKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,122:1\n1047#2,6:123\n*S KotlinDebug\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$PoweredByBadgeKt$lambda-1$1\n*L\n102#1:123,6\n*E\n"
)
final class ComposableSingletons$PoweredByBadgeKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$PoweredByBadgeKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$PoweredByBadgeKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@3488L2,98@3339L166:PoweredByBadge.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -979615037, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$PoweredByBadgeKt.lambda-1.<anonymous> (PoweredByBadge.kt:98)"
            );
         }

         PoweredBy$PoweredByIconType var3 = PoweredBy$PoweredByIconType.INTERCOM;
         String var10000 = "Powered by Intercom";
         PoweredBy$PoweredByIconType var10001 = var3;
         var1.startReplaceGroup(1481229056);
         ComposerKt.sourceInformation(var1, "CC(remember):PoweredByBadge.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         Object var10002;
         if (var8 == Composer.Companion.getEmpty()) {
            PoweredBy$PoweredByIconType var12 = var3;
            String var11 = "Powered by Intercom";
            int var10 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PoweredByBadgeKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var11;
            var10001 = var12;
            Object var14 = var13;
            var5.updateRememberedValue(var14);
            var10002 = var14;
         } else {
            var10002 = var8;
         }

         Function0 var4 = (Function0)var10002;
         var1.endReplaceGroup();
         PoweredByBadgeKt.recovered$9b47502c50dedaf9(var10000, var10001, var4, null, 0L, 0L, var1, 438, 56);
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
