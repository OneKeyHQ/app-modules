package io.intercom.android.sdk.m5.upload.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class MediaData$Gif$Creator implements Creator {
   public final MediaData$Gif[] newArray(int var1) {
      return new MediaData$Gif[var1];
   }

   public final MediaData$Gif createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new MediaData$Gif(var1.readInt(), var1.readInt(), var1.readString(), var1.readString());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
