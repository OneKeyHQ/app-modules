package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.FunctionKeyMeta;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$BrowseAllHelpTopicsAsListRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$CollectionRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$FullHelpCenterRow;
import io.intercom.android.sdk.m5.helpcenter.states.CollectionsRow$SendMessageRow;
import io.intercom.android.sdk.m5.helpcenter.ui.components.BrowseAllHelpTopicsComponentKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.CollectionRowComponentKt;
import io.intercom.android.sdk.m5.helpcenter.ui.components.TeamPresenceComponentKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLazyDsl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LazyDsl.kt\nandroidx/compose/foundation/lazy/LazyDslKt$itemsIndexed$4\n+ 2 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,523:1\n115#2,3:524\n119#2,11:528\n118#3:527\n*S KotlinDebug\n*F\n+ 1 HelpCenterCollectionsScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterCollectionsScreenKt\n*L\n117#1:527\n*E\n"
)
public final class HelpCenterCollectionsScreenKt$helpCenterCollectionItems$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3
   implements Function4 {
   // $VF: synthetic field
   final List $items;
   // $VF: synthetic field
   final Function1 $onCollectionClick$inlined;

   public HelpCenterCollectionsScreenKt$helpCenterCollectionItems$RecoveredInnere3b0c44298fc1c14$inlined$itemsIndexed$RecoveredInner37a8eec1ce19687d$3(
      List var1, Function1 var2
   ) {
      this.$items = var1;
      this.$onCollectionClick$inlined = var2;
   }

   @Composable
   @FunctionKeyMeta(key = 2039820996, startOffset = 10658, endOffset = 10700)
   public final void invoke(LazyItemScope var1, int var2, Composer var3, int var4) {
      ComposerKt.sourceInformation(var3, "CN(it)214@10668L26:LazyDsl.kt#428nma");
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var1) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changed(var2) ? 32 : 16;
      }

      if (var3.shouldExecute((var5 & 147) != 146, var5 & 1)) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2039820996, var5, -1, "androidx.compose.foundation.lazy.itemsIndexed.<anonymous> (LazyDsl.kt:214)");
         }

         Object var10001 = this.$items.get(var2);
         int var6 = 14 & var5 | 112 & var5;
         Composer var7 = var3;
         CollectionsRow var8 = (CollectionsRow)var10001;
         int var10 = 0;
         var7.startReplaceGroup(766027716);
         ComposerKt.sourceInformation(var7, "C:HelpCenterCollectionsScreen.kt#ln9mxm");
         CollectionsRow var11 = var8;
         if (Intrinsics.areEqual(var11, CollectionsRow$FullHelpCenterRow.INSTANCE)) {
            var7.startReplaceGroup(766073285);
            ComposerKt.sourceInformation(var7, "116@4443L70");
            Modifier var10000 = Modifier.Companion;
            int var12 = 24;
            int var13 = 0;
            BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsComponent(
               PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var12), 0.0F, 0.0F, 13, null), var7, 6, 0
            );
            var7.endReplaceGroup();
         } else if (var11 instanceof CollectionsRow$SendMessageRow) {
            var7.startReplaceGroup(766223604);
            ComposerKt.sourceInformation(var7, "119@4594L87");
            TeamPresenceComponentKt.TeamPresenceComponent(((CollectionsRow$SendMessageRow)var8).getTeamPresenceState(), false, null, var7, 48, 4);
            var7.endReplaceGroup();
         } else if (var11 instanceof CollectionsRow$CollectionRow) {
            var7.startReplaceGroup(766388896);
            ComposerKt.sourceInformation(var7, "122@4761L75");
            CollectionRowComponentKt.CollectionRowComponent(
               ((CollectionsRow$CollectionRow)var8).getRowData(), this.$onCollectionClick$inlined, null, var7, 0, 4
            );
            var7.endReplaceGroup();
         } else {
            if (!Intrinsics.areEqual(var11, CollectionsRow$BrowseAllHelpTopicsAsListRow.INSTANCE)) {
               var7.startReplaceGroup(-390931425);
               var7.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var7.startReplaceGroup(766553072);
            ComposerKt.sourceInformation(var7, "125@4928L27");
            BrowseAllHelpTopicsComponentKt.BrowseAllHelpTopicsAsItem(null, var7, 0, 1);
            var7.endReplaceGroup();
         }

         var7.endReplaceGroup();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      } else {
         var3.skipToGroupEnd();
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((LazyItemScope)var1, ((Number)var2).intValue(), (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
