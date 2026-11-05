package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPoweredByBadge.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$PoweredByBadgeKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,122:1\n1047#2,6:123\n*S KotlinDebug\n*F\n+ 1 PoweredByBadge.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$PoweredByBadgeKt$lambda-3$1\n*L\n117#1:123,6\n*E\n"
)
final class ComposableSingletons$PoweredByBadgeKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$PoweredByBadgeKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$PoweredByBadgeKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C116@3856L2,112@3655L218:PoweredByBadge.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1783930351, var2, -1, "io.intercom.android.sdk.m5.components.ComposableSingletons$PoweredByBadgeKt.lambda-3.<anonymous> (PoweredByBadge.kt:112)"
            );
         }

         long var3 = Color.Companion.recovered$562f21ab17d9b4c7();
         PoweredBy$PoweredByIconType var5 = PoweredBy$PoweredByIconType.FIN;
         String var10000 = "Powered by Fin Ai Agent";
         PoweredBy$PoweredByIconType var10001 = var5;
         var1.startReplaceGroup(201377154);
         ComposerKt.sourceInformation(var1, "CC(remember):PoweredByBadge.kt#9igjgp");
         Composer var7 = var1;
         boolean var8 = false;
         int var9 = 0;
         Object var10 = var7.rememberedValue();
         int var11 = 0;
         Object var10002;
         if (var10 == Composer.Companion.getEmpty()) {
            PoweredBy$PoweredByIconType var14 = var5;
            String var13 = "Powered by Fin Ai Agent";
            int var12 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PoweredByBadgeKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var10000 = var13;
            var10001 = var14;
            Object var16 = var15;
            var7.updateRememberedValue(var16);
            var10002 = var16;
         } else {
            var10002 = var10;
         }

         Function0 var6 = (Function0)var10002;
         var1.endReplaceGroup();
         PoweredByBadgeKt.recovered$9b47502c50dedaf9(var10000, var10001, var6, null, var3, 0L, var1, 25014, 40);
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
