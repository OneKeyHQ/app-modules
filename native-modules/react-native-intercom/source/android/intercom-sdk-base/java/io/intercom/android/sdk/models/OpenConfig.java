package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B+\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\bHÆ\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u001bH×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/models/OpenConfig;",
         "",
         "openTo",
         "Lio/intercom/android/sdk/models/OpenToSpace;",
         "spaces",
         "",
         "Lio/intercom/android/sdk/models/Space;",
         "form",
         "Lio/intercom/android/sdk/models/OpenForm;",
         "<init>",
         "(Lio/intercom/android/sdk/models/OpenToSpace;Ljava/util/List;Lio/intercom/android/sdk/models/OpenForm;)V",
         "getOpenTo",
         "()Lio/intercom/android/sdk/models/OpenToSpace;",
         "getSpaces",
         "()Ljava/util/List;",
         "getForm",
         "()Lio/intercom/android/sdk/models/OpenForm;",
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
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class OpenConfig {
   @SerializedName("open_to")
   @NotNull
   private final OpenToSpace openTo;
   @SerializedName("spaces")
   @NotNull
   private final List spaces;
   @SerializedName("form")
   @NotNull
   private final OpenForm form;
   public static final int $stable;

   static { $stable = 8; }

   public OpenConfig(@NotNull OpenToSpace var1, @NotNull List var2, @NotNull OpenForm var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "openTo");
      Intrinsics.checkNotNullParameter(var2, "spaces");
      Intrinsics.checkNotNullParameter(var3, "form");
      this.openTo = var1;
      this.spaces = var2;
      this.form = var3;
   }

   // $VF: synthetic method
   public OpenConfig(OpenToSpace var1, List var2, OpenForm var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (OpenToSpace) (OpenToSpace.HOME) : var1), (((var4 & 2) != 0) ? (List) (CollectionsKt.listOf(new Space(null, null, 3, null))) : var2), (((var4 & 4) != 0) ? (OpenForm) (OpenForm.DEFAULT) : var3));
   }

   @NotNull
   public final OpenToSpace getOpenTo() {
      return this.openTo;
   }

   @NotNull
   public final List getSpaces() {
      return this.spaces;
   }

   @NotNull
   public final OpenForm getForm() {
      return this.form;
   }

   @NotNull
   public final OpenToSpace component1() {
      return this.openTo;
   }

   @NotNull
   public final List component2() {
      return this.spaces;
   }

   @NotNull
   public final OpenForm component3() {
      return this.form;
   }

   @NotNull
   public final OpenConfig copy(@NotNull OpenToSpace var1, @NotNull List var2, @NotNull OpenForm var3) {
      Intrinsics.checkNotNullParameter(var1, "openTo");
      Intrinsics.checkNotNullParameter(var2, "spaces");
      Intrinsics.checkNotNullParameter(var3, "form");
      return new OpenConfig(var1, var2, var3);
   }

   // $VF: synthetic method
   public static OpenConfig copy$default(OpenConfig var0, OpenToSpace var1, List var2, OpenForm var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.openTo;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.spaces;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.form;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "OpenConfig(openTo=" + this.openTo + ", spaces=" + this.spaces + ", form=" + this.form + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.openTo.hashCode();
      var1 = var1 * 31 + this.spaces.hashCode();
      return var1 * 31 + this.form.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof OpenConfig)) {
         return false;
      } else {
         OpenConfig var2 = (OpenConfig)var1;
         if (this.openTo != var2.openTo) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.spaces, var2.spaces) ? false : this.form == var2.form;
         }
      }
   }

   public OpenConfig() {
      this(null, null, null, 7, null);
   }
}
