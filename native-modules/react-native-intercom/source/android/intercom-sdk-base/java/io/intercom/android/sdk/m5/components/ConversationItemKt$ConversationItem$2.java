package io.intercom.android.sdk.m5.components;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
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
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.m5.conversation.ui.components.row.TypingIndicatorKt;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.LastParticipatingAdmin;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ConversationItemKt$ConversationItem$2\n+ 2 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 6 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,287:1\n99#2:288\n96#2,9:289\n99#2:356\n96#2,9:357\n106#2:392\n106#2:400\n81#3,6:298\n88#3,6:313\n81#3,6:334\n88#3,6:349\n81#3,6:366\n88#3,6:381\n96#3:391\n96#3:395\n96#3:399\n402#4,9:304\n411#4:319\n402#4,9:340\n411#4:355\n402#4,9:372\n411#4:387\n412#4,2:389\n412#4,2:393\n412#4,2:397\n118#5:320\n118#5:321\n118#5:322\n118#5:324\n118#5:388\n75#6:323\n87#7:325\n85#7,8:326\n94#7:396\n*S KotlinDebug\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ConversationItemKt$ConversationItem$2\n*L\n54#1:288\n54#1:289,9\n90#1:356\n90#1:357,9\n90#1:392\n54#1:400\n54#1:298,6\n54#1:313,6\n86#1:334,6\n86#1:349,6\n90#1:366,6\n90#1:381,6\n90#1:391\n86#1:395\n54#1:399\n54#1:304,9\n54#1:319\n86#1:340,9\n86#1:355\n90#1:372,9\n90#1:387\n90#1:389,2\n86#1:393,2\n54#1:397,2\n74#1:320\n76#1:321\n82#1:322\n88#1:324\n111#1:388\n85#1:323\n86#1:325\n86#1:326,8\n86#1:396\n*E\n"
)
final class ConversationItemKt$ConversationItem$2 implements Function2 {
   // $VF: synthetic field
   final PaddingValues $contentPadding;
   // $VF: synthetic field
   final CurrentlyTypingState $typingState;
   // $VF: synthetic field
   final Conversation $conversation;
   // $VF: synthetic field
   final boolean $showUnreadIndicator;

   ConversationItemKt$ConversationItem$2(PaddingValues var1, CurrentlyTypingState var2, Conversation var3, boolean var4) {
      this.$contentPadding = var1;
      this.$typingState = var2;
      this.$conversation = var3;
      this.$showUnreadIndicator = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C53@2447L4126:ConversationItem.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(774321672, var2, -1, "io.intercom.android.sdk.m5.components.ConversationItem.<anonymous> (ConversationItem.kt:53)");
         }

         Modifier var3 = PaddingKt.padding(Modifier.Companion, this.$contentPadding);
         Vertical var5 = Alignment.Companion.getCenterVertically();
         CurrentlyTypingState var6 = this.$typingState;
         Conversation var7 = this.$conversation;
         boolean var8 = this.$showUnreadIndicator;
         int var9 = 384;
         int var10 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Horizontal var4 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var11 = RowKt.rowMeasurePolicy(var4, var5, var1, 14 & var9 >> 3 | 112 & var9 >> 3);
         Modifier var12 = var3;
         MeasurePolicy var13 = var11;
         Composer var14 = var1;
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
         ComposerKt.sourceInformationMarkerStart(var27, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var29 = 6 | 112 & var9 >> 6;
         Composer var30 = var27;
         RowScope var31 = RowScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -1058673795, "C:ConversationItem.kt#arqjag");
         var30.startReplaceGroup(1766961464);
         ComposerKt.sourceInformation(var30, "70@3230L176,75@3423L40");
         if (var6 == null || var6.getShowAvatar()) {
            List var10000;
            if (var6 != null) {
               var10000 = CollectionsKt.listOf(var6.getAvatarWrapper());
            } else if (LastParticipatingAdmin.isNull(var7.lastParticipatingAdmin())) {
               var10000 = ConversationItemKt.access$getActiveAdminsAvatars();
            } else {
               Avatar var10002 = var7.lastParticipatingAdmin().getAvatar();
               Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
               var10000 = CollectionsKt.listOf(new AvatarWrapper(var10002, var7.lastParticipatingAdmin().isBot()));
            }

            List var33 = var10000;
            Modifier var10001 = var31.align(Modifier.Companion, Alignment.Companion.getCenterVertically());
            int var34 = 32;
            int var35 = 0;
            AvatarTriangleGroupKt.recovered$617ccf65fce42f20(var33, var10001, null, Dp.recovered$95b3d103663bf060(var34), var30, 3072, 4);
            Modifier var122 = Modifier.Companion;
            var34 = 12;
            var35 = 0;
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var122, Dp.recovered$95b3d103663bf060(var34)), var30, 6);
         }

         var30.endReplaceGroup();
         if (var6 != null) {
            var30.startReplaceGroup(-1057866277);
            ComposerKt.sourceInformation(var30, "78@3533L173");
            Modifier var123 = RowScope.weight$default(var31, Modifier.Companion, 2.0F, false, 2, null);
            int var99 = 38;
            int var102 = 0;
            TypingIndicatorKt.AnimatedTypingDots(SizeKt.recovered$da9aba2b92020277(var123, 0.0F, Dp.recovered$95b3d103663bf060(var99), 1, null), var30, 0, 0);
            var30.endReplaceGroup();
         } else {
            var30.startReplaceGroup(-1057575373);
            ComposerKt.sourceInformation(var30, "84@3771L7,85@3795L2655,141@6514L17");
            CompositionLocal var103 = AndroidCompositionLocals_androidKt.getLocalContext();
            int $changed$ivx = 0;
            int var36 = 0;
            ComposerKt.sourceInformationMarkerStart(var30, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var37 = var30.consume(var103);
            ComposerKt.sourceInformationMarkerEnd(var30);
            Context var100 = (Context)var37;
            Modifier var104 = RowScope.weight$default(var31, Modifier.Companion, 2.0F, false, 2, null);
            $changed$ivx = 2;
            var36 = 0;
            androidx.compose.foundation.layout.Arrangement.Vertical var108 = Arrangement.INSTANCE
               .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($changed$ivx));
            byte var111 = 48;
            int var38 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var30, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            androidx.compose.ui.Alignment.Horizontal var110 = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var108, var110, var30, 14 & var111 >> 3 | 112 & var111 >> 3);
            Modifier var40 = var104;
            MeasurePolicy var41 = measurePolicy$ivx;
            Composer var42 = var30;
            int var43 = 112 & var111 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var42, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var42, 0));
            CompositionLocalMap localMap$iv$ivx = var42.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var42, var40);
            Function0 var48 = ComposeUiNode.Companion.getConstructor();
            Composer var49 = var42;
            int var50 = 6 | 896 & var43 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var49, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var49.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var49.startReusableNode();
            if (var49.getInserting()) {
               var49.createNode(var48);
            } else {
               var49.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var49);
            int var53/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var41, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var54 = 14 & var50 >> 6;
            Composer $composer$ivx = var49;
            int var56 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var57 = 6 | 112 & var111 >> 6;
            Composer var58 = $composer$ivx;
            ColumnScope var59 = ColumnScopeInstance.INSTANCE;
            int var60 = 0;
            ComposerKt.sourceInformationMarkerStart(var58, -1132088217, "C89@3965L1188:ConversationItem.kt#arqjag");
            Modifier var61 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
            Vertical var62 = Alignment.Companion.getCenterVertically();
            short var63 = 390;
            int $i$f$Rowx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var58, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Horizontal horizontalArrangement$ivx = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(horizontalArrangement$ivx, var62, var58, 14 & var63 >> 3 | 112 & var63 >> 3);
            Modifier var67 = var61;
            MeasurePolicy var68 = measurePolicy$ivxx;
            Composer var69 = var58;
            int var70 = 112 & var63 << 3;
            int $i$f$Layoutxx = 0;
            ComposerKt.sourceInformationMarkerStart(var69, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var69, 0));
            CompositionLocalMap localMap$iv$ivxx = var69.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var69, var67);
            Function0 var75 = ComposeUiNode.Companion.getConstructor();
            Composer var76 = var69;
            int var77 = 6 | 896 & var70 << 6;
            int $i$f$ReusableComposeNodexx = 0;
            ComposerKt.sourceInformationMarkerStart(var76, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var76.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var76.startReusableNode();
            if (var76.getInserting()) {
               var76.createNode(var75);
            } else {
               var76.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var76);
            int var80/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var68, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
            int var81 = 14 & var77 >> 6;
            Composer $composer$ivxx = var76;
            int var83/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var84 = 6 | 112 & var63 >> 6;
            Composer var85 = $composer$ivxx;
            RowScope var86 = RowScopeInstance.INSTANCE;
            int var87 = 0;
            ComposerKt.sourceInformationMarkerStart(var85, 545125164, "C98@4415L10,93@4140L469:ConversationItem.kt#arqjag");
            String var88 = var7.getTitle();
            Modifier var89 = RowScope.weight$default(var86, Modifier.Companion, 1.0F, false, 2, null);
            int var90 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextStyle var91 = TextStyle.recovered$cf01d8b71690b5c5(
               IntercomTheme.INSTANCE.getTypography(var85, IntercomTheme.$stable).getType04(),
               0L,
               0L,
               var7.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold(),
               null,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               null,
               0,
               0,
               0L,
               null,
               null,
               null,
               0,
               0,
               null,
               16777211,
               null
            );
            TextKt.recovered$5b114ab32a4f4190(
               var88, var89, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var90, false, 1, 0, null, var91, var85, 0, 24960, 110588
            );
            var88 = TimeFormatterExtKt.formattedDateFromLong(var7.lastPart().getCreatedAt(), var100);
            var85.startReplaceGroup(433245310);
            ComposerKt.sourceInformation(var85, "108@4909L10,109@4982L6,106@4799L306");
            if (var88.length() > 0) {
               TextStyle var121 = IntercomTheme.INSTANCE.getTypography(var85, IntercomTheme.$stable).getType04();
               long var92 = IntercomTheme.INSTANCE.getColors(var85, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
               Modifier var124 = Modifier.Companion;
               int var94 = 8;
               int var95 = 0;
               Modifier var96 = PaddingKt.recovered$03b481d31d62885a(var124, Dp.recovered$95b3d103663bf060(var94), 0.0F, 0.0F, 0.0F, 14, null);
               TextKt.recovered$5b114ab32a4f4190(
                  var88, var96, var92, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, var121, var85, 48, 0, 131064
               );
            }

            var85.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var85);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
            var76.endNode();
            ComposerKt.sourceInformationMarkerEnd(var76);
            ComposerKt.sourceInformationMarkerEnd(var69);
            ComposerKt.sourceInformationMarkerEnd(var58);
            String var112 = var7.lastPart().getSummary();
            var58.startReplaceGroup(-1283405160);
            ComposerKt.sourceInformation(var58, "133@6147L10,136@6366L6,129@5939L471");
            Intrinsics.checkNotNull(var112);
            if (var112.length() > 0) {
               var58.startReplaceGroup(-1283403261);
               ComposerKt.sourceInformation(var58, "119@5472L37");
               String var125;
               if (var7.lastPart().getParticipant().isUserWithId(ConversationItemKt.access$getUserIntercomId())) {
                  var125 = StringResources_androidKt.stringResource(string.intercom_you, var58, 0) + ": " + var112;
               } else {
                  CharSequence var117 = var7.lastParticipatingAdmin().getFirstName();
                  CharSequence recoveredName;
                  if (var117.length() == 0) {
                     int var119 = 0;
                     recoveredName = ConversationItemKt.access$getWorkspaceName();
                  } else {
                     recoveredName = var117;
                  }

                  String var115 = (String)recoveredName;
                  var125 = var115 + ": " + var112;
               }

               String var113 = var125;
               var58.endReplaceGroup();
               String var118 = var113;
               int var114 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
               TextStyle var116 = TextStyle.recovered$cf01d8b71690b5c5(
                  IntercomTheme.INSTANCE.getTypography(var58, IntercomTheme.$stable).getType04(),
                  0L,
                  0L,
                  var7.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold(),
                  null,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  null,
                  0L,
                  null,
                  null,
                  null,
                  0,
                  0,
                  0L,
                  null,
                  null,
                  null,
                  0,
                  0,
                  null,
                  16777211,
                  null
               );
               long var97 = IntercomTheme.INSTANCE.getColors(var58, IntercomTheme.$stable).getText().recovered$0aef7413383f6122();
               TextKt.recovered$5b114ab32a4f4190(
                  var118, null, var97, null, 0L, null, null, null, 0L, null, null, 0L, var114, false, 1, 0, null, var116, var58, 0, 24960, 110586
               );
            }

            var58.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var58);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var49.endNode();
            ComposerKt.sourceInformationMarkerEnd(var49);
            ComposerKt.sourceInformationMarkerEnd(var42);
            ComposerKt.sourceInformationMarkerEnd(var30);
            if (var8) {
               ConversationItemKt.UnreadIndicator(null, var30, 0, 1);
            }

            var30.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
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
