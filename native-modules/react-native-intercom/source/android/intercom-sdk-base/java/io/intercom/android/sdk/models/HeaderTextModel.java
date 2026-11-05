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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B1\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/models/HeaderTextModel;",
         "",
         "text",
         "",
         "opacity",
         "",
         "color",
         "colorDark",
         "<init>",
         "(Ljava/lang/String;FLjava/lang/String;Ljava/lang/String;)V",
         "getText",
         "()Ljava/lang/String;",
         "getOpacity",
         "()F",
         "getColor",
         "getColorDark",
         "component1",
         "component2",
         "component3",
         "component4",
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
public final class HeaderTextModel {
   @SerializedName("content")
   @NotNull
   private final String text;
   @SerializedName("opacity")
   private final float opacity;
   @SerializedName("text_color")
   @NotNull
   private final String color;
   @SerializedName("text_color_dark")
   @Nullable
   private final String colorDark;
   public static int $stable;

   static {}

   public HeaderTextModel(@NotNull String var1, float var2, @NotNull String var3, @Nullable String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var3, "color");
      this.text = var1;
      this.opacity = var2;
      this.color = var3;
      this.colorDark = var4;
   }

   // $VF: synthetic method
   public HeaderTextModel(String var1, float var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (String) ("") : var1), (((var5 & 2) != 0) ? (float) (1.0F) : var2), (((var5 & 4) != 0) ? (String) ("#000000") : var3), (((var5 & 8) != 0) ? (String) (null) : var4));
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final float getOpacity() {
      return this.opacity;
   }

   @NotNull
   public final String getColor() {
      return this.color;
   }

   @Nullable
   public final String getColorDark() {
      return this.colorDark;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   public final float component2() {
      return this.opacity;
   }

   @NotNull
   public final String component3() {
      return this.color;
   }

   @Nullable
   public final String component4() {
      return this.colorDark;
   }

   @NotNull
   public final HeaderTextModel copy(@NotNull String var1, float var2, @NotNull String var3, @Nullable String var4) {
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var3, "color");
      return new HeaderTextModel(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static HeaderTextModel copy$default(HeaderTextModel var0, String var1, float var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.opacity;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.color;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.colorDark;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "HeaderTextModel(text=" + this.text + ", opacity=" + this.opacity + ", color=" + this.color + ", colorDark=" + this.colorDark + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      var1 = var1 * 31 + Float.hashCode(this.opacity);
      var1 = var1 * 31 + this.color.hashCode();
      return var1 * 31 + (this.colorDark == null ? 0 : this.colorDark.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderTextModel)) {
         return false;
      } else {
         HeaderTextModel var2 = (HeaderTextModel)var1;
         if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else if (Float.compare(this.opacity, var2.opacity) != 0) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.color, var2.color) ? false : Intrinsics.areEqual(this.colorDark, var2.colorDark);
         }
      }
   }

   public HeaderTextModel() {
      this(null, 0.0F, null, null, 15, null);
   }
}
