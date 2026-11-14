package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsUiState$Content;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.Phrase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHelpCenterCollectionsScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt$helpCenterCollectionItems$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,131:1\n827#2:132\n855#2,2:133\n75#3:135\n118#4:136\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt$helpCenterCollectionItems$1\n*L\n93#1:132\n93#1:133,2\n99#1:135\n107#1:136\n*E\n"
)
final class HelpCenterCollectionsScreenKt$helpCenterCollectionItems$1 implements Function3 {
   // $VF: synthetic field
   final CollectionsUiState$Content $state;

   HelpCenterCollectionsScreenKt$helpCenterCollectionItems$1(CollectionsUiState$Content var1) {
      this.$state = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(var2, "C107@4173L10,103@4030L215,110@4254L17:HelpCenterCollectionsScreen.kt#ln9mxm");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -464708772, var3, -1, "io.intercom.android.sdk.m5.helpcenter.ui.helpCenterCollectionItems.<anonymous> (HelpCenterCollectionsScreen.kt:92)"
            );
         }

         Iterable var5 = this.$state.getCollections();
         int var6 = 0;
         Iterable var7 = var5;
         ArrayList var8 = new ArrayList();
         int var9 = 0;

         for (Object var11 : var7) {
            CollectionsRow var12 = (CollectionsRow)var11;
            int var13 = 0;
            if (!(var12 instanceof CollectionsRow$SendMessageRow)) {
               var8.add(var11);
            }
         }

         int var4 = ((List)var8).size();
         String var10000;
         if (var4 == 1) {
            var2.startReplaceGroup(765329843);
            ComposerKt.sourceInformation(var2, "95@3694L56");
            String var15 = StringResources_androidKt.stringResource(string.intercom_single_collection, var2, 0);
            var2.endReplaceGroup();
            var10000 = var15;
         } else {
            var2.startReplaceGroup(765428144);
            ComposerKt.sourceInformation(var2, "98@3834L7");
            CompositionLocal var18 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var21 = 0;
            var9 = 0;
            ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var24 = var2.consume(var18);
            ComposerKt.sourceInformationMarkerEnd(var2);
            String var16 = Phrase.from((Context)var24, string.intercom_multiple_collections).put("total_collection", var4).format().toString();
            var2.endReplaceGroup();
            var10000 = var16;
         }

         String var14 = var10000;
         Modifier var25 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var19 = 16;
         int var22 = 0;
         Modifier var17 = PaddingKt.recovered$eb6a744cd98cdbca(var25, Dp.recovered$95b3d103663bf060(var19));
         TextStyle var20 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04SemiBold();
         TextKt.recovered$5b114ab32a4f4190(var14, var17, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var20, var2, 48, 0, 131068);
         IntercomDividerKt.IntercomDivider(null, var2, 0, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
