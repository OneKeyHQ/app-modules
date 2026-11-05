package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.material3.AndroidMenu_androidKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$ConversationsInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$HelpInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$MoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$NewConversationInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData$TicketsInMoreMenuClicked;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Help;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$StartNewConversation;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadCountIndicator;
import io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadDotIndicator;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\u001aU\u0010\u0000\u001a\u00020\u00012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0004\b\u000b\u0010\f\u001a\f\u0010\r\u001a\u00020\u000e*\u00020\u0004H\u0002\u001a\u000e\u0010\u000f\u001a\u0004\u0018\u00010\u0010*\u00020\u0004H\u0002\u001a\f\u0010\u0011\u001a\u00020\u0012*\u00020\u0004H\u0002\u001a\u0014\u0010\u0013\u001a\u00020\n*\u00020\u00042\u0006\u0010\u0014\u001a\u00020\u000eH\u0002¨\u0006\u0015",
   d2 = {
         "ConversationKebab",
         "",
         "headerMenuItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/HeaderMenuItem;",
         "onMenuClicked",
         "Lkotlin/Function1;",
         "contentColor",
         "Landroidx/compose/ui/graphics/Color;",
         "trackMetric",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "ConversationKebab-cf5BqRc",
         "(Ljava/util/List;Lkotlin/jvm/functions/Function1;JLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "shouldShowUnreadDot",
         "",
         "badgeText",
         "",
         "icon",
         "",
         "metricData",
         "isMoreMenuContext",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationKebab.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationKebab.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationKebabKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 7 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 8 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,172:1\n1047#2,6:173\n1047#2,6:179\n1047#2,6:219\n1047#2,6:225\n1047#2,6:263\n1047#2,6:271\n99#3:185\n95#3,10:186\n106#3:284\n81#4,6:196\n88#4,6:211\n81#4,6:241\n88#4,6:256\n96#4:279\n96#4:283\n402#5,9:202\n411#5:217\n402#5,9:247\n411#5:262\n412#5,2:277\n412#5,2:281\n1#6:218\n87#7:231\n84#7,9:232\n94#7:280\n118#8:269\n118#8:270\n*S KotlinDebug\n*F\n+ 1 ConversationKebab.kt\nio/intercom/android/sdk/m5/conversation/ui/components/ConversationKebabKt\n*L\n25#1:173,6\n27#1:179,6\n55#1:219,6\n67#1:225,6\n78#1:263,6\n88#1:271,6\n29#1:185\n29#1:186,10\n29#1:284\n29#1:196,6\n29#1:211,6\n68#1:241,6\n68#1:256,6\n68#1:279\n29#1:283\n29#1:202,9\n29#1:217\n68#1:247,9\n68#1:262\n68#1:277,2\n29#1:281,2\n68#1:231\n68#1:232,9\n68#1:280\n86#1:269\n93#1:270\n*E\n"
)
public final class ConversationKebabKt {
   @Composable
   @Preview
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$25e666bc9a1af21e(
      @Nullable List var0, @Nullable Function1 var1, long var2, @Nullable Function1 var4, @Nullable Composer var5, int var6, int var7
   ) {
      Composer var102 = var5.startRestartGroup(1084184456);
      ComposerKt.sourceInformation(var102, "C(ConversationKebab)P(1,2,0:c#ui.graphics.Color)24@1023L2,26@1106L2,28@1118L3709:ConversationKebab.kt#o19g89");
      int var8 = var102.getCurrentMarker();
      int var9 = var6;
      if ((var7 & 1) != 0) {
         var9 |= 6;
      } else if ((var6 & 6) == 0) {
         var9 |= var102.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var9 |= 48;
      } else if ((var6 & 48) == 0) {
         var9 |= var102.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var9 |= 384;
      } else if ((var6 & 384) == 0) {
         var9 |= var102.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var9 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var9 |= var102.changedInstance(var4) ? 2048 : 1024;
      }

      if ((var9 & 1171) == 1170 && var102.getSkipping()) {
         var102.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = CollectionsKt.emptyList();
         }

         if ((var7 & 2) != 0) {
            var102.startReplaceGroup(2013516817);
            ComposerKt.sourceInformation(var102, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var11 = var102;
            boolean var12 = false;
            int var13 = 0;
            Object var14 = var11.rememberedValue();
            int var15 = 0;
            Object var10000;
            if (var14 == Composer.Companion.getEmpty()) {
               int var16 = 0;
               Object var115 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_414_0) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem) ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem)recoveredExpressionArgument_414_0)));
               var11.updateRememberedValue(var115);
               var10000 = var115;
            } else {
               var10000 = var14;
            }

            Function1 var10 = (Function1)var10000;
            var102.endReplaceGroup();
            var1 = var10;
         }

         if ((var7 & 4) != 0) {
            var2 = Color.Companion.recovered$235b30c090a399c5();
         }

         if ((var7 & 8) != 0) {
            var102.startReplaceGroup(2013519473);
            ComposerKt.sourceInformation(var102, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var105 = var102;
            boolean var107 = false;
            int var109 = 0;
            Object var111 = var105.rememberedValue();
            int var113 = 0;
            Object var160;
            if (var111 == Composer.Companion.getEmpty()) {
               int var116 = 0;
               Object var117 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_415_0) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.metrics.MetricData) ((io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredExpressionArgument_415_0)));
               var105.updateRememberedValue(var117);
               var160 = var117;
            } else {
               var160 = var111;
            }

            Function1 var103 = (Function1)var160;
            var102.endReplaceGroup();
            var4 = var103;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1084184456, var9, -1, "io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebab (ConversationKebab.kt:27)"
            );
         }

         Vertical var108 = Alignment.Companion.getCenterVertically();
         short var110 = 384;
         int var112 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var102, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         Modifier var104 = Modifier.Companion;
         Horizontal var106 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var114 = RowKt.rowMeasurePolicy(var106, var108, var102, 14 & var110 >> 3 | 112 & var110 >> 3);
         Modifier var118 = var104;
         MeasurePolicy var17 = var114;
         Composer var18 = var102;
         int var19 = 112 & var110 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var118);
         Function0 var24 = ComposeUiNode.Companion.getConstructor();
         Composer var25 = var18;
         int var26 = 6 | 896 & var19 << 6;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var25, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var25.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var25.startReusableNode();
         if (var25.getInserting()) {
            var25.createNode(var24);
         } else {
            var25.useNode();
         }

         Composer var28 = Updater.recovered$676aa17b44fd6280(var25);
         int var29 = 0;
         Updater.recovered$89682b199a142880(var28, var17, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var28, var22, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var28, var21, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var28, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var28, var23, ComposeUiNode.Companion.getSetModifier());
         int var30 = 14 & var26 >> 6;
         Composer var31 = var25;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var31, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var33 = 6 | 112 & var110 >> 6;
         Composer var34 = var31;
         RowScope var35 = RowScopeInstance.INSTANCE;
         int var36 = 0;
         var34.startReplaceGroup(2047867851);
         ComposerKt.sourceInformation(var34, "C:ConversationKebab.kt#o19g89");
         Iterable var37 = var0;
         Iterator var38 = var37.iterator();

         Object var161;
         while (true) {
            if (!var38.hasNext()) {
               var161 = null;
               break;
            }

            Object var39 = var38.next();
            HeaderMenuItem var40 = (HeaderMenuItem)var39;
            int var41 = 0;
            if (var40 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages) {
               var161 = var39;
               break;
            }
         }

         HeaderMenuItem var162 = (HeaderMenuItem)var161;
         io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator var163;
         if ((HeaderMenuItem)var161 != null) {
            HeaderMenuItem var119 = var162;
            int var122 = 0;
            Intrinsics.checkNotNull(var119, "null cannot be cast to non-null type io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem.Messages");
            var163 = ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages)var119).getTotalCountIndicator();
         } else {
            var163 = null;
         }

         io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator var42 = var163;
         String var43 = var42 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadCountIndicator ? ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadCountIndicator)var42).getUnreadMessagesCount() : null;
         boolean var44 = var42 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$TotalCountIndicator$UnreadDotIndicator;
         if (var0.isEmpty()) {
            var34.startReplaceGroup(2048373646);
            var34.endReplaceGroup();
            var34.endToMarker(var8);
            if (ComposerKt.isTraceInProgress()) {
               ComposerKt.traceEventEnd();
            }

            ScopeUpdateScope var169 = var102.endRestartGroup();
            if (var169 != null) {
               final java.util.List recoveredExpressionCapture_416_0 = var0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_416_1 = var1;
               final long recoveredExpressionCapture_416_2 = var2;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_416_3 = var4;
               final int recoveredExpressionCapture_416_4 = var6;
               final int recoveredExpressionCapture_416_5 = var7;
               var169.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_416_0, recoveredExpressionArgument_416_1) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$15$lambda$6(recoveredExpressionCapture_416_0, recoveredExpressionCapture_416_1, recoveredExpressionCapture_416_2, recoveredExpressionCapture_416_3, recoveredExpressionCapture_416_4, recoveredExpressionCapture_416_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_416_0), ((java.lang.Integer)recoveredExpressionArgument_416_1).intValue())));
            }

            return;
         }

         if (var0.size() == 1) {
            var34.startReplaceGroup(2048472753);
            ComposerKt.sourceInformation(var34, "51@2033L9,54@2159L155,50@1971L556");
            HeaderMenuItem var121 = (HeaderMenuItem)CollectionsKt.first(var0);
            String var125 = var121.getLabel().getText(var34, StringProvider.$stable);
            int var128 = icon(var121);
            boolean var131 = var121.getEnabled();
            String var168 = var125;
            boolean var172 = var131;
            int var173 = var128;
            Object var174 = null;
            String var175 = var43;
            boolean var176 = false;
            boolean var177 = var44;
            boolean var178 = false;
            var34.startReplaceGroup(-1180838359);
            ComposerKt.sourceInformation(var34, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var136 = var34;
            boolean var138 = (var9 & 112) == 32 | var102.changed(var121) | (var9 & 7168) == 2048;
            int var141 = 0;
            Object var142 = var136.rememberedValue();
            int var143 = 0;
            Object var179;
            if (!var138 && var142 != Composer.Companion.getEmpty()) {
               var179 = var142;
            } else {
               boolean var50 = false;
               boolean var51 = var44;
               boolean var52 = false;
               String var53 = var43;
               Object var54 = null;
               int var55 = var128;
               boolean var56 = var131;
               String var57 = var125;
               int var144 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_417_0 = var1;
               final io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem recoveredExpressionCapture_417_1 = var121;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_417_2 = var4;
               Function0 var59 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$15$lambda$8$lambda$7(recoveredExpressionCapture_417_0, recoveredExpressionCapture_417_1, recoveredExpressionCapture_417_2));
               var168 = var57;
               var172 = var56;
               var173 = var55;
               var174 = var54;
               var175 = var53;
               var176 = var52;
               var177 = var51;
               var178 = var50;
               Object var145 = var59;
               var136.updateRememberedValue(var145);
               var179 = var145;
            }

            Function0 var134 = (Function0)var179;
            var34.endReplaceGroup();
            HeaderMenuItemRowKt.recovered$04990d299bee3c60(
               var168, var172, var173, (Modifier)var174, var175, var176, var177, var178, var134, var2, var34, 12582912 | 1879048192 & var9 << 21, 40
            );
            var34.endReplaceGroup();
         } else {
            var34.startReplaceGroup(2049182188);
            ComposerKt.sourceInformation(var34, "66@2598L34,67@2649L2148");
            var34.startReplaceGroup(-1180824432);
            ComposerKt.sourceInformation(var34, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var126 = var34;
            boolean var129 = false;
            int var132 = 0;
            Object var45 = var126.rememberedValue();
            int var46 = 0;
            Object var164;
            if (var45 == Composer.Companion.getEmpty()) {
               int var47 = 0;
               Object var139 = SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
               var126.updateRememberedValue(var139);
               var164 = var139;
            } else {
               var164 = var45;
            }

            MutableState var123 = (MutableState)var164;
            var34.endReplaceGroup();
            MutableState var120 = var123;
            Modifier var124 = SizeKt.wrapContentWidth$default(Modifier.Companion, null, false, 3, null);
            byte var133 = 6;
            int var135 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var34, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
            );
            androidx.compose.foundation.layout.Arrangement.Vertical var127 = Arrangement.INSTANCE.getTop();
            androidx.compose.ui.Alignment.Horizontal var130 = Alignment.Companion.getStart();
            MeasurePolicy measurePolicy$ivx = ColumnKt.columnMeasurePolicy(var127, var130, var34, 14 & var133 >> 3 | 112 & var133 >> 3);
            Modifier var140 = var124;
            MeasurePolicy var48 = measurePolicy$ivx;
            Composer var49 = var34;
            int var58 = 112 & var133 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var49, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var49, 0));
            CompositionLocalMap localMap$iv$ivx = var49.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var49, var140);
            Function0 var64 = ComposeUiNode.Companion.getConstructor();
            Composer var65 = var49;
            int var66 = 6 | 896 & var58 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var65, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var65.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var65.startReusableNode();
            if (var65.getInserting()) {
               var65.createNode(var64);
            } else {
               var65.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var65);
            int var69/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var48, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var70 = 14 & var66 >> 6;
            Composer $composer$ivx = var65;
            int var72 = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
            int var73 = 6 | 112 & var133 >> 6;
            Composer var74 = $composer$ivx;
            ColumnScope var75 = ColumnScopeInstance.INSTANCE;
            int var76 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var74, 130492389, "C77@3120L165,70@2746L647,90@3720L6,91@3784L6,87@3583L80,93@3868L911,84@3414L1365:ConversationKebab.kt#o19g89"
            );
            int var77 = drawable.intercom_kebab;
            boolean var78 = !(Boolean)var120.getValue();
            String var165 = "More Menu";
            boolean var10001 = true;
            int var10002 = var77;
            Object var10003 = null;
            String var10004 = var43;
            boolean var10005 = var78;
            boolean var10006 = var44;
            boolean var10007 = false;
            var74.startReplaceGroup(-1935443118);
            ComposerKt.sourceInformation(var74, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var79 = var74;
            boolean var80 = (var9 & 7168) == 2048;
            int $i$f$cachex = 0;
            Object it$ivx = var79.rememberedValue();
            int var83/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10008;
            if (!var80 && it$ivx != Composer.Companion.getEmpty()) {
               var10008 = it$ivx;
            } else {
               boolean var84 = false;
               boolean var85 = var44;
               boolean var86 = var78;
               String var87 = var43;
               Object var88 = null;
               int var89 = var77;
               boolean var90 = true;
               String var91 = "More Menu";
               int var92 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_411_0 = var120;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_411_1 = var4;
               Function0 var93 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$15$lambda$14$lambda$11$lambda$10(recoveredExpressionCapture_411_0, recoveredExpressionCapture_411_1));
               var165 = var91;
               var10001 = var90;
               var10002 = var89;
               var10003 = var88;
               var10004 = var87;
               var10005 = var86;
               var10006 = var85;
               var10007 = var84;
               Object var94 = var93;
               var79.updateRememberedValue(var94);
               var10008 = var94;
            }

            Function0 var95 = (Function0)var10008;
            var74.endReplaceGroup();
            HeaderMenuItemRowKt.recovered$04990d299bee3c60(
               var165,
               var10001,
               var10002,
               (Modifier)var10003,
               var10004,
               var10005,
               var10006,
               var10007,
               var95,
               var2,
               var74,
               12582966 | 1879048192 & var9 << 21,
               8
            );
            Modifier var166 = Modifier.Companion;
            int var147 = 220;
            int var159 = 0;
            Modifier var146 = SizeKt.recovered$4f2dac42b6e775a9(var166, Dp.recovered$95b3d103663bf060(var147));
            boolean var148 = (Boolean)var120.getValue();
            long var96 = IntercomTheme.INSTANCE.getColors(var74, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05();
            CornerBasedShape var149 = IntercomTheme.INSTANCE.getShapes(var74, IntercomTheme.$stable).getSmall();
            int $this$dp$ivx = 8;
            int $i$f$getDpx = 0;
            float var150 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
            boolean var167 = var148;
            var74.startReplaceGroup(-1935428387);
            ComposerKt.sourceInformation(var74, "CC(remember):ConversationKebab.kt#9igjgp");
            Composer var154 = var74;
            boolean var157 = false;
            int $i$f$cachexx = 0;
            Object it$ivxx = var154.rememberedValue();
            int var99/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var171;
            if (it$ivxx == Composer.Companion.getEmpty()) {
               boolean var156 = var148;
               int var100 = 0;
               final androidx.compose.runtime.MutableState recoveredExpressionCapture_412_0 = var120;
               Function0 var155 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$15$lambda$14$lambda$13$lambda$12(recoveredExpressionCapture_412_0));
               var167 = var156;
               Object var101 = var155;
               var154.updateRememberedValue(var101);
               var171 = var101;
            } else {
               var171 = it$ivxx;
            }

            Function0 var152 = (Function0)var171;
            var74.endReplaceGroup();
            AndroidMenu_androidKt.recovered$7e5e85d5a2943f5c(
               var167,
               var152,
               var146,
               0L,
               null,
               null,
               var149,
               var96,
               0.0F,
               var150,
               null,
               ComposableLambdaKt.rememberComposableLambda(
                  1309645384, true, new ConversationKebabKt$ConversationKebab$3$3$3(var0, var1, var4, var2, var120), var74, 54
               ),
               var74,
               805306800,
               48,
               1336
            );
            ComposerKt.sourceInformationMarkerEnd(var74);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var65.endNode();
            ComposerKt.sourceInformationMarkerEnd(var65);
            ComposerKt.sourceInformationMarkerEnd(var49);
            ComposerKt.sourceInformationMarkerEnd(var34);
            var34.endReplaceGroup();
         }

         var34.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var102);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var170 = var102.endRestartGroup();
      if (var170 != null) {
         final java.util.List recoveredExpressionCapture_413_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_413_1 = var1;
         final long recoveredExpressionCapture_413_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_413_3 = var4;
         final int recoveredExpressionCapture_413_4 = var6;
         final int recoveredExpressionCapture_413_5 = var7;
         var170.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_413_0, recoveredExpressionArgument_413_1) -> io.intercom.android.sdk.m5.conversation.ui.components.ConversationKebabKt.ConversationKebab_cf5BqRc$lambda$16(recoveredExpressionCapture_413_0, recoveredExpressionCapture_413_1, recoveredExpressionCapture_413_2, recoveredExpressionCapture_413_3, recoveredExpressionCapture_413_4, recoveredExpressionCapture_413_5, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_413_0), ((java.lang.Integer)recoveredExpressionArgument_413_1).intValue())));
      }
   }

   private static final boolean shouldShowUnreadDot(HeaderMenuItem var0) {
      return var0 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets ? ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets)var0).getHasUnreadTickets() : false;
   }

   private static final String badgeText(HeaderMenuItem var0) {
      String var3;
      if (var0 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages) {
         CharSequence var1 = ((io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages)var0).getUnreadMessagesCount();
         CharSequence var10000;
         if (var1.length() == 0) {
            int var2 = 0;
            var10000 = null;
         } else {
            var10000 = var1;
         }

         var3 = (String)var10000;
      } else {
         var3 = null;
      }

      return var3;
   }

   private static final int icon(HeaderMenuItem var0) {
      HeaderMenuItem var1 = var0;
      int var10000;
      if (var1 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages) {
         var10000 = drawable.intercom_inbox;
      } else if (var1 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$StartNewConversation) {
         var10000 = drawable.intercom_send_message_icon;
      } else if (var1 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets) {
         var10000 = drawable.intercom_ticket_detail_icon;
      } else {
         if (!(var1 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Help)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = drawable.intercom_help_centre_icon;
      }

      return var10000;
   }

   private static final MetricData metricData(HeaderMenuItem var0, boolean var1) {
      HeaderMenuItem var2 = var0;
      MetricData var10000;
      if (var2 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Messages) {
         var10000 = new io.intercom.android.sdk.m5.conversation.metrics.MetricData$ConversationsInMoreMenuClicked(var1);
      } else if (var2 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Tickets) {
         var10000 = new io.intercom.android.sdk.m5.conversation.metrics.MetricData$TicketsInMoreMenuClicked(var1);
      } else if (var2 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$StartNewConversation) {
         var10000 = new io.intercom.android.sdk.m5.conversation.metrics.MetricData$NewConversationInMoreMenuClicked(var1);
      } else {
         if (!(var2 instanceof io.intercom.android.sdk.m5.conversation.states.HeaderMenuItem$Help)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = io.intercom.android.sdk.m5.conversation.metrics.MetricData$HelpInMoreMenuClicked.INSTANCE;
      }

      return var10000;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$1$lambda$0(HeaderMenuItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$3$lambda$2(MetricData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$15$lambda$6(
      List var0, Function1 var1, long var2, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      recovered$25e666bc9a1af21e(var0, var1, var2, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$15$lambda$8$lambda$7(Function1 var0, HeaderMenuItem var1, Function1 var2) {
      var0.invoke(var1);
      var2.invoke(metricData(var1, false));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$15$lambda$14$lambda$11$lambda$10(MutableState var0, Function1 var1) {
      var0.setValue(!(Boolean)var0.getValue());
      var1.invoke(io.intercom.android.sdk.m5.conversation.metrics.MetricData$MoreMenuClicked.INSTANCE);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$15$lambda$14$lambda$13$lambda$12(MutableState var0) {
      var0.setValue(false);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationKebab_cf5BqRc$lambda$16(
      List var0, Function1 var1, long var2, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      recovered$25e666bc9a1af21e(var0, var1, var2, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final MetricData access$metricData(HeaderMenuItem var0, boolean var1) {
      return metricData(var0, var1);
   }

   // $VF: synthetic method
   public static final int access$icon(HeaderMenuItem var0) {
      return icon(var0);
   }

   // $VF: synthetic method
   public static final String access$badgeText(HeaderMenuItem var0) {
      return badgeText(var0);
   }

   // $VF: synthetic method
   public static final boolean access$shouldShowUnreadDot(HeaderMenuItem var0) {
      return shouldShowUnreadDot(var0);
   }
}
