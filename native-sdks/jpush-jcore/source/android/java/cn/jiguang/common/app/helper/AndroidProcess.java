package cn.jiguang.common.app.helper;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AndroidProcess implements Parcelable {
   public final String recoverField_a;
   public final int recoverField_b;
   public static final Creator CREATOR = new Creator() {
      public AndroidProcess recoverMethod_a_45b45074(Parcel var1) {
         return new AndroidProcess(var1);
      }

      public AndroidProcess[] recoverMethod_a_f50c6a1f(int var1) {
         return new AndroidProcess[var1];
      }

      // $VF: synthetic method
      @Override
      public Object[] newArray(int var1) {
         return this.recoverMethod_a_f50c6a1f(var1);
      }

      // $VF: synthetic method
      @Override
      public Object createFromParcel(Parcel var1) {
         return this.recoverMethod_a_45b45074(var1);
      }
   };

   protected AndroidProcess(Parcel var1) {
      this.recoverField_a = var1.readString();
      this.recoverField_b = var1.readInt();
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.recoverField_a);
      var1.writeInt(this.recoverField_b);
   }
}
