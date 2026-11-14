package io.intercom.android.sdk.m5.home.ui;

import io.intercom.android.sdk.m5.home.states.HomeUiEffects;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects$NavigateToConversation;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects$NavigateToMessages;
import io.intercom.android.sdk.m5.home.states.HomeUiEffects$NavigateToNewConversation;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HomeScreenKt$HomeScreen$1$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Function0 $navigateToMessages;
   // $VF: synthetic field
   final Function0 $navigateToNewConversation;
   // $VF: synthetic field
   final Function1 $navigateToExistingConversation;

   HomeScreenKt$HomeScreen$1$1$1(Function0 var1, Function0 var2, Function1 var3) {
      this.$navigateToMessages = var1;
      this.$navigateToNewConversation = var2;
      this.$navigateToExistingConversation = var3;
   }

   public final Object emit(HomeUiEffects var1, Continuation var2) {
      HomeUiEffects var3 = var1;
      if (Intrinsics.areEqual(var3, HomeUiEffects$NavigateToMessages.INSTANCE)) {
         this.$navigateToMessages.invoke();
      } else if (Intrinsics.areEqual(var3, HomeUiEffects$NavigateToNewConversation.INSTANCE)) {
         this.$navigateToNewConversation.invoke();
      } else {
         if (!(var3 instanceof HomeUiEffects$NavigateToConversation)) {
            throw new NoWhenBranchMatchedException();
         }

         this.$navigateToExistingConversation.invoke(((HomeUiEffects$NavigateToConversation)var1).getConversationId());
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((HomeUiEffects)var1, var2);
   }
}
