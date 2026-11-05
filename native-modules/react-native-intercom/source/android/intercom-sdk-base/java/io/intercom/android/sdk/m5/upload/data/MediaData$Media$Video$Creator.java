package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class MediaData$Media$Video$Creator implements Creator {
   public final MediaData$Media$Video[] newArray(int var1) {
      return new MediaData$Media$Video[var1];
   }

   public final MediaData$Media$Video createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new MediaData$Media$Video(
         var1.readString(),
         var1.readInt(),
         var1.readInt(),
         var1.readLong(),
         var1.readString(),
         (Uri)var1.readParcelable(MediaData$Media$Video.class.getClassLoader()),
         var1.readLong(),
         (MediaData$Media$Image)(var1.readInt() == 0 ? null : MediaData$Media$Image.CREATOR.createFromParcel(var1))
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
