package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TicketType$Creator implements Creator {
   public final TicketType[] newArray(int var1) {
      return new TicketType[var1];
   }

   public final TicketType createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      long var10000 = var1.readLong();
      String var10001 = var1.readString();
      String var10002 = var1.readString();
      int var2 = var1.readInt();
      ArrayList var3 = new ArrayList(var2);
      int var4 = 0;
      String var8 = var10002;
      String var7 = var10001;
      long var5 = var10000;

      while (var4 != var2) {
         var3.add(TicketAttribute.CREATOR.createFromParcel(var1));
         var4++;
      }

      List var10003 = var3;
      boolean var9 = var1.readInt() != 0;
      List var10 = var10003;
      String var11 = var8;
      String var12 = var7;
      long var13 = var5;
      return new TicketType(var13, var12, var11, var10, var9);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
