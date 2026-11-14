package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import io.intercom.android.sdk.m5.conversation.states.SenderMetadata;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper;
import io.intercom.android.sdk.models.OpenMessengerResponse;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData;
import io.intercom.android.sdk.models.Part$Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0016\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0000\u001a$\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0001H\u0002¨\u0006\b",
   d2 = {
         "reduceIntroParts",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "clientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "reduceBotIntroParts",
         "introParts",
         "Lio/intercom/android/sdk/models/Part;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntroPartsReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntroPartsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/IntroPartsReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,63:1\n1563#2:64\n1634#2,2:65\n295#2,2:67\n1636#2:69\n1573#2:70\n1604#2,4:71\n*S KotlinDebug\n*F\n+ 1 IntroPartsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/IntroPartsReducerKt\n*L\n12#1:64\n12#1:65,2\n15#1:67,2\n12#1:69\n43#1:70\n43#1:71,4\n*E\n"
)
public final class IntroPartsReducerKt {
   @NotNull
   public static final List reduceIntroParts(@NotNull ConversationClientState var0) {
      List var26;
      label56: {
         Intrinsics.checkNotNullParameter(var0, "clientState");
         OpenMessengerResponse var10000 = var0.getOpenMessengerResponse();
         io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData var1 = var10000 != null ? var10000.getNewConversationData() : null;
         if (var1 != null) {
            List var25 = var1.getParts();
            if (var25 != null) {
               Iterable var4 = var25;
               int var5 = 0;
               Iterable var6 = var4;
               ArrayList var7 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var4, 10));
               int var8 = 0;

               for (Object var10 : var6) {
                  io.intercom.android.sdk.models.Part$Builder var11 = (io.intercom.android.sdk.models.Part$Builder)var10;
                  Collection var24 = var7;
                  int var12 = 0;
                  Part var13 = var11.build();
                  Part var14 = var13;
                  int var15 = 0;
                  Iterable var16 = var1.getParticipants();
                  int var17 = 0;
                  Iterator var18 = var16.iterator();

                  Object var27;
                  while (true) {
                     if (!var18.hasNext()) {
                        var27 = null;
                        break;
                     }

                     Object var19 = var18.next();
                     io.intercom.android.sdk.models.Participant$Builder var20 = (io.intercom.android.sdk.models.Participant$Builder)var19;
                     int var21 = 0;
                     if (Intrinsics.areEqual(var20.build().getId(), var14.getParticipantId())) {
                        var27 = var19;
                        break;
                     }
                  }

                  io.intercom.android.sdk.models.Participant$Builder var22 = (io.intercom.android.sdk.models.Participant$Builder)var27;
                  Participant var23 = var22 != null ? var22.build() : null;
                  if (var23 != null) {
                     var14.setParticipant(var23);
                  }

                  var24.add(var13);
               }

               var26 = (List)var7;
               break label56;
            }
         }

         var26 = CollectionsKt.emptyList();
      }

      List var2 = var26;
      return !var2.isEmpty() ? reduceBotIntroParts(var0, var2) : CollectionsKt.emptyList();
   }

   private static final List reduceBotIntroParts(ConversationClientState var0, List var1) {
      List var2 = CollectionsKt.createListBuilder();
      List var3 = var2;
      int var4 = 0;
      boolean var5 = var0.getPendingMessages().isEmpty();
      Iterable var6 = var1;
      List var7 = var3;
      int var8 = 0;
      Iterable var9 = var6;
      ArrayList var10 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var6, 10));
      int var11 = 0;
      int var12 = 0;

      for (Object var14 : var9) {
         int var15 = var12++;
         if (var15 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Part var16 = (Part)var14;
         int var17 = var15;
         Collection var18 = var10;
         int var19 = 0;
         io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper var20 = new io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow$PartWrapper(var16, var17 == CollectionsKt.getLastIndex(var1) && var5, true, null, null, 24, null);
         GroupingPosition var21 = ConversationPartsReducerKt.getGroupingPosition(var1, var17);
         SenderMetadata var22 = new SenderMetadata(ConversationPartsReducerKt.buildSenderLabel(var16), false, 2, null);
         var18.add(new io.intercom.android.sdk.m5.conversation.states.ContentRow$BubbleMessageRow(var20, var21, null, false, false, false, var22, 48, null));
      }

      var7.addAll((Collection & List)var10);
      return CollectionsKt.build(var2);
   }
}
