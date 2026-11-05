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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0015H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/models/ConfigModules;",
         "",
         "customization",
         "Lio/intercom/android/sdk/models/CustomizationModel;",
         "home",
         "Lio/intercom/android/sdk/models/HomeConfig;",
         "<init>",
         "(Lio/intercom/android/sdk/models/CustomizationModel;Lio/intercom/android/sdk/models/HomeConfig;)V",
         "getCustomization",
         "()Lio/intercom/android/sdk/models/CustomizationModel;",
         "getHome",
         "()Lio/intercom/android/sdk/models/HomeConfig;",
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
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConfigModules {
   @NotNull
   public static final ConfigModules$Companion Companion = new ConfigModules$Companion(null);
   @SerializedName("customization")
   @NotNull
   private final CustomizationModel customization;
   @SerializedName("home")
   @NotNull
   private final HomeConfig home;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final ConfigModules DEFAULT = new ConfigModules(
      new CustomizationModel(
         new CustomizationColorsModel("", "", null, null, null, null, 60, null),
         new CustomizationColorsModel("", "", null, null, null, null, 60, null),
         null,
         "",
         "",
         new CustomizationColorsModel("", "", null, null, null, null, 60, null),
         0,
         null,
         null,
         "",
         null,
         0,
         4,
         null
      ),
      null,
      2,
      null
   );
   @NotNull
   private static final ConfigModules DEFAULT_WITH_ACTION_COLORS = new ConfigModules(
      new CustomizationModel(
         new CustomizationColorsModel("#000000", "#FFFFFF", "#FFFFFF", null, null, null, 56, null),
         new CustomizationColorsModel("#000000", "#FAFAFA", null, null, null, null, 60, null),
         null,
         "",
         "",
         new CustomizationColorsModel("", "", null, null, null, null, 60, null),
         0,
         null,
         null,
         "",
         null,
         0,
         4,
         null
      ),
      null,
      2,
      null
   );

   public ConfigModules(@NotNull CustomizationModel var1, @NotNull HomeConfig var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "customization");
      Intrinsics.checkNotNullParameter(var2, "home");
      this.customization = var1;
      this.home = var2;
   }

   // $VF: synthetic method
   public ConfigModules(CustomizationModel var1, HomeConfig var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (HomeConfig) (new HomeConfig(null, null, 3, null)) : var2));
   }

   @NotNull
   public final CustomizationModel getCustomization() {
      return this.customization;
   }

   @NotNull
   public final HomeConfig getHome() {
      return this.home;
   }

   @NotNull
   public final CustomizationModel component1() {
      return this.customization;
   }

   @NotNull
   public final HomeConfig component2() {
      return this.home;
   }

   @NotNull
   public final ConfigModules copy(@NotNull CustomizationModel var1, @NotNull HomeConfig var2) {
      Intrinsics.checkNotNullParameter(var1, "customization");
      Intrinsics.checkNotNullParameter(var2, "home");
      return new ConfigModules(var1, var2);
   }

   // $VF: synthetic method
   public static ConfigModules copy$default(ConfigModules var0, CustomizationModel var1, HomeConfig var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.customization;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.home;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConfigModules(customization=" + this.customization + ", home=" + this.home + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.customization.hashCode();
      return var1 * 31 + this.home.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ConfigModules)) {
         return false;
      }

      ConfigModules var2 = (ConfigModules)var1;
      return !Intrinsics.areEqual(this.customization, var2.customization) ? false : Intrinsics.areEqual(this.home, var2.home);
   }

   @NotNull
   public static final ConfigModules getDEFAULT_WITH_ACTION_COLORS() {
      return Companion.getDEFAULT_WITH_ACTION_COLORS();
   }

   // $VF: synthetic method
   public static final ConfigModules access$getDEFAULT$cp() {
      return DEFAULT;
   }

   // $VF: synthetic method
   public static final ConfigModules access$getDEFAULT_WITH_ACTION_COLORS$cp() {
      return DEFAULT_WITH_ACTION_COLORS;
   }
}
