package io.intercom.android.sdk.conversation;

public interface UploadProgressListener {
   void uploadStarted();

   void uploadNotice(byte var1);

   void uploadSmoothEnd();

   void uploadStopped();
}
