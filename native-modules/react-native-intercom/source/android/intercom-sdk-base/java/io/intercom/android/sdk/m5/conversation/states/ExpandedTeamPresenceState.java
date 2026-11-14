package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.AvatarType;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0018\n\u0002\u0010\b\n\u0002\b\u0003\b\u0081\b\u0018\u0000 +2\u00020\u0001:\u0001+BW\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005\u0012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005\u0012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0005\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0011\u0010\u0012J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010 \u001a\u00020\bHÆ\u0003J\u000f\u0010!\u001a\b\u0012\u0004\u0012\u00020\n0\u0005HÆ\u0003J\u000f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\f0\u0005HÆ\u0003J\u000f\u0010#\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0005HÆ\u0003J\t\u0010$\u001a\u00020\u0010HÆ\u0003Jg\u0010%\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00052\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00052\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00052\b\b\u0002\u0010\u000f\u001a\u00020\u0010HÇ\u0001J\u0013\u0010&\u001a\u00020\u00102\b\u0010'\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010(\u001a\u00020)H×\u0001J\t\u0010*\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0016R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d¨\u0006,",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;",
         "",
         "title",
         "",
         "body",
         "",
         "Lio/intercom/android/sdk/models/Header$Expanded$Body;",
         "avatarType",
         "Lio/intercom/android/sdk/models/AvatarType;",
         "avatars",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "footers",
         "Lio/intercom/android/sdk/models/Header$Expanded$Footer;",
         "socialAccounts",
         "Lio/intercom/android/sdk/models/Header$Expanded$SocialAccount;",
         "displayActiveIndicator",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Lio/intercom/android/sdk/models/AvatarType;Ljava/util/List;Ljava/util/List;Ljava/util/List;Z)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getBody",
         "()Ljava/util/List;",
         "getAvatarType",
         "()Lio/intercom/android/sdk/models/AvatarType;",
         "getAvatars",
         "getFooters",
         "getSocialAccounts",
         "getDisplayActiveIndicator",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ExpandedTeamPresenceState {
   @NotNull
   public static final ExpandedTeamPresenceState$Companion Companion = new ExpandedTeamPresenceState$Companion(null);
   @NotNull
   private final String title;
   @NotNull
   private final List body;
   @NotNull
   private final AvatarType avatarType;
   @NotNull
   private final List avatars;
   @NotNull
   private final List footers;
   @NotNull
   private final List socialAccounts;
   private final boolean displayActiveIndicator;
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final ExpandedTeamPresenceState recovered$af19866b8159d0b6 = new ExpandedTeamPresenceState(
      "", CollectionsKt.emptyList(), AvatarType.UNKNOWN, CollectionsKt.emptyList(), CollectionsKt.emptyList(), CollectionsKt.emptyList(), false
   );

   public ExpandedTeamPresenceState(
      @NotNull String var1, @NotNull List var2, @NotNull AvatarType var3, @NotNull List var4, @NotNull List var5, @NotNull List var6, boolean var7
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "body");
      Intrinsics.checkNotNullParameter(var3, "avatarType");
      Intrinsics.checkNotNullParameter(var4, "avatars");
      Intrinsics.checkNotNullParameter(var5, "footers");
      Intrinsics.checkNotNullParameter(var6, "socialAccounts");
      this.title = var1;
      this.body = var2;
      this.avatarType = var3;
      this.avatars = var4;
      this.footers = var5;
      this.socialAccounts = var6;
      this.displayActiveIndicator = var7;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final List getBody() {
      return this.body;
   }

   @NotNull
   public final AvatarType getAvatarType() {
      return this.avatarType;
   }

   @NotNull
   public final List getAvatars() {
      return this.avatars;
   }

   @NotNull
   public final List getFooters() {
      return this.footers;
   }

   @NotNull
   public final List getSocialAccounts() {
      return this.socialAccounts;
   }

   public final boolean getDisplayActiveIndicator() {
      return this.displayActiveIndicator;
   }

   @NotNull
   public final String component1() {
      return this.title;
   }

   @NotNull
   public final List component2() {
      return this.body;
   }

   @NotNull
   public final AvatarType component3() {
      return this.avatarType;
   }

   @NotNull
   public final List component4() {
      return this.avatars;
   }

   @NotNull
   public final List component5() {
      return this.footers;
   }

   @NotNull
   public final List component6() {
      return this.socialAccounts;
   }

   public final boolean component7() {
      return this.displayActiveIndicator;
   }

   @NotNull
   public final ExpandedTeamPresenceState copy(
      @NotNull String var1, @NotNull List var2, @NotNull AvatarType var3, @NotNull List var4, @NotNull List var5, @NotNull List var6, boolean var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "body");
      Intrinsics.checkNotNullParameter(var3, "avatarType");
      Intrinsics.checkNotNullParameter(var4, "avatars");
      Intrinsics.checkNotNullParameter(var5, "footers");
      Intrinsics.checkNotNullParameter(var6, "socialAccounts");
      return new ExpandedTeamPresenceState(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static ExpandedTeamPresenceState copy$default(
      ExpandedTeamPresenceState var0, String var1, List var2, AvatarType var3, List var4, List var5, List var6, boolean var7, int var8, Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.body;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.avatarType;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.avatars;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.footers;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.socialAccounts;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.displayActiveIndicator;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "ExpandedTeamPresenceState(title="
         + this.title
         + ", body="
         + this.body
         + ", avatarType="
         + this.avatarType
         + ", avatars="
         + this.avatars
         + ", footers="
         + this.footers
         + ", socialAccounts="
         + this.socialAccounts
         + ", displayActiveIndicator="
         + this.displayActiveIndicator
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      var1 = var1 * 31 + this.body.hashCode();
      var1 = var1 * 31 + this.avatarType.hashCode();
      var1 = var1 * 31 + this.avatars.hashCode();
      var1 = var1 * 31 + this.footers.hashCode();
      var1 = var1 * 31 + this.socialAccounts.hashCode();
      return var1 * 31 + Boolean.hashCode(this.displayActiveIndicator);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ExpandedTeamPresenceState)) {
         return false;
      } else {
         ExpandedTeamPresenceState var2 = (ExpandedTeamPresenceState)var1;
         if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.body, var2.body)) {
            return false;
         } else if (this.avatarType != var2.avatarType) {
            return false;
         } else if (!Intrinsics.areEqual(this.avatars, var2.avatars)) {
            return false;
         } else if (!Intrinsics.areEqual(this.footers, var2.footers)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.socialAccounts, var2.socialAccounts) ? false : this.displayActiveIndicator == var2.displayActiveIndicator;
         }
      }
   }

   // $VF: synthetic method
   public static final ExpandedTeamPresenceState access$getDefault$cp() {
      return recovered$af19866b8159d0b6;
   }
}
