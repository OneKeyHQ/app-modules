package io.intercom.android.sdk.m5.home.ui.components;

import android.content.Context;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.AsyncImageKt;
import coil.request.ImageRequest;
import coil.request.ImageRequest.Builder;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData;
import io.intercom.android.sdk.m5.home.data.TicketLink;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nTicketLinksCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TicketLinksCard.kt\nio/intercom/android/sdk/m5/home/ui/components/TicketLinksCardKt$TicketLinksCard$1\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 7 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 9 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,115:1\n87#2:116\n83#2,10:117\n94#2:205\n81#3,6:127\n88#3,6:142\n81#3,6:170\n88#3,6:185\n96#3:198\n96#3:204\n402#4,9:133\n411#4:148\n402#4,9:176\n411#4:191\n412#4,2:196\n412#4,2:202\n118#5:149\n118#5:150\n118#5:159\n118#5:192\n118#5:193\n118#5:200\n1878#6,2:151\n1880#6:201\n1047#7,6:153\n99#8:160\n96#8,9:161\n106#8:199\n75#9:194\n75#9:195\n*S KotlinDebug\n*F\n+ 1 TicketLinksCard.kt\nio/intercom/android/sdk/m5/home/ui/components/TicketLinksCardKt$TicketLinksCard$1\n*L\n38#1:116\n38#1:117,10\n38#1:205\n38#1:127,6\n38#1:142,6\n49#1:170,6\n49#1:185,6\n49#1:198\n38#1:204\n38#1:133,9\n38#1:148\n49#1:176,9\n49#1:191\n49#1:196,2\n38#1:202,2\n42#1:149\n43#1:150\n53#1:159\n63#1:192\n65#1:193\n79#1:200\n48#1:151,2\n48#1:201\n52#1:153,6\n49#1:160\n49#1:161,9\n49#1:199\n66#1:194\n70#1:195\n*E\n"
)
final class TicketLinksCardKt$TicketLinksCard$1 implements Function3 {
   // $VF: synthetic field
   final HomeCards$HomeTicketLinksData $ticketLinksData;
   // $VF: synthetic field
   final Function1 $onTicketLinkClicked;

   TicketLinksCardKt$TicketLinksCard$1(HomeCards$HomeTicketLinksData var1, Function1 var2) {
      this.$ticketLinksData = var1;
      this.$onTicketLinkClicked = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C37@1579L2067:TicketLinksCard.kt#utrtal");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1008312684, var3, -1, "io.intercom.android.sdk.m5.home.ui.components.TicketLinksCard.<anonymous> (TicketLinksCard.kt:37)"
            );
         }

         HomeCards$HomeTicketLinksData var7 = this.$ticketLinksData;
         Function1 var8 = this.$onTicketLinkClicked;
         int var9 = 0;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var4 = Modifier.Companion;
         Vertical var5 = Arrangement.INSTANCE.getTop();
         Horizontal var6 = Alignment.Companion.getStart();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var5, var6, var2, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var4;
         MeasurePolicy var13 = var11;
         Composer var14 = var2;
         int var15 = 112 & var9 << 3;
         int var16 = 0;
         ComposerKt.sourceInformationMarkerStart(var14, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var17 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var14, 0));
         CompositionLocalMap var18 = var14.getCurrentCompositionLocalMap();
         Modifier var19 = ComposedModifierKt.materializeModifier(var14, var12);
         Function0 var20 = ComposeUiNode.Companion.getConstructor();
         Composer var21 = var14;
         int var22 = 6 | 896 & var15 << 6;
         int var23 = 0;
         ComposerKt.sourceInformationMarkerStart(var21, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var21.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var21.startReusableNode();
         if (var21.getInserting()) {
            var21.createNode(var20);
         } else {
            var21.useNode();
         }

         Composer var24 = Updater.recovered$676aa17b44fd6280(var21);
         int var25 = 0;
         Updater.recovered$89682b199a142880(var24, var13, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var24, var18, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var24, var17, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var24, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var24, var19, ComposeUiNode.Companion.getSetModifier());
         int var26 = 14 & var22 >> 6;
         Composer var27 = var21;
         int var28 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 1583390959, "C:TicketLinksCard.kt#utrtal");
         var30.startReplaceGroup(466717469);
         ComposerKt.sourceInformation(var30, "44@1923L10,39@1666L301");
         CharSequence var33 = var7.getCardTitle();
         if (var33 != null && !StringsKt.isBlank(var33)) {
            Modifier var10000 = Modifier.Companion;
            int var34 = 16;
            int var35 = 0;
            var10000 = PaddingKt.recovered$f3b9f2e94c07c806(var10000, Dp.recovered$95b3d103663bf060(var34), 0.0F, 2, null);
            var34 = 12;
            var35 = 0;
            float var10002 = Dp.recovered$95b3d103663bf060(var34);
            var34 = 4;
            var35 = 0;
            Modifier var82 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, var10002, 0.0F, Dp.recovered$95b3d103663bf060(var34), 5, null);
            String var86 = var7.getCardTitle();
            TextStyle var90 = IntercomTheme.INSTANCE.getTypography(var30, IntercomTheme.$stable).getType04SemiBold();
            TextKt.recovered$5b114ab32a4f4190(
               var86, var82, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var90, var30, 48, 0, 131068
            );
         }

         var30.endReplaceGroup();
         var30.startReplaceGroup(466732020);
         ComposerKt.sourceInformation(var30, "*51@2184L40,48@2064L1272");
         Iterable var83 = var7.getLinks();
         int var87 = 0;
         int var91 = 0;

         for (Object var37 : var83) {
            int var38 = var91++;
            if (var38 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            TicketLink var39 = (TicketLink)var37;
            int var40 = var38;
            int var41 = 0;
            Modifier var121 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            boolean var10001 = false;
            Object var127 = null;
            Object var10003 = null;
            Object var10004 = null;
            var30.startReplaceGroup(595961031);
            ComposerKt.sourceInformation(var30, "CC(remember):TicketLinksCard.kt#9igjgp");
            Composer var42 = var30;
            boolean var43 = var30.changed(var8) | var30.changedInstance(var39);
            int var44 = 0;
            Object var45 = var42.rememberedValue();
            int var46 = 0;
            Object var10005;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var10005 = var45;
            } else {
               Object var47 = null;
               Object var48 = null;
               Object var49 = null;
               boolean var50 = false;
               Modifier var51 = var121;
               int var52 = 0;
               final kotlin.jvm.functions.Function1 recoveredCapture_930_0 = var8;
               final io.intercom.android.sdk.m5.home.data.TicketLink recoveredCapture_930_1 = var39;
               Function0 var53 = ((kotlin.jvm.functions.Function0) () -> TicketLinksCardKt$TicketLinksCard$1.invoke$lambda$4$lambda$3$lambda$1$lambda$0(recoveredCapture_930_0, recoveredCapture_930_1));
               var121 = var51;
               var10001 = var50;
               var127 = var49;
               var10003 = var48;
               var10004 = var47;
               Object var54 = var53;
               var42.updateRememberedValue(var54);
               var10005 = var54;
            }

            Function0 var55 = (Function0)var10005;
            var30.endReplaceGroup();
            var121 = ClickableKt.recovered$1c9660c41fd850dc(
               var121, var10001, (String)var127, (Role)var10003, (MutableInteractionSource)var10004, var55, 15, null
            );
            int var102 = 16;
            int var92 = 0;
            float var126 = Dp.recovered$95b3d103663bf060(var102);
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            Modifier var104 = PaddingKt.recovered$904403cea4c49d95(var121, var126, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            androidx.compose.ui.Alignment.Vertical var96 = Alignment.Companion.getCenterVertically();
            short var97 = 384;
            var45 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Horizontal var94 = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var94, var96, var30, 14 & var97 >> 3 | 112 & var97 >> 3);
            Modifier var100 = var104;
            MeasurePolicy var101 = measurePolicy$ivx;
            Composer var56 = var30;
            int var57 = 112 & var97 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var56, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var56, 0));
            CompositionLocalMap localMap$iv$ivx = var56.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var56, var100);
            Function0 var62 = ComposeUiNode.Companion.getConstructor();
            Composer var63 = var56;
            int var64 = 6 | 896 & var57 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var63, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var63.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var63.startReusableNode();
            if (var63.getInserting()) {
               var63.createNode(var62);
            } else {
               var63.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var63);
            int var67/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var101, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var68 = 14 & var64 >> 6;
            Composer $composer$ivx = var63;
            int var70 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var71 = 6 | 112 & var97 >> 6;
            Composer var72 = $composer$ivx;
            RowScope var73 = RowScopeInstance.INSTANCE;
            int var74 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var72, -1377412460, "C55@2405L287,62@2713L40,65@2909L7,69@3115L7,71@3268L6,63@2774L544:TicketLinksCard.kt#utrtal"
            );
            Modifier var75 = RowScope.weight$default(var73, Modifier.Companion, 1.0F, false, 2, null);
            String var76 = var39.getTicketTypeName();
            FontWeight var77 = FontWeight.Companion.getNormal();
            int $changed$ivx = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var76, var75, 0L, null, 0L, null, var77, null, 0L, null, null, 0L, $changed$ivx, false, 1, 0, null, null, var72, 1572864, 24960, 241596
            );
            var121 = Modifier.Companion;
            int $this$dp$ivxx = 16;
            int $i$f$getDpxx = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var121, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var72, 6);
            var121 = Modifier.Companion;
            int $this$dp$ivxxx = 16;
            int $i$f$getDpxxx = 0;
            var75 = SizeKt.recovered$111ef94c93e78c04(var121, Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
            CompositionLocal var112 = AndroidCompositionLocals_androidKt.getLocalContext();
            $changed$ivx = 0;
            int var79 = 0;
            ComposerKt.sourceInformationMarkerStart(var72, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var80 = var72.consume(var112);
            ComposerKt.sourceInformationMarkerEnd(var72);
            ImageRequest var110 = new Builder((Context)var80).data(var39.getIconUrl()).crossfade(true).build();
            CompositionLocal var115 = AndroidCompositionLocals_androidKt.getLocalContext();
            var79 = 0;
            int $i$f$getCurrentx = 0;
            ComposerKt.sourceInformationMarkerStart(var72, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var81 = var72.consume(var115);
            ComposerKt.sourceInformationMarkerEnd(var72);
            ImageLoader var113 = IntercomImageLoaderKt.getImageLoader((Context)var81);
            ColorFilter var116 = Companion.recovered$14c5668fff7aadde(
               ColorFilter.Companion, IntercomTheme.INSTANCE.getColors(var72, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d(), 0, 2, null
            );
            AsyncImageKt.recovered$b6a46e909c1fcc39(var110, null, var113, var75, null, null, null, null, 0.0F, var116, 0, false, null, var72, 3120, 0, 7664);
            ComposerKt.sourceInformationMarkerEnd(var72);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var63.endNode();
            ComposerKt.sourceInformationMarkerEnd(var63);
            ComposerKt.sourceInformationMarkerEnd(var56);
            ComposerKt.sourceInformationMarkerEnd(var30);
            var30.startReplaceGroup(466773453);
            ComposerKt.sourceInformation(var30, "75@3420L184");
            if (var40 != var7.getLinks().size() - 1) {
               var121 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
               int $this$dp$ivxxxx = 16;
               int $i$f$getDpxxxx = 0;
               IntercomDividerKt.IntercomDivider(
                  PaddingKt.recovered$f3b9f2e94c07c806(var121, Dp.recovered$95b3d103663bf060($this$dp$ivxxxx), 0.0F, 2, null), var30, 6, 0
               );
            }

            var30.endReplaceGroup();
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$3$lambda$1$lambda$0(Function1 var0, TicketLink var1) {
      var0.invoke(var1.getTicketType());
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
