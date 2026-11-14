package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.FlowRowScope;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nQuickReplies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$QuickReplies$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,144:1\n1869#2:145\n1870#2:155\n1047#3,6:146\n118#4:152\n118#4:153\n118#4:154\n*S KotlinDebug\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt$QuickReplies$1\n*L\n40#1:145\n40#1:155\n42#1:146,6\n46#1:152\n49#1:153\n50#1:154\n*E\n"
)
final class QuickRepliesKt$QuickReplies$1 implements Function3 {
   // $VF: synthetic field
   final List $quickReplies;
   // $VF: synthetic field
   final Function1 $onQuickReplyClick;

   QuickRepliesKt$QuickReplies$1(List var1, Function1 var2) {
      this.$quickReplies = var1;
      this.$onQuickReplyClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(FlowRowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$FlowRow");
      ComposerKt.sourceInformation(var2, "C*41@1758L61,46@1984L6,47@2051L6,49@2180L6,44@1863L358,51@2237L358,40@1718L877:QuickReplies.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -458232018, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReplies.<anonymous> (QuickReplies.kt:39)"
            );
         }

         Iterable var4 = this.$quickReplies;
         Function1 var5 = this.$onQuickReplyClick;
         int var6 = 0;

         for (Object var8 : var4) {
            QuickReply var9 = (QuickReply)var8;
            int var10 = 0;
            var2.startReplaceGroup(373515393);
            ComposerKt.sourceInformation(var2, "CC(remember):QuickReplies.kt#9igjgp");
            Composer var11 = var2;
            boolean var12 = var2.changed(var5) | var2.changed(var9);
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (!var12 && var14 != Composer.Companion.getEmpty()) {
               var10000 = var14;
            } else {
               int var16 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_778_0 = var5;
               final io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply recoveredCapture_778_1 = var9;
               Object var17 = ((kotlin.jvm.functions.Function0) () -> QuickRepliesKt$QuickReplies$1.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_778_0, recoveredCapture_778_1));
               var11.updateRememberedValue(var17);
               var10000 = var17;
            }

            Function0 var18 = (Function0)var10000;
            var2.endReplaceGroup();
            IntercomCardStyle var10003 = IntercomCardStyle.INSTANCE;
            int var22 = 20;
            int var19 = 0;
            Shape var10004 = RoundedCornerShapeKt.recovered$8dfaa2739ee8e479(Dp.recovered$95b3d103663bf060(var22));
            long var10005 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            long var10006 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            float var10007 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            int $this$dp$ivxx = 1;
            int $i$f$getDpxx = 0;
            IntercomCardKt.IntercomCard(
               var18,
               null,
               false,
               var10003.recovered$24b4efdd2276d096(
                  var10004,
                  var10005,
                  var10006,
                  var10007,
                  BorderStrokeKt.recovered$91879a1bef2fb536(
                     Dp.recovered$95b3d103663bf060($this$dp$ivxx),
                     IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBorder().recovered$e2b74d614aa09788()
                  ),
                  0L,
                  var2,
                  3072 | IntercomCardStyle.$stable << 18,
                  32
               ),
               null,
               ComposableLambdaKt.rememberComposableLambda(-1399332631, true, new QuickRepliesKt$QuickReplies$1$1$2(var9), var2, 54),
               var2,
               196608 | Style.$stable << 9,
               22
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(Function1 var0, QuickReply var1) {
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((FlowRowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
