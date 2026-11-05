package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded$ConversationEndedCta;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.ActualString;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationEndedCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationEndedCard.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationEndedCardKt$lambda-4$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,124:1\n1047#2,6:125\n*S KotlinDebug\n*F\n+ 1 ConversationEndedCard.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationEndedCardKt$lambda-4$1\n*L\n112#1:125,6\n*E\n"
)
final class ComposableSingletons$ConversationEndedCardKt$RecoveredInner18df663aad2d7886$1 implements Function2 {
   public static final ComposableSingletons$ConversationEndedCardKt$RecoveredInner18df663aad2d7886$1 INSTANCE = new ComposableSingletons$ConversationEndedCardKt$RecoveredInner18df663aad2d7886$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C111@3998L2,110@3947L561:ConversationEndedCard.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -324942016,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationEndedCardKt.lambda-4.<anonymous> (ConversationEndedCard.kt:110)"
            );
         }

         Object var10000 = null;
         var1.startReplaceGroup(-1158189258);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationEndedCard.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function0 var12;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var10 = null;
            int var9 = 0;
            var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$ConversationEndedCardKt$RecoveredInner18df663aad2d7886$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            Object var11 = var12;
            var4.updateRememberedValue(var11);
            var12 = (Function0)var11;
         } else {
            var12 = (Function0)var7;
         }

         Function0 var3 = var12;
         var1.endReplaceGroup();
         ConversationEndedCardKt.ConversationEndedCard(
            (Modifier)var10000,
            var3,
            new ComposerState$ConversationEnded(
               new ActualString("You have another conversation about this issue"),
               new ComposerState$ConversationEnded$ConversationEndedCta(
                  new ActualString("Continue the conversation"), drawable.intercom_send_message_icon, "123"
               )
            ),
            var1,
            48 | (StringProvider.$stable | StringProvider.$stable) << 6,
            1
         );
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
