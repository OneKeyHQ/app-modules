package io.intercom.android.sdk.helpcenter.articles;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ArticleStatsArguments$Creator implements Creator {
   public final ArticleStatsArguments[] newArray(int var1) {
      return new ArticleStatsArguments[var1];
   }

   public final ArticleStatsArguments createFromParcel(Parcel var1) {
      Intrinsics.checkNotNullParameter(var1, "parcel");
      return new ArticleStatsArguments(var1.readString(), var1.readInt() != 0);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
