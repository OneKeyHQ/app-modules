package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.TextKt;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\u001a%\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0001¢\u0006\u0002\u0010\u0007\u001a\r\u0010\b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\t¨\u0006\n",
   d2 = {
         "TeamPresenceRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Landroidx/compose/runtime/Composer;II)V",
         "TeamPresenceRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTeamPresenceRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeamPresenceRow.kt\nio/intercom/android/sdk/m5/components/TeamPresenceRowKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,57:1\n118#2:58\n118#2:91\n99#3:59\n96#3,9:60\n106#3:95\n81#4,6:69\n88#4,6:84\n96#4:94\n402#5,9:75\n411#5:90\n412#5,2:92\n*S KotlinDebug\n*F\n+ 1 TeamPresenceRow.kt\nio/intercom/android/sdk/m5/components/TeamPresenceRowKt\n*L\n24#1:58\n36#1:91\n22#1:59\n22#1:60,9\n22#1:95\n22#1:69,6\n22#1:84,6\n22#1:94\n22#1:75,9\n22#1:90\n22#1:92,2\n*E\n"
)
public final class TeamPresenceRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TeamPresenceRow(@Nullable Modifier var0, @NotNull List var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "avatars");
      var2 = var2.startRestartGroup(1370953565);
      ComposerKt.sourceInformation(var2, "C(TeamPresenceRow)P(1)21@879L600:TeamPresenceRow.kt#arqjag");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1370953565, var5, -1, "io.intercom.android.sdk.m5.components.TeamPresenceRow (TeamPresenceRow.kt:20)");
         }

         int var6 = 16;
         int var7 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var6);
         var6 = 12;
         var7 = 0;
         Modifier var38 = TestTagKt.testTag(PaddingKt.recovered$904403cea4c49d95(var0, var10001, Dp.recovered$95b3d103663bf060(var6)), "team_presence_row");
         Vertical var8 = Alignment.Companion.getCenterVertically();
         short var9 = 384;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var40 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var40, var8, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var38;
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
         ComposerKt.sourceInformationMarkerStart(var27, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1596240933, "C29@1155L66,30@1257L10,27@1086L285,33@1380L93:TeamPresenceRow.kt#arqjag");
         Modifier var33 = RowScope.weight$default(var31, Modifier.Companion, 1.0F, false, 2, null);
         String var34 = StringResources_androidKt.stringResource(string.intercom_the_team_can_help_if_needed, var30, 0);
         TextStyle var35 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04Point5(),
            ColorExtensionsKt.toComposeColor$default("#757575", 0.0F, 1, null),
            0L,
            null,
            null,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16777214,
            null
         );
         TextKt.recovered$5b114ab32a4f4190(var34, var33, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var35, var30, 0, 0, 131068);
         List var10000 = CollectionsKt.take(var1, 3);
         int $this$dp$ivx = 24;
         int $i$f$getDpx = 0;
         AvatarGroupKt.recovered$813c81fac421be56(var10000, null, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0L, var30, 384, 10);
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

      ScopeUpdateScope var43 = var2.endRestartGroup();
      if (var43 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_242_0 = var0;
         final java.util.List recoveredCapture_242_1 = var1;
         final int recoveredCapture_242_2 = var3;
         final int recoveredCapture_242_3 = var4;
         var43.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_242_0, recoveredArgument_242_1) -> TeamPresenceRowKt.TeamPresenceRow$lambda$1(recoveredCapture_242_0, recoveredCapture_242_1, recoveredCapture_242_2, recoveredCapture_242_3, (androidx.compose.runtime.Composer)recoveredArgument_242_0, ((java.lang.Integer)recoveredArgument_242_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TeamPresenceRowPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1211328616);
      ComposerKt.sourceInformation(var0, "C(TeamPresenceRowPreview)43@1556L421:TeamPresenceRow.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1211328616, var1, -1, "io.intercom.android.sdk.m5.components.TeamPresenceRowPreview (TeamPresenceRow.kt:42)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TeamPresenceRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_243_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_243_0, recoveredArgument_243_1) -> TeamPresenceRowKt.TeamPresenceRowPreview$lambda$2(recoveredCapture_243_0, (androidx.compose.runtime.Composer)recoveredArgument_243_0, ((java.lang.Integer)recoveredArgument_243_1).intValue())));
      }
   }

   private static final Unit TeamPresenceRow$lambda$1(Modifier var0, List var1, int var2, int var3, Composer var4, int var5) {
      TeamPresenceRow(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TeamPresenceRowPreview$lambda$2(int var0, Composer var1, int var2) {
      TeamPresenceRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
