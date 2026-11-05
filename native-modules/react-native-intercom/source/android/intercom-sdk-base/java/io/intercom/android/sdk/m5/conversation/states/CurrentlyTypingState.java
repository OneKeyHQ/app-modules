package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B-\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J1\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00052\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\u001eH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "",
         "avatarWrapper",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "showAvatar",
         "",
         "description",
         "Lio/intercom/android/sdk/ui/common/StringProvider;",
         "userType",
         "Lio/intercom/android/sdk/m5/conversation/states/TypingIndicatorType;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;ZLio/intercom/android/sdk/ui/common/StringProvider;Lio/intercom/android/sdk/m5/conversation/states/TypingIndicatorType;)V",
         "getAvatarWrapper",
         "()Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "getShowAvatar",
         "()Z",
         "getDescription",
         "()Lio/intercom/android/sdk/ui/common/StringProvider;",
         "getUserType",
         "()Lio/intercom/android/sdk/m5/conversation/states/TypingIndicatorType;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class CurrentlyTypingState {
   @NotNull
   private final AvatarWrapper avatarWrapper;
   private final boolean showAvatar;
   @NotNull
   private final StringProvider description;
   @NotNull
   private final TypingIndicatorType userType;
   public static final int $stable;

   static { $stable = 8; }

   public CurrentlyTypingState(@NotNull AvatarWrapper var1, boolean var2, @NotNull StringProvider var3, @NotNull TypingIndicatorType var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "avatarWrapper");
      Intrinsics.checkNotNullParameter(var3, "description");
      Intrinsics.checkNotNullParameter(var4, "userType");
      this.avatarWrapper = var1;
      this.showAvatar = var2;
      this.description = var3;
      this.userType = var4;
   }

   // $VF: synthetic method
   public CurrentlyTypingState(AvatarWrapper var1, boolean var2, StringProvider var3, TypingIndicatorType var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (AvatarWrapper) (AvatarWrapper.Companion.getNULL()) : var1), (((var5 & 2) != 0) ? (boolean) (true) : var2), (((var5 & 4) != 0) ? (StringProvider) (new StringRes(string.intercom_thinking, null, 2, null)) : var3), var4);
   }

   @NotNull
   public final AvatarWrapper getAvatarWrapper() {
      return this.avatarWrapper;
   }

   public final boolean getShowAvatar() {
      return this.showAvatar;
   }

   @NotNull
   public final StringProvider getDescription() {
      return this.description;
   }

   @NotNull
   public final TypingIndicatorType getUserType() {
      return this.userType;
   }

   @NotNull
   public final AvatarWrapper component1() {
      return this.avatarWrapper;
   }

   public final boolean component2() {
      return this.showAvatar;
   }

   @NotNull
   public final StringProvider component3() {
      return this.description;
   }

   @NotNull
   public final TypingIndicatorType component4() {
      return this.userType;
   }

   @NotNull
   public final CurrentlyTypingState copy(@NotNull AvatarWrapper var1, boolean var2, @NotNull StringProvider var3, @NotNull TypingIndicatorType var4) {
      Intrinsics.checkNotNullParameter(var1, "avatarWrapper");
      Intrinsics.checkNotNullParameter(var3, "description");
      Intrinsics.checkNotNullParameter(var4, "userType");
      return new CurrentlyTypingState(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static CurrentlyTypingState copy$default(
      CurrentlyTypingState var0, AvatarWrapper var1, boolean var2, StringProvider var3, TypingIndicatorType var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.avatarWrapper;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.showAvatar;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.description;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.userType;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "CurrentlyTypingState(avatarWrapper="
         + this.avatarWrapper
         + ", showAvatar="
         + this.showAvatar
         + ", description="
         + this.description
         + ", userType="
         + this.userType
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatarWrapper.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.showAvatar);
      var1 = var1 * 31 + this.description.hashCode();
      return var1 * 31 + this.userType.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CurrentlyTypingState)) {
         return false;
      } else {
         CurrentlyTypingState var2 = (CurrentlyTypingState)var1;
         if (!Intrinsics.areEqual(this.avatarWrapper, var2.avatarWrapper)) {
            return false;
         } else if (this.showAvatar != var2.showAvatar) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.description, var2.description) ? false : this.userType == var2.userType;
         }
      }
   }
}
