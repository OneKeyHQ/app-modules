package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.ArrayList;
import java.util.List;

class CarouselScreen$1 implements Creator {
   public CarouselScreen createFromParcel(Parcel var1) {
      List var2 = new ArrayList();
      var1.readList(var2, ScreenAction.class.getClassLoader());
      List var3 = new ArrayList();
      var1.readList(var3, ScreenAction.class.getClassLoader());
      return CarouselScreen.create(var1.readString(), var1.readString(), var1.readString(), var2, var3, var1.readString());
   }

   public CarouselScreen[] newArray(int var1) {
      return new CarouselScreen[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
