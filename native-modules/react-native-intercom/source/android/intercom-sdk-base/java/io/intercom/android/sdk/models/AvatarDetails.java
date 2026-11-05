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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J#\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/models/AvatarDetails;",
         "",
         "avatarType",
         "Lio/intercom/android/sdk/models/AvatarType;",
         "avatars",
         "",
         "Lio/intercom/android/sdk/models/Avatar$Builder;",
         "<init>",
         "(Lio/intercom/android/sdk/models/AvatarType;Ljava/util/List;)V",
         "getAvatarType",
         "()Lio/intercom/android/sdk/models/AvatarType;",
         "getAvatars",
         "()Ljava/util/List;",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class AvatarDetails {
   @SerializedName("avatar_type")
   @NotNull
   private final AvatarType avatarType;
   @SerializedName("avatars")
   @NotNull
   private final List avatars;
   public static final int $stable;

   static { $stable = 8; }

   public AvatarDetails(@NotNull AvatarType var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "avatarType");
      Intrinsics.checkNotNullParameter(var2, "avatars");
      this.avatarType = var1;
      this.avatars = var2;
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
   public final AvatarType component1() {
      return this.avatarType;
   }

   @NotNull
   public final List component2() {
      return this.avatars;
   }

   @NotNull
   public final AvatarDetails copy(@NotNull AvatarType var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "avatarType");
      Intrinsics.checkNotNullParameter(var2, "avatars");
      return new AvatarDetails(var1, var2);
   }

   // $VF: synthetic method
   public static AvatarDetails copy$default(AvatarDetails var0, AvatarType var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.avatarType;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.avatars;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AvatarDetails(avatarType=" + this.avatarType + ", avatars=" + this.avatars + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.avatarType.hashCode();
      return var1 * 31 + this.avatars.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AvatarDetails)) {
         return false;
      }

      AvatarDetails var2 = (AvatarDetails)var1;
      return this.avatarType != var2.avatarType ? false : Intrinsics.areEqual(this.avatars, var2.avatars);
   }
}
