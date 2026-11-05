package io.intercom.android.sdk.utilities.extensions;

import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.Ticket$Status;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0012\n\u0000\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\u0000\u001a\f\u0010\u0003\u001a\u00020\u0004*\u00020\u0002H\u0000¨\u0006\u0005",
   d2 = {"lastActionCreatedAt", "", "Lio/intercom/android/sdk/models/Conversation;", "hasNonTicketParts", "", "intercom-sdk-base_release"}
)
@SourceDebugExtension(
   "SMAP\nConversationExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationExtensions.kt\nio/intercom/android/sdk/utilities/extensions/ConversationExtensionsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,14:1\n1999#2,14:15\n1761#2,3:29\n*S KotlinDebug\n*F\n+ 1 ConversationExtensions.kt\nio/intercom/android/sdk/utilities/extensions/ConversationExtensionsKt\n*L\n9#1:15,14\n13#1:29,3\n*E\n"
)
public final class ConversationExtensionsKt {
   public static final long lastActionCreatedAt(@NotNull Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      long var10000;
      if (!Intrinsics.areEqual(var0.lastPart(), Part.NULL)) {
         var10000 = var0.lastPart().getCreatedAt();
      } else {
         Ticket var14 = var0.getTicket();
         if (var14 != null) {
            List var15 = var14.getStatusList();
            if (var15 != null) {
               Iterable var1 = var15;
               int var2 = 0;
               Iterator var3 = var1.iterator();
               Object var16;
               if (!var3.hasNext()) {
                  var16 = null;
               } else {
                  Object var4 = var3.next();
                  if (!var3.hasNext()) {
                     var16 = (Ticket$Status)var4;
                  } else {
                     Ticket$Status var5 = (Ticket$Status)var4;
                     int var7 = 0;
                     long var11 = var5.getCreatedDate();

                     do {
                        Object var12 = var3.next();
                        Ticket$Status itx = (Ticket$Status)var12;
                        int var10/* $VF was: $i$a$-maxByOrNull-ConversationExtensionsKt$lastActionCreatedAt$1x */ = 0;
                        long var13 = itx.getCreatedDate();
                        if (var11 < var13) {
                           var4 = var12;
                           var11 = var13;
                        }
                     } while (var3.hasNext());

                     var16 = (Ticket$Status)var4;
                  }
               }

               Ticket$Status recoveredStatus = (Ticket$Status)var16;
               if (recoveredStatus != null) {
                  return recoveredStatus.getCreatedDate();
               }
            }
         }

         var10000 = 0L;
      }

      return var10000;
   }

   public static final boolean hasNonTicketParts(@NotNull Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      var var1 = var0.parts();
      int var2 = 0;
      boolean var10000;
      if (var1 instanceof Collection && ((Collection)var1).isEmpty()) {
         var10000 = false;
      } else {
         for (Object var4 : var1) {
            Part var5 = (Part)var4;
            int var6 = 0;
            if (var5.getMessageStyle() != MessageStyle.TICKET_STATE_UPDATED) {
               return true;
            }
         }

         var10000 = false;
      }

      return var10000;
   }
}
