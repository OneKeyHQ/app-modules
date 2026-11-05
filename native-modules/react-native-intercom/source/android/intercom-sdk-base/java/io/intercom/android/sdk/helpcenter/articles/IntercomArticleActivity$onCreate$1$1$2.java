package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.component.IntercomTopBarIcon;
import io.intercom.android.sdk.ui.component.IntercomTopBarKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomArticleActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1$2\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,290:1\n1047#2,6:291\n*S KotlinDebug\n*F\n+ 1 IntercomArticleActivity.kt\nio/intercom/android/sdk/helpcenter/articles/IntercomArticleActivity$onCreate$1$1$2\n*L\n91#1:291,6\n*E\n"
)
final class IntercomArticleActivity$onCreate$1$1$2 implements Function2 {
   // $VF: synthetic field
   final IntercomArticleActivity this$0;

   IntercomArticleActivity$onCreate$1$1$2(IntercomArticleActivity var1) {
      this.this$0 = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C90@4099L80,95@4331L6,96@4406L6,87@3884L568:IntercomArticleActivity.kt#1wdz33");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               547021723,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity.onCreate.<anonymous>.<anonymous>.<anonymous> (IntercomArticleActivity.kt:87)"
            );
         }

         Object var10000 = null;
         Object var10001 = null;
         int var10002 = drawable.intercom_ic_close;
         Object var10003 = null;
         var1.startReplaceGroup(344428451);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomArticleActivity.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.this$0);
         IntercomArticleActivity var6 = this.this$0;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var10004;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var10004 = var8;
         } else {
            Object var15 = null;
            int var14 = var10002;
            Object var13 = null;
            Object var12 = null;
            int var10 = 0;
            final io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity recoveredCapture_86_0 = var6;
            Function0 var16 = ((kotlin.jvm.functions.Function0) () -> IntercomArticleActivity$onCreate$1$1$2.invoke$lambda$1$lambda$0(recoveredCapture_86_0));
            var10000 = var12;
            var10001 = var13;
            var10002 = var14;
            var10003 = var15;
            Object var11 = var16;
            var4.updateRememberedValue(var11);
            var10004 = var11;
         }

         Function0 var3 = (Function0)var10004;
         var1.endReplaceGroup();
         Function0 var17 = var3;
         Object var18 = var10003;
         int var19 = var10002;
         IntercomTopBarKt.recovered$4ca9f42ae4e1cbf3(
            (Modifier)var10000,
            (String)var10001,
            new IntercomTopBarIcon(var19, (String)var18, var17),
            null,
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
            null,
            null,
            var1,
            IntercomTopBarIcon.$stable << 6,
            203
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(IntercomArticleActivity var0) {
      var0.finish();
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
