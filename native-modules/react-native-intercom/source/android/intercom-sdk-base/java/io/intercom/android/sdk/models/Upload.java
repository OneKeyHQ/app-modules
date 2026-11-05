package io.intercom.android.sdk.models;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Upload {
   public abstract long getId();

   public abstract String getAcl();

   public abstract String getAwsAccessKey();

   public abstract String getContentType();

   public abstract String getKey();

   public abstract String getPolicy();

   public abstract String getPublicUrl();

   public abstract String getSignature();

   public abstract String getSuccessActionStatus();

   public abstract String getUploadDestination();

   public abstract UploadMetadata getMetadata();
}
