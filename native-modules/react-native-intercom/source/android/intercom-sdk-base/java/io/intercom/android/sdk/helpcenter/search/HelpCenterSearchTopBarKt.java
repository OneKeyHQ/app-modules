package io.intercom.android.sdk.helpcenter.search;

import android.app.Activity;
import android.view.Window;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.material3.AppBarKt;
import androidx.compose.material3.TopAppBarColors;
import androidx.compose.material3.TopAppBarDefaults;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.focus.FocusManager;
import androidx.compose.ui.focus.FocusRequester;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u001aI\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\u0018\u0010\u0004\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u00010\u0005H\u0001¢\u0006\u0002\u0010\t¨\u0006\n²\u0006\n\u0010\u000b\u001a\u00020\u0007X\u008a\u008e\u0002",
   d2 = {
         "HelpCenterSearchTopBar",
         "",
         "onBackClick",
         "Lkotlin/Function0;",
         "onTextChanged",
         "Lkotlin/Function1;",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "",
         "onSearchAction",
         "(Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release",
         "searchText"
   }
)
@SourceDebugExtension(
   "SMAP\nHelpCenterSearchTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,163:1\n1047#2,6:164\n1047#2,6:171\n1047#2,6:177\n1047#2,6:184\n1047#2,6:190\n1047#2,6:196\n75#3:170\n75#3:183\n85#4:202\n117#4,2:203\n*S KotlinDebug\n*F\n+ 1 HelpCenterSearchTopBar.kt\nio/intercom/android/sdk/helpcenter/search/HelpCenterSearchTopBarKt\n*L\n56#1:164,6\n58#1:171,6\n59#1:177,6\n67#1:184,6\n73#1:190,6\n78#1:196,6\n57#1:170\n66#1:183\n58#1:202\n58#1:203,2\n*E\n"
)
public final class HelpCenterSearchTopBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HelpCenterSearchTopBar(@NotNull Function0 var0, @NotNull Function1 var1, @NotNull Function1 var2, @Nullable Composer var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "onBackClick");
      Intrinsics.checkNotNullParameter(var1, "onTextChanged");
      Intrinsics.checkNotNullParameter(var2, "onSearchAction");
      Composer var25 = var3.startRestartGroup(1649601348);
      ComposerKt.sourceInformation(
         var25,
         "C(HelpCenterSearchTopBar)P(!1,2)55@2341L29,56@2412L7,57@2442L31,58@2493L33,61@2634L6,65@2902L7,72@3176L39,72@3155L60,77@3302L45,77@3281L66,155@6671L6,156@6732L6,157@6804L6,158@6872L6,154@6611L287,83@3429L2763,145@6219L355,81@3353L3552:HelpCenterSearchTopBar.kt#fpck24"
      );
      int var5 = var4;
      if ((var4 & 6) == 0) {
         var5 |= var25.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var5 |= var25.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var5 |= var25.changedInstance(var2) ? 256 : 128;
      }

      if ((var5 & 147) == 146 && var25.getSkipping()) {
         var25.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1649601348, var5, -1, "io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBar (HelpCenterSearchTopBar.kt:54)");
         }

         var25.startReplaceGroup(149284955);
         ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var8 = var25;
         boolean var9 = false;
         int var10 = 0;
         Object var11 = var8.rememberedValue();
         int var12 = 0;
         Object var10000;
         if (var11 == Composer.Companion.getEmpty()) {
            int var13 = 0;
            Object var48 = new FocusRequester();
            var8.updateRememberedValue(var48);
            var10000 = var48;
         } else {
            var10000 = var11;
         }

         FocusRequester var7 = (FocusRequester)var10000;
         var25.endReplaceGroup();
         FocusRequester var6 = var7;
         CompositionLocal var27 = CompositionLocalsKt.getLocalFocusManager();
         int var29 = 0;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var36 = var25.consume(var27);
         ComposerKt.sourceInformationMarkerEnd(var25);
         FocusManager var26 = (FocusManager)var36;
         var25.startReplaceGroup(149288189);
         ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var33 = var25;
         boolean var37 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var33.rememberedValue();
         int var14/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var81;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var15 = 0;
            Object var61 = SnapshotStateKt.mutableStateOf$default("", null, 2, null);
            var33.updateRememberedValue(var61);
            var81 = var61;
         } else {
            var81 = it$ivx;
         }

         MutableState var30 = (MutableState)var81;
         var25.endReplaceGroup();
         MutableState var28 = var30;
         var25.startReplaceGroup(149289823);
         ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var38 = var25;
         boolean var44 = false;
         int $i$f$cachexx = 0;
         Object it$ivxx = var38.rememberedValue();
         int var62/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var82;
         if (it$ivxx == Composer.Companion.getEmpty()) {
            int var16 = 0;
            Object var67 = StateFlowKt.MutableStateFlow("");
            var38.updateRememberedValue(var67);
            var82 = var67;
         } else {
            var82 = it$ivxx;
         }

         MutableStateFlow var34 = (MutableStateFlow)var82;
         var25.endReplaceGroup();
         MutableStateFlow var31 = var34;
         boolean var35 = !ColorExtensionsKt.recovered$41e561cae61044c5(
            IntercomTheme.INSTANCE.getColors(var25, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4()
         );
         CompositionLocal var56 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivx = 0;
         int $i$f$getCurrentx = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var17 = var25.consume(var56);
         ComposerKt.sourceInformationMarkerEnd(var25);
         Object var51 = var17;
         Window var39 = (var51 instanceof Activity ? (Activity)var51 : null) != null ? (var51 instanceof Activity ? (Activity)var51 : null).getWindow() : null;
         var25.startReplaceGroup(149304033);
         ComposerKt.sourceInformation(var25, "*66@2958L132,66@2947L143");
         if (var39 == null) {
            Object var84 = null;
         } else {
            Window var57 = var39;
            int var64 = 0;
            var25.startReplaceGroup(1000405729);
            ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
            Composer var69 = var25;
            boolean var73 = var25.changedInstance(var57) | var25.changed(var35);
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var69.rememberedValue();
            int var20/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var83;
            if (!var73 && it$ivxxx != Composer.Companion.getEmpty()) {
               var83 = it$ivxxx;
            } else {
               int var21 = 0;
               final android.view.Window recoveredExpressionCapture_101_0 = var57;
               final boolean recoveredExpressionCapture_101_1 = var35;
               Object var22 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBarKt.HelpCenterSearchTopBar$lambda$7$lambda$6$lambda$5(recoveredExpressionCapture_101_0, recoveredExpressionCapture_101_1));
               var69.updateRememberedValue(var22);
               var83 = var22;
            }

            Function0 var23 = (Function0)var83;
            var25.endReplaceGroup();
            EffectsKt.SideEffect(var23, var25, 0);
         }

         var25.endReplaceGroup();
         Unit var85 = Unit.INSTANCE;
         var25.startReplaceGroup(149311685);
         ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var45 = var25;
         boolean var52 = (var5 & 112) == 32 | var25.changedInstance(var31);
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var45.rememberedValue();
         int var70/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var88;
         if (!var52 && it$ivxxx != Composer.Companion.getEmpty()) {
            var88 = it$ivxxx;
         } else {
            Unit var24 = var85;
            int var74 = 0;
            Function2 var10001 = new HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$2$1(var1, var31, null);
            var85 = var24;
            Object var76 = var10001;
            var45.updateRememberedValue(var76);
            var88 = var76;
         }

         Function2 var40 = (Function2)var88;
         var25.endReplaceGroup();
         EffectsKt.LaunchedEffect(var85, var40, var25, 6);
         Unit var86 = Unit.INSTANCE;
         var25.startReplaceGroup(149315723);
         ComposerKt.sourceInformation(var25, "CC(remember):HelpCenterSearchTopBar.kt#9igjgp");
         Composer var46 = var25;
         boolean invalid$ivx = false;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var46.rememberedValue();
         int var71/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var90;
         if (it$ivxxxx == Composer.Companion.getEmpty()) {
            Unit var80 = var86;
            int var75 = 0;
            Function2 var89 = new HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$3$1(var6, null);
            var86 = var80;
            Object var77 = var89;
            var46.updateRememberedValue(var77);
            var90 = var77;
         } else {
            var90 = it$ivxxxx;
         }

         Function2 var41 = (Function2)var90;
         var25.endReplaceGroup();
         EffectsKt.LaunchedEffect(var86, var41, var25, 6);
         Modifier var42 = WindowInsetsPadding_androidKt.statusBarsPadding(Modifier.Companion);
         TopAppBarDefaults var54 = TopAppBarDefaults.INSTANCE;
         long var60 = IntercomTheme.INSTANCE.getColors(var25, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4();
         long var72 = IntercomTheme.INSTANCE.getColors(var25, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
         long var78 = IntercomTheme.INSTANCE.getColors(var25, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
         long var79 = IntercomTheme.INSTANCE.getColors(var25, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
         TopAppBarColors var47 = var54.recovered$a08f27bd3095a956(var60, 0L, var78, var72, var79, 0L, var25, TopAppBarDefaults.$stable << 18, 34);
         AppBarKt.recovered$b7478c5c2e9436ce(
            ComposableLambdaKt.rememberComposableLambda(
               -227105272, true, new HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$4(var6, var2, var26, var31, var28), var25, 54
            ),
            var42,
            ComposableLambdaKt.rememberComposableLambda(-1996576886, true, new HelpCenterSearchTopBarKt$HelpCenterSearchTopBar$5(var0), var25, 54),
            null,
            0.0F,
            null,
            var47,
            null,
            var25,
            390,
            184
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var87 = var25.endRestartGroup();
      if (var87 != null) {
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_102_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_102_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_102_2 = var2;
         final int recoveredExpressionCapture_102_3 = var4;
         var87.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_102_0, recoveredExpressionArgument_102_1) -> io.intercom.android.sdk.helpcenter.search.HelpCenterSearchTopBarKt.HelpCenterSearchTopBar$lambda$10(recoveredExpressionCapture_102_0, recoveredExpressionCapture_102_1, recoveredExpressionCapture_102_2, recoveredExpressionCapture_102_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_102_0), ((java.lang.Integer)recoveredExpressionArgument_102_1).intValue())));
      }
   }

   private static final String HelpCenterSearchTopBar$lambda$2(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void HelpCenterSearchTopBar$lambda$3(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit HelpCenterSearchTopBar$lambda$7$lambda$6$lambda$5(Window var0, boolean var1) {
      WindowCompat.getInsetsController(var0, var0.getDecorView()).setAppearanceLightStatusBars(var1);
      return Unit.INSTANCE;
   }

   private static final Unit HelpCenterSearchTopBar$lambda$10(Function0 var0, Function1 var1, Function1 var2, int var3, Composer var4, int var5) {
      HelpCenterSearchTopBar(var0, var1, var2, var4, RecomposeScopeImplKt.updateChangedFlags(var3 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final String access$HelpCenterSearchTopBar$lambda$2(MutableState var0) {
      return HelpCenterSearchTopBar$lambda$2(var0);
   }

   // $VF: synthetic method
   public static final void access$HelpCenterSearchTopBar$lambda$3(MutableState var0, String var1) {
      HelpCenterSearchTopBar$lambda$3(var0, var1);
   }
}
