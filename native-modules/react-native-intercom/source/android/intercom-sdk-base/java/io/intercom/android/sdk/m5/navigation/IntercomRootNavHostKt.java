package io.intercom.android.sdk.m5.navigation;

import android.content.Intent;
import androidx.activity.ComponentActivity;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigator;
import androidx.navigation.compose.NavHostControllerKt;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001d\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0006¨\u0006\u0007",
   d2 = {
         "IntercomRootNavHost",
         "",
         "intent",
         "Landroid/content/Intent;",
         "rootActivity",
         "Landroidx/activity/ComponentActivity;",
         "(Landroid/content/Intent;Landroidx/activity/ComponentActivity;Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomRootNavHost.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomRootNavHost.kt\nio/intercom/android/sdk/m5/navigation/IntercomRootNavHostKt\n+ 2 Effects.kt\nandroidx/compose/runtime/EffectsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 Effects.kt\nandroidx/compose/runtime/EffectsKt$rememberCoroutineScope$1\n*L\n1#1,73:1\n615#2:74\n612#2,6:75\n1047#3,3:81\n1050#3,3:85\n613#4:84\n*S KotlinDebug\n*F\n+ 1 IntercomRootNavHost.kt\nio/intercom/android/sdk/m5/navigation/IntercomRootNavHostKt\n*L\n27#1:74\n27#1:75,6\n27#1:81,3\n27#1:85,3\n27#1:84\n*E\n"
)
public final class IntercomRootNavHostKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void IntercomRootNavHost(@NotNull Intent var0, @NotNull ComponentActivity var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "intent");
      Intrinsics.checkNotNullParameter(var1, "rootActivity");
      var2 = var2.startRestartGroup(884340874);
      ComposerKt.sourceInformation(var2, "C(IntercomRootNavHost)25@1051L23,26@1091L24,29@1151L1468,29@1143L1476:IntercomRootNavHost.kt#m1zs2i");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(884340874, var4, -1, "io.intercom.android.sdk.m5.navigation.IntercomRootNavHost (IntercomRootNavHost.kt:15)");
         }

         IntercomRootActivityArgs var5 = IntercomRootActivityArgsKt.getArgsForIntent(var0);
         if (var5 instanceof IntercomRootActivityArgs$NoContent) {
            var1.finish();
            LumberMill.getLogger().e("No content to display. Closing the activity.");
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var22 = var2.endRestartGroup();
            if (var22 != null) {
               final android.content.Intent recoveredCapture_1049_0 = var0;
               final androidx.activity.ComponentActivity recoveredCapture_1049_1 = var1;
               final int recoveredCapture_1049_2 = var3;
               var22.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1049_0, recoveredArgument_1049_1) -> IntercomRootNavHostKt.IntercomRootNavHost$lambda$0(recoveredCapture_1049_0, recoveredCapture_1049_1, recoveredCapture_1049_2, (androidx.compose.runtime.Composer)recoveredArgument_1049_0, ((java.lang.Integer)recoveredArgument_1049_1).intValue())));
            }

            return;
         }

         NavHostController var6 = NavHostControllerKt.rememberNavController(new Navigator[0], var2, 0);
         boolean var8 = false;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 773894976, "CC(rememberCoroutineScope)N(getContext)616@28039L68:Effects.kt#9igjgp");
         Composer var10 = var2;
         ComposerKt.sourceInformationMarkerStart(var2, 683736516, "CC(remember):Effects.kt#9igjgp");
         Composer var11 = var2;
         boolean var12 = false;
         int var13 = 0;
         Object var14 = var11.rememberedValue();
         int var15 = 0;
         Object var10000;
         if (var14 == Composer.Companion.getEmpty()) {
            int var16 = 0;
            int var17 = 0;
            Object var21 = EffectsKt.createCompositionCoroutineScope(EmptyCoroutineContext.INSTANCE, var10);
            var11.updateRememberedValue(var21);
            var10000 = var21;
         } else {
            var10000 = var14;
         }

         CoroutineScope var18 = (CoroutineScope)var10000;
         ComposerKt.sourceInformationMarkerEnd(var2);
         CoroutineScope var19 = var18;
         ComposerKt.sourceInformationMarkerEnd(var2);
         CoroutineScope var7 = var19;
         SurfaceKt.recovered$ecd79a99e04a9534(
            null,
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(1903672037, true, new IntercomRootNavHostKt$IntercomRootNavHost$2(var6, var5, var1, var7), var2, 54),
            var2,
            12582912,
            127
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var23 = var2.endRestartGroup();
      if (var23 != null) {
         final android.content.Intent recoveredCapture_1050_0 = var0;
         final androidx.activity.ComponentActivity recoveredCapture_1050_1 = var1;
         final int recoveredCapture_1050_2 = var3;
         var23.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1050_0, recoveredArgument_1050_1) -> IntercomRootNavHostKt.IntercomRootNavHost$lambda$1(recoveredCapture_1050_0, recoveredCapture_1050_1, recoveredCapture_1050_2, (androidx.compose.runtime.Composer)recoveredArgument_1050_0, ((java.lang.Integer)recoveredArgument_1050_1).intValue())));
      }
   }

   private static final Unit IntercomRootNavHost$lambda$0(Intent var0, ComponentActivity var1, int var2, Composer var3, int var4) {
      IntercomRootNavHost(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit IntercomRootNavHost$lambda$1(Intent var0, ComponentActivity var1, int var2, Composer var3, int var4) {
      IntercomRootNavHost(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }
}
