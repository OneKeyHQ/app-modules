package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.FlowRowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.SpanStyle;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.AnnotatedString.Builder;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.EventLabelSegment;
import io.intercom.android.sdk.models.Weight;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nEventRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EventRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/EventRowKt$EventRow$1\n+ 2 AnnotatedString.kt\nandroidx/compose/ui/text/AnnotatedStringKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,136:1\n1580#2:137\n1398#2,6:139\n1869#3:138\n1870#3:145\n1047#4,6:146\n*S KotlinDebug\n*F\n+ 1 EventRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/EventRowKt$EventRow$1\n*L\n59#1:137\n77#1:139,6\n60#1:138\n60#1:145\n93#1:146,6\n*E\n"
)
final class EventRowKt$EventRow$1 implements Function3 {
   // $VF: synthetic field
   final float $avatarSize;
   // $VF: synthetic field
   final AvatarWrapper $avatar;
   // $VF: synthetic field
   final String $contentDescription;
   // $VF: synthetic field
   final TextStyle $textStyle;
   // $VF: synthetic field
   final List $labelSegments;

   EventRowKt$EventRow$1(float var1, AvatarWrapper var2, String var3, TextStyle var4, List var5) {
      this.$avatarSize = var1;
      this.$avatar = var2;
      this.$contentDescription = var3;
      this.$textStyle = var4;
      this.$labelSegments = var5;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(FlowRowScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$FlowRow");
      ComposerKt.sourceInformation(var2, "C51@2114L162,92@3674L76,88@3522L239:EventRow.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(639828375, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.EventRow.<anonymous> (EventRow.kt:51)");
         }

         AvatarIconKt.recovered$f5ec3c81e3146789(
            SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, this.$avatarSize),
            this.$avatar,
            null,
            false,
            0L,
            Color.recovered$f46ef195ad4a2363(ColorKt.Color(4294046193L)),
            var2,
            196614,
            28
         );
         var2.startReplaceGroup(-1441491262);
         ComposerKt.sourceInformation(var2, "");
         List var6 = this.$labelSegments;
         int var7 = 0;
         Builder var8 = new Builder(0, 1, null);
         Builder var9 = var8;
         int var10 = 0;
         var2.startReplaceGroup(-1441489753);
         ComposerKt.sourceInformation(var2, "");
         Iterable var11 = var6;
         int var12 = 0;

         for (Object var14 : var11) {
            EventLabelSegment var15 = (EventLabelSegment)var14;
            int var16 = 0;
            Weight var10000 = var15.getWeight();
            FontWeight var46;
            switch (var10000 == null ? -1 : EventRowKt$EventRow$1$WhenMappings.$EnumSwitchMapping$0[var10000.ordinal()]) {
               case -1:
                  var46 = FontWeight.Companion.getNormal();
                  break;
               case 0:
               default:
                  throw new NoWhenBranchMatchedException();
               case 1:
                  var46 = FontWeight.Companion.getNormal();
                  break;
               case 2:
                  var46 = FontWeight.Companion.getLight();
                  break;
               case 3:
                  var46 = FontWeight.Companion.getMedium();
                  break;
               case 4:
                  var46 = FontWeight.Companion.getSemiBold();
                  break;
               case 5:
                  var46 = FontWeight.Companion.getBold();
            }

            FontWeight var17 = var46;
            long var47;
            if (var15.getWeight() != null && var15.getWeight() != Weight.REGULAR) {
               var2.startReplaceGroup(1299692337);
               ComposerKt.sourceInformation(var2, "71@3059L6");
               long var44 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
               var2.endReplaceGroup();
               var47 = var44;
            } else {
               var2.startReplaceGroup(1299770643);
               ComposerKt.sourceInformation(var2, "73@3138L6");
               long var18 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
               var2.endReplaceGroup();
               var47 = var18;
            }

            long var20 = var47;
            Builder var22 = var9;
            SpanStyle var23 = new SpanStyle(var20, 0L, var17, null, null, null, null, 0L, null, null, null, 0L, null, null, null, null, 65530, null);
            int var24 = 0;
            int var25 = var22.pushStyle(var23);

            try {
               Builder var26 = var22;
               int var27 = 0;
               var26.append(var15.getText());
               Unit var45 = Unit.INSTANCE;
            } finally {
               var22.pop(var25);
            }
         }

         var2.endReplaceGroup();
         AnnotatedString var5 = var8.toAnnotatedString();
         var2.endReplaceGroup();
         AnnotatedString var4 = var5;
         int var33 = TextAlign.Companion.recovered$d707b9dd64876179();
         Modifier var48 = Modifier.Companion;
         boolean var10001 = false;
         var2.startReplaceGroup(-1441450794);
         ComposerKt.sourceInformation(var2, "CC(remember):EventRow.kt#9igjgp");
         Composer var36 = var2;
         boolean var37 = var2.changed(this.$contentDescription);
         String var38 = this.$contentDescription;
         int var39 = 0;
         Object var40 = var36.rememberedValue();
         int var41 = 0;
         Object var10002;
         if (!var37 && var40 != Composer.Companion.getEmpty()) {
            var10002 = var40;
         } else {
            boolean var29 = false;
            Modifier var28 = var48;
            int var42 = 0;
            final java.lang.String recoveredExpressionCapture_704_0 = var38;
            Function1 var30 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_704_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.EventRowKt$EventRow$1.invoke$lambda$4$lambda$3(recoveredExpressionCapture_704_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver) ((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredExpressionArgument_704_0)));
            var48 = var28;
            var10001 = var29;
            Object var43 = var30;
            var36.updateRememberedValue(var43);
            var10002 = var43;
         }

         Function1 var35 = (Function1)var10002;
         var2.endReplaceGroup();
         Modifier var34 = SemanticsModifierKt.semantics$default(var48, var10001, var35, 1, null);
         TextKt.recovered$489343e57da77a35(
            var4,
            var34,
            0L,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var33),
            0L,
            0,
            false,
            0,
            0,
            null,
            null,
            this.$textStyle,
            var2,
            0,
            0,
            261116
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$3(String var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, var0);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((FlowRowScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
