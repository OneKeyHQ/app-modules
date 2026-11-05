package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class DeleteType$Remove$Creator implements Creator {
   public final DeleteType$Remove[] newArray(int var1) {
      return new DeleteType$Remove[var1];
   }

   public final DeleteType$Remove createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      var1.readInt();
      return DeleteType$Remove.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
