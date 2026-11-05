package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
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
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a)\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\u0007\u001a\r\u0010\b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\t¨\u0006\n",
   d2 = {
         "TicketLinksCard",
         "",
         "ticketLinksData",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeTicketLinksData;",
         "onTicketLinkClicked",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "(Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeTicketLinksData;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "ExternalLinkCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class TicketLinksCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketLinksCard(@NotNull HomeCards$HomeTicketLinksData var0, @NotNull Function1 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "ticketLinksData");
      Intrinsics.checkNotNullParameter(var1, "onTicketLinkClicked");
      var2 = var2.startRestartGroup(-1931742368);
      ComposerKt.sourceInformation(var2, "C(TicketLinksCard)P(1)36@1569L2083,36@1556L2096:TicketLinksCard.kt#utrtal");
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
            ComposerKt.traceEventStart(-1931742368, var4, -1, "io.intercom.android.sdk.m5.home.ui.components.TicketLinksCard (TicketLinksCard.kt:35)");
         }

         IntercomCardKt.IntercomCard(
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1008312684, true, new TicketLinksCardKt$TicketLinksCard$1(var0, var1), var2, 54),
            var2,
            384,
            3
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData recoveredCapture_929_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredCapture_929_1 = var1;
         final int recoveredCapture_929_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_929_0, recoveredArgument_929_1) -> TicketLinksCardKt.TicketLinksCard$lambda$0(recoveredCapture_929_0, recoveredCapture_929_1, recoveredCapture_929_2, (androidx.compose.runtime.Composer)recoveredArgument_929_0, ((java.lang.Integer)recoveredArgument_929_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExternalLinkCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1962060809);
      ComposerKt.sourceInformation(var0, "C(ExternalLinkCardPreview)89@3730L843:TicketLinksCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1962060809, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.ExternalLinkCardPreview (TicketLinksCard.kt:88)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketLinksCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_928_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_928_0, recoveredArgument_928_1) -> TicketLinksCardKt.ExternalLinkCardPreview$lambda$1(recoveredCapture_928_0, (androidx.compose.runtime.Composer)recoveredArgument_928_0, ((java.lang.Integer)recoveredArgument_928_1).intValue())));
      }
   }

   private static final Unit TicketLinksCard$lambda$0(HomeCards$HomeTicketLinksData var0, Function1 var1, int var2, Composer var3, int var4) {
      TicketLinksCard(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExternalLinkCardPreview$lambda$1(int var0, Composer var1, int var2) {
      ExternalLinkCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
