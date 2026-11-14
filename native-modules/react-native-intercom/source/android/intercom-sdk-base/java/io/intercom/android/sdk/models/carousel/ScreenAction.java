package io.intercom.android.sdk.models.carousel;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.auto.value.AutoValue;
import io.intercom.android.sdk.carousel.PermissionManager;
import java.util.List;

@AutoValue
public abstract class ScreenAction implements Parcelable {
   public static ScreenAction NULL = new ScreenAction$Builder().build();
   private static final String PERMISSION_TYPE_SUFFIX = "permission";
   public static final Creator CREATOR = new ScreenAction$1();

   public static ScreenAction create(String var0, String var1, String var2, String var3, String var4, String var5, List var6) {
      return new AutoValue_ScreenAction(var0, var1, var2, var3, var4, var5, var6);
   }

   public abstract String getId();

   public abstract String getType();

   public abstract String getTitle();

   public abstract String getTextColor();

   public abstract String getBackgroundColor();

   public abstract String getUri();

   public abstract List getAndroidPermissions();

   public boolean isPermissionAction() {
      return this.getType().contains("permission");
   }

   public List getValidPermissions(PermissionManager var1) {
      return var1.permissionsExistInManifest(this.getAndroidPermissions());
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel var1, int var2) {
      var1.writeList(this.getAndroidPermissions());
      var1.writeString(this.getId());
      var1.writeString(this.getType());
      var1.writeString(this.getTitle());
      var1.writeString(this.getTextColor());
      var1.writeString(this.getBackgroundColor());
      var1.writeString(this.getUri());
   }
}
