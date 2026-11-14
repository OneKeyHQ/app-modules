package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPreviewTopBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$PreviewTopBarKt$lambda-5$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,108:1\n1047#2,6:109\n1047#2,6:115\n*S KotlinDebug\n*F\n+ 1 PreviewTopBar.kt\nio/intercom/android/sdk/m5/preview/ui/ComposableSingletons$PreviewTopBarKt$lambda-5$1\n*L\n102#1:109,6\n103#1:115,6\n*E\n"
)
final class ComposableSingletons$PreviewTopBarKt$RecoveredInner117a8bc0ad94bbf2$1 implements Function2 {
   public static final ComposableSingletons$PreviewTopBarKt$RecoveredInner117a8bc0ad94bbf2$1 INSTANCE = new ComposableSingletons$PreviewTopBarKt$RecoveredInner117a8bc0ad94bbf2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C101@3346L2,102@3382L2,97@3178L221:PreviewTopBar.kt#hhbc0c");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               704118025, var2, -1, "io.intercom.android.sdk.m5.preview.ui.ComposableSingletons$PreviewTopBarKt.lambda-5.<anonymous> (PreviewTopBar.kt:97)"
            );
         }

         Object var10000 = null;
         boolean var10001 = true;
         boolean var10002 = true;
         boolean var10003 = true;
         var1.startReplaceGroup(-948368772);
         ComposerKt.sourceInformation(var1, "CC(remember):PreviewTopBar.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var10004;
         if (var7 == Composer.Companion.getEmpty()) {
            boolean var13 = true;
            boolean var12 = true;
            boolean var11 = true;
            Object var10 = null;
            int var9 = 0;
            Function0 var14 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PreviewTopBarKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$1$lambda$0());
            var10000 = var10;
            var10001 = var11;
            var10002 = var12;
            var10003 = var13;
            Object var22 = var14;
            var4.updateRememberedValue(var22);
            var10004 = (Function0)var22;
         } else {
            var10004 = (Function0)var7;
         }

         Function0 var3 = (kotlin.jvm.functions.Function0) var10004;
         var1.endReplaceGroup();
         var10004 = var3;
         var1.startReplaceGroup(-948367620);
         ComposerKt.sourceInformation(var1, "CC(remember):PreviewTopBar.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10005;
         if (var7 == Composer.Companion.getEmpty()) {
            Function0 var29 = var3;
            boolean var28 = var10003;
            boolean var27 = var10002;
            boolean var26 = var10001;
            Object var25 = var10000;
            int var23 = 0;
            Function0 var15 = ((kotlin.jvm.functions.Function0) () -> ComposableSingletons$PreviewTopBarKt$RecoveredInner117a8bc0ad94bbf2$1.invoke$lambda$3$lambda$2());
            var10000 = var25;
            var10001 = var26;
            var10002 = var27;
            var10003 = var28;
            var10004 = var29;
            Object var24 = var15;
            var4.updateRememberedValue(var24);
            var10005 = var24;
         } else {
            var10005 = var7;
         }

         var3 = (Function0)var10005;
         var1.endReplaceGroup();
         PreviewTopBarKt.PreviewTopBar((Modifier)var10000, var10001, var10002, var10003, (kotlin.jvm.functions.Function0) var10004, var3, null, null, var1, 224688, 193);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2() {
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
