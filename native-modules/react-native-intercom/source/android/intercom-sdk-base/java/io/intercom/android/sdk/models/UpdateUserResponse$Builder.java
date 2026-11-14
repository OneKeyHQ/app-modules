package io.intercom.android.sdk.models;

import androidx.annotation.Nullable;
import io.intercom.android.sdk.models.carousel.Carousel$Builder;
import io.intercom.android.sdk.survey.model.SurveyData;

public final class UpdateUserResponse$Builder extends UsersResponse$Builder {
   @Nullable
   Carousel$Builder carousel;
   @Nullable
   SurveyData survey;

   public UpdateUserResponse build() {
      return new UpdateUserResponse(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   // $VF: synthetic method
   // $VF: bridge method
   
}
