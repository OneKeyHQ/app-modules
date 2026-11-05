package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$CloseActionContext {
   int CLOSE_BUTTON = 0;
   int BACK_BUTTON = 1;
}
