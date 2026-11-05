package io.intercom.android.sdk.m5.home;

import androidx.lifecycle.Lifecycle;
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
         "Lio/intercom/android/sdk/m5/home/HomeViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/m5/home/HomeViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "lifecycle",
         "Landroidx/lifecycle/Lifecycle;",
         "factory",
         "io/intercom/android/sdk/m5/home/HomeViewModel$Companion$factory$1",
         "(Landroidx/lifecycle/Lifecycle;)Lio/intercom/android/sdk/m5/home/HomeViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class HomeViewModel$Companion {
   private HomeViewModel$Companion() {
   }

   @NotNull
   public final HomeViewModel create(@NotNull ViewModelStoreOwner var1, @NotNull Lifecycle var2) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "lifecycle");
      return (HomeViewModel)new ViewModelProvider(var1, this.factory(var2)).get(HomeViewModel.class);
   }

   private final HomeViewModel$Companion$factory$1 factory(Lifecycle var1) {
      return new HomeViewModel$Companion$factory$1(var1);
   }

   // $VF: synthetic method
   public HomeViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
