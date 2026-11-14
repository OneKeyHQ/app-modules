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
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-5$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,287:1\n118#2:288\n1047#3,6:289\n*S KotlinDebug\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationItemKt$lambda-5$1\n*L\n255#1:288\n256#1:289,6\n*E\n"
)
final class ComposableSingletons$ConversationItemKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$ConversationItemKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$ConversationItemKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C245@9719L6,255@10151L2,242@9595L558:ConversationItem.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -334179716,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationItemKt.lambda-5.<anonymous> (ConversationItem.kt:242)"
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
         Avatar var10004 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10004, "create(...)");
         CurrentlyTypingState var5 = new CurrentlyTypingState(new AvatarWrapper(var10004, false), false, null, TypingIndicatorType.ADMIN, 6, null);
         int var8 = 16;
         int var9 = 0;
         float var7 = Dp.recovered$95b3d103663bf060(var8);
         var9 = 16;
         int $i$f$getDpx = 0;
         PaddingValues var6 = PaddingKt.recovered$4ebb4826aa57abc6(Dp.recovered$95b3d103663bf060(var9), var7);
         Conversation var10000 = var4;
         Modifier var10001 = var3;
         CurrentlyTypingState var10002 = var5;
         PaddingValues var10003 = var6;
         boolean var26 = false;
         var1.startReplaceGroup(1598506294);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationItem.kt#9igjgp");
         Composer var21 = var1;
         boolean var23 = false;
         $i$f$getDpx = 0;
         Object var11 = var21.rememberedValue();
         int var12 = 0;
         Object var10005;
         if (var11 == Composer.Companion.getEmpty()) {
            boolean var18 = false;
            PaddingValues var17 = var6;
            CurrentlyTypingState var16 = var5;
            Modifier var15 = var3;
            Conversation var14 = var4;
            int var13 = 0;
            Function0 var19 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationItemKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0());
            var10000 = var14;
            var10001 = var15;
            var10002 = var16;
            var10003 = var17;
            var26 = var18;
            Object var25 = var19;
            var21.updateRememberedValue(var25);
            var10005 = var25;
         } else {
            var10005 = var11;
         }

         Function0 var20 = (Function0)var10005;
         var1.endReplaceGroup();
         ConversationItemKt.ConversationItem(var10000, var10001, var10002, var10003, var26, var20, var1, 196608, 16);
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
