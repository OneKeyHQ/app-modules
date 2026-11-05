package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.m5.components.avatar.AvatarShape;

@AutoValue
public abstract class Avatar implements Parcelable {
   public static final Avatar NULL = create("", "", AvatarShape.CIRCLE, "", "");
   public static final Creator CREATOR = new Avatar$1();

   public static Avatar create(String var0, String var1, AvatarShape var2, String var3, String var4) {
      return new AutoValue_Avatar(var0, var1, var2, var3, var4);
   }

   public static Avatar create(String var0, String var1, AvatarShape var2, String var3) {
      return new AutoValue_Avatar(var0, var1, var2, var3, "");
   }

   public static Avatar create(String var0, String var1, AvatarShape var2) {
      return new AutoValue_Avatar(var0, var1, var2, var1, "");
   }

   public static Avatar create(String var0, String var1) {
      return new AutoValue_Avatar(var0, var1, AvatarShape.CIRCLE, var1, "");
   }

   public abstract String getImageUrl();

   public abstract String getInitials();

   public abstract AvatarShape getShape();

   public abstract String getLabel();

   public abstract String getImageDarkUrl();

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.getImageUrl());
      var1.writeString(this.getInitials());
      var1.writeString(this.getShape().name());
      var1.writeString(this.getLabel());
      var1.writeString(this.getImageDarkUrl());
   }
}
