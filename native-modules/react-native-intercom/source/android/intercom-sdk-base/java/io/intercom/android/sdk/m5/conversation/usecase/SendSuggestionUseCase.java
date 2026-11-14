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
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage$FailedMediaUploadData;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationKt;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Part$MessageState;
import io.intercom.android.sdk.models.Participant$Builder;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
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
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ$\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0086B¢\u0006\u0002\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendSuggestionUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "soundEffectsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;Lio/intercom/android/sdk/identity/UserIdentity;)V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "suggestion",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSendSuggestionUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SendSuggestionUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendSuggestionUseCase\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,89:1\n230#2,5:90\n230#2,5:95\n*S KotlinDebug\n*F\n+ 1 SendSuggestionUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendSuggestionUseCase\n*L\n34#1:90,5\n77#1:95,5\n*E\n"
)
public final class SendSuggestionUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final SoundEffectsUseCase soundEffectsUseCase;
   @NotNull
   private final UserIdentity userIdentity;
   public static final int $stable;

   static { $stable = 8; }

   public SendSuggestionUseCase(@NotNull ConversationRepository var1, @NotNull SoundEffectsUseCase var2, @NotNull UserIdentity var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      Intrinsics.checkNotNullParameter(var2, "soundEffectsUseCase");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      this.conversationRepository = var1;
      this.soundEffectsUseCase = var2;
      this.userIdentity = var3;
   }

   // $VF: synthetic method
   public SendSuggestionUseCase(ConversationRepository var1, SoundEffectsUseCase var2, UserIdentity var3, int var4, DefaultConstructorMarker var5) {
      this(var1, var2, (((var4 & 4) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var3));
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull ReplySuggestion var2, @NotNull Continuation var3) {
      SendSuggestionUseCase recoveredThis = this;
      SendSuggestionUseCase$invoke$1 var23;
      label62: {
         if (var3 instanceof SendSuggestionUseCase$invoke$1) {
            var23 = (SendSuggestionUseCase$invoke$1)var3;
            if ((var23.label & -2147483648) != 0) {
               var23.label -= Integer.MIN_VALUE;
               break label62;
            }
         }

         var23 = new SendSuggestionUseCase$invoke$1(recoveredThis, var3);
      }

      Object var22 = var23.result;
      Object var28 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var23.label) {
         case 0:
            ResultKt.throwOnFailure(var22);
            BlockFactory var4 = new BlockFactory(new TextSplittingStrategy());
            String var37 = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue(var37, "toString(...)");
            String var5 = var37;
            MutableStateFlow var6 = var1;
            int var7 = 0;

            Object var8;
            Object var21;
            do {
               var8 = var6.getValue();
               ConversationClientState var9 = (ConversationClientState)var8;
               int var10 = 0;
               LinkedHashMap var11 = new LinkedHashMap();
               LinkedHashMap var12 = var11;
               ConversationClientState var13 = var9;
               int var14 = 0;
               var12.putAll(var9.getPendingMessages());
               Part var15 = new Part$Builder()
                  .withBlocks(var4.getBlocksForText(var2.getText()))
                  .withCreatedAt(TimeUnit.MILLISECONDS.toSeconds(TimeProvider.SYSTEM.currentTimeMillis()))
                  .withClientAssignedUuid(var5)
                  .build();
               Part var16 = var15;
               String var17 = var5;
               LinkedHashMap var18 = var12;
               int var19 = 0;
               var16.setParticipant(new Participant$Builder().withId(recoveredThis.userIdentity.getIntercomId()).build());
               var16.setMessageState(Part$MessageState.SENDING);
               Unit var20 = Unit.INSTANCE;
               Intrinsics.checkNotNullExpressionValue(var15, "apply(...)");
               Object var24 = null;
               byte var25 = 12;
               Object var26 = null;
               Part var27 = var15;
               var18.put(var17, new PendingMessage(var27, false, false, (PendingMessage$FailedMediaUploadData)var26, var25, (DefaultConstructorMarker)var24));
               var21 = ConversationClientState.copy$default(
                  var13,
                  var11,
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
                  0,
                  0,
                  null,
                  null,
                  false,
                  false,
                  null,
                  null,
                  null,
                  8388606,
                  null
               );
            } while (!var6.compareAndSet(var8, var21));

            if (((ConversationClientState)var1.getValue()).getConversation() != null) {
               return Unit.INSTANCE;
            }

            ConversationRepository var38 = recoveredThis.conversationRepository;
            String var10001 = var2.getId();
            OpenMessengerResponse var10002 = ((ConversationClientState)var1.getValue()).getOpenMessengerResponse();
            String var39 = var10002 != null ? var10002.getRequestId() : null;
            var23.L$0 = recoveredThis;
            var23.L$1 = var1;
            var23.label = 1;
            var10000 = var38.createConversationFromSuggestion(var10001, var39, var23);
            if (var10000 == var28) {
               return var28;
            }
            break;
         case 1:
            var1 = (MutableStateFlow)var23.L$1;
            recoveredThis = (SendSuggestionUseCase)var23.L$0;
            ResultKt.throwOnFailure(var22);
            var10000 = var22;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var29 = (NetworkResponse)var10000;
      if (!(var29 instanceof NetworkResponse$ClientError)
         && !(var29 instanceof NetworkResponse$NetworkError)
         && !(var29 instanceof NetworkResponse$ServerError)) {
         if (!(var29 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_SENT);
         Conversation var30 = (Conversation)((NetworkResponse$Success)var29).getBody();
         MutableStateFlow var31 = var1;
         int var32 = 0;

         Object var33;
         ConversationClientState var35;
         do {
            var33 = var31.getValue();
            var35 = (ConversationClientState)var33;
            int var36 = 0;
            var35 = ConversationClientState.copy$default(
               (ConversationClientState)var35,
               MapsKt.emptyMap(),
               ConversationKt.withOrderedComposerState(var30, ((ConversationClientState)var35).getConversation()),
               var30.getId(),
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
               0,
               0,
               null,
               null,
               false,
               false,
               null,
               null,
               null,
               8388600,
               null
            );
         } while (!var31.compareAndSet(var33, var35));
      } else {
         recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_FAILED);
      }

      return Unit.INSTANCE;
   }
}
