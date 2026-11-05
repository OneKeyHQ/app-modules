package io.intercom.android.sdk.tickets;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000%\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003*\u0001\u000b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0015\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\tH\u0002¢\u0006\u0002\u0010\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketDetailViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/tickets/TicketDetailViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "launchedFrom",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "factory",
         "io/intercom/android/sdk/tickets/TicketDetailViewModel$Companion$factory$1",
         "(Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;)Lio/intercom/android/sdk/tickets/TicketDetailViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class TicketDetailViewModel$Companion {
   private TicketDetailViewModel$Companion() {
   }

   @NotNull
   public final TicketDetailViewModel create(@NotNull ViewModelStoreOwner var1, @NotNull TicketLaunchedFrom var2) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "launchedFrom");
      return (TicketDetailViewModel)new ViewModelProvider(var1, this.factory(var2)).get(TicketDetailViewModel.class);
   }

   private final TicketDetailViewModel$Companion$factory$1 factory(TicketLaunchedFrom var1) {
      return new TicketDetailViewModel$Companion$factory$1(var1);
   }

   // $VF: synthetic method
   public TicketDetailViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
