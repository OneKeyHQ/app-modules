package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 ,2\u00020\u0001:\u0001,BM\b\u0007\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\t\u0010\u001c\u001a\u00020\bHÆ\u0003J\t\u0010\u001d\u001a\u00020\bHÆ\u0003JM\u0010\u001e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\bHÇ\u0001J\b\u0010\u001f\u001a\u00020 H\u0007J\u0013\u0010!\u001a\u00020\"2\b\u0010#\u001a\u0004\u0018\u00010$H×\u0003J\t\u0010%\u001a\u00020 H×\u0001J\t\u0010&\u001a\u00020\u0003H×\u0001J\u0018\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020 H\u0007R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0016\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u000f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0012\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\u0014\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\u0016\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0011¨\u0006-",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/BlockAttachment;",
         "Landroid/os/Parcelable;",
         "_name",
         "",
         "_url",
         "_contentType",
         "_humanFileSize",
         "size",
         "",
         "id",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JJ)V",
         "getSize",
         "()J",
         "getId",
         "name",
         "getName",
         "()Ljava/lang/String;",
         "url",
         "getUrl",
         "contentType",
         "getContentType",
         "humanFileSize",
         "getHumanFileSize",
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
         "",
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
@StabilityInferred(parameters = 1)
public final class BlockAttachment implements Parcelable {
   @NotNull
   public static final BlockAttachment$Companion Companion = new BlockAttachment$Companion(null);
   @SerializedName("name")
   @Nullable
   private final String _name;
   @SerializedName("url")
   @Nullable
   private final String _url;
   @SerializedName("contentType")
   @Nullable
   private final String _contentType;
   @SerializedName("humanFileSize")
   @Nullable
   private final String _humanFileSize;
   @SerializedName("size")
   private final long size;
   @SerializedName("id")
   private final long id;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new BlockAttachment$Creator();

   @JvmOverloads
   public BlockAttachment(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, long var5, long var7) {
      this._name = var1;
      this._url = var2;
      this._contentType = var3;
      this._humanFileSize = var4;
      this.size = var5;
      this.id = var7;
   }

   // $VF: synthetic method
   public BlockAttachment(String var1, String var2, String var3, String var4, long var5, long var7, int var9, DefaultConstructorMarker var10) {
      this((((var9 & 1) != 0) ? (String) (null) : var1), (((var9 & 2) != 0) ? (String) (null) : var2), (((var9 & 4) != 0) ? (String) (null) : var3), (((var9 & 8) != 0) ? (String) (null) : var4), (((var9 & 16) != 0) ? (long) (0L) : var5), (((var9 & 32) != 0) ? (long) (0L) : var7));
   }

   public final long getSize() {
      return this.size;
   }

   public final long getId() {
      return this.id;
   }

   @NotNull
   public final String getName() {
      String var10000 = this._name;
      if (this._name == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getUrl() {
      String var10000 = this._url;
      if (this._url == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getContentType() {
      String var10000 = this._contentType;
      if (this._contentType == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getHumanFileSize() {
      String var10000 = this._humanFileSize;
      if (this._humanFileSize == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this._name);
      var1.writeString(this._url);
      var1.writeString(this._contentType);
      var1.writeString(this._humanFileSize);
      var1.writeLong(this.size);
      var1.writeLong(this.id);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   private final String component1() {
      return this._name;
   }

   private final String component2() {
      return this._url;
   }

   private final String component3() {
      return this._contentType;
   }

   private final String component4() {
      return this._humanFileSize;
   }

   public final long component5() {
      return this.size;
   }

   public final long component6() {
      return this.id;
   }

   @NotNull
   public final BlockAttachment copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, long var5, long var7) {
      return new BlockAttachment(var1, var2, var3, var4, var5, var7);
   }

   // $VF: synthetic method
   public static BlockAttachment copy$default(
      BlockAttachment var0, String var1, String var2, String var3, String var4, long var5, long var7, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0._name;
      }

      if ((var9 & 2) != 0) {
         var2 = var0._url;
      }

      if ((var9 & 4) != 0) {
         var3 = var0._contentType;
      }

      if ((var9 & 8) != 0) {
         var4 = var0._humanFileSize;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.size;
      }

      if ((var9 & 32) != 0) {
         var7 = var0.id;
      }

      return var0.copy(var1, var2, var3, var4, var5, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "BlockAttachment(_name="
         + this._name
         + ", _url="
         + this._url
         + ", _contentType="
         + this._contentType
         + ", _humanFileSize="
         + this._humanFileSize
         + ", size="
         + this.size
         + ", id="
         + this.id
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._name == null ? 0 : this._name.hashCode();
      var1 = var1 * 31 + (this._url == null ? 0 : this._url.hashCode());
      var1 = var1 * 31 + (this._contentType == null ? 0 : this._contentType.hashCode());
      var1 = var1 * 31 + (this._humanFileSize == null ? 0 : this._humanFileSize.hashCode());
      var1 = var1 * 31 + Long.hashCode(this.size);
      return var1 * 31 + Long.hashCode(this.id);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BlockAttachment)) {
         return false;
      } else {
         BlockAttachment var2 = (BlockAttachment)var1;
         if (!Intrinsics.areEqual(this._name, var2._name)) {
            return false;
         } else if (!Intrinsics.areEqual(this._url, var2._url)) {
            return false;
         } else if (!Intrinsics.areEqual(this._contentType, var2._contentType)) {
            return false;
         } else if (!Intrinsics.areEqual(this._humanFileSize, var2._humanFileSize)) {
            return false;
         } else {
            return this.size != var2.size ? false : this.id == var2.id;
         }
      }
   }

   @JvmOverloads
   public BlockAttachment(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4, long var5) {
      this(var1, var2, var3, var4, var5, 0L, 32, null);
   }

   @JvmOverloads
   public BlockAttachment(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
      this(var1, var2, var3, var4, 0L, 0L, 48, null);
   }

   @JvmOverloads
   public BlockAttachment(@Nullable String var1, @Nullable String var2, @Nullable String var3) {
      this(var1, var2, var3, null, 0L, 0L, 56, null);
   }

   @JvmOverloads
   public BlockAttachment(@Nullable String var1, @Nullable String var2) {
      this(var1, var2, null, null, 0L, 0L, 60, null);
   }

   @JvmOverloads
   public BlockAttachment(@Nullable String var1) {
      this(var1, null, null, null, 0L, 0L, 62, null);
   }

   @JvmOverloads
   public BlockAttachment() {
      this(null, null, null, null, 0L, 0L, 63, null);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0, @NotNull String var1, @NotNull String var2, @NotNull String var3, long var4, long var6) {
      return Companion.create(var0, var1, var2, var3, var4, var6);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0, @NotNull String var1, @NotNull String var2, @NotNull String var3, long var4) {
      return Companion.create(var0, var1, var2, var3, var4);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0, @NotNull String var1, @NotNull String var2, @NotNull String var3) {
      return Companion.create(var0, var1, var2, var3);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0, @NotNull String var1, @NotNull String var2) {
      return Companion.create(var0, var1, var2);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0, @NotNull String var1) {
      return Companion.create(var0, var1);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create(@NotNull String var0) {
      return Companion.create(var0);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final BlockAttachment create() {
      return Companion.create();
   }
}
