package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.DividerKt;
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
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "NewMessagesRow",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "NewMessagesRowPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNewMessagesRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NewMessagesRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NewMessagesRowKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,46:1\n118#2:47\n118#2:80\n118#2:81\n99#3:48\n96#3,9:49\n106#3:85\n81#4,6:58\n88#4,6:73\n96#4:84\n402#5,9:64\n411#5:79\n412#5,2:82\n*S KotlinDebug\n*F\n+ 1 NewMessagesRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NewMessagesRowKt\n*L\n25#1:47\n29#1:80\n33#1:81\n22#1:48\n22#1:49,9\n22#1:85\n22#1:58,6\n22#1:73,6\n22#1:84\n22#1:64,9\n22#1:79\n22#1:82,2\n*E\n"
)
public final class NewMessagesRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NewMessagesRow(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(1974801002);
      ComposerKt.sourceInformation(var1, "C(NewMessagesRow)21@777L560:NewMessagesRow.kt#hr3bi5");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1974801002, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.NewMessagesRow (NewMessagesRow.kt:20)");
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         int var5 = 16;
         int var6 = 0;
         Modifier var38 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var5), 0.0F, 2, null);
         Vertical var7 = Alignment.Companion.getCenterVertically();
         short var8 = 384;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var39 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var10 = RowKt.rowMeasurePolicy(var39, var7, var1, 14 & var8 >> 3 | 112 & var8 >> 3);
         Modifier var11 = var38;
         MeasurePolicy var12 = var10;
         Composer var13 = var1;
         int var14 = 112 & var8 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         RowScope var30 = RowScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var29, -418115824, "C29@1051L6,27@952L122,33@1168L42,34@1246L6,31@1083L186,36@1318L6,36@1278L53:NewMessagesRow.kt#hr3bi5"
         );
         var10000 = Modifier.Companion;
         int $this$dp$ivx = 36;
         int $i$f$getDpx = 0;
         DividerKt.recovered$352cda44568e0a7f(
            SizeKt.recovered$4f2dac42b6e775a9(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
            0.0F,
            IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).recovered$ff4dc34320f68c71(),
            var29,
            6,
            2
         );
         var10000 = Modifier.Companion;
         $i$f$getDpx = 8;
         int $i$f$getDpxx = 0;
         Modifier var40 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060($i$f$getDpx), 0.0F, 2, null);
         String var42 = StringResources_androidKt.stringResource(string.intercom_new, var29, 0);
         long var35 = IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).recovered$ff4dc34320f68c71();
         TextKt.recovered$5b114ab32a4f4190(
            var42, var40, var35, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var29, 48, 0, 262136
         );
         DividerKt.recovered$352cda44568e0a7f(
            null, 0.0F, IntercomTheme.INSTANCE.getColors(var29, IntercomTheme.$stable).recovered$ff4dc34320f68c71(), var29, 0, 3
         );
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var45 = var1.endRestartGroup();
      if (var45 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_761_0 = var0;
         final int recoveredCapture_761_1 = var2;
         final int recoveredCapture_761_2 = var3;
         var45.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_761_0, recoveredArgument_761_1) -> NewMessagesRowKt.NewMessagesRow$lambda$1(recoveredCapture_761_0, recoveredCapture_761_1, recoveredCapture_761_2, (androidx.compose.runtime.Composer)recoveredArgument_761_0, ((java.lang.Integer)recoveredArgument_761_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NewMessagesRowPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2081615555);
      ComposerKt.sourceInformation(var0, "C(NewMessagesRowPreview)43@1414L16:NewMessagesRow.kt#hr3bi5");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2081615555, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.NewMessagesRowPreview (NewMessagesRow.kt:42)"
            );
         }

         NewMessagesRow(null, var0, 0, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_762_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_762_0, recoveredArgument_762_1) -> NewMessagesRowKt.NewMessagesRowPreview$lambda$2(recoveredCapture_762_0, (androidx.compose.runtime.Composer)recoveredArgument_762_0, ((java.lang.Integer)recoveredArgument_762_1).intValue())));
      }
   }

   private static final Unit NewMessagesRow$lambda$1(Modifier var0, int var1, int var2, Composer var3, int var4) {
      NewMessagesRow(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit NewMessagesRowPreview$lambda$2(int var0, Composer var1, int var2) {
      NewMessagesRowPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
