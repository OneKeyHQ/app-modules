package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TableCell$Creator implements Creator {
   public final TableCell[] newArray(int var1) {
      return new TableCell[var1];
   }

   public final TableCell createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      int var2 = var1.readInt();
      ArrayList var3 = new ArrayList(var2);

      for (int var4 = 0; var4 != var2; var4++) {
         var3.add(var1.readParcelable(TableCell.class.getClassLoader()));
      }

      List var5 = var3;
      return new TableCell(var5);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
