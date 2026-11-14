package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class MediaData$Media$Image$Creator implements Creator {
   public final MediaData$Media$Image[] newArray(int var1) {
      return new MediaData$Media$Image[var1];
   }

   public final MediaData$Media$Image createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      String var10000 = var1.readString();
      int var10001 = var1.readInt();
      int var10002 = var1.readInt();
      long var10003 = var1.readLong();
      String var10004 = var1.readString();
      Uri var10005 = (Uri)var1.readParcelable(MediaData$Media$Image.class.getClassLoader());
      int var2 = var1.readInt();
      LinkedHashMap var3 = new LinkedHashMap(var2);
      int var4 = 0;
      Uri var11 = var10005;
      String var10 = var10004;
      long var8 = var10003;
      int var7 = var10002;
      int var6 = var10001;
      String var5 = var10000;

      while (var4 != var2) {
         var3.put(var1.readString(), var1.readString());
         var4++;
      }

      Map var12 = var3;
      Uri var13 = var11;
      String var14 = var10;
      long var15 = var8;
      int var17 = var7;
      int var18 = var6;
      String var19 = var5;
      return new MediaData$Media$Image(var19, var18, var17, var15, var14, var13, var12);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
