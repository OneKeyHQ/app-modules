package io.intercom.android.sdk.m5.inbox;

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
   d1 = "\u0000%\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003*\u0001\u000b\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\r\u0010\n\u001a\u00020\u000bH\u0002¢\u0006\u0002\u0010\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/InboxViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "TYPING_INDICATOR_TIMEOUT",
         "",
         "create",
         "Lio/intercom/android/sdk/m5/inbox/InboxViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "factory",
         "io/intercom/android/sdk/m5/inbox/InboxViewModel$Companion$factory$1",
         "()Lio/intercom/android/sdk/m5/inbox/InboxViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class InboxViewModel$Companion {
   private InboxViewModel$Companion() {
   }

   @NotNull
   public final InboxViewModel create(@NotNull ViewModelStoreOwner var1) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      return (InboxViewModel)new ViewModelProvider(var1, this.factory()).get(InboxViewModel.class);
   }

   private final InboxViewModel$Companion$factory$1 factory() {
      return new InboxViewModel$Companion$factory$1();
   }

   // $VF: synthetic method
   public InboxViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
