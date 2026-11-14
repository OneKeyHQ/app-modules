package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nJumpToBottom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/Dp\n*L\n1#1,137:1\n118#2:138\n52#3:139\n*S KotlinDebug\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt$JumpToBottom$1\n*L\n52#1:138\n52#1:139\n*E\n"
)
final class JumpToBottomKt$JumpToBottom$1 implements Function3 {
   // $VF: synthetic field
   final String $badgeText;
   // $VF: synthetic field
   final long $badgeColor;
   // $VF: synthetic field
   final long $badgeContentColor;

   JumpToBottomKt$JumpToBottom$1(String var1, long var2, long var4) {
      this.$badgeText = var1;
      this.$badgeColor = var2;
      this.$badgeContentColor = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BadgedBox");
      ComposerKt.sourceInformation(var2, "C*49@1971L269:JumpToBottom.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1557616541, var3, -1, "io.intercom.android.sdk.ui.component.JumpToBottom.<anonymous> (JumpToBottom.kt:48)");
         }

         if (this.$badgeText != null) {
            String var4 = this.$badgeText;
            long var5 = this.$badgeColor;
            long var7 = this.$badgeContentColor;
            String var9 = var4;
            int var10 = 0;
            Modifier var10000 = Modifier.Companion;
            int var11 = -5;
            int var12 = 0;
            float var14 = Dp.recovered$95b3d103663bf060(var11);
            float var15 = JumpToBottomKt.getJumpBottomPadding();
            int var13 = 0;
            JumpToBottomKt.recovered$00a1b485d97ecfc4(
               OffsetKt.recovered$ed7db252cbfb0f30(var10000, Dp.recovered$95b3d103663bf060(var14 - var15), JumpToBottomKt.getJumpBottomPadding()),
               var9,
               var5,
               var7,
               var2,
               6,
               0
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
