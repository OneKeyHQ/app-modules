package io.intercom.android.sdk.m5.home.ui.header;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Error$ErrorHeader;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeHeader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeader.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,239:1\n1047#2,6:240\n*S KotlinDebug\n*F\n+ 1 HomeHeader.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderKt$lambda-3$1\n*L\n234#1:240,6\n*E\n"
)
final class ComposableSingletons$HomeHeaderKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$HomeHeaderKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C233@9053L2,228@8832L238:HomeHeader.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               555108293, var2, -1, "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderKt.lambda-3.<anonymous> (HomeHeader.kt:228)"
            );
         }

         Object var10000 = null;
         HomeUiState$Error$ErrorHeader var10001 = new HomeUiState$Error$ErrorHeader("#FFFFFF", "#000000");
         var1.startReplaceGroup(-173521700);
         ComposerKt.sourceInformation(var1, "CC(remember):HomeHeader.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            HomeUiState$Error$ErrorHeader var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$HomeHeaderKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var13 = var12;
            var4.updateRememberedValue(var13);
            var10002 = var13;
         } else {
            var10002 = var7;
         }

         Function0 var3 = (Function0)var10002;
         var1.endReplaceGroup();
         HomeHeaderKt.HomeErrorHeader((Modifier)var10000, var10001, var3, var1, 384, 1);
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
