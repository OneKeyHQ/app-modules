package io.intercom.android.sdk.survey.block;

import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.CompositionLocalsKt;
import androidx.compose.ui.unit.Density;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTableBlock.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt$TableBody$1\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,747:1\n75#2:748\n1#3:749\n*S KotlinDebug\n*F\n+ 1 TableBlock.kt\nio/intercom/android/sdk/survey/block/TableBlockKt$TableBody$1\n*L\n348#1:748\n*E\n"
)
final class TableBlockKt$TableBody$1 implements Function3 {
   // $VF: synthetic field
   final TableModel $model;
   // $VF: synthetic field
   final ScrollState $verticalScrollState;

   TableBlockKt$TableBody$1(TableModel var1, ScrollState var2) {
      this.$model = var1;
      this.$verticalScrollState = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C*347@16409L7,352@16642L21,348@16451L224:TableBlock.kt#jxhu3x");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(218029994, var4, -1, "io.intercom.android.sdk.survey.block.TableBody.<anonymous> (TableBlock.kt:347)");
         }

         CompositionLocal var6 = CompositionLocalsKt.getLocalDensity();
         int var7 = 0;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var9 = var2.consume(var6);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Density var10 = (Density)var9;
         var8 = 0;
         int var5 = var10.recovered$308ab30a2dba1f24(var1.recovered$4e84a3b22c31a649());
         TableBlockKt.access$TableGrid(
            this.$model,
            var5,
            this.$verticalScrollState,
            ScrollKt.horizontalScroll$default(Modifier.Companion, ScrollKt.rememberScrollState(0, var2, 0, 1), false, null, false, 14, null),
            var2,
            0,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
