package cn.jiguang.common.app.entity;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class Stat extends ProcFile {
   private final String[] recoverField_a;
   public static final Creator CREATOR = new Creator() {
      public Stat recoverMethod_a_49384555(Parcel var1) {
         return new Stat(var1);
      }

      public Stat[] recoverMethod_a_39c968c0(int var1) {
         return new Stat[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_39c968c0(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_49384555(var1);
      }
   };

   private Stat(Parcel var1) {
      super(var1);
      this.recoverField_a = var1.createStringArray();
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      super.writeToParcel(var1, var2);
      var1.writeStringArray(this.recoverField_a);
   }
}
