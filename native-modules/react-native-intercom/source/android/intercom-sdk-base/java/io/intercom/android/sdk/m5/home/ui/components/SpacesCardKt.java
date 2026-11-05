package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeSpacesData;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a)\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\u0007¨\u0006\b",
   d2 = {
         "SpacesCard",
         "",
         "homeSpacesData",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeSpacesData;",
         "onItemClick",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/home/data/SpaceItemType;",
         "(Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeSpacesData;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class SpacesCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SpacesCard(@NotNull HomeCards$HomeSpacesData var0, @NotNull Function1 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "homeSpacesData");
      Intrinsics.checkNotNullParameter(var1, "onItemClick");
      var2 = var2.startRestartGroup(-261271608);
      ComposerKt.sourceInformation(var2, "C(SpacesCard)21@854L1018,21@841L1031:SpacesCard.kt#utrtal");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-261271608, var4, -1, "io.intercom.android.sdk.m5.home.ui.components.SpacesCard (SpacesCard.kt:20)");
         }

         IntercomCardKt.IntercomCard(
            null, null, ComposableLambdaKt.rememberComposableLambda(1212336956, true, new SpacesCardKt$SpacesCard$1(var0, var1), var2, 54), var2, 384, 3
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeSpacesData recoveredCapture_926_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_926_1 = var1;
         final int recoveredCapture_926_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_926_0, recoveredArgument_926_1) -> SpacesCardKt.SpacesCard$lambda$0(recoveredCapture_926_0, recoveredCapture_926_1, recoveredCapture_926_2, (androidx.compose.runtime.Composer)recoveredArgument_926_0, ((java.lang.Integer)recoveredArgument_926_1).intValue())));
      }
   }

   private static final Unit SpacesCard$lambda$0(HomeCards$HomeSpacesData var0, Function1 var1, int var2, Composer var3, int var4) {
      SpacesCard(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }
}
