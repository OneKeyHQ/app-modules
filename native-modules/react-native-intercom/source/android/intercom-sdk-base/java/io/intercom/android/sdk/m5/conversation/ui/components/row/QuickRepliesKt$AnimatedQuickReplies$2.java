package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.animation.AnimatedVisibilityScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nQuickReplies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$AnimatedQuickReplies$2\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,144:1\n118#2:145\n*S KotlinDebug\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$AnimatedQuickReplies$2\n*L\n114#1:145\n*E\n"
)
final class QuickRepliesKt$AnimatedQuickReplies$2 implements Function3 {
   // $VF: synthetic field
   final List $replyOptions;
   // $VF: synthetic field
   final Function1 $onReplyClicked;

   QuickRepliesKt$AnimatedQuickReplies$2(List var1, Function1 var2) {
      this.$replyOptions = var1;
      this.$onReplyClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedVisibilityScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$AnimatedVisibility");
      ComposerKt.sourceInformation(var2, "C110@3973L227:QuickReplies.kt#hr3bi5");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            992499481, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AnimatedQuickReplies.<anonymous> (QuickReplies.kt:110)"
         );
      }

      Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
      int var4 = 16;
      int var5 = 0;
      float var10001 = Dp.recovered$95b3d103663bf060(var4);
      var4 = 16;
      var5 = 0;
      QuickRepliesKt.ReplyOptions(
         PaddingKt.recovered$03b481d31d62885a(var10000, var10001, 0.0F, Dp.recovered$95b3d103663bf060(var4), 0.0F, 10, null),
         this.$replyOptions,
         this.$onReplyClicked,
         var2,
         6,
         0
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((AnimatedVisibilityScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
