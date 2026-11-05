package io.intercom.android.sdk.tickets.create.data;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J\u001e\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J\u001e\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007J(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\rJ(\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\b\b\u0001\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\rJ\u001e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u0006H§@¢\u0006\u0002\u0010\u0007¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/data/TicketApi;",
         "",
         "createTicket",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/Ticket;",
         "options",
         "Lokhttp3/RequestBody;",
         "(Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchTickets",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsResponse;",
         "fetchTicketDetail",
         "ticketId",
         "",
         "(Ljava/lang/String;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "markAsRead",
         "",
         "fetchTicketType",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "intercom-sdk-base_release"
   }
)
public interface TicketApi {
   @POST("tickets/create")
   @Nullable
   Object createTicket(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("tickets")
   @Nullable
   Object fetchTickets(@Body @NotNull RequestBody var1, @NotNull Continuation var2);

   @POST("tickets/{ticketId}")
   @Nullable
   Object fetchTicketDetail(@Path("ticketId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("tickets/{ticketId}/read")
   @Nullable
   Object markAsRead(@Path("ticketId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("tickets/ticket_form")
   @Nullable
   Object fetchTicketType(@Body @NotNull RequestBody var1, @NotNull Continuation var2);
}
