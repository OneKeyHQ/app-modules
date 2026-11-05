package io.intercom.android.sdk.survey;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.survey.model.SurveyData;
import io.intercom.android.sdk.survey.model.SurveyData$Step;
import io.intercom.android.sdk.survey.model.SurveyData$Step$Question;
import io.intercom.android.sdk.survey.model.SurveyData$StepType;
import io.intercom.android.sdk.survey.model.SurveyData$SurveyActions;
import io.intercom.android.sdk.survey.model.SurveySenderAvatar;
import io.intercom.android.sdk.survey.model.SurveySenderData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.SharedFlowKt;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u008e\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\b\u0001\u0018\u0000 =2\u00020\u0001:\u0001=BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000fJ\u0006\u0010$\u001a\u00020%J\u0010\u0010&\u001a\u00020%2\u0006\u0010'\u001a\u00020(H\u0002J\u0018\u0010)\u001a\u00020*2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010+\u001a\u00020\u0013H\u0002J\u000e\u0010,\u001a\u00020%2\u0006\u0010-\u001a\u00020.J\u000e\u0010/\u001a\u00020%2\u0006\u00100\u001a\u000201J\u001c\u00102\u001a\u00020%2\u0006\u00103\u001a\u00020*2\n\b\u0002\u00104\u001a\u0004\u0018\u000105H\u0002J\u0012\u00106\u001a\u00020%2\n\b\u0002\u00104\u001a\u0004\u0018\u000105J\u0006\u00107\u001a\u00020%J\u001e\u00108\u001a\u00020%2\u0006\u0010'\u001a\u00020(2\u0006\u00109\u001a\u00020(H\u0082@¢\u0006\u0002\u0010:J\b\u0010;\u001a\u00020%H\u0002J\u0010\u0010<\u001a\u00020%2\u0006\u0010-\u001a\u00020.H\u0002R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e¢\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001d0\u001c¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\"0!X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00130!X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006>",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "dispatcher",
         "Lkotlinx/coroutines/CoroutineDispatcher;",
         "launchMode",
         "Lio/intercom/android/sdk/survey/SurveyLaunchMode;",
         "surveyRepository",
         "Lio/intercom/android/sdk/survey/SurveyRepository;",
         "appConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "<init>",
         "(Lkotlinx/coroutines/CoroutineDispatcher;Lio/intercom/android/sdk/survey/SurveyLaunchMode;Lio/intercom/android/sdk/survey/SurveyRepository;Lio/intercom/android/sdk/identity/AppConfig;Lio/intercom/android/sdk/metrics/MetricTracker;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;)V",
         "getDispatcher",
         "()Lkotlinx/coroutines/CoroutineDispatcher;",
         "currentStep",
         "Lio/intercom/android/sdk/survey/model/SurveyData$Step;",
         "surveyData",
         "Lio/intercom/android/sdk/survey/model/SurveyData;",
         "state",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/survey/SurveyState;",
         "getState",
         "()Lkotlinx/coroutines/flow/MutableStateFlow;",
         "effects",
         "Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "Lio/intercom/android/sdk/survey/SurveyEffects;",
         "getEffects",
         "()Lkotlinx/coroutines/flow/MutableSharedFlow;",
         "completedResponses",
         "",
         "Lio/intercom/android/sdk/survey/model/SubmitSurveyRequestItem;",
         "completedSteps",
         "onUiLoaded",
         "",
         "fetchAndShowSurvey",
         "surveyId",
         "",
         "buildContentState",
         "Lio/intercom/android/sdk/survey/SurveyState$Content;",
         "step",
         "onCloseClicked",
         "triggerType",
         "Lio/intercom/android/sdk/survey/CloseEventTrigger;",
         "onSecondaryCtaClicked",
         "secondaryCta",
         "Lio/intercom/android/sdk/survey/SurveyState$Content$SecondaryCta;",
         "submitSurvey",
         "surveyContent",
         "compositionAwareScope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "continueClicked",
         "onAnswerUpdated",
         "reportFailure",
         "surveyProgressId",
         "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "emitLoading",
         "sendDismissedEvent",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSurveyViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/SurveyViewModel\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,806:1\n1563#2:807\n1634#2,3:808\n1563#2:811\n1634#2,3:812\n1788#2,4:815\n1#3:819\n*S KotlinDebug\n*F\n+ 1 SurveyViewModel.kt\nio/intercom/android/sdk/survey/SurveyViewModel\n*L\n221#1:807\n221#1:808,3\n258#1:811\n258#1:812,3\n434#1:815,4\n*E\n"
)
public final class SurveyViewModel extends ViewModel {
   @NotNull
   public static final SurveyViewModel$Companion Companion = new SurveyViewModel$Companion(null);
   @NotNull
   private final CoroutineDispatcher dispatcher;
   @NotNull
   private final SurveyLaunchMode launchMode;
   @NotNull
   private final SurveyRepository surveyRepository;
   @NotNull
   private final AppConfig appConfig;
   @NotNull
   private final MetricTracker metricTracker;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @Nullable
   private SurveyData$Step currentStep;
   @NotNull
   private SurveyData surveyData;
   @NotNull
   private final MutableStateFlow state;
   @NotNull
   private final MutableSharedFlow effects;
   @NotNull
   private final List completedResponses;
   @NotNull
   private final List completedSteps;
   public static final int $stable;

   static { $stable = 8; }
   public static final int ENTITY_TYPE = 85;

   public SurveyViewModel(
      @NotNull CoroutineDispatcher var1,
      @NotNull SurveyLaunchMode var2,
      @NotNull SurveyRepository var3,
      @NotNull AppConfig var4,
      @NotNull MetricTracker var5,
      @NotNull IntercomDataLayer var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "dispatcher");
      Intrinsics.checkNotNullParameter(var2, "launchMode");
      Intrinsics.checkNotNullParameter(var3, "surveyRepository");
      Intrinsics.checkNotNullParameter(var4, "appConfig");
      Intrinsics.checkNotNullParameter(var5, "metricTracker");
      Intrinsics.checkNotNullParameter(var6, "intercomDataLayer");
      super();
      this.dispatcher = var1;
      this.launchMode = var2;
      this.surveyRepository = var3;
      this.appConfig = var4;
      this.metricTracker = var5;
      this.intercomDataLayer = var6;
      this.surveyData = SurveyData.Companion.getNULL();
      this.state = StateFlowKt.MutableStateFlow(SurveyState$Initial.INSTANCE);
      this.effects = SharedFlowKt.MutableSharedFlow$default(0, 0, null, 7, null);
      this.completedResponses = new ArrayList();
      this.completedSteps = new ArrayList();
   }

   // $VF: synthetic method
   public SurveyViewModel(
      CoroutineDispatcher var1,
      SurveyLaunchMode var2,
      SurveyRepository var3,
      AppConfig var4,
      MetricTracker var5,
      IntercomDataLayer var6,
      int var7,
      DefaultConstructorMarker var8
   ) {
      this((((var7 & 1) != 0) ? (CoroutineDispatcher) (Dispatchers.getIO()) : var1), var2, (((var7 & 4) != 0) ? (SurveyRepository) (new SurveyRepository(null, 1, null)) : var3), (((var7 & 8) != 0) ? (AppConfig) ((AppConfig)Injector.get().getAppConfigProvider().get()) : var4), (((var7 & 16) != 0) ? (MetricTracker) (Injector.get().getMetricTracker()) : var5), (((var7 & 32) != 0) ? (IntercomDataLayer) (Injector.get().getDataLayer()) : var6));
   }

   @NotNull
   public final CoroutineDispatcher getDispatcher() {
      return this.dispatcher;
   }

   @NotNull
   public final MutableStateFlow getState() {
      return this.state;
   }

   @NotNull
   public final MutableSharedFlow getEffects() {
      return this.effects;
   }

   public final void onUiLoaded() {
      if (Intrinsics.areEqual(this.state.getValue(), SurveyState$Initial.INSTANCE)) {
         BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new SurveyViewModel$onUiLoaded$1(this, null), 2, null);
      }
   }

   private final void fetchAndShowSurvey(String var1) {
      SurveyUiColors var2 = SurveyViewModelKt.toSurveyUiColors(this.surveyData.getCustomization());
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), this.dispatcher, null, new SurveyViewModel$fetchAndShowSurvey$1(var1, this, var2, null), 2, null
      );
   }

   private final SurveyState$Content buildContentState(SurveyData var1, SurveyData$Step var2) {
      SurveyUiColors var3 = SurveyViewModelKt.toSurveyUiColors(var1.getCustomization());
      float var4 = var1.getStepCount();
      int var5 = this.completedSteps.size();
      boolean var6 = var1.getShowProgressBar() ? var2.getType() == SurveyData$StepType.CONTENT || var2.getType() == SurveyData$StepType.QUESTION : false;
      ProgressBarState var7 = new ProgressBarState(var6, var5 / var4);
      SurveySenderData var10000 = var1.getSender();
      TopBarState var44;
      if ((var10000 != null ? var10000.getName() : null) == null) {
         var44 = new TopBarState$NoTopBarState(var1.isDismissible(), var3, var7);
      } else {
         Avatar$Builder var45 = new Avatar$Builder();
         SurveySenderAvatar var10001 = var1.getSender().getAvatar();
         Avatar var46 = var45.withImageUrl(var10001 != null ? var10001.getSquareImg128() : null).withInitials(var1.getSender().getInitials()).build();
         Intrinsics.checkNotNullExpressionValue(var46, "build(...)");
         Avatar var9 = var46;
         String var47 = var1.getSender().getFirstName();
         if (var47 == null) {
            var47 = var1.getSender().getName();
         }

         String var10 = var47;
         boolean var11 = var1.isDismissible();
         AppConfig var12 = this.appConfig;
         var44 = new TopBarState$SenderTopBarState(var9, var10, var12, var11, var3, var7);
      }

      TopBarState var8 = var44;
      String var26 = var2.getCustomButtonText();
      List var27 = var2.getBlocks();
      Iterable var29 = var2.getQuestions();
      int var13 = 0;
      Iterable $this$map$ivx = var29;
      ArrayList var15 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var29, 10));
      int var16 = 0;

      for (Object var18 : $this$map$ivx) {
         SurveyData$Step$Question var19 = (SurveyData$Step$Question)var18;
         Collection var25 = var15;
         int var20 = 0;
         var25.add(new QuestionState(var19.getQuestionModel(), var3));
      }

      List var28 = (List)var15;
      SurveyState$Content$PrimaryCta var48;
      switch (var2.getType()) {
         case QUESTION:
            CharSequence var36 = var26;
            var48 = var36 == null || var36.length() == 0
               ? new SurveyState$Content$PrimaryCta$Fallback(string.intercom_submit)
               : new SurveyState$Content$PrimaryCta$Custom(var26);
            break;
         case INTRO:
         case CONTENT:
            CharSequence var35 = var26;
            var48 = var35 == null || var35.length() == 0
               ? new SurveyState$Content$PrimaryCta$Fallback(string.intercom_surveys_next_button)
               : new SurveyState$Content$PrimaryCta$Custom(var26);
            break;
         case THANK_YOU:
            CharSequence var34 = var26;
            var48 = var34 == null || var34.length() == 0
               ? new SurveyState$Content$PrimaryCta$Fallback(string.intercom_surveys_done_button)
               : new SurveyState$Content$PrimaryCta$Custom(var26);
            break;
         default:
            CharSequence var33 = var26;
            var48 = var33 == null || var33.length() == 0
               ? new SurveyState$Content$PrimaryCta$Fallback(string.intercom_surveys_next_button)
               : new SurveyState$Content$PrimaryCta$Custom(var26);
      }

      SurveyState$Content$PrimaryCta var30 = var48;
      $this$map$ivx = var2.getActions();
      int $i$f$mapx = 0;
      Iterable var38 = $this$map$ivx;
      var destination$iv$ivx = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$ivx, 10));
      int $i$f$mapTox = 0;

      for (Object var42 : var38) {
         SurveyData$SurveyActions var21 = (SurveyData$SurveyActions)var42;
         Collection var43 = destination$iv$ivx;
         int var22 = 0;
         boolean var23 = var21.getAndroidUri() == null;
         String var24 = var23 ? var21.getWebUrl() : var21.getAndroidUri();
         SurveyState$Content$SecondaryCta var49;
         String var10002 = var21.getActionTitle();
         String var10003 = var24;
         if (var24 == null) {
            var10003 = "";
         }

         var49 = new SurveyState$Content$SecondaryCta(var10002, var10003, var23);
         var43.add(var49);
      }

      List var31 = (List)destination$iv$ivx;
      return new SurveyState$Content(var2, var27, var28, var31, var30, var3, var8);
   }

   public final void onCloseClicked(@NotNull CloseEventTrigger var1) {
      Intrinsics.checkNotNullParameter(var1, "triggerType");
      if (this.surveyData.isDismissible() || this.state.getValue() instanceof SurveyState$Error) {
         BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), null, null, new SurveyViewModel$onCloseClicked$1(this, var1, null), 3, null);
      }
   }

   public final void onSecondaryCtaClicked(@NotNull SurveyState$Content$SecondaryCta var1) {
      Intrinsics.checkNotNullParameter(var1, "secondaryCta");
      CloseEventTrigger var2 = var1.isExternalUrl() ? CloseEventTrigger.SECONDARY_CTA_EXTERNAL_LINK : CloseEventTrigger.SECONDARY_CTA_DEEP_LINK;
      Object var4 = this.state.getValue();
      SurveyState$Content var3 = var4 instanceof SurveyState$Content ? (SurveyState$Content)var4 : null;
      if (var2 == CloseEventTrigger.SECONDARY_CTA_DEEP_LINK && var3 != null) {
         BuildersKt.launch$default(GlobalScope.INSTANCE, this.dispatcher, null, new SurveyViewModel$onSecondaryCtaClicked$1(this, var3, null), 2, null);
      }

      this.onCloseClicked(var2);
   }

   private final void submitSurvey(SurveyState$Content var1, CoroutineScope var2) {
      BuildersKt.launch$default(ViewModelKt.getViewModelScope(this), this.dispatcher, null, new SurveyViewModel$submitSurvey$1(var1, this, var2, null), 2, null);
   }

   // $VF: synthetic method
   static void submitSurvey$default(SurveyViewModel var0, SurveyState$Content var1, CoroutineScope var2, int var3, Object var4) {
      if ((var3 & 2) != 0) {
         var2 = null;
      }

      var0.submitSurvey(var1, var2);
   }

   public final void continueClicked(@Nullable CoroutineScope var1) {
      Object var3 = this.state.getValue();
      SurveyState$Content var10000 = var3 instanceof SurveyState$Content ? (SurveyState$Content)var3 : null;
      if ((var3 instanceof SurveyState$Content ? (SurveyState$Content)var3 : null) != null) {
         SurveyState$Content var2 = var10000;
         this.submitSurvey(var2, var1);
      }
   }

   // $VF: synthetic method
   public static void continueClicked$default(SurveyViewModel var0, CoroutineScope var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = null;
      }

      var0.continueClicked(var1);
   }

   public final void onAnswerUpdated() {
      Object var3 = this.state.getValue();
      SurveyState$Content var10000 = var3 instanceof SurveyState$Content ? (SurveyState$Content)var3 : null;
      if ((var3 instanceof SurveyState$Content ? (SurveyState$Content)var3 : null) != null) {
         SurveyState$Content var1 = var10000;
         float var2 = this.surveyData.getStepCount();
         int var12 = this.completedSteps.size();
         var var5 = var1.getQuestions();
         int var6 = 0;
         int var14;
         if (var5 instanceof Collection && ((Collection)var5).isEmpty()) {
            var14 = 0;
         } else {
            int var7 = 0;

            for (Object var9 : var5) {
               QuestionState var10 = (QuestionState)var9;
               int var11 = 0;
               if (var10.getValidationError() instanceof ValidationError$NoValidationError) {
                  if (++var7 < 0) {
                     CollectionsKt.throwCountOverflow();
                  }
               }
            }

            var14 = var7;
         }

         int var4 = var14;
         float var13 = var12 / var2 + var4 / (var2 * var1.getCurrentStep().getQuestions().size());
         this.state
            .setValue(
               SurveyState$Content.copy$default(
                  var1,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  var1.getTopBarState().withProgressBarState(ProgressBarState.copy$default(var1.getTopBarState().getProgressBarState(), false, var13, 1, null)),
                  63,
                  null
               )
            );
      }
   }

   private final Object reportFailure(String var1, String var2, Continuation var3) {
      SurveyViewModel$reportFailure$1 var5;
      label20: {
         if (var3 instanceof SurveyViewModel$reportFailure$1) {
            var5 = (SurveyViewModel$reportFailure$1)var3;
            if ((var5.label & -2147483648) != 0) {
               var5.label -= Integer.MIN_VALUE;
               break label20;
            }
         }

         var5 = new SurveyViewModel$reportFailure$1(this, var3);
      }

      Object var4 = var5.result;
      Object var6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (var5.label) {
         case 0:
            ResultKt.throwOnFailure(var4);
            SurveyRepository var10000 = this.surveyRepository;
            var5.label = 1;
            if (var10000.reportFailure(var1, var2, var5) == var6) {
               return var6;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var4);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      return Unit.INSTANCE;
   }

   private final void emitLoading() {
      MutableStateFlow var10000 = this.state;
      TopBarState var1 = ((SurveyState)this.state.getValue()).getTopBarState();
      SurveyUiColors var2 = ((SurveyState)this.state.getValue()).getSurveyUiColors();
      var10000.setValue(new SurveyState$Loading(var2, var1));
   }

   private final void sendDismissedEvent(CloseEventTrigger var1) {
      SurveyState var3 = (SurveyState)this.state.getValue();
      String var2 = var3 instanceof SurveyState$Loading ? "step_load" : (var3 instanceof SurveyState$Error ? "step_error" : "survey");
      String var10000;
      switch (var1) {
         case CTA:
            var10000 = "from_cta";
            break;
         case CLOSE_BUTTON:
            var10000 = "from_close_button";
            break;
         case SECONDARY_CTA_EXTERNAL_LINK:
            var10000 = "from_onward_external_link";
            break;
         case SECONDARY_CTA_DEEP_LINK:
            var10000 = "from_onward_deep_link";
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      String var15 = var10000;
      SurveyData$Step var4 = this.currentStep;
      if (!this.surveyData.getSteps().isEmpty() && var4 != null) {
         String var5 = var4.getType() == SurveyData$StepType.THANK_YOU ? "closed" : "dismissed";
         Iterable var7 = this.completedSteps;
         int var8 = 0;

         for (Object var10 : var7) {
            SurveyData$Step var11 = (SurveyData$Step)var10;
            int var13 = var8;
            int var12 = 0;
            int var14 = var11.getQuestions().size();
            var8 = var13 + var14;
         }

         int var6 = var8 + var4.getQuestions().size();
         this.metricTracker
            .surveyDismissed(
               var5, var2, var15, this.surveyData.getId(), this.surveyData.getFormatMetric(), this.completedSteps.size(), this.surveyData.getStepCount(), var6
            );
      }
   }

   // $VF: synthetic method
   public static final SurveyLaunchMode access$getLaunchMode$p(SurveyViewModel var0) {
      return var0.launchMode;
   }

   // $VF: synthetic method
   public static final void access$setSurveyData$p(SurveyViewModel var0, SurveyData var1) {
      var0.surveyData = var1;
   }

   // $VF: synthetic method
   public static final SurveyData access$getSurveyData$p(SurveyViewModel var0) {
      return var0.surveyData;
   }

   // $VF: synthetic method
   public static final MetricTracker access$getMetricTracker$p(SurveyViewModel var0) {
      return var0.metricTracker;
   }

   // $VF: synthetic method
   public static final void access$setCurrentStep$p(SurveyViewModel var0, SurveyData$Step var1) {
      var0.currentStep = var1;
   }

   // $VF: synthetic method
   public static final SurveyState$Content access$buildContentState(SurveyViewModel var0, SurveyData var1, SurveyData$Step var2) {
      return var0.buildContentState(var1, var2);
   }

   // $VF: synthetic method
   public static final void access$fetchAndShowSurvey(SurveyViewModel var0, String var1) {
      var0.fetchAndShowSurvey(var1);
   }

   // $VF: synthetic method
   public static final void access$emitLoading(SurveyViewModel var0) {
      var0.emitLoading();
   }

   // $VF: synthetic method
   public static final SurveyRepository access$getSurveyRepository$p(SurveyViewModel var0) {
      return var0.surveyRepository;
   }

   // $VF: synthetic method
   public static final void access$sendDismissedEvent(SurveyViewModel var0, CloseEventTrigger var1) {
      var0.sendDismissedEvent(var1);
   }

   // $VF: synthetic method
   public static final SurveyData$Step access$getCurrentStep$p(SurveyViewModel var0) {
      return var0.currentStep;
   }

   // $VF: synthetic method
   public static final Object access$reportFailure(SurveyViewModel var0, String var1, String var2, Continuation var3) {
      return var0.reportFailure(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final IntercomDataLayer access$getIntercomDataLayer$p(SurveyViewModel var0) {
      return var0.intercomDataLayer;
   }

   // $VF: synthetic method
   public static final List access$getCompletedResponses$p(SurveyViewModel var0) {
      return var0.completedResponses;
   }

   // $VF: synthetic method
   public static final List access$getCompletedSteps$p(SurveyViewModel var0) {
      return var0.completedSteps;
   }

   // $VF: synthetic method
   public static final void access$submitSurvey(SurveyViewModel var0, SurveyState$Content var1, CoroutineScope var2) {
      var0.submitSurvey(var1, var2);
   }
}
