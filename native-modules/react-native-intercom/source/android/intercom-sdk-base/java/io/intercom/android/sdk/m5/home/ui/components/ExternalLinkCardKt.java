package io.intercom.android.sdk.m5.home.ui.components;

import android.content.Context;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0015\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "ExternalLinkCard",
         "",
         "homeExternalLinkData",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeExternalLinkData;",
         "(Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeExternalLinkData;Landroidx/compose/runtime/Composer;I)V",
         "ExternalLinkCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nExternalLinkCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExternalLinkCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ExternalLinkCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,102:1\n75#2:103\n*S KotlinDebug\n*F\n+ 1 ExternalLinkCard.kt\nio/intercom/android/sdk/m5/home/ui/components/ExternalLinkCardKt\n*L\n36#1:103\n*E\n"
)
public final class ExternalLinkCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExternalLinkCard(@NotNull HomeCards$HomeExternalLinkData var0, @Nullable Composer var1, int var2) {
      Intrinsics.checkNotNullParameter(var0, "homeExternalLinkData");
      var1 = var1.startRestartGroup(-1463768637);
      ComposerKt.sourceInformation(var1, "C(ExternalLinkCard)35@1512L7,36@1537L2033,36@1524L2046:ExternalLinkCard.kt#utrtal");
      int var3 = var2;
      if ((var2 & 6) == 0) {
         var3 |= var1.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1463768637, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.ExternalLinkCard (ExternalLinkCard.kt:34)");
         }

         CompositionLocal var5 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var6 = 0;
         int var7 = 0;
         ComposerKt.sourceInformationMarkerStart(var1, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var8 = var1.consume(var5);
         ComposerKt.sourceInformationMarkerEnd(var1);
         Context var4 = (Context)var8;
         IntercomCardKt.IntercomCard(
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(1810723127, true, new ExternalLinkCardKt$ExternalLinkCard$1(var0, var4), var1, 54),
            var1,
            384,
            3
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData recoveredCapture_913_0 = var0;
         final int recoveredCapture_913_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_913_0, recoveredArgument_913_1) -> ExternalLinkCardKt.ExternalLinkCard$lambda$0(recoveredCapture_913_0, recoveredCapture_913_1, (androidx.compose.runtime.Composer)recoveredArgument_913_0, ((java.lang.Integer)recoveredArgument_913_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExternalLinkCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-144974605);
      ComposerKt.sourceInformation(var0, "C(ExternalLinkCardPreview)88@3648L467:ExternalLinkCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-144974605, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.ExternalLinkCardPreview (ExternalLinkCard.kt:87)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ExternalLinkCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_914_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_914_0, recoveredArgument_914_1) -> ExternalLinkCardKt.ExternalLinkCardPreview$lambda$1(recoveredCapture_914_0, (androidx.compose.runtime.Composer)recoveredArgument_914_0, ((java.lang.Integer)recoveredArgument_914_1).intValue())));
      }
   }

   private static final Unit ExternalLinkCard$lambda$0(HomeCards$HomeExternalLinkData var0, int var1, Composer var2, int var3) {
      ExternalLinkCard(var0, var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExternalLinkCardPreview$lambda$1(int var0, Composer var1, int var2) {
      ExternalLinkCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
