package io.intercom.android.sdk.m5.conversation.data;

import com.google.gson.Gson;
import io.ably.lib.types.Message;
import io.intercom.android.sdk.models.Part;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000¨\u0006\u0003",
   d2 = {"asAblyData", "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent;", "Lio/ably/lib/types/Message;", "intercom-sdk-base_release"}
)
public final class ParsedAblyEventKt {
   @NotNull
   public static final ParsedAblyEvent asAblyData(@NotNull Message var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      ParsedAblyEvent var10000;
      if (Intrinsics.areEqual(var0.name, "NewComment")) {
         ParsedAblyEvent var1;
         try {
            Gson var5 = new Gson();
            AblyMessage var2 = (AblyMessage)var5.fromJson(var0.data.toString(), AblyMessage.class);
            Part var3 = var2.getEventData().getConversationPart().build();
            var1 = new ParsedAblyEvent$ConversationAblyEvent$NewComment(
               String.valueOf(var2.getEventData().getConversationId()),
               var3.isUser() ? String.valueOf(var3.isUser()) : "",
               "",
               var2.getEventData().getConversationPart()
            );
         } catch (Exception var4) {
            System.out.println((Object)("AblyManager: JSON parsing error: " + var4.getMessage()));
            var4.printStackTrace();
            var1 = ParsedAblyEvent$UnSupportedEvent.INSTANCE;
         }

         var10000 = var1;
      } else {
         var10000 = ParsedAblyEvent$UnSupportedEvent.INSTANCE;
      }

      return var10000;
   }
}
