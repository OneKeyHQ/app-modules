package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B;\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0019\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0002\u0010\u0010J\u0011\u0010\u001b\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bHÂ\u0003JB\u0010\u001c\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bHÇ\u0001¢\u0006\u0002\u0010\u001dJ\b\u0010\u001e\u001a\u00020\u001fH\u0007J\u0013\u0010 \u001a\u00020!2\b\u0010\"\u001a\u0004\u0018\u00010#H×\u0003J\t\u0010$\u001a\u00020\u001fH×\u0001J\t\u0010%\u001a\u00020\u0003H×\u0001J\u0018\u0010&\u001a\u00020'2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\u001fH\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b8\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\b8F¢\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017¨\u0006+",
   d2 = {
         "Lio/intercom/android/sdk/models/Metadata;",
         "Landroid/os/Parcelable;",
         "title",
         "",
         "suffix",
         "timestamp",
         "",
         "_avatars",
         "",
         "Lio/intercom/android/sdk/models/Avatar$Builder;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/util/List;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getSuffix",
         "getTimestamp",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "get_avatars$annotations",
         "()V",
         "avatars",
         "Lio/intercom/android/sdk/models/Avatar;",
         "getAvatars",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Ljava/util/List;)Lio/intercom/android/sdk/models/Metadata;",
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
         "intercom-sdk-base_release"
   }
)
@Parcelize
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nMetadata.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Metadata.kt\nio/intercom/android/sdk/models/Metadata\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,24:1\n1563#2:25\n1634#2,3:26\n*S KotlinDebug\n*F\n+ 1 Metadata.kt\nio/intercom/android/sdk/models/Metadata\n*L\n22#1:25\n22#1:26,3\n*E\n"
)
public final class Metadata implements Parcelable {
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("suffix")
   @Nullable
   private final String suffix;
   @SerializedName("timestamp")
   @Nullable
   private final Long timestamp;
   @SerializedName("avatars")
   @Nullable
   private final List _avatars;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   public static final Creator CREATOR = new Metadata$Creator();

   public Metadata(@NotNull String var1, @Nullable String var2, @Nullable Long var3, @Nullable List var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      this.title = var1;
      this.suffix = var2;
      this.timestamp = var3;
      this._avatars = var4;
   }

   // $VF: synthetic method
   public Metadata(String var1, String var2, Long var3, List var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (String) ("") : var1), (((var5 & 2) != 0) ? (String) (null) : var2), (((var5 & 4) != 0) ? (Long) (null) : var3), (((var5 & 8) != 0) ? (List) (null) : var4));
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @Nullable
   public final String getSuffix() {
      return this.suffix;
   }

   @Nullable
   public final Long getTimestamp() {
      return this.timestamp;
   }

   /** @deprecated */
   // $VF: synthetic method
   private static void get_avatars$annotations() {
   }

   @NotNull
   public final List getAvatars() {
      List var10000;
      if (this._avatars != null) {
         Iterable var1 = this._avatars;
         int var2 = 0;
         Iterable var3 = var1;
         ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
         int var5 = 0;

         for (Object var7 : var3) {
            Avatar$Builder var8 = (Avatar$Builder)var7;
            Collection var10 = var4;
            int var9 = 0;
            var10.add(var8.build());
         }

         var10000 = (List)var4;
      } else {
         var10000 = CollectionsKt.emptyList();
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this.title);
      var1.writeString(this.suffix);
      Long var3 = this.timestamp;
      if (var3 == null) {
         var1.writeInt(0);
      } else {
         var1.writeInt(1);
         var1.writeLong(var3);
      }
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @Nullable
   public final String component2() {
      return this.suffix;
   }

   @Nullable
   public final Long component3() {
      return this.timestamp;
   }

   private final List component4() {
      return this._avatars;
   }

   @NotNull
   public final Metadata copy(@NotNull String var1, @Nullable String var2, @Nullable Long var3, @Nullable List var4) {
      Intrinsics.checkNotNullParameter(var1, "title");
      return new Metadata(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Metadata copy$default(Metadata var0, String var1, String var2, Long var3, List var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.suffix;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.timestamp;
      }

      if ((var5 & 8) != 0) {
         var4 = var0._avatars;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Metadata(title=" + this.title + ", suffix=" + this.suffix + ", timestamp=" + this.timestamp + ", _avatars=" + this._avatars + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + (this.suffix == null ? 0 : this.suffix.hashCode());
      var1 = var1 * 31 + (this.timestamp == null ? 0 : this.timestamp.hashCode());
      return var1 * 31 + (this._avatars == null ? 0 : this._avatars.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Metadata)) {
         return false;
      } else {
         Metadata var2 = (Metadata)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.suffix, var2.suffix)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.timestamp, var2.timestamp) ? false : Intrinsics.areEqual(this._avatars, var2._avatars);
         }
      }
   }

   public Metadata() {
      this(null, null, null, null, 15, null);
   }
}
