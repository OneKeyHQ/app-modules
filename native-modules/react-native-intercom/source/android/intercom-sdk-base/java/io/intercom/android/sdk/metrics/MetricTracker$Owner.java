package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$Owner {
   String MESSENGER = "messenger";
   String EDUCATE = "educate";
}
