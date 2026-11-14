package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

class ScreenAction$1 implements Creator {
   public ScreenAction createFromParcel(Parcel var1) {
      List var2 = new ArrayList();
      var1.readList(var2, String.class.getClassLoader());
      return ScreenAction.create(var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readString(), var1.readString(), var2);
   }

   public ScreenAction[] newArray(int var1) {
      return new ScreenAction[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
