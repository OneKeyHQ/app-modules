package io.intercom.android.sdk.m5.navigation;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomRootActivityArgs$HelpCenterCollectionArgs$Creator implements Creator {
   public final IntercomRootActivityArgs$HelpCenterCollectionArgs[] newArray(int var1) {
      return new IntercomRootActivityArgs$HelpCenterCollectionArgs[var1];
   }

   public final IntercomRootActivityArgs$HelpCenterCollectionArgs createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new IntercomRootActivityArgs$HelpCenterCollectionArgs(var1.readString(), var1.readString());
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
