package io.intercom.android.sdk.m5.preview.data;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class DeleteType$Delete$Creator implements Creator {
   public final DeleteType$Delete[] newArray(int var1) {
      return new DeleteType$Delete[var1];
   }

   public final DeleteType$Delete createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      var1.readInt();
      return DeleteType$Delete.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
