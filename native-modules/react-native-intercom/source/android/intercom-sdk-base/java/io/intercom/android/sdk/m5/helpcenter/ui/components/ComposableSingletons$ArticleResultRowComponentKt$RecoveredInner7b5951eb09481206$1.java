package io.intercom.android.sdk.m5.helpcenter.ui.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.helpcenter.search.ArticleSearchResultRow$ArticleResultRow;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nArticleResultRowComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArticleResultRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$ArticleResultRowComponentKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,100:1\n1047#2,6:101\n*S KotlinDebug\n*F\n+ 1 ArticleResultRowComponent.kt\nio/intercom/android/sdk/m5/helpcenter/ui/components/ComposableSingletons$ArticleResultRowComponentKt$lambda-1$1\n*L\n96#1:101,6\n*E\n"
)
final class ComposableSingletons$ArticleResultRowComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ArticleResultRowComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ArticleResultRowComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C88@3359L6,95@3993L2,87@3275L731:ArticleResultRowComponent.kt#ngglre");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1483295159,
               var2,
               -1,
               "io.intercom.android.sdk.m5.helpcenter.ui.components.ComposableSingletons$ArticleResultRowComponentKt.lambda-1.<anonymous> (ArticleResultRowComponent.kt:87)"
            );
         }

         Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
            Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
         );
         ArticleSearchResultRow$ArticleResultRow var10000 = new ArticleSearchResultRow$ArticleResultRow(
            "",
            "<highlight>Lorem Ipsum</highlight> is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s.",
            "There are many variations of passages of <highlight>Lorem Ipsum</highlight> available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable.",
            0
         );
         var1.startReplaceGroup(-1446941114);
         ComposerKt.sourceInformation(var1, "CC(remember):ArticleResultRowComponent.kt#9igjgp");
         Composer var5 = var1;
         boolean var6 = false;
         int var7 = 0;
         Object var8 = var5.rememberedValue();
         int var9 = 0;
         kotlin.jvm.functions.Function1 var13;
         if (var8 == Composer.Companion.getEmpty()) {
            ArticleSearchResultRow$ArticleResultRow var11 = var10000;
            int var10 = 0;
            var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_872_0) -> ComposableSingletons$ArticleResultRowComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_872_0));
            var10000 = var11;
            Object var12 = var13;
            var5.updateRememberedValue(var12);
            var13 = (Function1)var12;
         } else {
            var13 = (Function1)var8;
         }

         Function1 var4 = var13;
         var1.endReplaceGroup();
         ArticleResultRowComponentKt.ArticleResultRowComponent(var10000, var4, var3, var1, 48, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
