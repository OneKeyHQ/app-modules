package io.intercom.android.sdk.identity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface FeatureFlag {
   String BLOCK_RENDERING_FALLBACK = "new-block-rendering-killswitch";
   String ABLY_REALTIME_DISABLED = "ably-realtime-disabled";
}
