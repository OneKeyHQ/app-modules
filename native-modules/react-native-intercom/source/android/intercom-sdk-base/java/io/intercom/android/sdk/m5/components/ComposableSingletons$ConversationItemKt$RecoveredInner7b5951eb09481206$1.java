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
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-1$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,287:1\n118#2:288\n1047#3,6:289\n*S KotlinDebug\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-1$1\n*L\n185#1:288\n186#1:289,6\n*E\n"
)
final class ComposableSingletons$ConversationItemKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ConversationItemKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ConversationItemKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C182@7690L6,185@7850L2,179@7566L286:ConversationItem.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               714793140,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationItemKt.lambda-1.<anonymous> (ConversationItem.kt:179)"
            );
         }

         Modifier var3 = BackgroundKt.recovered$42a3174180b3fb7a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null),
            IntercomTheme.INSTANCE.getColors(var1, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
            null,
            2,
            null
         );
         Conversation var4 = ConversationItemKt.sampleConversation$default(false, 1, null);
         int var7 = 16;
         int var8 = 0;
         float var6 = Dp.recovered$95b3d103663bf060(var7);
         var8 = 16;
         int $i$f$getDpx = 0;
         PaddingValues var5 = PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060(var8), var6);
         Conversation var10000 = var4;
         Modifier var10001 = var3;
         Object var10002 = null;
         PaddingValues var10003 = var5;
         boolean var10004 = false;
         var1.startReplaceGroup(-136807278);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationItem.kt#9igjgp");
         Composer var20 = var1;
         boolean var22 = false;
         $i$f$getDpx = 0;
         Object var10 = var20.rememberedValue();
         int var11 = 0;
         Object var10005;
         if (var10 == Composer.Companion.getEmpty()) {
            boolean var17 = false;
            PaddingValues var16 = var5;
            Object var15 = null;
            Modifier var14 = var3;
            Conversation var13 = var4;
            int var12 = 0;
            Function0 var18 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationItemKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$1$lambda$0());
            var10000 = var13;
            var10001 = var14;
            var10002 = var15;
            var10003 = var16;
            var10004 = var17;
            Object var24 = var18;
            var20.updateRememberedValue(var24);
            var10005 = var24;
         } else {
            var10005 = var10;
         }

         Function0 var19 = (Function0)var10005;
         var1.endReplaceGroup();
         ConversationItemKt.ConversationItem(var10000, var10001, (CurrentlyTypingState)var10002, var10003, var10004, var19, var1, 196608, 20);
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
