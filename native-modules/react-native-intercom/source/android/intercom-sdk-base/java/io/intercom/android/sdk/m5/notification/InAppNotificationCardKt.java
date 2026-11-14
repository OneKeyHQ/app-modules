package io.intercom.android.sdk.m5.notification;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.MaterialTheme;
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
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.ComposeView;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.text.AnnotatedString;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.m5.components.TextWithSeparatorKt;
import io.intercom.android.sdk.m5.components.TicketHeaderKt;
import io.intercom.android.sdk.m5.components.TicketStatusHeaderArgs;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.MessageStyle;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.overlay.ComposeCompatibilityUtilKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.Phrase;
import io.intercom.android.sdk.utilities.extensions.PartExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\n\u001a\u001a\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a\u001a\u0010\u0006\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a\u001f\u0010\n\u001a\u00020\u00012\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0001¢\u0006\u0002\u0010\r\u001a)\u0010\u000e\u001a\u00020\u00012\b\u0010\u000f\u001a\u0004\u0018\u00010\u00102\u0006\u0010\u0011\u001a\u00020\u00102\b\b\u0002\u0010\u0012\u001a\u00020\u0010H\u0001¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0018\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0015\"\u0010\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\t¨\u0006\u001a",
   d2 = {
         "addTicketHeaderToCompose",
         "",
         "composeView",
         "Landroidx/compose/ui/platform/ComposeView;",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "addInappNotificationCardToView",
         "IN_APP_NOTIFICATION_MAX_WIDTH",
         "Landroidx/compose/ui/unit/Dp;",
         "F",
         "InAppNotificationCard",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/models/Conversation;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "TicketInAppNotificationContent",
         "name",
         "",
         "ticketStatus",
         "timestamp",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "InAppNotificationCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "InAppNotificationCardLandscapePreview",
         "InAppNotificationCardTicketPreview",
         "InAppNotificationCardBotPreview",
         "InAppNotificationCardBotLandscapePreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nInAppNotificationCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InAppNotificationCard.kt\nio/intercom/android/sdk/m5/notification/InAppNotificationCardKt\n+ 2 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 8 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n*L\n1#1,327:1\n75#2:328\n75#2:474\n70#3:329\n68#3,8:330\n70#3:362\n67#3,9:363\n77#3:469\n77#3:473\n81#4,6:338\n88#4,6:353\n81#4,6:372\n88#4,6:387\n81#4,6:401\n88#4,6:416\n81#4,6:436\n88#4,6:451\n96#4:460\n96#4:464\n96#4:468\n96#4:472\n402#5,9:344\n411#5:359\n402#5,9:378\n411#5:393\n402#5,9:407\n411#5:422\n402#5,9:442\n411#5,3:457\n412#5,2:462\n412#5,2:466\n412#5,2:470\n118#6:360\n118#6:361\n118#6:394\n118#6:423\n118#6:424\n118#6:475\n99#7,6:395\n106#7:465\n87#8:425\n83#8,10:426\n94#8:461\n*S KotlinDebug\n*F\n+ 1 InAppNotificationCard.kt\nio/intercom/android/sdk/m5/notification/InAppNotificationCardKt\n*L\n101#1:328\n199#1:474\n104#1:329\n104#1:330,8\n108#1:362\n108#1:363,9\n108#1:469\n104#1:473\n104#1:338,6\n104#1:353,6\n108#1:372,6\n108#1:387,6\n116#1:401,6\n116#1:416,6\n126#1:436,6\n126#1:451,6\n126#1:460\n116#1:464\n108#1:468\n104#1:472\n104#1:344,9\n104#1:359\n108#1:378,9\n108#1:393\n116#1:407,9\n116#1:422\n126#1:442,9\n126#1:457,3\n116#1:462,2\n108#1:466,2\n104#1:470,2\n112#1:360\n114#1:361\n119#1:394\n124#1:423\n126#1:424\n94#1:475\n116#1:395,6\n116#1:465\n126#1:425\n126#1:426,10\n126#1:461\n*E\n"
)
public final class InAppNotificationCardKt {
   private static final float IN_APP_NOTIFICATION_MAX_WIDTH;

   public static final void addTicketHeaderToCompose(@Nullable ComposeView var0, @NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      if (var0 != null && !ComposeCompatibilityUtilKt.isLegacyActivity(var0)) {
         ComposeView var2 = var0;
         ComposeView var3 = var2;
         int var4 = 0;
         var3.setContent(ComposableLambdaKt.composableLambdaInstance(-744078063, true, new InAppNotificationCardKt$addTicketHeaderToCompose$1$1(var1)));
      }
   }

   public static final void addInappNotificationCardToView(@Nullable ComposeView var0, @NotNull Conversation var1) {
      Intrinsics.checkNotNullParameter(var1, "conversation");
      if (var0 != null && !ComposeCompatibilityUtilKt.isLegacyActivity(var0)) {
         ComposeView var2 = var0;
         ComposeView var3 = var2;
         int var4 = 0;
         var3.setContent(ComposableLambdaKt.composableLambdaInstance(-1448950486, true, new InAppNotificationCardKt$addInappNotificationCardToView$1$1(var1)));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCard(@NotNull Conversation var0, @Nullable Modifier var1, @Nullable Composer var2, int var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "conversation");
      var2 = var2.startRestartGroup(-320085669);
      ComposerKt.sourceInformation(var2, "C(InAppNotificationCard)100@3796L7,103@3852L4208:InAppNotificationCard.kt#1zjczn");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 2) != 0) {
            var1 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-320085669, var5, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCard (InAppNotificationCard.kt:99)");
         }

         CompositionLocal var7 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var8 = 0;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(var2, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var10 = var2.consume(var7);
         ComposerKt.sourceInformationMarkerEnd(var2);
         Context var6 = (Context)var10;
         Part var121 = var0.lastPart();
         Modifier var122 = SizeKt.fillMaxWidth$default(var1, 0.0F, 1, null);
         Alignment var123 = Alignment.Companion.getCenter();
         byte var11 = 48;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var124 = false;
         MeasurePolicy var13 = BoxKt.maybeCachedBoxMeasurePolicy(var123, var124);
         Modifier var14 = var122;
         MeasurePolicy var15 = var13;
         Composer var16 = var2;
         int var17 = 112 & var11 << 3;
         int var18 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var19 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var16, 0));
         CompositionLocalMap var20 = var16.getCurrentCompositionLocalMap();
         Modifier var21 = ComposedModifierKt.materializeModifier(var16, var14);
         Function0 var22 = ComposeUiNode.Companion.getConstructor();
         Composer var23 = var16;
         int var24 = 6 | 896 & var17 << 6;
         int var25 = 0;
         ComposerKt.sourceInformationMarkerStart(var23, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var23.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var23.startReusableNode();
         if (var23.getInserting()) {
            var23.createNode(var22);
         } else {
            var23.useNode();
         }

         Composer var26 = Updater.recovered$676aa17b44fd6280(var23);
         int var27 = 0;
         Updater.recovered$89682b199a142880(var26, var15, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var26, var20, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var26, var19, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var26, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var26, var21, ComposeUiNode.Companion.getSetModifier());
         int var28 = 14 & var24 >> 6;
         Composer var29 = var23;
         int var30 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         BoxScope var33 = BoxScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, -841725246, "C111@4137L6,112@4200L11,112@4238L6,107@3962L4092:InAppNotificationCard.kt#1zjczn");
         Modifier var10000 = SizeKt.fillMaxWidth$default(
            SizeKt.recovered$abafa0106f92be36(Modifier.Companion, 0.0F, IN_APP_NOTIFICATION_MAX_WIDTH, 1, null), 0.0F, 1, null
         );
         int var35 = 2;
         int var36 = 0;
         var10000 = BackgroundKt.recovered$60bc47fb440df483(
            ShadowKt.recovered$04904c26f7ded95f(
               var10000,
               Dp.recovered$95b3d103663bf060(var35),
               IntercomTheme.INSTANCE.getShapes(var32, IntercomTheme.$stable).getSmall(),
               false,
               0L,
               0L,
               24,
               null
            ),
            MaterialTheme.INSTANCE.getColorScheme(var32, MaterialTheme.$stable).recovered$a7e7bdd99c6c8d3d(),
            IntercomTheme.INSTANCE.getShapes(var32, IntercomTheme.$stable).getSmall()
         );
         var35 = 16;
         var36 = 0;
         float var10001 = Dp.recovered$95b3d103663bf060(var35);
         var35 = 16;
         var36 = 0;
         Modifier var127 = PaddingKt.recovered$904403cea4c49d95(var10000, var10001, Dp.recovered$95b3d103663bf060(var35));
         byte var37 = 0;
         int $i$f$Boxx = 0;
         ComposerKt.sourceInformationMarkerStart(
            var32, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Alignment var130 = Alignment.Companion.getTopStart();
         boolean propagateMinConstraints$ivx = false;
         MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(var130, propagateMinConstraints$ivx);
         Modifier var41 = var127;
         MeasurePolicy var42 = measurePolicy$ivx;
         Composer var43 = var32;
         int var44 = 112 & var37 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var43, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var43, 0));
         CompositionLocalMap localMap$iv$ivx = var43.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var43, var41);
         Function0 var49 = ComposeUiNode.Companion.getConstructor();
         Composer var50 = var43;
         int var51 = 6 | 896 & var44 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var50, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var50.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var50.startReusableNode();
         if (var50.getInserting()) {
            var50.createNode(var49);
         } else {
            var50.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var50);
         int var54/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var42, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var55 = 14 & var51 >> 6;
         Composer $composer$ivx = var50;
         int var57/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var58 = 6 | 112 & var37 >> 6;
         Composer var59 = $composer$ivx;
         BoxScope var60 = BoxScopeInstance.INSTANCE;
         int var61 = 0;
         ComposerKt.sourceInformationMarkerStart(var59, 898232807, "C115@4340L3704:InAppNotificationCard.kt#1zjczn");
         Modifier var62 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         Horizontal var131 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivx));
         Vertical var132 = Alignment.Companion.getTop();
         short var65 = 438;
         int var66 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var59, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy measurePolicy$ivxx = RowKt.rowMeasurePolicy(var131, var132, var59, 14 & var65 >> 3 | 112 & var65 >> 3);
         Modifier var68 = var62;
         MeasurePolicy var69 = measurePolicy$ivxx;
         Composer var70 = var59;
         int var71 = 112 & var65 << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var70, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var70, 0));
         CompositionLocalMap localMap$iv$ivxx = var70.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var70, var68);
         Function0 var76 = ComposeUiNode.Companion.getConstructor();
         Composer var77 = var70;
         int var78 = 6 | 896 & var71 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var77, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var77.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var77.startReusableNode();
         if (var77.getInserting()) {
            var77.createNode(var76);
         } else {
            var77.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var77);
         int var81/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var69, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var82 = 14 & var78 >> 6;
         Composer $composer$ivxx = var77;
         int var84 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var85 = 6 | 112 & var65 >> 6;
         Composer var86 = $composer$ivxx;
         RowScope var87 = RowScopeInstance.INSTANCE;
         int var88 = 0;
         ComposerKt.sourceInformationMarkerStart(var86, -861970501, "C121@4569L152,125@4738L3292:InAppNotificationCard.kt#1zjczn");
         AvatarWrapper var89 = PartExtensionsKt.resolvedAvatarWrapper(var121);
         var10000 = Modifier.Companion;
         int $this$dp$ivxx = 32;
         int $i$f$getDpxx = 0;
         AvatarIconKt.recovered$f5ec3c81e3146789(
            SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivxx)), var89, null, false, 0L, null, var86, 6, 60
         );
         int $this$dp$ivxxx = 4;
         $this$dp$ivxx = 0;
         androidx.compose.foundation.layout.Arrangement.Vertical var137 = Arrangement.INSTANCE
            .recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060($this$dp$ivxxx));
         $i$f$getDpxx = 48;
         int var93 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var86, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var133 = Modifier.Companion;
         androidx.compose.ui.Alignment.Horizontal var135 = Alignment.Companion.getStart();
         MeasurePolicy measurePolicy$ivxxx = ColumnKt.columnMeasurePolicy(var137, var135, var86, 14 & $i$f$getDpxx >> 3 | 112 & $i$f$getDpxx >> 3);
         Modifier var95 = var133;
         MeasurePolicy var96 = measurePolicy$ivxxx;
         Composer var97 = var86;
         int var98 = 112 & $i$f$getDpxx << 3;
         int $i$f$Layoutxxx = 0;
         ComposerKt.sourceInformationMarkerStart(var97, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var97, 0));
         CompositionLocalMap localMap$iv$ivxxx = var97.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var97, var95);
         Function0 var103 = ComposeUiNode.Companion.getConstructor();
         Composer var104 = var97;
         int var105 = 6 | 896 & var98 << 6;
         int $i$f$ReusableComposeNodexxx = 0;
         ComposerKt.sourceInformationMarkerStart(var104, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var104.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var104.startReusableNode();
         if (var104.getInserting()) {
            var104.createNode(var103);
         } else {
            var104.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var104);
         int var108/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var96, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
         int var109 = 14 & var105 >> 6;
         Composer $composer$ivxxx = var104;
         int var111 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var112 = 6 | 112 & $i$f$getDpxx >> 6;
         Composer var113 = $composer$ivxxx;
         ColumnScope var114 = ColumnScopeInstance.INSTANCE;
         int var115 = 0;
         ComposerKt.sourceInformationMarkerStart(var113, -1285993754, "C:InAppNotificationCard.kt#1zjczn");
         var113.startReplaceGroup(-1565507127);
         ComposerKt.sourceInformation(var113, "127@4876L310");
         if (var0.getTicket() != null) {
            TicketHeaderKt.TicketHeader(
               null,
               new TicketStatusHeaderArgs(var0.getTicket().getTitle(), var0.isRead() ? FontWeight.Companion.getNormal() : FontWeight.Companion.getSemiBold()),
               var113,
               0,
               1
            );
         }

         var113.endReplaceGroup();
         if (!var0.parts().isEmpty()) {
            var113.startReplaceGroup(-1285573581);
            ComposerKt.sourceInformation(var113, "");
            Part var116 = var0.lastPart();
            if (var116.getMessageStyle() == MessageStyle.TICKET_STATE_UPDATED) {
               var113.startReplaceGroup(-1285411172);
               ComposerKt.sourceInformation(var113, "144@5915L47,141@5690L311");
               String var117 = Intrinsics.areEqual(var116.getEventData().getStatus(), "submitted") ? null : var116.getParticipant().getForename();
               TicketInAppNotificationContent(
                  var117,
                  var116.getEventData().getEventAsPlainText(),
                  StringResources_androidKt.stringResource(string.intercom_time_just_now, var113, 0),
                  var113,
                  0,
                  0
               );
               var113.endReplaceGroup();
            } else {
               String var143 = var116.getSummary();
               Intrinsics.checkNotNullExpressionValue(var143, "getSummary(...)");
               if (!StringsKt.isBlank(var143)) {
                  var113.startReplaceGroup(-1284809927);
                  ComposerKt.sourceInformation(var113, "151@6265L10,149@6136L319");
                  String var144 = var116.getSummary();
                  Intrinsics.checkNotNullExpressionValue(var144, "getSummary(...)");
                  String var139 = var144;
                  TextStyle var118 = IntercomTheme.INSTANCE.getTypography(var113, IntercomTheme.$stable).getType04Point5();
                  int var119 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
                  TextKt.recovered$5b114ab32a4f4190(
                     var139, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var119, false, 2, 0, null, var118, var113, 0, 24960, 110590
                  );
                  var113.endReplaceGroup();
               } else {
                  var113.startReplaceGroup(-1284389443);
                  var113.endReplaceGroup();
               }
            }

            var113.endReplaceGroup();
         } else if (var0.getTicket() != null) {
            var113.startReplaceGroup(-1284165127);
            ComposerKt.sourceInformation(var113, "170@7190L47,167@6994L274");
            Ticket var138 = var0.getTicket();
            String var140 = Intrinsics.areEqual(var138.getCurrentStatus().getType(), "submitted") ? null : var138.getAssignee().build().getForename();
            TicketInAppNotificationContent(
               var140, var138.getCurrentStatus().getTitle(), StringResources_androidKt.stringResource(string.intercom_time_just_now, var113, 0), var113, 0, 0
            );
            var113.endReplaceGroup();
         } else {
            var113.startReplaceGroup(-1283613792);
            var113.endReplaceGroup();
         }

         var113.startReplaceGroup(-1565426172);
         ComposerKt.sourceInformation(var113, "182@7803L10,176@7397L593");
         if (var0.getTicket() == null) {
            TextWithSeparatorKt.recovered$741c28fc37ac0ca8(
               Phrase.from(var6, string.intercom_reply_from_admin).put("name", PartExtensionsKt.resolvedDisplayTitle(var121)).format().toString(),
               PartExtensionsKt.resolvedDisplaySuffix(var121),
               null,
               null,
               TextStyle.recovered$cf01d8b71690b5c5(
                  IntercomTheme.INSTANCE.getTypography(var113, IntercomTheme.$stable).getType04Point5(),
                  ColorKt.Color(4285887861L),
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
               ),
               0L,
               0,
               1,
               null,
               var113,
               12582912,
               364
            );
         }

         var113.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var113);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
         var104.endNode();
         ComposerKt.sourceInformationMarkerEnd(var104);
         ComposerKt.sourceInformationMarkerEnd(var97);
         ComposerKt.sourceInformationMarkerEnd(var86);
         ComposerKt.sourceInformationMarkerEnd(var86);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var77.endNode();
         ComposerKt.sourceInformationMarkerEnd(var77);
         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd(var59);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var50.endNode();
         ComposerKt.sourceInformationMarkerEnd(var50);
         ComposerKt.sourceInformationMarkerEnd(var43);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var145 = var2.endRestartGroup();
      if (var145 != null) {
         final io.intercom.android.sdk.models.Conversation recoveredCapture_1116_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_1116_1 = var1;
         final int recoveredCapture_1116_2 = var3;
         final int recoveredCapture_1116_3 = var4;
         var145.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1116_0, recoveredArgument_1116_1) -> InAppNotificationCardKt.InAppNotificationCard$lambda$6(recoveredCapture_1116_0, recoveredCapture_1116_1, recoveredCapture_1116_2, recoveredCapture_1116_3, (androidx.compose.runtime.Composer)recoveredArgument_1116_0, ((java.lang.Integer)recoveredArgument_1116_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TicketInAppNotificationContent(
      @Nullable String var0, @NotNull String var1, @Nullable String var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "ticketStatus");
      var3 = var3.startRestartGroup(818896879);
      ComposerKt.sourceInformation(var3, "C(TicketInAppNotificationContent)195@8177L47,212@8823L10,210@8766L145:InAppNotificationCard.kt#1zjczn");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var4 & 384) == 0) {
         var6 |= (var5 & 4) == 0 && var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         var3.startDefaults();
         if ((var4 & 1) != 0 && !var3.getDefaultsInvalid()) {
            var3.skipToGroupEnd();
            if ((var5 & 4) != 0) {
               var6 &= -897;
            }
         } else if ((var5 & 4) != 0) {
            var2 = StringResources_androidKt.stringResource(string.intercom_time_just_now, var3, 0);
            var6 &= -897;
         }

         var3.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               818896879, var6, -1, "io.intercom.android.sdk.m5.notification.TicketInAppNotificationContent (InAppNotificationCard.kt:195)"
            );
         }

         AnnotatedString var10000;
         if (var0 != null) {
            var3.startReplaceGroup(857685198);
            ComposerKt.sourceInformation(var3, "198@8332L7");
            CompositionLocal var9 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var10 = 0;
            int var11 = 0;
            ComposerKt.sourceInformationMarkerStart(var3, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var12 = var3.consume(var9);
            ComposerKt.sourceInformationMarkerEnd(var3);
            AnnotatedString var8 = new AnnotatedString(
               Phrase.from((Context)var12, string.intercom_tickets_status_event_moved)
                  .put("teammate", var0)
                  .put("status", var1)
                  .put("timestamp", var2)
                  .format()
                  .toString(),
               null,
               2,
               null
            );
            var3.endReplaceGroup();
            var10000 = var8;
         } else {
            var3.startReplaceGroup(858008714);
            ComposerKt.sourceInformation(var3, "207@8638L87");
            AnnotatedString var14 = new AnnotatedString(
               StringResources_androidKt.stringResource(string.intercom_tickets_status_description_prefix_when_submitted, var3, 0) + ' ' + var1, null, 2, null
            );
            var3.endReplaceGroup();
            var10000 = var14;
         }

         AnnotatedString var7 = var10000;
         TextStyle var15 = IntercomTheme.INSTANCE.getTypography(var3, IntercomTheme.$stable).getType05();
         int var16 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$489343e57da77a35(
            var7, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var16, false, 2, 0, null, null, var15, var3, 0, 24960, 241662
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var3.endRestartGroup();
      if (var17 != null) {
         final java.lang.String recoveredCapture_1122_0 = var0;
         final java.lang.String recoveredCapture_1122_1 = var1;
         final java.lang.String recoveredCapture_1122_2 = var2;
         final int recoveredCapture_1122_3 = var4;
         final int recoveredCapture_1122_4 = var5;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1122_0, recoveredArgument_1122_1) -> InAppNotificationCardKt.TicketInAppNotificationContent$lambda$7(recoveredCapture_1122_0, recoveredCapture_1122_1, recoveredCapture_1122_2, recoveredCapture_1122_3, recoveredCapture_1122_4, (androidx.compose.runtime.Composer)recoveredArgument_1122_0, ((java.lang.Integer)recoveredArgument_1122_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCardPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2144100909);
      ComposerKt.sourceInformation(var0, "C(InAppNotificationCardPreview)221@8995L409:InAppNotificationCard.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2144100909, var1, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCardPreview (InAppNotificationCard.kt:220)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InAppNotificationCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1120_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1120_0, recoveredArgument_1120_1) -> InAppNotificationCardKt.InAppNotificationCardPreview$lambda$8(recoveredCapture_1120_0, (androidx.compose.runtime.Composer)recoveredArgument_1120_0, ((java.lang.Integer)recoveredArgument_1120_1).intValue())));
      }
   }

   @Preview(name = "Landscape", widthDp = 720)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCardLandscapePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(567966218);
      ComposerKt.sourceInformation(var0, "C(InAppNotificationCardLandscapePreview)239@9616L409:InAppNotificationCard.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               567966218, var1, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCardLandscapePreview (InAppNotificationCard.kt:238)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InAppNotificationCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1119_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1119_0, recoveredArgument_1119_1) -> InAppNotificationCardKt.InAppNotificationCardLandscapePreview$lambda$9(recoveredCapture_1119_0, (androidx.compose.runtime.Composer)recoveredArgument_1119_0, ((java.lang.Integer)recoveredArgument_1119_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCardTicketPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-186124313);
      ComposerKt.sourceInformation(var0, "C(InAppNotificationCardTicketPreview)256@10115L600:InAppNotificationCard.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -186124313, var1, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCardTicketPreview (InAppNotificationCard.kt:255)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InAppNotificationCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1121_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1121_0, recoveredArgument_1121_1) -> InAppNotificationCardKt.InAppNotificationCardTicketPreview$lambda$10(recoveredCapture_1121_0, (androidx.compose.runtime.Composer)recoveredArgument_1121_0, ((java.lang.Integer)recoveredArgument_1121_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCardBotPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(61133886);
      ComposerKt.sourceInformation(var0, "C(InAppNotificationCardBotPreview)279@10991L739:InAppNotificationCard.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               61133886, var1, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCardBotPreview (InAppNotificationCard.kt:278)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InAppNotificationCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1118_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1118_0, recoveredArgument_1118_1) -> InAppNotificationCardKt.InAppNotificationCardBotPreview$lambda$11(recoveredCapture_1118_0, (androidx.compose.runtime.Composer)recoveredArgument_1118_0, ((java.lang.Integer)recoveredArgument_1118_1).intValue())));
      }
   }

   @Preview(name = "Landscape", widthDp = 720)
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InAppNotificationCardBotLandscapePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-2045228033);
      ComposerKt.sourceInformation(var0, "C(InAppNotificationCardBotLandscapePreview)305@11955L739:InAppNotificationCard.kt#1zjczn");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2045228033, var1, -1, "io.intercom.android.sdk.m5.notification.InAppNotificationCardBotLandscapePreview (InAppNotificationCard.kt:304)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InAppNotificationCardKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1117_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1117_0, recoveredArgument_1117_1) -> InAppNotificationCardKt.InAppNotificationCardBotLandscapePreview$lambda$12(recoveredCapture_1117_0, (androidx.compose.runtime.Composer)recoveredArgument_1117_0, ((java.lang.Integer)recoveredArgument_1117_1).intValue())));
      }
   }

   private static final Unit InAppNotificationCard$lambda$6(Conversation var0, Modifier var1, int var2, int var3, Composer var4, int var5) {
      InAppNotificationCard(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit TicketInAppNotificationContent$lambda$7(String var0, String var1, String var2, int var3, int var4, Composer var5, int var6) {
      TicketInAppNotificationContent(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit InAppNotificationCardPreview$lambda$8(int var0, Composer var1, int var2) {
      InAppNotificationCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InAppNotificationCardLandscapePreview$lambda$9(int var0, Composer var1, int var2) {
      InAppNotificationCardLandscapePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InAppNotificationCardTicketPreview$lambda$10(int var0, Composer var1, int var2) {
      InAppNotificationCardTicketPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InAppNotificationCardBotPreview$lambda$11(int var0, Composer var1, int var2) {
      InAppNotificationCardBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit InAppNotificationCardBotLandscapePreview$lambda$12(int var0, Composer var1, int var2) {
      InAppNotificationCardBotLandscapePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   static {
      int var0 = 480;
      int var1 = 0;
      IN_APP_NOTIFICATION_MAX_WIDTH = Dp.recovered$95b3d103663bf060(var0);
   }
}
