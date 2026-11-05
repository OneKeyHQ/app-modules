package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ImageKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxWithConstraintsScope;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.Updater;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.ColorFilter;
import androidx.compose.ui.graphics.ColorFilter.Companion;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.ContentScale;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.io.Closeable;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nPreviewUri.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt$DocumentPreview$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/Dp\n+ 3 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,320:1\n66#2:321\n87#3,6:322\n94#3:355\n81#4,6:328\n88#4,6:343\n96#4:354\n402#5,9:334\n411#5:349\n412#5,2:352\n118#6:350\n118#6:351\n*S KotlinDebug\n*F\n+ 1 PreviewUri.kt\nio/intercom/android/sdk/m5/preview/ui/PreviewUriKt$DocumentPreview$1\n*L\n225#1:321\n223#1:322,6\n223#1:355\n223#1:328,6\n223#1:343,6\n223#1:354\n223#1:334,9\n223#1:349\n223#1:352,2\n231#1:350\n240#1:351\n*E\n"
)
final class PreviewUriKt$DocumentPreview$1 implements Function3 {
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final Uri $uri;
   // $VF: synthetic field
   final ContentScale $contentScale;
   // $VF: synthetic field
   final boolean $showTitle;

   PreviewUriKt$DocumentPreview$1(Context var1, Uri var2, ContentScale var3, boolean var4) {
      this.$context = var1;
      this.$uri = var2;
      this.$contentScale = var3;
      this.$showTitle = var4;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(BoxWithConstraintsScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$BoxWithConstraints");
      ComposerKt.sourceInformation(var2, "C225@7791L6,222@7667L1023:PreviewUri.kt#hhbc0c");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changed(var1) ? 4 : 2;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-561669217, var4, -1, "io.intercom.android.sdk.m5.preview.ui.DocumentPreview.<anonymous> (PreviewUri.kt:208)");
         }

         float var5 = var1.recovered$4e84a3b22c31a649();
         Object var6 = null;
         var6 = "";

         try {
            Cursor var10000 = this.$context.getContentResolver().query(this.$uri, null, null, null, null);
            if (var10000 != null) {
               var var8 = var10000;
               Throwable var9 = null;

               try {
                  Cursor var10 = (Cursor)var8;
                  int var11 = 0;
                  if (var10.moveToFirst() && var10.getColumnIndex("_display_name") != -1) {
                     var6 = var10.getString(var10.getColumnIndexOrThrow("_display_name"));
                  }

                  Unit var51 = Unit.INSTANCE;
               } catch (Throwable var43) {
                  var9 = var43;
                  throw var43;
               } finally {
                  CloseableKt.closeFinally(var8, var9);
               }
            }
         } catch (SecurityException var45) {
         }

         Modifier var10001 = Modifier.Companion;
         float var47 = 1.414F;
         int var49 = 0;
         Modifier var7 = var1.align(
            BackgroundKt.recovered$42a3174180b3fb7a(
               SizeKt.recovered$dd5685bd56c7f636(var10001, var5, Dp.recovered$95b3d103663bf060(var5 * var47)),
               IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
               null,
               2,
               null
            ),
            Alignment.Companion.getCenter()
         );
         Horizontal var48 = Alignment.Companion.getCenterHorizontally();
         HorizontalOrVertical var50 = Arrangement.INSTANCE.getCenter();
         ContentScale var52 = this.$contentScale;
         boolean var53 = this.$showTitle;
         int var12 = 432;
         int var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var2, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         MeasurePolicy var14 = ColumnKt.columnMeasurePolicy(var50, var48, var2, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var7;
         MeasurePolicy var16 = var14;
         Composer var17 = var2;
         int var18 = 112 & var12 << 3;
         int var19 = 0;
         ComposerKt.sourceInformationMarkerStart(var17, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var20 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var17, 0));
         CompositionLocalMap var21 = var17.getCurrentCompositionLocalMap();
         Modifier var22 = ComposedModifierKt.materializeModifier(var17, var15);
         Function0 var23 = ComposeUiNode.Companion.getConstructor();
         Composer var24 = var17;
         int var25 = 6 | 896 & var18 << 6;
         int var26 = 0;
         ComposerKt.sourceInformationMarkerStart(var24, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var24.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var24.startReusableNode();
         if (var24.getInserting()) {
            var24.createNode(var23);
         } else {
            var24.useNode();
         }

         Composer var27 = Updater.recovered$676aa17b44fd6280(var24);
         int var28 = 0;
         Updater.recovered$89682b199a142880(var27, var16, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var27, var21, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var27, var20, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var27, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var27, var22, ComposeUiNode.Companion.getSetModifier());
         int var29 = 14 & var25 >> 6;
         Composer var30 = var24;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var30, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         ColumnScope var34 = ColumnScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -1663023925, "C233@8133L53,235@8298L6,231@8052L320:PreviewUri.kt#hhbc0c");
         int var36 = 48;
         int var37 = 0;
         float var62;
         if (Dp.recovered$a09f069d48ab27f2(var5, Dp.recovered$95b3d103663bf060(var36)) > 0) {
            var36 = 56;
            var37 = 0;
            var62 = Dp.recovered$95b3d103663bf060(var36);
         } else {
            var36 = 24;
            var37 = 0;
            var62 = Dp.recovered$95b3d103663bf060(var36);
         }

         float var38 = var62;
         Modifier var56 = SizeKt.recovered$111ef94c93e78c04(Modifier.Companion, var38);
         Painter var60 = PainterResources_androidKt.painterResource(drawable.intercom_ic_document, var33, 0);
         ColorFilter var39 = Companion.recovered$14c5668fff7aadde(
            ColorFilter.Companion, IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).recovered$202acdf3dbb5c020(), 0, 2, null
         );
         ImageKt.Image(var60, "Doc Icon", var56, null, var52, 0.0F, var39, var33, 48 | Painter.$stable, 40);
         var33.startReplaceGroup(1193292208);
         ComposerKt.sourceInformation(var33, "239@8418L41,242@8561L6,243@8624L10,240@8476L190");
         if (var53) {
            Modifier var63 = Modifier.Companion;
            var36 = 16;
            var37 = 0;
            SpacerKt.Spacer(SizeKt.recovered$3f6c6a76aa292717(var63, Dp.recovered$95b3d103663bf060(var36)), var33, 6);
            TextKt.recovered$5b114ab32a4f4190(
               (String)var6,
               null,
               IntercomTheme.INSTANCE.getColors(var33, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a(),
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
               IntercomTheme.INSTANCE.getTypography(var33, IntercomTheme.$stable).getType04Point5(),
               var33,
               0,
               0,
               131066
            );
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var2);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((BoxWithConstraintsScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
