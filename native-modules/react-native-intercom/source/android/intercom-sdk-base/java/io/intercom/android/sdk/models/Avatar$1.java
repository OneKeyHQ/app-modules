package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;

class Avatar$1 implements Creator {
   public Avatar createFromParcel(Parcel var1) {
      String var2 = var1.readString();
      String var3 = var1.readString();
      AvatarShape var4 = AvatarShape.Companion.fromName(var1.readString());
      String var5 = var1.readString();
      String var6 = var1.readString();
      return Avatar.create(var2, var3, var4, var5, var6);
   }

   public Avatar[] newArray(int var1) {
      return new Avatar[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
