package io.intercom.android.sdk.models;

import io.intercom.android.sdk.models.carousel.Carousel;
import io.intercom.android.sdk.models.carousel.Carousel$Builder;
import io.intercom.android.sdk.survey.model.SurveyData;

public class UpdateUserResponse extends UsersResponse {
   private final Carousel carousel;
   private final SurveyData surveyData;

   UpdateUserResponse(UpdateUserResponse$Builder var1) {
      super(var1);
      this.carousel = var1.carousel == null ? new Carousel$Builder().build() : var1.carousel.build();
      this.surveyData = var1.survey == null ? SurveyData.Companion.getNULL() : var1.survey;
   }

   public Carousel getCarousel() {
      return this.carousel;
   }

   public SurveyData getSurveyData() {
      return this.surveyData;
   }
}
