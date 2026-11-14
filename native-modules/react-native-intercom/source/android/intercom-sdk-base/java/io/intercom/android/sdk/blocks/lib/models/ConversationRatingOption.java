package io.intercom.android.sdk.blocks.lib.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B)\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0005HÂ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0005HÂ\u0003J+\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005HÇ\u0001J\b\u0010\u0014\u001a\u00020\u0003H\u0007J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H×\u0003J\t\u0010\u0019\u001a\u00020\u0003H×\u0001J\t\u0010\u001a\u001a\u00020\u0005H×\u0001J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u0003H\u0007R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u000b\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\r¨\u0006 ",
   d2 = {
         "Lio/intercom/android/sdk/blocks/lib/models/ConversationRatingOption;",
         "Landroid/os/Parcelable;",
         "index",
         "",
         "_emoji",
         "",
         "_unicode",
         "<init>",
         "(ILjava/lang/String;Ljava/lang/String;)V",
         "getIndex",
         "()I",
         "emoji",
         "getEmoji",
         "()Ljava/lang/String;",
         "unicode",
         "getUnicode",
         "component1",
         "component2",
         "component3",
         "copy",
         "describeContents",
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
@StabilityInferred(parameters = 1)
public final class ConversationRatingOption implements Parcelable {
   @SerializedName("index")
   private final int index;
   @SerializedName("emoji")
   @Nullable
   private final String _emoji;
   @SerializedName("unicode")
   @Nullable
   private final String _unicode;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new ConversationRatingOption$Creator();

   public ConversationRatingOption(int var1, @Nullable String var2, @Nullable String var3) {
      this.index = var1;
      this._emoji = var2;
      this._unicode = var3;
   }

   // $VF: synthetic method
   public ConversationRatingOption(int var1, String var2, String var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (int) (-1) : var1), (((var4 & 2) != 0) ? (String) (null) : var2), (((var4 & 4) != 0) ? (String) (null) : var3));
   }

   public final int getIndex() {
      return this.index;
   }

   @NotNull
   public final String getEmoji() {
      String var10000 = this._emoji;
      if (this._emoji == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getUnicode() {
      String var10000 = this._unicode;
      if (this._unicode == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeInt(this.index);
      var1.writeString(this._emoji);
      var1.writeString(this._unicode);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   public final int component1() {
      return this.index;
   }

   private final String component2() {
      return this._emoji;
   }

   private final String component3() {
      return this._unicode;
   }

   @NotNull
   public final ConversationRatingOption copy(int var1, @Nullable String var2, @Nullable String var3) {
      return new ConversationRatingOption(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ConversationRatingOption copy$default(ConversationRatingOption var0, int var1, String var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.index;
      }

      if ((var4 & 2) != 0) {
         var2 = var0._emoji;
      }

      if ((var4 & 4) != 0) {
         var3 = var0._unicode;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ConversationRatingOption(index=" + this.index + ", _emoji=" + this._emoji + ", _unicode=" + this._unicode + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.index);
      var1 = var1 * 31 + (this._emoji == null ? 0 : this._emoji.hashCode());
      return var1 * 31 + (this._unicode == null ? 0 : this._unicode.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationRatingOption)) {
         return false;
      } else {
         ConversationRatingOption var2 = (ConversationRatingOption)var1;
         if (this.index != var2.index) {
            return false;
         } else {
            return !Intrinsics.areEqual(this._emoji, var2._emoji) ? false : Intrinsics.areEqual(this._unicode, var2._unicode);
         }
      }
   }

   public ConversationRatingOption() {
      this(0, null, null, 7, null);
   }
}
