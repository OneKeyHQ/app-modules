package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import android.text.Spanned;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import androidx.core.text.HtmlCompat;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.survey.block.BlockExtensionsKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPushNotificationsBanner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/PushNotificationsBannerKt$PushNotificationsBanner$2$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,108:1\n118#2:109\n118#2:142\n118#2:144\n99#3:110\n96#3,9:111\n106#3:148\n81#4,6:120\n88#4,6:135\n96#4:147\n402#5,9:126\n411#5:141\n412#5,2:145\n75#6:143\n*S KotlinDebug\n*F\n+ 1 PushNotificationsBanner.kt\nio/intercom/android/sdk/m5/conversation/ui/components/PushNotificationsBannerKt$PushNotificationsBanner$2$1\n*L\n63#1:109\n68#1:142\n79#1:144\n59#1:110\n59#1:111,9\n59#1:148\n59#1:120,6\n59#1:135,6\n59#1:147\n59#1:126,9\n59#1:141\n59#1:145,2\n74#1:143\n*E\n"
)
final class PushNotificationsBannerKt$PushNotificationsBanner$2$1 implements Function2 {
   // $VF: synthetic field
   final Function0 $onClose;

   PushNotificationsBannerKt$PushNotificationsBanner$2$1(Function0 var1) {
      this.$onClose = var1;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C58@2041L1528:PushNotificationsBanner.kt#o19g89");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1181560824,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.PushNotificationsBanner.<anonymous>.<anonymous> (PushNotificationsBanner.kt:58)"
            );
         }

         Vertical var3 = Alignment.Companion.getCenterVertically();
         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var5 = 16;
         int var6 = 0;
         Modifier var4 = PaddingKt.recovered$03b481d31d62885a(var10000, Dp.recovered$95b3d103663bf060(var5), 0.0F, 0.0F, 0.0F, 14, null);
         Function0 var39 = this.$onClose;
         int var7 = 390;
         int var8 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var38 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var9 = RowKt.rowMeasurePolicy(var38, var3, var1, 14 & var7 >> 3 | 112 & var7 >> 3);
         Modifier var10 = var4;
         MeasurePolicy var11 = var9;
         Composer var12 = var1;
         int var13 = 112 & var7 << 3;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(var12, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var15 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var12, 0));
         CompositionLocalMap var16 = var12.getCurrentCompositionLocalMap();
         Modifier var17 = ComposedModifierKt.materializeModifier(var12, var10);
         Function0 var18 = ComposeUiNode.Companion.getConstructor();
         Composer var19 = var12;
         int var20 = 6 | 896 & var13 << 6;
         int var21 = 0;
         ComposerKt.sourceInformationMarkerStart(var19, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var19.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var19.startReusableNode();
         if (var19.getInserting()) {
            var19.createNode(var18);
         } else {
            var19.useNode();
         }

         Composer var22 = Updater.recovered$676aa17b44fd6280(var19);
         int var23 = 0;
         Updater.recovered$89682b199a142880(var22, var11, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var22, var16, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var22, var15, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var22, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var22, var17, ComposeUiNode.Companion.getSetModifier());
         int var24 = 14 & var20 >> 6;
         Composer var25 = var19;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var27 = 6 | 112 & var7 >> 6;
         Composer var28 = var25;
         RowScope var29 = RowScopeInstance.INSTANCE;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var28,
            -886648301,
            "C65@2295L56,68@2513L6,64@2259L290,71@2620L60,73@2785L7,79@3009L10,80@3076L6,74@2810L302,82@3129L426:PushNotificationsBanner.kt#o19g89"
         );
         Painter var46 = PainterResources_androidKt.painterResource(drawable.intercom_ticket_notification, var28, 0);
         Modifier var10002 = Modifier.Companion;
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         IconKt.recovered$e00adffb7fe45b02(
            var46,
            null,
            SizeKt.recovered$111ef94c93e78c04(var10002, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
            IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).getIcon().recovered$0aef7413383f6122(),
            var28,
            432 | Painter.$stable,
            0
         );
         Spanned var47 = HtmlCompat.fromHtml(StringResources_androidKt.stringResource(string.intercom_notification_enable_prompt, var28, 0), 0);
         Intrinsics.checkNotNullExpressionValue(var47, "fromHtml(...)");
         CharSequence var48 = var47;
         CompositionLocal var41 = AndroidCompositionLocals_androidKt.getLocalContext();
         int $changed$ivx = 0;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var28, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var35 = var28.consume(var41);
         ComposerKt.sourceInformationMarkerEnd(var28);
         AnnotatedString var40 = BlockExtensionsKt.recovered$c71786fddfec5215(var48, (Context)var35, null, 0L, 6, null);
         var10000 = RowScope.weight$default(var29, Modifier.Companion, 1.0F, false, 2, null);
         $changed$ivx = 8;
         var34 = 0;
         Modifier var42 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060($changed$ivx));
         TextStyle var44 = IntercomTheme.INSTANCE.getTypography(var28, IntercomTheme.$stable).getType04Point5();
         long var36 = IntercomTheme.INSTANCE.getColors(var28, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
         TextKt.recovered$489343e57da77a35(
            var40, var42, var36, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var44, var28, 0, 0, 262136
         );
         IconButtonKt.IconButton(
            var39, null, false, null, null, null, ComposableSingletons$PushNotificationsBannerKt.INSTANCE.recovered$eac478aa65045e5d(), var28, 1572864, 62
         );
         ComposerKt.sourceInformationMarkerEnd(var28);
         ComposerKt.sourceInformationMarkerEnd(var25);
         var19.endNode();
         ComposerKt.sourceInformationMarkerEnd(var19);
         ComposerKt.sourceInformationMarkerEnd(var12);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
