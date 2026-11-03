package cn.jiguang.common.app.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Locale;

public class ControlGroup implements Parcelable {
   public final int recoverField_a;
   public final String recoverField_b;
   public final String recoverField_c;
   public static final Creator CREATOR = new Creator() {
      public ControlGroup recoverMethod_a_74eaa447(Parcel var1) {
         return new ControlGroup(var1);
      }

      public ControlGroup[] recoverMethod_a_98432cb2(int var1) {
         return new ControlGroup[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_98432cb2(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_74eaa447(var1);
      }
   };

   protected ControlGroup(Parcel var1) {
      this.recoverField_a = var1.readInt();
      this.recoverField_b = var1.readString();
      this.recoverField_c = var1.readString();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeInt(this.recoverField_a);
      var1.writeString(this.recoverField_b);
      var1.writeString(this.recoverField_c);
   }

   @Override
   public String toString() {
      return String.format(Locale.ENGLISH, "%d:%s:%s", this.recoverField_a, this.recoverField_b, this.recoverField_c);
   }
}
