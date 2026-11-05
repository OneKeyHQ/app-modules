package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\b\u0087\b\u0018\u00002\u00020\u0001:\u0004#$%&BC\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\u000f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\b0\u0005HÆ\u0003J\u000f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\n0\u0005HÆ\u0003J\t\u0010\u001b\u001a\u00020\fHÆ\u0003JO\u0010\u001c\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\b\b\u0002\u0010\u000b\u001a\u00020\fHÇ\u0001J\u0013\u0010\u001d\u001a\u00020\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010 \u001a\u00020!H×\u0001J\t\u0010\"\u001a\u00020\fH×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u001c\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0016\u0010\u000b\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/models/Header$Expanded;",
         "",
         "avatarDetails",
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "body",
         "",
         "Lio/intercom/android/sdk/models/Header$Expanded$Body;",
         "footer",
         "Lio/intercom/android/sdk/models/Header$Expanded$Footer;",
         "socialAccounts",
         "Lio/intercom/android/sdk/models/Header$Expanded$SocialAccount;",
         "title",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/AvatarDetails;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;)V",
         "getAvatarDetails",
         "()Lio/intercom/android/sdk/models/AvatarDetails;",
         "getBody",
         "()Ljava/util/List;",
         "getFooter",
         "getSocialAccounts",
         "getTitle",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "Body",
         "Footer",
         "SocialAccount",
         "Style",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Header$Expanded {
   @SerializedName("avatar_details")
   @Nullable
   private final AvatarDetails avatarDetails;
   @SerializedName("body")
   @NotNull
   private final List body;
   @SerializedName("footer")
   @NotNull
   private final List footer;
   @SerializedName("social_accounts")
   @NotNull
   private final List socialAccounts;
   @SerializedName("title")
   @NotNull
   private final String title;
   public static final int $stable;

   static { $stable = 8; }

   public Header$Expanded(@Nullable AvatarDetails var1, @NotNull List var2, @NotNull List var3, @NotNull List var4, @NotNull String var5) {
      super();
      Intrinsics.checkNotNullParameter(var2, "body");
      Intrinsics.checkNotNullParameter(var3, "footer");
      Intrinsics.checkNotNullParameter(var4, "socialAccounts");
      Intrinsics.checkNotNullParameter(var5, "title");
      this.avatarDetails = var1;
      this.body = var2;
      this.footer = var3;
      this.socialAccounts = var4;
      this.title = var5;
   }

   @Nullable
   public final AvatarDetails getAvatarDetails() {
      return this.avatarDetails;
   }

   @NotNull
   public final List getBody() {
      return this.body;
   }

   @NotNull
   public final List getFooter() {
      return this.footer;
   }

   @NotNull
   public final List getSocialAccounts() {
      return this.socialAccounts;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @Nullable
   public final AvatarDetails component1() {
      return this.avatarDetails;
   }

   @NotNull
   public final List component2() {
      return this.body;
   }

   @NotNull
   public final List component3() {
      return this.footer;
   }

   @NotNull
   public final List component4() {
      return this.socialAccounts;
   }

   @NotNull
   public final String component5() {
      return this.title;
   }

   @NotNull
   public final Header$Expanded copy(@Nullable AvatarDetails var1, @NotNull List var2, @NotNull List var3, @NotNull List var4, @NotNull String var5) {
      Intrinsics.checkNotNullParameter(var2, "body");
      Intrinsics.checkNotNullParameter(var3, "footer");
      Intrinsics.checkNotNullParameter(var4, "socialAccounts");
      Intrinsics.checkNotNullParameter(var5, "title");
      return new Header$Expanded(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static Header$Expanded copy$default(Header$Expanded var0, AvatarDetails var1, List var2, List var3, List var4, String var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.avatarDetails;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.body;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.footer;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.socialAccounts;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.title;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "Expanded(avatarDetails="
         + this.avatarDetails
         + ", body="
         + this.body
         + ", footer="
         + this.footer
         + ", socialAccounts="
         + this.socialAccounts
         + ", title="
         + this.title
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatarDetails == null ? 0 : this.avatarDetails.hashCode();
      var1 = var1 * 31 + this.body.hashCode();
      var1 = var1 * 31 + this.footer.hashCode();
      var1 = var1 * 31 + this.socialAccounts.hashCode();
      return var1 * 31 + this.title.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Header$Expanded)) {
         return false;
      } else {
         Header$Expanded var2 = (Header$Expanded)var1;
         if (!Intrinsics.areEqual(this.avatarDetails, var2.avatarDetails)) {
            return false;
         } else if (!Intrinsics.areEqual(this.body, var2.body)) {
            return false;
         } else if (!Intrinsics.areEqual(this.footer, var2.footer)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.socialAccounts, var2.socialAccounts) ? false : Intrinsics.areEqual(this.title, var2.title);
         }
      }
   }
}
