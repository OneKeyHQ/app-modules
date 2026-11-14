package io.intercom.android.sdk.m5.home.ui.components;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import io.intercom.android.sdk.blocks.messengercard.CardWebView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nLegacyMessengerAppCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LegacyMessengerAppCard.kt\nio/intercom/android/sdk/m5/home/ui/components/LegacyMessengerAppCardKt$LegacyMessengerAppCard$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 ViewGroup.kt\nandroidx/core/view/ViewGroupKt\n*L\n1#1,148:1\n1047#2,6:149\n1047#2,6:155\n48#3:161\n*S KotlinDebug\n*F\n+ 1 LegacyMessengerAppCard.kt\nio/intercom/android/sdk/m5/home/ui/components/LegacyMessengerAppCardKt$LegacyMessengerAppCard$1\n*L\n43#1:149,6\n61#1:155,6\n64#1:161\n*E\n"
)
final class LegacyMessengerAppCardKt$LegacyMessengerAppCard$1 implements Function3 {
   // $VF: synthetic field
   final String $urlWithTheme;
   // $VF: synthetic field
   final boolean $isHomeScreen;

   LegacyMessengerAppCardKt$LegacyMessengerAppCard$1(String var1, boolean var2) {
      this.$urlWithTheme = var1;
      this.$isHomeScreen = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C:LegacyMessengerAppCard.kt#utrtal");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               231006519, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.LegacyMessengerAppCard.<anonymous> (LegacyMessengerAppCard.kt:40)"
            );
         }

         var2.startMovableGroup(1530860342, this.$urlWithTheme);
         ComposerKt.sourceInformation(var2, "42@1731L852,60@2610L710,41@1692L1643");
         var2.startReplaceGroup(1530861781);
         ComposerKt.sourceInformation(var2, "CC(remember):LegacyMessengerAppCard.kt#9igjgp");
         Composer var5 = var2;
         boolean var6 = var2.changed(this.$urlWithTheme) | var2.changed(this.$isHomeScreen);
         String var7 = this.$urlWithTheme;
         boolean var8 = this.$isHomeScreen;
         int var9 = 0;
         Object var10 = var5.rememberedValue();
         int var11 = 0;
         Object var10000;
         if (!var6 && var10 != Composer.Companion.getEmpty()) {
            var10000 = (Function1)var10;
         } else {
            int var12 = 0;
            final java.lang.String recoveredCapture_918_0 = var7;
            final boolean recoveredCapture_918_1 = var8;
            Object var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_918_0) -> LegacyMessengerAppCardKt$LegacyMessengerAppCard$1.invoke$lambda$2$lambda$1(recoveredCapture_918_0, recoveredCapture_918_1, (android.content.Context)recoveredArgument_918_0));
            var5.updateRememberedValue(var13);
            var10000 = (Function1)var13;
         }

         Function1 var4 = (kotlin.jvm.functions.Function1) var10000;
         var2.endReplaceGroup();
         var10000 = var4;
         Object var10001 = null;
         var2.startReplaceGroup(1530889767);
         ComposerKt.sourceInformation(var2, "CC(remember):LegacyMessengerAppCard.kt#9igjgp");
         var5 = var2;
         var6 = var2.changed(this.$urlWithTheme) | var2.changed(this.$isHomeScreen);
         var7 = this.$urlWithTheme;
         var8 = this.$isHomeScreen;
         var9 = 0;
         var10 = var5.rememberedValue();
         var11 = 0;
         Object var10002;
         if (!var6 && var10 != Composer.Companion.getEmpty()) {
            var10002 = var10;
         } else {
            Object var15 = null;
            Function1 var14 = var4;
            int var25 = 0;
            final java.lang.String recoveredCapture_917_0 = var7;
            final boolean recoveredCapture_917_1 = var8;
            Function1 var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_917_0) -> LegacyMessengerAppCardKt$LegacyMessengerAppCard$1.invoke$lambda$4$lambda$3(recoveredCapture_917_0, recoveredCapture_917_1, (android.widget.FrameLayout)recoveredArgument_917_0));
            var10000 = var14;
            var10001 = var15;
            Object var26 = var16;
            var5.updateRememberedValue(var26);
            var10002 = var26;
         }

         var4 = (Function1)var10002;
         var2.endReplaceGroup();
         AndroidView_androidKt.AndroidView((Function1)var10000, (Modifier)var10001, var4, var2, 0, 2);
         var2.endMovableGroup();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final FrameLayout invoke$lambda$2$lambda$1(String var0, boolean var1, Context var2) {
      Intrinsics.checkNotNullParameter(var2, "factoryContext");
      FrameLayout var3 = new FrameLayout(var2);
      FrameLayout var4 = var3;
      int var5 = 0;
      var4.setLayoutParams(new LayoutParams(-1, -2));
      CardWebView var6 = LegacyMessengerAppCardKt.access$getWebView(var0, var2, var1);
      if (var6 != null) {
         ViewParent var7 = var6.getParent();
         ViewGroup var10000 = var7 instanceof ViewGroup ? (ViewGroup)var7 : null;
         if ((var7 instanceof ViewGroup ? (ViewGroup)var7 : null) != null) {
            var10000.removeView(var6);
         }

         var4.addView(var6);
      }

      return var3;
   }

   private static final Unit invoke$lambda$4$lambda$3(String var0, boolean var1, FrameLayout var2) {
      Intrinsics.checkNotNullParameter(var2, "frameLayout");
      ViewGroup var3 = var2;
      int var4 = 0;
      if (var3.getChildCount() == 0) {
         Context var10001 = var2.getContext();
         Intrinsics.checkNotNullExpressionValue(var10001, "getContext(...)");
         var3 = LegacyMessengerAppCardKt.access$getWebView(var0, var10001, var1);
         if (var3 != null) {
            ViewParent var5 = var3.getParent();
            ViewGroup var10000 = var5 instanceof ViewGroup ? (ViewGroup)var5 : null;
            if ((var5 instanceof ViewGroup ? (ViewGroup)var5 : null) != null) {
               var10000.removeView(var3);
            }

            var2.addView(var3);
         }
      }

      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
