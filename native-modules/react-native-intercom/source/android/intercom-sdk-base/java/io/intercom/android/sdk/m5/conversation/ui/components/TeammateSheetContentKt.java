package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt;
import io.intercom.android.sdk.m5.conversation.ui.components.row.SpecialNoticeKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a+\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "TeammateSheetContent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "specialNoticeMessage",
         "",
         "teamPresenceState",
         "Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;Landroidx/compose/runtime/Composer;II)V",
         "TeammateSheetContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTeammateSheetContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeammateSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/TeammateSheetContentKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,78:1\n118#2:79\n87#3:80\n85#3,8:81\n94#3:114\n81#4,6:89\n88#4,6:104\n96#4:113\n402#5,9:95\n411#5,3:110\n*S KotlinDebug\n*F\n+ 1 TeammateSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/TeammateSheetContentKt\n*L\n28#1:79\n26#1:80\n26#1:81,8\n26#1:114\n26#1:89,6\n26#1:104,6\n26#1:113\n26#1:95,9\n26#1:110,3\n*E\n"
)
public final class TeammateSheetContentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeammateSheetContent(
      @Nullable Modifier var0, @Nullable String var1, @NotNull ExpandedTeamPresenceState var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var2, "teamPresenceState");
      var3 = var3.startRestartGroup(755089345);
      ComposerKt.sourceInformation(var3, "C(TeammateSheetContent)25@1121L374:TeammateSheetContent.kt#o19g89");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 2) != 0) {
            var1 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               755089345, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.TeammateSheetContent (TeammateSheetContent.kt:24)"
            );
         }

         int var7 = 16;
         int var8 = 0;
         Vertical var35 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var7));
         int var9 = 48 | 14 & var6;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var3, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var36 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var35, var36, var3, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var0;
         MeasurePolicy var13 = var11;
         Composer var14 = var3;
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
         ComposerKt.sourceInformationMarkerStart(var30, -1546870991, "C29@1233L138:TeammateSheetContent.kt#o19g89");
         Modifier var33 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayout(var2, var33, var30, 48 | 14 & var6 >> 6, 0);
         var30.startReplaceGroup(-1851009825);
         ComposerKt.sourceInformation(var30, "34@1437L42");
         CharSequence var37 = var1;
         if (var37 != null && var37.length() != 0) {
            SpecialNoticeKt.SpecialNotice(var1, null, var30, 14 & var6 >> 3, 2);
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var3);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_486_0 = var0;
         final java.lang.String recoveredCapture_486_1 = var1;
         final io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState recoveredCapture_486_2 = var2;
         final int recoveredCapture_486_3 = var4;
         final int recoveredCapture_486_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_486_0, recoveredArgument_486_1) -> TeammateSheetContentKt.TeammateSheetContent$lambda$1(recoveredCapture_486_0, recoveredCapture_486_1, recoveredCapture_486_2, recoveredCapture_486_3, recoveredCapture_486_4, (androidx.compose.runtime.Composer)recoveredArgument_486_0, ((java.lang.Integer)recoveredArgument_486_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeammateSheetContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(223292015);
      ComposerKt.sourceInformation(var0, "C(TeammateSheetContentPreview)42@1578L1326:TeammateSheetContent.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               223292015, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.TeammateSheetContentPreview (TeammateSheetContent.kt:41)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TeammateSheetContentKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_487_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_487_0, recoveredArgument_487_1) -> TeammateSheetContentKt.TeammateSheetContentPreview$lambda$2(recoveredCapture_487_0, (androidx.compose.runtime.Composer)recoveredArgument_487_0, ((java.lang.Integer)recoveredArgument_487_1).intValue())));
      }
   }

   private static final Unit TeammateSheetContent$lambda$1(
      Modifier var0, String var1, ExpandedTeamPresenceState var2, int var3, int var4, Composer var5, int var6
   ) {
      TeammateSheetContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit TeammateSheetContentPreview$lambda$2(int var0, Composer var1, int var2) {
      TeammateSheetContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
