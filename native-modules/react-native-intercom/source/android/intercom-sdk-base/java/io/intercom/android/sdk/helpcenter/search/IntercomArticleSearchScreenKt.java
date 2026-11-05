package io.intercom.android.sdk.helpcenter.search;

import android.content.Context;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.font.FontWeight;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a7\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0001¢\u0006\u0002\u0010\t\u001a\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\bH\u0002¨\u0006\u000f",
   d2 = {
         "IntercomArticleSearchScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;",
         "onClearSearchClick",
         "Lkotlin/Function0;",
         "onArticleClicked",
         "Lkotlin/Function1;",
         "",
         "(Lio/intercom/android/sdk/helpcenter/search/ArticleSearchState;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "getNoResultsMessage",
         "Landroidx/compose/ui/text/AnnotatedString;",
         "context",
         "Landroid/content/Context;",
         "searchTerm",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomArticleSearchScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleSearchScreen.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchScreenKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n*L\n1#1,129:1\n75#2:130\n1580#3:131\n1398#3,6:132\n*S KotlinDebug\n*F\n+ 1 IntercomArticleSearchScreen.kt\nio/intercom/android/sdk/helpcenter/search/IntercomArticleSearchScreenKt\n*L\n43#1:130\n120#1:131\n123#1:132,6\n*E\n"
)
public final class IntercomArticleSearchScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomArticleSearchScreen(
      @NotNull ArticleSearchState var0, @NotNull Function0 var1, @NotNull Function1 var2, @Nullable Composer var3, int var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "state");
      Intrinsics.checkNotNullParameter(var1, "onClearSearchClick");
      Intrinsics.checkNotNullParameter(var2, "onArticleClicked");
      var3 = var3.startRestartGroup(-1211464960);
      ComposerKt.sourceInformation(
         var3, "C(IntercomArticleSearchScreen)P(2,1)42@2072L7,44@2123L6,45@2178L6,46@2205L2380,43@2084L2501:IntercomArticleSearchScreen.kt#fpck24"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1211464960, var5, -1, "io.intercom.android.sdk.helpcenter.search.IntercomArticleSearchScreen (IntercomArticleSearchScreen.kt:41)"
            );
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var3.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var3);
         Context var6 = (Context)var10;
         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(
               1420291739, true, new IntercomArticleSearchScreenKt$IntercomArticleSearchScreen$1(var0, var2, var1, var6), var3, 54
            ),
            var3,
            12582912,
            115
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.helpcenter.search.ArticleSearchState recoveredCapture_114_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_114_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredCapture_114_2 = var2;
         final int recoveredCapture_114_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_114_0, recoveredArgument_114_1) -> IntercomArticleSearchScreenKt.IntercomArticleSearchScreen$lambda$0(recoveredCapture_114_0, recoveredCapture_114_1, recoveredCapture_114_2, recoveredCapture_114_3, (androidx.compose.runtime.Composer)recoveredArgument_114_0, ((java.lang.Integer)recoveredArgument_114_1).intValue())));
      }
   }

   private static final AnnotatedString getNoResultsMessage(Context var0, String var1) {
      String var2 = '\'' + var1 + '\'';
      String var10000 = var0.getString(string.intercom_no_results_for_searchterm);
      Intrinsics.checkNotNullExpressionValue(var10000, "getString(...)");
      String var3 = StringsKt.replace$default(var10000, "{searchTerm}", var2, false, 4, null);
      int var4 = 0;
      Builder var5 = new Builder(0, 1, null);
      Builder var6 = var5;
      int var7 = 0;
      int var8 = StringsKt.indexOf$default(var3, var2, 0, false, 6, null);
      String var10001 = var3.substring(0, var8);
      Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
      var6.append(var10001);
      Builder var9 = var6;
      SpanStyle var10 = new SpanStyle(
         0L, 0L, FontWeight.Companion.getBold(), null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65531, null
      );
      int var11 = 0;
      int var12 = var9.pushStyle(var10);

      try {
         Builder var13 = var9;
         int var14 = 0;
         var10001 = var3.substring(var8, var8 + var2.length());
         Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
         var13.append(var10001);
         Unit var15 = Unit.INSTANCE;
      } finally {
         var9.pop(var12);
      }

      var10001 = var3.substring(var8 + var2.length());
      Intrinsics.checkNotNullExpressionValue(var10001, "substring(...)");
      var6.append(var10001);
      return var5.toAnnotatedString();
   }

   private static final Unit IntercomArticleSearchScreen$lambda$0(ArticleSearchState var0, Function0 var1, Function1 var2, int var3, Composer var4, int var5) {
      IntercomArticleSearchScreen(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final AnnotatedString access$getNoResultsMessage(Context var0, String var1) {
      return getNoResultsMessage(var0, var1);
   }
}
