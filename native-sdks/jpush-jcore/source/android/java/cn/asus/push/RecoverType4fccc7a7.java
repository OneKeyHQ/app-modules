package cn.asus.push;

import android.os.Parcel;
import android.os.Parcelable.Creator;

final class RecoverType4fccc7a7 implements Creator {
   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final Object[] newArray(int var1) {
      return new DataBuffer[var1];
   }

   // $VF: synthetic method
   @Override
   public final Object createFromParcel(Parcel var1) {
      var1 = var1;
      return new DataBuffer(var1);
   }
}
