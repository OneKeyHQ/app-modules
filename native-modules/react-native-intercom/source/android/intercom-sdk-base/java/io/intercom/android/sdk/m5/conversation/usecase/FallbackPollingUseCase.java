package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B)\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u001c\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0086B¢\u0006\u0002\u0010\u0016R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/FallbackPollingUseCase;",
         "",
         "appConfig",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "timeProvider",
         "Lio/intercom/android/sdk/utilities/commons/TimeProvider;",
         "refreshConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "<init>",
         "(Lkotlin/jvm/functions/Function0;Lio/intercom/android/sdk/utilities/commons/TimeProvider;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;)V",
         "getAppConfig",
         "()Lkotlin/jvm/functions/Function0;",
         "getTimeProvider",
         "()Lio/intercom/android/sdk/utilities/commons/TimeProvider;",
         "getRefreshConversationUseCase",
         "()Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
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
public final class FallbackPollingUseCase {
   @NotNull
   private final Function0 appConfig;
   @NotNull
   private final TimeProvider timeProvider;
   @NotNull
   private final RefreshConversationUseCase refreshConversationUseCase;
   public static final int $stable;

   static { $stable = 8; }

   public FallbackPollingUseCase(@NotNull Function0 var1, @NotNull TimeProvider var2, @NotNull RefreshConversationUseCase var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "appConfig");
      Intrinsics.checkNotNullParameter(var2, "timeProvider");
      Intrinsics.checkNotNullParameter(var3, "refreshConversationUseCase");
      this.appConfig = var1;
      this.timeProvider = var2;
      this.refreshConversationUseCase = var3;
   }

   // $VF: synthetic method
   public FallbackPollingUseCase(Function0 var1, TimeProvider var2, RefreshConversationUseCase var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (Function0) (((kotlin.jvm.functions.Function0)()->FallbackPollingUseCase._init_$lambda$0())) : var1), (((var4 & 2) != 0) ? (TimeProvider) (Injector.get().getTimeProvider()) : var2), var3);
   }

   @NotNull
   public final Function0 getAppConfig() {
      return this.appConfig;
   }

   @NotNull
   public final TimeProvider getTimeProvider() {
      return this.timeProvider;
   }

   @NotNull
   public final RefreshConversationUseCase getRefreshConversationUseCase() {
      return this.refreshConversationUseCase;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull Continuation var2) {
      FallbackPollingUseCase recoveredThis = this;
      FallbackPollingUseCase$invoke$1 var8;
      label65: {
         if (var2 instanceof FallbackPollingUseCase$invoke$1) {
            var8 = (FallbackPollingUseCase$invoke$1)var2;
            if ((var8.label & -2147483648) != 0) {
               var8.label -= Integer.MIN_VALUE;
               break label65;
            }
         }

         var8 = new FallbackPollingUseCase$invoke$1(recoveredThis, var2);
      }

      Object var7 = var8.result;
      Object var9 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var8.label) {
         case 0:
            ResultKt.throwOnFailure(var7);
            break;
         case 1:
            var1 = (MutableStateFlow)var8.L$1;
            recoveredThis = (FallbackPollingUseCase)var8.L$0;
            ResultKt.throwOnFailure(var7);
            long var10000 = ((AppConfig)recoveredThis.appConfig.invoke()).getConversationStateSyncSettings().getSyncInterval();
            var8.L$0 = recoveredThis;
            var8.L$1 = var1;
            var8.label = 2;
            if (DelayKt.delay(var10000, var8) == var9) {
               return var9;
            }
            break;
         case 2:
            var1 = (MutableStateFlow)var8.L$1;
            recoveredThis = (FallbackPollingUseCase)var8.L$0;
            ResultKt.throwOnFailure(var7);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      while (((AppConfig)recoveredThis.appConfig.invoke()).getConversationStateSyncSettings().getEnabled()) {
         Conversation var3 = ((ConversationClientState)var1.getValue()).getConversation();
         Part var11;
         if (var3 != null) {
            List var10 = var3.parts();
            var11 = var10 != null ? (Part)CollectionsKt.lastOrNull(var10) : null;
         } else {
            var11 = null;
         }

         Part var4 = var11;
         if (var4 != null) {
            int var5 = ((AppConfig)recoveredThis.appConfig.invoke()).getConversationStateSyncSettings().getStartTimeout();
            boolean var6 = recoveredThis.timeProvider.currentTimeMillis() - var4.getCreatedAt() * 1000 > var5;
            if (var6) {
               RefreshConversationUseCase var12 = recoveredThis.refreshConversationUseCase;
               GetConversationReason var10002 = GetConversationReason.POLLING;
               var8.L$0 = recoveredThis;
               var8.L$1 = var1;
               var8.label = 1;
               if (var12.invoke(var1, var10002, var8) == var9) {
                  return var9;
               }
            }
         }

         long var13 = ((AppConfig)recoveredThis.appConfig.invoke()).getConversationStateSyncSettings().getSyncInterval();
         var8.L$0 = recoveredThis;
         var8.L$1 = var1;
         var8.label = 2;
         if (DelayKt.delay(var13, var8) == var9) {
            return var9;
         }
      }

      return Unit.INSTANCE;
   }

   private static final AppConfig _init_$lambda$0() {
      return (AppConfig)Injector.get().getDataLayer().getConfig().getValue();
   }
}
