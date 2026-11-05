package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import io.intercom.android.sdk.m5.home.data.IconType;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B'\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0007HÆ\u0003J)\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0018H×\u0001J\t\u0010\u0019\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/models/EmptyState$Action;",
         "",
         "type",
         "Lio/intercom/android/sdk/models/ActionType;",
         "label",
         "",
         "icon",
         "Lio/intercom/android/sdk/m5/home/data/IconType;",
         "<init>",
         "(Lio/intercom/android/sdk/models/ActionType;Ljava/lang/String;Lio/intercom/android/sdk/m5/home/data/IconType;)V",
         "getType",
         "()Lio/intercom/android/sdk/models/ActionType;",
         "getLabel",
         "()Ljava/lang/String;",
         "getIcon",
         "()Lio/intercom/android/sdk/m5/home/data/IconType;",
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
public final class EmptyState$Action {
   @SerializedName("type")
   @NotNull
   private final ActionType type;
   @SerializedName("label")
   @NotNull
   private final String label;
   @SerializedName("icon")
   @Nullable
   private final IconType icon;
   public static int $stable;

   static {}

   public EmptyState$Action(@NotNull ActionType var1, @NotNull String var2, @Nullable IconType var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "type");
      Intrinsics.checkNotNullParameter(var2, "label");
      this.type = var1;
      this.label = var2;
      this.icon = var3;
   }

   // $VF: synthetic method
   public EmptyState$Action(ActionType var1, String var2, IconType var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (ActionType) (ActionType.MESSAGE) : var1), (((var4 & 2) != 0) ? (String) ("") : var2), (((var4 & 4) != 0) ? (IconType) (null) : var3));
   }

   @NotNull
   public final ActionType getType() {
      return this.type;
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   @Nullable
   public final IconType getIcon() {
      return this.icon;
   }

   @NotNull
   public final ActionType component1() {
      return this.type;
   }

   @NotNull
   public final String component2() {
      return this.label;
   }

   @Nullable
   public final IconType component3() {
      return this.icon;
   }

   @NotNull
   public final EmptyState$Action copy(@NotNull ActionType var1, @NotNull String var2, @Nullable IconType var3) {
      Intrinsics.checkNotNullParameter(var1, "type");
      Intrinsics.checkNotNullParameter(var2, "label");
      return new EmptyState$Action(var1, var2, var3);
   }

   // $VF: synthetic method
   public static EmptyState$Action copy$default(EmptyState$Action var0, ActionType var1, String var2, IconType var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.type;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.label;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.icon;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Action(type=" + this.type + ", label=" + this.label + ", icon=" + this.icon + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = var1 * 31 + this.label.hashCode();
      return var1 * 31 + (this.icon == null ? 0 : this.icon.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof EmptyState$Action)) {
         return false;
      } else {
         EmptyState$Action var2 = (EmptyState$Action)var1;
         if (this.type != var2.type) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.label, var2.label) ? false : this.icon == var2.icon;
         }
      }
   }

   public EmptyState$Action() {
      this(null, null, null, 7, null);
   }
}
