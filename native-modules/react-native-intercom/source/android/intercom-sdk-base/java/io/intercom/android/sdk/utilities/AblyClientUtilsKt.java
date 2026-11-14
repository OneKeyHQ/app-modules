package io.intercom.android.sdk.utilities;

import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.flow.SharedFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0080@¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "handleAblyLifecycle",
         "",
         "dataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "(Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/AblyManager;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
public final class AblyClientUtilsKt {
   @Nullable
   public static final Object handleAblyLifecycle(@NotNull IntercomDataLayer var0, @NotNull AblyManager var1, @NotNull Continuation var2) {
      AblyClientUtilsKt$handleAblyLifecycle$1 var4;
      label20: {
         if (var2 instanceof AblyClientUtilsKt$handleAblyLifecycle$1) {
            var4 = (AblyClientUtilsKt$handleAblyLifecycle$1)var2;
            if ((var4.label & -2147483648) != 0) {
               var4.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         var4 = new AblyClientUtilsKt$handleAblyLifecycle$1(var2);
      }

      Object var3 = var4.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var4.label) {
         case 0:
            ResultKt.throwOnFailure(var3);
            SharedFlow var10000 = var0.getEvent();
            FlowCollector var10001 = new AblyClientUtilsKt$handleAblyLifecycle$2(var1);
            var4.label = 1;
            if (var10000.collect(var10001, var4) == var5) {
               return var5;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var3);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      throw new KotlinNothingValueException();
   }
}
