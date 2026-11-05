package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$ConversationEndedCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ConversationEndedCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ConversationEndedCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C64@2384L213:ConversationEndedCard.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -867938680,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationEndedCardKt.lambda-1.<anonymous> (ConversationEndedCard.kt:64)"
            );
         }

         ConversationEndedCardKt.ConversationEndedCard(
            null,
            null,
            new ComposerState$ConversationEnded(new StringRes(string.intercom_conversation_has_ended, null, 2, null), null, 2, null),
            var1,
            (StringProvider.$stable | StringProvider.$stable) << 6,
            3
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
