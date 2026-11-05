package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class Metadata$Creator implements Creator {
   public final Metadata[] newArray(int var1) {
      return new Metadata[var1];
   }

   public final Metadata createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new Metadata(var1.readString(), var1.readString(), var1.readInt() == 0 ? null : var1.readLong(), null, 8, null);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
