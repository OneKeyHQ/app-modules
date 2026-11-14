package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0014\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0005\u0012\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001b\u001a\u00020\bHÆ\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bHÆ\u0003JM\u0010\u001e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00052\u000e\b\u0002\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000bHÇ\u0001J\b\u0010\u001f\u001a\u00020 H\u0007J\u0013\u0010!\u001a\u00020\b2\b\u0010\"\u001a\u0004\u0018\u00010#H×\u0003J\t\u0010$\u001a\u00020 H×\u0001J\t\u0010%\u001a\u00020\u0005H×\u0001J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020 H\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/TicketAttribute;",
         "Landroid/os/Parcelable;",
         "id",
         "",
         "name",
         "",
         "type",
         "required",
         "",
         "identifier",
         "options",
         "",
         "<init>",
         "(JLjava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/util/List;)V",
         "getId",
         "()J",
         "getName",
         "()Ljava/lang/String;",
         "getType",
         "getRequired",
         "()Z",
         "getIdentifier",
         "getOptions",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
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
public final class TicketAttribute implements Parcelable {
   private final long id;
   @NotNull
   private final String name;
   @NotNull
   private final String type;
   private final boolean required;
   @Nullable
   private final String identifier;
   @NotNull
   private final List options;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new TicketAttribute$Creator();

   public TicketAttribute(long var1, @NotNull String var3, @NotNull String var4, boolean var5, @Nullable String var6, @NotNull List var7) {
      super();
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "type");
      Intrinsics.checkNotNullParameter(var7, "options");
      this.id = var1;
      this.name = var3;
      this.type = var4;
      this.required = var5;
      this.identifier = var6;
      this.options = var7;
   }

   // $VF: synthetic method
   public TicketAttribute(long var1, String var3, String var4, boolean var5, String var6, List var7, int var8, DefaultConstructorMarker var9) {
      this(var1, var3, var4, var5, var6, (((var8 & 32) != 0) ? (List) (CollectionsKt.emptyList()) : var7));
   }

   public final long getId() {
      return this.id;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   public final boolean getRequired() {
      return this.required;
   }

   @Nullable
   public final String getIdentifier() {
      return this.identifier;
   }

   @NotNull
   public final List getOptions() {
      return this.options;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeLong(this.id);
      var1.writeString(this.name);
      var1.writeString(this.type);
      var1.writeInt(this.required ? 1 : 0);
      var1.writeString(this.identifier);
      var1.writeStringList(this.options);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   public final long component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @NotNull
   public final String component3() {
      return this.type;
   }

   public final boolean component4() {
      return this.required;
   }

   @Nullable
   public final String component5() {
      return this.identifier;
   }

   @NotNull
   public final List component6() {
      return this.options;
   }

   @NotNull
   public final TicketAttribute copy(long var1, @NotNull String var3, @NotNull String var4, boolean var5, @Nullable String var6, @NotNull List var7) {
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "type");
      Intrinsics.checkNotNullParameter(var7, "options");
      return new TicketAttribute(var1, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static TicketAttribute copy$default(
      TicketAttribute var0, long var1, String var3, String var4, boolean var5, String var6, List var7, int var8, Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var8 & 2) != 0) {
         var3 = var0.name;
      }

      if ((var8 & 4) != 0) {
         var4 = var0.type;
      }

      if ((var8 & 8) != 0) {
         var5 = var0.required;
      }

      if ((var8 & 16) != 0) {
         var6 = var0.identifier;
      }

      if ((var8 & 32) != 0) {
         var7 = var0.options;
      }

      return var0.copy(var1, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketAttribute(id="
         + this.id
         + ", name="
         + this.name
         + ", type="
         + this.type
         + ", required="
         + this.required
         + ", identifier="
         + this.identifier
         + ", options="
         + this.options
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.id);
      var1 = var1 * 31 + this.name.hashCode();
      var1 = var1 * 31 + this.type.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.required);
      var1 = var1 * 31 + (this.identifier == null ? 0 : this.identifier.hashCode());
      return var1 * 31 + this.options.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketAttribute)) {
         return false;
      } else {
         TicketAttribute var2 = (TicketAttribute)var1;
         if (this.id != var2.id) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.type, var2.type)) {
            return false;
         } else if (this.required != var2.required) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.identifier, var2.identifier) ? false : Intrinsics.areEqual(this.options, var2.options);
         }
      }
   }
}
