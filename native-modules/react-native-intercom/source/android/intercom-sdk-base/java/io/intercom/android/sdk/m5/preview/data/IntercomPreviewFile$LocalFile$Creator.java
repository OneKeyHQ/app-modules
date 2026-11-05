package io.intercom.android.sdk.m5.preview.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomPreviewFile$LocalFile$Creator implements Creator {
   public final IntercomPreviewFile$LocalFile[] newArray(int var1) {
      return new IntercomPreviewFile$LocalFile[var1];
   }

   public final IntercomPreviewFile$LocalFile createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new IntercomPreviewFile$LocalFile((Uri)var1.readParcelable(IntercomPreviewFile$LocalFile.class.getClassLoader()));
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
