package io.intercom.android.sdk.m5.home.ui;

import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.ConversationHistoryCardKt;
import io.intercom.android.sdk.m5.components.SearchBrowseCardKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.HomeCards;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeExternalLinkData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeHelpCenterData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeMessengerAppData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeRecentConversationData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeRecentTicketsData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeSpacesData;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeTicketLinksData;
import io.intercom.android.sdk.m5.home.data.SpaceItemType;
import io.intercom.android.sdk.m5.home.states.HomeUiState$Content;
import io.intercom.android.sdk.m5.home.ui.components.ExternalLinkCardKt;
import io.intercom.android.sdk.m5.home.ui.components.LegacyMessengerAppCardKt;
import io.intercom.android.sdk.m5.home.ui.components.NewConversationCardKt;
import io.intercom.android.sdk.m5.home.ui.components.SpacesCardKt;
import io.intercom.android.sdk.m5.home.ui.components.TicketLinksCardKt;
import io.intercom.android.sdk.metrics.MetricTracker;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.tickets.RecentTicketsCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000:\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\u001a¡\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\u000e\b\u0002\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000b2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u000bH\u0001¢\u0006\u0002\u0010\u0012¨\u0006\u0013²\u0006\n\u0010\u0014\u001a\u00020\u0015X\u008a\u0084\u0002",
   d2 = {
         "HomeContentScreen",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "content",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content;",
         "onMessagesClicked",
         "Lkotlin/Function0;",
         "onHelpClicked",
         "onTicketsClicked",
         "onTicketItemClicked",
         "Lkotlin/Function1;",
         "",
         "onNewConversationClicked",
         "onConversationClicked",
         "Lio/intercom/android/sdk/models/Conversation;",
         "onTicketLinkClicked",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-base_release",
         "teamPresence",
         "Lio/intercom/android/sdk/models/TeamPresence;"
   }
)
@SourceDebugExtension(
   "SMAP\nHomeContentScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HomeContentScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeContentScreenKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,138:1\n1047#2,6:139\n1047#2,6:145\n1047#2,6:151\n1047#2,6:157\n1047#2,6:163\n1047#2,6:169\n1047#2,6:175\n1047#2,6:216\n1047#2,6:222\n118#3:181\n118#3:182\n87#4:183\n85#4,8:184\n94#4:236\n81#5,6:192\n88#5,6:207\n96#5:235\n402#6,9:198\n411#6:213\n412#6,2:233\n1878#7,2:214\n1563#7:228\n1634#7,3:229\n1880#7:232\n85#8:237\n*S KotlinDebug\n*F\n+ 1 HomeContentScreen.kt\nio/intercom/android/sdk/m5/home/ui/HomeContentScreenKt\n*L\n40#1:139,6\n41#1:145,6\n42#1:151,6\n43#1:157,6\n44#1:163,6\n45#1:169,6\n46#1:175,6\n59#1:216,6\n101#1:222,6\n51#1:181\n52#1:182\n50#1:183\n50#1:184,8\n50#1:236\n50#1:192,6\n50#1:207,6\n50#1:235\n50#1:198,9\n50#1:213\n50#1:233,2\n54#1:214,2\n108#1:228\n108#1:229,3\n54#1:232\n48#1:237\n*E\n"
)
public final class HomeContentScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void HomeContentScreen(
      @Nullable Modifier var0,
      @NotNull HomeUiState$Content var1,
      @Nullable Function0 var2,
      @Nullable Function0 var3,
      @Nullable Function0 var4,
      @Nullable Function1 var5,
      @Nullable Function0 var6,
      @Nullable Function1 var7,
      @Nullable Function1 var8,
      @Nullable Composer var9,
      int var10,
      int var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "content");
      var9 = var9.startRestartGroup(-1476773966);
      ComposerKt.sourceInformation(
         var9,
         "C(HomeContentScreen)P(1!1,4,3,8,6,5)39@2132L2,40@2168L2,41@2207L2,42@2255L2,43@2302L2,44@2358L2,45@2410L2,47@2480L16,49@2502L3514:HomeContentScreen.kt#ac106p"
      );
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var9.changedInstance(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var9.changedInstance(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var9.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var9.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var9.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var9.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var9.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 256) != 0) {
         var12 |= 100663296;
      } else if ((var10 & 100663296) == 0) {
         var12 |= var9.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 38347923) == 38347922 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var11 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var11 & 4) != 0) {
            var9.startReplaceGroup(-182161845);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var14 = var9;
            boolean var15 = false;
            int var16 = 0;
            Object var17 = var14.rememberedValue();
            int var18 = 0;
            Object var10000;
            if (var17 == Composer.Companion.getEmpty()) {
               int var19 = 0;
               Object var110 = ((kotlin.jvm.functions.Function0) () -> HomeContentScreenKt.HomeContentScreen$lambda$1$lambda$0());
               var14.updateRememberedValue(var110);
               var10000 = var110;
            } else {
               var10000 = var17;
            }

            Function0 var13 = (Function0)var10000;
            var9.endReplaceGroup();
            var2 = var13;
         }

         if ((var11 & 8) != 0) {
            var9.startReplaceGroup(-182160693);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var71 = var9;
            boolean var79 = false;
            int var88 = 0;
            Object var96 = var71.rememberedValue();
            int var103 = 0;
            Object var145;
            if (var96 == Composer.Companion.getEmpty()) {
               int var111 = 0;
               Object var112 = ((kotlin.jvm.functions.Function0) () -> HomeContentScreenKt.HomeContentScreen$lambda$3$lambda$2());
               var71.updateRememberedValue(var112);
               var145 = var112;
            } else {
               var145 = var96;
            }

            Function0 var64 = (Function0)var145;
            var9.endReplaceGroup();
            var3 = var64;
         }

         if ((var11 & 16) != 0) {
            var9.startReplaceGroup(-182159445);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var72 = var9;
            boolean var80 = false;
            int var89 = 0;
            Object var97 = var72.rememberedValue();
            int var104 = 0;
            Object var146;
            if (var97 == Composer.Companion.getEmpty()) {
               int var113 = 0;
               Object var114 = ((kotlin.jvm.functions.Function0) () -> HomeContentScreenKt.HomeContentScreen$lambda$5$lambda$4());
               var72.updateRememberedValue(var114);
               var146 = var114;
            } else {
               var146 = var97;
            }

            Function0 var65 = (Function0)var146;
            var9.endReplaceGroup();
            var4 = var65;
         }

         if ((var11 & 32) != 0) {
            var9.startReplaceGroup(-182157909);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var73 = var9;
            boolean var81 = false;
            int var90 = 0;
            Object var98 = var73.rememberedValue();
            int var105 = 0;
            Object var147;
            if (var98 == Composer.Companion.getEmpty()) {
               int var115 = 0;
               Object var116 = ((kotlin.jvm.functions.Function1) (recoveredArgument_896_0) -> HomeContentScreenKt.HomeContentScreen$lambda$7$lambda$6((java.lang.String)recoveredArgument_896_0));
               var73.updateRememberedValue(var116);
               var147 = var116;
            } else {
               var147 = var98;
            }

            Function1 var66 = (Function1)var147;
            var9.endReplaceGroup();
            var5 = var66;
         }

         if ((var11 & 64) != 0) {
            var9.startReplaceGroup(-182156405);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var74 = var9;
            boolean var82 = false;
            int var91 = 0;
            Object var99 = var74.rememberedValue();
            int var106 = 0;
            Object var148;
            if (var99 == Composer.Companion.getEmpty()) {
               int var117 = 0;
               Object var118 = ((kotlin.jvm.functions.Function0) () -> HomeContentScreenKt.HomeContentScreen$lambda$9$lambda$8());
               var74.updateRememberedValue(var118);
               var148 = var118;
            } else {
               var148 = var99;
            }

            Function0 var67 = (Function0)var148;
            var9.endReplaceGroup();
            var6 = var67;
         }

         if ((var11 & 128) != 0) {
            var9.startReplaceGroup(-182154613);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var75 = var9;
            boolean var83 = false;
            int var92 = 0;
            Object var100 = var75.rememberedValue();
            int var107 = 0;
            Object var149;
            if (var100 == Composer.Companion.getEmpty()) {
               int var119 = 0;
               Object var120 = ((kotlin.jvm.functions.Function1) (recoveredArgument_898_0) -> HomeContentScreenKt.HomeContentScreen$lambda$11$lambda$10((io.intercom.android.sdk.models.Conversation)recoveredArgument_898_0));
               var75.updateRememberedValue(var120);
               var149 = var120;
            } else {
               var149 = var100;
            }

            Function1 var68 = (Function1)var149;
            var9.endReplaceGroup();
            var7 = var68;
         }

         if ((var11 & 256) != 0) {
            var9.startReplaceGroup(-182152949);
            ComposerKt.sourceInformation(var9, "CC(remember):HomeContentScreen.kt#9igjgp");
            Composer var76 = var9;
            boolean var84 = false;
            int var93 = 0;
            Object var101 = var76.rememberedValue();
            int var108 = 0;
            Object var150;
            if (var101 == Composer.Companion.getEmpty()) {
               int var121 = 0;
               Object var122 = ((kotlin.jvm.functions.Function1) (recoveredArgument_899_0) -> HomeContentScreenKt.HomeContentScreen$lambda$13$lambda$12((io.intercom.android.sdk.blocks.lib.models.TicketType)recoveredArgument_899_0));
               var76.updateRememberedValue(var122);
               var150 = var122;
            } else {
               var150 = var101;
            }

            Function1 var69 = (Function1)var150;
            var9.endReplaceGroup();
            var8 = var69;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1476773966, var12, -1, "io.intercom.android.sdk.m5.home.ui.HomeContentScreen (HomeContentScreen.kt:46)");
         }

         State var70 = SnapshotStateKt.collectAsState(Injector.get().getDataLayer().getTeamPresence(), null, var9, 0, 1);
         int var77 = 16;
         int var85 = 0;
         Modifier var78 = PaddingKt.recovered$f3b9f2e94c07c806(var0, Dp.recovered$95b3d103663bf060(var77), 0.0F, 2, null);
         var85 = 10;
         int $i$f$getDpx = 0;
         Vertical var87 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var85));
         byte var102 = 48;
         int var109 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var9, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Horizontal var95 = Alignment.Companion.getStart();
         MeasurePolicy var123 = ColumnKt.columnMeasurePolicy(var87, var95, var9, 14 & var102 >> 3 | 112 & var102 >> 3);
         Modifier var20 = var78;
         MeasurePolicy var21 = var123;
         Composer var22 = var9;
         int var23 = 112 & var102 << 3;
         int var24 = 0;
         ComposerKt.sourceInformationMarkerStart(var22, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var25 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var22, 0));
         CompositionLocalMap var26 = var22.getCurrentCompositionLocalMap();
         Modifier var27 = ComposedModifierKt.materializeModifier(var22, var20);
         Function0 var28 = ComposeUiNode.Companion.getConstructor();
         Composer var29 = var22;
         int var30 = 6 | 896 & var23 << 6;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var29.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var29.startReusableNode();
         if (var29.getInserting()) {
            var29.createNode(var28);
         } else {
            var29.useNode();
         }

         Composer var32 = Updater.recovered$676aa17b44fd6280(var29);
         int var33 = 0;
         Updater.recovered$89682b199a142880(var32, var21, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var32, var26, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var32, var25, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var32, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var32, var27, ComposeUiNode.Companion.getSetModifier());
         int var34 = 14 & var30 >> 6;
         Composer var35 = var29;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var35, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var37 = 6 | 112 & var102 >> 6;
         Composer var38 = var35;
         ColumnScope var39 = ColumnScopeInstance.INSTANCE;
         int var40 = 0;
         ComposerKt.sourceInformationMarkerStart(var38, 792813139, "C:HomeContentScreen.kt#ac106p");
         var38.startReplaceGroup(-1359898270);
         ComposerKt.sourceInformation(var38, "");
         Iterable var41 = var1.getCards();
         int var42 = 0;
         int var43 = 0;

         for (Object var45 : var41) {
            int var46 = var43++;
            if (var46 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            HomeCards var47 = (HomeCards)var45;
            int var48 = var46;
            int var49 = 0;
            HomeCards var50 = var47;
            if (var50 instanceof HomeCards$HomeSpacesData) {
               var38.startReplaceGroup(1732343893);
               ComposerKt.sourceInformation(var38, "58@2885L321,56@2783L446");
               HomeCards$HomeSpacesData var154 = (HomeCards$HomeSpacesData)var47;
               var38.startReplaceGroup(55885864);
               ComposerKt.sourceInformation(var38, "CC(remember):HomeContentScreen.kt#9igjgp");
               Composer var126 = var38;
               boolean var128 = (var12 & 896) == 256 | (var12 & 7168) == 2048 | (var12 & 57344) == 16384;
               int var130 = 0;
               Object var132 = var126.rememberedValue();
               int var134 = 0;
               kotlin.jvm.functions.Function1 var159;
               if (!var128 && var132 != Composer.Companion.getEmpty()) {
                  var159 = (Function1)var132;
               } else {
                  HomeCards$HomeSpacesData var136 = var154;
                  int var138 = 0;
                  final kotlin.jvm.functions.Function0 recoveredCapture_891_0 = var2;
                  final kotlin.jvm.functions.Function0 recoveredCapture_891_1 = var3;
                  final kotlin.jvm.functions.Function0 recoveredCapture_891_2 = var4;
                  var159 = ((kotlin.jvm.functions.Function1) (recoveredArgument_891_0) -> HomeContentScreenKt.HomeContentScreen$lambda$20$lambda$19$lambda$16$lambda$15(recoveredCapture_891_0, recoveredCapture_891_1, recoveredCapture_891_2, (io.intercom.android.sdk.m5.home.data.SpaceItemType)recoveredArgument_891_0));
                  var154 = var136;
                  Object var140 = var159;
                  var126.updateRememberedValue(var140);
                  var159 = (Function1)var140;
               }

               Function1 var143 = var159;
               var38.endReplaceGroup();
               SpacesCardKt.SpacesCard(var154, var143, var38, 0);
               var38.endReplaceGroup();
            } else if (var50 instanceof HomeCards$HomeRecentTicketsData) {
               var38.startReplaceGroup(1732867266);
               ComposerKt.sourceInformation(var38, "70@3377L221");
               if (!((HomeCards$HomeRecentTicketsData)var47).getTickets().isEmpty()) {
                  RecentTicketsCardKt.RecentTicketsCard(
                     null, var47.getCardTitle(), ((HomeCards$HomeRecentTicketsData)var47).getTickets(), var5, var38, 7168 & var12 >> 6, 1
                  );
               }

               var38.endReplaceGroup();
            } else if (var50 instanceof HomeCards$HomeRecentConversationData) {
               var38.startReplaceGroup(1733261276);
               ComposerKt.sourceInformation(var38, "80@3779L253");
               if (!((HomeCards$HomeRecentConversationData)var47).getConversations().isEmpty()) {
                  List var142 = ((HomeCards$HomeRecentConversationData)var47).getConversations();
                  String var125 = var47.getCardTitle();
                  ConversationHistoryCardKt.ConversationHistoryCard(null, var125, var142, var7, var38, 7168 & var12 >> 12, 1);
               }

               var38.endReplaceGroup();
            } else if (var50 instanceof HomeCards$HomeNewConversationData) {
               var38.startReplaceGroup(1733687154);
               ComposerKt.sourceInformation(var38, "89@4142L289");
               NewConversationCardKt.NewConversationCard(
                  (HomeCards$HomeNewConversationData)var47, var1.getAdminsAvatars(), var1.getBotAvatar(), var6, var38, 7168 & var12 >> 9, 0
               );
               var38.endReplaceGroup();
            } else if (!(var50 instanceof HomeCards$HomeHelpCenterData)) {
               if (var50 instanceof HomeCards$HomeExternalLinkData) {
                  var38.startReplaceGroup(1734909825);
                  ComposerKt.sourceInformation(var38, "116@5382L50");
                  ExternalLinkCardKt.ExternalLinkCard((HomeCards$HomeExternalLinkData)var47, var38, 0);
                  var38.endReplaceGroup();
               } else if (var50 instanceof HomeCards$HomeMessengerAppData) {
                  var38.startReplaceGroup(1735048674);
                  ComposerKt.sourceInformation(var38, "122@5644L14,120@5517L209");
                  LegacyMessengerAppCardKt.LegacyMessengerAppCard(
                     ((HomeCards$HomeMessengerAppData)var47).getFallbackUrl(),
                     IntercomCardStyle.INSTANCE.recovered$24b4efdd2276d096(null, 0L, 0L, 0.0F, null, 0L, var38, IntercomCardStyle.$stable << 18, 63),
                     true,
                     var38,
                     384 | Style.$stable << 3
                  );
                  var38.endReplaceGroup();
               } else if (var50 instanceof HomeCards$HomeTicketLinksData) {
                  var38.startReplaceGroup(1735337749);
                  ComposerKt.sourceInformation(var38, "128@5810L158");
                  TicketLinksCardKt.TicketLinksCard((HomeCards$HomeTicketLinksData)var47, var8, var38, 112 & var12 >> 21);
                  var38.endReplaceGroup();
               } else {
                  var38.startReplaceGroup(1735541915);
                  var38.endReplaceGroup();
               }
            } else {
               var38.startReplaceGroup(1734071492);
               ComposerKt.sourceInformation(var38, "100@4597L114,98@4514L220,104@4755L542");
               String var152 = "";
               var38.startReplaceGroup(55940441);
               ComposerKt.sourceInformation(var38, "CC(remember):HomeContentScreen.kt#9igjgp");
               Composer var51 = var38;
               boolean var52 = var38.changed(var48);
               int var53 = 0;
               Object var54 = var51.rememberedValue();
               int var55 = 0;
               Object var156;
               if (!var52 && var54 != Composer.Companion.getEmpty()) {
                  var156 = (Function2)var54;
               } else {
                  String var56 = "";
                  int var57 = 0;
                  var156 = new HomeContentScreenKt$HomeContentScreen$8$1$2$1(var48, null);
                  var152 = var56;
                  Object var58 = var156;
                  var51.updateRememberedValue(var58);
                  var156 = (Function2)var58;
               }

               Function2 var59 = (kotlin.jvm.functions.Function2) var156;
               var38.endReplaceGroup();
               EffectsKt.LaunchedEffect(var152, var59, var38, 6);
               HomeCards$HomeHelpCenterData var153 = (HomeCards$HomeHelpCenterData)var47;
               boolean var157 = ((AppConfig)Injector.get().getAppConfigProvider().get()).isHelpCenterRequireSearchEnabled();
               Iterable var141 = HomeContentScreen$lambda$14(var70).getBuiltActiveAdmins();
               boolean var60 = var157;
               HomeCards$HomeHelpCenterData var135 = var153;
               int var124 = 0;
               Iterable var127 = var141;
               ArrayList var129 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var141, 10));
               var54 = 0;

               for (Object var137 : var127) {
                  Participant var139 = (Participant)var137;
                  Collection var61 = var129;
                  int var62 = 0;
                  Avatar var10002 = var139.getAvatar();
                  Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
                  Boolean var10003 = var139.isBot();
                  Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
                  var61.add(new AvatarWrapper(var10002, var10003));
               }

               List var144 = (List)var129;
               boolean var160 = ((AppConfig)Injector.get().getAppConfigProvider().get()).isAccessToTeammateEnabled();
               MetricTracker var10004 = Injector.get().getMetricTracker();
               Intrinsics.checkNotNullExpressionValue(var10004, "getMetricTracker(...)");
               SearchBrowseCardKt.SearchBrowseCard(var135, var60, var144, var160, var10004, var38, 0);
               var38.endReplaceGroup();
            }
         }

         var38.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var38);
         ComposerKt.sourceInformationMarkerEnd(var35);
         var29.endNode();
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var22);
         ComposerKt.sourceInformationMarkerEnd(var9);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var155 = var9.endRestartGroup();
      if (var155 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_892_0 = var0;
         final io.intercom.android.sdk.m5.home.states.HomeUiState$Content recoveredCapture_892_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_892_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_892_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_892_4 = var4;
         final kotlin.jvm.functions.Function1 recoveredCapture_892_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_892_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredCapture_892_7 = var7;
         final kotlin.jvm.functions.Function1 recoveredCapture_892_8 = var8;
         final int recoveredCapture_892_9 = var10;
         final int recoveredCapture_892_10 = var11;
         var155.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_892_0, recoveredArgument_892_1) -> HomeContentScreenKt.HomeContentScreen$lambda$21(recoveredCapture_892_0, recoveredCapture_892_1, recoveredCapture_892_2, recoveredCapture_892_3, recoveredCapture_892_4, recoveredCapture_892_5, recoveredCapture_892_6, recoveredCapture_892_7, recoveredCapture_892_8, recoveredCapture_892_9, recoveredCapture_892_10, (androidx.compose.runtime.Composer)recoveredArgument_892_0, ((java.lang.Integer)recoveredArgument_892_1).intValue())));
      }
   }

   private static final Unit HomeContentScreen$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$7$lambda$6(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$9$lambda$8() {
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$11$lambda$10(Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$13$lambda$12(TicketType var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final TeamPresence HomeContentScreen$lambda$14(State var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (TeamPresence)var1.getValue();
   }

   private static final Unit HomeContentScreen$lambda$20$lambda$19$lambda$16$lambda$15(Function0 var0, Function0 var1, Function0 var2, SpaceItemType var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      switch (var3) {
         case MESSAGES:
            var0.invoke();
            break;
         case HELP:
            var1.invoke();
            break;
         case TICKETS:
            var2.invoke();
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return Unit.INSTANCE;
   }

   private static final Unit HomeContentScreen$lambda$21(
      Modifier var0,
      HomeUiState$Content var1,
      Function0 var2,
      Function0 var3,
      Function0 var4,
      Function1 var5,
      Function0 var6,
      Function1 var7,
      Function1 var8,
      int var9,
      int var10,
      Composer var11,
      int var12
   ) {
      HomeContentScreen(var0, var1, var2, var3, var4, var5, var6, var7, var8, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }
}
