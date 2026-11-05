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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/models/CloseButtonModel;",
         "",
         "backgroundColor",
         "",
         "backgroundOpacity",
         "",
         "foregroundColor",
         "<init>",
         "(Ljava/lang/String;FLjava/lang/String;)V",
         "getBackgroundColor",
         "()Ljava/lang/String;",
         "getBackgroundOpacity",
         "()F",
         "getForegroundColor",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class CloseButtonModel {
   @SerializedName("background_color")
   @NotNull
   private final String backgroundColor;
   @SerializedName("background_opacity")
   private final float backgroundOpacity;
   @SerializedName("foreground_color")
   @NotNull
   private final String foregroundColor;
   public static int $stable;

   static {}

   public CloseButtonModel(@NotNull String var1, float var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var3, "foregroundColor");
      this.backgroundColor = var1;
      this.backgroundOpacity = var2;
      this.foregroundColor = var3;
   }

   // $VF: synthetic method
   public CloseButtonModel(String var1, float var2, String var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (String) ("#FFFFFF") : var1), (((var4 & 2) != 0) ? (float) (1.0F) : var2), (((var4 & 4) != 0) ? (String) ("#000000") : var3));
   }

   @NotNull
   public final String getBackgroundColor() {
      return this.backgroundColor;
   }

   public final float getBackgroundOpacity() {
      return this.backgroundOpacity;
   }

   @NotNull
   public final String getForegroundColor() {
      return this.foregroundColor;
   }

   @NotNull
   public final String component1() {
      return this.backgroundColor;
   }

   public final float component2() {
      return this.backgroundOpacity;
   }

   @NotNull
   public final String component3() {
      return this.foregroundColor;
   }

   @NotNull
   public final CloseButtonModel copy(@NotNull String var1, float var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var3, "foregroundColor");
      return new CloseButtonModel(var1, var2, var3);
   }

   // $VF: synthetic method
   public static CloseButtonModel copy$default(CloseButtonModel var0, String var1, float var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.backgroundOpacity;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.foregroundColor;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "CloseButtonModel(backgroundColor="
         + this.backgroundColor
         + ", backgroundOpacity="
         + this.backgroundOpacity
         + ", foregroundColor="
         + this.foregroundColor
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.backgroundColor.hashCode();
      var1 = var1 * 31 + Float.hashCode(this.backgroundOpacity);
      return var1 * 31 + this.foregroundColor.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CloseButtonModel)) {
         return false;
      } else {
         CloseButtonModel var2 = (CloseButtonModel)var1;
         if (!Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else {
            return Float.compare(this.backgroundOpacity, var2.backgroundOpacity) != 0 ? false : Intrinsics.areEqual(this.foregroundColor, var2.foregroundColor);
         }
      }
   }

   public CloseButtonModel() {
      this(null, 0.0F, null, 7, null);
   }
}
