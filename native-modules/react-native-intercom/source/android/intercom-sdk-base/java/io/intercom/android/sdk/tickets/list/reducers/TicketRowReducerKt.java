package io.intercom.android.sdk.tickets.list.reducers;

import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.tickets.TicketDetailReducerKt;
import io.intercom.android.sdk.tickets.TicketStatus;
import io.intercom.android.sdk.tickets.list.ui.TicketRowData;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000¨\u0006\u0004",
   d2 = {
         "reduceTicketRowData",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketRowReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketRowReducer.kt\nio/intercom/android/sdk/tickets/list/reducers/TicketRowReducerKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,20:1\n1#2:21\n*E\n"
)
public final class TicketRowReducerKt {
   @NotNull
   public static final TicketRowData reduceTicketRowData(@NotNull Ticket var0) {
      Intrinsics.checkNotNullParameter(var0, "ticket");
      String var10000 = var0.getPublicId();
      if (var10000 != null) {
         String var4 = var10000;
         int var5 = 0;
         var10000 = '#' + var4;
      } else {
         var10000 = null;
      }

      String var1 = var10000;
      String[] var3 = new String[]{var1, var0.getCurrentStatus().getTitle()};
      String var2 = CollectionsKt.joinToString$default(CollectionsKt.listOfNotNull(var3), " • ", null, null, 0, null, null, 62, null);
      TicketStatus var6 = TicketDetailReducerKt.toTicketStatus(var0.getCurrentStatus());
      Boolean var10006 = var0.isRead();
      return new TicketRowData(var0.getId(), var0.getTitle(), var2, var6, var10006 != null ? var10006 : true);
   }
}
