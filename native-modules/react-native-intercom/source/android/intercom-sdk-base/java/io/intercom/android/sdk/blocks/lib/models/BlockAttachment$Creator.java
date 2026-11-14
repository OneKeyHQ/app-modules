package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class BlockAttachment$Creator implements Creator {
   public final BlockAttachment[] newArray(int var1) {
      return new BlockAttachment[var1];
   }

   public final BlockAttachment createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new BlockAttachment(var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readLong(), var1.readLong());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
