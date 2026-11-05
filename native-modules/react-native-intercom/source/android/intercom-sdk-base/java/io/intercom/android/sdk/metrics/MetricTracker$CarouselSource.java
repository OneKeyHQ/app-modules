package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$CarouselSource {
   String AUTOMATIC = "automatic";
   String PROGRAMMATIC = "programmatic";
}
