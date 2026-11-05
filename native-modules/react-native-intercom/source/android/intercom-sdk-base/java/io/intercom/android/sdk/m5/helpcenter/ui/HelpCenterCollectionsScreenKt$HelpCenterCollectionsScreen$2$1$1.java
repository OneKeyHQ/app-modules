package io.intercom.android.sdk.m5.helpcenter.ui;

import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiEffects;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiEffects$NavigateToCollectionContent;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$2$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Function1 $onAutoNavigateToCollection;

   HelpCenterCollectionsScreenKt$HelpCenterCollectionsScreen$2$1$1(Function1 var1) {
      this.$onAutoNavigateToCollection = var1;
   }

   public final Object emit(CollectionsUiEffects var1, Continuation var2) {
      if (var1 instanceof CollectionsUiEffects$NavigateToCollectionContent) {
         this.$onAutoNavigateToCollection.invoke(((CollectionsUiEffects$NavigateToCollectionContent)var1).getCollectionId());
         return Unit.INSTANCE;
      } else {
         throw new NoWhenBranchMatchedException();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((CollectionsUiEffects)var1, var2);
   }
}
