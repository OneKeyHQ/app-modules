package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ReplyOption$Creator implements Creator {
   public final ReplyOption[] newArray(int var1) {
      return new ReplyOption[var1];
   }

   public final ReplyOption createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new ReplyOption(var1.readString(), var1.readString());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
