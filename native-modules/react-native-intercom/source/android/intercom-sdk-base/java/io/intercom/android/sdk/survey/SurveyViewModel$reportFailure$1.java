package io.intercom.android.sdk.survey;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@DebugMetadata(f = "SurveyViewModel.kt", l = 463, i = {}, s = {}, n = {}, m = "reportFailure", c = "io.intercom.android.sdk.survey.SurveyViewModel")
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class SurveyViewModel$reportFailure$1 extends ContinuationImpl {
   // $VF: synthetic field
   Object result;
   // $VF: synthetic field
   final SurveyViewModel this$0;
   int label;

   SurveyViewModel$reportFailure$1(SurveyViewModel var1, Continuation var2) {
      super(var2);
      this.this$0 = var1;
   }

   @Nullable
   @Override
   public final Object invokeSuspend(@NotNull Object var1) {
      this.result = var1;
      this.label |= Integer.MIN_VALUE;
      return SurveyViewModel.access$reportFailure(this.this$0, null, null, this);
   }
}
