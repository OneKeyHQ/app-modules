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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0005HÆ\u0003J!\u0010\u0012\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0016\u001a\u00020\u0017HÖ\u0001J\t\u0010\u0018\u001a\u00020\u0005HÖ\u0001R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/config/SharedStorageConfiguration;",
         "",
         "saveAt",
         "Lio/intercom/android/sdk/lightcompressor/config/SaveLocation;",
         "subFolderName",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/lightcompressor/config/SaveLocation;Ljava/lang/String;)V",
         "getSaveAt",
         "()Lio/intercom/android/sdk/lightcompressor/config/SaveLocation;",
         "setSaveAt",
         "(Lio/intercom/android/sdk/lightcompressor/config/SaveLocation;)V",
         "getSubFolderName",
         "()Ljava/lang/String;",
         "setSubFolderName",
         "(Ljava/lang/String;)V",
         "component1",
         "component2",
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
public final class SharedStorageConfiguration {
   @Nullable
   private SaveLocation saveAt;
   @Nullable
   private String subFolderName;

   public SharedStorageConfiguration(@Nullable SaveLocation var1, @Nullable String var2) {
      this.saveAt = var1;
      this.subFolderName = var2;
   }

   // $VF: synthetic method
   public SharedStorageConfiguration(SaveLocation var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (SaveLocation) (null) : var1), (((var3 & 2) != 0) ? (String) (null) : var2));
   }

   @Nullable
   public final SaveLocation getSaveAt() {
      return this.saveAt;
   }

   public final void setSaveAt(@Nullable SaveLocation var1) {
      this.saveAt = var1;
   }

   @Nullable
   public final String getSubFolderName() {
      return this.subFolderName;
   }

   public final void setSubFolderName(@Nullable String var1) {
      this.subFolderName = var1;
   }

   @Nullable
   public final SaveLocation component1() {
      return this.saveAt;
   }

   @Nullable
   public final String component2() {
      return this.subFolderName;
   }

   @NotNull
   public final SharedStorageConfiguration copy(@Nullable SaveLocation var1, @Nullable String var2) {
      return new SharedStorageConfiguration(var1, var2);
   }

   // $VF: synthetic method
   public static SharedStorageConfiguration copy$default(SharedStorageConfiguration var0, SaveLocation var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.saveAt;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.subFolderName;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "SharedStorageConfiguration(saveAt=" + this.saveAt + ", subFolderName=" + this.subFolderName + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.saveAt == null ? 0 : this.saveAt.hashCode();
      return var1 * 31 + (this.subFolderName == null ? 0 : this.subFolderName.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SharedStorageConfiguration)) {
         return false;
      }

      SharedStorageConfiguration var2 = (SharedStorageConfiguration)var1;
      return this.saveAt != var2.saveAt ? false : Intrinsics.areEqual(this.subFolderName, var2.subFolderName);
   }

   public SharedStorageConfiguration() {
      this(null, null, 3, null);
   }
}
