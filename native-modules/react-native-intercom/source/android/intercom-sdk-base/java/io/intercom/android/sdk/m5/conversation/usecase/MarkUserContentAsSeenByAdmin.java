package io.intercom.android.sdk.m5.conversation.usecase;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Part$Builder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.MutableStateFlow;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0017\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007H\u0086\u0002¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/usecase/MarkUserContentAsSeenByAdmin;",
         "",
         "<init>",
         "()V",
         "invoke",
         "",
         "clientStateFlow",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nMarkUserContentAsSeenByAdmin.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MarkUserContentAsSeenByAdmin.kt\nio/intercom/android/sdk/m5/conversation/usecase/MarkUserContentAsSeenByAdmin\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,36:1\n230#2,3:37\n233#2,2:45\n1573#3:40\n1604#3,4:41\n*S KotlinDebug\n*F\n+ 1 MarkUserContentAsSeenByAdmin.kt\nio/intercom/android/sdk/m5/conversation/usecase/MarkUserContentAsSeenByAdmin\n*L\n12#1:37,3\n12#1:45,2\n15#1:40\n15#1:41,4\n*E\n"
)
public final class MarkUserContentAsSeenByAdmin {
   public static int $stable;

   static {}

   public final void invoke(@NotNull MutableStateFlow var1) {
      Intrinsics.checkNotNullParameter(var1, "clientStateFlow");
      MutableStateFlow var2 = var1;
      int var3 = 0;

      Object var4;
      ConversationClientState var28;
      do {
         var4 = var2.getValue();
         var28 = (ConversationClientState)var4;
         int var6 = 0;
         ConversationClientState var10000 = (ConversationClientState)var28;
         Object var10001 = null;
         Conversation var10002 = ((ConversationClientState)var28).getConversation();
         if (var10002 == null) {
            var10002 = null;
         } else {
            Iterable var7 = CollectionsKt.toList(((ConversationClientState)var28).getConversation().getPartBuilderList());
            Object var8 = null;
            boolean var9 = false;
            Object var10 = null;
            Object var11 = null;
            Conversation var12 = var10002;
            Object var13 = null;
            ConversationClientState var14 = (ConversationClientState)var28;
            int var15 = 0;
            Iterable var16 = var7;
            ArrayList var17 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
            int var18 = 0;
            int var19 = 0;

            for (Object var21 : var16) {
               int var22 = var19++;
               if (var22 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               Part$Builder var23 = (Part$Builder)var21;
               int var24 = var22;
               Collection var25 = var17;
               int var26 = 0;
               Part var27 = var23.build();
               var25.add(
                  !var27.isAdmin()
                     ? (
                        var24 == CollectionsKt.getLastIndex(((ConversationClientState)var28).getConversation().parts())
                           ? var23.withSeenByAdmin("seen")
                           : (!Intrinsics.areEqual(var27.getSeenByAdmin(), "hide") ? var23.withSeenByAdmin("hide") : var23)
                     )
                     : var23
               );
            }

            List var29 = (List)var17;
            var10000 = var14;
            var10001 = var13;
            var10002 = Conversation.copy$default(
               var12,
               (String)var11,
               (String)var10,
               var9,
               (List)var8,
               var29,
               null,
               null,
               null,
               false,
               false,
               null,
               null,
               false,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               null,
               2097135,
               null
            );
         }

         var28 = ConversationClientState.copy$default(
            var10000,
            (Map)var10001,
            var10002,
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
            8388605,
            null
         );
      } while (!var2.compareAndSet(var4, var28));
   }
}
