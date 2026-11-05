package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
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
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.compose.NavHostKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nIntercomRootNavHost.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomRootNavHost.kt\nio/intercom/android/sdk/m5/navigation/IntercomRootNavHostKt$IntercomRootNavHost$2\n+ 2 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,73:1\n70#2:74\n67#2,9:75\n77#2:115\n81#3,6:84\n88#3,6:99\n96#3:114\n402#4,9:90\n411#4:105\n412#4,2:112\n1047#5,6:106\n*S KotlinDebug\n*F\n+ 1 IntercomRootNavHost.kt\nio/intercom/android/sdk/m5/navigation/IntercomRootNavHostKt$IntercomRootNavHost$2\n*L\n31#1:74\n31#1:75,9\n31#1:115\n31#1:84,6\n31#1:99,6\n31#1:114\n31#1:90,9\n31#1:105\n31#1:112,2\n38#1:106,6\n*E\n"
)
final class IntercomRootNavHostKt$IntercomRootNavHost$2 implements Function2 {
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final IntercomRootActivityArgs $intercomRootActivityArgs;
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final CoroutineScope $scope;

   IntercomRootNavHostKt$IntercomRootNavHost$2(NavHostController var1, IntercomRootActivityArgs var2, ComponentActivity var3, CoroutineScope var4) {
      this.$navController = var1;
      this.$intercomRootActivityArgs = var2;
      this.$rootActivity = var3;
      this.$scope = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C30@1161L1452:IntercomRootNavHost.kt#m1zs2i");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1903672037, var2, -1, "io.intercom.android.sdk.m5.navigation.IntercomRootNavHost.<anonymous> (IntercomRootNavHost.kt:30)"
            );
         }

         Modifier var3 = SizeKt.fillMaxSize$default(Modifier.Companion, 0.0F, 1, null);
         NavHostController var6 = this.$navController;
         IntercomRootActivityArgs var7 = this.$intercomRootActivityArgs;
         ComponentActivity var8 = this.$rootActivity;
         CoroutineScope var9 = this.$scope;
         int var10 = 6;
         int var11 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var4 = Alignment.Companion.getTopStart();
         boolean var5 = false;
         MeasurePolicy var12 = BoxKt.maybeCachedBoxMeasurePolicy(var4, var5);
         Modifier var13 = var3;
         MeasurePolicy var14 = var12;
         Composer var15 = var1;
         int var16 = 112 & var10 << 3;
         int var17 = 0;
         ComposerKt.sourceInformationMarkerStart(var15, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var18 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var15, 0));
         CompositionLocalMap var19 = var15.getCurrentCompositionLocalMap();
         Modifier var20 = ComposedModifierKt.materializeModifier(var15, var13);
         Function0 var21 = ComposeUiNode.Companion.getConstructor();
         Composer var22 = var15;
         int var23 = 6 | 896 & var16 << 6;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var22.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var22.startReusableNode();
         if (var22.getInserting()) {
            var22.createNode(var21);
         } else {
            var22.useNode();
         }

         Composer var25 = Updater.recovered$676aa17b44fd6280(var22);
         int var26 = 0;
         Updater.recovered$89682b199a142880(var25, var14, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var25, var19, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var25, var18, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var25, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var25, var20, ComposeUiNode.Companion.getSetModifier());
         int var27 = 14 & var23 >> 6;
         Composer var28 = var22;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var30 = 6 | 112 & var10 >> 6;
         Composer var31 = var28;
         BoxScope var32 = BoxScopeInstance.INSTANCE;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -425333372, "C37@1391L1212,34@1254L1349:IntercomRootNavHost.kt#m1zs2i");
         NavHostController var10000 = var6;
         String var10001 = var7.getRoute();
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         Object var10007 = null;
         Object var10008 = null;
         Object var10009 = null;
         var31.startReplaceGroup(-1676284170);
         ComposerKt.sourceInformation(var31, "CC(remember):IntercomRootNavHost.kt#9igjgp");
         Composer var34 = var31;
         boolean var35 = var31.changedInstance(var6) | var31.changedInstance(var8) | var31.changedInstance(var9) | var31.changed(var7);
         int var36 = 0;
         Object var37 = var34.rememberedValue();
         int var38 = 0;
         Object var10010;
         if (!var35 && var37 != Composer.Companion.getEmpty()) {
            var10010 = var37;
         } else {
            Object var39 = null;
            Object var40 = null;
            Object var41 = null;
            Object var42 = null;
            Object var43 = null;
            Object var44 = null;
            Object var45 = null;
            Object var46 = null;
            String var47 = var10001;
            NavHostController var48 = var6;
            int var49 = 0;
            final androidx.navigation.NavHostController recoveredCapture_1051_0 = var6;
            final androidx.activity.ComponentActivity recoveredCapture_1051_1 = var8;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1051_2 = var9;
            final io.intercom.android.sdk.m5.navigation.IntercomRootActivityArgs recoveredCapture_1051_3 = var7;
            Function1 var50 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1051_0) -> IntercomRootNavHostKt$IntercomRootNavHost$2.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_1051_0, recoveredCapture_1051_1, recoveredCapture_1051_2, recoveredCapture_1051_3, (androidx.navigation.NavGraphBuilder)recoveredArgument_1051_0));
            var10000 = var48;
            var10001 = var47;
            var10002 = var46;
            var10003 = var45;
            var10004 = var44;
            var10005 = var43;
            var10006 = var42;
            var10007 = var41;
            var10008 = var40;
            var10009 = var39;
            Object var51 = var50;
            var34.updateRememberedValue(var51);
            var10010 = var51;
         }

         Function1 var52 = (Function1)var10010;
         var31.endReplaceGroup();
         NavHostKt.NavHost(
            var10000,
            var10001,
            (Modifier)var10002,
            (Alignment)var10003,
            (String)var10004,
            (Function1)var10005,
            (Function1)var10006,
            (Function1)var10007,
            (Function1)var10008,
            (Function1)var10009,
            var52,
            var31,
            0,
            0,
            1020
         );
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var28);
         var22.endNode();
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var15);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(
      NavHostController var0, ComponentActivity var1, CoroutineScope var2, IntercomRootActivityArgs var3, NavGraphBuilder var4
   ) {
      Intrinsics.checkNotNullParameter(var4, "$this$NavHost");
      HomeScreenDestinationKt.homeScreen(var4, var0, var1, var2);
      MessagesDestinationKt.messagesDestination(var4, var0, var1);
      HelpCenterDestinationKt.helpCenterDestination(var4, var1, var0, var3);
      TicketDetailDestinationKt.ticketDetailDestination(var4, var0, var1);
      ConversationDestinationKt.conversationDestination(var4, var0, var1);
      TicketsDestinationKt.ticketsDestination(var4, var0, var1);
      CreateTicketDestinationKt.createTicketDestination(var4, var0, var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
