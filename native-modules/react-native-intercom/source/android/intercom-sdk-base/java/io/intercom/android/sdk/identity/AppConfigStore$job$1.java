package io.intercom.android.sdk.identity;

import kotlin.ResultKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(f = "AppConfigStore.kt", l = {79, 91}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.identity.AppConfigStore$job$1")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class AppConfigStore$job$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final AppConfigStore this$0;

   AppConfigStore$job$1(AppConfigStore var1, Continuation var2) {
      super(2, var2);
      this.this$0 = var1;
   }

   @Override
   public final Object invokeSuspend(Object result) {
      Object suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      boolean resumeCollection;
      if (this.label == 0) {
         ResultKt.throwOnFailure(result);
         resumeCollection = false;
      } else if (this.label == 1) {
         resumeCollection = true;
      } else if (this.label == 2) {
         // Delay cancellation is outside the collection error handler.
         ResultKt.throwOnFailure(result);
         resumeCollection = false;
      } else {
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
      while (true) {
         try {
            if (resumeCollection) {
               resumeCollection = false;
               ResultKt.throwOnFailure(result);
            } else {
               Flow data = AppConfigStore.access$getDataStore$p(this.this$0).getData();
               FlowCollector collector = new AppConfigStore$job$1$1(this.this$0);
               this.label = 1;
               if (data.collect(collector, this) == suspended) {
                  return suspended;
               }
            }
         } catch (CancellationException cancelled) {
            throw cancelled;
         } catch (Exception collectionFailure) {
            AppConfigStore.access$getTwig$cp().w("AppConfig DataStore collection failed: " + collectionFailure.getMessage(), new Object[0]);
         }
         // Completion and retry delay failures are intentionally not caught above.
         AppConfigStore.access$getFirstLoad$p(this.this$0).complete(Unit.INSTANCE);
         this.label = 2;
         if (DelayKt.delay(1000L, this) == suspended) {
            return suspended;
         }
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new AppConfigStore$job$1(this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((AppConfigStore$job$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
