package io.intercom.android.sdk.survey;

import io.intercom.android.sdk.survey.model.SurveyData$StepType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class SurveyViewModel$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$1;

   static {
      int[] var0 = new int[SurveyData$StepType.values().length];

      try {
         var0[SurveyData$StepType.QUESTION.ordinal()] = 1;
      } catch (NoSuchFieldError var9) {
      }

      try {
         var0[SurveyData$StepType.INTRO.ordinal()] = 2;
      } catch (NoSuchFieldError var8) {
      }

      try {
         var0[SurveyData$StepType.CONTENT.ordinal()] = 3;
      } catch (NoSuchFieldError var7) {
      }

      try {
         var0[SurveyData$StepType.THANK_YOU.ordinal()] = 4;
      } catch (NoSuchFieldError var6) {
      }

      $EnumSwitchMapping$0 = var0;
      var0 = new int[CloseEventTrigger.values().length];

      try {
         var0[CloseEventTrigger.CTA.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[CloseEventTrigger.CLOSE_BUTTON.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[CloseEventTrigger.SECONDARY_CTA_EXTERNAL_LINK.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[CloseEventTrigger.SECONDARY_CTA_DEEP_LINK.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$1 = var0;
   }
}
