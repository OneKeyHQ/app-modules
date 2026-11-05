package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.Gson;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.MessengerApiHelper;
import io.intercom.android.sdk.api.SurveyApi;
import io.intercom.android.sdk.survey.model.StatsCtaRequestModel;
import io.intercom.android.sdk.survey.model.SubmitSurveyRequest;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J$\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0086@¢\u0006\u0002\u0010\rJ$\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\fH\u0086@¢\u0006\u0002\u0010\u0012J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00140\u00072\u0006\u0010\u0010\u001a\u00020\fH\u0086@¢\u0006\u0002\u0010\u0015J$\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00072\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\fH\u0086@¢\u0006\u0002\u0010\u0012J\u001e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0018\u001a\u00020\u0019H\u0086@¢\u0006\u0002\u0010\u001aR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyRepository;",
         "",
         "surveyApi",
         "Lio/intercom/android/sdk/api/SurveyApi;",
         "<init>",
         "(Lio/intercom/android/sdk/api/SurveyApi;)V",
         "submitSurvey",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyResponse;",
         "submitSurveyRequest",
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyRequest;",
         "id",
         "",
         "(Lio/intercom/android/sdk/survey/model/SubmitSurveyRequest;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "dismissSurvey",
         "",
         "surveyId",
         "surveyProgressId",
         "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "fetchSurvey",
         "Lio/intercom/android/sdk/survey/model/FetchSurveyRequest;",
         "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "reportFailure",
         "submitCtaStat",
         "statsCtaRequestModel",
         "Lio/intercom/android/sdk/survey/model/StatsCtaRequestModel;",
         "(Ljava/lang/String;Lio/intercom/android/sdk/survey/model/StatsCtaRequestModel;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class SurveyRepository {
   @NotNull
   private final SurveyApi surveyApi;
   public static final int $stable;

   static { $stable = 8; }

   public SurveyRepository(@NotNull SurveyApi var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "surveyApi");
      this.surveyApi = var1;
   }

   // $VF: synthetic method
   public SurveyRepository(SurveyApi var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (SurveyApi) (Injector.get().getSurveyApi()) : var1));
   }

   @Nullable
   public final Object submitSurvey(@NotNull SubmitSurveyRequest var1, @NotNull String var2, @NotNull Continuation var3) {
      String var4 = new Gson().toJson(var1);
      Object var10000 = new Gson().fromJson(var4, Map.class);
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.Any>");
      Map var5 = (Map)var10000;
      RequestBody var6 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(var5);
      return this.surveyApi.submitSurveyStep(var2, var6, var3);
   }

   @Nullable
   public final Object dismissSurvey(@NotNull String var1, @NotNull String var2, @NotNull Continuation var3) {
      return this.surveyApi
         .dismissSurvey(
            var1, MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(TuplesKt.to("survey_progress_id", var2))), var3
         );
   }

   @Nullable
   public final Object fetchSurvey(@NotNull String var1, @NotNull Continuation var2) {
      return this.surveyApi
         .fetchSurvey(var1, MessengerApiHelper.getDefaultRequestBody$intercom_sdk_base_release$default(MessengerApiHelper.INSTANCE, null, 1, null), var2);
   }

   @Nullable
   public final Object reportFailure(@NotNull String var1, @NotNull String var2, @NotNull Continuation var3) {
      return this.surveyApi
         .reportFailure(
            var1, MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(TuplesKt.to("survey_progress_id", var2))), var3
         );
   }

   @Nullable
   public final Object submitCtaStat(@NotNull String var1, @NotNull StatsCtaRequestModel var2, @NotNull Continuation var3) {
      String var4 = new Gson().toJson(var2);
      Object var10000 = new Gson().fromJson(var4, Map.class);
      Intrinsics.checkNotNull(var10000, "null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.Any>");
      Map var5 = (Map)var10000;
      RequestBody var6 = MessengerApiHelper.INSTANCE.getDefaultRequestBody$intercom_sdk_base_release(var5);
      var10000 = this.surveyApi.submitCtaStat(var1, var6, var3);
      return var10000 == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? var10000 : Unit.INSTANCE;
   }

   public SurveyRepository() {
      this(null, 1, null);
   }
}
