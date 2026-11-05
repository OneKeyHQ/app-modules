package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomPreviewArgs$Creator implements Creator {
   public final IntercomPreviewArgs[] newArray(int var1) {
      return new IntercomPreviewArgs[var1];
   }

   public final IntercomPreviewArgs createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      int var2 = var1.readInt();
      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 0; var4 != var2; var4++) {
         var3.add(var1.readParcelable(IntercomPreviewArgs.class.getClassLoader()));
      }

      List var10000 = var3;
      DeleteType var10001 = (DeleteType)var1.readParcelable(IntercomPreviewArgs.class.getClassLoader());
      String var10002 = var1.readString();
      boolean var10003 = var1.readInt() != 0;
      int var10004 = var1.readInt();
      DownloadState var5 = (DownloadState)DownloadState.CREATOR.createFromParcel(var1);
      int var6 = var10004;
      boolean var7 = var10003;
      String var8 = var10002;
      DeleteType var9 = var10001;
      List var10 = var10000;
      return new IntercomPreviewArgs(var10, var9, var8, var7, var6, var5);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
