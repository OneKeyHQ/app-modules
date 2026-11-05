package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nReactionsComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ReactionsComponent.kt\nio/intercom/android/sdk/helpcenter/articles/ComposableSingletons$ReactionsComponentKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,189:1\n1047#2,6:190\n1047#2,6:196\n1047#2,6:202\n*S KotlinDebug\n*F\n+ 1 ReactionsComponent.kt\nio/intercom/android/sdk/helpcenter/articles/ComposableSingletons$ReactionsComponentKt$lambda-1$1\n*L\n148#1:190,6\n149#1:196,6\n150#1:202,6\n*E\n"
)
final class ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C147@6131L2,148@6175L2,149@6217L2,145@5983L251:ReactionsComponent.kt#1wdz33");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1445652918,
               var2,
               -1,
               "io.intercom.android.sdk.helpcenter.articles.ComposableSingletons$ReactionsComponentKt.lambda-1.<anonymous> (ReactionsComponent.kt:145)"
            );
         }

         Object var10000 = null;
         ArticleViewState$ReactionState var10001 = ArticleViewState$ReactionState.Companion.getDefaultReactionState();
         var1.startReplaceGroup(-613641924);
         ComposerKt.sourceInformation(var1, "CC(remember):ReactionsComponent.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            ArticleViewState$ReactionState var11 = var10001;
            Object var10 = null;
            int var9 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            Object var27 = var12;
            var4.updateRememberedValue(var27);
            var10002 = (Function0)var27;
         } else {
            var10002 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var3;
         var1.startReplaceGroup(-613640516);
         ComposerKt.sourceInformation(var1, "CC(remember):ReactionsComponent.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var36 = var3;
            ArticleViewState$ReactionState var34 = var10001;
            Object var32 = var10000;
            int var28 = 0;
            Function0 var13 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$3$lambda$2());
            var10000 = var32;
            var10001 = var34;
            var10002 = var36;
            Object var29 = var13;
            var4.updateRememberedValue(var29);
            var10003 = (Function0)var29;
         } else {
            var10003 = (Function0)var7;
         }

         var3 = (kotlin.jvm.functions.Function0) var10003;
         var1.endReplaceGroup();
         var10003 = var3;
         var1.startReplaceGroup(-613639172);
         ComposerKt.sourceInformation(var1, "CC(remember):ReactionsComponent.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var38 = var3;
            Function0 var37 = (kotlin.jvm.functions.Function0) var10002;
            ArticleViewState$ReactionState var35 = var10001;
            Object var33 = var10000;
            int var30 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ReactionsComponentKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$5$lambda$4());
            var10000 = var33;
            var10001 = var35;
            var10002 = var37;
            var10003 = var38;
            Object var31 = var14;
            var4.updateRememberedValue(var31);
            var10004 = var31;
         } else {
            var10004 = var7;
         }

         var3 = (Function0)var10004;
         var1.endReplaceGroup();
         ReactionsComponentKt.ReactionsComponent((Modifier)var10000, var10001, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, var3, var1, 28080, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4() {
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
