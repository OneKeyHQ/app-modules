package io.intercom.android.sdk.identity;

import org.jspecify.annotations.Nullable;

final class AutoValue_SoftUserIdentity extends SoftUserIdentity {
   private final String anonymousId;
   private final String email;
   private final String fingerprint;
   private final String hmac;
   private final String jwt;
   private final String intercomId;
   private final String userId;
   private final String encryptedUserId;
   private final String messengerEnvironment;

   AutoValue_SoftUserIdentity(String var1, String var2, String var3, String var4, String var5, String var6, String var7, String var8, String var9) {
      if (var1 == null) {
         throw new NullPointerException("Null anonymousId");
      }

      this.anonymousId = var1;
      if (var2 == null) {
         throw new NullPointerException("Null email");
      }

      this.email = var2;
      if (var3 == null) {
         throw new NullPointerException("Null fingerprint");
      }

      this.fingerprint = var3;
      if (var4 == null) {
         throw new NullPointerException("Null hmac");
      }

      this.hmac = var4;
      if (var5 == null) {
         throw new NullPointerException("Null jwt");
      }

      this.jwt = var5;
      if (var6 == null) {
         throw new NullPointerException("Null intercomId");
      }

      this.intercomId = var6;
      if (var7 == null) {
         throw new NullPointerException("Null userId");
      }

      this.userId = var7;
      if (var8 == null) {
         throw new NullPointerException("Null encryptedUserId");
      }

      this.encryptedUserId = var8;
      if (var9 == null) {
         throw new NullPointerException("Null messengerEnvironment");
      }

      this.messengerEnvironment = var9;
   }

   @Override
   String anonymousId() {
      return this.anonymousId;
   }

   @Override
   String email() {
      return this.email;
   }

   @Override
   String fingerprint() {
      return this.fingerprint;
   }

   @Override
   String hmac() {
      return this.hmac;
   }

   @Override
   String jwt() {
      return this.jwt;
   }

   @Override
   String intercomId() {
      return this.intercomId;
   }

   @Override
   String userId() {
      return this.userId;
   }

   @Override
   String encryptedUserId() {
      return this.encryptedUserId;
   }

   @Override
   String messengerEnvironment() {
      return this.messengerEnvironment;
   }

   @Override
   public String toString() {
      return "SoftUserIdentity{anonymousId="
         + this.anonymousId
         + ", email="
         + this.email
         + ", fingerprint="
         + this.fingerprint
         + ", hmac="
         + this.hmac
         + ", jwt="
         + this.jwt
         + ", intercomId="
         + this.intercomId
         + ", userId="
         + this.userId
         + ", encryptedUserId="
         + this.encryptedUserId
         + ", messengerEnvironment="
         + this.messengerEnvironment
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof SoftUserIdentity)) {
         return false;
      }

      SoftUserIdentity var2 = (SoftUserIdentity)var1;
      return this.anonymousId.equals(var2.anonymousId())
         && this.email.equals(var2.email())
         && this.fingerprint.equals(var2.fingerprint())
         && this.hmac.equals(var2.hmac())
         && this.jwt.equals(var2.jwt())
         && this.intercomId.equals(var2.intercomId())
         && this.userId.equals(var2.userId())
         && this.encryptedUserId.equals(var2.encryptedUserId())
         && this.messengerEnvironment.equals(var2.messengerEnvironment());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= this.anonymousId.hashCode();
      var1 *= 1000003;
      var1 ^= this.email.hashCode();
      var1 *= 1000003;
      var1 ^= this.fingerprint.hashCode();
      var1 *= 1000003;
      var1 ^= this.hmac.hashCode();
      var1 *= 1000003;
      var1 ^= this.jwt.hashCode();
      var1 *= 1000003;
      var1 ^= this.intercomId.hashCode();
      var1 *= 1000003;
      var1 ^= this.userId.hashCode();
      var1 *= 1000003;
      var1 ^= this.encryptedUserId.hashCode();
      var1 *= 1000003;
      return var1 ^ this.messengerEnvironment.hashCode();
   }
}
