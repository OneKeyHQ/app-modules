package cn.jiguang.common.app.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.io.File;

public class ProcFile extends File implements Parcelable {
   public final String recoverField_b;
   public static final Creator CREATOR = new Creator() {
      public ProcFile recoverMethod_a_fa0d2677(Parcel var1) {
         return new ProcFile(var1);
      }

      public ProcFile[] recoverMethod_a_2d389c62(int var1) {
         return new ProcFile[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_2d389c62(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_fa0d2677(var1);
      }
   };

   protected ProcFile(Parcel var1) {
      super(var1.readString());
      this.recoverField_b = var1.readString();
   }

   @Override
   public long length() {
      return this.recoverField_b.length();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.getAbsolutePath());
      var1.writeString(this.recoverField_b);
   }
}
