package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.OffsetKt;
import androidx.compose.material3.BadgeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHeaderMenuItemRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,208:1\n118#2:209\n118#2:210\n*S KotlinDebug\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$1\n*L\n86#1:209\n92#1:210\n*E\n"
)
final class HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$1 implements Function3 {
   // $VF: synthetic field
   final boolean $showUnreadDot;
   // $VF: synthetic field
   final String $badgeText;
   // $VF: synthetic field
   final boolean $showBadge;

   HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$1(boolean var1, String var2, boolean var3) {
      this.$showUnreadDot = var1;
      this.$badgeText = var2;
      this.$showBadge = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BadgedBox");
      ComposerKt.sourceInformation(var2, "C:HeaderMenuItemRow.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -415284443, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemRow.<anonymous> (HeaderMenuItemRow.kt:83)"
            );
         }

         if (this.$showUnreadDot) {
            var2.startReplaceGroup(-1594514865);
            ComposerKt.sourceInformation(var2, "84@2704L153");
            Modifier var12 = Modifier.Companion;
            int var7 = -10;
            int var10 = 0;
            float var13 = Dp.recovered$95b3d103663bf060(var7);
            var7 = 10;
            var10 = 0;
            BadgeKt.recovered$d9697d11cb3615b2(
               OffsetKt.recovered$ed7db252cbfb0f30(var12, var13, Dp.recovered$95b3d103663bf060(var7)), ColorKt.Color(4292544041L), 0L, null, var2, 54, 12
            );
            var2.endReplaceGroup();
         } else {
            var2.startReplaceGroup(-1594323502);
            ComposerKt.sourceInformation(var2, "90@2965L144");
            if (this.$badgeText == null || !this.$showBadge) {
               var2.endReplaceGroup();
               if (ComposerKt.isTraceInProgress()) {
                  ComposerKt.traceEventEnd();
               }

               return;
            }

            Modifier var10000 = Modifier.Companion;
            int var4 = -6;
            int var5 = 0;
            float var10001 = Dp.recovered$95b3d103663bf060(var4);
            var4 = 4;
            var5 = 0;
            HeaderMenuItemRowKt.access$UnreadBadge(
               OffsetKt.recovered$ed7db252cbfb0f30(var10000, var10001, Dp.recovered$95b3d103663bf060(var4)), this.$badgeText, var2, 6, 0
            );
            var2.endReplaceGroup();
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
