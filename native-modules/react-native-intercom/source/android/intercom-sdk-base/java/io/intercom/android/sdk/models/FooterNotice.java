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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0006HÆ\u0003J+\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/models/FooterNotice;",
         "",
         "title",
         "",
         "subtitle",
         "avatarDetails",
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/AvatarDetails;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getSubtitle",
         "getAvatarDetails",
         "()Lio/intercom/android/sdk/models/AvatarDetails;",
         "component1",
         "component2",
         "component3",
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
@StabilityInferred(parameters = 0)
public final class FooterNotice {
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("subtitle")
   @Nullable
   private final String subtitle;
   @SerializedName("avatar_details")
   @Nullable
   private final AvatarDetails avatarDetails;
   public static final int $stable;

   static { $stable = 8; }

   public FooterNotice(@NotNull String var1, @Nullable String var2, @Nullable AvatarDetails var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      this.title = var1;
      this.subtitle = var2;
      this.avatarDetails = var3;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @Nullable
   public final String getSubtitle() {
      return this.subtitle;
   }

   @Nullable
   public final AvatarDetails getAvatarDetails() {
      return this.avatarDetails;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @Nullable
   public final String component2() {
      return this.subtitle;
   }

   @Nullable
   public final AvatarDetails component3() {
      return this.avatarDetails;
   }

   @NotNull
   public final FooterNotice copy(@NotNull String var1, @Nullable String var2, @Nullable AvatarDetails var3) {
      Intrinsics.checkNotNullParameter(var1, "title");
      return new FooterNotice(var1, var2, var3);
   }

   // $VF: synthetic method
   public static FooterNotice copy$default(FooterNotice var0, String var1, String var2, AvatarDetails var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.subtitle;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.avatarDetails;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "FooterNotice(title=" + this.title + ", subtitle=" + this.subtitle + ", avatarDetails=" + this.avatarDetails + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + (this.subtitle == null ? 0 : this.subtitle.hashCode());
      return var1 * 31 + (this.avatarDetails == null ? 0 : this.avatarDetails.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FooterNotice)) {
         return false;
      } else {
         FooterNotice var2 = (FooterNotice)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.subtitle, var2.subtitle) ? false : Intrinsics.areEqual(this.avatarDetails, var2.avatarDetails);
         }
      }
   }
}
