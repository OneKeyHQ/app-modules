package io.intercom.android.sdk.m5.conversation.ui.components;

import android.content.Context;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.Arrangement.HorizontalOrVertical;
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
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.semantics.SemanticsModifierKt;
import androidx.compose.ui.semantics.SemanticsPropertiesKt;
import androidx.compose.ui.semantics.SemanticsPropertyReceiver;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.AvatarGroupKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.TimeFormatterExtKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\u001aE\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0001¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000e¨\u0006\u0010",
   d2 = {
         "MessageMetadata",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "title",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "suffix",
         "timestamp",
         "",
         "(Ljava/util/List;Ljava/lang/String;Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/Long;Landroidx/compose/runtime/Composer;II)V",
         "MessageMetadataPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "MessageMetadataLongTextPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nMessageMetadata.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageMetadata.kt\nio/intercom/android/sdk/m5/conversation/ui/components/MessageMetadataKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 4 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 5 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 6 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 7 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,133:1\n118#2:134\n118#2:163\n118#2:164\n99#3,6:135\n106#3:181\n81#4,6:141\n88#4,6:156\n96#4:180\n402#5,9:147\n411#5:162\n412#5,2:178\n1047#6,6:165\n1047#6,6:171\n75#7:177\n*S KotlinDebug\n*F\n+ 1 MessageMetadata.kt\nio/intercom/android/sdk/m5/conversation/ui/components/MessageMetadataKt\n*L\n38#1:134\n42#1:163\n44#1:164\n35#1:135,6\n35#1:181\n35#1:141,6\n35#1:156,6\n35#1:180\n35#1:147,9\n35#1:162\n35#1:178,2\n59#1:165,6\n77#1:171,6\n85#1:177\n*E\n"
)
public final class MessageMetadataKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MessageMetadata(
      @NotNull List var0,
      @NotNull String var1,
      @Nullable Modifier var2,
      @Nullable String var3,
      @Nullable Long var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "avatars");
      Intrinsics.checkNotNullParameter(var1, "title");
      var5 = var5.startRestartGroup(1631390024);
      ComposerKt.sourceInformation(var5, "C(MessageMetadata)P(!1,4)33@1359L10,34@1426L1804:MessageMetadata.kt#o19g89");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if ((var7 & 8) != 0) {
            var3 = null;
         }

         if ((var7 & 16) != 0) {
            var4 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1631390024, var8, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadata (MessageMetadata.kt:32)");
         }

         TextStyle var9 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var5, IntercomTheme.$stable).getType04Point5(),
            0L,
            0L,
            FontWeight.Companion.getSemiBold(),
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
         Vertical var10 = Alignment.Companion.getCenterVertically();
         int var12 = 4;
         int var13 = 0;
         HorizontalOrVertical var11 = Arrangement.INSTANCE.recovered$cb1245a98196b4f7(Dp.recovered$95b3d103663bf060(var12));
         var12 = 432 | 14 & var8 >> 6;
         var13 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var14 = RowKt.rowMeasurePolicy(var11, var10, var5, 14 & var12 >> 3 | 112 & var12 >> 3);
         Modifier var15 = var2;
         MeasurePolicy var16 = var14;
         Composer var17 = var5;
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
         ComposerKt.sourceInformationMarkerStart(var30, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var32 = 6 | 112 & var12 >> 6;
         Composer var33 = var30;
         RowScope var34 = RowScopeInstance.INSTANCE;
         int var35 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, -624628283, "C46@1805L202:MessageMetadata.kt#o19g89");
         var33.startReplaceGroup(1642417258);
         ComposerKt.sourceInformation(var33, "40@1632L154");
         if (!var0.isEmpty()) {
            Modifier var10000 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            Modifier var38 = PaddingKt.recovered$03b481d31d62885a(var10000, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0.0F, 11, null);
            $i$f$getDpx = 24;
            int $i$f$getDpxx = 0;
            float var57 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
            AvatarGroupKt.recovered$813c81fac421be56(var0, var38, var57, 0L, var33, 432 | 14 & var8, 8);
         }

         var33.endReplaceGroup();
         Modifier var61 = var34.weight(Modifier.Companion, 1.0F, false);
         int var58 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var1, var61, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var58, false, 1, 0, null, var9, var33, 14 & var8 >> 3, 24960, 110588
         );
         var33.startReplaceGroup(1642431398);
         ComposerKt.sourceInformation(var33, "*64@2373L161");
         if (var3 == null) {
            Object var84 = null;
         } else {
            String var60 = var3;
            int var62 = 0;
            var33.startReplaceGroup(1642431772);
            ComposerKt.sourceInformation(var33, "58@2217L71,56@2124L222");
            if (!StringsKt.isBlank(var1)) {
               String var85 = "•";
               Modifier var10001 = Modifier.Companion;
               boolean var10002 = false;
               var33.startReplaceGroup(-704192764);
               ComposerKt.sourceInformation(var33, "CC(remember):MessageMetadata.kt#9igjgp");
               Composer var40 = var33;
               boolean var41 = false;
               int var42 = 0;
               Object var43 = var40.rememberedValue();
               int var44 = 0;
               Object var10003;
               if (var43 == Composer.Companion.getEmpty()) {
                  boolean var45 = false;
                  Modifier var46 = var10001;
                  String var47 = "•";
                  int var48 = 0;
                  Function1 var49 = ((kotlin.jvm.functions.Function1) (recoveredArgument_458_0) -> MessageMetadataKt.MessageMetadata$lambda$6$lambda$2$lambda$1$lambda$0((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredArgument_458_0));
                  var85 = var47;
                  var10001 = var46;
                  var10002 = var45;
                  Object var77 = var49;
                  var40.updateRememberedValue(var77);
                  var10003 = var77;
               } else {
                  var10003 = var43;
               }

               Function1 var50 = (Function1)var10003;
               var33.endReplaceGroup();
               TextKt.recovered$5b114ab32a4f4190(
                  var85,
                  SemanticsModifierKt.semantics$default(var10001, var10002, var50, 1, null),
                  0L,
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
                  var9,
                  var33,
                  6,
                  0,
                  131068
               );
            }

            var33.endReplaceGroup();
            int var81 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var60, null, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var81, false, 1, 0, null, var9, var33, 0, 24960, 110590
            );
         }

         var33.endReplaceGroup();
         var33.startReplaceGroup(1642448818);
         ComposerKt.sourceInformation(var33, "*84@3076L7,82@2945L269");
         if (var4 == null) {
            Object var87 = null;
         } else {
            long var51 = var4.longValue();
            int var63 = 0;
            var33.startReplaceGroup(1642449084);
            ComposerKt.sourceInformation(var33, "76@2789L71,74@2696L222");
            if (!StringsKt.isBlank(var1) || (var3 != null ? !StringsKt.isBlank(var3) : false)) {
               String var86 = "•";
               Modifier var89 = Modifier.Companion;
               boolean var90 = false;
               var33.startReplaceGroup(-704174460);
               ComposerKt.sourceInformation(var33, "CC(remember):MessageMetadata.kt#9igjgp");
               Composer var64 = var33;
               boolean var66 = false;
               int var69 = 0;
               Object var71 = var64.rememberedValue();
               int var73 = 0;
               Object var91;
               if (var71 == Composer.Companion.getEmpty()) {
                  boolean var75 = false;
                  Modifier var76 = var89;
                  String var78 = "•";
                  int var79 = 0;
                  Function1 var82 = ((kotlin.jvm.functions.Function1) (recoveredArgument_459_0) -> MessageMetadataKt.MessageMetadata$lambda$6$lambda$5$lambda$4$lambda$3((androidx.compose.ui.semantics.SemanticsPropertyReceiver)recoveredArgument_459_0));
                  var86 = var78;
                  var89 = var76;
                  var90 = var75;
                  Object var80 = var82;
                  var64.updateRememberedValue(var80);
                  var91 = var80;
               } else {
                  var91 = var71;
               }

               Function1 var53 = (Function1)var91;
               var33.endReplaceGroup();
               TextKt.recovered$5b114ab32a4f4190(
                  var86,
                  SemanticsModifierKt.semantics$default(var89, var90, var53, 1, null),
                  0L,
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
                  var9,
                  var33,
                  6,
                  0,
                  131068
               );
            }

            var33.endReplaceGroup();
            Modifier var83 = var34.weight(Modifier.Companion, 0.5F, false);
            CompositionLocal var67 = AndroidCompositionLocals_androidKt.getLocalContext();
            int var70 = 0;
            int var72 = 0;
            ComposerKt.sourceInformationMarkerStart(var33, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
            Object var74 = var33.consume(var67);
            ComposerKt.sourceInformationMarkerEnd(var33);
            String var65 = TimeFormatterExtKt.formattedDateFromLong(var51, (Context)var74);
            int var68 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var65, var83, 0L, null, 0L, null, null, null, 0L, null, null, 0L, var68, false, 1, 0, null, var9, var33, 0, 24960, 110588
            );
         }

         var33.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var33);
         ComposerKt.sourceInformationMarkerEnd(var30);
         var24.endNode();
         ComposerKt.sourceInformationMarkerEnd(var24);
         ComposerKt.sourceInformationMarkerEnd(var17);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var88 = var5.endRestartGroup();
      if (var88 != null) {
         final java.util.List recoveredCapture_457_0 = var0;
         final java.lang.String recoveredCapture_457_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_457_2 = var2;
         final java.lang.String recoveredCapture_457_3 = var3;
         final java.lang.Long recoveredCapture_457_4 = var4;
         final int recoveredCapture_457_5 = var6;
         final int recoveredCapture_457_6 = var7;
         var88.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_457_0, recoveredArgument_457_1) -> MessageMetadataKt.MessageMetadata$lambda$7(recoveredCapture_457_0, recoveredCapture_457_1, recoveredCapture_457_2, recoveredCapture_457_3, recoveredCapture_457_4, recoveredCapture_457_5, recoveredCapture_457_6, (androidx.compose.runtime.Composer)recoveredArgument_457_0, ((java.lang.Integer)recoveredArgument_457_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MessageMetadataPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1316869201);
      ComposerKt.sourceInformation(var0, "C(MessageMetadataPreview)96@3307L406:MessageMetadata.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1316869201, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadataPreview (MessageMetadata.kt:95)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$MessageMetadataKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_461_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_461_0, recoveredArgument_461_1) -> MessageMetadataKt.MessageMetadataPreview$lambda$8(recoveredCapture_461_0, (androidx.compose.runtime.Composer)recoveredArgument_461_0, ((java.lang.Integer)recoveredArgument_461_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MessageMetadataLongTextPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-764241754);
      ComposerKt.sourceInformation(var0, "C(MessageMetadataLongTextPreview)115@3798L599:MessageMetadata.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -764241754, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.MessageMetadataLongTextPreview (MessageMetadata.kt:114)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$MessageMetadataKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_460_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_460_0, recoveredArgument_460_1) -> MessageMetadataKt.MessageMetadataLongTextPreview$lambda$9(recoveredCapture_460_0, (androidx.compose.runtime.Composer)recoveredArgument_460_0, ((java.lang.Integer)recoveredArgument_460_1).intValue())));
      }
   }

   private static final Unit MessageMetadata$lambda$6$lambda$2$lambda$1$lambda$0(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var0, "");
      return Unit.INSTANCE;
   }

   private static final Unit MessageMetadata$lambda$6$lambda$5$lambda$4$lambda$3(SemanticsPropertyReceiver var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$semantics");
      SemanticsPropertiesKt.setContentDescription(var0, "");
      return Unit.INSTANCE;
   }

   private static final Unit MessageMetadata$lambda$7(
      List var0, String var1, Modifier var2, String var3, Long var4, int var5, int var6, Composer var7, int var8
   ) {
      MessageMetadata(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit MessageMetadataPreview$lambda$8(int var0, Composer var1, int var2) {
      MessageMetadataPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MessageMetadataLongTextPreview$lambda$9(int var0, Composer var1, int var2) {
      MessageMetadataLongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
