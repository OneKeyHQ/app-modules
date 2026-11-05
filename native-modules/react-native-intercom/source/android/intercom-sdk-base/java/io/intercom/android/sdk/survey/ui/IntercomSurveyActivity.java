package io.intercom.android.sdk.survey.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.activity.compose.ComponentActivityKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.LifecycleOwnerKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.activities.IntercomBaseActivity;
import io.intercom.android.sdk.survey.SurveyLaunchMode;
import io.intercom.android.sdk.survey.SurveyLaunchMode$Automatic;
import io.intercom.android.sdk.survey.SurveyLaunchMode$Programmatic;
import io.intercom.android.sdk.survey.SurveyViewModel;
import io.intercom.android.sdk.survey.model.SurveyData;
import io.intercom.android.sdk.utilities.PhoneNumberValidator;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u000e\u001a\u00020\tH\u0002J\u0012\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0014J\b\u0010\u0013\u001a\u00020\u0010H\u0014R\u0018\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u001b\u0010\b\u001a\u00020\t8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\f\u0010\r\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/survey/ui/IntercomSurveyActivity;",
         "Lio/intercom/android/sdk/activities/IntercomBaseActivity;",
         "<init>",
         "()V",
         "injector",
         "Lio/intercom/android/sdk/Injector;",
         "kotlin.jvm.PlatformType",
         "Lio/intercom/android/sdk/Injector;",
         "viewModel",
         "Lio/intercom/android/sdk/survey/SurveyViewModel;",
         "getViewModel",
         "()Lio/intercom/android/sdk/survey/SurveyViewModel;",
         "viewModel$delegate",
         "Lkotlin/Lazy;",
         "createVM",
         "onCreate",
         "",
         "savedInstanceState",
         "Landroid/os/Bundle;",
         "onStart",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nIntercomSurveyActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomSurveyActivity.kt\nio/intercom/android/sdk/survey/ui/IntercomSurveyActivity\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,96:1\n1#2:97\n*E\n"
)
public final class IntercomSurveyActivity extends IntercomBaseActivity {
   @NotNull
   public static final IntercomSurveyActivity$Companion Companion = new IntercomSurveyActivity$Companion(null);
   private final Injector injector = Injector.get();
   @NotNull
   private final Lazy viewModel$delegate = LazyKt.lazy(((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.survey.ui.IntercomSurveyActivity.viewModel_delegate$lambda$0(this)));
   public static final int $stable;

   static { $stable = 8; }
   @NotNull
   private static final String PARCEL_SURVEY_ID = "parcel_survey_id";

   private final SurveyViewModel getViewModel() {
      Lazy var1 = this.viewModel$delegate;
      return (SurveyViewModel)var1.getValue();
   }

   private final SurveyViewModel createVM() {
      String var10000 = this.getIntent().getStringExtra("parcel_survey_id");
      SurveyLaunchMode var5;
      if (var10000 != null) {
         String var2 = var10000;
         int var3 = 0;
         int var4 = 0;
         var5 = new SurveyLaunchMode$Programmatic(var2);
      } else {
         var5 = new SurveyLaunchMode$Automatic((SurveyData)this.injector.getDataLayer().getSurveyData().getValue());
      }

      SurveyLaunchMode var1 = var5;
      return SurveyViewModel.Companion.create(this, var1);
   }

   @Override
   protected void onCreate(@Nullable Bundle var1) {
      EdgeToEdge.enable$default(this, null, null, 3, null);
      super.onCreate(var1);
      this.getOnBackPressedDispatcher().addCallback(new IntercomSurveyActivity$onCreate$1());
      PhoneNumberValidator.loadCountryAreaCodes(this);
      ComponentActivityKt.setContent$default(
         this, null, ComposableLambdaKt.composableLambdaInstance(-179321000, true, new IntercomSurveyActivity$onCreate$2(this)), 1, null
      );
   }

   @Override
   protected void onStart() {
      super.onStart();
      BuildersKt.launch$default(LifecycleOwnerKt.getLifecycleScope(this), null, null, new IntercomSurveyActivity$onStart$1(this, null), 3, null);
      this.getViewModel().onUiLoaded();
   }

   private static final SurveyViewModel viewModel_delegate$lambda$0(IntercomSurveyActivity var0) {
      return var0.createVM();
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final Intent buildIntent(@NotNull Context var0, @Nullable String var1) {
      return Companion.buildIntent(var0, var1);
   }

   @JvmStatic
   @JvmOverloads
   @NotNull
   public static final Intent buildIntent(@NotNull Context var0) {
      return Companion.buildIntent(var0);
   }

   // $VF: synthetic method
   public static final SurveyViewModel access$getViewModel(IntercomSurveyActivity var0) {
      return var0.getViewModel();
   }

   // $VF: synthetic method
   public static final Injector access$getInjector$p(IntercomSurveyActivity var0) {
      return var0.injector;
   }
}
