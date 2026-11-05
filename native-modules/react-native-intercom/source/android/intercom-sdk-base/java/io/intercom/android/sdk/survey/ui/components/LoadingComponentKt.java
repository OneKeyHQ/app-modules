package io.intercom.android.sdk.survey.ui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import com.facebook.shimmer.ShimmerFrameLayout;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.survey.SurveyState$Loading;
import io.intercom.android.sdk.views.IntercomShimmerLayout;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\u001a\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0000\u001a)\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\u00102\b\b\u0001\u0010\u0011\u001a\u00020\u0012H\u0000¢\u0006\u0004\b\u0013\u0010\u0014¨\u0006\u0015",
   d2 = {
         "SurveyLoading",
         "",
         "state",
         "Lio/intercom/android/sdk/survey/SurveyState$Loading;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/survey/SurveyState$Loading;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "buildLoadingContainer",
         "Lcom/facebook/shimmer/ShimmerFrameLayout;",
         "context",
         "Landroid/content/Context;",
         "animateShimmer",
         "",
         "buildLoadingContent",
         "Landroid/view/View;",
         "tintColor",
         "Landroidx/compose/ui/graphics/Color;",
         "resId",
         "",
         "buildLoadingContent-bw27NRU",
         "(Landroid/content/Context;JI)Landroid/view/View;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nLoadingComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LoadingComponent.kt\nio/intercom/android/sdk/survey/ui/components/LoadingComponentKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/Dp\n*L\n1#1,82:1\n1047#2,6:83\n118#3:89\n66#4:90\n*S KotlinDebug\n*F\n+ 1 LoadingComponent.kt\nio/intercom/android/sdk/survey/ui/components/LoadingComponentKt\n*L\n32#1:83,6\n63#1:89\n63#1:90\n*E\n"
)
public final class LoadingComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SurveyLoading(@NotNull SurveyState$Loading var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "state");
      var2 = var2.startRestartGroup(913588806);
      ComposerKt.sourceInformation(var2, "C(SurveyLoading)P(1)31@1143L352,29@1069L433:LoadingComponent.kt#bmpoeo");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(913588806, var5, -1, "io.intercom.android.sdk.survey.ui.components.SurveyLoading (LoadingComponent.kt:28)");
         }

         Modifier var6 = SizeKt.fillMaxSize$default(var1, 0.0F, 1, null);
         var2.startReplaceGroup(1496338436);
         ComposerKt.sourceInformation(var2, "CC(remember):LoadingComponent.kt#9igjgp");
         Composer var8 = var2;
         boolean var9 = (var5 & 14) == 4;
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var10000;
         if (!var9 && var11 != Composer.Companion.getEmpty()) {
            var10000 = var11;
         } else {
            int var13 = 0;
            final io.intercom.android.sdk.survey.SurveyState$Loading recoveredCapture_1330_0 = var0;
            Object var14 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1330_0) -> LoadingComponentKt.SurveyLoading$lambda$2$lambda$1(recoveredCapture_1330_0, (android.content.Context)recoveredArgument_1330_0));
            var8.updateRememberedValue(var14);
            var10000 = var14;
         }

         Function1 var7 = (Function1)var10000;
         var2.endReplaceGroup();
         AndroidView_androidKt.AndroidView(var7, var6, null, var2, 0, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var16 = var2.endRestartGroup();
      if (var16 != null) {
         final io.intercom.android.sdk.survey.SurveyState$Loading recoveredCapture_1331_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1331_1 = var1;
         final int recoveredCapture_1331_2 = var3;
         final int recoveredCapture_1331_3 = var4;
         var16.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1331_0, recoveredArgument_1331_1) -> LoadingComponentKt.SurveyLoading$lambda$3(recoveredCapture_1331_0, recoveredCapture_1331_1, recoveredCapture_1331_2, recoveredCapture_1331_3, (androidx.compose.runtime.Composer)recoveredArgument_1331_0, ((java.lang.Integer)recoveredArgument_1331_1).intValue())));
      }
   }

   @NotNull
   public static final ShimmerFrameLayout buildLoadingContainer(@NotNull Context var0, boolean var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      ShimmerFrameLayout var2 = new IntercomShimmerLayout(var0, var1);
      LayoutParams var3 = new android.widget.FrameLayout.LayoutParams(-1, -1);
      var2.setLayoutParams(var3);
      var2.setClickable(true);
      var2.setFocusable(true);
      return var2;
   }

   // $VF: synthetic method
   public static ShimmerFrameLayout buildLoadingContainer$default(Context var0, boolean var1, int var2, Object var3) {
      if ((var2 & 2) != 0) {
         var1 = true;
      }

      return buildLoadingContainer(var0, var1);
   }

   @NotNull
   public static final View recovered$b7cb79a556d1e984(@NotNull Context var0, long var1, @DrawableRes int var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      ImageView var4 = new ImageView(var0);
      android.widget.FrameLayout.LayoutParams var5 = new android.widget.FrameLayout.LayoutParams(-1, -2);
      android.widget.FrameLayout.LayoutParams var6 = var5;
      ImageView var12 = var4;
      int var7 = 0;
      int var8 = 20;
      int var9 = 0;
      float var16 = Dp.recovered$95b3d103663bf060(var8);
      float var18 = var0.getResources().getDisplayMetrics().density;
      int var10 = 0;
      int var11 = (int)Dp.recovered$95b3d103663bf060(var16 * var18);
      var6.setMarginStart(var11);
      var6.setMarginEnd(var11);
      var6.topMargin = var11;
      var12.setLayoutParams(var5);
      var4.setAdjustViewBounds(true);
      var4.setScaleType(ScaleType.FIT_XY);
      Drawable var13 = ResourcesCompat.getDrawable(var0.getResources(), var3, null);
      if (var13 != null) {
         Drawable var14 = var13;
         Drawable var15 = var14;
         var8 = 0;
         DrawableCompat.setTint(var15, ColorKt.recovered$81a7e7de546cbf77(var1));
         DrawableCompat.setAutoMirrored(var15, true);
         var4.setImageDrawable(var15);
      }

      return var4;
   }

   private static final ShimmerFrameLayout SurveyLoading$lambda$2$lambda$1(SurveyState$Loading var0, Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      ShimmerFrameLayout var2 = buildLoadingContainer$default(var1, false, 2, null);
      ShimmerFrameLayout var3 = var2;
      int var4 = 0;
      var3.addView(recovered$b7cb79a556d1e984(var1, var0.getSurveyUiColors().recovered$f126774a6443d8d2(), drawable.intercom_survey_loading_state));
      return var2;
   }

   private static final Unit SurveyLoading$lambda$3(SurveyState$Loading var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      SurveyLoading(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }
}
