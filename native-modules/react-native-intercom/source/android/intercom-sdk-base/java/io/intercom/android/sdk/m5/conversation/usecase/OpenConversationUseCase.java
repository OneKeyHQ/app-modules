package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.data.CommonRepository;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
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
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ&\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\b\b\u0002\u0010\u000f\u001a\u00020\u0010H\u0086B¢\u0006\u0002\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/OpenConversationUseCase;",
         "",
         "refreshConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "refreshUnreadConversationsCountUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase;",
         "commonRepository",
         "Lio/intercom/android/sdk/m5/data/CommonRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshUnreadConversationsCountUseCase;Lio/intercom/android/sdk/m5/data/CommonRepository;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "getConversationReason",
         "Lio/intercom/android/sdk/m5/conversation/data/GetConversationReason;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/m5/conversation/data/GetConversationReason;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nOpenConversationUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 OpenConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/OpenConversationUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,64:1\n230#2,5:65\n230#2,5:70\n*S KotlinDebug\n*F\n+ 1 OpenConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/OpenConversationUseCase\n*L\n34#1:65,5\n54#1:70,5\n*E\n"
)
public final class OpenConversationUseCase {
   @NotNull
   private final RefreshConversationUseCase refreshConversationUseCase;
   @NotNull
   private final RefreshUnreadConversationsCountUseCase refreshUnreadConversationsCountUseCase;
   @NotNull
   private final CommonRepository commonRepository;
   public static final int $stable;

   static { $stable = 8; }

   public OpenConversationUseCase(
      @NotNull RefreshConversationUseCase var1, @NotNull RefreshUnreadConversationsCountUseCase var2, @NotNull CommonRepository var3
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "refreshConversationUseCase");
      Intrinsics.checkNotNullParameter(var2, "refreshUnreadConversationsCountUseCase");
      Intrinsics.checkNotNullParameter(var3, "commonRepository");
      this.refreshConversationUseCase = var1;
      this.refreshUnreadConversationsCountUseCase = var2;
      this.commonRepository = var3;
   }

   // $VF: synthetic method
   public OpenConversationUseCase(
      RefreshConversationUseCase var1, RefreshUnreadConversationsCountUseCase var2, CommonRepository var3, int var4, DefaultConstructorMarker var5
   ) {
      if ((var4 & 4) != 0) {
         MessengerApi var10002 = Injector.get().getMessengerApi();
         Intrinsics.checkNotNullExpressionValue(var10002, "getMessengerApi(...)");
         IntercomDataLayer var10003 = Injector.get().getDataLayer();
         Intrinsics.checkNotNullExpressionValue(var10003, "getDataLayer(...)");
         var3 = new CommonRepository(var10002, var10003);
      }

      this(var1, var2, var3);
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull GetConversationReason var2, @NotNull Continuation var3) {
      OpenConversationUseCase recoveredThis = this;
      OpenConversationUseCase$invoke$1 var12;
      label72: {
         if (var3 instanceof OpenConversationUseCase$invoke$1) {
            var12 = (OpenConversationUseCase$invoke$1)var3;
            if ((var12.label & -2147483648) != 0) {
               var12.label -= Integer.MIN_VALUE;
               break label72;
            }
         }

         var12 = new OpenConversationUseCase$invoke$1(recoveredThis, var3);
      }

      Object var13;
      label84: {
         Object var11 = var12.result;
         var13 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         Object var10000;
         switch (var12.label) {
            case 0:
               ResultKt.throwOnFailure(var11);
               CommonRepository var21 = recoveredThis.commonRepository;
               var12.L$0 = recoveredThis;
               var12.L$1 = var1;
               var12.L$2 = var2;
               var12.label = 1;
               var10000 = var21.openMessenger(var12);
               if (var10000 == var13) {
                  return var13;
               }
               break;
            case 1:
               var2 = (GetConversationReason)var12.L$2;
               var1 = (MutableStateFlow)var12.L$1;
               recoveredThis = (OpenConversationUseCase)var12.L$0;
               ResultKt.throwOnFailure(var11);
               var10000 = var11;
               break;
            case 2:
               var1 = (MutableStateFlow)var12.L$1;
               recoveredThis = (OpenConversationUseCase)var12.L$0;
               ResultKt.throwOnFailure(var11);
               break label84;
            case 3:
               ResultKt.throwOnFailure(var11);
               return Unit.INSTANCE;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         NetworkResponse var4 = (NetworkResponse)var10000;
         if (((ConversationClientState)var1.getValue()).getLastNetworkCall() != null
            && !(((ConversationClientState)var1.getValue()).getLastNetworkCall() instanceof NetworkResponse$Success)) {
            return Unit.INSTANCE;
         }

         NetworkResponse var5 = var4;
         if (!(var5 instanceof NetworkResponse$Success)) {
            if (!(var5 instanceof NetworkResponse$ClientError)
               && !(var5 instanceof NetworkResponse$NetworkError)
               && !(var5 instanceof NetworkResponse$ServerError)) {
               throw new NoWhenBranchMatchedException();
            }

            MutableStateFlow var14 = var1;
            int var15 = 0;

            Object var16;
            ConversationClientState var19;
            do {
               var16 = var14.getValue();
               var19 = (ConversationClientState)var16;
               int var20 = 0;
               var19 = ConversationClientState.copy$default(
                  (ConversationClientState)var19,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  var4,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  0,
                  0,
                  null,
                  null,
                  false,
                  false,
                  null,
                  null,
                  null,
                  8380287,
                  null
               );
            } while (!var14.compareAndSet(var16, var19));

            return Unit.INSTANCE;
         }

         MutableStateFlow var6 = var1;
         int var7 = 0;

         Object var8;
         ConversationClientState var17;
         do {
            var8 = var6.getValue();
            var17 = (ConversationClientState)var8;
            int var10 = 0;
            var17 = ConversationClientState.copy$default(
               (ConversationClientState)var17,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               var4,
               null,
               null,
               null,
               null,
               null,
               (OpenMessengerResponse)((NetworkResponse$Success)var4).getBody(),
               0,
               0,
               null,
               null,
               false,
               false,
               null,
               null,
               null,
               8380287,
               null
            );
         } while (!var6.compareAndSet(var8, var17));

         RefreshConversationUseCase var22 = recoveredThis.refreshConversationUseCase;
         var12.L$0 = recoveredThis;
         var12.L$1 = var1;
         var12.L$2 = null;
         var12.label = 2;
         if (var22.invoke(var1, var2, var12) == var13) {
            return var13;
         }
      }

      RefreshUnreadConversationsCountUseCase var23 = recoveredThis.refreshUnreadConversationsCountUseCase;
      var12.L$0 = null;
      var12.L$1 = null;
      var12.label = 3;
      return var23.invoke(var1, var12) == var13 ? var13 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static Object invoke$default(
      OpenConversationUseCase var0, MutableStateFlow var1, GetConversationReason var2, Continuation var3, int var4, Object var5
   ) {
      if ((var4 & 2) != 0) {
         var2 = GetConversationReason.OPEN_CONVERSATION;
      }

      return var0.invoke(var1, var2, var3);
   }
}
