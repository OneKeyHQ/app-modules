package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.lazy.LazyItemScope;
import androidx.compose.foundation.lazy.LazyListState;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.IntState;
import androidx.compose.runtime.MutableIntState;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotIntStateKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.layout.OnRemeasuredModifierKt;
import androidx.compose.ui.unit.IntSize;
import io.intercom.android.sdk.m5.conversation.ui.components.row.StreamingMessageContentKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlinx.coroutines.flow.StateFlow;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nStreamingMessageList.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$streamingAnswerItem$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 SnapshotIntState.kt\nandroidx/compose/runtime/SnapshotIntStateKt__SnapshotIntStateKt\n+ 4 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n+ 5 IntSize.kt\nandroidx/compose/ui/unit/IntSize\n+ 6 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n*L\n1#1,578:1\n1047#2,6:579\n1047#2,6:585\n1047#2,6:591\n1047#2,6:597\n1047#2,6:603\n1047#2,6:609\n78#3:615\n111#3,2:616\n85#4:618\n117#4,2:619\n59#5:621\n90#6:622\n*S KotlinDebug\n*F\n+ 1 StreamingMessageList.kt\nio/intercom/android/sdk/m5/conversation/ui/components/StreamingMessageListKt$streamingAnswerItem$1\n*L\n446#1:579,6\n447#1:585,6\n449#1:591,6\n455#1:597,6\n459#1:603,6\n474#1:609,6\n446#1:615\n446#1:616,2\n447#1:618\n447#1:619,2\n474#1:621\n474#1:622\n*E\n"
)
final class StreamingMessageListKt$streamingAnswerItem$1 implements Function3 {
   // $VF: synthetic field
   final LazyListState $lazyListState;
   // $VF: synthetic field
   final Function0 $isAtBottom;
   // $VF: synthetic field
   final float $topGap;
   // $VF: synthetic field
   final StateFlow $stateFlow;

   StreamingMessageListKt$streamingAnswerItem$1(LazyListState var1, Function0 var2, float var3, StateFlow var4) {
      this.$lazyListState = var1;
      this.$isAtBottom = var2;
      this.$topGap = var3;
      this.$stateFlow = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(LazyItemScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$item");
      ComposerKt.sourceInformation(
         var2,
         "C445@20890L33,446@20945L41,448@21022L158,448@20992L188,454@21216L100,454@21186L130,458@21352L233,458@21322L263,473@21802L39,467@21591L257:StreamingMessageList.kt#o19g89"
      );
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               339555046, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.streamingAnswerItem.<anonymous> (StreamingMessageList.kt:445)"
            );
         }

         var2.startReplaceGroup(1776070268);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var6 = var2;
         boolean var7 = false;
         int var8 = 0;
         Object var9 = var6.rememberedValue();
         int var10 = 0;
         Object var10000;
         if (var9 == Composer.Companion.getEmpty()) {
            int var11 = 0;
            Object var42 = SnapshotIntStateKt.mutableIntStateOf(0);
            var6.updateRememberedValue(var42);
            var10000 = var42;
         } else {
            var10000 = var9;
         }

         MutableIntState var5 = (MutableIntState)var10000;
         var2.endReplaceGroup();
         MutableIntState var4 = var5;
         var2.startReplaceGroup(1776072036);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var22 = var2;
         boolean var27 = false;
         Function0 var32 = this.$isAtBottom;
         int $i$f$cachex = 0;
         Object it$ivx = var22.rememberedValue();
         int var12/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var63;
         if (it$ivx == Composer.Companion.getEmpty()) {
            int var13 = 0;
            Object var14 = SnapshotStateKt.mutableStateOf$default(var32.invoke(), null, 2, null);
            var22.updateRememberedValue(var14);
            var63 = var14;
         } else {
            var63 = it$ivx;
         }

         MutableState var17 = (MutableState)var63;
         var2.endReplaceGroup();
         MutableState var16 = var17;
         LazyListState var64 = this.$lazyListState;
         var2.startReplaceGroup(1776074617);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var23 = var2;
         boolean var28 = var2.changed(this.$lazyListState);
         LazyListState var33 = this.$lazyListState;
         int $i$f$cachexx = 0;
         Object it$ivxx = var23.rememberedValue();
         int var48/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         Object var68;
         if (!var28 && it$ivxx != Composer.Companion.getEmpty()) {
            var68 = it$ivxx;
         } else {
            LazyListState var15 = var64;
            int var52 = 0;
            Function2 var10001 = new StreamingMessageListKt$streamingAnswerItem$1$1$1(var33, var16, null);
            var64 = var15;
            Object var56 = var10001;
            var23.updateRememberedValue(var56);
            var68 = var56;
         }

         Function2 var18 = (Function2)var68;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var64, var18, var2, 0);
         LazyListState var65 = this.$lazyListState;
         var2.startReplaceGroup(1776080767);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var24 = var2;
         boolean var29 = var2.changed(this.$isAtBottom);
         Function0 var34 = this.$isAtBottom;
         int $i$f$cachexxx = 0;
         Object it$ivxxx = var24.rememberedValue();
         int var49/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
         Object var70;
         if (!var29 && it$ivxxx != Composer.Companion.getEmpty()) {
            var70 = it$ivxxx;
         } else {
            LazyListState var60 = var65;
            int var53 = 0;
            Function2 var69 = new StreamingMessageListKt$streamingAnswerItem$1$2$1(var34, var16, null);
            var65 = var60;
            Object var57 = var69;
            var24.updateRememberedValue(var57);
            var70 = var57;
         }

         Function2 var19 = (Function2)var70;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var65, var19, var2, 0);
         LazyListState var66 = this.$lazyListState;
         var2.startReplaceGroup(1776085252);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var25 = var2;
         boolean var30 = var2.changed(this.$lazyListState);
         LazyListState var35 = this.$lazyListState;
         int $i$f$cachexxxx = 0;
         Object it$ivxxxx = var25.rememberedValue();
         int var50/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
         Object var72;
         if (!var30 && it$ivxxxx != Composer.Companion.getEmpty()) {
            var72 = it$ivxxxx;
         } else {
            LazyListState var61 = var66;
            int var54 = 0;
            Function2 var71 = new StreamingMessageListKt$streamingAnswerItem$1$3$1(var4, var35, var16, null);
            var66 = var61;
            Object var58 = var71;
            var25.updateRememberedValue(var58);
            var72 = var58;
         }

         Function2 var20 = (Function2)var72;
         var2.endReplaceGroup();
         EffectsKt.LaunchedEffect(var66, var20, var2, 0);
         Modifier var67 = PaddingKt.recovered$03b481d31d62885a(
            SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null), 0.0F, this.$topGap, 0.0F, 0.0F, 13, null
         );
         var2.startReplaceGroup(1776099458);
         ComposerKt.sourceInformation(var2, "CC(remember):StreamingMessageList.kt#9igjgp");
         Composer var31 = var2;
         boolean var36 = false;
         int $i$f$cachexxxxx = 0;
         Object it$ivxxxxx = var31.rememberedValue();
         int var51/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
         Object var74;
         if (it$ivxxxxx == Composer.Companion.getEmpty()) {
            Modifier var62 = var67;
            int var55 = 0;
            final androidx.compose.runtime.MutableIntState recoveredExpressionCapture_483_0 = var4;
            Function1 var73 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_483_0) -> io.intercom.android.sdk.m5.conversation.ui.components.StreamingMessageListKt$streamingAnswerItem$1.invoke$lambda$10$lambda$9(recoveredExpressionCapture_483_0, (androidx.compose.ui.unit.IntSize) ((androidx.compose.ui.unit.IntSize)recoveredExpressionArgument_483_0)));
            var67 = var62;
            Object var59 = var73;
            var31.updateRememberedValue(var59);
            var74 = var59;
         } else {
            var74 = it$ivxxxxx;
         }

         Function1 var26 = (Function1)var74;
         var2.endReplaceGroup();
         Modifier var21 = OnRemeasuredModifierKt.onSizeChanged(var67, var26);
         StreamingMessageContentKt.StreamingMessageContent(this.$stateFlow, var21, true, var2, 384, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final int invoke$lambda$1(MutableIntState var0) {
      IntState var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return var1.getIntValue();
   }

   private static final void invoke$lambda$2(MutableIntState var0, int var1) {
      MutableIntState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setIntValue(var1);
   }

   private static final boolean invoke$lambda$4(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void invoke$lambda$5(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit invoke$lambda$10$lambda$9(MutableIntState var0, IntSize var1) {
      long var2 = var1.recovered$45f9de7b85e0e0b5();
      int var4 = 0;
      long var5 = var2;
      int var7 = 0;
      invoke$lambda$2(var0, (int)(var5 & 4294967295L));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((LazyItemScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$invoke$lambda$5(MutableState var0, boolean var1) {
      invoke$lambda$5(var0, var1);
   }

   // $VF: synthetic method
   public static final boolean access$invoke$lambda$4(MutableState var0) {
      return invoke$lambda$4(var0);
   }

   // $VF: synthetic method
   public static final int access$invoke$lambda$1(MutableIntState var0) {
      return invoke$lambda$1(var0);
   }
}
