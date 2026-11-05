package io.intercom.android.sdk.tickets.create.model;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003*\u0001\u000f\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J(\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\rJ'\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\rH\u0002¢\u0006\u0002\u0010\u0010¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "ticketTypeId",
         "",
         "conversationId",
         "",
         "launchedFrom",
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketLaunchedFrom;",
         "factory",
         "io/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$Companion$factory$1",
         "(JLjava/lang/String;Lio/intercom/android/sdk/tickets/create/model/CreateTicketLaunchedFrom;)Lio/intercom/android/sdk/tickets/create/model/CreateTicketViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class CreateTicketViewModel$Companion {
   private CreateTicketViewModel$Companion() {
   }

   @NotNull
   public final CreateTicketViewModel create(@NotNull ViewModelStoreOwner var1, long var2, @Nullable String var4, @NotNull CreateTicketLaunchedFrom var5) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var5, "launchedFrom");
      return (CreateTicketViewModel)new ViewModelProvider(var1, this.factory(var2, var4, var5)).get(CreateTicketViewModel.class);
   }

   private final CreateTicketViewModel$Companion$factory$1 factory(long var1, String var3, CreateTicketLaunchedFrom var4) {
      return new CreateTicketViewModel$Companion$factory$1(var4, var3, var1);
   }

   // $VF: synthetic method
   public CreateTicketViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
