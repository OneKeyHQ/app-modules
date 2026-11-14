package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 )2\u00020\u0001:\u0001)B5\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\t0\bHÆ\u0003J\t\u0010\u001b\u001a\u00020\u000bHÆ\u0003JA\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\b\b\u0002\u0010\n\u001a\u00020\u000bHÇ\u0001J\b\u0010\u001d\u001a\u00020\u001eH\u0007J\u0013\u0010\u001f\u001a\u00020\u000b2\b\u0010 \u001a\u0004\u0018\u00010!H×\u0003J\t\u0010\"\u001a\u00020\u001eH×\u0001J\t\u0010#\u001a\u00020\u0005H×\u0001J\u0018\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020\u001eH\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0016\u0010\u0006\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "Landroid/os/Parcelable;",
         "id",
         "",
         "name",
         "",
         "emoji",
         "attributes",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketAttribute;",
         "archived",
         "",
         "<init>",
         "(JLjava/lang/String;Ljava/lang/String;Ljava/util/List;Z)V",
         "getId",
         "()J",
         "getName",
         "()Ljava/lang/String;",
         "getEmoji",
         "getAttributes",
         "()Ljava/util/List;",
         "getArchived",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
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
         "Companion",
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
public final class TicketType implements Parcelable {
   @NotNull
   public static final TicketType$Companion Companion = new TicketType$Companion(null);
   @SerializedName("id")
   private final long id;
   @SerializedName("name")
   @NotNull
   private final String name;
   @SerializedName("emoji")
   @NotNull
   private final String emoji;
   @SerializedName("attributes")
   @NotNull
   private final List attributes;
   @SerializedName("archived")
   private final boolean archived;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new TicketType$Creator();
   @NotNull
   private static final TicketType NULL = new TicketType(-1L, "", "", CollectionsKt.emptyList(), false);

   public TicketType(long var1, @NotNull String var3, @NotNull String var4, @NotNull List var5, boolean var6) {
      super();
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "emoji");
      Intrinsics.checkNotNullParameter(var5, "attributes");
      this.id = var1;
      this.name = var3;
      this.emoji = var4;
      this.attributes = var5;
      this.archived = var6;
   }

   public final long getId() {
      return this.id;
   }

   @NotNull
   public final String getName() {
      return this.name;
   }

   @NotNull
   public final String getEmoji() {
      return this.emoji;
   }

   @NotNull
   public final List getAttributes() {
      return this.attributes;
   }

   public final boolean getArchived() {
      return this.archived;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeLong(this.id);
      var1.writeString(this.name);
      var1.writeString(this.emoji);
      List var3 = this.attributes;
      var1.writeInt(var3.size());
      Iterator var4 = var3.iterator();

      while (var4.hasNext()) {
         ((TicketAttribute)var4.next()).writeToParcel(var1, var2);
      }

      var1.writeInt(this.archived ? 1 : 0);
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
      return this.emoji;
   }

   @NotNull
   public final List component4() {
      return this.attributes;
   }

   public final boolean component5() {
      return this.archived;
   }

   @NotNull
   public final TicketType copy(long var1, @NotNull String var3, @NotNull String var4, @NotNull List var5, boolean var6) {
      Intrinsics.checkNotNullParameter(var3, "name");
      Intrinsics.checkNotNullParameter(var4, "emoji");
      Intrinsics.checkNotNullParameter(var5, "attributes");
      return new TicketType(var1, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static TicketType copy$default(TicketType var0, long var1, String var3, String var4, List var5, boolean var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var7 & 2) != 0) {
         var3 = var0.name;
      }

      if ((var7 & 4) != 0) {
         var4 = var0.emoji;
      }

      if ((var7 & 8) != 0) {
         var5 = var0.attributes;
      }

      if ((var7 & 16) != 0) {
         var6 = var0.archived;
      }

      return var0.copy(var1, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketType(id="
         + this.id
         + ", name="
         + this.name
         + ", emoji="
         + this.emoji
         + ", attributes="
         + this.attributes
         + ", archived="
         + this.archived
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Long.hashCode(this.id);
      var1 = var1 * 31 + this.name.hashCode();
      var1 = var1 * 31 + this.emoji.hashCode();
      var1 = var1 * 31 + this.attributes.hashCode();
      return var1 * 31 + Boolean.hashCode(this.archived);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketType)) {
         return false;
      } else {
         TicketType var2 = (TicketType)var1;
         if (this.id != var2.id) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else if (!Intrinsics.areEqual(this.emoji, var2.emoji)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.attributes, var2.attributes) ? false : this.archived == var2.archived;
         }
      }
   }

   // $VF: synthetic method
   public static final TicketType access$getNULL$cp() {
      return NULL;
   }
}
