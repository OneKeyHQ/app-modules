package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Participant implements Parcelable {
   public static final String ADMIN_TYPE = "admin";
   public static final String USER_TYPE = "user";
   public static final Participant NULL = create("", "", "", "", Avatar.create("", ""), false);
   public static final Creator CREATOR = new Participant$1();

   public static Participant create(String var0, String var1, String var2, String var3, Avatar var4, Boolean var5) {
      return new AutoValue_Participant(var0, var1, var2, var3, var4, var5);
   }

   public abstract String getId();

   public abstract String getName();

   public abstract String getType();

   public abstract String getEmail();

   public abstract Avatar getAvatar();

   public abstract Boolean isBot();

   public boolean isAdmin() {
      return "admin".equals(this.getType());
   }

   @VisibleForTesting
   String nameOrEmail() {
      return this.getName().isEmpty() ? this.getEmail() : this.getName();
   }

   public String getForename() {
      return this.nameOrEmail().trim().split(" ")[0];
   }

   public boolean isUserWithId(@Nullable String var1) {
      return "user".equals(this.getType()) && this.getId().equals(var1);
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeString(this.getId());
      var1.writeString(this.getName());
      var1.writeString(this.getType());
      var1.writeString(this.getEmail());
      var1.writeValue(this.getAvatar());
      var1.writeByte((byte)(this.isBot() ? 1 : 0));
   }
}
