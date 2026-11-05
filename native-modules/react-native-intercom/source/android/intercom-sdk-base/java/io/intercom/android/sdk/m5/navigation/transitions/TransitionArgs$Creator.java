package io.intercom.android.sdk.m5.navigation.transitions;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TransitionArgs$Creator implements Creator {
   public final TransitionArgs[] newArray(int var1) {
      return new TransitionArgs[var1];
   }

   public final TransitionArgs createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new TransitionArgs(
         EnterTransitionStyle.valueOf(var1.readString()),
         ExitTransitionStyle.valueOf(var1.readString()),
         EnterTransitionStyle.valueOf(var1.readString()),
         ExitTransitionStyle.valueOf(var1.readString())
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
