package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$Place {
   String MESSENGER = "messenger";
   String IN_APP = "in_app";
   String PUSH = "push";
   String MESSENGER_CARD = "messenger_card";
   String CAROUSEL_MESSAGE = "carousel_message";
   String SEARCH_RESULTS = "search_results";
   String ARTICLE = "article";
   String COLLECTION_LIST = "collection_list";
   String ARTICLE_LIST = "article_list";
   String API = "api";
   String LEGACY_ARTICLE_SEARCH_APP = "legacy_article_search_app";
   String CONVERSATION = "conversation";
   String MESSENGER_HOME_APP = "messenger_home_app";
   String HOME_SCREEN = "home";
   String SEARCH_BROWSE = "search_browse";
   String SURVEY = "survey";
   String SEARCH_BROWSE_CARD = "search_browse_card";
}
