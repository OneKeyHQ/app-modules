package io.intercom.android.sdk.m5.home.ui.header;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;
import io.intercom.android.sdk.utilities.ColorUtils;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeHeaderBackdrop.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,242:1\n118#2:243\n1047#3,6:244\n*S KotlinDebug\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-1$1\n*L\n103#1:243\n112#1:244,6\n*E\n"
)
final class ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C111@4422L2,101@4005L430:HomeHeaderBackdrop.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1593394498,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt.lambda-1.<anonymous> (HomeHeaderBackdrop.kt:101)"
            );
         }

         int var3 = 200;
         int var4 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var3);
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10001 = new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(
            ColorKt.Color(ColorUtils.parseColor("#326D7D")), false, null
         );
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10002 = new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(
            ColorKt.Color(ColorUtils.parseColor("#8B0000")), false, null
         );
         var1.startReplaceGroup(1231018180);
         ComposerKt.sourceInformation(var1, "CC(remember):HomeHeaderBackdrop.kt#9igjgp");
         Composer var15 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var15.rememberedValue();
         int var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            HomeUiState$Content$ContentHeader$HeaderBackdropStyle var12 = var10002;
            HomeUiState$Content$ContentHeader$HeaderBackdropStyle var11 = var10001;
            float var10 = var10000;
            int var9 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            Object var16 = var13;
            var15.updateRememberedValue(var16);
            var10003 = var16;
         } else {
            var10003 = var7;
         }

         Function0 var14 = (Function0)var10003;
         var1.endReplaceGroup();
         HomeHeaderBackdropKt.recovered$b457fc8554473e41(var10000, var10001, var10002, var14, var1, 3078, 0);
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
