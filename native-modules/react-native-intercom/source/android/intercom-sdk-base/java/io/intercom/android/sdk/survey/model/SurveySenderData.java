package io.intercom.android.sdk.survey.model;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B7\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tJ\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0007HÆ\u0003J9\u0010\u0014\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0018\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveySenderData;",
         "",
         "firstName",
         "",
         "name",
         "initials",
         "avatar",
         "Lio/intercom/android/sdk/survey/model/SurveySenderAvatar;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/survey/model/SurveySenderAvatar;)V",
         "getFirstName",
         "()Ljava/lang/String;",
         "getName",
         "getInitials",
         "getAvatar",
         "()Lio/intercom/android/sdk/survey/model/SurveySenderAvatar;",
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
public final class SurveySenderData {
   @SerializedName("first_name")
   @Nullable
   private final String firstName;
   @SerializedName("name")
   @Nullable
   private final String name;
   @SerializedName("initial")
   @Nullable
   private final String initials;
   @SerializedName("avatar")
   @Nullable
   private final SurveySenderAvatar avatar;
   public static int $stable;

   static {}

   public SurveySenderData(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable SurveySenderAvatar var4) {
      this.firstName = var1;
      this.name = var2;
      this.initials = var3;
      this.avatar = var4;
   }

   // $VF: synthetic method
   public SurveySenderData(String var1, String var2, String var3, SurveySenderAvatar var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (String) (null) : var1), (((var5 & 2) != 0) ? (String) (null) : var2), (((var5 & 4) != 0) ? (String) (null) : var3), (((var5 & 8) != 0) ? (SurveySenderAvatar) (null) : var4));
   }

   @Nullable
   public final String getFirstName() {
      return this.firstName;
   }

   @Nullable
   public final String getName() {
      return this.name;
   }

   @Nullable
   public final String getInitials() {
      return this.initials;
   }

   @Nullable
   public final SurveySenderAvatar getAvatar() {
      return this.avatar;
   }

   @Nullable
   public final String component1() {
      return this.firstName;
   }

   @Nullable
   public final String component2() {
      return this.name;
   }

   @Nullable
   public final String component3() {
      return this.initials;
   }

   @Nullable
   public final SurveySenderAvatar component4() {
      return this.avatar;
   }

   @NotNull
   public final SurveySenderData copy(@Nullable String var1, @Nullable String var2, @Nullable String var3, @Nullable SurveySenderAvatar var4) {
      return new SurveySenderData(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static SurveySenderData copy$default(SurveySenderData var0, String var1, String var2, String var3, SurveySenderAvatar var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.firstName;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.name;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.initials;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.avatar;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "SurveySenderData(firstName=" + this.firstName + ", name=" + this.name + ", initials=" + this.initials + ", avatar=" + this.avatar + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.firstName == null ? 0 : this.firstName.hashCode();
      var1 = var1 * 31 + (this.name == null ? 0 : this.name.hashCode());
      var1 = var1 * 31 + (this.initials == null ? 0 : this.initials.hashCode());
      return var1 * 31 + (this.avatar == null ? 0 : this.avatar.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SurveySenderData)) {
         return false;
      } else {
         SurveySenderData var2 = (SurveySenderData)var1;
         if (!Intrinsics.areEqual(this.firstName, var2.firstName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, var2.name)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.initials, var2.initials) ? false : Intrinsics.areEqual(this.avatar, var2.avatar);
         }
      }
   }

   public SurveySenderData() {
      this(null, null, null, null, 15, null);
   }
}
