package io.intercom.android.sdk.lightcompressor.config;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0013\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000b\u0010\t\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0015\u0010\n\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003HÆ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u000e\u001a\u00020\u000fHÖ\u0001J\t\u0010\u0010\u001a\u00020\u0003HÖ\u0001R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/config/AppSpecificStorageConfiguration;",
         "",
         "subFolderName",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "getSubFolderName",
         "()Ljava/lang/String;",
         "setSubFolderName",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class AppSpecificStorageConfiguration {
   @Nullable
   private String subFolderName;

   public AppSpecificStorageConfiguration(@Nullable String var1) {
      this.subFolderName = var1;
   }

   // $VF: synthetic method
   public AppSpecificStorageConfiguration(String var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (String) (null) : var1));
   }

   @Nullable
   public final String getSubFolderName() {
      return this.subFolderName;
   }

   public final void setSubFolderName(@Nullable String var1) {
      this.subFolderName = var1;
   }

   @Nullable
   public final String component1() {
      return this.subFolderName;
   }

   @NotNull
   public final AppSpecificStorageConfiguration copy(@Nullable String var1) {
      return new AppSpecificStorageConfiguration(var1);
   }

   // $VF: synthetic method
   public static AppSpecificStorageConfiguration copy$default(AppSpecificStorageConfiguration var0, String var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.subFolderName;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "AppSpecificStorageConfiguration(subFolderName=" + this.subFolderName + ')';
   }

   @Override
   public int hashCode() {
      return this.subFolderName == null ? 0 : this.subFolderName.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AppSpecificStorageConfiguration)) {
         return false;
      }

      AppSpecificStorageConfiguration var2 = (AppSpecificStorageConfiguration)var1;
      return Intrinsics.areEqual(this.subFolderName, var2.subFolderName);
   }

   public AppSpecificStorageConfiguration() {
      this(null, 1, null);
   }
}
