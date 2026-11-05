package io.intercom.android.sdk.tickets;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.ProgressIndicatorKt;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.tooling.preview.Preview;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0017\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0001¢\u0006\u0002\u0010\u0004\u001a\r\u0010\u0005\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "TicketDetailsLoadingScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketDetailsLoadingScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTicketDetailsLoadingScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketDetailsLoadingScreen.kt\nio/intercom/android/sdk/tickets/TicketDetailsLoadingScreenKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,28:1\n70#2:29\n68#2,8:30\n77#2:63\n81#3,6:38\n88#3,6:53\n96#3:62\n402#4,9:44\n411#4,3:59\n*S KotlinDebug\n*F\n+ 1 TicketDetailsLoadingScreen.kt\nio/intercom/android/sdk/tickets/TicketDetailsLoadingScreenKt\n*L\n16#1:29\n16#1:30,8\n16#1:63\n16#1:38,6\n16#1:53,6\n16#1:62\n16#1:44,9\n16#1:59,3\n*E\n"
)
public final class TicketDetailsLoadingScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketDetailsLoadingScreen(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(2088941682);
      ComposerKt.sourceInformation(var1, "C(TicketDetailsLoadingScreen)15@519L119:TicketDetailsLoadingScreen.kt#3ofz5f");
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
            ComposerKt.traceEventStart(2088941682, var4, -1, "io.intercom.android.sdk.tickets.TicketDetailsLoadingScreen (TicketDetailsLoadingScreen.kt:14)");
         }

         Alignment var5 = Alignment.Companion.getCenter();
         Modifier var6 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
         int var8 = 48;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var7 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var5, var7);
         Modifier var11 = var6;
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
         ComposerKt.sourceInformationMarkerStart(var26, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         BoxScope var30 = BoxScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1782341596, "C16@605L27:TicketDetailsLoadingScreen.kt#3ofz5f");
         ProgressIndicatorKt.recovered$276fbc1f701bb35f(null, 0L, 0.0F, 0L, 0, 0.0F, var29, 0, 63);
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

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1542_0 = var0;
         final int recoveredCapture_1542_1 = var2;
         final int recoveredCapture_1542_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1542_0, recoveredArgument_1542_1) -> TicketDetailsLoadingScreenKt.TicketDetailsLoadingScreen$lambda$1(recoveredCapture_1542_0, recoveredCapture_1542_1, recoveredCapture_1542_2, (androidx.compose.runtime.Composer)recoveredArgument_1542_0, ((java.lang.Integer)recoveredArgument_1542_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TicketDetailsLoadingScreenPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1945499309);
      ComposerKt.sourceInformation(var0, "C(TicketDetailsLoadingScreenPreview)23@717L58:TicketDetailsLoadingScreen.kt#3ofz5f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1945499309, var1, -1, "io.intercom.android.sdk.tickets.TicketDetailsLoadingScreenPreview (TicketDetailsLoadingScreen.kt:22)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TicketDetailsLoadingScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1543_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1543_0, recoveredArgument_1543_1) -> TicketDetailsLoadingScreenKt.TicketDetailsLoadingScreenPreview$lambda$2(recoveredCapture_1543_0, (androidx.compose.runtime.Composer)recoveredArgument_1543_0, ((java.lang.Integer)recoveredArgument_1543_1).intValue())));
      }
   }

   private static final Unit TicketDetailsLoadingScreen$lambda$1(Modifier var0, int var1, int var2, Composer var3, int var4) {
      TicketDetailsLoadingScreen(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit TicketDetailsLoadingScreenPreview$lambda$2(int var0, Composer var1, int var2) {
      TicketDetailsLoadingScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
