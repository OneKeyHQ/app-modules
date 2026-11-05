package io.intercom.android.sdk.tickets.create.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusClient;
import io.intercom.android.sdk.AblyManager;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApiHelper;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.conversation.data.CombinedEventAsFlowKt;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.UploadRepository;
import io.intercom.android.sdk.models.Ticket;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.flow.Flow;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B9\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fJ4\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019H\u0086@¢\u0006\u0002\u0010\u001bJ\u001c\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\u00122\u0006\u0010\u001e\u001a\u00020\u001fH\u0086@¢\u0006\u0002\u0010 J&\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00122\u0006\u0010#\u001a\u00020\u00172\b\b\u0002\u0010$\u001a\u00020%H\u0086@¢\u0006\u0002\u0010&J\u001c\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0016\u001a\u00020\u0015H\u0086@¢\u0006\u0002\u0010(J,\u0010)\u001a\b\u0012\u0004\u0012\u00020*0\u00122\u0006\u0010+\u001a\u00020\u00172\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u001a0\u0019H\u0086@¢\u0006\u0002\u0010,J\u001c\u0010-\u001a\b\u0012\u0004\u0012\u00020.0\u00122\u0006\u0010\u0016\u001a\u00020\u0015H\u0086@¢\u0006\u0002\u0010(R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006/",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/data/TicketRepository;",
         "",
         "api",
         "Lio/intercom/android/sdk/tickets/create/data/TicketApi;",
         "uploadRepository",
         "Lio/intercom/android/sdk/m5/upload/data/UploadRepository;",
         "nexusClient",
         "Lio/intercom/android/nexus/NexusClient;",
         "ablyManager",
         "Lio/intercom/android/sdk/AblyManager;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lio/intercom/android/sdk/tickets/create/data/TicketApi;Lio/intercom/android/sdk/m5/upload/data/UploadRepository;Lio/intercom/android/nexus/NexusClient;Lio/intercom/android/sdk/AblyManager;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "realTimeEvents",
         "Lkotlinx/coroutines/flow/Flow;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "createTicket",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/Ticket;",
         "conversationId",
         "",
         "ticketId",
         "",
         "attributes",
         "",
         "Lio/intercom/android/sdk/tickets/create/data/TicketAttributeRequest;",
         "(Ljava/lang/String;JLjava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "uploadFile",
         "Lio/intercom/android/sdk/models/Upload$Builder;",
         "data",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchTickets",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsResponse;",
         "page",
         "pageSize",
         "",
         "(JILkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchTicketDetail",
         "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchTicketType",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketTypeV2;",
         "ticketTypeId",
         "(JLjava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "markAsRead",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketRepository {
   @NotNull
   private final TicketApi api;
   @NotNull
   private final UploadRepository uploadRepository;
   @NotNull
   private final NexusClient nexusClient;
   @NotNull
   private final AblyManager ablyManager;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   public static final int $stable;

   static { $stable = 8; }

   public TicketRepository(
      @NotNull TicketApi var1, @NotNull UploadRepository var2, @NotNull NexusClient var3, @NotNull AblyManager var4, @NotNull IntercomDataLayer var5
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "api");
      Intrinsics.checkNotNullParameter(var2, "uploadRepository");
      Intrinsics.checkNotNullParameter(var3, "nexusClient");
      Intrinsics.checkNotNullParameter(var4, "ablyManager");
      Intrinsics.checkNotNullParameter(var5, "intercomDataLayer");
      this.api = var1;
      this.uploadRepository = var2;
      this.nexusClient = var3;
      this.ablyManager = var4;
      this.intercomDataLayer = var5;
   }

   // $VF: synthetic method
   public TicketRepository(
      TicketApi var1, UploadRepository var2, NexusClient var3, AblyManager var4, IntercomDataLayer var5, int var6, DefaultConstructorMarker var7
   ) {
      this((((var6 & 1) != 0) ? (TicketApi) (Injector.get().getTicketApi()) : var1), (((var6 & 2) != 0) ? (UploadRepository) (new UploadRepository(null, null, null, null, 15, null)) : var2), (((var6 & 4) != 0) ? (NexusClient) (Injector.get().getNexusClient()) : var3), (((var6 & 8) != 0) ? (AblyManager) (Injector.get().getAblyManager()) : var4), (((var6 & 16) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var5));
   }

   @NotNull
   public final Flow realTimeEvents() {
      return CombinedEventAsFlowKt.combinedEventAsFlow(this.nexusClient, this.ablyManager);
   }

   @Nullable
   public final Object createTicket(@Nullable String var1, long var2, @NotNull List var4, @NotNull Continuation var5) {
      TicketRepository recoveredThis = this;
      TicketRepository$createTicket$1 var15;
      label37: {
         if (var5 instanceof TicketRepository$createTicket$1) {
            var15 = (TicketRepository$createTicket$1)var5;
            if ((var15.label & -2147483648) != 0) {
               var15.label -= Integer.MIN_VALUE;
               break label37;
            }
         }

         var15 = new TicketRepository$createTicket$1(recoveredThis, var5);
      }

      Object var14 = var15.result;
      Object var16 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var15.label) {
         case 0:
            ResultKt.throwOnFailure(var14);
            TicketApi var17 = recoveredThis.api;
            MessengerApiHelper var10001 = MessengerApiHelper.INSTANCE;
            Map var7 = MapsKt.createMapBuilder();
            Map var8 = var7;
            MessengerApiHelper var12 = var10001;
            TicketApi var11 = var17;
            int var9 = 0;
            CharSequence var10 = var1;
            if (var10 != null && !StringsKt.isBlank(var10)) {
               var8.put("conversation_id", var1);
            }

            var8.put("type_id", Boxing.boxLong(var2));
            var8.put("attributes", var4);
            Unit var13 = Unit.INSTANCE;
            RequestBody var18 = var12.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.build(var7));
            var15.L$0 = recoveredThis;
            var15.label = 1;
            var10000 = var11.createTicket(var18, var15);
            if (var10000 == var16) {
               return var16;
            }
            break;
         case 1:
            recoveredThis = (TicketRepository)var15.L$0;
            ResultKt.throwOnFailure(var14);
            var10000 = var14;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var6 = (NetworkResponse)var10000;
      if (var6 instanceof NetworkResponse$Success) {
         recoveredThis.intercomDataLayer.updateTicket((Ticket)((NetworkResponse$Success)var6).getBody());
      }

      return var6;
   }

   @Nullable
   public final Object uploadFile(@NotNull MediaData$Media var1, @NotNull Continuation var2) {
      return this.uploadRepository.uploadFile(var1, var2);
   }

   @Nullable
   public final Object fetchTickets(long var1, int var3, @NotNull Continuation var4) {
      TicketApi var10000 = this.api;
      MessengerApiHelper var10001 = MessengerApiHelper.INSTANCE;
      Pair[] var5 = new Pair[]{TuplesKt.to("page", Boxing.boxLong(var1)), TuplesKt.to("per_page", Boxing.boxInt(var3))};
      return var10000.fetchTickets(var10001.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(var5)), var4);
   }

   // $VF: synthetic method
   public static Object fetchTickets$default(TicketRepository var0, long var1, int var3, Continuation var4, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var3 = 10;
      }

      return var0.fetchTickets(var1, var3, var4);
   }

   @Nullable
   public final Object fetchTicketDetail(@NotNull String var1, @NotNull Continuation var2) {
      return this.api
         .fetchTicketDetail(var1, MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null), var2);
   }

   @Nullable
   public final Object fetchTicketType(long var1, @NotNull List var3, @NotNull Continuation var4) {
      TicketApi var10000 = this.api;
      MessengerApiHelper var10001 = MessengerApiHelper.INSTANCE;
      Pair[] var5 = new Pair[]{TuplesKt.to("ticket_type_id", Boxing.boxLong(var1)), TuplesKt.to("attributes", var3)};
      return var10000.fetchTicketType(var10001.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(var5)), var4);
   }

   // $VF: synthetic method
   public static Object fetchTicketType$default(TicketRepository var0, long var1, List var3, Continuation var4, int var5, Object var6) {
      if ((var5 & 2) != 0) {
         var3 = CollectionsKt.emptyList();
      }

      return var0.fetchTicketType(var1, var3, var4);
   }

   @Nullable
   public final Object markAsRead(@NotNull String var1, @NotNull Continuation var2) {
      return this.api
         .markAsRead(var1, MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null), var2);
   }

   public TicketRepository() {
      this(null, null, null, null, null, 31, null);
   }
}
