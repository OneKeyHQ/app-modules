package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.BlockFactory;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.logic.TextSplittingStrategy;
import io.intercom.android.sdk.helpcenter.articles.ArticleMetadata;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent;
import io.intercom.android.sdk.m5.data.IntercomEvent$NewConversation;
import io.intercom.android.sdk.models.ComposerSuggestions;
import io.intercom.android.sdk.models.Config;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationResponse;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000Z\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ<\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00190\u00182\b\b\u0002\u0010\u001a\u001a\u00020\u0016H\u0086B¢\u0006\u0002\u0010\u001bJ4\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00140\u00132\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00182\b\b\u0002\u0010\u001a\u001a\u00020\u0016H\u0086B¢\u0006\u0002\u0010\u001eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "refreshConversationUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "soundEffectsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/SoundEffectsUseCase;Lio/intercom/android/sdk/identity/UserIdentity;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "newConversationRequestPending",
         "Ljava/util/concurrent/atomic/AtomicBoolean;",
         "invoke",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "messageText",
         "",
         "mediaUploadItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "clientUUID",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "blocks",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "(Lkotlinx/coroutines/flow/MutableStateFlow;Ljava/util/List;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSendMessageUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SendMessageUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,229:1\n1617#2,9:230\n1869#2:239\n1870#2:241\n1626#2:242\n1#3:240\n1#3:248\n230#4,5:243\n*S KotlinDebug\n*F\n+ 1 SendMessageUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase\n*L\n42#1:230,9\n42#1:239\n42#1:241\n42#1:242\n42#1:240\n116#1:243,5\n*E\n"
)
public final class SendMessageUseCase {
   @NotNull
   public static final SendMessageUseCase$Companion Companion = new SendMessageUseCase$Companion(null);
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final RefreshConversationUseCase refreshConversationUseCase;
   @NotNull
   private final SoundEffectsUseCase soundEffectsUseCase;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final AtomicBoolean newConversationRequestPending;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final String INBOUND_CONVERSATION_ALREADY_OPEN = "inbound_conversation_already_open";

   public SendMessageUseCase(
      @NotNull ConversationRepository var1,
      @NotNull RefreshConversationUseCase var2,
      @NotNull SoundEffectsUseCase var3,
      @NotNull UserIdentity var4,
      @NotNull IntercomDataLayer var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      Intrinsics.checkNotNullParameter(var2, "refreshConversationUseCase");
      Intrinsics.checkNotNullParameter(var3, "soundEffectsUseCase");
      Intrinsics.checkNotNullParameter(var4, "userIdentity");
      Intrinsics.checkNotNullParameter(var5, "intercomDataLayer");
      super();
      this.conversationRepository = var1;
      this.refreshConversationUseCase = var2;
      this.soundEffectsUseCase = var3;
      this.userIdentity = var4;
      this.intercomDataLayer = var5;
      this.newConversationRequestPending = new AtomicBoolean(false);
   }

   // $VF: synthetic method
   public SendMessageUseCase(
      ConversationRepository var1,
      RefreshConversationUseCase var2,
      SoundEffectsUseCase var3,
      UserIdentity var4,
      IntercomDataLayer var5,
      int var6,
      DefaultConstructorMarker var7
   ) {
      if ((var6 & 8) != 0) {
         var4 = Injector.get().getUserIdentity();
      }

      if ((var6 & 16) != 0) {
         var5 = Injector.get().getDataLayer();
      }

      this(var1, var2, var3, var4, var5);
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull String var2, @NotNull List var3, @NotNull String var4, @NotNull Continuation var5) {
      Iterable var7 = var3;
      int var8 = 0;
      Iterable var9 = var7;
      ArrayList var10 = new ArrayList();
      int var11 = 0;
      Iterable var12 = var9;
      int var13 = 0;

      for (Object var15 : var12) {
         Object var16 = var15;
         int var17 = 0;
         MediaUploadItem var18 = (MediaUploadItem)var16;
         int var19 = 0;
         io.intercom.android.sdk.blocks.lib.models.Block$Builder var10000 = var18.getUploadedBlock();
         if (var10000 != null) {
            Object var20 = var10000;
            int var21 = 0;
            var10.add(var20);
         }
      }

      List var6 = (List)var10;
      BlockFactory var22 = new BlockFactory(new TextSplittingStrategy());
      List var23 = var22.getBlocksForText(StringsKt.trim(var2).toString());
      if (var2.length() <= 0 && var6.isEmpty()) {
         return Unit.INSTANCE;
      }

      Collection var10002 = var6;
      Intrinsics.checkNotNull(var23);
      Object var24 = this.invoke(var1, CollectionsKt.plus(var10002, var23), var4, var5);
      return var24 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var24 : Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static Object invoke$default(
      SendMessageUseCase var0, MutableStateFlow var1, String var2, List var3, String var4, Continuation var5, int var6, Object var7
   ) {
      if ((var6 & 8) != 0) {
         var4 = UUID.randomUUID().toString();
      }

      return var0.invoke(var1, var2, var3, var4, var5);
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull List var2, @NotNull String var3, @NotNull Continuation var4) {
      SendMessageUseCase recoveredThis = this;
      SendMessageUseCase$invoke$2 var19;
      label191: {
         if (var4 instanceof SendMessageUseCase$invoke$2) {
            var19 = (SendMessageUseCase$invoke$2)var4;
            if ((var19.label & -2147483648) != 0) {
               var19.label -= Integer.MIN_VALUE;
               break label191;
            }
         }

         var19 = new SendMessageUseCase$invoke$2(recoveredThis, var4);
      }

      ConversationResponse var7;
      label227: {
         Object var20;
         Object var10000;
         label197: {
            Object var35;
            label198: {
               Object var18 = var19.result;
               var20 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
               Object var36;
               switch (var19.label) {
                  case 0:
                     ResultKt.throwOnFailure(var18);
                     Companion.addBlocksToPendingMessages(var1, var2, var3, recoveredThis.userIdentity);
                     if (((ConversationClientState)var1.getValue()).getConversationId() != null
                        || !recoveredThis.newConversationRequestPending.compareAndSet(false, true)) {
                        Flow var40 = var1;
                        Function2 var49 = new SendMessageUseCase$invoke$resolvedState$1(var3, null);
                        var19.L$0 = recoveredThis;
                        var19.L$1 = var1;
                        var19.L$2 = var2;
                        var19.L$3 = var3;
                        var19.label = 3;
                        var35 = FlowKt.first(var40, var49, var19);
                        if (var35 == var20) {
                           return var20;
                        }
                        break label198;
                     }

                     OpenMessengerResponse var5;
                     ConversationRepository var37;
                     String var51;
                     List var52;
                     String var10002;
                     String var10004;
                     label161: {
                        var5 = ((ConversationClientState)var1.getValue()).getOpenMessengerResponse();
                        var37 = recoveredThis.conversationRepository;
                        var10002 = var5 != null ? var5.getBotIntroId() : null;
                        ArticleMetadata var10003 = ((ConversationClientState)var1.getValue()).getArticleMetadata();
                        var51 = var10003 != null ? var10003.getId() : null;
                        var10004 = var5 != null ? var5.getResolutionBotBehaviorVersionId() : null;
                        if (var5 != null) {
                           ComposerSuggestions var10005 = var5.getComposerSuggestions();
                           if (var10005 != null) {
                              var52 = var10005.getSuggestions();
                              break label161;
                           }
                        }

                        var52 = null;
                     }

                     Long var53;
                     label154: {
                        if (var5 != null) {
                           ComposerSuggestions var10006 = var5.getComposerSuggestions();
                           if (var10006 != null) {
                              var53 = var10006.getSnapshotId();
                              break label154;
                           }
                        }

                        var53 = null;
                     }

                     String var10007 = var5 != null ? var5.getRequestId() : null;
                     var19.L$0 = recoveredThis;
                     var19.L$1 = var1;
                     var19.L$2 = var3;
                     var19.label = 1;
                     var36 = var37.createNewConversation(var2, var10002, var51, var10004, var52, var53, var10007, var3, var19);
                     if (var36 == var20) {
                        return var20;
                     }
                     break;
                  case 1:
                     var3 = (String)var19.L$2;
                     var1 = (MutableStateFlow)var19.L$1;
                     recoveredThis = (SendMessageUseCase)var19.L$0;
                     ResultKt.throwOnFailure(var18);
                     var36 = var18;
                     break;
                  case 2:
                     var7 = (ConversationResponse)var19.L$3;
                     var3 = (String)var19.L$2;
                     var1 = (MutableStateFlow)var19.L$1;
                     recoveredThis = (SendMessageUseCase)var19.L$0;
                     ResultKt.throwOnFailure(var18);
                     break label227;
                  case 3:
                     var3 = (String)var19.L$3;
                     var2 = (List)var19.L$2;
                     var1 = (MutableStateFlow)var19.L$1;
                     recoveredThis = (SendMessageUseCase)var19.L$0;
                     ResultKt.throwOnFailure(var18);
                     var35 = var18;
                     break label198;
                  case 4:
                     var3 = (String)var19.L$2;
                     var1 = (MutableStateFlow)var19.L$1;
                     recoveredThis = (SendMessageUseCase)var19.L$0;
                     ResultKt.throwOnFailure(var18);
                     var10000 = var18;
                     break label197;
                  case 5:
                     ResultKt.throwOnFailure(var18);
                     return Unit.INSTANCE;
                  default:
                     throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
               }

               NetworkResponse var6 = (NetworkResponse)var36;
               if (var6 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError || var6 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError || var6 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError) {
                  recoveredThis.newConversationRequestPending.set(false);
                  recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_FAILED);
                  NetworkResponse var27 = var6;
                  String var23 = (var27 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError ? (io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError)var27 : null) != null
                     ? (var27 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError ? (io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError)var27 : null).getErrorCode()
                     : null;
                  if (Intrinsics.areEqual(var23, "inbound_conversation_already_open")) {
                     Companion.failAllPendingMessages(var1, false);
                  } else {
                     SendMessageUseCase$Companion.updateFailedPendingMessages$default(Companion, var1, var3, false, 4, null);
                  }

                  return Unit.INSTANCE;
               }

               if (!(var6 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
                  throw new NoWhenBranchMatchedException();
               }

               recoveredThis.newConversationRequestPending.set(false);
               recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_SENT);
               var7 = ((io.intercom.android.sdk.models.ConversationResponse$Builder)((io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)var6).getBody()).build();
               IntercomDataLayer var38 = recoveredThis.intercomDataLayer;
               IntercomEvent var10001 = new io.intercom.android.sdk.m5.data.IntercomEvent$NewConversation(var7.getConversation());
               var19.L$0 = recoveredThis;
               var19.L$1 = var1;
               var19.L$2 = var3;
               var19.L$3 = var7;
               var19.label = 2;
               if (var38.emitEvent(var10001, var19) == var20) {
                  return var20;
               }
               break label227;
            }

            ConversationClientState var21 = (ConversationClientState)var35;
            PendingMessage var22 = (PendingMessage)var21.getPendingMessages().get(var3);
            if (var22 == null || var22.isFailed()) {
               return Unit.INSTANCE;
            }

            String var41 = var21.getConversationId();
            if (var41 == null) {
               return Unit.INSTANCE;
            }

            String var24;
            Long var46;
            label137: {
               var24 = var41;
               Conversation var42 = ((ConversationClientState)var1.getValue()).getConversation();
               if (var42 != null) {
                  List var43 = var42.parts();
                  if (var43 != null) {
                     List var30 = var43;
                     ListIterator var31 = var30.listIterator(var30.size());

                     Object var44;
                     while (true) {
                        if (!var31.hasPrevious()) {
                           var44 = null;
                           break;
                        }

                        Object var32 = var31.previous();
                        Part var33 = (Part)var32;
                        int var34 = 0;
                        if (var33.isAdmin()) {
                           var44 = var32;
                           break;
                        }
                     }

                     Part var45 = (Part)var44;
                     if ((Part)var44 != null) {
                        var46 = Boxing.boxLong(var45.getCreatedAt());
                        break label137;
                     }
                  }
               }

               var46 = null;
            }

            Long var26 = var46;
            ConversationRepository var47 = recoveredThis.conversationRepository;
            var19.L$0 = recoveredThis;
            var19.L$1 = var1;
            var19.L$2 = var3;
            var19.L$3 = null;
            var19.label = 4;
            var10000 = var47.replyToConversation(var24, var3, var2, var26, var19);
            if (var10000 == var20) {
               return var20;
            }
         }

         NetworkResponse var28 = (NetworkResponse)var10000;
         NetworkResponse var29 = var28;
         if (!(var29 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError) && !(var29 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError) && !(var29 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError)) {
            if (!(var29 instanceof io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success)) {
               throw new NoWhenBranchMatchedException();
            }

            recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_SENT);
            RefreshConversationUseCase var48 = recoveredThis.refreshConversationUseCase;
            GetConversationReason var50 = GetConversationReason.NEW_COMMENT;
            var19.L$0 = null;
            var19.L$1 = null;
            var19.L$2 = null;
            var19.label = 5;
            if (var48.invoke(var1, var50, var19) == var20) {
               return var20;
            }

            return Unit.INSTANCE;
         }

         recoveredThis.soundEffectsUseCase.invoke$intercom_sdk_base_release(Action.MESSAGE_FAILED);
         SendMessageUseCase$Companion.updateFailedPendingMessages$default(Companion, var1, var3, false, 4, null);
         return Unit.INSTANCE;
      }

      IntercomDataLayer var39 = recoveredThis.intercomDataLayer;
      Config var8 = var7.getConfig();
      Intrinsics.checkNotNullExpressionValue(var8, "getConfig(...)");
      var39.updateConfig(var8);
      MutableStateFlow var25 = var1;
      int var9 = 0;

      Object var10;
      Object var17;
      do {
         var10 = var25.getValue();
         ConversationClientState var11 = (ConversationClientState)var10;
         int var12 = 0;
         Map var13 = MapsKt.toMutableMap(((ConversationClientState)var1.getValue()).getPendingMessages());
         Map var14 = var13;
         ConversationClientState var15 = var11;
         int var16 = 0;
         var14.remove(var3);
         var17 = ConversationClientState.copy$default(
            var15,
            var13,
            var7.getConversation(),
            var7.getConversation().getId(),
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
      } while (!var25.compareAndSet(var10, var17));

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static Object invoke$default(SendMessageUseCase var0, MutableStateFlow var1, List var2, String var3, Continuation var4, int var5, Object var6) {
      if ((var5 & 4) != 0) {
         var3 = UUID.randomUUID().toString();
      }

      return var0.invoke(var1, var2, var3, var4);
   }
}
