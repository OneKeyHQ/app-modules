package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class Location$Creator implements Creator {
   public final Location[] newArray(int var1) {
      return new Location[var1];
   }

   public final Location createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new Location(var1.readString(), var1.readString(), var1.readInt() == 0 ? null : var1.readInt());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
