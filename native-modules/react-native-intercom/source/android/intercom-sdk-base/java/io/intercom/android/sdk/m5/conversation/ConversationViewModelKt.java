package io.intercom.android.sdk.m5.conversation;

import io.intercom.android.sdk.m5.conversation.data.ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping;
import io.intercom.android.sdk.m5.conversation.states.ConversationClientState;
import io.intercom.android.sdk.m5.conversation.states.PendingMessage;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0012\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0002¨\u0006\u0005",
   d2 = {
         "isFastFinTyping",
         "",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping;",
         "clientState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationClientState;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModelKt\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,864:1\n188#2,3:865\n*S KotlinDebug\n*F\n+ 1 ConversationViewModel.kt\nio/intercom/android/sdk/m5/conversation/ConversationViewModelKt\n*L\n863#1:865,3\n*E\n"
)
public final class ConversationViewModelKt {
   private static final boolean isFastFinTyping(ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping var0, ConversationClientState var1) {
      if (var0.getClientId().length() > 0) {
         Map var2 = var1.getPendingMessages();
         int var3 = 0;
         boolean var10000;
         if (var2.isEmpty()) {
            var10000 = false;
         } else {
            Iterator var4 = var2.entrySet().iterator();

            while (true) {
               if (!var4.hasNext()) {
                  var10000 = false;
                  break;
               }

               Entry var5 = (Entry)var4.next();
               Entry var6 = var5;
               int var7 = 0;
               if (Intrinsics.areEqual(((PendingMessage)var6.getValue()).getPart().getUuid(), var0.getClientId())) {
                  var10000 = true;
                  break;
               }
            }
         }

         if (var10000) {
            return true;
         }
      }

      return false;
   }

   // $VF: synthetic method
   public static final boolean access$isFastFinTyping(ParsedNexusEvent$ConversationNexusEvent$AdminIsTyping var0, ConversationClientState var1) {
      return isFastFinTyping(var0, var1);
   }
}
