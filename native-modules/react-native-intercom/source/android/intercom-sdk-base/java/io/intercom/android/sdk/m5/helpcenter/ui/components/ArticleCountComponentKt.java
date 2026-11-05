package io.intercom.android.sdk.m5.helpcenter.ui.components;

import android.content.Context;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.TextStyle;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\u001a\u001f\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\n",
   d2 = {
         "ArticleCountComponent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "count",
         "",
         "(Landroidx/compose/ui/Modifier;ILandroidx/compose/runtime/Composer;II)V",
         "ArticleCountComponentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "SingleArticleCountComponentPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nArticleCountComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleCountComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleCountComponentKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,52:1\n75#2:53\n75#2:54\n*S KotlinDebug\n*F\n+ 1 ArticleCountComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ArticleCountComponentKt\n*L\n17#1:53\n19#1:54\n*E\n"
)
public final class ArticleCountComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ArticleCountComponent(@Nullable Modifier var0, int var1, @Nullable Composer var2, int var3, int var4) {
      var2 = var2.startRestartGroup(-731744304);
      ComposerKt.sourceInformation(var2, "C(ArticleCountComponent)P(1)25@974L10,22@883L156:ArticleCountComponent.kt#ngglre");
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
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -731744304, var5, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleCountComponent (ArticleCountComponent.kt:14)"
            );
         }

         CharSequence var10000;
         if (var1 == 1) {
            var2.startReplaceGroup(1038713318);
            ComposerKt.sourceInformation(var2, "16@666L7");
            CompositionLocal var8 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var9 = 0;
            int var10 = 0;
            ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var11 = var2.consume(var8);
            ComposerKt.sourceInformationMarkerEnd(var2);
            CharSequence var7 = Phrase.from((Context)var11, string.intercom_single_article).format();
            var2.endReplaceGroup();
            var10000 = var7;
         } else {
            var2.startReplaceGroup(1038811929);
            ComposerKt.sourceInformation(var2, "18@764L7");
            CompositionLocal var15 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var17 = 0;
            int var18 = 0;
            ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var19 = var2.consume(var15);
            ComposerKt.sourceInformationMarkerEnd(var2);
            CharSequence var13 = Phrase.from((Context)var19, string.intercom_multiple_articles).put("total_articles", var1).format();
            var2.endReplaceGroup();
            var10000 = var13;
         }

         String var6 = var10000.toString();
         TextStyle var14 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04Point5();
         long var16 = ColorKt.Color(4285887861L);
         TextKt.recovered$5b114ab32a4f4190(
            var6, var0, var16, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var14, var2, 384 | 112 & var5 << 3, 0, 131064
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var20 = var2.endRestartGroup();
      if (var20 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_849_0 = var0;
         final int recoveredCapture_849_1 = var1;
         final int recoveredCapture_849_2 = var3;
         final int recoveredCapture_849_3 = var4;
         var20.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_849_0, recoveredArgument_849_1) -> ArticleCountComponentKt.ArticleCountComponent$lambda$0(recoveredCapture_849_0, recoveredCapture_849_1, recoveredCapture_849_2, recoveredCapture_849_3, (androidx.compose.runtime.Composer)recoveredArgument_849_0, ((java.lang.Integer)recoveredArgument_849_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ArticleCountComponentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1155458330);
      ComposerKt.sourceInformation(var0, "C(ArticleCountComponentPreview)33@1123L162:ArticleCountComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1155458330, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.ArticleCountComponentPreview (ArticleCountComponent.kt:32)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ArticleCountComponentKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_850_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_850_0, recoveredArgument_850_1) -> ArticleCountComponentKt.ArticleCountComponentPreview$lambda$1(recoveredCapture_850_0, (androidx.compose.runtime.Composer)recoveredArgument_850_0, ((java.lang.Integer)recoveredArgument_850_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SingleArticleCountComponentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1795936462);
      ComposerKt.sourceInformation(var0, "C(SingleArticleCountComponentPreview)44@1375L161:ArticleCountComponent.kt#ngglre");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1795936462, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.components.SingleArticleCountComponentPreview (ArticleCountComponent.kt:43)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ArticleCountComponentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_851_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_851_0, recoveredArgument_851_1) -> ArticleCountComponentKt.SingleArticleCountComponentPreview$lambda$2(recoveredCapture_851_0, (androidx.compose.runtime.Composer)recoveredArgument_851_0, ((java.lang.Integer)recoveredArgument_851_1).intValue())));
      }
   }

   private static final Unit ArticleCountComponent$lambda$0(Modifier var0, int var1, int var2, int var3, Composer var4, int var5) {
      ArticleCountComponent(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit ArticleCountComponentPreview$lambda$1(int var0, Composer var1, int var2) {
      ArticleCountComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SingleArticleCountComponentPreview$lambda$2(int var0, Composer var1, int var2) {
      SingleArticleCountComponentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
