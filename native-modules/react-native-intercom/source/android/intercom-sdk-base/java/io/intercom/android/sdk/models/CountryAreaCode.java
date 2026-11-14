package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0081\b\u0018\u0000 '2\u00020\u0001:\u0001'BI\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t¢\u0006\u0004\b\n\u0010\u000bJ\u000b\u0010\u001a\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0007HÂ\u0003¢\u0006\u0002\u0010\u001eJ\u0011\u0010\u001f\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tHÂ\u0003JP\u0010 \u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\tHÇ\u0001¢\u0006\u0002\u0010!J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010%\u001a\u00020\u0007H×\u0001J\t\u0010&\u001a\u00020\u0003H×\u0001R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0002X\u0083\u0004¢\u0006\u0004\n\u0002\u0010\fR\u0018\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\t8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\r\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\u0012\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u0014\u001a\u00020\u00078F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00030\t8F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/models/CountryAreaCode;",
         "",
         "_code",
         "",
         "_dialCode",
         "_emoji",
         "_priority",
         "",
         "_areaCodes",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/util/List;)V",
         "Ljava/lang/Integer;",
         "code",
         "getCode",
         "()Ljava/lang/String;",
         "dialCode",
         "getDialCode",
         "emoji",
         "getEmoji",
         "priority",
         "getPriority",
         "()I",
         "areaCodes",
         "getAreaCodes",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "component3",
         "component4",
         "()Ljava/lang/Integer;",
         "component5",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/util/List;)Lio/intercom/android/sdk/models/CountryAreaCode;",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class CountryAreaCode {
   @NotNull
   public static final CountryAreaCode$Companion Companion = new CountryAreaCode$Companion(null);
   @SerializedName("code")
   @Nullable
   private final String _code;
   @SerializedName("dialCode")
   @Nullable
   private final String _dialCode;
   @SerializedName("emoji")
   @Nullable
   private final String _emoji;
   @SerializedName("priority")
   @Nullable
   private final Integer _priority;
   @SerializedName("areaCodes")
   @Nullable
   private final List _areaCodes;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   public static final CountryAreaCode UNKNOWN = new CountryAreaCode(null, null, null, null, null, 31, null);

   public CountryAreaCode(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable Integer var4, @Nullable List var5) {
      this._code = var1;
      this._dialCode = var2;
      this._emoji = var3;
      this._priority = var4;
      this._areaCodes = var5;
   }

   // $VF: synthetic method
   public CountryAreaCode(String var1, String var2, String var3, Integer var4, List var5, int var6, DefaultConstructorMarker var7) {
      this((((var6 & 1) != 0) ? (String) (null) : var1), (((var6 & 2) != 0) ? (String) (null) : var2), (((var6 & 4) != 0) ? (String) (null) : var3), (((var6 & 8) != 0) ? (Integer) (null) : var4), (((var6 & 16) != 0) ? (List) (null) : var5));
   }

   @NotNull
   public final String getCode() {
      String var10000 = this._code;
      if (this._code == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getDialCode() {
      String var10000 = this._dialCode;
      if (this._dialCode == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getEmoji() {
      String var10000 = this._emoji;
      if (this._emoji == null) {
         var10000 = "\ud83c\udf0e";
      }

      return var10000;
   }

   public final int getPriority() {
      return this._priority != null ? this._priority : 0;
   }

   @NotNull
   public final List getAreaCodes() {
      if (this._areaCodes != null) {
         List var10000 = CollectionsKt.filterNotNull(this._areaCodes);
         if (var10000 != null) {
            return var10000;
         }
      }

      return CollectionsKt.emptyList();
   }

   private final String component1() {
      return this._code;
   }

   private final String component2() {
      return this._dialCode;
   }

   private final String component3() {
      return this._emoji;
   }

   private final Integer component4() {
      return this._priority;
   }

   private final List component5() {
      return this._areaCodes;
   }

   @NotNull
   public final CountryAreaCode copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable Integer var4, @Nullable List var5) {
      return new CountryAreaCode(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static CountryAreaCode copy$default(CountryAreaCode var0, String var1, String var2, String var3, Integer var4, List var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0._code;
      }

      if ((var6 & 2) != 0) {
         var2 = var0._dialCode;
      }

      if ((var6 & 4) != 0) {
         var3 = var0._emoji;
      }

      if ((var6 & 8) != 0) {
         var4 = var0._priority;
      }

      if ((var6 & 16) != 0) {
         var5 = var0._areaCodes;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "CountryAreaCode(_code="
         + this._code
         + ", _dialCode="
         + this._dialCode
         + ", _emoji="
         + this._emoji
         + ", _priority="
         + this._priority
         + ", _areaCodes="
         + this._areaCodes
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._code == null ? 0 : this._code.hashCode();
      var1 = var1 * 31 + (this._dialCode == null ? 0 : this._dialCode.hashCode());
      var1 = var1 * 31 + (this._emoji == null ? 0 : this._emoji.hashCode());
      var1 = var1 * 31 + (this._priority == null ? 0 : this._priority.hashCode());
      return var1 * 31 + (this._areaCodes == null ? 0 : this._areaCodes.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CountryAreaCode)) {
         return false;
      } else {
         CountryAreaCode var2 = (CountryAreaCode)var1;
         if (!Intrinsics.areEqual(this._code, var2._code)) {
            return false;
         } else if (!Intrinsics.areEqual(this._dialCode, var2._dialCode)) {
            return false;
         } else if (!Intrinsics.areEqual(this._emoji, var2._emoji)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this._priority, var2._priority) ? false : Intrinsics.areEqual(this._areaCodes, var2._areaCodes);
         }
      }
   }

   public CountryAreaCode() {
      this(null, null, null, null, null, 31, null);
   }
}
