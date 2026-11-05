package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.utilities.NullSafety;

public final class Upload$Builder {
   long id;
   @Nullable
   String acl;
   @Nullable
   String aws_access_key;
   @Nullable
   String content_type;
   @Nullable
   String key;
   @Nullable
   String policy;
   @Nullable
   String public_url;
   @Nullable
   String signature;
   @Nullable
   String success_action_status;
   @Nullable
   String upload_destination;
   @Nullable
   UploadMetadata metadata;

   private UploadMetadata defaultMetadataBuilder() {
      return this.metadata != null ? this.metadata : new UploadMetadata("");
   }

   public Upload build() {
      return new AutoValue_Upload(
         this.id,
         NullSafety.valueOrEmpty(this.acl),
         NullSafety.valueOrEmpty(this.aws_access_key),
         NullSafety.valueOrEmpty(this.content_type),
         NullSafety.valueOrEmpty(this.key),
         NullSafety.valueOrEmpty(this.policy),
         NullSafety.valueOrEmpty(this.public_url),
         NullSafety.valueOrEmpty(this.signature),
         NullSafety.valueOrEmpty(this.success_action_status),
         NullSafety.valueOrEmpty(this.upload_destination),
         this.defaultMetadataBuilder()
      );
   }
}
