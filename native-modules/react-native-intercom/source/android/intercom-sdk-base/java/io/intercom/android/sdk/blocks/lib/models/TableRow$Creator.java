package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TableRow$Creator implements Creator {
   public final TableRow[] newArray(int var1) {
      return new TableRow[var1];
   }

   public final TableRow createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      int var2 = var1.readInt();
      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 0; var4 != var2; var4++) {
         var3.add(TableCell.CREATOR.createFromParcel(var1));
      }

      List var5 = var3;
      return new TableRow(var5);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
