package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class Attribute$Creator implements Creator {
   public final Attribute[] newArray(int var1) {
      return new Attribute[var1];
   }

   public final Attribute createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new Attribute(
         var1.readString(),
         var1.readString(),
         var1.readString(),
         var1.readInt() != 0,
         var1.readString(),
         var1.createStringArrayList(),
         var1.readInt() == 0 ? null : var1.readInt() != 0,
         var1.readString()
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
