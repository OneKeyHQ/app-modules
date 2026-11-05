package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
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
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
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
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u001f\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006\u001a\r\u0010\u0007\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "DayDivider",
         "",
         "text",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/lang/String;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "DayDividerPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nDayDivider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DayDivider.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/DayDividerKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,48:1\n118#2:49\n99#3,6:50\n106#3:81\n81#4,6:56\n88#4,6:71\n96#4:80\n402#5,9:62\n411#5,3:77\n*S KotlinDebug\n*F\n+ 1 DayDivider.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/DayDividerKt\n*L\n30#1:49\n21#1:50,6\n21#1:81\n21#1:56,6\n21#1:71,6\n21#1:80\n21#1:62,9\n21#1:77,3\n*E\n"
)
public final class DayDividerKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DayDivider(@NotNull String var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "text");
      var2 = var2.startRestartGroup(600554765);
      ComposerKt.sourceInformation(var2, "C(DayDivider)P(1)26@950L6,27@1016L6,20@715L509:DayDivider.kt#hr3bi5");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(600554765, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.DayDivider (DayDivider.kt:19)");
         }

         HorizontalOrVertical var6 = Arrangement.INSTANCE.getCenter();
         Vertical var7 = Alignment.Companion.getCenterVertically();
         Modifier var10000 = BackgroundKt.recovered$60bc47fb440df483(
            SizeKt.wrapContentSize$default(var1, null, false, 3, null),
            IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$4f0eb35b16b89a87(),
            IntercomTheme.INSTANCE.getShapes(var2, IntercomTheme.$stable).getSmall()
         );
         int var9 = 12;
         int var10 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var9);
         var9 = 4;
         var10 = 0;
         Modifier var8 = PaddingKt.recovered$904403cea4c49d95(var10000, var10001, Dp.recovered$95b3d103663bf060(var9));
         short var35 = 432;
         var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var6, var7, var2, 14 & var35 >> 3 | 112 & var35 >> 3);
         Modifier var12 = var8;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var35 << 3;
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
         int var29 = 6 | 112 & var35 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 733465145, "C33@1184L10,31@1119L99:DayDivider.kt#hr3bi5");
         TextKt.recovered$5b114ab32a4f4190(
            var0,
            null,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            0L,
            0,
            false,
            0,
            0,
            null,
            IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04Point5(),
            var30,
            14 & var5,
            0,
            131070
         );
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

      ScopeUpdateScope var38 = var2.endRestartGroup();
      if (var38 != null) {
         final java.lang.String recoveredCapture_699_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_699_1 = var1;
         final int recoveredCapture_699_2 = var3;
         final int recoveredCapture_699_3 = var4;
         var38.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_699_0, recoveredArgument_699_1) -> DayDividerKt.DayDivider$lambda$1(recoveredCapture_699_0, recoveredCapture_699_1, recoveredCapture_699_2, recoveredCapture_699_3, (androidx.compose.runtime.Composer)recoveredArgument_699_0, ((java.lang.Integer)recoveredArgument_699_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DayDividerPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(191155235);
      ComposerKt.sourceInformation(var0, "C(DayDividerPreview)41@1297L83:DayDivider.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(191155235, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.DayDividerPreview (DayDivider.kt:40)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$DayDividerKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_700_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_700_0, recoveredArgument_700_1) -> DayDividerKt.DayDividerPreview$lambda$2(recoveredCapture_700_0, (androidx.compose.runtime.Composer)recoveredArgument_700_0, ((java.lang.Integer)recoveredArgument_700_1).intValue())));
      }
   }

   private static final Unit DayDivider$lambda$1(String var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      DayDivider(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit DayDividerPreview$lambda$2(int var0, Composer var1, int var2) {
      DayDividerPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
