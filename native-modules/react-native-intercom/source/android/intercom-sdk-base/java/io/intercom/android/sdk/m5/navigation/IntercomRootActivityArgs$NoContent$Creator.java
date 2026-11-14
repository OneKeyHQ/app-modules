package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomRootActivityArgs$NoContent$Creator implements Creator {
   public final IntercomRootActivityArgs$NoContent[] newArray(int var1) {
      return new IntercomRootActivityArgs$NoContent[var1];
   }

   public final IntercomRootActivityArgs$NoContent createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      var1.readInt();
      return IntercomRootActivityArgs$NoContent.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
