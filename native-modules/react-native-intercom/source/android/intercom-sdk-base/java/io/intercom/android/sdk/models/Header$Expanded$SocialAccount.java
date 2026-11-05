package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J1\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\nR\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\n¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/models/Header$Expanded$SocialAccount;",
         "",
         "provider",
         "",
         "profileUrl",
         "imageUrl",
         "username",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getProvider",
         "()Ljava/lang/String;",
         "getProfileUrl",
         "getImageUrl",
         "getUsername",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class Header$Expanded$SocialAccount {
   @SerializedName("provider")
   @NotNull
   private final String provider;
   @SerializedName("profile_url")
   @NotNull
   private final String profileUrl;
   @SerializedName("image_url")
   @NotNull
   private final String imageUrl;
   @SerializedName("username")
   @NotNull
   private final String username;
   public static int $stable;

   static {}

   public Header$Expanded$SocialAccount(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "provider");
      Intrinsics.checkNotNullParameter(var2, "profileUrl");
      Intrinsics.checkNotNullParameter(var3, "imageUrl");
      Intrinsics.checkNotNullParameter(var4, "username");
      this.provider = var1;
      this.profileUrl = var2;
      this.imageUrl = var3;
      this.username = var4;
   }

   @NotNull
   public final String getProvider() {
      return this.provider;
   }

   @NotNull
   public final String getProfileUrl() {
      return this.profileUrl;
   }

   @NotNull
   public final String getImageUrl() {
      return this.imageUrl;
   }

   @NotNull
   public final String getUsername() {
      return this.username;
   }

   @NotNull
   public final String component1() {
      return this.provider;
   }

   @NotNull
   public final String component2() {
      return this.profileUrl;
   }

   @NotNull
   public final String component3() {
      return this.imageUrl;
   }

   @NotNull
   public final String component4() {
      return this.username;
   }

   @NotNull
   public final Header$Expanded$SocialAccount copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "provider");
      Intrinsics.checkNotNullParameter(var2, "profileUrl");
      Intrinsics.checkNotNullParameter(var3, "imageUrl");
      Intrinsics.checkNotNullParameter(var4, "username");
      return new Header$Expanded$SocialAccount(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Header$Expanded$SocialAccount copy$default(
      Header$Expanded$SocialAccount var0, String var1, String var2, String var3, String var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.provider;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.profileUrl;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.imageUrl;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.username;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "SocialAccount(provider="
         + this.provider
         + ", profileUrl="
         + this.profileUrl
         + ", imageUrl="
         + this.imageUrl
         + ", username="
         + this.username
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.provider.hashCode();
      var1 = var1 * 31 + this.profileUrl.hashCode();
      var1 = var1 * 31 + this.imageUrl.hashCode();
      return var1 * 31 + this.username.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Header$Expanded$SocialAccount)) {
         return false;
      } else {
         Header$Expanded$SocialAccount var2 = (Header$Expanded$SocialAccount)var1;
         if (!Intrinsics.areEqual(this.provider, var2.provider)) {
            return false;
         } else if (!Intrinsics.areEqual(this.profileUrl, var2.profileUrl)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.imageUrl, var2.imageUrl) ? false : Intrinsics.areEqual(this.username, var2.username);
         }
      }
   }
}
