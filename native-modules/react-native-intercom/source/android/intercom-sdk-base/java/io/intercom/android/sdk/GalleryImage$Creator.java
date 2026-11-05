package io.intercom.android.sdk;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class GalleryImage$Creator implements Creator {
   public final GalleryImage[] newArray(int var1) {
      return new GalleryImage[var1];
   }

   public final GalleryImage createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new GalleryImage(
         var1.readString(),
         var1.readString(),
         (Uri)var1.readParcelable(GalleryImage.class.getClassLoader()),
         var1.readString(),
         var1.readString(),
         var1.readInt(),
         var1.readInt(),
         var1.readInt(),
         var1.readInt() != 0,
         var1.readInt() != 0,
         var1.readString()
      );
   }




}
