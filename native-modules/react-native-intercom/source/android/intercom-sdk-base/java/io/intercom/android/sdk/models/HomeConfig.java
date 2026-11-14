package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0015H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/models/HomeConfig;",
         "",
         "header",
         "Lio/intercom/android/sdk/models/HeaderConfig;",
         "openConfig",
         "Lio/intercom/android/sdk/models/OpenConfig;",
         "<init>",
         "(Lio/intercom/android/sdk/models/HeaderConfig;Lio/intercom/android/sdk/models/OpenConfig;)V",
         "getHeader",
         "()Lio/intercom/android/sdk/models/HeaderConfig;",
         "getOpenConfig",
         "()Lio/intercom/android/sdk/models/OpenConfig;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeConfig {
   @SerializedName("header")
   @NotNull
   private final HeaderConfig header;
   @SerializedName("open_config")
   @NotNull
   private final OpenConfig openConfig;
   public static final int $stable;

   static { $stable = 8; }

   public HomeConfig(@NotNull HeaderConfig var1, @NotNull OpenConfig var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "header");
      Intrinsics.checkNotNullParameter(var2, "openConfig");
      this.header = var1;
      this.openConfig = var2;
   }

   // $VF: synthetic method
   public HomeConfig(HeaderConfig var1, OpenConfig var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (HeaderConfig) (new HeaderConfig(null, null, false, 7, null)) : var1), (((var3 & 2) != 0) ? (OpenConfig) (new OpenConfig(null, null, null, 7, null)) : var2));
   }

   @NotNull
   public final HeaderConfig getHeader() {
      return this.header;
   }

   @NotNull
   public final OpenConfig getOpenConfig() {
      return this.openConfig;
   }

   @NotNull
   public final HeaderConfig component1() {
      return this.header;
   }

   @NotNull
   public final OpenConfig component2() {
      return this.openConfig;
   }

   @NotNull
   public final HomeConfig copy(@NotNull HeaderConfig var1, @NotNull OpenConfig var2) {
      Intrinsics.checkNotNullParameter(var1, "header");
      Intrinsics.checkNotNullParameter(var2, "openConfig");
      return new HomeConfig(var1, var2);
   }

   // $VF: synthetic method
   public static HomeConfig copy$default(HomeConfig var0, HeaderConfig var1, OpenConfig var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.header;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.openConfig;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "HomeConfig(header=" + this.header + ", openConfig=" + this.openConfig + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.header.hashCode();
      return var1 * 31 + this.openConfig.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeConfig)) {
         return false;
      }

      HomeConfig var2 = (HomeConfig)var1;
      return !Intrinsics.areEqual(this.header, var2.header) ? false : Intrinsics.areEqual(this.openConfig, var2.openConfig);
   }

   public HomeConfig() {
      this(null, null, 3, null);
   }
}
