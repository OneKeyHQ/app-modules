package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

class LinkList$1 implements Creator {
   public LinkList createFromParcel(Parcel var1) {
      List var2 = new ArrayList();
      var1.readList(var2, Link.class.getClassLoader());
      Link var3 = (Link)var1.readParcelable(Link.class.getClassLoader());
      return new LinkList(var2, var3);
   }

   public LinkList[] newArray(int var1) {
      return new LinkList[var1];
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
