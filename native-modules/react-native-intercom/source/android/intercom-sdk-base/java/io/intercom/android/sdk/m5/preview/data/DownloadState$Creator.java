package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class DownloadState$Creator implements Creator {
   public final DownloadState[] newArray(int var1) {
      return new DownloadState[var1];
   }

   public final DownloadState createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new DownloadState(var1.readInt() != 0, var1.readString(), var1.readString(), var1.readString(), var1.readString());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
