package io.intercom.android.sdk.survey.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionData$QuestionSubType;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "NUMERIC",
         "STARS",
         "EMOJI",
         "NPS",
         "UNSUPPORTED",
         "intercom-sdk-base_release"
   }
)
public enum SurveyData$Step$Question$QuestionData$QuestionSubType {
   NUMERIC,
   STARS,
   EMOJI,
   NPS,
   UNSUPPORTED;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(SurveyData$Step$Question$QuestionData$QuestionSubType.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
