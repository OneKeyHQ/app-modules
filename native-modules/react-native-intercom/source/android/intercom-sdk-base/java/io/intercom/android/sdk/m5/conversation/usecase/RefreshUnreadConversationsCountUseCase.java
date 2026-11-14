package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.Config;
import io.intercom.android.sdk.models.UsersResponse;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001c\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bH\u0086B¢\u0006\u0002\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase;",
         "",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/data/CommonRepository;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nRefreshUnreadConversationsCountUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RefreshUnreadConversationsCountUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,32:1\n230#2,5:33\n*S KotlinDebug\n*F\n+ 1 RefreshUnreadConversationsCountUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase\n*L\n23#1:33,5\n*E\n"
)
public final class RefreshUnreadConversationsCountUseCase {
   @NotNull
   private final CommonRepository commonRepository;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   public static final int $stable;

   static { $stable = 8; }

   public RefreshUnreadConversationsCountUseCase(@NotNull CommonRepository var1, @NotNull IntercomDataLayer var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "commonRepository");
      Intrinsics.checkNotNullParameter(var2, "intercomDataLayer");
      this.commonRepository = var1;
      this.intercomDataLayer = var2;
   }

   // $VF: synthetic method
   public RefreshUnreadConversationsCountUseCase(CommonRepository var1, IntercomDataLayer var2, int var3, DefaultConstructorMarker var4) {
      if ((var3 & 1) != 0) {
         MessengerApi var10002 = Injector.get().getMessengerApi();
         Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
         IntercomDataLayer var10003 = Injector.get().getDataLayer();
         Intrinsics.checkNotNullExpressionValue(var10003, "getDataLayer(...)");
         var1 = new CommonRepository(var10002, var10003);
      }

      if ((var3 & 2) != 0) {
         var2 = Injector.get().getDataLayer();
      }

      this(var1, var2);
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull Continuation var2) {
      RefreshUnreadConversationsCountUseCase recoveredThis = this;
      RefreshUnreadConversationsCountUseCase$invoke$1 var10;
      label38: {
         if (var2 instanceof RefreshUnreadConversationsCountUseCase$invoke$1) {
            var10 = (RefreshUnreadConversationsCountUseCase$invoke$1)var2;
            if ((var10.label & -2147483648) != 0) {
               var10.label -= Integer.MIN_VALUE;
               break label38;
            }
         }

         var10 = new RefreshUnreadConversationsCountUseCase$invoke$1(recoveredThis, var2);
      }

      Object var9 = var10.result;
      Object var11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var10.label) {
         case 0:
            ResultKt.throwOnFailure(var9);
            if (((ConversationClientState)var1.getValue()).getLaunchMode() != LaunchMode.PROGRAMMATIC) {
               return Unit.INSTANCE;
            }

            CommonRepository var14 = recoveredThis.commonRepository;
            var10.L$0 = recoveredThis;
            var10.L$1 = var1;
            var10.label = 1;
            var10000 = var14.fetchUnreadCounts(var10);
            if (var10000 == var11) {
               return var11;
            }
            break;
         case 1:
            var1 = (MutableStateFlow)var10.L$1;
            recoveredThis = (RefreshUnreadConversationsCountUseCase)var10.L$0;
            ResultKt.throwOnFailure(var9);
            var10000 = var9;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      UsersResponse recoveredUsersResponse = (UsersResponse)var10000;
      if (recoveredUsersResponse == null) {
         return Unit.INSTANCE;
      }

      UsersResponse var3 = recoveredUsersResponse;
      IntercomDataLayer var16 = recoveredThis.intercomDataLayer;
      Config var4 = var3.getConfig();
      Intrinsics.checkNotNullExpressionValue(var4, "getConfig(...)");
      var16.updateConfig(var4);
      MutableStateFlow var12 = var1;
      int var5 = 0;

      Object var6;
      ConversationClientState var13;
      do {
         var6 = var12.getValue();
         var13 = (ConversationClientState)var6;
         int var8 = 0;
         var13 = ConversationClientState.copy$default(
            (ConversationClientState)var13,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var3.getUnreadConversations().getUnreadConversationsCount(),
            var3.getUnreadTickets().getTotalCount(),
            null,
            null,
            false,
            false,
            null,
            null,
            null,
            8339455,
            null
         );
      } while (!var12.compareAndSet(var6, var13));

      return Unit.INSTANCE;
   }

   public RefreshUnreadConversationsCountUseCase() {
      this(null, null, 3, null);
   }
}
