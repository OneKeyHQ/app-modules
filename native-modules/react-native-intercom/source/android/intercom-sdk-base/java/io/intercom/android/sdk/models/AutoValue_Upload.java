package io.intercom.android.sdk.models;

import org.jspecify.annotations.Nullable;

final class AutoValue_Upload extends Upload {
   private final long id;
   private final String acl;
   private final String awsAccessKey;
   private final String contentType;
   private final String key;
   private final String policy;
   private final String publicUrl;
   private final String signature;
   private final String successActionStatus;
   private final String uploadDestination;
   private final UploadMetadata metadata;

   AutoValue_Upload(
      long var1, String var3, String var4, String var5, String var6, String var7, String var8, String var9, String var10, String var11, UploadMetadata var12
   ) {
      this.id = var1;
      if (var3 == null) {
         throw new NullPointerException("Null acl");
      }

      this.acl = var3;
      if (var4 == null) {
         throw new NullPointerException("Null awsAccessKey");
      }

      this.awsAccessKey = var4;
      if (var5 == null) {
         throw new NullPointerException("Null contentType");
      }

      this.contentType = var5;
      if (var6 == null) {
         throw new NullPointerException("Null key");
      }

      this.key = var6;
      if (var7 == null) {
         throw new NullPointerException("Null policy");
      }

      this.policy = var7;
      if (var8 == null) {
         throw new NullPointerException("Null publicUrl");
      }

      this.publicUrl = var8;
      if (var9 == null) {
         throw new NullPointerException("Null signature");
      }

      this.signature = var9;
      if (var10 == null) {
         throw new NullPointerException("Null successActionStatus");
      }

      this.successActionStatus = var10;
      if (var11 == null) {
         throw new NullPointerException("Null uploadDestination");
      }

      this.uploadDestination = var11;
      if (var12 == null) {
         throw new NullPointerException("Null metadata");
      }

      this.metadata = var12;
   }

   @Override
   public long getId() {
      return this.id;
   }

   @Override
   public String getAcl() {
      return this.acl;
   }

   @Override
   public String getAwsAccessKey() {
      return this.awsAccessKey;
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   @Override
   public String getPolicy() {
      return this.policy;
   }

   @Override
   public String getPublicUrl() {
      return this.publicUrl;
   }

   @Override
   public String getSignature() {
      return this.signature;
   }

   @Override
   public String getSuccessActionStatus() {
      return this.successActionStatus;
   }

   @Override
   public String getUploadDestination() {
      return this.uploadDestination;
   }

   @Override
   public UploadMetadata getMetadata() {
      return this.metadata;
   }

   @Override
   public String toString() {
      return "Upload{id="
         + this.id
         + ", acl="
         + this.acl
         + ", awsAccessKey="
         + this.awsAccessKey
         + ", contentType="
         + this.contentType
         + ", key="
         + this.key
         + ", policy="
         + this.policy
         + ", publicUrl="
         + this.publicUrl
         + ", signature="
         + this.signature
         + ", successActionStatus="
         + this.successActionStatus
         + ", uploadDestination="
         + this.uploadDestination
         + ", metadata="
         + this.metadata
         + "}";
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (var1 == this) {
         return true;
      }

      if (!(var1 instanceof Upload)) {
         return false;
      }

      Upload var2 = (Upload)var1;
      return this.id == var2.getId()
         && this.acl.equals(var2.getAcl())
         && this.awsAccessKey.equals(var2.getAwsAccessKey())
         && this.contentType.equals(var2.getContentType())
         && this.key.equals(var2.getKey())
         && this.policy.equals(var2.getPolicy())
         && this.publicUrl.equals(var2.getPublicUrl())
         && this.signature.equals(var2.getSignature())
         && this.successActionStatus.equals(var2.getSuccessActionStatus())
         && this.uploadDestination.equals(var2.getUploadDestination())
         && this.metadata.equals(var2.getMetadata());
   }

   @Override
   public int hashCode() {
      int var1 = 1;
      var1 *= 1000003;
      var1 ^= (int)(this.id >>> 32 ^ this.id);
      var1 *= 1000003;
      var1 ^= this.acl.hashCode();
      var1 *= 1000003;
      var1 ^= this.awsAccessKey.hashCode();
      var1 *= 1000003;
      var1 ^= this.contentType.hashCode();
      var1 *= 1000003;
      var1 ^= this.key.hashCode();
      var1 *= 1000003;
      var1 ^= this.policy.hashCode();
      var1 *= 1000003;
      var1 ^= this.publicUrl.hashCode();
      var1 *= 1000003;
      var1 ^= this.signature.hashCode();
      var1 *= 1000003;
      var1 ^= this.successActionStatus.hashCode();
      var1 *= 1000003;
      var1 ^= this.uploadDestination.hashCode();
      var1 *= 1000003;
      return var1 ^ this.metadata.hashCode();
   }
}
