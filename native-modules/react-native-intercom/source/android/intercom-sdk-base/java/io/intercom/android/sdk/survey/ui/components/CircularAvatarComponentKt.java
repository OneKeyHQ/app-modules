package io.intercom.android.sdk.survey.ui.components;

import android.content.Context;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
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
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.res.StringResources_androidKt;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.unit.Dp;
import coil.ImageLoader;
import coil.compose.AsyncImagePainter;
import coil.compose.AsyncImagePainterKt;
import coil.request.ImageRequest.Builder;
import coil.transform.CircleCropTransformation;
import coil.transform.Transformation;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.ui.coil.IntercomImageLoaderKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
import kotlin.Unit;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a)\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0004\b\b\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u000b¨\u0006\r",
   d2 = {
         "CircularAvatar",
         "",
         "avatar",
         "Lio/intercom/android/sdk/models/Avatar;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "size",
         "Landroidx/compose/ui/unit/Dp;",
         "CircularAvatar-aM-cp0Q",
         "(Lio/intercom/android/sdk/models/Avatar;JFLandroidx/compose/runtime/Composer;II)V",
         "PreviewDefaultAvatar",
         "(Landroidx/compose/runtime/Composer;I)V",
         "PreviewInitialAvatar",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nCircularAvatarComponent.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CircularAvatarComponent.kt\nio/intercom/android/sdk/survey/ui/components/CircularAvatarComponentKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 8 ImagePainter.kt\ncoil/compose/ImagePainterKt\n*L\n1#1,108:1\n118#2:109\n70#3:110\n66#3,10:111\n70#3:143\n67#3,9:144\n77#3:184\n70#3:185\n67#3,9:186\n77#3:220\n77#3:229\n81#4,6:121\n88#4,6:136\n81#4,6:153\n88#4,6:168\n96#4:183\n81#4,6:195\n88#4,6:210\n96#4:219\n96#4:228\n402#5,9:127\n411#5:142\n402#5,9:159\n411#5:174\n412#5,2:181\n402#5,9:201\n411#5,3:216\n412#5,2:226\n1047#6,6:175\n75#7:221\n74#7:223\n67#8:222\n68#8:224\n66#8:225\n*S KotlinDebug\n*F\n+ 1 CircularAvatarComponent.kt\nio/intercom/android/sdk/survey/ui/components/CircularAvatarComponentKt\n*L\n35#1:109\n37#1:110\n37#1:111,10\n44#1:143\n44#1:144,9\n44#1:184\n61#1:185\n61#1:186,9\n61#1:220\n37#1:229\n37#1:121,6\n37#1:136,6\n44#1:153,6\n44#1:168,6\n44#1:183\n61#1:195,6\n61#1:210,6\n61#1:219\n37#1:228\n37#1:127,9\n37#1:142\n44#1:159,9\n44#1:174\n44#1:181,2\n61#1:201,9\n61#1:216,3\n37#1:226,2\n54#1:175,6\n82#1:221\n80#1:223\n80#1:222\n80#1:224\n80#1:225\n*E\n"
)
public final class CircularAvatarComponentKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$7baf454f09f9b402(@NotNull Avatar var0, long var1, float var3, @Nullable Composer var4, int var5, int var6) {
      Intrinsics.checkNotNullParameter(var0, "avatar");
      var4 = var4.startRestartGroup(-276383091);
      ComposerKt.sourceInformation(var4, "C(CircularAvatar)P(!1,1:c#ui.graphics.Color,2:c#ui.unit.Dp)36@1381L2111:CircularAvatarComponent.kt#bmpoeo");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var3) ? 256 : 128;
      }

      if ((var7 & 147) == 146 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            int var8 = 40;
            int var9 = 0;
            var3 = Dp.recovered$95b3d103663bf060(var8);
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-276383091, var7, -1, "io.intercom.android.sdk.survey.ui.components.CircularAvatar (CircularAvatarComponent.kt:35)");
         }

         byte var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var4, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         Modifier var76 = Modifier.Companion;
         Alignment var77 = Alignment.Companion.getTopStart();
         boolean var10 = false;
         MeasurePolicy var13 = BoxKt.maybeCachedBoxMeasurePolicy(var77, var10);
         Modifier var14 = var76;
         MeasurePolicy var15 = var13;
         Composer var16 = var4;
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
         ComposerKt.sourceInformationMarkerStart(var32, -2071744221, "C37@1420L82:CircularAvatarComponent.kt#bmpoeo");
         String var35 = StringResources_androidKt.stringResource(string.intercom_surveys_sender_image, var32, 0);
         String var10000 = var0.getInitials();
         Intrinsics.checkNotNullExpressionValue(var10000, "getInitials(...)");
         if (var10000.length() <= 0) {
            var32.startReplaceGroup(-2071004283);
            ComposerKt.sourceInformation(var32, "60@2199L656");
            Modifier var78 = BackgroundKt.recovered$42a3174180b3fb7a(
               ClipKt.clip(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var3), RoundedCornerShapeKt.getCircleShape()), var1, null, 2, null
            );
            byte var80 = 0;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var32, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment contentAlignment$ivx = Alignment.Companion.getTopStart();
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(contentAlignment$ivx, propagateMinConstraints$ivx);
            Modifier var95 = var78;
            MeasurePolicy var97 = measurePolicy$ivx;
            Composer var99 = var32;
            int var101 = 112 & var80 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var99, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var99, 0));
            CompositionLocalMap localMap$iv$ivx = var99.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var99, var95);
            Function0 var110 = ComposeUiNode.Companion.getConstructor();
            Composer var111 = var99;
            int var112 = 6 | 896 & var101 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var111, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var111.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var111.startReusableNode();
            if (var111.getInserting()) {
               var111.createNode(var110);
            } else {
               var111.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var111);
            int var115/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var97, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var116 = 14 & var112 >> 6;
            Composer $composer$ivx = var111;
            int var118/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var119 = 6 | 112 & var80 >> 6;
            Composer var120 = $composer$ivx;
            BoxScope var121 = BoxScopeInstance.INSTANCE;
            int var122 = 0;
            ComposerKt.sourceInformationMarkerStart(var120, 1182984911, "C66@2415L61,67@2493L348:CircularAvatarComponent.kt#bmpoeo");
            Painter var123 = PainterResources_androidKt.painterResource(drawable.intercom_default_avatar_icon, var120, 0);
            ImageKt.Image(
               var123,
               var35,
               var121.align(Modifier.Companion, Alignment.Companion.getCenter()),
               null,
               ContentScale.Companion.getCrop(),
               0.0F,
               Companion.recovered$14c5668fff7aadde(ColorFilter.Companion, ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var1), 0, 2, null),
               var120,
               24576 | Painter.$stable,
               40
            );
            ComposerKt.sourceInformationMarkerEnd(var120);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var111.endNode();
            ComposerKt.sourceInformationMarkerEnd(var111);
            ComposerKt.sourceInformationMarkerEnd(var99);
            ComposerKt.sourceInformationMarkerEnd(var32);
            var32.endReplaceGroup();
         } else {
            var32.startReplaceGroup(-2071598305);
            ComposerKt.sourceInformation(var32, "43@1603L566");
            Modifier var36 = BackgroundKt.recovered$42a3174180b3fb7a(
               ClipKt.clip(SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var3), RoundedCornerShapeKt.getCircleShape()), var1, null, 2, null
            );
            byte var37 = 0;
            int $i$f$Boxx = 0;
            ComposerKt.sourceInformationMarkerStart(
               var32, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
            );
            Alignment contentAlignment$ivx = Alignment.Companion.getTopStart();
            boolean propagateMinConstraints$ivx = false;
            MeasurePolicy measurePolicy$ivx = BoxKt.maybeCachedBoxMeasurePolicy(contentAlignment$ivx, propagateMinConstraints$ivx);
            Modifier var42 = var36;
            MeasurePolicy var43 = measurePolicy$ivx;
            Composer var44 = var32;
            int var45 = 112 & var37 << 3;
            int $i$f$Layoutx = 0;
            ComposerKt.sourceInformationMarkerStart(var44, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var44, 0));
            CompositionLocalMap localMap$iv$ivx = var44.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var44, var42);
            Function0 var50 = ComposeUiNode.Companion.getConstructor();
            Composer var51 = var44;
            int var52 = 6 | 896 & var45 << 6;
            int $i$f$ReusableComposeNodex = 0;
            ComposerKt.sourceInformationMarkerStart(var51, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var51.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var51.startReusableNode();
            if (var51.getInserting()) {
               var51.createNode(var50);
            } else {
               var51.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var51);
            int var55/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var43, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
            int var56 = 14 & var52 >> 6;
            Composer $composer$ivx = var51;
            int var58/* $VF was: $i$a$-Layout-BoxKt$Box$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivx, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
            int var59 = 6 | 112 & var37 >> 6;
            Composer var60 = $composer$ivx;
            BoxScope var61 = BoxScopeInstance.INSTANCE;
            int var62 = 0;
            ComposerKt.sourceInformationMarkerStart(var60, 1182390889, "C53@1971L100,49@1797L358:CircularAvatarComponent.kt#bmpoeo");
            var10000 = var0.getInitials();
            Intrinsics.checkNotNullExpressionValue(var10000, "getInitials(...)");
            Modifier var10001 = var61.align(Modifier.Companion, Alignment.Companion.getCenter());
            boolean var10002 = false;
            var60.startReplaceGroup(592336280);
            ComposerKt.sourceInformation(var60, "CC(remember):CircularAvatarComponent.kt#9igjgp");
            Composer var63 = var60;
            boolean var64 = var60.changed(var35);
            int var65 = 0;
            Object var66 = var63.rememberedValue();
            int var67 = 0;
            Object var10003;
            if (!var64 && var66 != Composer.Companion.getEmpty()) {
               var10003 = var66;
            } else {
               boolean var68 = false;
               Modifier var69 = var10001;
               String var70 = var10000;
               int var71 = 0;
               final java.lang.String recoveredCapture_1325_0 = var35;
               Function1 var72 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1325_0) -> CircularAvatarComponentKt.CircularAvatar_aM_cp0Q$lambda$5$lambda$2$lambda$1$lambda$0(recoveredCapture_1325_0, (androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredArgument_1325_0));
               var10000 = var70;
               var10001 = var69;
               var10002 = var68;
               Object var73 = var72;
               var63.updateRememberedValue(var73);
               var10003 = var73;
            }

            Function1 var74 = (Function1)var10003;
            var60.endReplaceGroup();
            TextKt.recovered$5b114ab32a4f4190(
               var10000,
               SemanticsModifierKt.semantics$default(var10001, var10002, var74, 1, null),
               ColorExtensionsKt.recovered$f5f9d7dd14b2e4c1(var1),
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               null,
               0L,
               0,
               false,
               0,
               0,
               null,
               null,
               var60,
               0,
               0,
               262136
            );
            ComposerKt.sourceInformationMarkerEnd(var60);
            ComposerKt.sourceInformationMarkerEnd($composer$ivx);
            var51.endNode();
            ComposerKt.sourceInformationMarkerEnd(var51);
            ComposerKt.sourceInformationMarkerEnd(var44);
            ComposerKt.sourceInformationMarkerEnd(var32);
            var32.endReplaceGroup();
         }

         var32.startReplaceGroup(-1313708522);
         ComposerKt.sourceInformation(var32, "81@3128L7,79@3008L290,87@3311L165");
         var10000 = var0.getImageUrl();
         Intrinsics.checkNotNullExpressionValue(var10000, "getImageUrl(...)");
         if (var10000.length() > 0) {
            String var87 = var0.getImageUrl();
            CompositionLocal var89 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var81 = 0;
            int var84 = 0;
            ComposerKt.sourceInformationMarkerStart(var32, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var92 = var32.consume(var89);
            ComposerKt.sourceInformationMarkerEnd(var32);
            ImageLoader var90 = IntercomImageLoaderKt.getImageLoader((Context)var92);
            boolean var82 = false;
            var84 = 0;
            var32.startReplaceableGroup(1750824323);
            CompositionLocal var93 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var96 = 0;
            int $i$f$getCurrentx = 0;
            ComposerKt.sourceInformationMarkerStart(var32, 2023513938, "CC:CompositionLocal.kt#9igjgp");
            Object var100 = var32.consume(var93);
            ComposerKt.sourceInformationMarkerEnd(var32);
            Builder var94 = new Builder((Context)var100).data(var87);
            Builder var102 = var94;
            int var104 = 0;
            var102.crossfade(true);
            Transformation[] var106 = new Transformation[]{new CircleCropTransformation()};
            var102.transformations(var106);
            AsyncImagePainter var108 = AsyncImagePainterKt.recovered$8ecdef7ed4026761(var94.build(), var90, null, null, null, 0, null, var32, 72, 124);
            var32.endReplaceableGroup();
            AsyncImagePainter var79 = var108;
            ImageKt.Image(var79, var35, SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var3), null, null, 0.0F, null, var32, 0, 120);
         }

         var32.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var32);
         ComposerKt.sourceInformationMarkerEnd(var29);
         var23.endNode();
         ComposerKt.sourceInformationMarkerEnd(var23);
         ComposerKt.sourceInformationMarkerEnd(var16);
         ComposerKt.sourceInformationMarkerEnd(var4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var126 = var4.endRestartGroup();
      if (var126 != null) {
         final io.intercom.android.sdk.models.Avatar recoveredCapture_1324_0 = var0;
         final long recoveredCapture_1324_1 = var1;
         final float recoveredCapture_1324_2 = var3;
         final int recoveredCapture_1324_3 = var5;
         final int recoveredCapture_1324_4 = var6;
         var126.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1324_0, recoveredArgument_1324_1) -> CircularAvatarComponentKt.CircularAvatar_aM_cp0Q$lambda$6(recoveredCapture_1324_0, recoveredCapture_1324_1, recoveredCapture_1324_2, recoveredCapture_1324_3, recoveredCapture_1324_4, (androidx.compose.runtime.Composer)recoveredArgument_1324_0, ((java.lang.Integer)recoveredArgument_1324_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewDefaultAvatar(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1706634993);
      ComposerKt.sourceInformation(var0, "C(PreviewDefaultAvatar)99@3559L78:CircularAvatarComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1706634993, var1, -1, "io.intercom.android.sdk.survey.ui.components.PreviewDefaultAvatar (CircularAvatarComponent.kt:98)"
            );
         }

         Avatar var10000 = Avatar.create("", "");
         Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
         recovered$7baf454f09f9b402(var10000, Color.Companion.recovered$ba858dcfc5e04b9c(), 0.0F, var0, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var3 = var0.endRestartGroup();
      if (var3 != null) {
         final int recoveredCapture_1322_0 = var1;
         var3.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1322_0, recoveredArgument_1322_1) -> CircularAvatarComponentKt.PreviewDefaultAvatar$lambda$7(recoveredCapture_1322_0, (androidx.compose.runtime.Composer)recoveredArgument_1322_0, ((java.lang.Integer)recoveredArgument_1322_1).intValue())));
      }
   }

   @Preview
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PreviewInitialAvatar(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1788709612);
      ComposerKt.sourceInformation(var0, "C(PreviewInitialAvatar)105@3704L78:CircularAvatarComponent.kt#bmpoeo");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1788709612, var1, -1, "io.intercom.android.sdk.survey.ui.components.PreviewInitialAvatar (CircularAvatarComponent.kt:104)"
            );
         }

         Avatar var10000 = Avatar.create("", "PS");
         Intrinsics.checkNotNullExpressionValue(var10000, "create(...)");
         recovered$7baf454f09f9b402(var10000, Color.Companion.recovered$ff90a8751967f2f7(), 0.0F, var0, 48, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var3 = var0.endRestartGroup();
      if (var3 != null) {
         final int recoveredCapture_1323_0 = var1;
         var3.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1323_0, recoveredArgument_1323_1) -> CircularAvatarComponentKt.PreviewInitialAvatar$lambda$8(recoveredCapture_1323_0, (androidx.compose.runtime.Composer)recoveredArgument_1323_0, ((java.lang.Integer)recoveredArgument_1323_1).intValue())));
      }
   }

   private static final Unit CircularAvatar_aM_cp0Q$lambda$5$lambda$2$lambda$1$lambda$0(String var0, SemanticsPropertyReceiver var1) {
      Intrinsics.checkNotNullParameter(var1, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var1, var0);
      return Unit.INSTANCE;
   }

   private static final Unit CircularAvatar_aM_cp0Q$lambda$6(Avatar var0, long var1, float var3, int var4, int var5, Composer var6, int var7) {
      recovered$7baf454f09f9b402(var0, var1, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit PreviewDefaultAvatar$lambda$7(int var0, Composer var1, int var2) {
      PreviewDefaultAvatar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PreviewInitialAvatar$lambda$8(int var0, Composer var1, int var2) {
      PreviewInitialAvatar(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
