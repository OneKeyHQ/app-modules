package io.intercom.android.sdk.m5.navigation;

import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.compose.LocalLifecycleOwnerKt;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import androidx.navigation.NavOptions;
import androidx.navigation.NavOptionsBuilder;
import androidx.navigation.NavOptionsBuilderKt;
import androidx.navigation.PopUpToBuilder;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.m5.home.HomeViewModel;
import io.intercom.android.sdk.m5.home.ui.HomeScreenKt;
import io.intercom.android.sdk.m5.navigation.transitions.EnterTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.ExitTransitionStyle;
import io.intercom.android.sdk.m5.navigation.transitions.TransitionArgs;
import io.intercom.android.sdk.models.Conversation;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nHomeScreenDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeScreenDestination.kt\nio/intercom/android/sdk/m5/navigation/HomeScreenDestinationKt$homeScreen$2\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,168:1\n75#2:169\n1047#3,6:170\n1047#3,6:176\n1047#3,6:182\n1047#3,6:188\n1047#3,6:194\n1047#3,6:200\n1047#3,6:206\n1047#3,6:212\n1047#3,6:218\n1047#3,6:224\n1047#3,6:230\n1047#3,6:236\n*S KotlinDebug\n*F\n+ 1 HomeScreenDestination.kt\nio/intercom/android/sdk/m5/navigation/HomeScreenDestinationKt$homeScreen$2\n*L\n32#1:169\n60#1:170,6\n71#1:176,6\n82#1:182,6\n92#1:188,6\n104#1:194,6\n109#1:200,6\n140#1:206,6\n126#1:212,6\n137#1:218,6\n121#1:224,6\n152#1:230,6\n162#1:236,6\n*E\n"
)
final class HomeScreenDestinationKt$homeScreen$2 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;
   // $VF: synthetic field
   final CoroutineScope $scope;

   HomeScreenDestinationKt$homeScreen$2(ComponentActivity var1, NavHostController var2, CoroutineScope var3) {
      this.$rootActivity = var1;
      this.$navController = var2;
      this.$scope = var3;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "it");
      ComposerKt.sourceInformation(
         var3,
         "C31@1364L7,32@1431L7,59@2481L506,70@3017L504,81@3554L413,91@4003L525,103@4563L189,108@4794L497,139@6043L465,125@5467L436,136@5941L55,120@5320L106,151@6544L234,57@2398L4391,161@6857L105,159@6798L175:HomeScreenDestination.kt#m1zs2i"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(1180315695, var4, -1, "io.intercom.android.sdk.m5.navigation.homeScreen.<anonymous> (HomeScreenDestination.kt:31)");
      }

      CompositionLocal var6 = LocalLifecycleOwnerKt.getLocalLifecycleOwner();
      int var7 = 0;
      int var8 = 0;
      ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
      Object var9 = var3.consume(var6);
      ComposerKt.sourceInformationMarkerEnd(var3);
      LifecycleOwner var5 = (LifecycleOwner)var9;
      ViewModelStoreOwner var10000 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
      if (var10000 == null) {
         var10000 = this.$rootActivity;
      }

      ViewModelStoreOwner var30 = var10000;
      HomeViewModel var31 = HomeViewModel.Companion.create(var30, var5.getLifecycle());
      HomeViewModel var197 = var31;
      var3.startReplaceGroup(940847052);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      Composer var44 = var3;
      boolean var10 = var3.changedInstance(this.$navController);
      NavHostController var11 = this.$navController;
      int var12 = 0;
      Object var13 = var44.rememberedValue();
      int var14 = 0;
      Object var199;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var199 = (Function0)var13;
      } else {
         HomeViewModel var18 = var31;
         int var15 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1033_0 = var11;
         var199 = ((kotlin.jvm.functions.Function0) () -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$1$lambda$0(recoveredCapture_1033_0));
         var197 = var18;
         Object var16 = var199;
         var44.updateRememberedValue(var16);
         var199 = (Function0)var16;
      }

      Function0 var32 = (kotlin.jvm.functions.Function0) var199;
      var3.endReplaceGroup();
      var199 = var32;
      var3.startReplaceGroup(940864202);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10002;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10002 = (Function0)var13;
      } else {
         Function0 var19 = var32;
         HomeViewModel var132 = var197;
         int var112 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1034_0 = var11;
         Function0 var20 = ((kotlin.jvm.functions.Function0) () -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$3$lambda$2(recoveredCapture_1034_0));
         var197 = var132;
         var199 = var19;
         Object var122 = var20;
         var44.updateRememberedValue(var122);
         var10002 = (Function0)var122;
      }

      Function0 var33 = (kotlin.jvm.functions.Function0) var10002;
      var3.endReplaceGroup();
      var10002 = var33;
      var3.startReplaceGroup(940881295);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10003;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10003 = (Function0)var13;
      } else {
         Function0 var152 = var33;
         Function0 var143 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var133 = var197;
         int var113 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1035_0 = var11;
         Function0 var21 = ((kotlin.jvm.functions.Function0) () -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$5$lambda$4(recoveredCapture_1035_0));
         var197 = var133;
         var199 = var143;
         var10002 = var152;
         Object var123 = var21;
         var44.updateRememberedValue(var123);
         var10003 = (Function0)var123;
      }

      Function0 var34 = (kotlin.jvm.functions.Function0) var10003;
      var3.endReplaceGroup();
      var10003 = var34;
      var3.startReplaceGroup(940895775);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10004;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10004 = (Function1)var13;
      } else {
         Function0 var161 = var34;
         Function0 var153 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var144 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var134 = var197;
         int var114 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1036_0 = var11;
         Function1 var22 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1036_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$7$lambda$6(recoveredCapture_1036_0, (java.lang.String)recoveredArgument_1036_0));
         var197 = var134;
         var199 = var144;
         var10002 = var153;
         var10003 = var161;
         Object var124 = var22;
         var44.updateRememberedValue(var124);
         var10004 = (Function1)var124;
      }

      Function1 var35 = (kotlin.jvm.functions.Function1) var10004;
      var3.endReplaceGroup();
      var10004 = var35;
      var3.startReplaceGroup(940913359);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10005;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10005 = (Function0)var13;
      } else {
         Function1 var169 = var35;
         Function0 var162 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var154 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var145 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var135 = var197;
         int var115 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1037_0 = var11;
         Function0 var23 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$11$lambda$10(recoveredExpressionCapture_1037_0));
         var197 = var135;
         var199 = var145;
         var10002 = var154;
         var10003 = var162;
         var10004 = var169;
         Object var125 = var23;
         var44.updateRememberedValue(var125);
         var10005 = (Function0)var125;
      }

      Function0 var36 = (kotlin.jvm.functions.Function0) var10005;
      var3.endReplaceGroup();
      var10005 = var36;
      var3.startReplaceGroup(940921059);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10006;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10006 = (Function0)var13;
      } else {
         Function0 var176 = var36;
         Function1 var170 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var163 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var155 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var146 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var136 = var197;
         int var116 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1038_0 = var11;
         Function0 var24 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$15$lambda$14(recoveredExpressionCapture_1038_0));
         var197 = var136;
         var199 = var146;
         var10002 = var155;
         var10003 = var163;
         var10004 = var170;
         var10005 = var176;
         Object var126 = var24;
         var44.updateRememberedValue(var126);
         var10006 = (Function0)var126;
      }

      Function0 var37 = (kotlin.jvm.functions.Function0) var10006;
      var3.endReplaceGroup();
      var10006 = var37;
      var3.startReplaceGroup(940960995);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10007;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10007 = (Function1)var13;
      } else {
         Function0 var182 = var37;
         Function0 var177 = (kotlin.jvm.functions.Function0) var10005;
         Function1 var171 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var164 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var156 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var147 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var137 = var197;
         int var117 = 0;
         final androidx.navigation.NavHostController recoveredExpressionCapture_1039_0 = var11;
         Function1 var25 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1039_0) -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$19$lambda$18(recoveredExpressionCapture_1039_0, (java.lang.String) ((java.lang.String)recoveredExpressionArgument_1039_0)));
         var197 = var137;
         var199 = var147;
         var10002 = var156;
         var10003 = var164;
         var10004 = var171;
         var10005 = var177;
         var10006 = var182;
         Object var127 = var25;
         var44.updateRememberedValue(var127);
         var10007 = (Function1)var127;
      }

      Function1 var38 = (kotlin.jvm.functions.Function1) var10007;
      var3.endReplaceGroup();
      var10007 = var38;
      var3.startReplaceGroup(940942534);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10008;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10008 = (Function0)var13;
      } else {
         Function1 var187 = var38;
         Function0 var183 = (kotlin.jvm.functions.Function0) var10006;
         Function0 var178 = (kotlin.jvm.functions.Function0) var10005;
         Function1 var172 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var165 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var157 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var148 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var138 = var197;
         int var118 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1040_0 = var11;
         Function0 var26 = ((kotlin.jvm.functions.Function0) () -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$21$lambda$20(recoveredCapture_1040_0));
         var197 = var138;
         var199 = var148;
         var10002 = var157;
         var10003 = var165;
         var10004 = var172;
         var10005 = var178;
         var10006 = var183;
         var10007 = var187;
         Object var128 = var26;
         var44.updateRememberedValue(var128);
         var10008 = (Function0)var128;
      }

      Function0 var39 = (kotlin.jvm.functions.Function0) var10008;
      var3.endReplaceGroup();
      var10008 = var39;
      var3.startReplaceGroup(940957321);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10009;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10009 = (Function1)var13;
      } else {
         Function0 var191 = var39;
         Function1 var188 = (kotlin.jvm.functions.Function1) var10007;
         Function0 var184 = (kotlin.jvm.functions.Function0) var10006;
         Function0 var179 = (kotlin.jvm.functions.Function0) var10005;
         Function1 var173 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var166 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var158 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var149 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var139 = var197;
         int var119 = 0;
         final androidx.navigation.NavHostController recoveredCapture_1041_0 = var11;
         Function1 var27 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1041_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$23$lambda$22(recoveredCapture_1041_0, (io.intercom.android.sdk.models.Conversation)recoveredArgument_1041_0));
         var197 = var139;
         var199 = var149;
         var10002 = var158;
         var10003 = var166;
         var10004 = var173;
         var10005 = var179;
         var10006 = var184;
         var10007 = var188;
         var10008 = var191;
         Object var129 = var27;
         var44.updateRememberedValue(var129);
         var10009 = (Function1)var129;
      }

      Function1 var40 = (kotlin.jvm.functions.Function1) var10009;
      var3.endReplaceGroup();
      var10009 = var40;
      var3.startReplaceGroup(940937500);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$scope) | var3.changedInstance(this.$rootActivity);
      CoroutineScope var75 = this.$scope;
      ComponentActivity var86 = this.$rootActivity;
      var13 = 0;
      Object recoveredExpandCache = var44.rememberedValue();
      int var120/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
      Object var10010;
      if (!var10 && recoveredExpandCache != Composer.Companion.getEmpty()) {
         var10010 = recoveredExpandCache;
      } else {
         Function1 var194 = var40;
         Function0 var192 = (kotlin.jvm.functions.Function0) var10008;
         Function1 var189 = (kotlin.jvm.functions.Function1) var10007;
         Function0 var185 = (kotlin.jvm.functions.Function0) var10006;
         Function0 var180 = (kotlin.jvm.functions.Function0) var10005;
         Function1 var174 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var167 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var159 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var150 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var140 = var197;
         int var130 = 0;
         final kotlinx.coroutines.CoroutineScope recoveredCapture_1042_0 = var75;
         final androidx.activity.ComponentActivity recoveredCapture_1042_1 = var86;
         Function0 var28 = ((kotlin.jvm.functions.Function0) () -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$25$lambda$24(recoveredCapture_1042_0, recoveredCapture_1042_1));
         var197 = var140;
         var199 = var150;
         var10002 = var159;
         var10003 = var167;
         var10004 = var174;
         var10005 = var180;
         var10006 = var185;
         var10007 = var189;
         var10008 = var192;
         var10009 = var194;
         Object var17 = var28;
         var44.updateRememberedValue(var17);
         var10010 = (Function0)var17;
      }

      Function0 var41 = (Function0)var10010;
      var3.endReplaceGroup();
      var10010 = var41;
      var3.startReplaceGroup(940976796);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = var3.changedInstance(this.$navController);
      var11 = this.$navController;
      var12 = 0;
      var13 = var44.rememberedValue();
      var14 = 0;
      Object var10011;
      if (!var10 && var13 != Composer.Companion.getEmpty()) {
         var10011 = var13;
      } else {
         Function0 var196 = var41;
         Function1 var195 = (kotlin.jvm.functions.Function1) var10009;
         Function0 var193 = (kotlin.jvm.functions.Function0) var10008;
         Function1 var190 = (kotlin.jvm.functions.Function1) var10007;
         Function0 var186 = (kotlin.jvm.functions.Function0) var10006;
         Function0 var181 = (kotlin.jvm.functions.Function0) var10005;
         Function1 var175 = (kotlin.jvm.functions.Function1) var10004;
         Function0 var168 = (kotlin.jvm.functions.Function0) var10003;
         Function0 var160 = (kotlin.jvm.functions.Function0) var10002;
         Function0 var151 = (kotlin.jvm.functions.Function0) var199;
         HomeViewModel var141 = var197;
         var120/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         final androidx.navigation.NavHostController recoveredCapture_1032_0 = var11;
         Function1 var29 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1032_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$27$lambda$26(recoveredCapture_1032_0, (io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_1032_0));
         var197 = var141;
         var199 = var151;
         var10002 = var160;
         var10003 = var168;
         var10004 = var175;
         var10005 = var181;
         var10006 = var186;
         var10007 = var190;
         var10008 = var193;
         var10009 = var195;
         var10010 = var196;
         Object var131 = var29;
         var44.updateRememberedValue(var131);
         var10011 = var131;
      }

      Function1 var42 = (Function1)var10011;
      var3.endReplaceGroup();
      HomeScreenKt.HomeScreen(var197, (kotlin.jvm.functions.Function0) var199, (kotlin.jvm.functions.Function0) var10002, (kotlin.jvm.functions.Function0) var10003, (kotlin.jvm.functions.Function1) var10004, (kotlin.jvm.functions.Function0) var10005, (kotlin.jvm.functions.Function0) var10006, (kotlin.jvm.functions.Function1) var10007, (kotlin.jvm.functions.Function0) var10008, (kotlin.jvm.functions.Function1) var10009, (kotlin.jvm.functions.Function0) var10010, var42, var3, 0, 0);
      String var198 = "";
      var3.startReplaceGroup(940986683);
      ComposerKt.sourceInformation(var3, "CC(remember):HomeScreenDestination.kt#9igjgp");
      var44 = var3;
      var10 = false;
      int $i$f$cachex = 0;
      Object recoveredLaunchCache = var44.rememberedValue();
      var13 = 0;
      Object var202;
      if (recoveredLaunchCache == Composer.Companion.getEmpty()) {
         String var142 = "";
         var14 = 0;
         var202 = new HomeScreenDestinationKt$homeScreen$2$12$1(null);
         var198 = var142;
         Object var111 = var202;
         var44.updateRememberedValue(var111);
         var202 = (Function2)var111;
      } else {
         var202 = recoveredLaunchCache;
      }

      Function2 var43 = (Function2)var202;
      var3.endReplaceGroup();
      EffectsKt.LaunchedEffect(var198, var43, var3, 6);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }
   }

   private static final void invoke$openConversation(NavHostController var0, String var1, NavOptions var2, TransitionArgs var3) {
      Injector.get().getMetricTracker().viewedConversation("home", var1);
      IntercomRouterKt.openConversation$default(var0, var1, null, false, null, var2, var3, 14, null);
   }

   // $VF: synthetic method
   static void invoke$openConversation$default(NavHostController var0, String var1, NavOptions var2, TransitionArgs var3, int var4, Object var5) {
      if ((var4 & 4) != 0) {
         var2 = null;
      }

      if ((var4 & 8) != 0) {
         var3 = new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_OUT_RIGHT);
      }

      invoke$openConversation(var0, var1, var2, var3);
   }

   private static final Unit invoke$lambda$1$lambda$0(NavHostController var0) {
      Injector.get().getMetricTracker().clickedSpace("messages");
      IntercomRouterKt.openMessages$default(
         var0,
         new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_OUT_RIGHT),
         false,
         2,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(NavHostController var0) {
      Injector.get().getMetricTracker().clickedSpace("help");
      IntercomRouterKt.openHelpCenter$default(
         var0,
         new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_OUT_RIGHT),
         false,
         2,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(NavHostController var0) {
      IntercomRouterKt.openTicketList$default(
         var0,
         new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_OUT_RIGHT),
         false,
         2,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$7$lambda$6(NavHostController var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "ticketId");
      IntercomRouterKt.openTicketDetailScreen$default(
         var0,
         var1,
         "home",
         new TransitionArgs(EnterTransitionStyle.SLIDE_UP, ExitTransitionStyle.NULL, EnterTransitionStyle.NULL, ExitTransitionStyle.SLIDE_DOWN),
         false,
         8,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10$lambda$9$lambda$8(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(true);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10$lambda$9(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navigate");
      var0.popUpTo("HOME", ((kotlin.jvm.functions.Function1) (recoveredArgument_1044_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$11$lambda$10$lambda$9$lambda$8((androidx.navigation.PopUpToBuilder)recoveredArgument_1044_0)));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10(NavHostController var0) {
      var0.navigate("MESSAGES", ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1043_0) -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$11$lambda$10$lambda$9((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1043_0))));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$14$lambda$13$lambda$12(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(false);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$14$lambda$13(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("HOME", ((kotlin.jvm.functions.Function1) (recoveredArgument_1046_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$15$lambda$14$lambda$13$lambda$12((androidx.navigation.PopUpToBuilder)recoveredArgument_1046_0)));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$15$lambda$14(NavHostController var0) {
      IntercomRouterKt.openNewConversation(
         var0,
         false,
         NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1045_0) -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$15$lambda$14$lambda$13((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1045_0)))),
         new TransitionArgs(EnterTransitionStyle.NONE, ExitTransitionStyle.SLIDE_OUT_RIGHT, null, null, 12, null)
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$19$lambda$18$lambda$17$lambda$16(PopUpToBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$popUpTo");
      var0.setInclusive(false);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$19$lambda$18$lambda$17(NavOptionsBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navOptions");
      var0.popUpTo("HOME", ((kotlin.jvm.functions.Function1) (recoveredArgument_1048_0) -> HomeScreenDestinationKt$homeScreen$2.invoke$lambda$19$lambda$18$lambda$17$lambda$16((androidx.navigation.PopUpToBuilder)recoveredArgument_1048_0)));
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$19$lambda$18(NavHostController var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      invoke$openConversation(
         var0,
         var1,
         NavOptionsBuilderKt.navOptions(((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_1047_0) -> io.intercom.android.sdk.m5.navigation.HomeScreenDestinationKt$homeScreen$2.invoke$lambda$19$lambda$18$lambda$17((androidx.navigation.NavOptionsBuilder) ((androidx.navigation.NavOptionsBuilder)recoveredExpressionArgument_1047_0)))),
         new TransitionArgs(EnterTransitionStyle.NONE, ExitTransitionStyle.SLIDE_OUT_RIGHT, null, null, 12, null)
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$21$lambda$20(NavHostController var0) {
      Injector.get().getMetricTracker().viewedNewConversation("home");
      IntercomRouterKt.openNewConversation$default(
         var0, false, null, new TransitionArgs(EnterTransitionStyle.SLIDE_IN_LEFT, ExitTransitionStyle.SLIDE_OUT_RIGHT, null, null, 12, null), 3, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$23$lambda$22(NavHostController var0, Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      invoke$openConversation$default(var0, var1.getId(), null, null, 12, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$25$lambda$24(CoroutineScope var0, ComponentActivity var1) {
      BuildersKt.launch$default(var0, null, null, new HomeScreenDestinationKt$homeScreen$2$10$1$1(var1, null), 3, null);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$27$lambda$26(NavHostController var0, TicketType var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      IntercomRouterKt.openCreateTicketsScreen(var0, var1, null, "home_screen");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((AnimatedContentScope)var1, (NavBackStackEntry)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }
}
