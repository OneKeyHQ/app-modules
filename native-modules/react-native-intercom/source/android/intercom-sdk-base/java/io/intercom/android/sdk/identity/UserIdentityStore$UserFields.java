package io.intercom.android.sdk.identity;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BM\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003JO\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001f\u001a\u00020 H×\u0001J\t\u0010!\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0011\u0010\b\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserIdentityStore$UserFields;",
         "",
         "anonymousId",
         "",
         "intercomId",
         "userId",
         "email",
         "hmac",
         "jwt",
         "messengerEnvironment",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getAnonymousId",
         "()Ljava/lang/String;",
         "getIntercomId",
         "getUserId",
         "getEmail",
         "getHmac",
         "getJwt",
         "getMessengerEnvironment",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
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
public final class UserIdentityStore$UserFields {
   @NotNull
   private final String anonymousId;
   @NotNull
   private final String intercomId;
   @NotNull
   private final String userId;
   @NotNull
   private final String email;
   @NotNull
   private final String hmac;
   @NotNull
   private final String jwt;
   @NotNull
   private final String messengerEnvironment;
   public static int $stable;

   static {}

   public UserIdentityStore$UserFields(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5, @NotNull String var6, @NotNull String var7
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "anonymousId");
      Intrinsics.checkNotNullParameter(var2, "intercomId");
      Intrinsics.checkNotNullParameter(var3, "userId");
      Intrinsics.checkNotNullParameter(var4, "email");
      Intrinsics.checkNotNullParameter(var5, "hmac");
      Intrinsics.checkNotNullParameter(var6, "jwt");
      Intrinsics.checkNotNullParameter(var7, "messengerEnvironment");
      this.anonymousId = var1;
      this.intercomId = var2;
      this.userId = var3;
      this.email = var4;
      this.hmac = var5;
      this.jwt = var6;
      this.messengerEnvironment = var7;
   }

   // $VF: synthetic method
   public UserIdentityStore$UserFields(
      String var1, String var2, String var3, String var4, String var5, String var6, String var7, int var8, DefaultConstructorMarker var9
   ) {
      this((((var8 & 1) != 0) ? (String) ("") : var1), (((var8 & 2) != 0) ? (String) ("") : var2), (((var8 & 4) != 0) ? (String) ("") : var3), (((var8 & 8) != 0) ? (String) ("") : var4), (((var8 & 16) != 0) ? (String) ("") : var5), (((var8 & 32) != 0) ? (String) ("") : var6), (((var8 & 64) != 0) ? (String) ("") : var7));
   }

   @NotNull
   public final String getAnonymousId() {
      return this.anonymousId;
   }

   @NotNull
   public final String getIntercomId() {
      return this.intercomId;
   }

   @NotNull
   public final String getUserId() {
      return this.userId;
   }

   @NotNull
   public final String getEmail() {
      return this.email;
   }

   @NotNull
   public final String getHmac() {
      return this.hmac;
   }

   @NotNull
   public final String getJwt() {
      return this.jwt;
   }

   @NotNull
   public final String getMessengerEnvironment() {
      return this.messengerEnvironment;
   }

   @NotNull
   public final String component1() {
      return this.anonymousId;
   }

   @NotNull
   public final String component2() {
      return this.intercomId;
   }

   @NotNull
   public final String component3() {
      return this.userId;
   }

   @NotNull
   public final String component4() {
      return this.email;
   }

   @NotNull
   public final String component5() {
      return this.hmac;
   }

   @NotNull
   public final String component6() {
      return this.jwt;
   }

   @NotNull
   public final String component7() {
      return this.messengerEnvironment;
   }

   @NotNull
   public final UserIdentityStore$UserFields copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull String var5, @NotNull String var6, @NotNull String var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "anonymousId");
      Intrinsics.checkNotNullParameter(var2, "intercomId");
      Intrinsics.checkNotNullParameter(var3, "userId");
      Intrinsics.checkNotNullParameter(var4, "email");
      Intrinsics.checkNotNullParameter(var5, "hmac");
      Intrinsics.checkNotNullParameter(var6, "jwt");
      Intrinsics.checkNotNullParameter(var7, "messengerEnvironment");
      return new UserIdentityStore$UserFields(var1, var2, var3, var4, var5, var6, var7);
   }

   // $VF: synthetic method
   public static UserIdentityStore$UserFields copy$default(
      UserIdentityStore$UserFields var0, String var1, String var2, String var3, String var4, String var5, String var6, String var7, int var8, Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.anonymousId;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.intercomId;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.userId;
      }

      if ((var8 & 8) != 0) {
         var4 = var0.email;
      }

      if ((var8 & 16) != 0) {
         var5 = var0.hmac;
      }

      if ((var8 & 32) != 0) {
         var6 = var0.jwt;
      }

      if ((var8 & 64) != 0) {
         var7 = var0.messengerEnvironment;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "UserFields(anonymousId="
         + this.anonymousId
         + ", intercomId="
         + this.intercomId
         + ", userId="
         + this.userId
         + ", email="
         + this.email
         + ", hmac="
         + this.hmac
         + ", jwt="
         + this.jwt
         + ", messengerEnvironment="
         + this.messengerEnvironment
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.anonymousId.hashCode();
      var1 = var1 * 31 + this.intercomId.hashCode();
      var1 = var1 * 31 + this.userId.hashCode();
      var1 = var1 * 31 + this.email.hashCode();
      var1 = var1 * 31 + this.hmac.hashCode();
      var1 = var1 * 31 + this.jwt.hashCode();
      return var1 * 31 + this.messengerEnvironment.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof UserIdentityStore$UserFields)) {
         return false;
      } else {
         UserIdentityStore$UserFields var2 = (UserIdentityStore$UserFields)var1;
         if (!Intrinsics.areEqual(this.anonymousId, var2.anonymousId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.intercomId, var2.intercomId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.userId, var2.userId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.email, var2.email)) {
            return false;
         } else if (!Intrinsics.areEqual(this.hmac, var2.hmac)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.jwt, var2.jwt) ? false : Intrinsics.areEqual(this.messengerEnvironment, var2.messengerEnvironment);
         }
      }
   }

   public UserIdentityStore$UserFields() {
      this(null, null, null, null, null, null, null, 127, null);
   }
}
