package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HeaderMenuItemRowKt$UnreadBadge$1 implements Function3 {
   // $VF: synthetic field
   final String $badgeText;
   // $VF: synthetic field
   final long $contentColor;

   HeaderMenuItemRowKt$UnreadBadge$1(String var1, long var2) {
      this.$badgeText = var1;
      this.$contentColor = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Badge");
      ComposerKt.sourceInformation(var2, "C162@5025L10,160@4955L282:HeaderMenuItemRow.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1370135743, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UnreadBadge.<anonymous> (HeaderMenuItemRow.kt:160)"
            );
         }

         String var10000 = this.$badgeText;
         TextStyle var4 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType05();
         int var5 = TextAlign.Companion.recovered$d707b9dd64876179();
         long var6 = TextUnitKt.getSp(10);
         FontWeight var8 = FontWeight.Companion.getW400();
         TextKt.recovered$5b114ab32a4f4190(
            var10000,
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            TextStyle.recovered$cf01d8b71690b5c5(
               var4,
               this.$contentColor,
               var6,
               var8,
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               var5,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16744440,
               null
            ),
            var2,
            0,
            0,
            131070
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((RowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
