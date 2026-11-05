package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u0018B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0005HÆ\u0003J'\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "",
         "icon",
         "Lio/intercom/android/sdk/models/PoweredBy$PoweredByIconType;",
         "linkUrl",
         "",
         "text",
         "<init>",
         "(Lio/intercom/android/sdk/models/PoweredBy$PoweredByIconType;Ljava/lang/String;Ljava/lang/String;)V",
         "getIcon",
         "()Lio/intercom/android/sdk/models/PoweredBy$PoweredByIconType;",
         "getLinkUrl",
         "()Ljava/lang/String;",
         "getText",
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
         "PoweredByIconType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class PoweredBy {
   @SerializedName("icon")
   @NotNull
   private final PoweredBy$PoweredByIconType icon;
   @SerializedName("link_url")
   @NotNull
   private final String linkUrl;
   @SerializedName("text")
   @NotNull
   private final String text;
   public static int $stable;

   static {}

   public PoweredBy(@NotNull PoweredBy$PoweredByIconType var1, @NotNull String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "icon");
      Intrinsics.checkNotNullParameter(var2, "linkUrl");
      Intrinsics.checkNotNullParameter(var3, "text");
      this.icon = var1;
      this.linkUrl = var2;
      this.text = var3;
   }

   @NotNull
   public final PoweredBy$PoweredByIconType getIcon() {
      return this.icon;
   }

   @NotNull
   public final String getLinkUrl() {
      return this.linkUrl;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   public final PoweredBy$PoweredByIconType component1() {
      return this.icon;
   }

   @NotNull
   public final String component2() {
      return this.linkUrl;
   }

   @NotNull
   public final String component3() {
      return this.text;
   }

   @NotNull
   public final PoweredBy copy(@NotNull PoweredBy$PoweredByIconType var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "icon");
      Intrinsics.checkNotNullParameter(var2, "linkUrl");
      Intrinsics.checkNotNullParameter(var3, "text");
      return new PoweredBy(var1, var2, var3);
   }

   // $VF: synthetic method
   public static PoweredBy copy$default(PoweredBy var0, PoweredBy$PoweredByIconType var1, String var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.icon;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.linkUrl;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.text;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "PoweredBy(icon=" + this.icon + ", linkUrl=" + this.linkUrl + ", text=" + this.text + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.icon.hashCode();
      var1 = var1 * 31 + this.linkUrl.hashCode();
      return var1 * 31 + this.text.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof PoweredBy)) {
         return false;
      } else {
         PoweredBy var2 = (PoweredBy)var1;
         if (this.icon != var2.icon) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.linkUrl, var2.linkUrl) ? false : Intrinsics.areEqual(this.text, var2.text);
         }
      }
   }
}
