package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-4$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,287:1\n118#2:288\n1047#3,6:289\n*S KotlinDebug\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-4$1\n*L\n234#1:288\n235#1:289,6\n*E\n"
)
final class ComposableSingletons$ConversationItemKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ConversationItemKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ConversationItemKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C224@8932L6,234@9478L2,221@8808L672:ConversationItem.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1429931941,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationItemKt.lambda-4.<anonymous> (ConversationItem.kt:221)"
            );
         }

         Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         Conversation var4 = Conversation.copy$default(
            ConversationItemKt.sampleConversation$default(false, 1, null),
            null,
            "This is a very long conversation title that should be truncated with ellipsis",
            false,
            null,
            CollectionsKt.listOf(
               new Part$Builder().withSummary("This is the last message received/sent to/from this user").withCreatedAt(System.currentTimeMillis())
            ),
            null,
            null,
            null,
            false,
            false,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            2097133,
            null
         );
         int var6 = 16;
         int var7 = 0;
         float var10000 = Dp.recovered$95b3d103663bf060(var6);
         var6 = 16;
         var7 = 0;
         PaddingValues var5 = PaddingKt.recovered$4ebb4826aa57abc6(var10000, Dp.recovered$95b3d103663bf060(var6));
         Conversation var24 = var4;
         Modifier var10001 = var3;
         Object var10002 = null;
         PaddingValues var10003 = var5;
         boolean var10004 = false;
         var1.startReplaceGroup(90932949);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationItem.kt#9igjgp");
         Composer var22 = var1;
         boolean var8 = false;
         int var9 = 0;
         Object var10 = var22.rememberedValue();
         int var11 = 0;
         Object var10005;
         if (var10 == Composer.Companion.getEmpty()) {
            boolean var17 = false;
            PaddingValues var16 = var5;
            Object var15 = null;
            Modifier var14 = var3;
            Conversation var13 = var4;
            int var12 = 0;
            Function0 var18 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationItemKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var24 = var13;
            var10001 = var14;
            var10002 = var15;
            var10003 = var16;
            var10004 = var17;
            Object var23 = var18;
            var22.updateRememberedValue(var23);
            var10005 = var23;
         } else {
            var10005 = var10;
         }

         Function0 var20 = (Function0)var10005;
         var1.endReplaceGroup();
         ConversationItemKt.ConversationItem(var24, var10001, (CurrentlyTypingState)var10002, var10003, var10004, var20, var1, 199680, 20);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
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
