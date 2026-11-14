package io.intercom.android.sdk.tickets.list.ui;

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
   d1 = "\u0000\u001f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003*\u0001\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\r\u0010\b\u001a\u00020\tH\u0002¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "factory",
         "io/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$Companion$factory$1",
         "()Lio/intercom/android/sdk/tickets/list/ui/TicketsScreenViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class TicketsScreenViewModel$Companion {
   private TicketsScreenViewModel$Companion() {
   }

   @NotNull
   public final TicketsScreenViewModel create(@NotNull ViewModelStoreOwner var1) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      return (TicketsScreenViewModel)new ViewModelProvider(var1, this.factory()).get(TicketsScreenViewModel.class);
   }

   private final TicketsScreenViewModel$Companion$factory$1 factory() {
      return new TicketsScreenViewModel$Companion$factory$1();
   }

   // $VF: synthetic method
   public TicketsScreenViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
