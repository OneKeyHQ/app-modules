package io.intercom.android.sdk.m5.helpcenter;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import io.intercom.android.sdk.helpcenter.api.HelpCenterApi;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000+\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\b\u0003*\u0001\r\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u001d\u0010\f\u001a\u00020\r2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002¢\u0006\u0002\u0010\u000e¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "create",
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "helpCenterApi",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;",
         "place",
         "",
         "factory",
         "io/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$Companion$factory$1",
         "(Lio/intercom/android/sdk/helpcenter/api/HelpCenterApi;Ljava/lang/String;)Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class HelpCenterViewModel$Companion {
   private HelpCenterViewModel$Companion() {
   }

   @NotNull
   public final HelpCenterViewModel create(@NotNull ViewModelStoreOwner var1, @NotNull HelpCenterApi var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "helpCenterApi");
      Intrinsics.checkNotNullParameter(var3, "place");
      return (HelpCenterViewModel)new ViewModelProvider(var1, this.factory(var2, var3)).get(HelpCenterViewModel.class);
   }

   private final HelpCenterViewModel$Companion$factory$1 factory(HelpCenterApi var1, String var2) {
      return new HelpCenterViewModel$Companion$factory$1(var1, var2);
   }

   // $VF: synthetic method
   public HelpCenterViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
