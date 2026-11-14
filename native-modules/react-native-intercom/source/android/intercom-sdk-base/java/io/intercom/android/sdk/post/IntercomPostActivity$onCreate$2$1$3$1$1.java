package io.intercom.android.sdk.post;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.activities.ConversationReactionListener;
import io.intercom.android.sdk.conversation.ReactionInputView;
import io.intercom.android.sdk.conversation.ReactionListener;
import io.intercom.android.sdk.metrics.MetricTracker$ReactionLocation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReactionReply;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomPostActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$3$1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,370:1\n1047#2,6:371\n1047#2,6:377\n1047#2,6:383\n*S KotlinDebug\n*F\n+ 1 IntercomPostActivity.kt\nio/intercom/android/sdk/post/IntercomPostActivity$onCreate$2$1$3$1$1\n*L\n164#1:371,6\n190#1:377,6\n196#1:383,6\n*E\n"
)
final class IntercomPostActivity$onCreate$2$1$3$1$1 implements Function3 {
   // $VF: synthetic field
   final Part $part;
   // $VF: synthetic field
   final IntercomPostActivity this$0;

   IntercomPostActivity$onCreate$2$1$3$1$1(Part var1, IntercomPostActivity var2) {
      this.$part = var1;
      this.this$0 = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BottomBarContent");
      ComposerKt.sourceInformation(var2, "C:IntercomPostActivity.kt#hv1x2k");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1915412601,
               var3,
               -1,
               "io.intercom.android.sdk.post.IntercomPostActivity.onCreate.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (IntercomPostActivity.kt:160)"
            );
         }

         if (!ReactionReply.isNull(this.$part.getReactionReply())) {
            var2.startReplaceGroup(1612520741);
            ComposerKt.sourceInformation(var2, "163@6967L1561,162@6896L1679");
            var2.startReplaceGroup(-1056358321);
            ComposerKt.sourceInformation(var2, "CC(remember):IntercomPostActivity.kt#9igjgp");
            Composer var25 = var2;
            boolean var29 = var2.changedInstance(this.$part) | var2.changedInstance(this.this$0);
            Part var33 = this.$part;
            IntercomPostActivity var35 = this.this$0;
            int var37 = 0;
            Object var39 = var25.rememberedValue();
            int var41 = 0;
            Object var55;
            if (!var29 && var39 != Composer.Companion.getEmpty()) {
               var55 = var39;
            } else {
               int var43 = 0;
               final io.intercom.android.sdk.models.Part recoveredCapture_1212_0 = var33;
               final io.intercom.android.sdk.post.IntercomPostActivity recoveredCapture_1212_1 = var35;
               Object var45 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1212_0) -> IntercomPostActivity$onCreate$2$1$3$1$1.invoke$lambda$2$lambda$1(recoveredCapture_1212_0, recoveredCapture_1212_1, (android.content.Context)recoveredArgument_1212_0));
               var25.updateRememberedValue(var45);
               var55 = var45;
            }

            Function1 var21 = (Function1)var55;
            var2.endReplaceGroup();
            AndroidView_androidKt.AndroidView(var21, null, null, var2, 0, 6);
            var2.endReplaceGroup();
         } else if (!IntercomPostActivity.access$isComposerVisible(this.this$0)) {
            var2.startReplaceGroup(1615277292);
            var2.endReplaceGroup();
         } else {
            var2.startReplaceGroup(1614313192);
            ComposerKt.sourceInformation(var2, "189@8812L22,191@9013L10,188@8728L424,195@9281L22,197@9465L10,194@9197L407");
            Modifier var10000 = Modifier.Companion;
            boolean var10001 = false;
            Object var10002 = null;
            Object var10003 = null;
            Object var10004 = null;
            var2.startReplaceGroup(-1056300820);
            ComposerKt.sourceInformation(var2, "CC(remember):IntercomPostActivity.kt#9igjgp");
            Composer var6 = var2;
            boolean var7 = var2.changedInstance(this.this$0);
            IntercomPostActivity var8 = this.this$0;
            int var9 = 0;
            Object var10 = var6.rememberedValue();
            int var11 = 0;
            Object var10005;
            if (!var7 && var10 != Composer.Companion.getEmpty()) {
               var10005 = var10;
            } else {
               Object var18 = null;
               Object var17 = null;
               Object var16 = null;
               boolean var15 = false;
               Modifier var14 = var10000;
               int var12 = 0;
               final io.intercom.android.sdk.post.IntercomPostActivity recoveredCapture_1210_0 = var8;
               Function0 var19 = ((kotlin.jvm.functions.Function0) () -> IntercomPostActivity$onCreate$2$1$3$1$1.invoke$lambda$4$lambda$3(recoveredCapture_1210_0));
               var10000 = var14;
               var10001 = var15;
               var10002 = var16;
               var10003 = var17;
               var10004 = var18;
               Object var13 = var19;
               var6.updateRememberedValue(var13);
               var10005 = var13;
            }

            Function0 var5 = (Function0)var10005;
            var2.endReplaceGroup();
            Modifier var4 = ClickableKt.recovered$1c9660c41fd850dc(
               var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var5, 15, null
            );
            String var52 = this.this$0.getString(string.intercom_reply_to_conversation);
            Intrinsics.checkNotNullExpressionValue(var52, "getString(...)");
            String var22 = var52;
            TextStyle var26 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04();
            long var30 = ColorKt.Color(4288585374L);
            TextKt.recovered$5b114ab32a4f4190(
               var22, var4, var30, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var26, var2, 384, 0, 131064
            );
            var10000 = Modifier.Companion;
            var10001 = false;
            var10002 = null;
            var10003 = null;
            var10004 = null;
            var2.startReplaceGroup(-1056285812);
            ComposerKt.sourceInformation(var2, "CC(remember):IntercomPostActivity.kt#9igjgp");
            var6 = var2;
            var7 = var2.changedInstance(this.this$0);
            var8 = this.this$0;
            var9 = 0;
            var10 = var6.rememberedValue();
            var11 = 0;
            if (!var7 && var10 != Composer.Companion.getEmpty()) {
               var10005 = var10;
            } else {
               Object var50 = null;
               Object var49 = null;
               Object var48 = null;
               boolean var47 = false;
               Modifier var46 = var10000;
               int var42 = 0;
               final io.intercom.android.sdk.post.IntercomPostActivity recoveredCapture_1211_0 = var8;
               Function0 var51 = ((kotlin.jvm.functions.Function0) () -> IntercomPostActivity$onCreate$2$1$3$1$1.invoke$lambda$6$lambda$5(recoveredCapture_1211_0));
               var10000 = var46;
               var10001 = var47;
               var10002 = var48;
               var10003 = var49;
               var10004 = var50;
               Object var44 = var51;
               var6.updateRememberedValue(var44);
               var10005 = var44;
            }

            var5 = (Function0)var10005;
            var2.endReplaceGroup();
            var4 = ClickableKt.recovered$1c9660c41fd850dc(
               var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var5, 15, null
            );
            String var54 = this.this$0.getString(string.intercom_send);
            Intrinsics.checkNotNullExpressionValue(var54, "getString(...)");
            String var24 = var54;
            TextStyle var28 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04();
            long var32 = ColorKt.Color(4288585374L);
            TextKt.recovered$5b114ab32a4f4190(
               var24, var4, var32, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var28, var2, 384, 0, 131064
            );
            var2.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final ReactionInputView invoke$lambda$2$lambda$1(Part var0, IntercomPostActivity var1, Context var2) {
      Intrinsics.checkNotNullParameter(var2, "it");
      ReactionInputView var3 = new ReactionInputView(var2, null);
      ReactionInputView var4 = var3;
      int var5 = 0;
      var4.setLayoutParams(new LayoutParams(-1, -2));
      ReactionListener var6 = new ConversationReactionListener(
         MetricTracker$ReactionLocation.IN_APP,
         var0.getId(),
         IntercomPostActivity.access$getConversationId(var1),
         IntercomPostActivity.access$getInjector(var1).getApi(),
         IntercomPostActivity.access$getInjector(var1).getMetricTracker()
      );
      var4.setUpReactions(var0.getReactionReply(), true, var6);
      return var3;
   }

   private static final Unit invoke$lambda$4$lambda$3(IntercomPostActivity var0) {
      IntercomPostActivity.access$openConversation(var0);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$6$lambda$5(IntercomPostActivity var0) {
      IntercomPostActivity.access$openConversation(var0);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
