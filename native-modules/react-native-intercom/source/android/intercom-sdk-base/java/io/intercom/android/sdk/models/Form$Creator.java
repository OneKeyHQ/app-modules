package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class Form$Creator implements Creator {
   public final Form[] newArray(int var1) {
      return new Form[var1];
   }

   public final Form createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      String var10000 = var1.readString();
      int var2 = var1.readInt();
      ArrayList var3 = new ArrayList(var2);
      int var4 = 0;
      String var5 = var10000;

      while (var4 != var2) {
         var3.add(Attribute.CREATOR.createFromParcel(var1));
         var4++;
      }

      List var10001 = var3;
      boolean var6 = var1.readInt() != 0;
      List var7 = var10001;
      String var8 = var5;
      return new Form(var8, var7, var6);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
