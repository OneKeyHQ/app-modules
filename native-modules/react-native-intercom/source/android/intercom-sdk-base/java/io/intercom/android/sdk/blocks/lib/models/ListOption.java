package io.intercom.android.sdk.blocks.lib.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00052\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0017\u001a\u00020\u0018H×\u0001J\t\u0010\u0019\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/ListOption;",
         "",
         "label",
         "",
         "archived",
         "",
         "id",
         "description",
         "<init>",
         "(Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;)V",
         "getLabel",
         "()Ljava/lang/String;",
         "getArchived",
         "()Z",
         "getId",
         "getDescription",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ListOption {
   @SerializedName("label")
   @NotNull
   private final String label;
   @SerializedName("archived")
   private final boolean archived;
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("description")
   @Nullable
   private final String description;
   public static int $stable;

   static {}

   public ListOption(@NotNull String var1, boolean var2, @NotNull String var3, @Nullable String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "label");
      Intrinsics.checkNotNullParameter(var3, "id");
      this.label = var1;
      this.archived = var2;
      this.id = var3;
      this.description = var4;
   }

   // $VF: synthetic method
   public ListOption(String var1, boolean var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (String) (null) : var4));
   }

   @NotNull
   public final String getLabel() {
      return this.label;
   }

   public final boolean getArchived() {
      return this.archived;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @Nullable
   public final String getDescription() {
      return this.description;
   }

   @NotNull
   public final String component1() {
      return this.label;
   }

   public final boolean component2() {
      return this.archived;
   }

   @NotNull
   public final String component3() {
      return this.id;
   }

   @Nullable
   public final String component4() {
      return this.description;
   }

   @NotNull
   public final ListOption copy(@NotNull String var1, boolean var2, @NotNull String var3, @Nullable String var4) {
      Intrinsics.checkNotNullParameter(var1, "label");
      Intrinsics.checkNotNullParameter(var3, "id");
      return new ListOption(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static ListOption copy$default(ListOption var0, String var1, boolean var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.label;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.archived;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.id;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.description;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "ListOption(label=" + this.label + ", archived=" + this.archived + ", id=" + this.id + ", description=" + this.description + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.label.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.archived);
      var1 = var1 * 31 + this.id.hashCode();
      return var1 * 31 + (this.description == null ? 0 : this.description.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ListOption)) {
         return false;
      } else {
         ListOption var2 = (ListOption)var1;
         if (!Intrinsics.areEqual(this.label, var2.label)) {
            return false;
         } else if (this.archived != var2.archived) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.id, var2.id) ? false : Intrinsics.areEqual(this.description, var2.description);
         }
      }
   }
}
