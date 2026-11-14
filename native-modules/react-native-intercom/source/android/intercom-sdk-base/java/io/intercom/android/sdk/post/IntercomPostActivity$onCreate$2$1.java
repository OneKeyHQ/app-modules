package io.intercom.android.sdk.post;

import androidx.compose.foundation.ScrollState;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.graphics.Color;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,370:1\n1047#2,6:371\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1\n*L\n134#1:371,6\n*E\n"
)
final class IntercomPostActivity$onCreate$2$1 implements Function2 {
   // $VF: synthetic field
   final IntercomPostActivity this$0;
   // $VF: synthetic field
   final ScrollState $scrollState;

   IntercomPostActivity$onCreate$2$1(IntercomPostActivity var1, ScrollState var2) {
      this.this$0 = var1;
      this.$scrollState = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C133@5509L20,133@5475L55,138@5674L757,155@6465L3331,206@9816L2230,136@5585L6461:IntercomPostActivity.kt#hv1x2k");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1094919204, var2, -1, "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous>.<anonymous> (IntercomPostActivity.kt:133)"
            );
         }

         String var10000 = "";
         var1.startReplaceGroup(-333903347);
         ComposerKt.sourceInformation(var1, "CC(remember):IntercomPostActivity.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = var1.changedInstance(this.this$0);
         IntercomPostActivity var6 = this.this$0;
         int var7 = 0;
         Object var8 = var4.rememberedValue();
         int var9 = 0;
         Object var15;
         if (!var5 && var8 != Composer.Companion.getEmpty()) {
            var15 = (Function2)var8;
         } else {
            String var12 = "";
            int var10 = 0;
            var15 = new IntercomPostActivity$onCreate$2$1$1$1(var6, null);
            var10000 = var12;
            Object var11 = var15;
            var4.updateRememberedValue(var11);
            var15 = (Function2)var11;
         }

         Function2 var3 = (kotlin.jvm.functions.Function2) var15;
         var1.endReplaceGroup();
         EffectsKt.LaunchedEffect(var10000, var3, var1, 6);
         Part var13 = IntercomPostActivity.access$getPart(this.this$0);
         long var14 = Color.Companion.recovered$235b30c090a399c5();
         ScaffoldKt.recovered$857cf99fc8161460(
            null,
            ComposableLambdaKt.rememberComposableLambda(-2005201696, true, new IntercomPostActivity$onCreate$2$1$2(var13, this.this$0), var1, 54),
            ComposableLambdaKt.rememberComposableLambda(1401728063, true, new IntercomPostActivity$onCreate$2$1$3(this.this$0, var13), var1, 54),
            null,
            null,
            0,
            var14,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(-76912331, true, new IntercomPostActivity$onCreate$2$1$4(this.$scrollState, var13), var1, 54),
            var1,
            806879664,
            441
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
