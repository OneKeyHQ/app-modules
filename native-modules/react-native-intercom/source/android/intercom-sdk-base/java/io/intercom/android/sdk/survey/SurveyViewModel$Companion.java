package io.intercom.android.sdk.survey;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000+\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u0003*\u0001\r\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bJ\u0015\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u000bH\u0002¢\u0006\u0002\u0010\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000f",
   d2 = {
         "Lio/intercom/android/sdk/survey/SurveyViewModel$Companion;",
         "",
         "<init>",
         "()V",
         "ENTITY_TYPE",
         "",
         "create",
         "Lio/intercom/android/sdk/survey/SurveyViewModel;",
         "owner",
         "Landroidx/lifecycle/ViewModelStoreOwner;",
         "launchMode",
         "Lio/intercom/android/sdk/survey/SurveyLaunchMode;",
         "factory",
         "io/intercom/android/sdk/survey/SurveyViewModel$Companion$factory$1",
         "(Lio/intercom/android/sdk/survey/SurveyLaunchMode;)Lio/intercom/android/sdk/survey/SurveyViewModel$Companion$factory$1;",
         "intercom-sdk-base_release"
   }
)
public final class SurveyViewModel$Companion {
   private SurveyViewModel$Companion() {
   }

   @NotNull
   public final SurveyViewModel create(@NotNull ViewModelStoreOwner var1, @NotNull SurveyLaunchMode var2) {
      Intrinsics.checkNotNullParameter(var1, "owner");
      Intrinsics.checkNotNullParameter(var2, "launchMode");
      return (SurveyViewModel)new ViewModelProvider(var1, this.factory(var2)).get(SurveyViewModel.class);
   }

   private final SurveyViewModel$Companion$factory$1 factory(SurveyLaunchMode var1) {
      return new SurveyViewModel$Companion$factory$1(var1);
   }

   // $VF: synthetic method
   public SurveyViewModel$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
