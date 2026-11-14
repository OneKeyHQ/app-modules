package io.intercom.android.sdk.survey.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\n\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step$Question$QuestionValidation$ValidationType;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "TEXT",
         "NUMBER",
         "EMAIL",
         "PHONE",
         "NO_VALIDATION",
         "FLOAT",
         "INTEGER",
         "intercom-sdk-base_release"
   }
)
public enum SurveyData$Step$Question$QuestionValidation$ValidationType {
   TEXT,
   NUMBER,
   EMAIL,
   PHONE,
   NO_VALIDATION,
   FLOAT,
   INTEGER;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(SurveyData$Step$Question$QuestionValidation$ValidationType.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
