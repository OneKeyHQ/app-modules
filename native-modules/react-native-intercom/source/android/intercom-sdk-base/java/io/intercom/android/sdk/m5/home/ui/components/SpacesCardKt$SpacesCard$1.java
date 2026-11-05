package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.HomeItemKt;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeSpacesData;
import io.intercom.android.sdk.m5.home.data.SpaceItem;
import io.intercom.android.sdk.m5.home.data.SpaceItemType;
import io.intercom.android.sdk.m5.home.reducers.HomeItemBadgeReducerKt;
import io.intercom.android.sdk.m5.home.states.HomeItemBadge;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nSpacesCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpacesCard.kt\nio/intercom/android/sdk/m5/home/ui/components/SpacesCardKt$SpacesCard$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,48:1\n87#2:49\n83#2,10:50\n94#2:98\n81#3,6:60\n88#3,6:75\n96#3:97\n402#4,9:66\n411#4:81\n412#4,2:95\n774#5:82\n865#5,2:83\n1878#5,2:85\n1880#5:94\n1047#6,6:87\n118#7:93\n*S KotlinDebug\n*F\n+ 1 SpacesCard.kt\nio/intercom/android/sdk/m5/home/ui/components/SpacesCardKt$SpacesCard$1\n*L\n23#1:49\n23#1:50,10\n23#1:98\n23#1:60,6\n23#1:75,6\n23#1:97\n23#1:66,9\n23#1:81\n23#1:95,2\n24#1:82\n24#1:83,2\n25#1:85,2\n25#1:94\n34#1:87,6\n41#1:93\n*E\n"
)
final class SpacesCardKt$SpacesCard$1 implements Function3 {
   // $VF: synthetic field
   final HomeCards$HomeSpacesData $homeSpacesData;
   // $VF: synthetic field
   final Function1 $onItemClick;

   SpacesCardKt$SpacesCard$1(HomeCards$HomeSpacesData var1, Function1 var2) {
      this.$homeSpacesData = var1;
      this.$onItemClick = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C22@864L1002:SpacesCard.kt#utrtal");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1212336956, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.SpacesCard.<anonymous> (SpacesCard.kt:22)");
         }

         HomeCards$HomeSpacesData var7 = this.$homeSpacesData;
         Function1 var8 = this.$onItemClick;
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var4;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2020445794, "C:SpacesCard.kt#utrtal");
         Iterable var33 = var7.getSpaceItems();
         int var34 = 0;
         Iterable var35 = var33;
         ArrayList var36 = new ArrayList();
         int var37 = 0;

         for (Object var39 : var35) {
            SpaceItem var40 = (SpaceItem)var39;
            int var41 = 0;
            if (ArraysKt.contains(SpaceItemType.values(), var40.getType())) {
               var36.add(var39);
            }
         }

         List var42 = (List)var36;
         var30.startReplaceGroup(-1735936299);
         ComposerKt.sourceInformation(var30, "*33@1466L26,30@1348L224");
         var33 = var42;
         var34 = 0;
         int var65 = 0;

         for (Object var67 : var33) {
            int var68 = var65++;
            if (var68 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            SpaceItem var69 = (SpaceItem)var67;
            int var70 = var68;
            int var71 = 0;
            int var74;
            switch (var69.getType()) {
               case MESSAGES:
                  var74 = drawable.intercom_messages_icon;
                  break;
               case HELP:
                  var74 = drawable.intercom_help_centre_icon;
                  break;
               case TICKETS:
                  var74 = drawable.intercom_ticket_detail_icon;
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }

            int var43 = var74;
            String var44 = var69.getLabel();
            HomeItemBadge var45 = HomeItemBadgeReducerKt.reduceHomeItemBadge(var69.getBadge());
            Modifier var75 = null;
            Integer var10001 = var43;
            Object var10002 = null;
            Object var10003 = null;
            Object var10004 = null;
            String var10005 = var44;
            Object var10006 = null;
            HomeItemBadge var10007 = var45;
            var30.startReplaceGroup(-1302667345);
            ComposerKt.sourceInformation(var30, "CC(remember):SpacesCard.kt#9igjgp");
            Composer var46 = var30;
            boolean var47 = var30.changed(var8) | var30.changed(var69);
            int var48 = 0;
            Object var49 = var46.rememberedValue();
            int var50 = 0;
            Object var10008;
            if (!var47 && var49 != Composer.Companion.getEmpty()) {
               var10008 = var49;
            } else {
               HomeItemBadge var51 = var45;
               Object var52 = null;
               String var53 = var44;
               Object var54 = null;
               Object var55 = null;
               Object var56 = null;
               Integer var57 = var10001;
               Object var58 = null;
               int var59 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_927_0 = var8;
               final io.intercom.android.sdk.m5.home.data.SpaceItem recoveredCapture_927_1 = var69;
               Function0 var60 = ((kotlin.jvm.functions.Function0) () -> SpacesCardKt$SpacesCard$1.invoke$lambda$4$lambda$3$lambda$2$lambda$1(recoveredCapture_927_0, recoveredCapture_927_1));
               var75 = (Modifier)var58;
               var10001 = var57;
               var10002 = var56;
               var10003 = var55;
               var10004 = var54;
               var10005 = var53;
               var10006 = var52;
               var10007 = var51;
               Object var61 = var60;
               var46.updateRememberedValue(var61);
               var10008 = var61;
            }

            Function0 var62 = (Function0)var10008;
            var30.endReplaceGroup();
            HomeItemKt.HomeItem(
               var75, var10001, (Function2)var10002, (Modifier)var10003, (Integer)var10004, var10005, (String)var10006, var10007, var62, var30, 0, 93
            );
            var30.startReplaceGroup(-1735917837);
            ComposerKt.sourceInformation(var30, "37@1640L184");
            if (var70 != var42.size() - 1) {
               var75 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int var72 = 16;
               int var73 = 0;
               IntercomDividerKt.IntercomDivider(PaddingKt.recovered$f3b9f2e94c07c806(var75, Dp.recovered$95b3d103663bf060(var72), 0.0F, 2, null), var30, 6, 0);
            }

            var30.endReplaceGroup();
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$2$lambda$1(Function1 var0, SpaceItem var1) {
      var0.invoke(var1.getType());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
