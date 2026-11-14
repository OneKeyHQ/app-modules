package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

@kotlin.Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class EventData$Creator implements Creator {
   public final EventData[] newArray(int var1) {
      return new EventData[var1];
   }

   public final EventData createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      EventParticipant$Builder var10000 = (EventParticipant$Builder)var1.readValue(EventData.class.getClassLoader());
      String var10001 = var1.readString();
      ArrayList var10002;
      if (var1.readInt() == 0) {
         var10002 = null;
      } else {
         int var2 = var1.readInt();
         ArrayList var3 = new ArrayList(var2);
         int var4 = 0;
         String var6 = var10001;
         EventParticipant$Builder var5 = var10000;

         while (var4 != var2) {
            var3.add(EventContent.CREATOR.createFromParcel(var1));
            var4++;
         }

         var10000 = var5;
         var10001 = var6;
         var10002 = var3;
      }

      var10002 = var10002;
      String var10003 = var1.readString();
      String var10004 = var1.readString();
      String var7;
      String var8 = var7 = var1.readString();
      String var9 = var1.readString();
      String var10 = var10004;
      String var11 = var10003;
      List var12 = var10002;
      String var13 = var10001;
      EventParticipant$Builder var14 = var10000;
      return new EventData(var14, var13, var12, var11, var10, var9, var8, var7);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
