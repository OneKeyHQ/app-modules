package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.ConversationRepository;
import io.intercom.android.sdk.m5.conversation.data.GetConversationReason;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.states.FinStreamingData;
import io.intercom.android.sdk.m5.conversation.states.FloatingIndicatorState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.m5.conversation.states.TypingIndicatorType;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationKt;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Ticket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ$\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0086B¢\u0006\u0002\u0010\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "trackLastReceivedPartsUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;",
         "recordOpenedInteractionUseCase",
         "Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lio/intercom/android/sdk/m5/conversation/usecase/TrackLastReceivedPartsUseCase;Lio/intercom/android/sdk/m5/conversation/usecase/RecordOpenedInteractionUseCase;)V",
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
   "SMAP\nRefreshConversationUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RefreshConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase\n+ 2 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n*L\n1#1,120:1\n563#2:121\n548#2,2:122\n550#2,4:127\n1761#3,3:124\n1761#3,3:131\n827#3:134\n855#3:135\n1761#3,3:136\n856#3:139\n230#4,5:140\n230#4,5:145\n*S KotlinDebug\n*F\n+ 1 RefreshConversationUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/RefreshConversationUseCase\n*L\n43#1:121\n43#1:122,2\n43#1:127,4\n43#1:124,3\n47#1:131,3\n56#1:134\n56#1:135\n57#1:136,3\n56#1:139\n66#1:140,5\n93#1:145,5\n*E\n"
)
public final class RefreshConversationUseCase {
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final TrackLastReceivedPartsUseCase trackLastReceivedPartsUseCase;
   @NotNull
   private final RecordOpenedInteractionUseCase recordOpenedInteractionUseCase;
   public static final int $stable;

   static { $stable = 8; }

   public RefreshConversationUseCase(
      @NotNull ConversationRepository var1,
      @NotNull IntercomDataLayer var2,
      @NotNull TrackLastReceivedPartsUseCase var3,
      @NotNull RecordOpenedInteractionUseCase var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      Intrinsics.checkNotNullParameter(var2, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var3, "trackLastReceivedPartsUseCase");
      Intrinsics.checkNotNullParameter(var4, "recordOpenedInteractionUseCase");
      this.conversationRepository = var1;
      this.intercomDataLayer = var2;
      this.trackLastReceivedPartsUseCase = var3;
      this.recordOpenedInteractionUseCase = var4;
   }

   // $VF: synthetic method
   public RefreshConversationUseCase(
      ConversationRepository var1,
      IntercomDataLayer var2,
      TrackLastReceivedPartsUseCase var3,
      RecordOpenedInteractionUseCase var4,
      int var5,
      DefaultConstructorMarker var6
   ) {
      this(var1, (((var5 & 2) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var2), var3, var4);
   }

   @Nullable
   public final Object invoke(@NotNull MutableStateFlow var1, @NotNull GetConversationReason var2, @NotNull Continuation var3) {
      RefreshConversationUseCase recoveredThis = this;
      RefreshConversationUseCase$invoke$1 var28;
      label186: {
         if (var3 instanceof RefreshConversationUseCase$invoke$1) {
            var28 = (RefreshConversationUseCase$invoke$1)var3;
            if ((var28.label & -2147483648) != 0) {
               var28.label -= Integer.MIN_VALUE;
               break label186;
            }
         }

         var28 = new RefreshConversationUseCase$invoke$1(recoveredThis, var3);
      }

      Conversation var7;
      label215: {
         Object var27 = var28.result;
         Object var29 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         Object var10000;
         switch (var28.label) {
            case 0:
               ResultKt.throwOnFailure(var27);
               String var4 = ((ConversationClientState)var1.getValue()).getConversationId();
               if (var4 == null) {
                  return Unit.INSTANCE;
               }

               ConversationRepository var65 = recoveredThis.conversationRepository;
               var28.L$0 = recoveredThis;
               var28.L$1 = var1;
               var28.label = 1;
               var10000 = var65.getConversation(var4, var2, var28);
               if (var10000 == var29) {
                  return var29;
               }
               break;
            case 1:
               var1 = (MutableStateFlow)var28.L$1;
               recoveredThis = (RefreshConversationUseCase)var28.L$0;
               ResultKt.throwOnFailure(var27);
               var10000 = var27;
               break;
            case 2:
               var7 = (Conversation)var28.L$1;
               recoveredThis = (RefreshConversationUseCase)var28.L$0;
               ResultKt.throwOnFailure(var27);
               break label215;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         NetworkResponse var5 = (NetworkResponse)var10000;
         ConversationClientState var6 = (ConversationClientState)var1.getValue();
         if (!(var5 instanceof NetworkResponse$Success)) {
            MutableStateFlow var30 = var1;
            int var31 = 0;

            Object var33;
            Object var42;
            do {
               var33 = var30.getValue();
               int var38 = 0;
               var42 = ConversationClientState.copy$default(
                  var6,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  var5,
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
                  8388479,
                  null
               );
            } while (!var30.compareAndSet(var33, var42));

            return Unit.INSTANCE;
         }

         var7 = (Conversation)((NetworkResponse$Success)var5).getBody();
         recoveredThis.trackLastReceivedPartsUseCase.invoke(var7);
         Map var9 = var6.getPendingMessages();
         int var10 = 0;
         Map var11 = var9;
         Map var12 = new LinkedHashMap();
         int var13 = 0;

         for (java.lang.Object recoveredEntryElement_0_222 : var11.entrySet()) {
            Entry var15 = (Entry) recoveredEntryElement_0_222;
            Entry var16 = var15;
            int var17 = 0;
            var var18 = var7.parts();
            int var19 = 0;
            boolean var66;
            if (var18 instanceof Collection && ((Collection)var18).isEmpty()) {
               var66 = false;
            } else {
               Iterator var20 = var18.iterator();

               while (true) {
                  if (!var20.hasNext()) {
                     var66 = false;
                     break;
                  }

                  Object var21 = var20.next();
                  Part var22 = (Part)var21;
                  int var23 = 0;
                  if (Intrinsics.areEqual(var22.getUuid(), ((PendingMessage)var16.getValue()).getPart().getUuid())
                     || Intrinsics.areEqual(var22.getId(), ((PendingMessage)var16.getValue()).getPart().getId())) {
                     var66 = true;
                     break;
                  }
               }
            }

            if (!var66) {
               var12.put(var15.getKey(), var15.getValue());
            }
         }

         Map var8 = var12;
         var var34 = var7.parts();
         int var36 = 0;
         boolean var68;
         if (var34 instanceof Collection && ((Collection)var34).isEmpty()) {
            var68 = false;
         } else {
            label201: {
               for (Object var43 : var34) {
                  Part var46 = (Part)var43;
                  int var49 = 0;
                  String var67 = var46.getUuid();
                  Intrinsics.checkNotNullExpressionValue(var67, "getUuid(...)");
                  if (!StringsKt.isBlank(var67) && Intrinsics.areEqual(var46.getUuid(), var6.getFinStreamingData().getClientAssignedUUID())) {
                     var68 = true;
                     break label201;
                  }
               }

               var68 = false;
            }
         }

         boolean var32 = var68;
         FinStreamingData var35 = var32 ? new FinStreamingData(false, null, 3, null) : var6.getFinStreamingData();
         Iterable $this$filterNot$ivx = var7.parts();
         var13 = 0;
         Iterable var47 = $this$filterNot$ivx;
         var destination$iv$ivx = new ArrayList();
         int $i$f$filterNotTox = 0;

         for (Object var56 : var47) {
            label130: {
               Part var58 = (Part)var56;
               int var60 = 0;
               if (!var58.isUser()) {
                  Conversation var69 = var6.getConversation();
                  List var70 = var69 != null ? var69.parts() : null;
                  if (var70 == null) {
                     var70 = CollectionsKt.emptyList();
                  }

                  var $this$any$ivx = var70;
                  int $i$f$anyx = 0;
                  if ($this$any$ivx instanceof Collection && ((Collection)$this$any$ivx).isEmpty()) {
                     var68 = false;
                  } else {
                     Iterator var64 = $this$any$ivx.iterator();

                     while (true) {
                        if (!var64.hasNext()) {
                           var68 = false;
                           break;
                        }

                        Object var24 = var64.next();
                        Part var25 = (Part)var24;
                        int var26 = 0;
                        if (Intrinsics.areEqual(var25.getId(), var58.getId())) {
                           var68 = true;
                           break;
                        }
                     }
                  }

                  if (!var68) {
                     var68 = false;
                     break label130;
                  }
               }

               var68 = true;
            }

            if (!var68) {
               destination$iv$ivx.add(var56);
            }
         }

         List var37 = (List)destination$iv$ivx;
         Pair var41 = RefreshConversationUseCaseKt.access$calculateJumpToBottomButtonState(var6, var7, var37);
         FloatingIndicatorState var45 = (FloatingIndicatorState)var41.component1();
         String var48 = (String)var41.component2();
         MutableStateFlow var51 = var1;
         $i$f$filterNotTox = 0;

         Object var55;
         Object var61;
         do {
            var55 = var51.getValue();
            ConversationClientState var57 = (ConversationClientState)var55;
            int var59 = 0;
            var61 = ConversationClientState.copy$default(
               var6,
               var8,
               ConversationKt.withOrderedComposerState(var7, var57.getConversation()),
               null,
               new CurrentlyTypingState(null, false, null, TypingIndicatorType.NONE, 7, null),
               null,
               null,
               null,
               var5,
               null,
               null,
               null,
               null,
               var35,
               null,
               0,
               0,
               var45,
               var48,
               false,
               false,
               null,
               null,
               null,
               8187764,
               null
            );
         } while (!var51.compareAndSet(var55, var61));

         IntercomDataLayer var73 = recoveredThis.intercomDataLayer;
         Ticket var10001 = var7.getTicket();
         if (var10001 == null) {
            var10001 = Ticket.Companion.getNULL();
         }

         var73.updateTicket(var10001);
         recoveredThis.intercomDataLayer.fetchConversationSuccess(var7);
         recoveredThis.recordOpenedInteractionUseCase.invoke(var7);
         if (var7.isRead()) {
            return Unit.INSTANCE;
         }

         ConversationRepository var74 = recoveredThis.conversationRepository;
         String var75 = var7.getId();
         var28.L$0 = recoveredThis;
         var28.L$1 = var7;
         var28.label = 2;
         if (var74.markAsRead(var75, var28) == var29) {
            return var29;
         }
      }

      recoveredThis.conversationRepository.nexusEventsRepository().markAsSeen(var7.getId());
      recoveredThis.intercomDataLayer.markConversationAsRead(var7.getId());
      return Unit.INSTANCE;
   }
}
