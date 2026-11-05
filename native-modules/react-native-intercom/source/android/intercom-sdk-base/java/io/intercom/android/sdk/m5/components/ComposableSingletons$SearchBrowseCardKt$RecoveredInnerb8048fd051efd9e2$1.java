package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.HomeCardType;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData;
import io.intercom.android.sdk.m5.home.data.SuggestedArticle;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Avatar;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
final class ComposableSingletons$SearchBrowseCardKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$SearchBrowseCardKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$SearchBrowseCardKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C226@9664L881:SearchBrowseCard.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               130670641,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$SearchBrowseCardKt.lambda-3.<anonymous> (SearchBrowseCard.kt:226)"
            );
         }

         HomeCardType var10003 = HomeCardType.HELP_CENTER;
         SuggestedArticle[] var3 = new SuggestedArticle[]{
            new SuggestedArticle("", "How to restart recording", ""),
            new SuggestedArticle("", "How to track your recording time", ""),
            new SuggestedArticle("", "How to correct your transcript", "")
         };
         HomeCards$HomeHelpCenterData var10000 = new HomeCards$HomeHelpCenterData("Help Center", var10003, CollectionsKt.listOf(var3));
         AvatarWrapper[] var4 = new AvatarWrapper[3];
         Avatar var10006 = Avatar.create("", "VR");
         Intrinsics.checkNotNullExpressionValue(var10006, "create(...)");
         var4[0] = new AvatarWrapper(var10006, false, 2, null);
         var10006 = Avatar.create("", "PR");
         Intrinsics.checkNotNullExpressionValue(var10006, "create(...)");
         var4[1] = new AvatarWrapper(var10006, false, 2, null);
         var10006 = Avatar.create("", "SK");
         Intrinsics.checkNotNullExpressionValue(var10006, "create(...)");
         var4[2] = new AvatarWrapper(var10006, false, 2, null);
         List var10002 = CollectionsKt.listOf(var4);
         MetricTracker var10004 = Injector.get().getMetricTracker();
         Intrinsics.checkNotNullExpressionValue(var10004, "getMetricTracker(...)");
         SearchBrowseCardKt.SearchBrowseCard(var10000, false, var10002, true, var10004, var1, 3120);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
