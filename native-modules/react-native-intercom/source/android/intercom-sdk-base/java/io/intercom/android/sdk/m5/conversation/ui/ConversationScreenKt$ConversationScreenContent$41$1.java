package io.intercom.android.sdk.m5.conversation.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import androidx.activity.compose.ManagedActivityResultLauncher;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.SoftwareKeyboardController;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Content;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.m5.conversation.ui.components.ConversationTopAppBarKt;
import io.intercom.android.sdk.m5.conversation.ui.components.PushNotificationsBannerKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,1034:1\n87#2:1035\n83#2,10:1036\n94#2:1089\n81#3,6:1046\n88#3,6:1061\n96#3:1088\n402#4,9:1052\n411#4:1067\n412#4,2:1086\n1047#5,6:1068\n1047#5,6:1074\n1047#5,6:1080\n*S KotlinDebug\n*F\n+ 1 ConversationScreen.kt\nio/intercom/android/sdk/m5/conversation/ui/ConversationScreenKt$ConversationScreenContent$41$1\n*L\n659#1:1035\n659#1:1036,10\n659#1:1089\n659#1:1046,6\n659#1:1061,6\n659#1:1088\n659#1:1052,9\n659#1:1067\n659#1:1086,2\n664#1:1068,6\n676#1:1074,6\n684#1:1080,6\n*E\n"
)
final class ConversationScreenKt$ConversationScreenContent$41$1 implements Function2 {
   // $VF: synthetic field
   final ConversationUiState $uiState;
   // $VF: synthetic field
   final Function0 $onBackClick;
   // $VF: synthetic field
   final Function0 $onTitleClicked;
   // $VF: synthetic field
   final SoftwareKeyboardController $keyboardController;
   // $VF: synthetic field
   final CoroutineScope $coroutineScope;
   // $VF: synthetic field
   final State $keyboardAsState$delegate;
   // $VF: synthetic field
   final MutableState $openBottomSheet;
   // $VF: synthetic field
   final Function0 $navigateToTicketDetail;
   // $VF: synthetic field
   final Function1 $onMenuClicked;
   // $VF: synthetic field
   final Function1 $trackMetric;
   // $VF: synthetic field
   final ManagedActivityResultLauncher $pushNotificationsPermissionLauncher;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Function2 $updatePushNotificationsBanner;

   ConversationScreenKt$ConversationScreenContent$41$1(
      ConversationUiState var1,
      Function0 var2,
      Function0 var3,
      SoftwareKeyboardController var4,
      CoroutineScope var5,
      State var6,
      MutableState var7,
      Function0 var8,
      Function1 var9,
      Function1 var10,
      ManagedActivityResultLauncher var11,
      Context var12,
      Function2 var13
   ) {
      this.$uiState = var1;
      this.$onBackClick = var2;
      this.$onTitleClicked = var3;
      this.$keyboardController = var4;
      this.$coroutineScope = var5;
      this.$keyboardAsState$delegate = var6;
      this.$openBottomSheet = var7;
      this.$navigateToTicketDetail = var8;
      this.$onMenuClicked = var9;
      this.$trackMetric = var10;
      this.$pushNotificationsPermissionLauncher = var11;
      this.$context = var12;
      this.$updatePushNotificationsBanner = var13;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C658@28812L1726:ConversationScreen.kt#fnibcj");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -10937487, var2, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationScreenContent.<anonymous>.<anonymous> (ConversationScreen.kt:658)"
            );
         }

         ConversationUiState var6 = this.$uiState;
         Function0 var7 = this.$onBackClick;
         Function0 var8 = this.$onTitleClicked;
         SoftwareKeyboardController var9 = this.$keyboardController;
         CoroutineScope var10 = this.$coroutineScope;
         State var11 = this.$keyboardAsState$delegate;
         MutableState var12 = this.$openBottomSheet;
         Function0 var13 = this.$navigateToTicketDetail;
         Function1 var14 = this.$onMenuClicked;
         Function1 var15 = this.$trackMetric;
         ManagedActivityResultLauncher var16 = this.$pushNotificationsPermissionLauncher;
         Context var17 = this.$context;
         Function2 var18 = this.$updatePushNotificationsBanner;
         int var19 = 0;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var3 = Modifier.Companion;
         Vertical var4 = Arrangement.INSTANCE.getTop();
         Horizontal var5 = Alignment.Companion.getStart();
         MeasurePolicy var21 = ColumnKt.columnMeasurePolicy(var4, var5, var1, 14 & var19 >> 3 | 112 & var19 >> 3);
         Modifier var22 = var3;
         MeasurePolicy var23 = var21;
         Composer var24 = var1;
         int var25 = 112 & var19 << 3;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var27 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var24, 0));
         CompositionLocalMap var28 = var24.getCurrentCompositionLocalMap();
         Modifier var29 = ComposedModifierKt.materializeModifier(var24, var22);
         Function0 var30 = ComposeUiNode.Companion.getConstructor();
         Composer var31 = var24;
         int var32 = 6 | 896 & var25 << 6;
         int var33 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var31.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var31.startReusableNode();
         if (var31.getInserting()) {
            var31.createNode(var30);
         } else {
            var31.useNode();
         }

         Composer var34 = Updater.recovered$676aa17b44fd6280(var31);
         int var35 = 0;
         Updater.recovered$89682b199a142880(var34, var23, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var34, var28, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var34, var27, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var34, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var34, var29, ComposeUiNode.Companion.getSetModifier());
         int var36 = 14 & var32 >> 6;
         Composer var37 = var31;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var39 = 6 | 112 & var19 >> 6;
         Composer var40 = var37;
         ColumnScope var41 = ColumnScopeInstance.INSTANCE;
         int var42 = 0;
         ComposerKt.sourceInformationMarkerStart(var40, -1928696754, "C:ConversationScreen.kt#fnibcj");
         var40.startReplaceGroup(1461804629);
         ComposerKt.sourceInformation(var40, "*663@29074L265,660@28898L659");
         TopAppBarUiState var43 = var6.getTopAppBarUiState();
         int var44 = 0;
         TopAppBarUiState var10000 = var43;
         Function0 var10001 = var7;
         var40.startReplaceGroup(690876142);
         ComposerKt.sourceInformation(var40, "CC(remember):ConversationScreen.kt#9igjgp");
         Composer var45 = var40;
         boolean var46 = var40.changedInstance(var6)
            | var40.changed(var8)
            | var40.changed(var9)
            | var40.changedInstance(var10)
            | var40.changed(var11)
            | var40.changed(var12);
         int var47 = 0;
         Object var48 = var45.rememberedValue();
         int var49 = 0;
         Object var10002;
         if (!var46 && var48 != Composer.Companion.getEmpty()) {
            var10002 = var48;
         } else {
            Function0 var50 = var7;
            TopAppBarUiState var51 = var43;
            int var52 = 0;
            final io.intercom.android.sdk.m5.conversation.states.ConversationUiState recoveredCapture_383_0 = var6;
            final kotlin.jvm.functions.Function0 recoveredCapture_383_1 = var8;
            final androidx.compose.ui.platform.SoftwareKeyboardController recoveredCapture_383_2 = var9;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_383_3 = var10;
            final androidx.compose.runtime.MutableState recoveredCapture_383_4 = var12;
            final androidx.compose.runtime.State recoveredCapture_383_5 = var11;
            Function0 var53 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt$ConversationScreenContent$41$1.invoke$lambda$7$lambda$6$lambda$1$lambda$0(recoveredCapture_383_0, recoveredCapture_383_1, recoveredCapture_383_2, recoveredCapture_383_3, recoveredCapture_383_4, recoveredCapture_383_5));
            var10000 = var51;
            var10001 = var50;
            Object var54 = var53;
            var45.updateRememberedValue(var54);
            var10002 = var54;
         }

         Function0 var55 = (Function0)var10002;
         var40.endReplaceGroup();
         ConversationTopAppBarKt.ConversationTopAppBar(var10000, var10001, var55, var13, var14, var15, var40, 0, 0);
         var40.startReplaceGroup(1461827578);
         ComposerKt.sourceInformation(var40, "675@29722L561,683@30327L114,674@29654L818");
         if (var43.getPushNotificationsBannerState() != null) {
            var40.startReplaceGroup(690897174);
            ComposerKt.sourceInformation(var40, "CC(remember):ConversationScreen.kt#9igjgp");
            var45 = var40;
            var46 = var40.changedInstance(var16) | var40.changedInstance(var17);
            var47 = 0;
            var48 = var45.rememberedValue();
            var49 = 0;
            Object var73;
            if (!var46 && var48 != Composer.Companion.getEmpty()) {
               var73 = (Function0)var48;
            } else {
               int var67 = 0;
               final androidx.activity.compose.ManagedActivityResultLauncher recoveredCapture_384_0 = var16;
               final android.content.Context recoveredCapture_384_1 = var17;
               Object var69 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt$ConversationScreenContent$41$1.invoke$lambda$7$lambda$6$lambda$3$lambda$2(recoveredCapture_384_0, recoveredCapture_384_1));
               var45.updateRememberedValue(var69);
               var73 = (Function0)var69;
            }

            var55 = (kotlin.jvm.functions.Function0) var73;
            var40.endReplaceGroup();
            var73 = var55;
            var40.startReplaceGroup(690916087);
            ComposerKt.sourceInformation(var40, "CC(remember):ConversationScreen.kt#9igjgp");
            var45 = var40;
            var46 = var40.changed(var18);
            var47 = 0;
            var48 = var45.rememberedValue();
            var49 = 0;
            if (!var46 && var48 != Composer.Companion.getEmpty()) {
               var10001 = (Function0)var48;
            } else {
               Function0 var66 = var55;
               int var68 = 0;
               final kotlin.jvm.functions.Function2 recoveredCapture_385_0 = var18;
               var10001 = ((kotlin.jvm.functions.Function0) () -> ConversationScreenKt$ConversationScreenContent$41$1.invoke$lambda$7$lambda$6$lambda$5$lambda$4(recoveredCapture_385_0));
               var73 = var66;
               Object var70 = var10001;
               var45.updateRememberedValue(var70);
               var10001 = (Function0)var70;
            }

            var55 = var10001;
            var40.endReplaceGroup();
            PushNotificationsBannerKt.PushNotificationsBanner((kotlin.jvm.functions.Function0) var73, var55, var40, 0);
         }

         var40.endReplaceGroup();
         var40.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var40);
         ComposerKt.sourceInformationMarkerEnd(var37);
         var31.endNode();
         ComposerKt.sourceInformationMarkerEnd(var31);
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$1$lambda$0(
      ConversationUiState var0, Function0 var1, SoftwareKeyboardController var2, CoroutineScope var3, MutableState var4, State var5
   ) {
      if (var0 instanceof ConversationUiState$Content) {
         var1.invoke();
         ConversationScreenKt.access$ConversationScreenContent$hideKeyboardAndShowBottomSheet(var2, var3, var4, var5);
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$3$lambda$2(ManagedActivityResultLauncher var0, Context var1) {
      if (VERSION.SDK_INT >= 33) {
         var0.launch("android.permission.POST_NOTIFICATIONS");
      } else {
         Uri var2 = Uri.fromParts("package", var1.getPackageName(), null);
         var1.startActivity(new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", var2));
      }

      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6$lambda$5$lambda$4(Function2 var0) {
      var0.invoke(true, false);
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
