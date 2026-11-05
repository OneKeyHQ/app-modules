package io.intercom.android.sdk.m5.conversation.ui.components.row;

import android.content.Context;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.Indication;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
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
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.Modifier.Companion;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.AvatarGroupKt;
import io.intercom.android.sdk.m5.components.BotAndHumansFacePileKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Header$Expanded$Body;
import io.intercom.android.sdk.models.Header$Expanded$Footer;
import io.intercom.android.sdk.models.Header$Expanded$SocialAccount;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import io.intercom.android.sdk.utilities.LinkOpener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u001a\u001f\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a!\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0003¢\u0006\u0002\u0010\u0010\u001a\r\u0010\u0011\u001a\u00020\u0004H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0004H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0014\u001a\u00020\u0004H\u0003¢\u0006\u0002\u0010\u0012\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0002¨\u0006\u0015",
   d2 = {
         "AvatarSize",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "ExpandedTeamPresenceLayout",
         "",
         "teamPresenceUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/conversation/states/ExpandedTeamPresenceState;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "getTextStyleFor",
         "Landroidx/compose/ui/text/TextStyle;",
         "style",
         "Lio/intercom/android/sdk/models/Header$Expanded$Style;",
         "color",
         "",
         "(Lio/intercom/android/sdk/models/Header$Expanded$Style;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)Landroidx/compose/ui/text/TextStyle;",
         "ExpandedTeamPresenceLayoutPreviewWithFin",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ExpandedTeamPresenceLayoutPreviewWithFinAndHumans",
         "ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nExpandedTeamPresenceLayout.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExpandedTeamPresenceLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ExpandedTeamPresenceLayoutKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 9 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,366:1\n75#2:367\n87#3:368\n84#3,9:369\n94#3:507\n81#4,6:378\n88#4,6:393\n81#4,6:417\n88#4,6:432\n96#4:456\n81#4,6:472\n88#4,6:487\n96#4:501\n96#4:506\n402#5,9:384\n411#5:399\n402#5,9:423\n411#5:438\n412#5,2:454\n402#5,9:478\n411#5:493\n412#5,2:499\n412#5,2:504\n118#6:400\n118#6:402\n118#6:404\n118#6:405\n118#6:440\n118#6:459\n118#6:460\n118#6:498\n118#6:508\n1869#7:401\n1870#7:403\n1869#7:439\n1870#7:453\n1869#7:458\n1563#7:494\n1634#7,3:495\n1870#7:503\n99#8:406\n95#8,10:407\n106#8:457\n99#8:461\n95#8,10:462\n106#8:502\n1047#9,6:441\n1047#9,6:447\n*S KotlinDebug\n*F\n+ 1 ExpandedTeamPresenceLayout.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/ExpandedTeamPresenceLayoutKt\n*L\n49#1:367\n50#1:368\n50#1:369,9\n50#1:507\n50#1:378,6\n50#1:393,6\n122#1:417,6\n122#1:432,6\n122#1:456\n155#1:472,6\n155#1:487,6\n155#1:501\n50#1:506\n50#1:384,9\n50#1:399\n122#1:423,9\n122#1:438\n122#1:454,2\n155#1:478,9\n155#1:493\n155#1:499,2\n50#1:504,2\n107#1:400\n110#1:402\n121#1:404\n123#1:405\n137#1:440\n154#1:459\n156#1:460\n163#1:498\n42#1:508\n109#1:401\n109#1:403\n126#1:439\n126#1:453\n153#1:458\n162#1:494\n162#1:495,3\n153#1:503\n122#1:406\n122#1:407,10\n122#1:457\n155#1:461\n155#1:462,10\n155#1:502\n139#1:441,6\n141#1:447,6\n*E\n"
)
public final class ExpandedTeamPresenceLayoutKt {
   private static final float AvatarSize;

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ExpandedTeamPresenceLayout(
      @NotNull ExpandedTeamPresenceState var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "teamPresenceUiState");
      Composer var95 = var2.startRestartGroup(-1694898660);
      ComposerKt.sourceInformation(var95, "C(ExpandedTeamPresenceLayout)P(1)48@2130L7,49@2142L5108:ExpandedTeamPresenceLayout.kt#hr3bi5");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var95.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var95.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var95.getSkipping()) {
         var95.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1694898660, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayout (ExpandedTeamPresenceLayout.kt:47)"
            );
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var95, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var95.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var95);
         Context var6 = (Context)var10;
         Horizontal var97 = Alignment.Companion.getCenterHorizontally();
         int var98 = 384 | 14 & var5 >> 3;
         int var99 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var95, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var96 = Arrangement.INSTANCE.getTop();
         MeasurePolicy var11 = ColumnKt.columnMeasurePolicy(var96, var97, var95, 14 & var98 >> 3 | 112 & var98 >> 3);
         Modifier var12 = var1;
         MeasurePolicy var13 = var11;
         Composer var14 = var95;
         int var15 = 112 & var98 << 3;
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
         int var29 = 6 | 112 & var98 >> 6;
         Composer var30 = var27;
         ColumnScope var31 = ColumnScopeInstance.INSTANCE;
         int var32 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, -655412482, "C106@4393L41:ExpandedTeamPresenceLayout.kt#hr3bi5");
         switch (var0.getAvatarType()) {
            case FACEPILE:
               var30.startReplaceGroup(-655467756);
               ComposerKt.sourceInformation(var30, "56@2377L745");
               BotAndHumansFacePileKt.recovered$2d180ffbba3167ff(
                  Modifier.Companion,
                  ((AvatarWrapper)CollectionsKt.first(var0.getAvatars())).getAvatar(),
                  var0.getAvatars().size() >= 3
                     ? TuplesKt.to(((AvatarWrapper)var0.getAvatars().get(1)).getAvatar(), ((AvatarWrapper)var0.getAvatars().get(2)).getAvatar())
                     : (var0.getAvatars().size() == 2 ? TuplesKt.to(((AvatarWrapper)var0.getAvatars().get(1)).getAvatar(), null) : TuplesKt.to(null, null)),
                  AvatarSize,
                  null,
                  var30,
                  3078,
                  16
               );
               var30.endReplaceGroup();
               break;
            case LAYERED_BUBBLES:
               var30.startReplaceGroup(-654655587);
               ComposerKt.sourceInformation(var30, "");
               if (var0.getAvatars().size() == 1) {
                  var30.startReplaceGroup(-654606390);
                  ComposerKt.sourceInformation(var30, "78@3263L299");
                  AvatarWrapper var100 = (AvatarWrapper)var0.getAvatars().get(0);
                  Modifier var106 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, AvatarSize);
                  long var111 = TextUnitKt.getSp(24);
                  boolean var112 = var0.getDisplayActiveIndicator();
                  AvatarIconKt.recovered$f5ec3c81e3146789(var106, var100, null, var112, var111, null, var30, 24582, 36);
                  var30.endReplaceGroup();
               } else {
                  var30.startReplaceGroup(-654265855);
                  ComposerKt.sourceInformation(var30, "85@3608L244");
                  Companion var101 = Modifier.Companion;
                  List var107 = var0.getAvatars();
                  float var38 = AvatarSize;
                  long var39 = TextUnitKt.getSp(24);
                  AvatarGroupKt.recovered$813c81fac421be56(var107, var101, var38, var39, var30, 3504, 0);
                  var30.endReplaceGroup();
               }

               var30.endReplaceGroup();
               break;
            case LOGO:
               var30.startReplaceGroup(-653933318);
               ComposerKt.sourceInformation(var30, "95@3935L387");
               AvatarWrapper var33 = (AvatarWrapper)var0.getAvatars().get(0);
               Modifier var34 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, AvatarSize);
               long var35 = TextUnitKt.getSp(24);
               boolean var37 = var0.getDisplayActiveIndicator();
               long var41 = Color.Companion.recovered$562f21ab17d9b4c7();
               AvatarIconKt.recovered$f5ec3c81e3146789(var34, var33, null, var37, var35, Color.recovered$f46ef195ad4a2363(var41), var30, 221190, 4);
               var30.endReplaceGroup();
               break;
            case UNKNOWN:
               var30.startReplaceGroup(-653494885);
               var30.endReplaceGroup();
               break;
            default:
               var30.startReplaceGroup(-852429191);
               var30.endReplaceGroup();
               throw new NoWhenBranchMatchedException();
         }

         Modifier var10000 = Modifier.Companion;
         int var43 = 12;
         int var44 = 0;
         SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var43)), var30, 6);
         var30.startReplaceGroup(-852359896);
         ComposerKt.sourceInformation(var30, "*109@4527L40,112@4642L35,110@4580L238");
         Iterable var121 = var0.getBody();
         int var125 = 0;

         for (Object var108 : var121) {
            io.intercom.android.sdk.models.Header$Expanded$Body var118 = (io.intercom.android.sdk.models.Header$Expanded$Body)var108;
            int var45 = 0;
            Modifier var193 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var193, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var30, 6);
            String var114 = var118.getText();
            TextStyle var132 = getTextStyleFor(var118.getStyle(), var118.getColor(), var30, 0, 0);
            int var47 = TextAlign.Companion.recovered$d707b9dd64876179();
            int var48 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var114,
               null,
               0L,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var47),
               0L,
               var48,
               false,
               2,
               0,
               null,
               var132,
               var30,
               0,
               24960,
               109566
            );
         }

         var30.endReplaceGroup();
         var30.startReplaceGroup(-852346650);
         ComposerKt.sourceInformation(var30, "120@4951L41,121@5005L1310");
         if (!var0.getSocialAccounts().isEmpty()) {
            Modifier var194 = Modifier.Companion;
            int $this$dp$ivx = 12;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var194, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var30, 6);
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            androidx.compose.foundation.layout.Arrangement.Horizontal var128 = Arrangement.INSTANCE
               .recovered$49b0bf381d0b9a78(Dp.recovered$95b3d103663bf060($this$dp$ivxx), Alignment.Companion.getCenterHorizontally());
            androidx.compose.ui.Alignment.Vertical var104 = Alignment.Companion.getCenterVertically();
            short var109 = 432;
            int var119 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var123 = Modifier.Companion;
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var128, var104, var30, 14 & var109 >> 3 | 112 & var109 >> 3);
            Modifier var115 = var123;
            MeasurePolicy var133 = measurePolicy$ivx;
            Composer var137 = var30;
            int var140 = 112 & var109 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var137, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var137, 0));
            CompositionLocalMap localMap$iv$ivx = var137.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var137, var115);
            Function0 var53 = ComposeUiNode.Companion.getConstructor();
            Composer var54 = var137;
            int var55 = 6 | 896 & var140 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var54, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var54.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var54.startReusableNode();
            if (var54.getInserting()) {
               var54.createNode(var53);
            } else {
               var54.useNode();
            }

            Composer var57 = Updater.recovered$676aa17b44fd6280(var54);
            int var58 = 0;
            Updater.recovered$89682b199a142880(var57, var133, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var57, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var57, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var57, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var57, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var59 = 14 & var55 >> 6;
            Composer var60 = var54;
            int var61 = 0;
            ComposerKt.sourceInformationMarkerStart(var60, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var62 = 6 | 112 & var109 >> 6;
            Composer var63 = var60;
            RowScope var64 = RowScopeInstance.INSTANCE;
            int var65 = 0;
            ComposerKt.sourceInformationMarkerStart(var63, -1304649834, "C:ExpandedTeamPresenceLayout.kt#hr3bi5");
            var63.startReplaceGroup(-457726390);
            ComposerKt.sourceInformation(var63, "*132@5531L26,134@5662L6,138@5867L39,140@5989L271,131@5491L792");
            Iterable $this$forEach$ivx = var0.getSocialAccounts();
            int $i$f$forEachx = 0;

            for (Object var69 : $this$forEach$ivx) {
               io.intercom.android.sdk.models.Header$Expanded$SocialAccount var70 = (io.intercom.android.sdk.models.Header$Expanded$SocialAccount)var69;
               int var71 = 0;
               if (Intrinsics.areEqual(var70.getProvider(), "twitter")) {
                  int var72 = drawable.intercom_twitter;
                  Painter var73 = PainterResources_androidKt.painterResource(var72, var63, 0);
                  String var74 = var70.getProvider();
                  long var75 = IntercomTheme.INSTANCE.getColors(var63, IntercomTheme.$stable).recovered$d5d3a0ddb8f4248d();
                  Modifier var195 = Modifier.Companion;
                  byte var77 = 16;
                  boolean var78 = false;
                  Modifier var196 = SizeKt.recovered$111ef94c93e78c04(var195, Dp.recovered$95b3d103663bf060(var77));
                  var63.startReplaceGroup(-144020278);
                  ComposerKt.sourceInformation(var63, "CC(remember):ExpandedTeamPresenceLayout.kt#9igjgp");
                  Composer var176 = var63;
                  boolean var79 = false;
                  boolean var80 = false;
                  Object var81 = var176.rememberedValue();
                  boolean var82 = false;
                  Object var199;
                  if (var81 == Composer.Companion.getEmpty()) {
                     Modifier var83 = var196;
                     int var84 = 0;
                     MutableInteractionSource var10001 = InteractionSourceKt.MutableInteractionSource();
                     var196 = var83;
                     Object var190 = var10001;
                     var176.updateRememberedValue(var190);
                     var199 = var190;
                  } else {
                     var199 = var81;
                  }

                  MutableInteractionSource var173 = (MutableInteractionSource)var199;
                  var63.endReplaceGroup();
                  MutableInteractionSource var200 = var173;
                  Object var10002 = null;
                  boolean var10003 = false;
                  Object var10004 = null;
                  Object var10005 = null;
                  var63.startReplaceGroup(-144016142);
                  ComposerKt.sourceInformation(var63, "CC(remember):ExpandedTeamPresenceLayout.kt#9igjgp");
                  Composer var177 = var63;
                  boolean var179 = var63.changed(var70) | var63.changedInstance(var6);
                  int var181 = 0;
                  Object var183 = var177.rememberedValue();
                  int var185 = 0;
                  Object var10006;
                  if (!var179 && var183 != Composer.Companion.getEmpty()) {
                     var10006 = var183;
                  } else {
                     Object var85 = null;
                     Object var86 = null;
                     boolean var87 = false;
                     Object var88 = null;
                     MutableInteractionSource var89 = var173;
                     Modifier var187 = var196;
                     int var191 = 0;
                     final io.intercom.android.sdk.models.Header$Expanded$SocialAccount recoveredExpressionCapture_705_0 = var70;
                     final android.content.Context recoveredExpressionCapture_705_1 = var6;
                     Function0 var90 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayout$lambda$9$lambda$5$lambda$4$lambda$3$lambda$2(recoveredExpressionCapture_705_0, recoveredExpressionCapture_705_1));
                     var196 = var187;
                     var200 = var89;
                     var10002 = var88;
                     var10003 = var87;
                     var10004 = var86;
                     var10005 = var85;
                     Object var91 = var90;
                     var177.updateRememberedValue(var91);
                     var10006 = var91;
                  }

                  Function0 var174 = (Function0)var10006;
                  var63.endReplaceGroup();
                  Modifier var92 = ClickableKt.recovered$1f84abc9b53ba787(
                     var196, var200, (Indication)var10002, var10003, (String)var10004, (Role)var10005, var174, 28, null
                  );
                  IconKt.recovered$e00adffb7fe45b02(var73, var74, var92, var75, var63, Painter.$stable, 0);
               }
            }

            var63.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd(var63);
            ComposerKt.sourceInformationMarkerEnd(var60);
            var54.endNode();
            ComposerKt.sourceInformationMarkerEnd(var54);
            ComposerKt.sourceInformationMarkerEnd(var137);
            ComposerKt.sourceInformationMarkerEnd(var30);
         }

         var30.endReplaceGroup();
         var30.startReplaceGroup(-852298704);
         ComposerKt.sourceInformation(var30, "*153@6423L40,154@6476L758");
         Iterable $this$forEach$ivx = var0.getFooters();
         int $i$f$forEachx = 0;

         for (Object var110 : $this$forEach$ivx) {
            io.intercom.android.sdk.models.Header$Expanded$Footer var120 = (io.intercom.android.sdk.models.Header$Expanded$Footer)var110;
            int var131 = 0;
            Modifier var197 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var197, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var30, 6);
            int $this$dp$ivxx = 8;
            int $i$f$getDpxx = 0;
            androidx.compose.foundation.layout.Arrangement.Horizontal var136 = Arrangement.INSTANCE
               .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxx));
            androidx.compose.ui.Alignment.Vertical var139 = Alignment.Companion.getCenterVertically();
            short var141 = 432;
            int var142 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var30, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier var117 = Modifier.Companion;
            MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var136, var139, var30, 14 & var141 >> 3 | 112 & var141 >> 3);
            Modifier var144 = var117;
            MeasurePolicy var145 = measurePolicy$ivx;
            Composer var146 = var30;
            int var147 = 112 & var141 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var146, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var146, 0));
            CompositionLocalMap localMap$iv$ivx = var146.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var146, var144);
            Function0 var152 = ComposeUiNode.Companion.getConstructor();
            Composer var153 = var146;
            int var154 = 6 | 896 & var147 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var153, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var153.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var153.startReusableNode();
            if (var153.getInserting()) {
               var153.createNode(var152);
            } else {
               var153.useNode();
            }

            Composer var156 = Updater.recovered$676aa17b44fd6280(var153);
            int var157 = 0;
            Updater.recovered$89682b199a142880(var156, var145, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(var156, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880(var156, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9(var156, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880(var156, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var158 = 14 & var154 >> 6;
            Composer var159 = var153;
            int var160 = 0;
            ComposerKt.sourceInformationMarkerStart(var159, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var161 = 6 | 112 & var141 >> 6;
            Composer var162 = var159;
            RowScope var163 = RowScopeInstance.INSTANCE;
            int var164 = 0;
            ComposerKt.sourceInformationMarkerStart(var162, -168872927, "C167@7028L35,165@6958L262:ExpandedTeamPresenceLayout.kt#hr3bi5");
            var162.startReplaceGroup(-143995125);
            ComposerKt.sourceInformation(var162, "159@6697L226");
            if (var120.getAvatarDetails() != null) {
               Companion var165 = Modifier.Companion;
               Iterable var167 = var120.getAvatarDetails().getAvatars();
               int var170 = 0;
               Iterable var93 = var167;
               ArrayList var94 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var167, 10));
               int var175 = 0;

               for (Object var180 : var93) {
                  io.intercom.android.sdk.models.Avatar$Builder var182 = (io.intercom.android.sdk.models.Avatar$Builder)var180;
                  Collection var184 = var94;
                  int var186 = 0;
                  Avatar var201 = var182.build();
                  Intrinsics.checkNotNullExpressionValue(var201, "build(...)");
                  var184.add(new AvatarWrapper(var201, false, 2, null));
               }

               List var188 = (List)var94;
               int $this$dp$ivxxx = 20;
               int $i$f$getDpxxx = 0;
               float var168 = Dp.recovered$95b3d103663bf060($this$dp$ivxxx);
               AvatarGroupKt.recovered$813c81fac421be56(var188, var165, var168, 0L, var162, 432, 8);
            }

            var162.endReplaceGroup();
            String var166 = var120.getText();
            TextStyle var189 = getTextStyleFor(var120.getStyle(), var120.getColor(), var162, 0, 0);
            int var169 = TextAlign.Companion.recovered$d707b9dd64876179();
            int var172 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var166,
               null,
               0L,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var169),
               0L,
               var172,
               false,
               2,
               0,
               null,
               var189,
               var162,
               0,
               24960,
               109566
            );
            ComposerKt.sourceInformationMarkerEnd(var162);
            ComposerKt.sourceInformationMarkerEnd(var159);
            var153.endNode();
            ComposerKt.sourceInformationMarkerEnd(var153);
            ComposerKt.sourceInformationMarkerEnd(var146);
            ComposerKt.sourceInformationMarkerEnd(var30);
         }

         var30.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var30);
         ComposerKt.sourceInformationMarkerEnd(var27);
         var21.endNode();
         ComposerKt.sourceInformationMarkerEnd(var21);
         ComposerKt.sourceInformationMarkerEnd(var14);
         ComposerKt.sourceInformationMarkerEnd(var95);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var198 = var95.endRestartGroup();
      if (var198 != null) {
         final io.intercom.android.sdk.m5.conversation.states.ExpandedTeamPresenceState recoveredExpressionCapture_706_0 = var0;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_706_1 = var1;
         final int recoveredExpressionCapture_706_2 = var3;
         final int recoveredExpressionCapture_706_3 = var4;
         var198.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_706_0, recoveredExpressionArgument_706_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayout$lambda$10(recoveredExpressionCapture_706_0, recoveredExpressionCapture_706_1, recoveredExpressionCapture_706_2, recoveredExpressionCapture_706_3, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_706_0), ((java.lang.Integer)recoveredExpressionArgument_706_1).intValue())));
      }
   }

   @Composable
   private static final TextStyle getTextStyleFor(io.intercom.android.sdk.models.Header$Expanded$Style var0, String var1, Composer var2, int var3, int var4) {
      var2.startReplaceGroup(33871301);
      ComposerKt.sourceInformation(var2, "C(getTextStyleFor)P(1):ExpandedTeamPresenceLayout.kt#hr3bi5");
      if ((var4 & 2) != 0) {
         var1 = null;
      }

      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            33871301, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.getTextStyleFor (ExpandedTeamPresenceLayout.kt:182)"
         );
      }

      TextStyle var10000;
      switch (var0) {
         case H1:
            var2.startReplaceGroup(1720557014);
            ComposerKt.sourceInformation(var2, "183@7426L10");
            TextStyle var10 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType03();
            var2.endReplaceGroup();
            var10000 = var10;
            break;
         case PARAGRAPH:
            var2.startReplaceGroup(1720559895);
            ComposerKt.sourceInformation(var2, "185@7506L10,186@7595L6");
            TextStyle var13 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04();
            Color var15 = var1 != null ? Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.toComposeColor$default(var1, 0.0F, 1, null)) : null;
            TextStyle var9 = TextStyle.recovered$cf01d8b71690b5c5(
               var13,
               var15 == null
                  ? IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$0aef7413383f6122()
                  : var15.recovered$45f9de7b85e0e0b5(),
               0L,
               null,
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
               16777214,
               null
            );
            var2.endReplaceGroup();
            var10000 = var9;
            break;
         case INTRO:
            var2.startReplaceGroup(1720565689);
            ComposerKt.sourceInformation(var2, "190@7687L10,191@7776L6");
            TextStyle var12 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType01();
            Color var14 = var1 != null ? Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.toComposeColor$default(var1, 0.0F, 1, null)) : null;
            TextStyle var8 = TextStyle.recovered$cf01d8b71690b5c5(
               var12,
               var14 == null
                  ? IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a()
                  : var14.recovered$45f9de7b85e0e0b5(),
               0L,
               null,
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
               16777214,
               null
            );
            var2.endReplaceGroup();
            var10000 = var8;
            break;
         case GREETING:
            var2.startReplaceGroup(1720571639);
            ComposerKt.sourceInformation(var2, "195@7873L10,196@7962L6");
            TextStyle var11 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType01();
            Color var10001 = var1 != null ? Color.recovered$f46ef195ad4a2363(ColorExtensionsKt.toComposeColor$default(var1, 0.0F, 1, null)) : null;
            TextStyle var7 = TextStyle.recovered$cf01d8b71690b5c5(
               var11,
               var10001 == null
                  ? IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$0aef7413383f6122()
                  : var10001.recovered$45f9de7b85e0e0b5(),
               0L,
               null,
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
               16777214,
               null
            );
            var2.endReplaceGroup();
            var10000 = var7;
            break;
         default:
            var2.startReplaceGroup(1720576214);
            ComposerKt.sourceInformation(var2, "199@8026L10");
            TextStyle var6 = IntercomTheme.INSTANCE.getTypography(var2, IntercomTheme.$stable).getType04();
            var2.endReplaceGroup();
            var10000 = var6;
      }

      TextStyle var5 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var2.endReplaceGroup();
      return var5;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExpandedTeamPresenceLayoutPreviewWithFin(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(467453596);
      ComposerKt.sourceInformation(var2, "C(ExpandedTeamPresenceLayoutPreviewWithFin)205@8142L2453:ExpandedTeamPresenceLayout.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               467453596,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutPreviewWithFin (ExpandedTeamPresenceLayout.kt:204)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$ExpandedTeamPresenceLayoutKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_708_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_708_0, recoveredExpressionArgument_708_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayoutPreviewWithFin$lambda$11(recoveredExpressionCapture_708_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_708_0), ((java.lang.Integer)recoveredExpressionArgument_708_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExpandedTeamPresenceLayoutPreviewWithFinAndHumans(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(278476299);
      ComposerKt.sourceInformation(var2, "C(ExpandedTeamPresenceLayoutPreviewWithFinAndHumans)263@10699L3014:ExpandedTeamPresenceLayout.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               278476299,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutPreviewWithFinAndHumans (ExpandedTeamPresenceLayout.kt:262)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$ExpandedTeamPresenceLayoutKt.INSTANCE.recovered$54bdcf11cafa5012(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_709_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_709_0, recoveredExpressionArgument_709_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayoutPreviewWithFinAndHumans$lambda$12(recoveredExpressionCapture_709_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_709_0), ((java.lang.Integer)recoveredExpressionArgument_709_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-1042616954);
      ComposerKt.sourceInformation(var2, "C(ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin)330@13818L1437:ExpandedTeamPresenceLayout.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1042616954,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin (ExpandedTeamPresenceLayout.kt:329)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$ExpandedTeamPresenceLayoutKt.INSTANCE.recovered$5a1850a8ff371bfb(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_707_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_707_0, recoveredExpressionArgument_707_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.ExpandedTeamPresenceLayoutKt.ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin$lambda$13(recoveredExpressionCapture_707_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_707_0), ((java.lang.Integer)recoveredExpressionArgument_707_1).intValue())));
      }
   }

   private static final Unit ExpandedTeamPresenceLayout$lambda$9$lambda$5$lambda$4$lambda$3$lambda$2(io.intercom.android.sdk.models.Header$Expanded$SocialAccount var0, Context var1) {
      LinkOpener.handleUrl(var0.getProfileUrl(), var1, Injector.get().getApi());
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTeamPresenceLayout$lambda$10(ExpandedTeamPresenceState var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      ExpandedTeamPresenceLayout(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTeamPresenceLayoutPreviewWithFin$lambda$11(int var0, Composer var1, int var2) {
      ExpandedTeamPresenceLayoutPreviewWithFin(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTeamPresenceLayoutPreviewWithFinAndHumans$lambda$12(int var0, Composer var1, int var2) {
      ExpandedTeamPresenceLayoutPreviewWithFinAndHumans(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin$lambda$13(int var0, Composer var1, int var2) {
      ExpandedTeamPresenceLayoutPreviewWithAssignedAdmin(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   static {
      int var0 = 56;
      int var1 = 0;
      AvatarSize = Dp.recovered$95b3d103663bf060(var0);
   }
}
