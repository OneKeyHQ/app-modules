package io.intercom.android.sdk.tickets.list.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0005\b\t\n\u000b\fB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0005\r\u000e\u000f\u0010\u0011¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "",
         "<init>",
         "()V",
         "screenLabel",
         "",
         "getScreenLabel",
         "()Ljava/lang/String;",
         "Initial",
         "Loading",
         "Content",
         "Empty",
         "Error",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Content;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Empty;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Error;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Initial;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Loading;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class TicketsScreenUiState {
   public static int $stable;

   static {}

   private TicketsScreenUiState() {
   }

   @NotNull
   public abstract String getScreenLabel();

   // $VF: synthetic method
   public TicketsScreenUiState(DefaultConstructorMarker var1) {
      this();
   }
}
