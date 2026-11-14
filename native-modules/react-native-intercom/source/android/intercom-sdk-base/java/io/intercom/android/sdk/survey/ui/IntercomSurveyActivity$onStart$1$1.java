package io.intercom.android.sdk.survey.ui;

import io.intercom.android.sdk.survey.SurveyEffects;
import io.intercom.android.sdk.survey.SurveyEffects$ExitSurvey;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class IntercomSurveyActivity$onStart$1$1 implements FlowCollector {
   // $VF: synthetic field
   final IntercomSurveyActivity this$0;

   IntercomSurveyActivity$onStart$1$1(IntercomSurveyActivity var1) {
      this.this$0 = var1;
   }

   public final Object emit(SurveyEffects var1, Continuation var2) {
      if (Intrinsics.areEqual(var1, SurveyEffects$ExitSurvey.INSTANCE)) {
         this.this$0.finish();
         return Unit.INSTANCE;
      } else {
         throw new NoWhenBranchMatchedException();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((SurveyEffects)var1, var2);
   }
}
