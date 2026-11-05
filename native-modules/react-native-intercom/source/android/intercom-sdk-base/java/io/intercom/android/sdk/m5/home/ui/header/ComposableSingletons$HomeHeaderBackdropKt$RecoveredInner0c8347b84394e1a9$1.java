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
   "SMAP\nHomeHeaderBackdrop.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-6$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,242:1\n118#2:243\n1563#3:244\n1634#3,3:245\n1047#4,6:248\n*S KotlinDebug\n*F\n+ 1 HomeHeaderBackdrop.kt\nio/intercom/android/sdk/m5/home/ui/header/ComposableSingletons$HomeHeaderBackdropKt$lambda-6$1\n*L\n208#1:243\n211#1:244\n211#1:245,3\n218#1:248,6\n*E\n"
)
final class ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner0c8347b84394e1a9$1 implements Function2 {
   public static final ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner0c8347b84394e1a9$1 INSTANCE = new ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner0c8347b84394e1a9$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C217@8373L2,206@7892L494:HomeHeaderBackdrop.kt#rkbaxi");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               814622986,
               var2,
               -1,
               "io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt.lambda-6.<anonymous> (HomeHeaderBackdrop.kt:206)"
            );
         }

         int var3 = 200;
         int var4 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var3);
         String[] var18 = new String[]{"#142C4D", "#0057FF", "#CAF4F7"};
         Iterable var19 = CollectionsKt.listOf(var18);
         float var12 = var10000;
         int var21 = 0;
         Iterable var5 = var19;
         ArrayList var6 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var19, 10));
         int var7 = 0;

         for (Object var9 : var5) {
            String var10 = (String)var9;
            Collection var15 = var6;
            int var11 = 0;
            var15.add(Color.recovered$f46ef195ad4a2363(ColorKt.Color(ColorUtils.parseColor(var10))));
         }

         List var30 = (List)var6;
         float var32 = var12;
         boolean var16 = true;
         List var17 = var30;
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10001 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var17, var16);
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var10002 = new io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(ColorKt.Color(ColorUtils.parseColor("#8B0000")), true, null);
         var1.startReplaceGroup(178969065);
         ComposerKt.sourceInformation(var1, "CC(remember):HomeHeaderBackdrop.kt#9igjgp");
         Composer var22 = var1;
         boolean var23 = false;
         int var24 = 0;
         Object var25 = var22.rememberedValue();
         int var26 = 0;
         Object var10003;
         if (var25 == Composer.Companion.getEmpty()) {
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var14 = var10002;
            io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var13 = var10001;
            float var29 = var12;
            int var27 = 0;
            Function0 var31 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.home.ui.header.ComposableSingletons$HomeHeaderBackdropKt$RecoveredInner0c8347b84394e1a9$1.invoke$lambda$2$lambda$1());
            var32 = var29;
            var10001 = var13;
            var10002 = var14;
            Object var28 = var31;
            var22.updateRememberedValue(var28);
            var10003 = var28;
         } else {
            var10003 = var25;
         }

         Function0 var20 = (Function0)var10003;
         var1.endReplaceGroup();
         HomeHeaderBackdropKt.recovered$b457fc8554473e41(var32, var10001, var10002, var20, var1, 3078, 0);
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
