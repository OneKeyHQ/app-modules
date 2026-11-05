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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u0015B\u001d\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0005H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/models/Space;",
         "",
         "type",
         "Lio/intercom/android/sdk/models/Space$Type;",
         "label",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/Space$Type;Ljava/lang/String;)V",
         "getType",
         "()Lio/intercom/android/sdk/models/Space$Type;",
         "getLabel",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Type",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Space {
   @SerializedName("type")
   @NotNull
   private final Space$Type type;
   @SerializedName("label")
   @Nullable
   private final String label;
   public static int $stable;

   static {}

   public Space(@NotNull Space$Type var1, @Nullable String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "type");
      this.type = var1;
      this.label = var2;
   }

   // $VF: synthetic method
   public Space(Space$Type var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (Space$Type) (Space$Type.HOME) : var1), (((var3 & 2) != 0) ? (String) (null) : var2));
   }

   @NotNull
   public final Space$Type getType() {
      return this.type;
   }

   @Nullable
   public final String getLabel() {
      return this.label;
   }

   @NotNull
   public final Space$Type component1() {
      return this.type;
   }

   @Nullable
   public final String component2() {
      return this.label;
   }

   @NotNull
   public final Space copy(@NotNull Space$Type var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var1, "type");
      return new Space(var1, var2);
   }

   // $VF: synthetic method
   public static Space copy$default(Space var0, Space$Type var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.type;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.label;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Space(type=" + this.type + ", label=" + this.label + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.type.hashCode();
      return var1 * 31 + (this.label == null ? 0 : this.label.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Space)) {
         return false;
      }

      Space var2 = (Space)var1;
      return this.type != var2.type ? false : Intrinsics.areEqual(this.label, var2.label);
   }

   public Space() {
      this(null, null, 3, null);
   }
}
