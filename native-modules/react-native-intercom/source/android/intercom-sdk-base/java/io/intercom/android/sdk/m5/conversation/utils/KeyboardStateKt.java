package io.intercom.android.sdk.m5.conversation.utils;

import androidx.compose.foundation.layout.WindowInsets;
import androidx.compose.foundation.layout.WindowInsetsKt;
import androidx.compose.foundation.layout.WindowInsets_androidKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0013\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0003¨\u0006\u0004²\u0006\f\u0010\u0005\u001a\u0004\u0018\u00010\u0002X\u008a\u008e\u0002",
   d2 = {
         "keyboardAsState",
         "Landroidx/compose/runtime/State;",
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;",
         "(Landroidx/compose/runtime/Composer;I)Landroidx/compose/runtime/State;",
         "intercom-sdk-base_release",
         "oldState"
   }
)
@SourceDebugExtension(
   "SMAP\nKeyboardState.kt\nKotlin\n*S Kotlin\n*F\n+ 1 KeyboardState.kt\nio/intercom/android/sdk/m5/conversation/utils/KeyboardStateKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,52:1\n1047#2,6:53\n75#3:59\n1#4:60\n118#5:61\n85#6:62\n117#6,2:63\n*S KotlinDebug\n*F\n+ 1 KeyboardState.kt\nio/intercom/android/sdk/m5/conversation/utils/KeyboardStateKt\n*L\n24#1:53,6\n25#1:59\n32#1:61\n24#1:62\n24#1:63,2\n*E\n"
)
public final class KeyboardStateKt {
   @Composable
   @NotNull
   public static final State keyboardAsState(@Nullable Composer var0, int var1) {
      var0.startReplaceGroup(-1867236611);
      ComposerKt.sourceInformation(
         var0, "C(keyboardAsState)23@984L33,24@1049L7,25@1091L3,26@1152L3,26@1177L14,28@1285L18,28@1320L18,41@1919L30:KeyboardState.kt#18naso"
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(-1867236611, var1, -1, "io.intercom.android.sdk.m5.conversation.utils.keyboardAsState (KeyboardState.kt:22)");
      }

      var0.startReplaceGroup(-137398301);
      ComposerKt.sourceInformation(var0, "CC(remember):KeyboardState.kt#9igjgp");
      Composer var4 = var0;
      boolean var5 = false;
      int var6 = 0;
      Object var7 = var4.rememberedValue();
      int var8 = 0;
      Object var10000;
      if (var7 == Composer.Companion.getEmpty()) {
         int var9 = 0;
         Object var23 = SnapshotStateKt.mutableStateOf$default(null, null, 2, null);
         var4.updateRememberedValue(var23);
         var10000 = var23;
      } else {
         var10000 = var7;
      }

      MutableState var2;
      int var15;
      int var17;
      boolean var19;
      boolean var21;
      KeyboardState var27;
      label53: {
         MutableState var3 = (MutableState)var10000;
         var0.endReplaceGroup();
         var2 = var3;
         CompositionLocal var14 = CompositionLocalsKt.getLocalDensity();
         int var16 = 0;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var0, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var20 = var0.consume(var14);
         ComposerKt.sourceInformationMarkerEnd(var0);
         Density var13 = (Density)var20;
         var15 = WindowInsets_androidKt.getIme(WindowInsets.Companion, var0, 6).getBottom(var13);
         var17 = WindowInsetsKt.exclude(
               WindowInsets_androidKt.getIme(WindowInsets.Companion, var0, 6), WindowInsets_androidKt.getNavigationBars(WindowInsets.Companion, var0, 6)
            )
            .getBottom(var13);
         var19 = var15 > 0;
         var21 = !Intrinsics.areEqual(
            WindowInsets_androidKt.getImeAnimationSource(WindowInsets.Companion, var0, 6),
            WindowInsets_androidKt.getImeAnimationTarget(WindowInsets.Companion, var0, 6)
         );
         Density var24 = var13;
         int var10 = 0;
         int var11 = 2;
         int var12 = 0;
         float var22 = var24.recovered$245d7c913f591981(Dp.recovered$95b3d103663bf060(var11));
         KeyboardState recoveredPrevious;
         var27 = new KeyboardState(
            var21,
            var15,
            var19,
            !var19 || (var21 && (((recoveredPrevious = keyboardAsState$lambda$1(var2)) != null
               ? recoveredPrevious.getBottomDiff() : 0) - var15 >= var22)),
            var17
         );
      }

      KeyboardState var25 = var27;
      keyboardAsState$lambda$2(var2, var25);
      State var26 = SnapshotStateKt.rememberUpdatedState(var25, var0, 0);
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var0.endReplaceGroup();
      return var26;
   }

   private static final KeyboardState keyboardAsState$lambda$1(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (KeyboardState)var1.getValue();
   }

   private static final void keyboardAsState$lambda$2(MutableState var0, KeyboardState var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }
}
