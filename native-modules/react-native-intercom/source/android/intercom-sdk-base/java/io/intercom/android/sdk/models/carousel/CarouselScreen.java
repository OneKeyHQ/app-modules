package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.blocks.lib.models.Block;
import java.util.List;

@AutoValue
public abstract class CarouselScreen implements Parcelable {
   public static CarouselScreen NULL = new CarouselScreen$Builder().build();
   public static final Creator CREATOR = new CarouselScreen$1();

   public static CarouselScreen create(String var0, String var1, String var2, List var3, List var4, String var5) {
      return new AutoValue_CarouselScreen(var0, var1, var2, var3, var4, var5);
   }

   public abstract String getId();

   public abstract String getBackgroundColor();

   public abstract String getTextColor();

   public abstract List getBlocks();

   public abstract List getActions();

   public abstract String getVerticalAlignment();

   public int getGravity() {
      if ("bottom".equals(this.getVerticalAlignment())) {
         return 80;
      } else {
         return "center".equals(this.getVerticalAlignment()) ? 16 : 48;
      }
   }

   public ScreenAction getPermissionAction() {
      for (java.lang.Object recoveredEntryElement_0_40 : this.getActions()) {
         ScreenAction var2 = (ScreenAction) recoveredEntryElement_0_40;
         if (var2.isPermissionAction()) {
            return var2;
         }
      }

      return ScreenAction.NULL;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeList(this.getBlocks());
      var1.writeList(this.getActions());
      var1.writeString(this.getId());
      var1.writeString(this.getBackgroundColor());
      var1.writeString(this.getTextColor());
      var1.writeString(this.getVerticalAlignment());
   }
}
