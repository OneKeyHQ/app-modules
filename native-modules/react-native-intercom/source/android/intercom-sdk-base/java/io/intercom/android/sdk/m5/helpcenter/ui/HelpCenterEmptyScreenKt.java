package io.intercom.android.sdk.m5.helpcenter.ui;

import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
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
import androidx.compose.ui.res.StringResources_androidKt;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.EmptyStateKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
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
         "HelpCenterEmptyScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "InboxEmptyScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterEmptyScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterEmptyScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterEmptyScreenKt\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,35:1\n70#2:36\n66#2,10:37\n77#2:72\n81#3,6:47\n88#3,6:62\n96#3:71\n402#4,9:53\n411#4,3:68\n*S KotlinDebug\n*F\n+ 1 HelpCenterEmptyScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterEmptyScreenKt\n*L\n19#1:36\n19#1:37,10\n19#1:72\n19#1:47,6\n19#1:62,6\n19#1:71\n19#1:53,9\n19#1:68,3\n*E\n"
)
public final class HelpCenterEmptyScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterEmptyScreen(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(-123597347);
      ComposerKt.sourceInformation(var1, "C(HelpCenterEmptyScreen)18@644L262:HelpCenterEmptyScreen.kt#ln9mxm");
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
            ComposerKt.traceEventStart(-123597347, var4, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterEmptyScreen (HelpCenterEmptyScreen.kt:17)");
         }

         Alignment var6 = Alignment.Companion.getCenter();
         int var8 = 48;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var5 = Modifier.Companion;
         boolean var7 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var6, var7);
         Modifier var11 = var5;
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
         ComposerKt.sourceInformationMarkerStart(var29, -345331608, "C21@774L56,19@695L205:HelpCenterEmptyScreen.kt#ln9mxm");
         Modifier var32 = SizeKt.fillMaxSize$default(var0, 0.0F, 1, null);
         String var33 = StringResources_androidKt.stringResource(string.intercom_no_articles_to_display, var29, 0);
         int var34 = drawable.intercom_help_centre_icon;
         EmptyStateKt.EmptyState(var33, var32, null, var34, null, var29, 0, 20);
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
         final androidx.compose.ui.Modifier recoveredCapture_822_0 = var0;
         final int recoveredCapture_822_1 = var2;
         final int recoveredCapture_822_2 = var3;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_822_0, recoveredArgument_822_1) -> HelpCenterEmptyScreenKt.HelpCenterEmptyScreen$lambda$1(recoveredCapture_822_0, recoveredCapture_822_1, recoveredCapture_822_2, (androidx.compose.runtime.Composer)recoveredArgument_822_0, ((java.lang.Integer)recoveredArgument_822_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxEmptyScreenPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1897399468);
      ComposerKt.sourceInformation(var0, "C(InboxEmptyScreenPreview)30@984L115:HelpCenterEmptyScreen.kt#ln9mxm");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1897399468, var1, -1, "io.intercom.android.sdk.m5.helpcenter.ui.InboxEmptyScreenPreview (HelpCenterEmptyScreen.kt:29)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterEmptyScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_823_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_823_0, recoveredArgument_823_1) -> HelpCenterEmptyScreenKt.InboxEmptyScreenPreview$lambda$2(recoveredCapture_823_0, (androidx.compose.runtime.Composer)recoveredArgument_823_0, ((java.lang.Integer)recoveredArgument_823_1).intValue())));
      }
   }

   private static final Unit HelpCenterEmptyScreen$lambda$1(Modifier var0, int var1, int var2, Composer var3, int var4) {
      HelpCenterEmptyScreen(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit InboxEmptyScreenPreview$lambda$2(int var0, Composer var1, int var2) {
      InboxEmptyScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
