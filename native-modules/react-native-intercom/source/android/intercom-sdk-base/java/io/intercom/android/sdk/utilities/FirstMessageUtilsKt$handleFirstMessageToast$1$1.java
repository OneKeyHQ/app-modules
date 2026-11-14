package io.intercom.android.sdk.utilities;

import android.content.Context;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.data.IntercomEvent$BaseResponseReceived;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.flow.FlowCollector;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class FirstMessageUtilsKt$handleFirstMessageToast$1$1 implements FlowCollector {
   // $VF: synthetic field
   final Twig $twig;
   // $VF: synthetic field
   final Context $context;

   FirstMessageUtilsKt$handleFirstMessageToast$1$1(Twig var1, Context var2) {
      this.$twig = var1;
      this.$context = var2;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   public final Object emit(IntercomEvent$BaseResponseReceived var1, Continuation var2) {
      FirstMessageUtilsKt$handleFirstMessageToast$1$1 recoveredThis = this;
      FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1 var5;
      label43: {
         if (var2 instanceof FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1) {
            var5 = (FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1)var2;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label43;
            }
         }

         var5 = new FirstMessageUtilsKt$handleFirstMessageToast$1$1$emit$1(recoveredThis, var2);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            if (!var1.getBaseResponse().getConfig().isFirstRequest()) {
               return Unit.INSTANCE;
            }

            recoveredThis.$twig.i(recoveredThis.$context.getString(string.intercom_android_activated_message));

            Object var10;
            try {
               CoroutineContext recoveredToastDispatcher = Dispatchers.getMain();
               Function2 var10001 = new FirstMessageUtilsKt$handleFirstMessageToast$1$1$1(recoveredThis.$context, null);
               var5.L$0 = recoveredThis;
               var5.label = 1;
               var10 = BuildersKt.withContext(recoveredToastDispatcher, var10001, var5);
            } catch (Exception var8) {
               recoveredThis.$twig.internal("Couldn't show first message toast: " + var8.getMessage());
               return Unit.INSTANCE;
            }

            if (var10 == var6) {
               return var6;
            }
            break;
         case 1:
            recoveredThis = (FirstMessageUtilsKt$handleFirstMessageToast$1$1)var5.L$0;

            try {
               ResultKt.throwOnFailure(var4);
               break;
            } catch (Exception var9) {
               recoveredThis.$twig.internal("Couldn't show first message toast: " + var9.getMessage());
               return Unit.INSTANCE;
            }
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      try {
         ;
      } catch (Exception var7) {
         recoveredThis.$twig.internal("Couldn't show first message toast: " + var7.getMessage());
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object emit(Object var1, Continuation var2) {
      return this.emit((IntercomEvent$BaseResponseReceived)var1, var2);
   }
}
