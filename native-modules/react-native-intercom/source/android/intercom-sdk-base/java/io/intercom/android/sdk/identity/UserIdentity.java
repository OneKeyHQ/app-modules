package io.intercom.android.sdk.identity;

import androidx.annotation.RestrictTo;
import androidx.annotation.VisibleForTesting;
import androidx.annotation.RestrictTo.Scope;
import io.intercom.android.sdk.models.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserIdentity {
   private static final String USER = "user";
   private static final String TYPE = "type";
   private static final String USER_ID = "user_id";
   public static final String EMAIL = "email";
   public static final String ANONYMOUS_ID = "anonymous_id";
   public static final String INTERCOM_ID = "intercom_id";
   private final UserIdentityStore store;
   private volatile boolean loaded;
   private String hmac = "";
   private String jwt = "";
   private String anonymousId = "";
   private String intercomId = "";
   private String encryptedUserId = "";
   private String userId = "";
   private String email = "";
   private String messengerEnvironment = "";
   private String fingerprint = "";
   private SoftUserIdentity softUserIdentity = SoftUserIdentity.NONE;

   @RestrictTo(Scope.LIBRARY)
   public UserIdentity(UserIdentityStore var1) {
      this.store = var1;
   }

   private void ensureLoaded() {
      if (!this.loaded) {
         synchronized (this) {
            if (!this.loaded) {
               UserIdentityStore$UserFields var2 = this.store.loadUserFields();
               this.anonymousId = var2.getAnonymousId();
               this.intercomId = var2.getIntercomId();
               this.userId = var2.getUserId();
               this.email = var2.getEmail();
               this.hmac = var2.getHmac();
               this.jwt = var2.getJwt();
               this.messengerEnvironment = var2.getMessengerEnvironment();
               this.encryptedUserId = "";
               if (!this.email.isEmpty() || !this.userId.isEmpty() || !this.intercomId.isEmpty() || !this.anonymousId.isEmpty()) {
                  this.fingerprint = this.generateFingerprint();
               }

               this.loaded = true;
            }
         }
      }
   }

   public void registerIdentifiedUser(Registration var1) {
      this.ensureLoaded();
      this.intercomId = "";
      if (!var1.getUserId().isEmpty()) {
         this.userId = var1.getUserId();
      }

      if (!var1.getEmail().isEmpty()) {
         this.email = var1.getEmail();
      }

      this.store
         .save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, this.jwt, this.messengerEnvironment));
      if (this.fingerprint.isEmpty()) {
         this.fingerprint = this.generateFingerprint();
      }
   }

   public void registerUnidentifiedUser() {
      this.ensureLoaded();
      this.anonymousId = UUID.randomUUID().toString();
      this.store
         .save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, this.jwt, this.messengerEnvironment));
      if (this.fingerprint.isEmpty()) {
         this.fingerprint = this.generateFingerprint();
      }
   }

   public synchronized void update(User var1) {
      if (var1 != User.NULL) {
         this.ensureLoaded();
         this.userId = var1.getUserId();
         this.email = var1.getEmail();
         this.anonymousId = var1.getAnonymousId();
         this.encryptedUserId = var1.getEncryptedUserId();
         if (!var1.getIntercomId().isEmpty()) {
            this.intercomId = var1.getIntercomId();
         }

         this.store
            .save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, this.jwt, this.messengerEnvironment));
      }
   }

   public void setUserHash(String var1) {
      this.ensureLoaded();
      this.hmac = var1;
      this.store.save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, var1, this.jwt, this.messengerEnvironment));
   }

   public void setJwt(String var1) {
      this.ensureLoaded();
      this.jwt = var1;
      this.store.save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, var1, this.messengerEnvironment));
   }

   public String getUserId() {
      this.ensureLoaded();
      return this.userId;
   }

   public String getEmail() {
      this.ensureLoaded();
      return this.email;
   }

   public String getAnonymousId() {
      this.ensureLoaded();
      return this.anonymousId;
   }

   public String getHmac() {
      this.ensureLoaded();
      return this.hmac;
   }

   public String getJwt() {
      this.ensureLoaded();
      return this.jwt;
   }

   public String getMessengerEnvironment() {
      this.ensureLoaded();
      return this.messengerEnvironment;
   }

   public void setMessengerEnvironment(String var1) {
      this.ensureLoaded();
      this.messengerEnvironment = var1;
      this.store.save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, this.jwt, var1));
   }

   public String getIntercomId() {
      this.ensureLoaded();
      return this.intercomId;
   }

   public String getEncryptedUserId() {
      this.ensureLoaded();
      return this.encryptedUserId;
   }

   public String getFingerprint() {
      this.ensureLoaded();
      return this.fingerprint;
   }

   public boolean hasIntercomId() {
      this.ensureLoaded();
      return !this.getIntercomId().isEmpty();
   }

   public boolean identityExists() {
      this.ensureLoaded();
      return !this.email.isEmpty() || !this.userId.isEmpty() || !this.intercomId.isEmpty() || !this.anonymousId.isEmpty();
   }

   public boolean isIdentified() {
      this.ensureLoaded();
      return this.identityExists() && !this.isUnidentified();
   }

   public boolean isUnidentified() {
      this.ensureLoaded();
      return !this.anonymousId.isEmpty() && this.email.isEmpty() && this.userId.isEmpty();
   }

   public void hardReset() {
      this.softUserIdentity = SoftUserIdentity.NONE;
   }

   public Map toMap() {
      this.ensureLoaded();
      Map var1 = new HashMap();
      if (!this.anonymousId.isEmpty()) {
         var1.put("anonymous_id", this.anonymousId);
      } else if (!this.intercomId.isEmpty()) {
         var1.put("intercom_id", this.intercomId);
      }

      if (!this.userId.isEmpty()) {
         var1.put("user_id", this.userId);
      }

      if (!this.email.isEmpty()) {
         var1.put("email", this.email);
      }

      var1.put("type", "user");
      return var1;
   }

   public Map softUserIdentityToMap() {
      Map var1 = new HashMap();
      String var2 = this.softUserIdentity.anonymousId();
      String var3 = this.softUserIdentity.intercomId();
      String var4 = this.softUserIdentity.userId();
      String var5 = this.softUserIdentity.email();
      if (!var2.isEmpty()) {
         var1.put("anonymous_id", var2);
      } else if (!var3.isEmpty()) {
         var1.put("intercom_id", var3);
      }

      if (!var4.isEmpty()) {
         var1.put("user_id", var4);
      }

      if (!var5.isEmpty()) {
         var1.put("email", var5);
      }

      var1.put("type", "user");
      return var1;
   }

   private String generateFingerprint() {
      return UUID.randomUUID().toString();
   }

   public boolean registrationHasAttributes(Registration var1) {
      return var1 != null && var1.getAttributes() != null && !var1.getAttributes().isEmpty();
   }

   public boolean softIdentityIsSameUser(Registration var1) {
      this.ensureLoaded();
      return this.isUnidentified() ? false : this.softUserIdentity.isSameUser(var1);
   }

   public boolean softUserIdentityHmacDiffers(String var1) {
      return this.softUserIdentity.isPresent() && !this.getSoftUserIdentityHmac().equals(var1);
   }

   @VisibleForTesting
   public String getSoftUserIdentityHmac() {
      return this.softUserIdentity.hmac();
   }

   public boolean softUserIdentityJwtDiffers(String var1) {
      return this.softUserIdentity.isPresent() && !this.softUserIdentity.jwt().equals(var1);
   }

   public synchronized void softReset() {
      this.ensureLoaded();
      if (!this.isSoftReset()) {
         this.softUserIdentity = SoftUserIdentity.create(
            this.anonymousId, this.email, this.fingerprint, this.hmac, this.jwt, this.intercomId, this.userId, this.encryptedUserId, this.messengerEnvironment
         );
         this.store.clear();
         this.anonymousId = "";
         this.intercomId = "";
         this.encryptedUserId = "";
         this.userId = "";
         this.email = "";
         this.hmac = "";
         this.jwt = "";
         this.fingerprint = "";
         this.messengerEnvironment = "";
      }
   }

   public boolean isSoftReset() {
      return this.softUserIdentity.isPresent();
   }

   public void softRestart() {
      this.ensureLoaded();
      this.userId = this.softUserIdentity.userId();
      this.email = this.softUserIdentity.email();
      this.anonymousId = this.softUserIdentity.anonymousId();
      this.intercomId = this.softUserIdentity.intercomId();
      this.encryptedUserId = this.softUserIdentity.encryptedUserId();
      this.hmac = this.softUserIdentity.hmac();
      this.jwt = this.softUserIdentity.jwt();
      this.fingerprint = this.softUserIdentity.fingerprint();
      this.messengerEnvironment = this.softUserIdentity.messengerEnvironment();
      this.store
         .save(new UserIdentityStore$UserFields(this.anonymousId, this.intercomId, this.userId, this.email, this.hmac, this.jwt, this.messengerEnvironment));
      this.softUserIdentity = SoftUserIdentity.NONE;
   }
}
