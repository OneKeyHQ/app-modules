package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Appearance implements Parcelable {
   public static Appearance NULL = new Appearance$Builder().build();
   public static final Creator CREATOR = new Appearance$1();

   static Appearance create(String var0, String var1, String var2) {
      return new AutoValue_Appearance(var0, var1, var2);
   }

   public abstract String getType();

   public abstract String getAlignment();

   public abstract String getTextColor();

   public Appearance$Builder toBuilder() {
      return new Appearance$Builder().withType(this.getType()).withTextAlignment(this.getAlignment()).withTextColor(this.getTextColor());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.getType());
      var1.writeString(this.getAlignment());
      var1.writeString(this.getTextColor());
   }
}
