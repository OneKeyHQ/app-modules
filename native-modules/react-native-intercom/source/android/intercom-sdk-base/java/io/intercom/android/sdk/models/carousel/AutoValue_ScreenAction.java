package io.intercom.android.sdk.models.carousel;

import java.util.List;
import org.jspecify.annotations.Nullable;

final class AutoValue_ScreenAction extends ScreenAction {
   private final String id;
   private final String type;
   private final String title;
   private final String textColor;
   private final String backgroundColor;
   private final String uri;
   private final List androidPermissions;

   AutoValue_ScreenAction(String var1, String var2, String var3, String var4, String var5, String var6, List var7) {
      if (var1 == null) {
         throw new NullPointerException("Null id");
      }

      this.id = var1;
      if (var2 == null) {
         throw new NullPointerException("Null type");
      }

      this.type = var2;
      if (var3 == null) {
         throw new NullPointerException("Null title");
      }

      this.title = var3;
      if (var4 == null) {
         throw new NullPointerException("Null textColor");
      }

      this.textColor = var4;
      if (var5 == null) {
         throw new NullPointerException("Null backgroundColor");
      }

      this.backgroundColor = var5;
      if (var6 == null) {
         throw new NullPointerException("Null uri");
      }

      this.uri = var6;
      if (var7 == null) {
         throw new NullPointerException("Null androidPermissions");
      }

      this.androidPermissions = var7;
   }

   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public String getType() {
      return this.type;
   }

   @Override
   public String getTitle() {
      return this.title;
   }

   @Override
   public String getTextColor() {
      return this.textColor;
   }

   @Override
   public String getBackgroundColor() {
      return this.backgroundColor;
   }

   @Override
   public String getUri() {
      return this.uri;
   }

   @Override
   public List getAndroidPermissions() {
      return this.androidPermissions;
   }

   @Override
   public String toString() {
      return "ScreenAction{id="
         + this.id
         + ", type="
         + this.type
         + ", title="
         + this.title
         + ", textColor="
         + this.textColor
         + ", backgroundColor="
         + this.backgroundColor
         + ", uri="
         + this.uri
         + ", androidPermissions="
         + this.androidPermissions
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof ScreenAction)) {
         return false;
      }

      ScreenAction var2 = (ScreenAction)var1;
      return this.id.equals(var2.getId())
         && this.type.equals(var2.getType())
         && this.title.equals(var2.getTitle())
         && this.textColor.equals(var2.getTextColor())
         && this.backgroundColor.equals(var2.getBackgroundColor())
         && this.uri.equals(var2.getUri())
         && this.androidPermissions.equals(var2.getAndroidPermissions());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.id.hashCode();
      var1 *= 1000003;
      var1 ^= this.type.hashCode();
      var1 *= 1000003;
      var1 ^= this.title.hashCode();
      var1 *= 1000003;
      var1 ^= this.textColor.hashCode();
      var1 *= 1000003;
      var1 ^= this.backgroundColor.hashCode();
      var1 *= 1000003;
      var1 ^= this.uri.hashCode();
      var1 *= 1000003;
      return var1 ^ this.androidPermissions.hashCode();
   }
}
