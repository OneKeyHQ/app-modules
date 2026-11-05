package io.intercom.android.sdk.survey.ui;

import android.content.Context;
import android.content.Intent;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/IntercomSurveyActivity$Companion;",
         "",
         "<init>",
         "()V",
         "PARCEL_SURVEY_ID",
         "",
         "buildIntent",
         "Landroid/content/Intent;",
         "context",
         "Landroid/content/Context;",
         "surveyId",
         "intercom-sdk-base_release"
   }
)
public final class IntercomSurveyActivity$Companion {
   private IntercomSurveyActivity$Companion() {
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final Intent buildIntent(@NotNull Context var1, @Nullable String var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      Intent var3 = new Intent(var1, IntercomSurveyActivity.class);
      Intent var4 = var3;
      int var5 = 0;
      var4.putExtra("parcel_survey_id", var2);
      var4.setFlags(335544320);
      return var3;
   }

   // $VF: synthetic method
   public static Intent buildIntent$default(IntercomSurveyActivity$Companion var0, Context var1, String var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.buildIntent(var1, var2);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public final Intent buildIntent(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      return buildIntent$default(this, var1, null, 2, null);
   }

   // $VF: synthetic method
   public IntercomSurveyActivity$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
