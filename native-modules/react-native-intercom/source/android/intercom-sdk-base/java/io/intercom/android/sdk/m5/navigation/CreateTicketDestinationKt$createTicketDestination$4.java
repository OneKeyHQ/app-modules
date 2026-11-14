package io.intercom.android.sdk.m5.navigation;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.animation.AnimatedContentScope;
import androidx.compose.material3.ModalBottomSheetKt;
import androidx.compose.material3.SheetState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.RectangleShapeKt;
import androidx.compose.ui.graphics.Shape;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavHostController;
import io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success;
import io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData;
import io.intercom.android.sdk.tickets.create.model.CreateTicketLaunchedFrom;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$Companion;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState;
import io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Initial;
import io.intercom.android.sdk.tickets.create.ui.CreateTicketContentScreenKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KFunction;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nCreateTicketDestination.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CreateTicketDestination.kt\nio/intercom/android/sdk/m5/navigation/CreateTicketDestinationKt$createTicketDestination$4\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n+ 5 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,174:1\n1047#2,6:175\n1047#2,3:188\n1050#2,3:192\n1047#2,6:195\n1047#2,6:201\n1047#2,6:207\n1047#2,6:213\n1047#2,6:219\n1047#2,6:225\n1047#2,6:231\n615#3:181\n612#3,6:182\n613#4:191\n85#5:237\n*S KotlinDebug\n*F\n+ 1 CreateTicketDestination.kt\nio/intercom/android/sdk/m5/navigation/CreateTicketDestinationKt$createTicketDestination$4\n*L\n79#1:175,6\n84#1:188,3\n84#1:192,3\n100#1:195,6\n118#1:201,6\n162#1:207,6\n163#1:213,6\n166#1:219,6\n167#1:225,6\n168#1:231,6\n84#1:181\n84#1:182,6\n84#1:191\n159#1:237\n*E\n"
)
final class CreateTicketDestinationKt$createTicketDestination$4 implements Function4 {
   // $VF: synthetic field
   final ComponentActivity $rootActivity;
   // $VF: synthetic field
   final NavHostController $navController;

   CreateTicketDestinationKt$createTicketDestination$4(ComponentActivity var1, NavHostController var2) {
      this.$rootActivity = var1;
      this.$navController = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(AnimatedContentScope var1, NavBackStackEntry var2, Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var1, "$this$composable");
      Intrinsics.checkNotNullParameter(var2, "navBackStackEntry");
      ComposerKt.sourceInformation(
         var3,
         "C69@3252L7,78@3539L51,79@3622L82,83@3726L24,99@4093L522,97@4034L592,158@6259L79,161@6424L12,162@6467L85,165@6577L12,166@6621L33,167@6684L79,159@6347L427:CreateTicketDestination.kt#m1zs2i"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            -521503931, var4, -1, "io.intercom.android.sdk.m5.navigation.createTicketDestination.<anonymous> (CreateTicketDestination.kt:64)"
         );
      }

      Long var5;
      String var6;
      String var123;
      label126: {
         Bundle var10000 = var2.getArguments();
         var5 = var10000 != null ? var10000.getLong("ticket_type_id") : null;
         Bundle var121 = var2.getArguments();
         var6 = var121 != null ? var121.getString("conversation_id") : null;
         Bundle var122 = var2.getArguments();
         if (var122 != null) {
            var123 = var122.getString("from");
            if (var123 != null) {
               break label126;
            }
         }

         var123 = "conversation";
      }

      String var7 = var123;
      if (var5 == null) {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      } else {
         io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$Companion var124 = CreateTicketViewModel.Companion;
         ViewModelStoreOwner var10001 = LocalViewModelStoreOwner.INSTANCE.getCurrent(var3, LocalViewModelStoreOwner.$stable);
         if (var10001 == null) {
            var10001 = this.$rootActivity;
         }

         CreateTicketViewModel var8 = var124.create(
            var10001, var5, var6, Intrinsics.areEqual(var7, "conversation") ? CreateTicketLaunchedFrom.Conversation : CreateTicketLaunchedFrom.Home
         );
         var3.startReplaceGroup(-693655536);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var11 = var3;
         boolean var12 = false;
         int var13 = 0;
         Object var14 = var11.rememberedValue();
         int var15 = 0;
         Object var125;
         if (var14 == Composer.Companion.getEmpty()) {
            int var16 = 0;
            Object var63 = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
            var11.updateRememberedValue(var63);
            var125 = var63;
         } else {
            var125 = var14;
         }

         MutableState var10 = (MutableState)var125;
         var3.endReplaceGroup();
         MutableState var9 = var10;
         SheetState var36 = ModalBottomSheetKt.rememberModalBottomSheetState(true, null, var3, 6, 2);
         boolean var41 = false;
         int var48 = 0;
         ComposerKt.sourceInformationMarkerStart(var3, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var55 = var3;
         ComposerKt.sourceInformationMarkerStart(var3, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var64 = var3;
         boolean var17 = false;
         int $i$f$cachex = 0;
         Object var19 = var64.rememberedValue();
         int var20 = 0;
         Object var126;
         if (var19 == Composer.Companion.getEmpty()) {
            int var21 = 0;
            int var22 = 0;
            Object var101 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var55);
            var64.updateRememberedValue(var101);
            var126 = var101;
         } else {
            var126 = var19;
         }

         CoroutineScope var23 = (CoroutineScope)var126;
         ComposerKt.sourceInformationMarkerEnd(var3);
         CoroutineScope var24 = var23;
         ComposerKt.sourceInformationMarkerEnd(var3);
         CoroutineScope var37 = var24;
         String var127 = "";
         var3.startReplaceGroup(-693637337);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var42 = var3;
         boolean var49 = var3.changedInstance(var8) | var3.changedInstance(this.$navController) | var3.changedInstance(var37);
         NavHostController var56 = this.$navController;
         int $i$f$cachexx = 0;
         Object it$ivx = var42.rememberedValue();
         int var79/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var132;
         if (!var49 && it$ivx != Composer.Companion.getEmpty()) {
            var132 = it$ivx;
         } else {
            String var30 = "";
            int var85 = 0;
            Function2 var131 = new CreateTicketDestinationKt$createTicketDestination$4$1$1(var8, var56, var37, var9, null);
            var127 = var30;
            Object var92 = var131;
            var42.updateRememberedValue(var92);
            var132 = var92;
         }

         Function2 var38 = (Function2)var132;
         var3.endReplaceGroup();
         EffectsKt.LaunchedEffect(var127, var38, var3, 6);
         AnswerClickData var39 = (AnswerClickData)var9.getValue();
         var3.startReplaceGroup(-693618128);
         ComposerKt.sourceInformation(var3, "*117@4740L54,128@5140L1066,116@4687L1519");
         if (var39 == null) {
            Object var128 = null;
         } else {
            AnswerClickData var57 = var39;
            int var66 = 0;
            var3.startReplaceGroup(-693614134);
            ComposerKt.sourceInformation(var3, "123@4968L6");
            long var73 = var57.getClickedItem().getUploadStatus() instanceof io.intercom.android.sdk.survey.ui.models.Answer$MediaAnswer$FileUploadStatus$Success
               ? Color.Companion.recovered$235b30c090a399c5()
               : IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            var3.endReplaceGroup();
            long var86 = var73;
            Shape var98 = RectangleShapeKt.getRectangleShape();
            var3.startReplaceGroup(806366386);
            ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
            Composer var102 = var3;
            boolean var105 = var3.changedInstance(var37);
            int $i$f$cachexxx = 0;
            Object it$ivxx = var102.rememberedValue();
            int var26/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var129;
            if (!var105 && it$ivxx != Composer.Companion.getEmpty()) {
               var129 = it$ivxx;
            } else {
               int var27 = 0;
               final kotlinx.coroutines.CoroutineScope recoveredCapture_1017_0 = var37;
               final androidx.compose.runtime.MutableState recoveredCapture_1017_1 = var9;
               Object var28 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4.invoke$lambda$4$lambda$3$lambda$2(recoveredCapture_1017_0, recoveredCapture_1017_1));
               var102.updateRememberedValue(var28);
               var129 = var28;
            }

            Function0 var29 = (Function0)var129;
            var3.endReplaceGroup();
            ModalBottomSheetKt.recovered$1910bd45b3835662(
               var29,
               null,
               var36,
               0.0F,
               false,
               var98,
               var86,
               0L,
               0.0F,
               0L,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  -693932488, true, new CreateTicketDestinationKt$createTicketDestination$4$2$2(var57, var8, var37, var9), var3, 54
               ),
               var3,
               196608,
               3078,
               7066
            );
         }

         var3.endReplaceGroup();
         State var40 = SnapshotStateKt.collectAsState(var8.getUiState(), io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState$Initial.INSTANCE, null, var3, 48, 2);
         io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var130 = invoke$lambda$5(var40);
         var3.startReplaceGroup(-693563255);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var50 = var3;
         boolean var58 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
         NavHostController var67 = this.$navController;
         ComponentActivity var74 = this.$rootActivity;
         int $i$f$cachexxx = 0;
         Object it$ivxx = var50.rememberedValue();
         int var93/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var134;
         if (!var58 && it$ivxx != Composer.Companion.getEmpty()) {
            var134 = it$ivxx;
         } else {
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var107 = var130;
            int var99 = 0;
            KFunction var133 = new CreateTicketDestinationKt$createTicketDestination$4$3$1(var67, var74);
            var130 = var107;
            Object var103 = var133;
            var50.updateRememberedValue(var103);
            var134 = var103;
         }

         KFunction var43 = (KFunction)var134;
         var3.endReplaceGroup();
         Function0 var135 = (Function0)var43;
         var3.startReplaceGroup(-693561806);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var51 = var3;
         boolean var59 = var3.changedInstance(var8) | var3.changedInstance(var37);
         int $i$f$cachexxxx = 0;
         Object it$ivxxx = var51.rememberedValue();
         int var81/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var10002;
         if (!var59 && it$ivxxx != Composer.Companion.getEmpty()) {
            var10002 = it$ivxxx;
         } else {
            Function0 var31 = var135;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var108 = var130;
            int var88 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1018_0 = var8;
            final kotlinx.coroutines.CoroutineScope recoveredCapture_1018_1 = var37;
            Function0 var32 = ((kotlin.jvm.functions.Function0) () -> CreateTicketDestinationKt$createTicketDestination$4.invoke$lambda$8$lambda$7(recoveredCapture_1018_0, recoveredCapture_1018_1));
            var130 = var108;
            var135 = var31;
            Object var94 = var32;
            var51.updateRememberedValue(var94);
            var10002 = var94;
         }

         Function0 var44 = (Function0)var10002;
         var3.endReplaceGroup();
         Function0 var136 = var44;
         var3.startReplaceGroup(-693558359);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var52 = var3;
         boolean var60 = var3.changedInstance(this.$navController) | var3.changedInstance(this.$rootActivity);
         NavHostController var69 = this.$navController;
         ComponentActivity var76 = this.$rootActivity;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxx = var52.rememberedValue();
         int var95/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var10003;
         if (!var60 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var10003 = it$ivxxxx;
         } else {
            Function0 var115 = var44;
            Function0 var112 = var135;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var109 = var130;
            int var100 = 0;
            KFunction var33 = new CreateTicketDestinationKt$createTicketDestination$4$5$1(var69, var76);
            var130 = var109;
            var135 = var112;
            var136 = var115;
            Object var104 = var33;
            var52.updateRememberedValue(var104);
            var10003 = var104;
         }

         KFunction var45 = (KFunction)var10003;
         var3.endReplaceGroup();
         Function0 var137 = (Function0)var45;
         var3.startReplaceGroup(-693556930);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var53 = var3;
         boolean var61 = var3.changedInstance(var8);
         int $i$f$cachexxxxxx = 0;
         Object it$ivxxxxx = var53.rememberedValue();
         int var83/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var10004;
         if (!var61 && it$ivxxxxx != Composer.Companion.getEmpty()) {
            var10004 = it$ivxxxxx;
         } else {
            Function0 var118 = var137;
            Function0 var116 = var136;
            Function0 var113 = var135;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var110 = var130;
            int var90 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1019_0 = var8;
            Function1 var34 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1019_0) -> CreateTicketDestinationKt$createTicketDestination$4.invoke$lambda$11$lambda$10(recoveredCapture_1019_0, (java.lang.String)recoveredArgument_1019_0));
            var130 = var110;
            var135 = var113;
            var136 = var116;
            var137 = var118;
            Object var96 = var34;
            var53.updateRememberedValue(var96);
            var10004 = var96;
         }

         Function1 var46 = (Function1)var10004;
         var3.endReplaceGroup();
         Function1 var138 = var46;
         var3.startReplaceGroup(-693554868);
         ComposerKt.sourceInformation(var3, "CC(remember):CreateTicketDestination.kt#9igjgp");
         Composer var54 = var3;
         boolean var62 = var3.changedInstance(var8);
         int $i$f$cachexxxxxxx = 0;
         Object it$ivxxxxxx = var54.rememberedValue();
         int var84/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxxx */ = 0;
         Object var10005;
         if (!var62 && it$ivxxxxxx != Composer.Companion.getEmpty()) {
            var10005 = it$ivxxxxxx;
         } else {
            Function1 var120 = var46;
            Function0 var119 = var137;
            Function0 var117 = var136;
            Function0 var114 = var135;
            io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState var111 = var130;
            int var91 = 0;
            final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel recoveredCapture_1016_0 = var8;
            Function1 var35 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1016_0) -> CreateTicketDestinationKt$createTicketDestination$4.invoke$lambda$13$lambda$12(recoveredCapture_1016_0, (io.intercom.android.sdk.survey.ui.questiontype.AnswerClickData)recoveredArgument_1016_0));
            var130 = var111;
            var135 = var114;
            var136 = var117;
            var137 = var119;
            var138 = var120;
            Object var97 = var35;
            var54.updateRememberedValue(var97);
            var10005 = var97;
         }

         Function1 var47 = (Function1)var10005;
         var3.endReplaceGroup();
         CreateTicketContentScreenKt.CreateTicketScreen(var130, var135, var136, var137, var138, var47, var3, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final void invoke$showSheet(CoroutineScope var0, MutableState var1, AnswerClickData var2) {
      BuildersKt.launch$default(var0, null, null, new CreateTicketDestinationKt$createTicketDestination$4$showSheet$1(var1, var2, null), 3, null);
   }

   private static final void invoke$dismissSheet(CoroutineScope var0, MutableState var1) {
      BuildersKt.launch$default(var0, null, null, new CreateTicketDestinationKt$createTicketDestination$4$dismissSheet$1(var1, null), 3, null);
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$2(CoroutineScope var0, MutableState var1) {
      invoke$dismissSheet(var0, var1);
      return Unit.INSTANCE;
   }

   private static final io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState invoke$lambda$5(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (io.intercom.android.sdk.tickets.create.model.CreateTicketViewModel$CreateTicketFormUiState)var1.getValue();
   }

   private static final Unit invoke$lambda$8$lambda$7(CreateTicketViewModel var0, CoroutineScope var1) {
      var0.createTicket(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$11$lambda$10(CreateTicketViewModel var0, String var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onAnswerUpdated(var1);
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$13$lambda$12(CreateTicketViewModel var0, AnswerClickData var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.onAnswerClicked(var1);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3, Object var4) {
      this.invoke((AnimatedContentScope)var1, (NavBackStackEntry)var2, (Composer)var3, ((Number)var4).intValue());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$invoke$showSheet(CoroutineScope var0, MutableState var1, AnswerClickData var2) {
      invoke$showSheet(var0, var1, var2);
   }

   // $VF: synthetic method
   public static final void access$invoke$dismissSheet(CoroutineScope var0, MutableState var1) {
      invoke$dismissSheet(var0, var1);
   }
}
