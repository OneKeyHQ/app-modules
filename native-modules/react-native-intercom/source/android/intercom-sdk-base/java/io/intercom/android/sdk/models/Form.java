package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010\u0013\u001a\u00020\bHÆ\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\b\u0010\u0015\u001a\u00020\u0016H\u0007J\u0013\u0010\u0017\u001a\u00020\b2\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019H×\u0003J\t\u0010\u001a\u001a\u00020\u0016H×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001J\u0018\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u0016H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/models/Form;",
         "Landroid/os/Parcelable;",
         "type",
         "",
         "attributes",
         "",
         "Lio/intercom/android/sdk/models/Attribute;",
         "disabled",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Z)V",
         "getType",
         "()Ljava/lang/String;",
         "getAttributes",
         "()Ljava/util/List;",
         "getDisabled",
         "()Z",
         "component1",
         "component2",
         "component3",
         "copy",
         "describeContents",
         "",
         "equals",
         "other",
         "",
         "hashCode",
         "toString",
         "writeToParcel",
         "",
         "dest",
         "Landroid/os/Parcel;",
         "flags",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
public final class Form implements Parcelable {
   @SerializedName("type")
   @NotNull
   private final String type;
   @SerializedName("attributes")
   @NotNull
   private final List attributes;
   @SerializedName("disabled")
   private final boolean disabled;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new Form$Creator();

   public Form(@NotNull String var1, @NotNull List var2, boolean var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "type");
      Intrinsics.checkNotNullParameter(var2, "attributes");
      this.type = var1;
      this.attributes = var2;
      this.disabled = var3;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   @NotNull
   public final List getAttributes() {
      return this.attributes;
   }

   public final boolean getDisabled() {
      return this.disabled;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.type);
      List var3 = this.attributes;
      var1.writeInt(var3.size());
      Iterator var4 = var3.iterator();

      while (var4.hasNext()) {
         ((Attribute)var4.next()).writeToParcel(var1, var2);
      }

      var1.writeInt(this.disabled ? 1 : 0);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.type;
   }

   @NotNull
   public final List component2() {
      return this.attributes;
   }

   public final boolean component3() {
      return this.disabled;
   }

   @NotNull
   public final Form copy(@NotNull String var1, @NotNull List var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "type");
      Intrinsics.checkNotNullParameter(var2, "attributes");
      return new Form(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Form copy$default(Form var0, String var1, List var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.type;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.attributes;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.disabled;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Form(type=" + this.type + ", attributes=" + this.attributes + ", disabled=" + this.disabled + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.type.hashCode();
      var1 = var1 * 31 + this.attributes.hashCode();
      return var1 * 31 + Boolean.hashCode(this.disabled);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Form)) {
         return false;
      } else {
         Form var2 = (Form)var1;
         if (!Intrinsics.areEqual(this.type, var2.type)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.attributes, var2.attributes) ? false : this.disabled == var2.disabled;
         }
      }
   }
}
