package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.ui.common.StringProvider;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationKebab.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationKebab.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationKebabKt$ConversationKebab$3$3$3\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,172:1\n1869#2:173\n1870#2:180\n1047#3,6:174\n*S KotlinDebug\n*F\n+ 1 ConversationKebab.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationKebabKt$ConversationKebab$3$3$3\n*L\n95#1:173\n95#1:180\n103#1:174,6\n*E\n"
)
final class ConversationKebabKt$ConversationKebab$3$3$3 implements Function3 {
   // $VF: synthetic field
   final List $headerMenuItems;
   // $VF: synthetic field
   final Function1 $onMenuClicked;
   // $VF: synthetic field
   final Function1 $trackMetric;
   // $VF: synthetic field
   final long $contentColor;
   // $VF: synthetic field
   final MutableState $isExpanded;

   ConversationKebabKt$ConversationKebab$3$3$3(List var1, Function1 var2, Function1 var3, long var4, MutableState var6) {
      this.$headerMenuItems = var1;
      this.$onMenuClicked = var2;
      this.$trackMetric = var3;
      this.$contentColor = var4;
      this.$isExpanded = var6;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$DropdownMenu");
      ComposerKt.sourceInformation(var2, "C*96@4016L9,102@4354L239,95@3948L783:ConversationKebab.kt#o19g89");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1309645384,
               var3,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebab.<anonymous>.<anonymous>.<anonymous> (ConversationKebab.kt:94)"
            );
         }

         Iterable var4 = this.$headerMenuItems;
         Function1 var5 = this.$onMenuClicked;
         Function1 var6 = this.$trackMetric;
         long var7 = this.$contentColor;
         MutableState var9 = this.$isExpanded;
         int var10 = 0;

         for (Object var12 : var4) {
            HeaderMenuItem var13 = (HeaderMenuItem)var12;
            int var14 = 0;
            String var15 = var13.getLabel().getText(var2, StringProvider.$stable);
            int var16 = ConversationKebabKt.access$icon(var13);
            String var17 = ConversationKebabKt.access$badgeText(var13);
            boolean var18 = ConversationKebabKt.access$shouldShowUnreadDot(var13);
            boolean var19 = var13.getEnabled();
            String var10000 = var15;
            boolean var10001 = var19;
            int var10002 = var16;
            Object var10003 = null;
            String var10004 = var17;
            boolean var10005 = true;
            boolean var10006 = var18;
            boolean var10007 = true;
            var2.startReplaceGroup(1730714714);
            ComposerKt.sourceInformation(var2, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var20 = var2;
            boolean var21 = var2.changed(var5) | var2.changed(var13) | var2.changed(var6);
            int var22 = 0;
            Object var23 = var20.rememberedValue();
            int var24 = 0;
            Object var10008;
            if (!var21 && var23 != Composer.Companion.getEmpty()) {
               var10008 = var23;
            } else {
               boolean var25 = true;
               boolean var26 = var18;
               boolean var27 = true;
               String var28 = var17;
               Object var29 = null;
               int var30 = var16;
               boolean var31 = var19;
               String var32 = var15;
               int var33 = 0;
               final androidx.compose.runtime.MutableState recoveredCapture_418_0 = var9;
               final kotlin.jvm.functions.Function1 recoveredCapture_418_1 = var5;
               final io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem recoveredCapture_418_2 = var13;
               final kotlin.jvm.functions.Function1 recoveredCapture_418_3 = var6;
               Function0 var34 = ((kotlin.jvm.functions.Function0) () -> ConversationKebabKt$ConversationKebab$3$3$3.invoke$lambda$2$lambda$1$lambda$0(recoveredCapture_418_0, recoveredCapture_418_1, recoveredCapture_418_2, recoveredCapture_418_3));
               var10000 = var32;
               var10001 = var31;
               var10002 = var30;
               var10003 = var29;
               var10004 = var28;
               var10005 = var27;
               var10006 = var26;
               var10007 = var25;
               Object var35 = var34;
               var20.updateRememberedValue(var35);
               var10008 = var35;
            }

            Function0 var36 = (Function0)var10008;
            var2.endReplaceGroup();
            HeaderMenuItemRowKt.recovered$04990d299bee3c60(
               var10000, var10001, var10002, (Modifier)var10003, var10004, var10005, var10006, var10007, var36, var7, var2, 12779520, 8
            );
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$2$lambda$1$lambda$0(MutableState var0, Function1 var1, HeaderMenuItem var2, Function1 var3) {
      var0.setValue(false);
      var1.invoke(var2);
      var3.invoke(ConversationKebabKt.access$metricData(var2, true));
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
