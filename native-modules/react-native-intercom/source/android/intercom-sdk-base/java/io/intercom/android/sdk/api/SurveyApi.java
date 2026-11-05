package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b`\u0018\u00002\u00020\u0001J(\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH§@¢\u0006\u0002\u0010\tJ(\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH§@¢\u0006\u0002\u0010\tJ(\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH§@¢\u0006\u0002\u0010\tJ(\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH§@¢\u0006\u0002\u0010\tJ(\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00032\b\b\u0001\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH§@¢\u0006\u0002\u0010\t¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/api/SurveyApi;",
         "",
         "submitSurveyStep",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyResponse;",
         "surveyId",
         "",
         "options",
         "Lokhttp3/RequestBody;",
         "(Ljava/lang/String;Lokhttp3/RequestBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "dismissSurvey",
         "",
         "submitCtaStat",
         "reportFailure",
         "fetchSurvey",
         "Lio/intercom/android/sdk/survey/model/FetchSurveyRequest;",
         "intercom-sdk-base_release"
   }
)
public interface SurveyApi {
   @POST("surveys/{surveyId}/submit")
   @Nullable
   Object submitSurveyStep(@Path("surveyId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("surveys/{surveyId}/dismiss")
   @Nullable
   Object dismissSurvey(@Path("surveyId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("surveys/{surveyId}/action_button_clicked")
   @Nullable
   Object submitCtaStat(@Path("surveyId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("surveys/{survey_id}/failure")
   @Nullable
   Object reportFailure(@Path("survey_id") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);

   @POST("surveys/{surveyId}/fetch")
   @Nullable
   Object fetchSurvey(@Path("surveyId") @NotNull String var1, @Body @NotNull RequestBody var2, @NotNull Continuation var3);
}
