package io.intercom.android.sdk.helpcenter.api;

import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.metrics.MetricTracker;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

@DebugMetadata(
   f = "HelpCenterApiWrapper.kt",
   l = {23, 24},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.api.HelpCenterApiWrapper$fetchHelpCenterCollections$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class HelpCenterApiWrapper$fetchHelpCenterCollections$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final MetricTracker $metricTracker;
   // $VF: synthetic field
   final CollectionRequestCallback $collectionRequestCallback;

   HelpCenterApiWrapper$fetchHelpCenterCollections$1(MetricTracker var1, CollectionRequestCallback var2, Continuation var3) {
      super(2, var3);
      this.$metricTracker = var1;
      this.$collectionRequestCallback = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            this.$metricTracker.requestedHelpCenterData("collection_list");
            HelpCenterApi var4 = Injector.get().getHelpCenterApi();
            Continuation var10002 = this;
            this.label = 1;
            var10000 = HelpCenterApi$DefaultImpls.fetchCollections$default(var4, null, var10002, 1, null);
            if (var10000 == var3) {
               return var3;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            var10000 = var1;
            break;
         case 2:
            ResultKt.throwOnFailure(var1);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      CoroutineContext var5 = Dispatchers.getMain();
      Function2 var10001 = new HelpCenterApiWrapper$fetchHelpCenterCollections$1$1(var2, this.$metricTracker, this.$collectionRequestCallback, null);
      Continuation var6 = this;
      this.label = 2;
      return BuildersKt.withContext(var5, var10001, var6) == var3 ? var3 : Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterApiWrapper$fetchHelpCenterCollections$1(this.$metricTracker, this.$collectionRequestCallback, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterApiWrapper$fetchHelpCenterCollections$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
