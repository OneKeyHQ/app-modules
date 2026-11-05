package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class Appearance$1 implements Creator {
   public Appearance createFromParcel(Parcel var1) {
      return Appearance.create(var1.readString(), var1.readString(), var1.readString());
   }

   public Appearance[] newArray(int var1) {
      return new Appearance[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
