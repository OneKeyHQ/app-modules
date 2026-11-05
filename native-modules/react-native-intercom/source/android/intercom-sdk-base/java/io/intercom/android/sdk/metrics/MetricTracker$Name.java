package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$Name {
   String MESSENGER = "m5_metric";
   String EDUCATE = "educate_event";
   String CAROUSEL = "carousel_metric";
   String SURVEY = "survey_metric";
   String SEARCH_BROWSE = "search_browse_event";
}
