package cn.asus.push;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DataBuffer implements Parcelable {
   public String cmd;
   public Bundle bundle;
   public static final Creator CREATOR = new RecoverType4fccc7a7();

   public DataBuffer(String var1, Bundle var2) {
      this.cmd = var1;
      this.bundle = var2;
   }

   protected DataBuffer(Parcel var1) {
      this.cmd = var1.readString();
      this.bundle = var1.readBundle();
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.cmd);
      var1.writeBundle(this.bundle);
   }

   @Override
   public int describeContents() {
      return 0;
   }
}
