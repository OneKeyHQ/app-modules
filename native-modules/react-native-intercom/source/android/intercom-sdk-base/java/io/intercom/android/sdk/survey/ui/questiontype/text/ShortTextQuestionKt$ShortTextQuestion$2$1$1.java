package io.intercom.android.sdk.survey.ui.questiontype.text;

import androidx.compose.foundation.relocation.BringIntoViewRequester;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "ShortTextQuestion.kt",
   l = 70,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.survey.ui.questiontype.text.ShortTextQuestionKt$ShortTextQuestion$2$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class ShortTextQuestionKt$ShortTextQuestion$2$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final BringIntoViewRequester $bringIntoViewRequester;

   ShortTextQuestionKt$ShortTextQuestion$2$1$1(BringIntoViewRequester var1, Continuation var2) {
      super(2, var2);
      this.$bringIntoViewRequester = var1;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            BringIntoViewRequester var10000 = this.$bringIntoViewRequester;
            Continuation var10002 = this;
            this.label = 1;
            if (BringIntoViewRequester.bringIntoView$default(var10000, null, var10002, 1, null) == var2) {
               return var2;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new ShortTextQuestionKt$ShortTextQuestion$2$1$1(this.$bringIntoViewRequester, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((ShortTextQuestionKt$ShortTextQuestion$2$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
