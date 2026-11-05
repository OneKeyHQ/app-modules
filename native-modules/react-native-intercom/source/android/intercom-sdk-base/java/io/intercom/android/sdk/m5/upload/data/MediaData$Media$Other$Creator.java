package io.intercom.android.sdk.m5.upload.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class MediaData$Media$Other$Creator implements Creator {
   public final MediaData$Media$Other[] newArray(int var1) {
      return new MediaData$Media$Other[var1];
   }

   public final MediaData$Media$Other createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new MediaData$Media$Other(
         var1.readString(), var1.readLong(), var1.readString(), (Uri)var1.readParcelable(MediaData$Media$Other.class.getClassLoader())
      );
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
