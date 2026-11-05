package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0014\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 $2\u00020\u0001:\u0001$B7\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0007\u0010\bJ\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0003HÂ\u0003J9\u0010\u0016\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\b\u0010\u0017\u001a\u00020\u0018H\u0007J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH×\u0003J\t\u0010\u001d\u001a\u00020\u0018H×\u0001J\t\u0010\u001e\u001a\u00020\u0003H×\u0001J\u0018\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0018H\u0007R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\t\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u000e\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u000bR\u0011\u0010\u0010\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u000b¨\u0006%",
   d2 = {
         "Lio/intercom/android/sdk/models/Attachments;",
         "Landroid/os/Parcelable;",
         "_name",
         "",
         "_url",
         "_contentType",
         "_humanFileSize",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
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
public final class Attachments implements Parcelable {
   @NotNull
   public static final Attachments$Companion Companion = new Attachments$Companion(null);
   @SerializedName("name")
   @Nullable
   private final String _name;
   @SerializedName("url")
   @Nullable
   private final String _url;
   @SerializedName("content_type")
   @Nullable
   private final String _contentType;
   @SerializedName("human_file_size")
   @Nullable
   private final String _humanFileSize;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new Attachments$Creator();

   public Attachments(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
      this._name = var1;
      this._url = var2;
      this._contentType = var3;
      this._humanFileSize = var4;
   }

   // $VF: synthetic method
   public Attachments(String var1, String var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (String) (null) : var1), (((var5 & 2) != 0) ? (String) (null) : var2), (((var5 & 4) != 0) ? (String) (null) : var3), (((var5 & 8) != 0) ? (String) (null) : var4));
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

   @NotNull
   public final Attachments copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable String var4) {
      return new Attachments(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Attachments copy$default(Attachments var0, String var1, String var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0._name;
      }

      if ((var5 & 2) != 0) {
         var2 = var0._url;
      }

      if ((var5 & 4) != 0) {
         var3 = var0._contentType;
      }

      if ((var5 & 8) != 0) {
         var4 = var0._humanFileSize;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Attachments(_name="
         + this._name
         + ", _url="
         + this._url
         + ", _contentType="
         + this._contentType
         + ", _humanFileSize="
         + this._humanFileSize
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._name == null ? 0 : this._name.hashCode();
      var1 = var1 * 31 + (this._url == null ? 0 : this._url.hashCode());
      var1 = var1 * 31 + (this._contentType == null ? 0 : this._contentType.hashCode());
      return var1 * 31 + (this._humanFileSize == null ? 0 : this._humanFileSize.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Attachments)) {
         return false;
      } else {
         Attachments var2 = (Attachments)var1;
         if (!Intrinsics.areEqual(this._name, var2._name)) {
            return false;
         } else if (!Intrinsics.areEqual(this._url, var2._url)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this._contentType, var2._contentType) ? false : Intrinsics.areEqual(this._humanFileSize, var2._humanFileSize);
         }
      }
   }

   public Attachments() {
      this(null, null, null, null, 15, null);
   }

   @JvmStatic
   @NotNull
   public static final Attachments create(@NotNull String var0, @NotNull String var1, @NotNull String var2, @NotNull String var3) {
      return Companion.create(var0, var1, var2, var3);
   }
}
