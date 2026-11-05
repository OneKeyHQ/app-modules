package io.intercom.android.sdk.m5.conversation.ui.components;

import android.net.Uri;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.Indication;
import androidx.compose.foundation.IndicationNodeFactory;
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
import androidx.compose.material3.IconKt;
import androidx.compose.material3.RippleKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.InputTypeState;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputButtonKt;
import io.intercom.android.sdk.m5.conversation.ui.components.composer.CameraInputType;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt;
import io.intercom.android.sdk.m5.preview.ui.MediaType;
import io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TopBarButton;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0004\u001a[\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0018\u0010\u0004\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u0006\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u00052\u0006\u0010\f\u001a\u00020\rH\u0001¢\u0006\u0002\u0010\u000e\u001a\u001f\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u000b2\b\b\u0001\u0010\u0011\u001a\u00020\u0012H\u0003¢\u0006\u0002\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0015¨\u0006\u0016",
   d2 = {
         "MediaInputSheetContent",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onMediaItemsSelected",
         "Lkotlin/Function1;",
         "",
         "Landroid/net/Uri;",
         "dismissSheet",
         "Lkotlin/Function0;",
         "trackClickedInput",
         "",
         "inputTypeState",
         "Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;",
         "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;Landroidx/compose/runtime/Composer;II)V",
         "MediaInputSheetContentItem",
         "text",
         "icon",
         "",
         "(Ljava/lang/String;ILandroidx/compose/runtime/Composer;I)V",
         "MediaInputSheetContentPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMediaInputSheetContent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaInputSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/MediaInputSheetContentKt\n+ 2 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 3 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 4 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 5 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 7 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,164:1\n87#2:165\n84#2,9:166\n94#2:260\n81#3,6:175\n88#3,6:190\n96#3:259\n81#3,6:273\n88#3,6:288\n96#3:299\n402#4,9:181\n411#4:196\n412#4,2:257\n402#4,9:279\n411#4:294\n412#4,2:297\n1047#5,6:197\n1047#5,6:203\n1047#5,6:209\n1047#5,6:215\n1047#5,6:221\n1047#5,6:227\n1047#5,6:233\n1047#5,6:239\n1047#5,6:245\n1047#5,6:251\n118#6:261\n118#6:262\n118#6:295\n118#6:296\n99#7:263\n96#7,9:264\n106#7:300\n*S KotlinDebug\n*F\n+ 1 MediaInputSheetContent.kt\nio/intercom/android/sdk/m5/conversation/ui/components/MediaInputSheetContentKt\n*L\n42#1:165\n42#1:166,9\n42#1:260\n42#1:175,6\n42#1:190,6\n42#1:259\n132#1:273,6\n132#1:288,6\n132#1:299\n42#1:181,9\n42#1:196\n42#1:257,2\n132#1:279,9\n132#1:294\n132#1:297,2\n49#1:197,6\n54#1:203,6\n68#1:209,6\n71#1:215,6\n74#1:221,6\n86#1:227,6\n89#1:233,6\n92#1:239,6\n107#1:245,6\n112#1:251,6\n134#1:261\n136#1:262\n141#1:295\n145#1:296\n132#1:263\n132#1:264,9\n132#1:300\n*E\n"
)
public final class MediaInputSheetContentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MediaInputSheetContent(
      @Nullable Modifier var0,
      @NotNull Function1 var1,
      @NotNull Function0 var2,
      @NotNull Function1 var3,
      @NotNull InputTypeState var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "onMediaItemsSelected");
      Intrinsics.checkNotNullParameter(var2, "dismissSheet");
      Intrinsics.checkNotNullParameter(var3, "trackClickedInput");
      Intrinsics.checkNotNullParameter(var4, "inputTypeState");
      Composer var58 = var5.startRestartGroup(-842940445);
      ComposerKt.sourceInformation(var58, "C(MediaInputSheetContent)P(2,3!1,4)41@1793L3453:MediaInputSheetContent.kt#o19g89");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var58.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var58.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var58.changedInstance(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var58.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var58.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var58.getSkipping()) {
         var58.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -842940445, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContent (MediaInputSheetContent.kt:40)"
            );
         }

         int var11 = 14 & var8;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var58, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical var9 = Arrangement.INSTANCE.getTop();
         Horizontal var10 = Alignment.Companion.getStart();
         MeasurePolicy var13 = ColumnKt.columnMeasurePolicy(var9, var10, var58, 14 & var11 >> 3 | 112 & var11 >> 3);
         Modifier var14 = var0;
         MeasurePolicy var15 = var13;
         Composer var16 = var58;
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
         ComposerKt.sourceInformationMarkerStart(var29, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var31 = 6 | 112 & var11 >> 6;
         Composer var32 = var29;
         ColumnScope var33 = ColumnScopeInstance.INSTANCE;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var32, 1890212380, "C:MediaInputSheetContent.kt#o19g89");
         var32.startReplaceGroup(-1878688057);
         ComposerKt.sourceInformation(var32, "");
         InputTypeState var35 = var4;
         int var36 = 0;
         var32.startReplaceGroup(-1878689416);
         ComposerKt.sourceInformation(var32, "48@2137L72,53@2386L99,44@1907L875");
         if (var35.getMediaInputEnabled()) {
            int var37 = var35.getAvailableMediaItems();
            MediaType var38 = MediaType.ImageAndVideo;
            Set var39 = var35.getTrustedFileExtensions();
            io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TopBarButton var40 = io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TopBarButton.INSTANCE;
            IndicationNodeFactory var41 = RippleKt.recovered$2ce5fb7060fb71ea(true, 0.0F, 0L, 6, null);
            int var10000 = var37;
            Indication var10001 = var41;
            MediaType var10002 = var38;
            Set var10003 = var39;
            var32.startReplaceGroup(2017634557);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var42 = var32;
            boolean var43 = (var8 & 112) == 32;
            int var44 = 0;
            Object var45 = var42.rememberedValue();
            int var46 = 0;
            Object var10004;
            if (!var43 && var45 != Composer.Companion.getEmpty()) {
               var10004 = var45;
            } else {
               Set var47 = var39;
               MediaType var48 = var38;
               Indication var49 = var10001;
               int var50 = var37;
               int var51 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_450_0 = var1;
               Function1 var52 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_450_0) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$1$lambda$0(recoveredExpressionCapture_450_0, (java.util.List) ((java.util.List)recoveredExpressionArgument_450_0)));
               var10000 = var50;
               var10001 = var49;
               var10002 = var48;
               var10003 = var47;
               Object var53 = var52;
               var42.updateRememberedValue(var53);
               var10004 = var53;
            }

            Function1 var54 = (Function1)var10004;
            var32.endReplaceGroup();
            Function1 var195 = var54;
            MediaPickerButtonCTAStyle var10005 = var40;
            boolean var10006 = false;
            var32.startReplaceGroup(2017642552);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var94 = var32;
            boolean var103 = (var8 & 7168) == 2048;
            int $i$f$cachex = 0;
            Object it$ivx = var94.rememberedValue();
            int var118/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var10007;
            if (!var103 && it$ivx != Composer.Companion.getEmpty()) {
               var10007 = it$ivx;
            } else {
               boolean var55 = false;
               MediaPickerButtonCTAStyle var56 = var10005;
               Function1 var158 = var54;
               Set var121 = var10003;
               MediaType var128 = var10002;
               Indication var137 = var10001;
               int var146 = var10000;
               int var155 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_451_0 = var3;
               Function0 var57 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$3$lambda$2(recoveredExpressionCapture_451_0));
               var10000 = var146;
               var10001 = var137;
               var10002 = var128;
               var10003 = var121;
               var195 = var158;
               var10005 = var56;
               var10006 = var55;
               Object var163 = var57;
               var94.updateRememberedValue(var163);
               var10007 = var163;
            }

            Function0 var166 = (Function0)var10007;
            var32.endReplaceGroup();
            MediaPickerButtonKt.MediaPickerButton(
               var10000,
               var10001,
               var10002,
               var10003,
               var195,
               var10005,
               var10006,
               var166,
               io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.INSTANCE.recovered$eac478aa65045e5d(),
               var32,
               102433200,
               0
            );
         }

         var32.endReplaceGroup();
         var32.startReplaceGroup(-1878658692);
         ComposerKt.sourceInformation(var32, "67@2962L87,70@3092L100,73@3235L62,65@2851L701,85@3681L87,88@3811L100,91@3954L62,83@3570L703");
         if (var35.getCameraInputEnabled()) {
            Object var178 = null;
            CameraInputType var182 = CameraInputType.PHOTO;
            var32.startReplaceGroup(2017660972);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var66 = var32;
            boolean var73 = (var8 & 112) == 32;
            int var80 = 0;
            Object var87 = var66.rememberedValue();
            int var167 = 0;
            Object var185;
            if (!var73 && var87 != Composer.Companion.getEmpty()) {
               var185 = var87;
            } else {
               CameraInputType var138 = var182;
               Object var147 = null;
               int var95 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_452_0 = var1;
               Function1 var129 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_452_0) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$5$lambda$4(recoveredExpressionCapture_452_0, (android.net.Uri) ((android.net.Uri)recoveredExpressionArgument_452_0)));
               var178 = var147;
               var182 = var138;
               Object var104 = var129;
               var66.updateRememberedValue(var104);
               var185 = var104;
            }

            Function1 var59 = (Function1)var185;
            var32.endReplaceGroup();
            Function1 var186 = var59;
            var32.startReplaceGroup(2017665145);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var67 = var32;
            boolean var74 = (var8 & 7168) == 2048;
            int $i$f$cachex = 0;
            Object it$ivx = var67.rememberedValue();
            int var168/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var190;
            if (!var74 && it$ivx != Composer.Companion.getEmpty()) {
               var190 = it$ivx;
            } else {
               Function1 var130 = var59;
               CameraInputType var139 = var182;
               Object var148 = var178;
               int var96 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_453_0 = var3;
               Function0 var122 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$7$lambda$6(recoveredExpressionCapture_453_0));
               var178 = var148;
               var182 = var139;
               var186 = var130;
               Object var105 = var122;
               var67.updateRememberedValue(var105);
               var190 = var105;
            }

            Function0 var60 = (Function0)var190;
            var32.endReplaceGroup();
            Function0 var191 = var60;
            var32.startReplaceGroup(2017669683);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var68 = var32;
            boolean var75 = (var8 & 896) == 256;
            int $i$f$cachexx = 0;
            Object it$ivxx = var68.rememberedValue();
            int var169/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
            Object var196;
            if (!var75 && it$ivxx != Composer.Companion.getEmpty()) {
               var196 = it$ivxx;
            } else {
               Function0 var123 = var60;
               Function1 var131 = var186;
               CameraInputType var140 = var182;
               Object var149 = var178;
               int var97 = 0;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_454_0 = var2;
               Function0 var159 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$9$lambda$8(recoveredExpressionCapture_454_0));
               var178 = var149;
               var182 = var140;
               var186 = var131;
               var191 = var123;
               Object var106 = var159;
               var68.updateRememberedValue(var106);
               var196 = var106;
            }

            Function0 var61 = (Function0)var196;
            var32.endReplaceGroup();
            CameraInputButtonKt.CameraInputButton(
               (Modifier)var178,
               var182,
               var186,
               var191,
               var61,
               io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.INSTANCE.recovered$1400af5e34386102(),
               var32,
               196656,
               1
            );
            Object var179 = null;
            CameraInputType var183 = CameraInputType.VIDEO;
            var32.startReplaceGroup(2017683980);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var69 = var32;
            boolean var76 = (var8 & 112) == 32;
            int $i$f$cachexxx = 0;
            Object it$ivxxx = var69.rememberedValue();
            int var170/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxx */ = 0;
            Object var187;
            if (!var76 && it$ivxxx != Composer.Companion.getEmpty()) {
               var187 = it$ivxxx;
            } else {
               CameraInputType var141 = var183;
               Object var150 = null;
               int var98 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_444_0 = var1;
               Function1 var132 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_444_0) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$11$lambda$10(recoveredExpressionCapture_444_0, (android.net.Uri) ((android.net.Uri)recoveredExpressionArgument_444_0)));
               var179 = var150;
               var183 = var141;
               Object var107 = var132;
               var69.updateRememberedValue(var107);
               var187 = var107;
            }

            Function1 var62 = (Function1)var187;
            var32.endReplaceGroup();
            Function1 var188 = var62;
            var32.startReplaceGroup(2017688153);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var70 = var32;
            boolean var77 = (var8 & 7168) == 2048;
            int $i$f$cachexxxx = 0;
            Object it$ivxxxx = var70.rememberedValue();
            int var171/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxx */ = 0;
            Object var192;
            if (!var77 && it$ivxxxx != Composer.Companion.getEmpty()) {
               var192 = it$ivxxxx;
            } else {
               Function1 var133 = var62;
               CameraInputType var142 = var183;
               Object var151 = var179;
               int var99 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_445_0 = var3;
               Function0 var124 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$13$lambda$12(recoveredExpressionCapture_445_0));
               var179 = var151;
               var183 = var142;
               var188 = var133;
               Object var108 = var124;
               var70.updateRememberedValue(var108);
               var192 = var108;
            }

            Function0 var63 = (Function0)var192;
            var32.endReplaceGroup();
            Function0 var193 = var63;
            var32.startReplaceGroup(2017692691);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var71 = var32;
            boolean var78 = (var8 & 896) == 256;
            int $i$f$cachexxxxx = 0;
            Object it$ivxxxxx = var71.rememberedValue();
            int var172/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxxxxx */ = 0;
            Object var197;
            if (!var78 && it$ivxxxxx != Composer.Companion.getEmpty()) {
               var197 = it$ivxxxxx;
            } else {
               Function0 var125 = var63;
               Function1 var134 = var188;
               CameraInputType var143 = var183;
               Object var152 = var179;
               int var100 = 0;
               final kotlin.jvm.functions.Function0 recoveredExpressionCapture_446_0 = var2;
               Function0 var160 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$15$lambda$14(recoveredExpressionCapture_446_0));
               var179 = var152;
               var183 = var143;
               var188 = var134;
               var193 = var125;
               Object var109 = var160;
               var71.updateRememberedValue(var109);
               var197 = var109;
            }

            Function0 var64 = (Function0)var197;
            var32.endReplaceGroup();
            CameraInputButtonKt.CameraInputButton(
               (Modifier)var179,
               var183,
               var188,
               var193,
               var64,
               io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.INSTANCE.recovered$fd838d44303763da(),
               var32,
               196656,
               1
            );
         }

         var32.endReplaceGroup();
         var32.startReplaceGroup(-1878611528);
         ComposerKt.sourceInformation(var32, "106@4569L72,111@4818L98,102@4340L876");
         if (var35.getFileInputEnabled()) {
            int var65 = var35.getAvailableMediaItems();
            MediaType var72 = MediaType.DocumentOnly;
            Set var79 = var35.getTrustedFileExtensions();
            io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TopBarButton var86 = io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonCTAStyle$TopBarButton.INSTANCE;
            IndicationNodeFactory var93 = RippleKt.recovered$2ce5fb7060fb71ea(true, 0.0F, 0L, 6, null);
            int var180 = var65;
            Indication var184 = var93;
            MediaType var189 = var72;
            Set var194 = var79;
            var32.startReplaceGroup(2017712381);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var101 = var32;
            boolean var110 = (var8 & 112) == 32;
            int var113 = 0;
            Object var116 = var101.rememberedValue();
            int var119 = 0;
            Object var198;
            if (!var110 && var116 != Composer.Companion.getEmpty()) {
               var198 = var116;
            } else {
               Set var126 = var79;
               MediaType var135 = var72;
               Indication var144 = var184;
               int var153 = var65;
               int var156 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_447_0 = var1;
               Function1 var161 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_447_0) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$17$lambda$16(recoveredExpressionCapture_447_0, (java.util.List) ((java.util.List)recoveredExpressionArgument_447_0)));
               var180 = var153;
               var184 = var144;
               var189 = var135;
               var194 = var126;
               Object var164 = var161;
               var101.updateRememberedValue(var164);
               var198 = var164;
            }

            Function1 var173 = (Function1)var198;
            var32.endReplaceGroup();
            Function1 var199 = var173;
            MediaPickerButtonCTAStyle var200 = var86;
            boolean var201 = false;
            var32.startReplaceGroup(2017720375);
            ComposerKt.sourceInformation(var32, "CC(remember):MediaInputSheetContent.kt#9igjgp");
            Composer var102 = var32;
            boolean var111 = (var8 & 7168) == 2048;
            int $i$f$cachex = 0;
            Object it$ivx = var102.rememberedValue();
            int var120/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
            Object var202;
            if (!var111 && it$ivx != Composer.Companion.getEmpty()) {
               var202 = it$ivx;
            } else {
               boolean var175 = false;
               MediaPickerButtonCTAStyle var176 = var200;
               Function1 var162 = var173;
               Set var127 = var194;
               MediaType var136 = var189;
               Indication var145 = var184;
               int var154 = var180;
               int var157 = 0;
               final kotlin.jvm.functions.Function1 recoveredExpressionCapture_448_0 = var3;
               Function0 var177 = ((kotlin.jvm.functions.Function0) () -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$21$lambda$20$lambda$19$lambda$18(recoveredExpressionCapture_448_0));
               var180 = var154;
               var184 = var145;
               var189 = var136;
               var194 = var127;
               var199 = var162;
               var200 = var176;
               var201 = var175;
               Object var165 = var177;
               var102.updateRememberedValue(var165);
               var202 = var165;
            }

            Function0 var174 = (Function0)var202;
            var32.endReplaceGroup();
            MediaPickerButtonKt.MediaPickerButton(
               var180,
               var184,
               var189,
               var194,
               var199,
               var200,
               var201,
               var174,
               io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.INSTANCE.recovered$54bdcf11cafa5012(),
               var32,
               102433200,
               0
            );
         }

         var32.endReplaceGroup();
         var32.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var58);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var181 = var58.endRestartGroup();
      if (var181 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_449_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_449_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredExpressionCapture_449_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_449_3 = var3;
         final io.intercom.android.sdk.m5.conversation.states.InputTypeState recoveredExpressionCapture_449_4 = var4;
         final int recoveredExpressionCapture_449_5 = var6;
         final int recoveredExpressionCapture_449_6 = var7;
         var181.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_449_0, recoveredExpressionArgument_449_1) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContent$lambda$22(recoveredExpressionCapture_449_0, recoveredExpressionCapture_449_1, recoveredExpressionCapture_449_2, recoveredExpressionCapture_449_3, recoveredExpressionCapture_449_4, recoveredExpressionCapture_449_5, recoveredExpressionCapture_449_6, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_449_0), ((java.lang.Integer)recoveredExpressionArgument_449_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MediaInputSheetContentItem(String var0, @DrawableRes int var1, Composer var2, int var3) {
      Composer var35 = var2.startRestartGroup(-181831684);
      ComposerKt.sourceInformation(var35, "C(MediaInputSheetContentItem)P(1)131@5356L457:MediaInputSheetContent.kt#o19g89");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var35.changed(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var35.changed(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var35.getSkipping()) {
         var35.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -181831684, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentItem (MediaInputSheetContent.kt:130)"
            );
         }

         Modifier var10000 = Modifier.Companion;
         int var5 = 56;
         int var6 = 0;
         Modifier var42 = SizeKt.fillMaxWidth$default(SizeKt.recovered$3f6c6a76aa292717(var10000, Dp.recovered$95b3d103663bf060(var5)), 0.0F, 1, null);
         int $this$dp$ivx = 16;
         int $i$f$getDpx = 0;
         Modifier var37 = PaddingKt.recovered$eb6a744cd98cdbca(var42, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         androidx.compose.ui.Alignment.Vertical var7 = Alignment.Companion.getCenterVertically();
         short var8 = 390;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var35, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var39 = Arrangement.INSTANCE.getStart();
         MeasurePolicy var10 = RowKt.rowMeasurePolicy(var39, var7, var35, 14 & var8 >> 3 | 112 & var8 >> 3);
         Modifier var11 = var37;
         MeasurePolicy var12 = var10;
         Composer var13 = var35;
         int var14 = 112 & var8 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         RowScope var30 = RowScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, 611462324, "C141@5635L26,138@5545L188,144@5742L39,145@5790L17:MediaInputSheetContent.kt#o19g89");
         Modifier var43 = Modifier.Companion;
         int $this$dp$ivxx = 24;
         int $i$f$getDpxx = 0;
         Modifier var34 = SizeKt.recovered$111ef94c93e78c04(var43, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
         IconKt.recovered$e00adffb7fe45b02(
            PainterResources_androidKt.painterResource(var1, var29, 14 & var4 >> 3), null, var34, 0L, var29, 432 | Painter.$stable, 8
         );
         Modifier var44 = Modifier.Companion;
         int $this$dp$ivxxx = 8;
         int $i$f$getDpxxx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var44, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var29, 6);
         TextKt.recovered$5b114ab32a4f4190(
            var0, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var29, 14 & var4, 0, 262142
         );
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var35);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var45 = var35.endRestartGroup();
      if (var45 != null) {
         final java.lang.String recoveredExpressionCapture_455_0 = var0;
         final int recoveredExpressionCapture_455_1 = var1;
         final int recoveredExpressionCapture_455_2 = var3;
         var45.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_455_0, recoveredExpressionArgument_455_1) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContentItem$lambda$24(recoveredExpressionCapture_455_0, recoveredExpressionCapture_455_1, recoveredExpressionCapture_455_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_455_0), ((java.lang.Integer)recoveredExpressionArgument_455_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MediaInputSheetContentPreview(Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(-375557009);
      ComposerKt.sourceInformation(var2, "C(MediaInputSheetContentPreview)152@5897L274:MediaInputSheetContent.kt#o19g89");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -375557009, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentPreview (MediaInputSheetContent.kt:151)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.ComposableSingletons$MediaInputSheetContentKt.INSTANCE.recovered$5a1850a8ff371bfb(),
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
         final int recoveredExpressionCapture_456_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_456_0, recoveredExpressionArgument_456_1) -> io.intercom.android.sdk.m5.conversation.ui.components.MediaInputSheetContentKt.MediaInputSheetContentPreview$lambda$25(recoveredExpressionCapture_456_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_456_0), ((java.lang.Integer)recoveredExpressionArgument_456_1).intValue())));
      }
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$1$lambda$0(Function1 var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$3$lambda$2(Function1 var0) {
      var0.invoke("image_input");
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$5$lambda$4(Function1 var0, Uri var1) {
      var0.invoke(CollectionsKt.listOfNotNull(var1));
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$7$lambda$6(Function1 var0) {
      var0.invoke("camera_input");
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$9$lambda$8(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$11$lambda$10(Function1 var0, Uri var1) {
      var0.invoke(CollectionsKt.listOfNotNull(var1));
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$13$lambda$12(Function1 var0) {
      var0.invoke("camera_input");
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$15$lambda$14(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$17$lambda$16(Function1 var0, List var1) {
      Intrinsics.checkNotNullParameter(var1, "it");
      var0.invoke(var1);
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$21$lambda$20$lambda$19$lambda$18(Function1 var0) {
      var0.invoke("file_input");
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContent$lambda$22(
      Modifier var0, Function1 var1, Function0 var2, Function1 var3, InputTypeState var4, int var5, int var6, Composer var7, int var8
   ) {
      MediaInputSheetContent(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContentItem$lambda$24(String var0, int var1, int var2, Composer var3, int var4) {
      MediaInputSheetContentItem(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MediaInputSheetContentPreview$lambda$25(int var0, Composer var1, int var2) {
      MediaInputSheetContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$MediaInputSheetContentItem(String var0, int var1, Composer var2, int var3) {
      MediaInputSheetContentItem(var0, var1, var2, var3);
   }
}
