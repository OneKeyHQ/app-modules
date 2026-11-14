package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\b\u001a\u00020\tH\u0007J\u0018\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\tH\u0007R\u0014\u0010\u0004\u001a\u00020\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs$TicketsScreenArgs;",
         "Lio/intercom/android/sdk/m5/navigation/IntercomRootActivityArgs;",
         "<init>",
         "()V",
         "route",
         "",
         "getRoute",
         "()Ljava/lang/String;",
         "describeContents",
         "",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class IntercomRootActivityArgs$TicketsScreenArgs extends IntercomRootActivityArgs {
   @NotNull
   public static final IntercomRootActivityArgs$TicketsScreenArgs INSTANCE = new IntercomRootActivityArgs$TicketsScreenArgs();
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new IntercomRootActivityArgs$TicketsScreenArgs$Creator();

   private IntercomRootActivityArgs$TicketsScreenArgs() {
      super(null);
   }

   @NotNull
   @Override
   public String getRoute() {
      return "TICKETS";
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(1);
   }

   @Override
   public final int describeContents() {
      return 0;
   }
}
