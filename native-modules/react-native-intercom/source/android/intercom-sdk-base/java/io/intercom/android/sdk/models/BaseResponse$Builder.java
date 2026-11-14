package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;

abstract class BaseResponse$Builder {
   @Nullable
   Config config;
   @Nullable
   User$Builder user;
   boolean has_conversations;

   public abstract BaseResponse build();
}
