package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class Participant$1 implements Creator {
   public Participant createFromParcel(Parcel var1) {
      String var2 = var1.readString();
      String var3 = var1.readString();
      String var4 = var1.readString();
      String var5 = var1.readString();
      Avatar var6 = (Avatar)var1.readValue(Avatar.class.getClassLoader());
      Boolean var7 = var1.readByte() == 0;
      return Participant.create(var2, var3, var4, var5, var6, var7);
   }

   public Participant[] newArray(int var1) {
      return new Participant[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
