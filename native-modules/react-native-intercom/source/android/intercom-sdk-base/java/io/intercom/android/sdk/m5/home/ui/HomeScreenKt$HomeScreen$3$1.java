package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.MutableFloatState;
import androidx.compose.runtime.State;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import io.intercom.android.sdk.m5.home.HomeViewModel;
import io.intercom.android.sdk.m5.home.states.HomeUiState;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle;
import io.intercom.android.sdk.m5.home.ui.header.HomeHeaderBackdropKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt$HomeScreen$3$1\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,275:1\n75#2:276\n1#3:277\n1047#4,6:278\n*S KotlinDebug\n*F\n+ 1 HomeScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeScreenKt$HomeScreen$3$1\n*L\n111#1:276\n112#1:278,6\n*E\n"
)
final class HomeScreenKt$HomeScreen$3$1 implements Function3 {
   // $VF: synthetic field
   final State $uiState;
   // $VF: synthetic field
   final HomeViewModel $homeViewModel;
   // $VF: synthetic field
   final MutableFloatState $headerHeightPx;

   HomeScreenKt$HomeScreen$3$1(State var1, HomeViewModel var2, MutableFloatState var3) {
      this.$uiState = var1;
      this.$homeViewModel = var2;
      this.$headerHeightPx = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C*110@4933L7,111@5020L34,107@4686L391:HomeScreen.kt#ac106p");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(750386582, var3, -1, "io.intercom.android.sdk.m5.home.ui.HomeScreen.<anonymous>.<anonymous> (HomeScreen.kt:105)");
      }

      HomeUiState var4 = (HomeUiState)this.$uiState.getValue();
      if (var4 instanceof io.intercom.android.sdk.m5.home.states.HomeUiState$Content) {
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var5 = ((io.intercom.android.sdk.m5.home.states.HomeUiState$Content)var4).getHeader().getHeaderBackdropStyle();
         io.intercom.android.sdk.m5.home.states.HomeUiState$Content$ContentHeader$HeaderBackdropStyle var6 = ((io.intercom.android.sdk.m5.home.states.HomeUiState$Content)var4).getHeader().getHeaderBackdropStyleDark();
         CompositionLocal var8 = CompositionLocalsKt.getLocalDensity();
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var11 = var2.consume(var8);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Object var18 = var11;
         MutableFloatState var20 = this.$headerHeightPx;
         Density var22 = (Density)var18;
         int var24 = 0;
         float var7 = var22.recovered$7835939063ae77d1(var20.getFloatValue());
         HomeViewModel var21 = this.$homeViewModel;
         var2.startReplaceGroup(-1050762168);
         ComposerKt.sourceInformation(var2, "CC(remember):HomeScreen.kt#9igjgp");
         Composer var25 = var2;
         boolean var12 = var2.changedInstance(var21);
         int var13 = 0;
         Object var14 = var25.rememberedValue();
         int var15 = 0;
         Object var10000;
         if (!var12 && var14 != Composer.Companion.getEmpty()) {
            var10000 = var14;
         } else {
            int var16 = 0;
            Object var17 = new HomeScreenKt$HomeScreen$3$1$2$1(var21);
            var25.updateRememberedValue(var17);
            var10000 = var17;
         }

         KFunction var23 = (KFunction)var10000;
         var2.endReplaceGroup();
         KFunction var19 = var23;
         HomeHeaderBackdropKt.recovered$b457fc8554473e41(var7, var5, var6, (Function0)var19, var2, 0, 0);
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
