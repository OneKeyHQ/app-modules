package io.intercom.android.sdk.m5.components;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import com.facebook.shimmer.ShimmerFrameLayout;
import io.intercom.android.sdk.survey.ui.components.LoadingComponentKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a+\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "LoadingScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "shimmerDrawable",
         "",
         "animateShimmer",
         "",
         "(Landroidx/compose/ui/Modifier;IZLandroidx/compose/runtime/Composer;II)V",
         "LoadingScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nLoadingScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadingScreen.kt\nio/intercom/android/sdk/m5/components/LoadingScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,49:1\n1047#2,6:50\n*S KotlinDebug\n*F\n+ 1 LoadingScreen.kt\nio/intercom/android/sdk/m5/components/LoadingScreenKt\n*L\n24#1:50,6\n*E\n"
)
public final class LoadingScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void LoadingScreen(@Nullable Modifier var0, @DrawableRes int var1, boolean var2, @Nullable Composer var3, int var4, int var5) {
      var3 = var3.startRestartGroup(1820782258);
      ComposerKt.sourceInformation(var3, "C(LoadingScreen)P(1,2)20@789L6,23@887L319,21@813L400:LoadingScreen.kt#arqjag");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 4) != 0) {
            var2 = true;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1820782258, var6, -1, "io.intercom.android.sdk.m5.components.LoadingScreen (LoadingScreen.kt:19)");
         }

         long var7 = IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         Modifier var9 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
         var3.startReplaceGroup(1035115521);
         ComposerKt.sourceInformation(var3, "CC(remember):LoadingScreen.kt#9igjgp");
         Composer var11 = var3;
         boolean var12 = (var6 & 896) == 256 | var3.changed(var7) | (var6 & 112) == 32;
         int var13 = 0;
         Object var14 = var11.rememberedValue();
         int var15 = 0;
         Object var10000;
         if (!var12 && var14 != Composer.Companion.getEmpty()) {
            var10000 = var14;
         } else {
            int var16 = 0;
            final boolean recoveredCapture_229_0 = var2;
            final long recoveredCapture_229_1 = var7;
            final int recoveredCapture_229_2 = var1;
            Object var17 = ((kotlin.jvm.functions.Function1) (recoveredArgument_229_0) -> LoadingScreenKt.LoadingScreen$lambda$2$lambda$1(recoveredCapture_229_0, recoveredCapture_229_1, recoveredCapture_229_2, (android.content.Context)recoveredArgument_229_0));
            var11.updateRememberedValue(var17);
            var10000 = var17;
         }

         Function1 var10 = (Function1)var10000;
         var3.endReplaceGroup();
         AndroidView_androidKt.AndroidView(var10, var9, null, var3, 0, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var19 = var3.endRestartGroup();
      if (var19 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_230_0 = var0;
         final int recoveredCapture_230_1 = var1;
         final boolean recoveredCapture_230_2 = var2;
         final int recoveredCapture_230_3 = var4;
         final int recoveredCapture_230_4 = var5;
         var19.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_230_0, recoveredArgument_230_1) -> LoadingScreenKt.LoadingScreen$lambda$3(recoveredCapture_230_0, recoveredCapture_230_1, recoveredCapture_230_2, recoveredCapture_230_3, recoveredCapture_230_4, (androidx.compose.runtime.Composer)recoveredArgument_230_0, ((java.lang.Integer)recoveredArgument_230_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void LoadingScreenPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1596356708);
      ComposerKt.sourceInformation(var0, "C(LoadingScreenPreview)40@1289L237:LoadingScreen.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1596356708, var1, -1, "io.intercom.android.sdk.m5.components.LoadingScreenPreview (LoadingScreen.kt:39)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$LoadingScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_231_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_231_0, recoveredArgument_231_1) -> LoadingScreenKt.LoadingScreenPreview$lambda$4(recoveredCapture_231_0, (androidx.compose.runtime.Composer)recoveredArgument_231_0, ((java.lang.Integer)recoveredArgument_231_1).intValue())));
      }
   }

   private static final ShimmerFrameLayout LoadingScreen$lambda$2$lambda$1(boolean var0, long var1, int var3, Context var4) {
      Intrinsics.checkNotNullParameter(var4, "context");
      ShimmerFrameLayout var5 = LoadingComponentKt.buildLoadingContainer(var4, var0);
      ShimmerFrameLayout var6 = var5;
      int var7 = 0;
      var6.addView(LoadingComponentKt.recovered$b7cb79a556d1e984(var4, var1, var3));
      return var5;
   }

   private static final Unit LoadingScreen$lambda$3(Modifier var0, int var1, boolean var2, int var3, int var4, Composer var5, int var6) {
      LoadingScreen(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit LoadingScreenPreview$lambda$4(int var0, Composer var1, int var2) {
      LoadingScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
