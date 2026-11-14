package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Modifier.Companion;
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
final class JumpToBottomKt$UnreadBadge$1 implements Function3 {
   // $VF: synthetic field
   final long $contentColor;
   // $VF: synthetic field
   final String $badgeText;

   JumpToBottomKt$UnreadBadge$1(long var1, String var3) {
      this.$contentColor = var1;
      this.$badgeText = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(RowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$Badge");
      ComposerKt.sourceInformation(var2, "C127@4553L10,124@4450L315:JumpToBottom.kt#yr602f");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-480925784, var3, -1, "io.intercom.android.sdk.ui.component.UnreadBadge.<anonymous> (JumpToBottom.kt:124)");
         }

         Companion var4 = Modifier.Companion;
         TextStyle var6 = IntercomTheme.INSTANCE.getTypography(var2, 6).getType05();
         int var7 = TextAlign.Companion.recovered$d707b9dd64876179();
         long var8 = TextUnitKt.getSp(12);
         FontWeight var10 = FontWeight.Companion.getBold();
         TextStyle var5 = TextStyle.recovered$cf01d8b71690b5c5(
            var6,
            this.$contentColor,
            var8,
            var10,
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
            var7,
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
         );
         TextKt.recovered$5b114ab32a4f4190(
            this.$badgeText, var4, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var5, var2, 48, 0, 131068
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
