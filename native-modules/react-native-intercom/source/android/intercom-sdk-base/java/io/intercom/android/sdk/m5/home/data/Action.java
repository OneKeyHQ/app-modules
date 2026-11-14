package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003J+\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/Action;",
         "",
         "label",
         "",
         "icon",
         "Lio/intercom/android/sdk/m5/home/data/IconType;",
         "subtitle",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/IconType;Ljava/lang/String;)V",
         "getLabel",
         "()Ljava/lang/String;",
         "getIcon",
         "()Lio/intercom/android/sdk/m5/home/data/IconType;",
         "getSubtitle",
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
public final class Action {
   @SerializedName("label")
   @NotNull
   private final String label;
   @SerializedName("icon")
   @Nullable
   private final IconType icon;
   @SerializedName("subtitle")
   @Nullable
   private final String subtitle;
   public static int $stable;

   static {}

   public Action(@NotNull String var1, @Nullable IconType var2, @Nullable String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "label");
      this.label = var1;
      this.icon = var2;
      this.subtitle = var3;
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   @Nullable
   public final IconType getIcon() {
      return this.icon;
   }

   @Nullable
   public final String getSubtitle() {
      return this.subtitle;
   }

   @NotNull
   public final String component1() {
      return this.label;
   }

   @Nullable
   public final IconType component2() {
      return this.icon;
   }

   @Nullable
   public final String component3() {
      return this.subtitle;
   }

   @NotNull
   public final Action copy(@NotNull String var1, @Nullable IconType var2, @Nullable String var3) {
      Intrinsics.checkNotNullParameter(var1, "label");
      return new Action(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Action copy$default(Action var0, String var1, IconType var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.label;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.icon;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.subtitle;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Action(label=" + this.label + ", icon=" + this.icon + ", subtitle=" + this.subtitle + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.label.hashCode();
      var1 = var1 * 31 + (this.icon == null ? 0 : this.icon.hashCode());
      return var1 * 31 + (this.subtitle == null ? 0 : this.subtitle.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Action)) {
         return false;
      } else {
         Action var2 = (Action)var1;
         if (!Intrinsics.areEqual(this.label, var2.label)) {
            return false;
         } else {
            return this.icon != var2.icon ? false : Intrinsics.areEqual(this.subtitle, var2.subtitle);
         }
      }
   }
}
