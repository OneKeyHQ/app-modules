package io.intercom.android.sdk.m5.home.ui.header;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;
import io.intercom.android.sdk.utilities.ColorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeHeaderBackdrop.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-5$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,242:1\n118#2:243\n1563#3:244\n1634#3,3:245\n1047#4,6:248\n*S KotlinDebug\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-5$1\n*L\n187#1:243\n194#1:244\n194#1:245,3\n197#1:248,6\n*E\n"
)
final class ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C196@7576L2,185@7106L483:HomeHeaderBackdrop.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2043694272,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt.lambda-5.<anonymous> (HomeHeaderBackdrop.kt:185)"
            );
         }

         int var3 = 200;
         int var4 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var3);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10001 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(ColorKt.Color(ColorUtils.parseColor("#326D7D")), true, null);
         String[] var19 = new String[]{"#8B0000", "#4B0000"};
         Iterable var20 = CollectionsKt.listOf(var19);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var13 = var10001;
         float var12 = var10000;
         int var22 = 0;
         Iterable var5 = var20;
         ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var20, 10));
         int var7 = 0;

         for (Object var9 : var5) {
            String var10 = (String)var9;
            Collection var16 = var6;
            int var11 = 0;
            var16.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var10))));
         }

         List var32 = (List)var6;
         float var33 = var12;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var34 = var13;
         boolean var17 = true;
         List var18 = var32;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10002 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var18, var17);
         var1.startReplaceGroup(-1328608248);
         ComposerKt.sourceInformation(var1, "CC(remember):HomeHeaderBackdrop.kt#9igjgp");
         Composer var23 = var1;
         boolean var24 = false;
         int var25 = 0;
         Object var26 = var23.rememberedValue();
         int var27 = 0;
         Object var10003;
         if (var26 == Composer.Companion.getEmpty()) {
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var14 = var10002;
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var31 = var13;
            float var30 = var12;
            int var28 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$2$lambda$1());
            var33 = var30;
            var34 = var31;
            var10002 = var14;
            Object var29 = var15;
            var23.updateRememberedValue(var29);
            var10003 = var29;
         } else {
            var10003 = var26;
         }

         Function0 var21 = (Function0)var10003;
         var1.endReplaceGroup();
         HomeHeaderBackdropKt.recovered$b457fc8554473e41(var33, var34, var10002, var21, var1, 3078, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1() {
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
