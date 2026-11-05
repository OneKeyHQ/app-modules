package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsPaddingKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalKt;
import androidx.compose.runtime.ProvidableCompositionLocal;
import androidx.compose.runtime.ProvidedValue;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.navigation.NamedNavArgument;
import androidx.navigation.NamedNavArgumentKt;
import androidx.navigation.NavArgumentBuilder;
import androidx.navigation.NavGraphBuilder;
import androidx.navigation.NavHostController;
import androidx.navigation.NavType;
import androidx.navigation.compose.NavGraphBuilderKt;
import androidx.navigation.compose.NavHostKt;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u001a;\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\nH\u0001¢\u0006\u0002\u0010\u000b\u001a8\u0010\f\u001a\u00020\u00012\b\b\u0001\u0010\t\u001a\u00020\n2\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\u0011\u0010\r\u001a\r\u0012\u0004\u0012\u00020\u00010\b¢\u0006\u0002\b\u000eH\u0003¢\u0006\u0002\u0010\u000f\u001a;\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0001\u0010\t\u001a\u00020\nH\u0003¢\u0006\u0002\u0010\u000b\u001a=\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00062\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u0001¢\u0006\u0002\u0010\u0017\"\u000e\u0010\u0018\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0019²\u0006\u0012\u0010\u001a\u001a\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001bX\u008a\u0084\u0002",
   d2 = {
         "HelpCenterScreen",
         "",
         "viewModel",
         "Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;",
         "collectionIds",
         "",
         "",
         "onCloseClick",
         "Lkotlin/Function0;",
         "navIcon",
         "",
         "(Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;Ljava/util/List;Lkotlin/jvm/functions/Function0;ILandroidx/compose/runtime/Composer;I)V",
         "HelpCenterOpenGateScaffold",
         "content",
         "Landroidx/compose/runtime/Composable;",
         "(ILkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)V",
         "HelpCenterContent",
         "HelpCenterNavGraph",
         "navController",
         "Landroidx/navigation/NavHostController;",
         "startDestination",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/helpcenter/HelpCenterViewModel;Landroidx/navigation/NavHostController;Ljava/lang/String;Ljava/util/List;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "START_DESTINATION",
         "intercom-sdk-base_release",
         "openState",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,259:1\n75#2:260\n75#2:261\n1047#3,6:262\n85#4:268\n*S KotlinDebug\n*F\n+ 1 HelpCenterScreen.kt\nio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterScreenKt\n*L\n119#1:260\n179#1:261\n184#1:262,6\n47#1:268\n*E\n"
)
public final class HelpCenterScreenKt {
   @NotNull
   private static final String START_DESTINATION = "startDestination";

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterScreen(
      @NotNull HelpCenterViewModel var0, @NotNull List var1, @NotNull Function0 var2, @DrawableRes int var3, @Nullable Composer var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "viewModel");
      Intrinsics.checkNotNullParameter(var1, "collectionIds");
      Intrinsics.checkNotNullParameter(var2, "onCloseClick");
      var4 = var4.startRestartGroup(1421214035);
      ComposerKt.sourceInformation(var4, "C(HelpCenterScreen)P(3!1,2)46@1993L16:HelpCenterScreen.kt#ln9mxm");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1421214035, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreen (HelpCenterScreen.kt:45)");
         }

         State var7 = SnapshotStateKt.collectAsState(var0.getOpenState(), null, var4, 0, 1);
         NetworkResponse var8 = HelpCenterScreen$lambda$0(var7);
         if (var8 == null) {
            var4.startReplaceGroup(-556371760);
            ComposerKt.sourceInformation(var4, "50@2087L132");
            HelpCenterOpenGateScaffold(
               var3, var2, ComposableSingletons$HelpCenterScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var4, 384 | 14 & var6 >> 9 | 112 & var6 >> 3
            );
            var4.endReplaceGroup();
         } else if (!(var8 instanceof NetworkResponse$NetworkError)
            && !(var8 instanceof NetworkResponse$ClientError)
            && !(var8 instanceof NetworkResponse$ServerError)) {
            if (!(var8 instanceof NetworkResponse$Success)) {
               var4.startReplaceGroup(-1957611050);
               var4.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
            }

            var4.startReplaceGroup(-555574130);
            ComposerKt.sourceInformation(var4, "72@2889L198");
            HelpCenterContent(var0, var1, var2, var3, var4, 14 & var6 | 112 & var6 | 896 & var6 | 7168 & var6);
            var4.endReplaceGroup();
         } else {
            var4.startReplaceGroup(-556074284);
            ComposerKt.sourceInformation(var4, "59@2452L373,59@2377L448");
            HelpCenterOpenGateScaffold(
               var3,
               var2,
               ComposableLambdaKt.rememberComposableLambda(-1856200158, true, new HelpCenterScreenKt$HelpCenterScreen$1(var8, var0), var4, 54),
               var4,
               384 | 14 & var6 >> 9 | 112 & var6 >> 3
            );
            var4.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredCapture_837_0 = var0;
         final java.util.List recoveredCapture_837_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_837_2 = var2;
         final int recoveredCapture_837_3 = var3;
         final int recoveredCapture_837_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_837_0, recoveredArgument_837_1) -> HelpCenterScreenKt.HelpCenterScreen$lambda$1(recoveredCapture_837_0, recoveredCapture_837_1, recoveredCapture_837_2, recoveredCapture_837_3, recoveredCapture_837_4, (androidx.compose.runtime.Composer)recoveredArgument_837_0, ((java.lang.Integer)recoveredArgument_837_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   private static final void HelpCenterOpenGateScaffold(@DrawableRes int var0, Function0 var1, Function2 var2, Composer var3, int var4) {
      var3 = var3.startRestartGroup(-683098219);
      ComposerKt.sourceInformation(
         var3, "C(HelpCenterOpenGateScaffold)P(1,2)90@3341L6,91@3405L13,92@3438L170,99@3616L185,88@3265L536:HelpCenterScreen.kt#ln9mxm"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-683098219, var5, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterOpenGateScaffold (HelpCenterScreen.kt:87)");
         }

         ScaffoldKt.recovered$857cf99fc8161460(
            WindowInsetsPaddingKt.windowInsetsPadding(
               BackgroundKt.recovered$42a3174180b3fb7a(
                  Modifier.Companion, IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(), null, 2, null
               ),
               WindowInsets_androidKt.getDisplayCutout(WindowInsets.Companion, var3, 6)
            ),
            ComposableLambdaKt.rememberComposableLambda(1941106521, true, new HelpCenterScreenKt$HelpCenterOpenGateScaffold$1(var1, var0), var3, 54),
            null,
            null,
            null,
            0,
            0L,
            0L,
            null,
            ComposableLambdaKt.rememberComposableLambda(941231204, true, new HelpCenterScreenKt$HelpCenterOpenGateScaffold$2(var2), var3, 54),
            var3,
            805306416,
            508
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_836_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_836_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_836_2 = var2;
         final int recoveredCapture_836_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_836_0, recoveredArgument_836_1) -> HelpCenterScreenKt.HelpCenterOpenGateScaffold$lambda$2(recoveredCapture_836_0, recoveredCapture_836_1, recoveredCapture_836_2, recoveredCapture_836_3, (androidx.compose.runtime.Composer)recoveredArgument_836_0, ((java.lang.Integer)recoveredArgument_836_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HelpCenterContent(HelpCenterViewModel var0, List var1, Function0 var2, @DrawableRes int var3, Composer var4, int var5) {
      var4 = var4.startRestartGroup(1043765272);
      ComposerKt.sourceInformation(var4, "C(HelpCenterContent)P(3!1,2)118@4082L7,119@4098L1921,117@3986L2033:HelpCenterScreen.kt#ln9mxm");
      int var6 = var5;
      if ((var5 & 6) == 0) {
         var6 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 48) == 0) {
         var6 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 384) == 0) {
         var6 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 3072) == 0) {
         var6 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1043765272, var6, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterContent (HelpCenterScreen.kt:116)");
         }

         ProvidableCompositionLocal var10000 = AndroidCompositionLocals_androidKt.getLocalContext();
         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var4, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var4.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var4);
         CompositionLocalKt.CompositionLocalProvider(
            var10000.provides(var0.localizedContext((Context)var10)),
            ComposableLambdaKt.rememberComposableLambda(222051544, true, new HelpCenterScreenKt$HelpCenterContent$1(var2, var3, var0, var1), var4, 54),
            var4,
            48 | ProvidedValue.$stable
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var4.endRestartGroup();
      if (var12 != null) {
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredCapture_831_0 = var0;
         final java.util.List recoveredCapture_831_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_831_2 = var2;
         final int recoveredCapture_831_3 = var3;
         final int recoveredCapture_831_4 = var5;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_831_0, recoveredArgument_831_1) -> HelpCenterScreenKt.HelpCenterContent$lambda$3(recoveredCapture_831_0, recoveredCapture_831_1, recoveredCapture_831_2, recoveredCapture_831_3, recoveredCapture_831_4, (androidx.compose.runtime.Composer)recoveredArgument_831_0, ((java.lang.Integer)recoveredArgument_831_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterNavGraph(
      @NotNull HelpCenterViewModel var0,
      @NotNull NavHostController var1,
      @NotNull String var2,
      @NotNull List var3,
      @Nullable Modifier var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "viewModel");
      Intrinsics.checkNotNullParameter(var1, "navController");
      Intrinsics.checkNotNullParameter(var2, "startDestination");
      Intrinsics.checkNotNullParameter(var3, "collectionIds");
      var5 = var5.startRestartGroup(686627856);
      ComposerKt.sourceInformation(var5, "C(HelpCenterNavGraph)P(4,2,3)178@6275L7,183@6415L3277,179@6287L3405:HelpCenterScreen.kt#ln9mxm");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 16) != 0) {
            var4 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(686627856, var8, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterNavGraph (HelpCenterScreen.kt:177)");
         }

         CompositionLocal var10 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var13 = var5.consume(var10);
         ComposerKt.sourceInformationMarkerEnd(var5);
         Context var9 = (Context)var13;
         NavHostController var10000 = var1;
         String var10001 = var2;
         Modifier var10002 = var4;
         Object var10003 = null;
         Object var10004 = null;
         Object var10005 = null;
         Object var10006 = null;
         Object var10007 = null;
         Object var10008 = null;
         Object var10009 = null;
         var5.startReplaceGroup(-1939226718);
         ComposerKt.sourceInformation(var5, "CC(remember):HelpCenterScreen.kt#9igjgp");
         Composer var31 = var5;
         boolean var32 = var5.changedInstance(var0) | var5.changedInstance(var3) | var5.changedInstance(var1) | var5.changedInstance(var9);
         int var33 = 0;
         Object var14 = var31.rememberedValue();
         int var15 = 0;
         Object var10010;
         if (!var32 && var14 != Composer.Companion.getEmpty()) {
            var10010 = var14;
         } else {
            Object var27 = null;
            Object var26 = null;
            Object var25 = null;
            Object var24 = null;
            Object var23 = null;
            Object var22 = null;
            Object var21 = null;
            Modifier var20 = var4;
            String var19 = var2;
            NavHostController var18 = var1;
            int var16 = 0;
            final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredExpressionCapture_832_0 = var0;
            final java.util.List recoveredExpressionCapture_832_1 = var3;
            final androidx.navigation.NavHostController recoveredExpressionCapture_832_2 = var1;
            final android.content.Context recoveredExpressionCapture_832_3 = var9;
            Function1 var28 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_832_0) -> io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterScreenKt.HelpCenterNavGraph$lambda$7$lambda$6(recoveredExpressionCapture_832_0, recoveredExpressionCapture_832_1, recoveredExpressionCapture_832_2, recoveredExpressionCapture_832_3, (androidx.navigation.NavGraphBuilder) ((androidx.navigation.NavGraphBuilder)recoveredExpressionArgument_832_0)));
            var10000 = var18;
            var10001 = var19;
            var10002 = var20;
            var10003 = var21;
            var10004 = var22;
            var10005 = var23;
            var10006 = var24;
            var10007 = var25;
            var10008 = var26;
            var10009 = var27;
            Object var17 = var28;
            var31.updateRememberedValue(var17);
            var10010 = var17;
         }

         Function1 var30 = (Function1)var10010;
         var5.endReplaceGroup();
         NavHostKt.NavHost(
            var10000,
            var10001,
            var10002,
            (Alignment)var10003,
            (String)var10004,
            (Function1)var10005,
            (Function1)var10006,
            (Function1)var10007,
            (Function1)var10008,
            (Function1)var10009,
            var30,
            var5,
            14 & var8 >> 3 | 112 & var8 >> 3 | 896 & var8 >> 6,
            0,
            1016
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var34 = var5.endRestartGroup();
      if (var34 != null) {
         final io.intercom.android.sdk.m5.helpcenter.HelpCenterViewModel recoveredCapture_833_0 = var0;
         final androidx.navigation.NavHostController recoveredCapture_833_1 = var1;
         final java.lang.String recoveredCapture_833_2 = var2;
         final java.util.List recoveredCapture_833_3 = var3;
         final androidx.compose.ui.Modifier recoveredCapture_833_4 = var4;
         final int recoveredCapture_833_5 = var6;
         final int recoveredCapture_833_6 = var7;
         var34.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_833_0, recoveredArgument_833_1) -> HelpCenterScreenKt.HelpCenterNavGraph$lambda$8(recoveredCapture_833_0, recoveredCapture_833_1, recoveredCapture_833_2, recoveredCapture_833_3, recoveredCapture_833_4, recoveredCapture_833_5, recoveredCapture_833_6, (androidx.compose.runtime.Composer)recoveredArgument_833_0, ((java.lang.Integer)recoveredArgument_833_1).intValue())));
      }
   }

   private static final NetworkResponse HelpCenterScreen$lambda$0(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (NetworkResponse)var1.getValue();
   }

   private static final Unit HelpCenterScreen$lambda$1(HelpCenterViewModel var0, List var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      HelpCenterScreen(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterOpenGateScaffold$lambda$2(int var0, Function0 var1, Function2 var2, int var3, Composer var4, int var5) {
      HelpCenterOpenGateScaffold(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterContent$lambda$3(HelpCenterViewModel var0, List var1, Function0 var2, int var3, int var4, Composer var5, int var6) {
      HelpCenterContent(var0, var1, var2, var3, var5, RecomposeScopeImplKt.updateChangedFlags(var4 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterNavGraph$lambda$7$lambda$6$lambda$4(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.StringType);
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterNavGraph$lambda$7$lambda$6$lambda$5(NavArgumentBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$navArgument");
      var0.setType(NavType.BoolType);
      var0.setDefaultValue(false);
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterNavGraph$lambda$7$lambda$6(
      HelpCenterViewModel var0, List var1, NavHostController var2, Context var3, NavGraphBuilder var4
   ) {
      Intrinsics.checkNotNullParameter(var4, "$this$NavHost");
      NavGraphBuilderKt.composable$default(
         var4,
         "COLLECTIONS",
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(-1869047411, true, new HelpCenterScreenKt$HelpCenterNavGraph$1$1$1(var0, var1, var2)),
         254,
         null
      );
      NamedNavArgument[] var5 = new NamedNavArgument[]{
         NamedNavArgumentKt.navArgument("id", ((kotlin.jvm.functions.Function1) (recoveredArgument_834_0) -> HelpCenterScreenKt.HelpCenterNavGraph$lambda$7$lambda$6$lambda$4((androidx.navigation.NavArgumentBuilder)recoveredArgument_834_0))),
         NamedNavArgumentKt.navArgument("startDestination", ((kotlin.jvm.functions.Function1) (recoveredArgument_835_0) -> HelpCenterScreenKt.HelpCenterNavGraph$lambda$7$lambda$6$lambda$5((androidx.navigation.NavArgumentBuilder)recoveredArgument_835_0)))
      };
      NavGraphBuilderKt.composable$default(
         var4,
         "COLLECTION_DETAILS/{id}?startDestination={startDestination}",
         CollectionsKt.listOf(var5),
         null,
         null,
         null,
         null,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(2018839094, true, new HelpCenterScreenKt$HelpCenterNavGraph$1$1$4(var0, var3, var2)),
         252,
         null
      );
      NavGraphBuilderKt.composable$default(
         var4,
         "COLLECTION_DETAILS",
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(-157077227, true, new HelpCenterScreenKt$HelpCenterNavGraph$1$1$5(var0, var1, var3, var2)),
         254,
         null
      );
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterNavGraph$lambda$8(
      HelpCenterViewModel var0, NavHostController var1, String var2, List var3, Modifier var4, int var5, int var6, Composer var7, int var8
   ) {
      HelpCenterNavGraph(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }
}
