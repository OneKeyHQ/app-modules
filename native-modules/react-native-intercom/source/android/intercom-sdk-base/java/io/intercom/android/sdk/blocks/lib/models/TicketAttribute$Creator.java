package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TicketAttribute$Creator implements Creator {
   public final TicketAttribute[] newArray(int var1) {
      return new TicketAttribute[var1];
   }

   public final TicketAttribute createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new TicketAttribute(var1.readLong(), var1.readString(), var1.readString(), var1.readInt() != 0, var1.readString(), var1.createStringArrayList());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
