package cn.jiguang.common.app.entity;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public final class Cgroup extends ProcFile {
   public final ArrayList recoverField_a;
   public static final Creator CREATOR = new Creator() {
      public Cgroup recoverMethod_a_7897c12d(Parcel var1) {
         return new Cgroup(var1);
      }

      public Cgroup[] recoverMethod_a_896db5d8(int var1) {
         return new Cgroup[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_896db5d8(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_7897c12d(var1);
      }
   };

   private Cgroup(Parcel var1) {
      super(var1);
      this.recoverField_a = var1.createTypedArrayList(ControlGroup.CREATOR);
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      super.writeToParcel(var1, var2);
      var1.writeTypedList(this.recoverField_a);
   }
}
