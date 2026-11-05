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
   "SMAP\nHomeHeaderBackdrop.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-4$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,242:1\n118#2:243\n1563#3:244\n1634#3,3:245\n1563#3:248\n1634#3,3:249\n1047#4,6:252\n*S KotlinDebug\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-4$1\n*L\n165#1:243\n168#1:244\n168#1:245,3\n173#1:248\n173#1:249,3\n176#1:252,6\n*E\n"
)
final class ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C175@6790L2,163@6243L560:HomeHeaderBackdrop.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -79142919,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt.lambda-4.<anonymous> (HomeHeaderBackdrop.kt:163)"
            );
         }

         int var3 = 200;
         int var4 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var3);
         String[] var21 = new String[]{"#142C4D", "#0057FF", "#CAF4F7"};
         Iterable var22 = CollectionsKt.listOf(var21);
         float var12 = var10000;
         int var26 = 0;
         Iterable var5 = var22;
         ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var22, 10));
         int var7 = 0;

         for (Object var9 : var5) {
            String var10 = (String)var9;
            Collection var15 = var6;
            int var11 = 0;
            var15.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var10))));
         }

         List var45 = (List)var6;
         boolean var17 = true;
         List var18 = var45;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10001 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var18, var17);
         String[] var23 = new String[]{"#8B0000", "#4B0000", "#2B0000"};
         Iterable var24 = CollectionsKt.listOf(var23);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var13 = var10001;
         float var42 = var12;
         int $i$f$mapx = 0;
         Iterable var29 = var24;
         var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault(var24, 10));
         int $i$f$mapTox = 0;

         for (Object var37 : var29) {
            String var40 = (String)var37;
            Collection var16 = destination$iv$ivx;
            int var41 = 0;
            var16.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var40))));
         }

         List var47 = (List)destination$iv$ivx;
         float var48 = var42;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var49 = var13;
         boolean var19 = true;
         List var20 = var47;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10002 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var20, var19);
         var1.startReplaceGroup(1458782087);
         ComposerKt.sourceInformation(var1, "CC(remember):HomeHeaderBackdrop.kt#9igjgp");
         Composer var28 = var1;
         boolean var30 = false;
         int var32 = 0;
         Object var34 = var28.rememberedValue();
         int var36 = 0;
         Object var10003;
         if (var34 == Composer.Companion.getEmpty()) {
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var14 = var10002;
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var44 = var13;
            float var43 = var42;
            int var38 = 0;
            Function0 var46 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$3$lambda$2());
            var48 = var43;
            var49 = var44;
            var10002 = var14;
            Object var39 = var46;
            var28.updateRememberedValue(var39);
            var10003 = var39;
         } else {
            var10003 = var34;
         }

         Function0 var25 = (Function0)var10003;
         var1.endReplaceGroup();
         HomeHeaderBackdropKt.recovered$b457fc8554473e41(var48, var49, var10002, var25, var1, 3078, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$3$lambda$2() {
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
