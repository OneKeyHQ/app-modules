package io.intercom.android.sdk.metrics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface MetricTracker$Context {
   String FROM_LAUNCHER = "from_launcher";
   String FROM_CUSTOM_LAUNCHER = "from_custom_launcher";
   String FROM_ANDROID_BACK_BUTTON = "from_android_back_button";
   String FROM_ONWARD_EXTERNAL_LINK = "from_onward_external_link";
   String FROM_ONWARD_DEEP_LINK = "from_onward_deep_link";
   String FROM_CLOSE_BUTTON = "from_close_button";
   String FROM_CONVERSATION_LIST = "from_conversation_list";
   String IN_NEW_CONVERSATION = "in_new_conversation";
   String FROM_NEW_CONVERSATION = "from_new_conversation";
   String FROM_CONVERSATION = "from_conversation";
   String IN_CONVERSATION = "in_conversation";
   String FROM_SNIPPET = "from_snippet";
   String FROM_FULL = "from_full";
   String FROM_CLICKING = "from_clicking";
   String FROM_SCROLLING = "from_scrolling";
   String FROM_AUTO = "from_auto";
   String FROM_PUSH = "from_push";
   String ON_ARTICLE = "on_article";
   String FROM_HELP_CENTER_WEBVIEW = "from_help_center_webview";
   String FROM_ARTICLE_ON_BUTTON = "from_article_on_button";
   String CONVERSATION = "conversation";
   String HOME_SCREEN = "home_screen";
   String FROM_CLOSED_CONVERSATION = "from_closed_conversation";
   String IN_FOLLOWUP_CONVERSATION = "in_followup_conversation";
   String FROM_CAROUSEL = "from_carousel";
   String FROM_RETRY = "from_retry";
   String FROM_CTA = "from_cta";
   String FROM_PERMISSION = "from_permission";
   String FROM_PERMISSION_SKIPPED = "from_permission_skipped";
   String NO_CONTEXT = "no_context";
   String STYLE_HUMAN = "style_human";
   String STYLE_BOT = "style_bot";
   String REACTION_SAD = "reaction_sad";
   String REACTION_NEUTRAL = "reaction_neutral";
   String REACTION_HAPPY = "reaction_happy";
   String ARTICLE = "article";
   String MESSENGER = "messenger";
   String FROM_PROGRAMMATIC = "from_programmatic";
   String FROM_AUTOMATIC = "from_automatic";
   String SPACE_MESSAGES = "messages";
   String SPACE_HELP = "help";
   String SPACE_HOME = "home";
   String FROM_TICKETS_SPACE = "from_tickets_space";
   String RECENT_ACTIVITY = "recent_activity";
   String NEW_CONVERSATION = "new_conversation";
   String EXISTING_CONVERSATION = "existing_conversation";
   String MORE_MENU = "more_menu";
}
