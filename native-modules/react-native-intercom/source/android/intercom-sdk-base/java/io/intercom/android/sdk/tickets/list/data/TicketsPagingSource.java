package io.intercom.android.sdk.tickets.list.data;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.PagingSource.LoadParams;
import androidx.paging.PagingSource.LoadResult;
import androidx.paging.PagingSource.LoadResult.Error;
import androidx.paging.PagingSource.LoadResult.Page;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.tickets.create.data.TicketRepository;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 \u00112\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001:\u0001\u0011B\u0011\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J(\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00020\u000bH\u0096@¢\u0006\u0002\u0010\fJ#\u0010\r\u001a\u0004\u0018\u00010\u00022\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u000fH\u0016¢\u0006\u0002\u0010\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/data/TicketsPagingSource;",
         "Landroidx/paging/PagingSource;",
         "",
         "Lio/intercom/android/sdk/models/Ticket;",
         "repository",
         "Lio/intercom/android/sdk/tickets/create/data/TicketRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/tickets/create/data/TicketRepository;)V",
         "load",
         "Landroidx/paging/PagingSource$LoadResult;",
         "params",
         "Landroidx/paging/PagingSource$LoadParams;",
         "(Landroidx/paging/PagingSource$LoadParams;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "getRefreshKey",
         "state",
         "Landroidx/paging/PagingState;",
         "(Landroidx/paging/PagingState;)Ljava/lang/Long;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketsPagingSource extends PagingSource {
   @NotNull
   public static final TicketsPagingSource$Companion Companion = new TicketsPagingSource$Companion(null);
   @NotNull
   private final TicketRepository repository;
   public static final int $stable;

   static { $stable = 8; }
   public static final int PAGE_SIZE = 10;

   public TicketsPagingSource(@NotNull TicketRepository var1) {
      Intrinsics.checkNotNullParameter(var1, "repository");
      super();
      this.repository = var1;
   }

   // $VF: synthetic method
   public TicketsPagingSource(TicketRepository var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (TicketRepository) (new TicketRepository(null, null, null, null, null, 31, null)) : var1));
   }

   @Nullable
   @Override
   public Object load(@NotNull LoadParams var1, @NotNull Continuation var2) {
      TicketsPagingSource$load$1 var6;
      label45: {
         if (var2 instanceof TicketsPagingSource$load$1) {
            var6 = (TicketsPagingSource$load$1)var2;
            if ((var6.label & -2147483648) != 0) {
               var6.label -= Integer.MIN_VALUE;
               break label45;
            }
         }

         var6 = new TicketsPagingSource$load$1(this, var2);
      }

      Object var5 = var6.result;
      Object var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      Object var10000;
      switch (var6.label) {
         case 0:
            ResultKt.throwOnFailure(var5);
            TicketRepository var8 = this.repository;
            Long var10001 = (Long)var1.getKey();
            long var10 = var10001 != null ? var10001 : 1L;
            var6.L$0 = var1;
            var6.label = 1;
            var10000 = var8.fetchTickets(var10, 10, var6);
            if (var10000 == var7) {
               return var7;
            }
            break;
         case 1:
            var1 = (LoadParams)var6.L$0;
            ResultKt.throwOnFailure(var5);
            var10000 = var5;
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      NetworkResponse var3 = (NetworkResponse)var10000;
      LoadResult var9;
      if (var3 instanceof NetworkResponse$Success) {
         List var4 = ((TicketsResponse)((NetworkResponse$Success)var3).getBody()).getTickets();
         Long var10003 = (Long)var1.getKey();
         var9 = new Page(
            var4, var10003 != null ? Boxing.boxLong(var10003 + -1L) : null, ((TicketsResponse)((NetworkResponse$Success)var3).getBody()).getNextPage()
         );
      } else if (var3 instanceof NetworkResponse$NetworkError) {
         var9 = new Error(((NetworkResponse$NetworkError)var3).getError());
      } else if (var3 instanceof NetworkResponse$ClientError) {
         var9 = new Error(((NetworkResponse$ClientError)var3).getError());
      } else {
         if (!(var3 instanceof NetworkResponse$ServerError)) {
            throw new NoWhenBranchMatchedException();
         }

         var9 = new Error(new java.lang.Error("Server error : code " + ((NetworkResponse$ServerError)var3).getCode()));
      }

      return var9;
   }

   @Nullable
   public Long getRefreshKey(@NotNull PagingState var1) {
      Intrinsics.checkNotNullParameter(var1, "state");
      return null;
   }

   public TicketsPagingSource() {
      this(null, 1, null);
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
