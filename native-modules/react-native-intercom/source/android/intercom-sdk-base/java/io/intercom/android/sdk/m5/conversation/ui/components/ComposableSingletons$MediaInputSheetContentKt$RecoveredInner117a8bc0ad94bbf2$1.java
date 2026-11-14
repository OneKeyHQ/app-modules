package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.conversation.states.InputTypeState;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaInputSheetContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaInputSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$MediaInputSheetContentKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,164:1\n1047#2,6:165\n1047#2,6:171\n1047#2,6:177\n*S KotlinDebug\n*F\n+ 1 MediaInputSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ComposableSingletons$MediaInputSheetContentKt$lambda-5$1\n*L\n156#1:165,6\n157#1:171,6\n158#1:177,6\n*E\n"
)
final class ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C155@6006L2,156@6041L2,157@6081L2,154@5943L212:MediaInputSheetContent.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2443124,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.lambda-5.<anonymous> (MediaInputSheetContent.kt:154)"
            );
         }

         Object var10000 = null;
         var1.startReplaceGroup(-735245379);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaInputSheetContent.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var35;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var10 = null;
            int var9 = 0;
            var35 = ((kotlin.jvm.functions.Function1) (recoveredArgument_408_0) -> ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0((java.util.List)recoveredArgument_408_0));
            var10000 = var10;
            Object var26 = var35;
            var4.updateRememberedValue(var26);
            var35 = (Function1)var26;
         } else {
            var35 = (Function1)var7;
         }

         Function1 var3 = (kotlin.jvm.functions.Function1) var35;
         var1.endReplaceGroup();
         var35 = var3;
         var1.startReplaceGroup(-735244259);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaInputSheetContent.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var11 = var3;
            Object var31 = var10000;
            int var27 = 0;
            Function0 var12 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$3$lambda$2());
            var10000 = var31;
            var35 = var11;
            Object var28 = var12;
            var4.updateRememberedValue(var28);
            var10002 = (Function0)var28;
         } else {
            var10002 = (Function0)var7;
         }

         Function0 var14 = (kotlin.jvm.functions.Function0) var10002;
         var1.endReplaceGroup();
         var10002 = var14;
         var1.startReplaceGroup(-735242979);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaInputSheetContent.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var34 = var14;
            Function1 var33 = (kotlin.jvm.functions.Function1) var35;
            Object var32 = var10000;
            int var29 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_407_0) -> ComposableSingletons$MediaInputSheetContentKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$5$lambda$4((java.lang.String)recoveredArgument_407_0));
            var10000 = var32;
            var35 = var33;
            var10002 = var34;
            Object var30 = var13;
            var4.updateRememberedValue(var30);
            var10003 = var30;
         } else {
            var10003 = var7;
         }

         var3 = (Function1)var10003;
         var1.endReplaceGroup();
         MediaInputSheetContentKt.MediaInputSheetContent((Modifier)var10000, (kotlin.jvm.functions.Function1) var35, (kotlin.jvm.functions.Function0) var10002, var3, InputTypeState.Companion.getDEFAULT(), var1, 3504, 1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
