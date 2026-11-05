package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.BlockFactory;
import io.intercom.android.sdk.blocks.logic.TextSplittingStrategy;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.ReplyOption;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ,\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0086B¢\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendQuickReplyUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "refreshConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "soundEffectsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;Lio/intercom/android/sdk/identity/UserIdentity;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "replyOption",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "part",
         "Lio/intercom/android/sdk/models/Part;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/models/ReplyOption;Lio/intercom/android/sdk/models/Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SendQuickReplyUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final RefreshConversationUseCase refreshConversationUseCase;
   @NotNull
   private final SoundEffectsUseCase soundEffectsUseCase;
   @NotNull
   private final UserIdentity userIdentity;
   public static final int $stable;

   static { $stable = 8; }

   public SendQuickReplyUseCase(
      @NotNull ConversationRepository var1, @NotNull RefreshConversationUseCase var2, @NotNull SoundEffectsUseCase var3, @NotNull UserIdentity var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      Intrinsics.checkNotNullParameter(var2, "refreshConversationUseCase");
      Intrinsics.checkNotNullParameter(var3, "soundEffectsUseCase");
      Intrinsics.checkNotNullParameter(var4, "userIdentity");
      this.conversationRepository = var1;
      this.refreshConversationUseCase = var2;
      this.soundEffectsUseCase = var3;
      this.userIdentity = var4;
   }

   // $VF: synthetic method
   public SendQuickReplyUseCase(
      ConversationRepository var1, RefreshConversationUseCase var2, SoundEffectsUseCase var3, UserIdentity var4, int var5, DefaultConstructorMarker var6
   ) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var4));
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull ReplyOption var2, @NotNull Part var3, @NotNull Continuation var4) {
      SendQuickReplyUseCase recoveredThis = this;
      SendQuickReplyUseCase$invoke$1 var12;
      label50: {
         if (var4 instanceof SendQuickReplyUseCase$invoke$1) {
            var12 = (SendQuickReplyUseCase$invoke$1)var4;
            if ((var12.label & -2147483648) != 0) {
               var12.label -= Integer.MIN_VALUE;
               break label50;
            }
         }

         var12 = new SendQuickReplyUseCase$invoke$1(recoveredThis, var4);
      }

      Object var11 = var12.result;
      Object var13 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      String var5;
      Object var10000;
      switch (var12.label) {
         case 0:
            ResultKt.throwOnFailure(var11);
            String var14 = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue(var14, "toString(...)");
            var5 = var14;
            BlockFactory var6 = new BlockFactory(new TextSplittingStrategy());
            List var7 = var6.getBlocksForText(StringsKt.trim(var2.getText()).toString());
            SendMessageUseCase$Companion var15 = SendMessageUseCase.Companion;
            Intrinsics.checkNotNull(var7);
            var15.addBlocksToPendingMessages(var1, var7, var5, recoveredThis.userIdentity);
            Conversation var8 = ((ConversationClientState)var1.getValue()).getConversation();
            String var9 = !var3.isInitialMessage() ? var3.getId() : "";
            if (var8 == null) {
               return Unit.INSTANCE;
            }

            ConversationRepository var16 = recoveredThis.conversationRepository;
            String var10001 = var2.getUuid();
            String var10002 = var8.getId();
            Intrinsics.checkNotNull(var9);
            var12.L$0 = recoveredThis;
            var12.L$1 = var1;
            var12.L$2 = var5;
            var12.label = 1;
            var10000 = var16.addQuickReplyToConversation(var10001, var10002, var9, var5, var12);
            if (var10000 == var13) {
               return var13;
            }
            break;
         case 1:
            var5 = (String)var12.L$2;
            var1 = (MutableStateFlow)var12.L$1;
            recoveredThis = (SendQuickReplyUseCase)var12.L$0;
            ResultKt.throwOnFailure(var11);
            var10000 = var11;
            break;
         case 2:
            ResultKt.throwOnFailure(var11);
            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var10 = (NetworkResponse)var10000;
      if (var10 instanceof NetworkResponse$ClientError || var10 instanceof NetworkResponse$NetworkError || var10 instanceof NetworkResponse$ServerError) {
         recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_FAILED);
         SendMessageUseCase$Companion.updateFailedPendingMessages$default(SendMessageUseCase.Companion, var1, var5, false, 4, null);
         return Unit.INSTANCE;
      }

      if (!(var10 instanceof NetworkResponse$Success)) {
         throw new NoWhenBranchMatchedException();
      }

      recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_SENT);
      RefreshConversationUseCase var17 = recoveredThis.refreshConversationUseCase;
      GetConversationReason var18 = GetConversationReason.NEW_COMMENT;
      var12.L$0 = null;
      var12.L$1 = null;
      var12.L$2 = null;
      var12.label = 2;
      return var17.invoke(var1, var18, var12) == var13 ? var13 : Unit.INSTANCE;
   }
}
