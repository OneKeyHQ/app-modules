package io.intercom.android.sdk.helpcenter.api;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "HelpCenterApiWrapper.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.helpcenter.api.HelpCenterApiWrapper$fetchHelpCenterCollections$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class HelpCenterApiWrapper$fetchHelpCenterCollections$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final NetworkResponse $fetchCollectionListResponse;
   // $VF: synthetic field
   final MetricTracker $metricTracker;
   // $VF: synthetic field
   final CollectionRequestCallback $collectionRequestCallback;

   HelpCenterApiWrapper$fetchHelpCenterCollections$1$1(NetworkResponse var1, MetricTracker var2, CollectionRequestCallback var3, Continuation var4) {
      super(2, var4);
      this.$fetchCollectionListResponse = var1;
      this.$metricTracker = var2;
      this.$collectionRequestCallback = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            NetworkResponse var2 = this.$fetchCollectionListResponse;
            if (var2 instanceof NetworkResponse$ServerError) {
               this.$metricTracker
                  .failedHelpCenter(
                     "help_center_data", "collection_list", String.valueOf(((NetworkResponse$ServerError)this.$fetchCollectionListResponse).getCode()), false
                  );
               this.$collectionRequestCallback.onError(((NetworkResponse$ServerError)this.$fetchCollectionListResponse).getCode());
            } else if (!(var2 instanceof NetworkResponse$ClientError) && !(var2 instanceof NetworkResponse$NetworkError)) {
               if (!(var2 instanceof NetworkResponse$Success)) {
                  throw new NoWhenBranchMatchedException();
               }

               this.$collectionRequestCallback.onComplete((List)((NetworkResponse$Success)this.$fetchCollectionListResponse).getBody());
            } else {
               this.$metricTracker.failedHelpCenter("help_center_data", "collection_list", null, false);
               this.$collectionRequestCallback.onFailure();
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new HelpCenterApiWrapper$fetchHelpCenterCollections$1$1(
         this.$fetchCollectionListResponse, this.$metricTracker, this.$collectionRequestCallback, var2
      );
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((HelpCenterApiWrapper$fetchHelpCenterCollections$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
