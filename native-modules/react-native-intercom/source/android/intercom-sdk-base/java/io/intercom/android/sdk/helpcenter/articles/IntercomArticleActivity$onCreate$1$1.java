package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,290:1\n1047#2,6:291\n*S KotlinDebug\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1\n*L\n80#1:291,6\n*E\n"
)
final class IntercomArticleActivity$onCreate$1$1 implements Function2 {
   // $VF: synthetic field
   final IntercomArticleActivity this$0;

   IntercomArticleActivity$onCreate$1$1(IntercomArticleActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(
         var1, "C78@3459L6,79@3535L85,79@3507L113,84@3737L6,85@3813L13,86@3858L616,99@4494L8812,82@3637L9669:IntercomArticleActivity.kt#1wdz33"
      );
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -199442729,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.onCreate.<anonymous>.<anonymous> (IntercomArticleActivity.kt:77)"
            );
         }

         WindowCompat.getInsetsController(this.this$0.getWindow(), this.this$0.getWindow().getDecorView())
            .setAppearanceLightStatusBars(
               ColorExtensionsKt.recovered$4886b56a29c1c487(
                  IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05()
               )
            );
         Unit var10000 = Unit.INSTANCE;
         var1.startReplaceGroup(1553126697);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleActivity.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.this$0);
         IntercomArticleActivity var6 = this.this$0;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var13;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var13 = (Function2)var8;
         } else {
            Unit var12 = var10000;
            int var10 = 0;
            var13 = new IntercomArticleActivity$onCreate$1$1$1$1(var6, null);
            var10000 = var12;
            Object var11 = var13;
            var4.updateRememberedValue(var11);
            var13 = (Function2)var11;
         }

         Function2 var3 = (kotlin.jvm.functions.Function2) var13;
         var1.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var3, var1, 6);
         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var1, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(547021723, true, new IntercomArticleActivity$onCreate$1$1$2(this.this$0), var1, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-494666138, true, new IntercomArticleActivity$onCreate$1$1$3(this.this$0), var1, 54),
            var1,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
