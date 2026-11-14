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
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087\b\u0018\u00002\u00020\u0001:\u0001\u001dB-\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007¢\u0006\u0004\b\t\u0010\nJ\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0007HÆ\u0003J7\u0010\u0016\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001a\u001a\u00020\u001bH×\u0001J\t\u0010\u001c\u001a\u00020\u0007H×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0016\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/models/Header$Collapsed;",
         "",
         "avatarDetails",
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "icon",
         "Lio/intercom/android/sdk/models/Header$Collapsed$HeaderIconType;",
         "subtitle",
         "",
         "title",
         "<init>",
         "(Lio/intercom/android/sdk/models/AvatarDetails;Lio/intercom/android/sdk/models/Header$Collapsed$HeaderIconType;Ljava/lang/String;Ljava/lang/String;)V",
         "getAvatarDetails",
         "()Lio/intercom/android/sdk/models/AvatarDetails;",
         "getIcon",
         "()Lio/intercom/android/sdk/models/Header$Collapsed$HeaderIconType;",
         "getSubtitle",
         "()Ljava/lang/String;",
         "getTitle",
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
         "HeaderIconType",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Header$Collapsed {
   @SerializedName("avatar_details")
   @Nullable
   private final AvatarDetails avatarDetails;
   @SerializedName("icon")
   @Nullable
   private final Header$Collapsed$HeaderIconType icon;
   @SerializedName("subtitle")
   @Nullable
   private final String subtitle;
   @SerializedName("title")
   @NotNull
   private final String title;
   public static final int $stable;

   static { $stable = 8; }

   public Header$Collapsed(@Nullable AvatarDetails var1, @Nullable Header$Collapsed$HeaderIconType var2, @Nullable String var3, @NotNull String var4) {
      super();
      Intrinsics.checkNotNullParameter(var4, "title");
      this.avatarDetails = var1;
      this.icon = var2;
      this.subtitle = var3;
      this.title = var4;
   }

   @Nullable
   public final AvatarDetails getAvatarDetails() {
      return this.avatarDetails;
   }

   @Nullable
   public final Header$Collapsed$HeaderIconType getIcon() {
      return this.icon;
   }

   @Nullable
   public final String getSubtitle() {
      return this.subtitle;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @Nullable
   public final AvatarDetails component1() {
      return this.avatarDetails;
   }

   @Nullable
   public final Header$Collapsed$HeaderIconType component2() {
      return this.icon;
   }

   @Nullable
   public final String component3() {
      return this.subtitle;
   }

   @NotNull
   public final String component4() {
      return this.title;
   }

   @NotNull
   public final Header$Collapsed copy(@Nullable AvatarDetails var1, @Nullable Header$Collapsed$HeaderIconType var2, @Nullable String var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var4, "title");
      return new Header$Collapsed(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static Header$Collapsed copy$default(
      Header$Collapsed var0, AvatarDetails var1, Header$Collapsed$HeaderIconType var2, String var3, String var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.avatarDetails;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.icon;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.subtitle;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.title;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Collapsed(avatarDetails=" + this.avatarDetails + ", icon=" + this.icon + ", subtitle=" + this.subtitle + ", title=" + this.title + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatarDetails == null ? 0 : this.avatarDetails.hashCode();
      var1 = var1 * 31 + (this.icon == null ? 0 : this.icon.hashCode());
      var1 = var1 * 31 + (this.subtitle == null ? 0 : this.subtitle.hashCode());
      return var1 * 31 + this.title.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Header$Collapsed)) {
         return false;
      } else {
         Header$Collapsed var2 = (Header$Collapsed)var1;
         if (!Intrinsics.areEqual(this.avatarDetails, var2.avatarDetails)) {
            return false;
         } else if (this.icon != var2.icon) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.subtitle, var2.subtitle) ? false : Intrinsics.areEqual(this.title, var2.title);
         }
      }
   }
}
