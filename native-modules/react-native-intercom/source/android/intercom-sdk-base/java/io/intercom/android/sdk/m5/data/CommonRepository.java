package io.intercom.android.sdk.m5.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApi$DefaultImpls;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.models.UsersResponse$Builder;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.sync.Mutex;
import kotlinx.coroutines.sync.MutexKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0086@¢\u0006\u0002\u0010\rJ\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0086@¢\u0006\u0002\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/api/MessengerApi;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "openMutex",
         "Lkotlinx/coroutines/sync/Mutex;",
         "openMessenger",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchUnreadCounts",
         "Lio/intercom/android/sdk/models/UsersResponse;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nCommonRepository.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CommonRepository.kt\nio/intercom/android/sdk/m5/data/CommonRepository\n+ 2 Mutex.kt\nkotlinx/coroutines/sync/MutexKt\n*L\n1#1,47:1\n116#2,11:48\n*S KotlinDebug\n*F\n+ 1 CommonRepository.kt\nio/intercom/android/sdk/m5/data/CommonRepository\n*L\n18#1:48,11\n*E\n"
)
public final class CommonRepository {
   @NotNull
   private final MessengerApi messengerApi;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final Mutex openMutex;
   public static final int $stable;

   static { $stable = 8; }

   public CommonRepository(@NotNull MessengerApi var1, @NotNull IntercomDataLayer var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "messengerApi");
      Intrinsics.checkNotNullParameter(var2, "intercomDataLayer");
      this.messengerApi = var1;
      this.intercomDataLayer = var2;
      this.openMutex = MutexKt.Mutex$default(false, 1, null);
   }

   @Nullable
   public final Object openMessenger(@NotNull Continuation completion) {
      CommonRepository$openMessenger$1 state;
      if (completion instanceof CommonRepository$openMessenger$1
            && ((((CommonRepository$openMessenger$1) completion).label & Integer.MIN_VALUE) != 0)) {
         state = (CommonRepository$openMessenger$1) completion;
         state.label -= Integer.MIN_VALUE;
      } else {
         state = new CommonRepository$openMessenger$1(this, completion);
      }
      Object result = state.result;
      Object suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      int resumeLabel = state.label;
      CommonRepository owner;
      Mutex mutex;
      if (resumeLabel == 0) {
         ResultKt.throwOnFailure(result);
         owner = this;
         mutex = this.openMutex;
         state.L$0 = owner;
         state.L$1 = mutex;
         state.label = 1;
         if (mutex.lock(null, state) == suspended) {
            return suspended;
         }
      } else if (resumeLabel == 1) {
         mutex = (Mutex) state.L$1;
         owner = (CommonRepository) state.L$0;
         // A failed lock resume never acquired the mutex and must not unlock it.
         ResultKt.throwOnFailure(result);
      } else if (resumeLabel == 2) {
         mutex = (Mutex) state.L$1;
         owner = (CommonRepository) state.L$0;
      } else {
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse response;
      try {
         boolean fetchedResponse;
         if (resumeLabel == 2) {
            ResultKt.throwOnFailure(result);
            response = (NetworkResponse) result;
            fetchedResponse = true;
         } else {
            OpenMessengerResponse cached = owner.intercomDataLayer.getOpenResponse();
            if (cached != null) {
               response = new NetworkResponse$Success(cached);
               fetchedResponse = false;
            } else {
               if (!Injector.get().getAblyManager().isConnected()) {
                  Injector.get().getAblyManager().connect();
               }
               state.L$0 = owner;
               state.L$1 = mutex;
               state.label = 2;
               Object fetched = MessengerApi$DefaultImpls.openMessengerSuspended$default(
                     owner.messengerApi, null, state, 1, null);
               // Suspension retains the mutex; a finally block would release it too early.
               if (fetched == suspended) {
                  return suspended;
               }
               response = (NetworkResponse) fetched;
               fetchedResponse = true;
            }
         }
         if (fetchedResponse && response instanceof NetworkResponse$Success) {
            owner.intercomDataLayer.updateOpenResponse(
                  (OpenMessengerResponse) ((NetworkResponse$Success) response).getBody());
            TeamPresence presence = ((OpenMessengerResponse) ((NetworkResponse$Success) response).getBody()).getTeamPresence();
            owner.intercomDataLayer.updateTeamPresence(presence == null ? TeamPresence.NULL : presence);
         }
      } catch (Throwable failure) {
         mutex.unlock(null);
         throw failure;
      }
      // Unlock failures propagate directly rather than entering the protected body again.
      mutex.unlock(null);
      return response;
   }

   @Nullable
   public final Object fetchUnreadCounts(@NotNull Continuation var1) {
      CommonRepository$fetchUnreadCounts$1 var4;
      label25: {
         if (var1 instanceof CommonRepository$fetchUnreadCounts$1) {
            var4 = (CommonRepository$fetchUnreadCounts$1)var1;
            if ((var4.label & -2147483648) != 0) {
               var4.label -= Integer.MIN_VALUE;
               break label25;
            }
         }

         var4 = new CommonRepository$fetchUnreadCounts$1(this, var1);
      }

      Object var3 = var4.result;
      Object var5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var4.label) {
         case 0:
            ResultKt.throwOnFailure(var3);
            MessengerApi var6 = this.messengerApi;
            var4.label = 1;
            var10000 = MessengerApi$DefaultImpls.getUnreadConversationsSuspended$default(var6, null, var4, 1, null);
            if (var10000 == var5) {
               return var5;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var3);
            var10000 = var3;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var2 = (NetworkResponse)var10000;
      return var2 instanceof NetworkResponse$Success ? ((UsersResponse$Builder)((NetworkResponse$Success)var2).getBody()).build() : null;
   }
}
