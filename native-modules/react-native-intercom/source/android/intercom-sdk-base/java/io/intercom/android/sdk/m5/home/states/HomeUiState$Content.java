package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.PoweredBy;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001:\u0001)BG\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fJ\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\u000f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\b0\u0005HÆ\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\bHÆ\u0003J\t\u0010\u001f\u001a\u00020\u000bHÆ\u0003J\t\u0010 \u001a\u00020\rHÆ\u0003JU\u0010!\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rHÇ\u0001J\u0013\u0010\"\u001a\u00020\u000b2\b\u0010#\u001a\u0004\u0018\u00010$H×\u0003J\t\u0010%\u001a\u00020&H×\u0001J\t\u0010'\u001a\u00020(H×\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0013\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\f\u001a\u00020\r¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "cards",
         "",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards;",
         "adminsAvatars",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "botAvatar",
         "teammateAccessEnabled",
         "",
         "header",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;",
         "<init>",
         "(Lio/intercom/android/sdk/models/PoweredBy;Ljava/util/List;Ljava/util/List;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;ZLio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;)V",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "getCards",
         "()Ljava/util/List;",
         "getAdminsAvatars",
         "getBotAvatar",
         "()Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "getTeammateAccessEnabled",
         "()Z",
         "getHeader",
         "()Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "ContentHeader",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeUiState$Content extends HomeUiState {
   @Nullable
   private final PoweredBy poweredBy;
   @NotNull
   private final List cards;
   @NotNull
   private final List adminsAvatars;
   @Nullable
   private final AvatarWrapper botAvatar;
   private final boolean teammateAccessEnabled;
   @NotNull
   private final HomeUiState$Content$ContentHeader header;
   public static final int $stable;

   static { $stable = 8; }

   public HomeUiState$Content(
      @Nullable PoweredBy var1,
      @NotNull List var2,
      @NotNull List var3,
      @Nullable AvatarWrapper var4,
      boolean var5,
      @NotNull HomeUiState$Content$ContentHeader var6
   ) {
      Intrinsics.checkNotNullParameter(var2, "cards");
      Intrinsics.checkNotNullParameter(var3, "adminsAvatars");
      Intrinsics.checkNotNullParameter(var6, "header");
      super(null);
      this.poweredBy = var1;
      this.cards = var2;
      this.adminsAvatars = var3;
      this.botAvatar = var4;
      this.teammateAccessEnabled = var5;
      this.header = var6;
   }

   @Nullable
   @Override
   public PoweredBy getPoweredBy() {
      return this.poweredBy;
   }

   @NotNull
   public final List getCards() {
      return this.cards;
   }

   @NotNull
   public final List getAdminsAvatars() {
      return this.adminsAvatars;
   }

   @Nullable
   public final AvatarWrapper getBotAvatar() {
      return this.botAvatar;
   }

   public final boolean getTeammateAccessEnabled() {
      return this.teammateAccessEnabled;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader getHeader() {
      return this.header;
   }

   @Nullable
   public final PoweredBy component1() {
      return this.poweredBy;
   }

   @NotNull
   public final List component2() {
      return this.cards;
   }

   @NotNull
   public final List component3() {
      return this.adminsAvatars;
   }

   @Nullable
   public final AvatarWrapper component4() {
      return this.botAvatar;
   }

   public final boolean component5() {
      return this.teammateAccessEnabled;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader component6() {
      return this.header;
   }

   @NotNull
   public final HomeUiState$Content copy(
      @Nullable PoweredBy var1,
      @NotNull List var2,
      @NotNull List var3,
      @Nullable AvatarWrapper var4,
      boolean var5,
      @NotNull HomeUiState$Content$ContentHeader var6
   ) {
      Intrinsics.checkNotNullParameter(var2, "cards");
      Intrinsics.checkNotNullParameter(var3, "adminsAvatars");
      Intrinsics.checkNotNullParameter(var6, "header");
      return new HomeUiState$Content(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static HomeUiState$Content copy$default(
      HomeUiState$Content var0,
      PoweredBy var1,
      List var2,
      List var3,
      AvatarWrapper var4,
      boolean var5,
      HomeUiState$Content$ContentHeader var6,
      int var7,
      Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.poweredBy;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.cards;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.adminsAvatars;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.botAvatar;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.teammateAccessEnabled;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.header;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(poweredBy="
         + this.poweredBy
         + ", cards="
         + this.cards
         + ", adminsAvatars="
         + this.adminsAvatars
         + ", botAvatar="
         + this.botAvatar
         + ", teammateAccessEnabled="
         + this.teammateAccessEnabled
         + ", header="
         + this.header
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.poweredBy == null ? 0 : this.poweredBy.hashCode();
      var1 = var1 * 31 + this.cards.hashCode();
      var1 = var1 * 31 + this.adminsAvatars.hashCode();
      var1 = var1 * 31 + (this.botAvatar == null ? 0 : this.botAvatar.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.teammateAccessEnabled);
      return var1 * 31 + this.header.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Content)) {
         return false;
      } else {
         HomeUiState$Content var2 = (HomeUiState$Content)var1;
         if (!Intrinsics.areEqual(this.poweredBy, var2.poweredBy)) {
            return false;
         } else if (!Intrinsics.areEqual(this.cards, var2.cards)) {
            return false;
         } else if (!Intrinsics.areEqual(this.adminsAvatars, var2.adminsAvatars)) {
            return false;
         } else if (!Intrinsics.areEqual(this.botAvatar, var2.botAvatar)) {
            return false;
         } else {
            return this.teammateAccessEnabled != var2.teammateAccessEnabled ? false : Intrinsics.areEqual(this.header, var2.header);
         }
      }
   }
}
