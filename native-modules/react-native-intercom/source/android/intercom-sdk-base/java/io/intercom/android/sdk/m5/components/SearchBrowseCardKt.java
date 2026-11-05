package io.intercom.android.sdk.m5.components;

import android.content.Context;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
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
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a;\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u000bH\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0012",
   d2 = {
         "SearchBrowseCard",
         "",
         "helpCenterData",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeHelpCenterData;",
         "isSearchFirstEnabled",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "accessToTeammateEnabled",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "(Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeHelpCenterData;ZLjava/util/List;ZLio/intercom/android/sdk/metrics/MetricTracker;Landroidx/compose/runtime/Composer;I)V",
         "PreviewSearchBrowse",
         "(Landroidx/compose/runtime/Composer;I)V",
         "PreviewSearchBrowseNoSuggestions",
         "PreviewSearchBrowseNoSearchFirst",
         "PreviewSearchBrowseNoSuggestionsNoSearchFirst",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSearchBrowseCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,270:1\n75#2:271\n*S KotlinDebug\n*F\n+ 1 SearchBrowseCard.kt\nio/intercom/android/sdk/m5/components/SearchBrowseCardKt\n*L\n54#1:271\n*E\n"
)
public final class SearchBrowseCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SearchBrowseCard(
      @NotNull HomeCards$HomeHelpCenterData var0,
      boolean var1,
      @NotNull List var2,
      boolean var3,
      @NotNull MetricTracker var4,
      @Nullable Composer var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "helpCenterData");
      Intrinsics.checkNotNullParameter(var2, "avatars");
      Intrinsics.checkNotNullParameter(var4, "metricTracker");
      var5 = var5.startRestartGroup(382156573);
      ComposerKt.sourceInformation(var5, "C(SearchBrowseCard)P(2,3,1)53@2398L7,56@2474L5366,54@2410L5430:SearchBrowseCard.kt#arqjag");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var5.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 24576) == 0) {
         var7 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(382156573, var7, -1, "io.intercom.android.sdk.m5.components.SearchBrowseCard (SearchBrowseCard.kt:52)");
         }

         CompositionLocal var9 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var10 = 0;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var12 = var5.consume(var9);
         ComposerKt.sourceInformationMarkerEnd(var5);
         Context var8 = (Context)var12;
         IntercomCardKt.IntercomCard(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            null,
            ComposableLambdaKt.rememberComposableLambda(
               -1020132823, true, new SearchBrowseCardKt$SearchBrowseCard$1(var1, var0, var4, var8, var3, var2), var5, 54
            ),
            var5,
            390,
            2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var5.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData recoveredCapture_239_0 = var0;
         final boolean recoveredCapture_239_1 = var1;
         final java.util.List recoveredCapture_239_2 = var2;
         final boolean recoveredCapture_239_3 = var3;
         final io.intercom.android.sdk.metrics.MetricTracker recoveredCapture_239_4 = var4;
         final int recoveredCapture_239_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_239_0, recoveredArgument_239_1) -> SearchBrowseCardKt.SearchBrowseCard$lambda$0(recoveredCapture_239_0, recoveredCapture_239_1, recoveredCapture_239_2, recoveredCapture_239_3, recoveredCapture_239_4, recoveredCapture_239_5, (androidx.compose.runtime.Composer)recoveredArgument_239_0, ((java.lang.Integer)recoveredArgument_239_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewSearchBrowse(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1546858090);
      ComposerKt.sourceInformation(var0, "C(PreviewSearchBrowse)177@7914L910:SearchBrowseCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1546858090, var1, -1, "io.intercom.android.sdk.m5.components.PreviewSearchBrowse (SearchBrowseCard.kt:176)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SearchBrowseCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_235_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_235_0, recoveredArgument_235_1) -> SearchBrowseCardKt.PreviewSearchBrowse$lambda$1(recoveredCapture_235_0, (androidx.compose.runtime.Composer)recoveredArgument_235_0, ((java.lang.Integer)recoveredArgument_235_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewSearchBrowseNoSuggestions(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1745562356);
      ComposerKt.sourceInformation(var0, "C(PreviewSearchBrowseNoSuggestions)203@8911L642:SearchBrowseCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1745562356, var1, -1, "io.intercom.android.sdk.m5.components.PreviewSearchBrowseNoSuggestions (SearchBrowseCard.kt:202)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SearchBrowseCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_237_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_237_0, recoveredArgument_237_1) -> SearchBrowseCardKt.PreviewSearchBrowseNoSuggestions$lambda$2(recoveredCapture_237_0, (androidx.compose.runtime.Composer)recoveredArgument_237_0, ((java.lang.Integer)recoveredArgument_237_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewSearchBrowseNoSearchFirst(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-678171621);
      ComposerKt.sourceInformation(var0, "C(PreviewSearchBrowseNoSearchFirst)225@9640L911:SearchBrowseCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-678171621, var1, -1, "io.intercom.android.sdk.m5.components.PreviewSearchBrowseNoSearchFirst (SearchBrowseCard.kt:224)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SearchBrowseCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_236_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_236_0, recoveredArgument_236_1) -> SearchBrowseCardKt.PreviewSearchBrowseNoSearchFirst$lambda$3(recoveredCapture_236_0, (androidx.compose.runtime.Composer)recoveredArgument_236_0, ((java.lang.Integer)recoveredArgument_236_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void PreviewSearchBrowseNoSuggestionsNoSearchFirst(Composer var0, int var1) {
      var0 = var0.startRestartGroup(354688977);
      ComposerKt.sourceInformation(var0, "C(PreviewSearchBrowseNoSuggestionsNoSearchFirst)251@10651L643:SearchBrowseCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               354688977, var1, -1, "io.intercom.android.sdk.m5.components.PreviewSearchBrowseNoSuggestionsNoSearchFirst (SearchBrowseCard.kt:250)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$SearchBrowseCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_238_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_238_0, recoveredArgument_238_1) -> SearchBrowseCardKt.PreviewSearchBrowseNoSuggestionsNoSearchFirst$lambda$4(recoveredCapture_238_0, (androidx.compose.runtime.Composer)recoveredArgument_238_0, ((java.lang.Integer)recoveredArgument_238_1).intValue())));
      }
   }

   private static final Unit SearchBrowseCard$lambda$0(
      HomeCards$HomeHelpCenterData var0, boolean var1, List var2, boolean var3, MetricTracker var4, int var5, Composer var6, int var7
   ) {
      SearchBrowseCard(var0, var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewSearchBrowse$lambda$1(int var0, Composer var1, int var2) {
      PreviewSearchBrowse(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewSearchBrowseNoSuggestions$lambda$2(int var0, Composer var1, int var2) {
      PreviewSearchBrowseNoSuggestions(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewSearchBrowseNoSearchFirst$lambda$3(int var0, Composer var1, int var2) {
      PreviewSearchBrowseNoSearchFirst(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewSearchBrowseNoSuggestionsNoSearchFirst$lambda$4(int var0, Composer var1, int var2) {
      PreviewSearchBrowseNoSuggestionsNoSearchFirst(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
