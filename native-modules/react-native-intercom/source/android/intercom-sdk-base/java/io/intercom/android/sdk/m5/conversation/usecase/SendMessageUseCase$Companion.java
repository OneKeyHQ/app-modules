package io.intercom.android.sdk.m5.conversation.usecase;

import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Part$MessageState;
import io.intercom.android.sdk.models.Participant$Builder;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J2\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0010J&\u0010\u0011\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\u0006\u0010\u0012\u001a\u00020\u00052\b\b\u0002\u0010\u0013\u001a\u00020\u0014J\u001e\u0010\u0015\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\t2\b\b\u0002\u0010\u0013\u001a\u00020\u0014R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase$Companion;",
         "",
         "<init>",
         "()V",
         "INBOUND_CONVERSATION_ALREADY_OPEN",
         "",
         "addBlocksToPendingMessages",
         "",
         "clientState",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "blocks",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "uuid",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "updateFailedPendingMessages",
         "clientUUID",
         "isRetryable",
         "",
         "failAllPendingMessages",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nSendMessageUseCase.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SendMessageUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase$Companion\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,229:1\n230#2,5:230\n230#2,5:235\n230#2,3:240\n233#2,2:249\n465#3:243\n415#3:244\n1252#4,4:245\n*S KotlinDebug\n*F\n+ 1 SendMessageUseCase.kt\nio/intercom/android/sdk/m5/conversation/usecase/SendMessageUseCase$Companion\n*L\n186#1:230,5\n201#1:235,5\n219#1:240,3\n219#1:249,2\n221#1:243\n221#1:244\n221#1:245,4\n*E\n"
)
public final class SendMessageUseCase$Companion {
   private SendMessageUseCase$Companion() {
   }

   public final void addBlocksToPendingMessages(@NotNull MutableStateFlow var1, @NotNull List var2, @NotNull String var3, @NotNull UserIdentity var4) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      Intrinsics.checkNotNullParameter(var2, "blocks");
      Intrinsics.checkNotNullParameter(var3, "uuid");
      Intrinsics.checkNotNullParameter(var4, "userIdentity");
      Part var6 = new Part$Builder()
         .withBlocks(var2)
         .withCreatedAt(TimeUnit.MILLISECONDS.toSeconds(TimeProvider.SYSTEM.currentTimeMillis()))
         .withParticipantIsAdmin(false)
         .withClientAssignedUuid(var3)
         .build();
      Part var7 = var6;
      int var8 = 0;
      var7.setParticipant(new Participant$Builder().withId(var4.getIntercomId()).build());
      var7.setMessageState(Part$MessageState.SENDING);
      Part var5 = var6;
      MutableStateFlow var16 = var1;
      int var17 = 0;

      Object var15;
      Object var18;
      do {
         var18 = var16.getValue();
         ConversationClientState var9 = (ConversationClientState)var18;
         int var10 = 0;
         LinkedHashMap var11 = new LinkedHashMap();
         LinkedHashMap var12 = var11;
         ConversationClientState var13 = var9;
         int var14 = 0;
         var12.putAll(var9.getPendingMessages());
         Intrinsics.checkNotNull(var5);
         var12.put(var3, new PendingMessage(var5, false, false, null, 12, null));
         var15 = ConversationClientState.copy$default(
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
      } while (!var16.compareAndSet(var18, var15));
   }

   public final void updateFailedPendingMessages(@NotNull MutableStateFlow var1, @NotNull String var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      Intrinsics.checkNotNullParameter(var2, "clientUUID");
      MutableStateFlow var4 = var1;
      int var5 = 0;

      Object var6;
      Object var16;
      do {
         var6 = var4.getValue();
         ConversationClientState var7 = (ConversationClientState)var6;
         int var8 = 0;
         Map var9 = MapsKt.toMutableMap(((ConversationClientState)var1.getValue()).getPendingMessages());
         Map var10 = var9;
         ConversationClientState var11 = var7;
         int var12 = 0;
         PendingMessage var10000 = (PendingMessage)var10.get(var2);
         if (var10000 != null) {
            PendingMessage var13 = var10000;
            int var14 = 0;
            PendingMessage var15 = PendingMessage.copy$default(var13, null, true, var3, null, 9, null);
            var10.put(var2, var15);
         }

         var16 = ConversationClientState.copy$default(
            var11,
            var9,
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
      } while (!var4.compareAndSet(var6, var16));
   }

   // $VF: synthetic method
   public static void updateFailedPendingMessages$default(
      SendMessageUseCase$Companion var0, MutableStateFlow var1, String var2, boolean var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = true;
      }

      var0.updateFailedPendingMessages(var1, var2, var3);
   }

   public final void failAllPendingMessages(@NotNull MutableStateFlow var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "clientState");
      MutableStateFlow var3 = var1;
      int var4 = 0;

      Object var5;
      ConversationClientState var27;
      do {
         var5 = var3.getValue();
         var27 = (ConversationClientState)var5;
         int var7 = 0;
         Map var8 = ((ConversationClientState)var27).getPendingMessages();
         ConversationClientState var9 = (ConversationClientState)var27;
         int var10 = 0;
         Map var11 = var8;
         Map var12 = new LinkedHashMap(MapsKt.mapCapacity(var8.size()));
         int var13 = 0;
         Iterable var14 = var11.entrySet();
         int var15 = 0;

         for (Object var17 : var14) {
            Entry var18 = (Entry)var17;
            Map var19 = var12;
            int var20 = 0;
            Object var10001 = var18.getKey();
            Entry var21 = (Entry)var17;
            Object var22 = var10001;
            Map var23 = var19;
            int var24 = 0;
            PendingMessage var25 = (PendingMessage)var21.getValue();
            PendingMessage var26 = PendingMessage.copy$default(var25, null, true, var2, null, 9, null);
            var23.put(var22, var26);
         }

         var27 = ConversationClientState.copy$default(
            var9,
            var12,
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
      } while (!var3.compareAndSet(var5, var27));
   }

   // $VF: synthetic method
   public static void failAllPendingMessages$default(SendMessageUseCase$Companion var0, MutableStateFlow var1, boolean var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = true;
      }

      var0.failAllPendingMessages(var1, var2);
   }

   // $VF: synthetic method
   public SendMessageUseCase$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
