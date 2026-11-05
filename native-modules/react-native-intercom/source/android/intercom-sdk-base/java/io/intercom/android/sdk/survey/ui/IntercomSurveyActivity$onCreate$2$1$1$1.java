package io.intercom.android.sdk.survey.ui;

import io.intercom.android.sdk.survey.SurveyViewModel;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReferenceImpl;
import kotlinx.coroutines.CoroutineScope;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomSurveyActivity$onCreate$2$1$1$1 extends FunctionReferenceImpl implements Function1 {
   IntercomSurveyActivity$onCreate$2$1$1$1(Object var1) {
      super(1, var1, SurveyViewModel.class, "continueClicked", "continueClicked(Lkotlinx/coroutines/CoroutineScope;)V", 0);
   }

   public final void invoke(CoroutineScope var1) {
      ((SurveyViewModel)this.receiver).continueClicked(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1) {
      this.invoke((CoroutineScope)var1);
      return Unit.INSTANCE;
   }
}
