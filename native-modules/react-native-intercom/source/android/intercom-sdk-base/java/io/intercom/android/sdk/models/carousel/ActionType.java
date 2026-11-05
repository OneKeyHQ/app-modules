package io.intercom.android.sdk.models.carousel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ActionType {
   String CONTINUE = "continue";
   String SKIP = "skip";
   String DISMISS = "dismiss";
   String LINK = "link";
   String START_CHAT = "start_chat";
   String LOCATION_PERMISSION = "location_permission";
   String CAMERA_PERMISSION = "camera_permission";
   String PHOTOS_PERMISSION = "photos_permission";
   String MICROPHONE_PERMISSION = "microphone_permission";
}
