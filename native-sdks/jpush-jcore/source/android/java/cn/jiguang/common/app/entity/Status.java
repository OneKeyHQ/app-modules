package cn.jiguang.common.app.entity;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class Status extends ProcFile {
   public static final Creator CREATOR = new Creator() {
      public Status recoverMethod_a_dc3d2bf7(Parcel var1) {
         return new Status(var1);
      }

      public Status[] recoverMethod_a_ed1320a2(int var1) {
         return new Status[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_ed1320a2(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_dc3d2bf7(var1);
      }
   };

   private Status(Parcel var1) {
      super(var1);
   }
}
