package io.intercom.android.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.parcelize.Parcelize;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0081\b\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\u001f\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000b\u0010\f\u001a\u0004\u0018\u00010\u0003HÂ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0003HÂ\u0003J!\u0010\u000e\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\b\u0010\u000f\u001a\u00020\u0010H\u0007J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H×\u0003J\t\u0010\u0015\u001a\u00020\u0010H×\u0001J\t\u0010\u0016\u001a\u00020\u0003H×\u0001J\u0018\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0010H\u0007R\u0012\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\t¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/models/SocialAccount;",
         "Landroid/os/Parcelable;",
         "_provider",
         "",
         "_profileUrl",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "provider",
         "getProvider",
         "()Ljava/lang/String;",
         "profileUrl",
         "getProfileUrl",
         "component1",
         "component2",
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
public final class SocialAccount implements Parcelable {
   @NotNull
   public static final SocialAccount$Companion Companion = new SocialAccount$Companion(null);
   @SerializedName("provider")
   @Nullable
   private final String _provider;
   @SerializedName("profile_url")
   @Nullable
   private final String _profileUrl;
   public static int $stable;

   static {}
   @NotNull
   public static final Creator CREATOR = new SocialAccount$Creator();
   @JvmField
   @NotNull
   public static final SocialAccount NULL = new SocialAccount(null, null, 3, null);

   public SocialAccount(@Nullable String var1, @Nullable String var2) {
      this._provider = var1;
      this._profileUrl = var2;
   }

   // $VF: synthetic method
   public SocialAccount(String var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) (null) : var1), (((var3 & 2) != 0) ? (String) (null) : var2));
   }

   @NotNull
   public final String getProvider() {
      String var10000 = this._provider;
      if (this._provider == null) {
         var10000 = "";
      }

      return var10000;
   }

   @NotNull
   public final String getProfileUrl() {
      String var10000 = this._profileUrl;
      if (this._profileUrl == null) {
         var10000 = "";
      }

      return var10000;
   }

   @Override
   public final void writeToParcel(@NotNull Parcel var1, int var2) {
      Intrinsics.checkNotNullParameter(var1, "dest");
      var1.writeString(this._provider);
      var1.writeString(this._profileUrl);
   }

   @Override
   public final int describeContents() {
      return 0;
   }

   private final String component1() {
      return this._provider;
   }

   private final String component2() {
      return this._profileUrl;
   }

   @NotNull
   public final SocialAccount copy(@Nullable String var1, @Nullable String var2) {
      return new SocialAccount(var1, var2);
   }

   // $VF: synthetic method
   public static SocialAccount copy$default(SocialAccount var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0._provider;
      }

      if ((var3 & 2) != 0) {
         var2 = var0._profileUrl;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "SocialAccount(_provider=" + this._provider + ", _profileUrl=" + this._profileUrl + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this._provider == null ? 0 : this._provider.hashCode();
      return var1 * 31 + (this._profileUrl == null ? 0 : this._profileUrl.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof SocialAccount)) {
         return false;
      }

      SocialAccount var2 = (SocialAccount)var1;
      return !Intrinsics.areEqual(this._provider, var2._provider) ? false : Intrinsics.areEqual(this._profileUrl, var2._profileUrl);
   }

   public SocialAccount() {
      this(null, null, 3, null);
   }
}
