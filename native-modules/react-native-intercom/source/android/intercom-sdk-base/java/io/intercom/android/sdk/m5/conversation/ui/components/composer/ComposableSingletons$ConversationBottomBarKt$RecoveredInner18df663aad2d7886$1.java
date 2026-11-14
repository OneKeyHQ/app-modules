package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$ConversationBottomBarKt$lambda-4$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,297:1\n118#2:298\n*S KotlinDebug\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$ConversationBottomBarKt$lambda-4$1\n*L\n265#1:298\n*E\n"
)
final class ComposableSingletons$ConversationBottomBarKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ConversationBottomBarKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ConversationBottomBarKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C264@12531L4767:ConversationBottomBar.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -753965082,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$ConversationBottomBarKt.lambda-4.<anonymous> (ConversationBottomBar.kt:264)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var3 = 500;
         int var4 = 0;
         SurfaceKt.recovered$ecd79a99e04a9534(
            SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var3)),
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableSingletons$ConversationBottomBarKt.INSTANCE.recovered$fd838d44303763da(),
            var1,
            12582918,
            126
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
