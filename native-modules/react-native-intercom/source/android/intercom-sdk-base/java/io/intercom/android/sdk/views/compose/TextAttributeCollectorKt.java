package io.intercom.android.sdk.views.compose;

import android.content.Context;
import android.content.res.Resources;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.IntrinsicKt;
import androidx.compose.foundation.layout.IntrinsicSize;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.shape.CornerBasedShape;
import androidx.compose.foundation.shape.CornerSize;
import androidx.compose.foundation.shape.CornerSizeKt;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ClipKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.models.CountryAreaCode;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.PhoneNumberValidator;
import io.intercom.android.sdk.utilities.UtilsKt;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Unit;
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
   d1 = "\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\f\u001aU\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u0014\b\u0002\u0010\b\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\t2\u0014\b\u0002\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\f\u001a;\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u0013H\u0003¢\u0006\u0002\u0010\u0014\u001a\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\nH\u0002\u001a\u0014\u0010\u0018\u001a\u00020\n*\u00020\u00052\u0006\u0010\u0019\u001a\u00020\u0016H\u0002\u001a\u0011\u0010\u001a\u001a\u00020\u001b*\u00020\u0005H\u0002¢\u0006\u0002\u0010\u001c\u001a\f\u0010\u001d\u001a\u00020\u0007*\u00020\u0005H\u0002\u001a\r\u0010\u001e\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010 \u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010!\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010\"\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010#\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f\u001a\r\u0010$\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001f¨\u0006%²\u0006\n\u0010&\u001a\u00020\nX\u008a\u008e\u0002²\u0006\n\u0010'\u001a\u00020\nX\u008a\u008e\u0002",
   d2 = {
         "TextAttributeCollector",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "attributeData",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "loading",
         "",
         "onValidationError",
         "Lkotlin/Function1;",
         "",
         "onSubmitAttribute",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/AttributeData;ZLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "TextAttributeTrailingComponent",
         "disabled",
         "submitted",
         "shape",
         "Landroidx/compose/foundation/shape/CornerBasedShape;",
         "onClick",
         "Lkotlin/Function0;",
         "(ZZZLandroidx/compose/foundation/shape/CornerBasedShape;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "getCountryAreaCodeFromText",
         "Lio/intercom/android/sdk/models/CountryAreaCode;",
         "text",
         "getHint",
         "countryAreaCode",
         "getKeyboardType",
         "Landroidx/compose/ui/text/input/KeyboardType;",
         "(Lio/intercom/android/sdk/m5/conversation/states/AttributeData;)I",
         "isPhoneType",
         "FilledTextAttributePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "EmptyTextAttributePreview",
         "SubmittedTextAttributePreview",
         "DisabledTextAttributePreview",
         "SubmittedAndDisabledTextAttributePreview",
         "PhoneAttributePreview",
         "intercom-sdk-base_release",
         "value",
         "countryFlag"
   }
)
@SourceDebugExtension(
   "SMAP\nTextAttributeCollector.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/TextAttributeCollectorKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,367:1\n1047#2,6:368\n1047#2,6:374\n1047#2,6:381\n1047#2,6:387\n1047#2,6:394\n75#3:380\n118#4:393\n118#4:400\n118#4:401\n118#4:402\n118#4:403\n118#4:435\n118#4:436\n70#5:404\n68#5,8:405\n77#5:440\n81#6,6:413\n88#6,6:428\n96#6:439\n402#7,9:419\n411#7:434\n412#7,2:437\n85#8:441\n117#8,2:442\n85#8:444\n117#8,2:445\n*S KotlinDebug\n*F\n+ 1 TextAttributeCollector.kt\nio/intercom/android/sdk/views/compose/TextAttributeCollectorKt\n*L\n49#1:368,6\n50#1:374,6\n70#1:381,6\n72#1:387,6\n84#1:394,6\n52#1:380\n79#1:393\n162#1:400\n165#1:401\n166#1:402\n171#1:403\n187#1:435\n188#1:436\n160#1:404\n160#1:405,8\n160#1:440\n160#1:413,6\n160#1:428,6\n160#1:439\n160#1:419,9\n160#1:434\n160#1:437,2\n70#1:441\n70#1:442,2\n72#1:444\n72#1:445,2\n*E\n"
)
public final class TextAttributeCollectorKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextAttributeCollector(
      @Nullable Modifier var0,
      @NotNull AttributeData var1,
      boolean var2,
      @Nullable Function1 var3,
      @Nullable Function1 var4,
      @Nullable Composer var5,
      int var6,
      int var7
   ) {
      Intrinsics.checkNotNullParameter(var1, "attributeData");
      var5 = var5.startRestartGroup(-1938202913);
      ComposerKt.sourceInformation(
         var5,
         "C(TextAttributeCollector)P(2!2,4)48@2199L2,49@2252L2,51@2291L7,55@2411L6,69@2871L55,69@2854L72,71@2968L94,71@2951L111,83@3281L162,99@3810L181,110@4159L1407,81@3207L2366:TextAttributeCollector.kt#h0e4f2"
      );
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changedInstance(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 16) != 0) {
         var8 |= 24576;
      } else if ((var6 & 24576) == 0) {
         var8 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var8 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var7 & 4) != 0) {
            var2 = false;
         }

         if ((var7 & 8) != 0) {
            var5.startReplaceGroup(1971770096);
            ComposerKt.sourceInformation(var5, "CC(remember):TextAttributeCollector.kt#9igjgp");
            Composer var10 = var5;
            boolean var11 = false;
            int var12 = 0;
            Object var13 = var10.rememberedValue();
            int var14 = 0;
            Object var10000;
            if (var13 == Composer.Companion.getEmpty()) {
               int var15 = 0;
               Object var52 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1661_0) -> TextAttributeCollectorKt.TextAttributeCollector$lambda$1$lambda$0((java.lang.String)recoveredArgument_1661_0));
               var10.updateRememberedValue(var52);
               var10000 = var52;
            } else {
               var10000 = var13;
            }

            Function1 var9 = (Function1)var10000;
            var5.endReplaceGroup();
            var3 = var9;
         }

         if ((var7 & 16) != 0) {
            var5.startReplaceGroup(1971771792);
            ComposerKt.sourceInformation(var5, "CC(remember):TextAttributeCollector.kt#9igjgp");
            Composer var38 = var5;
            boolean var41 = false;
            int var44 = 0;
            Object var47 = var38.rememberedValue();
            int var50 = 0;
            Object var77;
            if (var47 == Composer.Companion.getEmpty()) {
               int var53 = 0;
               Object var54 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1662_0) -> TextAttributeCollectorKt.TextAttributeCollector$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredArgument_1662_0));
               var38.updateRememberedValue(var54);
               var77 = var54;
            } else {
               var77 = var47;
            }

            Function1 var36 = (Function1)var77;
            var5.endReplaceGroup();
            var4 = var36;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1938202913, var8, -1, "io.intercom.android.sdk.views.compose.TextAttributeCollector (TextAttributeCollector.kt:50)");
         }

         CompositionLocal var39 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var42 = 0;
         int var45 = 0;
         ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var48 = var5.consume(var39);
         ComposerKt.sourceInformationMarkerEnd(var5);
         Context var37 = (Context)var48;
         Resources var40 = var37.getResources();
         Locale var43 = UtilsKt.getLocaleCompat(var37);
         CornerBasedShape var46 = IntercomTheme.INSTANCE.getShapes(var5, IntercomTheme.$stable).getSmall();
         CountryAreaCode var78;
         if (isPhoneType(var1)) {
            PhoneNumberValidator.loadCountryAreaCodes(var37);
            var78 = PhoneNumberValidator.getCountryAreaCodeFromLocale(var43.getCountry());
         } else {
            var78 = CountryAreaCode.UNKNOWN;
         }

         CountryAreaCode var49 = var78;
         boolean var51 = var1.isFormDisabled();
         boolean var55 = var1.getAttribute().getSubmitted();
         boolean var16 = Intrinsics.areEqual(var1.getAttribute().getMultiline(), true);
         Object[] var79 = new Object[0];
         var5.startReplaceGroup(1971791653);
         ComposerKt.sourceInformation(var5, "CC(remember):TextAttributeCollector.kt#9igjgp");
         Composer var19 = var5;
         boolean recoveredAttributeChanged = var5.changedInstance(var1);
         int var20;
         int var21 = 0;
         Object var22 = var19.rememberedValue();
         int var23 = 0;
         Object var84;
         if (!recoveredAttributeChanged && var22 != Composer.Companion.getEmpty()) {
            var84 = (Function0)var22;
         } else {
            Object[] var34 = var79;
            int var24 = 0;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1663_0 = var1;
            var84 = ((kotlin.jvm.functions.Function0) () -> TextAttributeCollectorKt.TextAttributeCollector$lambda$5$lambda$4(recoveredCapture_1663_0));
            var79 = var34;
            Object var25 = var84;
            var19.updateRememberedValue(var25);
            var84 = (Function0)var25;
         }

         Function0 var18 = (kotlin.jvm.functions.Function0) var84;
         var5.endReplaceGroup();
         MutableState var17 = (MutableState)RememberSaveableKt.rememberSaveable(var79, var18, var5, 0);
         Object[] var80 = new Object[0];
         var5.startReplaceGroup(1971794796);
         ComposerKt.sourceInformation(var5, "CC(remember):TextAttributeCollector.kt#9igjgp");
         Composer var59 = var5;
         boolean recoveredCountryChanged = var5.changedInstance(var1) | var5.changedInstance(var49);
         int var62;
         int recoveredCountryInlineMarker = 0;
         Object recoveredCountryCache = var59.rememberedValue();
         int var69/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         if (!recoveredCountryChanged && recoveredCountryCache != Composer.Companion.getEmpty()) {
            var84 = recoveredCountryCache;
         } else {
            Object[] var75 = var80;
            int var71 = 0;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1664_0 = var1;
            final io.intercom.android.sdk.models.CountryAreaCode recoveredCapture_1664_1 = var49;
            var84 = ((kotlin.jvm.functions.Function0) () -> TextAttributeCollectorKt.TextAttributeCollector$lambda$9$lambda$8(recoveredCapture_1664_0, recoveredCapture_1664_1));
            var80 = var75;
            Object var26 = var84;
            var59.updateRememberedValue(var26);
            var84 = (Function0)var26;
         }

         Function0 var57 = (Function0)var84;
         var5.endReplaceGroup();
         MutableState var56 = (MutableState)RememberSaveableKt.rememberSaveable(var80, var57, var5, 0);
         Modifier var81;
         if (var16) {
            var81 = IntrinsicKt.height(var0, IntrinsicSize.Max);
         } else {
            var20 = 40;
            var62 = 0;
            var81 = SizeKt.recovered$3f6c6a76aa292717(var0, Dp.recovered$95b3d103663bf060(var20));
         }

         Modifier var58 = var81;
         String var61 = TextAttributeCollector$lambda$6(var17);
         boolean var64 = !var51;
         var22 = new KeyboardOptions(0, null, getKeyboardType(var1), 0, null, null, null, 123, null);
         boolean var68 = !var16;
         var69/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = var16 ? 2 : 1;
         var5.startReplaceGroup(1971828325);
         ComposerKt.sourceInformation(var5, "106@4062L28");
         Function2 var73 = isPhoneType(var1)
            ? ComposableLambdaKt.rememberComposableLambda(-1990705988, true, new TextAttributeCollectorKt$TextAttributeCollector$3(var56), var5, 54)
            : null;
         var5.endReplaceGroup();
         Function2 var72 = var73;
         String var82 = var61;
         var5.startReplaceGroup(1971804880);
         ComposerKt.sourceInformation(var5, "CC(remember):TextAttributeCollector.kt#9igjgp");
         Composer var27 = var5;
         boolean var28 = var5.changed(var17) | var5.changedInstance(var1) | var5.changed(var56);
         int $i$f$cachex = 0;
         Object it$ivx = var27.rememberedValue();
         int var31/* $VF was: $i$a$-let-ComposerKt$cache$1$ivxx */ = 0;
         kotlin.jvm.functions.Function1 var88;
         if (!var28 && it$ivx != Composer.Companion.getEmpty()) {
            var88 = (Function1)it$ivx;
         } else {
            String var76 = var61;
            int var32 = 0;
            final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1665_0 = var1;
            final androidx.compose.runtime.MutableState recoveredCapture_1665_1 = var17;
            final androidx.compose.runtime.MutableState recoveredCapture_1665_2 = var56;
            var88 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1665_0) -> TextAttributeCollectorKt.TextAttributeCollector$lambda$13$lambda$12(recoveredCapture_1665_0, recoveredCapture_1665_1, recoveredCapture_1665_2, (java.lang.String)recoveredArgument_1665_0));
            var82 = var76;
            Object var33 = var88;
            var27.updateRememberedValue(var33);
            var88 = (Function1)var33;
         }

         Function1 var74 = var88;
         var5.endReplaceGroup();
         IntercomOutlinedTextFieldKt.IntercomOutlinedTextField(
            var82,
            var74,
            var58,
            var64,
            var55,
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1290485581, true, new TextAttributeCollectorKt$TextAttributeCollector$5(var1, var49), var5, 54),
            var72,
            ComposableLambdaKt.rememberComposableLambda(
               930248561, true, new TextAttributeCollectorKt$TextAttributeCollector$6(var51, var55, var2, var46, var17, var3, var40, var1, var4), var5, 54
            ),
            false,
            null,
            (KeyboardOptions)var22,
            null,
            var68,
            3,
            var69/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */,
            null,
            var46,
            null,
            null,
            var5,
            817889280,
            196608,
            0,
            1715296
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var83 = var5.endRestartGroup();
      if (var83 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1666_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.AttributeData recoveredCapture_1666_1 = var1;
         final boolean recoveredCapture_1666_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_1666_3 = var3;
         final kotlin.jvm.functions.Function1 recoveredCapture_1666_4 = var4;
         final int recoveredCapture_1666_5 = var6;
         final int recoveredCapture_1666_6 = var7;
         var83.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1666_0, recoveredArgument_1666_1) -> TextAttributeCollectorKt.TextAttributeCollector$lambda$14(recoveredCapture_1666_0, recoveredCapture_1666_1, recoveredCapture_1666_2, recoveredCapture_1666_3, recoveredCapture_1666_4, recoveredCapture_1666_5, recoveredCapture_1666_6, (androidx.compose.runtime.Composer)recoveredArgument_1666_0, ((java.lang.Integer)recoveredArgument_1666_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void TextAttributeTrailingComponent(
      boolean var0, boolean var1, boolean var2, CornerBasedShape var3, Function0 var4, Composer var5, int var6
   ) {
      var5 = var5.startRestartGroup(1872215775);
      ComposerKt.sourceInformation(var5, "C(TextAttributeTrailingComponent)P(!1,4!1,3)159@5943L1310:TextAttributeCollector.kt#h0e4f2");
      int var7 = var6;
      if ((var6 & 6) == 0) {
         var7 |= var5.changed(var0) ? 4 : 2;
      }

      if ((var6 & 48) == 0) {
         var7 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var6 & 384) == 0) {
         var7 |= var5.changed(var2) ? 256 : 128;
      }

      if ((var6 & 3072) == 0) {
         var7 |= var5.changed(var3) ? 2048 : 1024;
      }

      if ((var6 & 24576) == 0) {
         var7 |= var5.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var7 & 9363) == 9362 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1872215775, var7, -1, "io.intercom.android.sdk.views.compose.TextAttributeTrailingComponent (TextAttributeCollector.kt:153)"
            );
         }

         long var10000;
         if (var1) {
            var5.startReplaceGroup(803985965);
            var5.endReplaceGroup();
            var10000 = Color.Companion.recovered$562f21ab17d9b4c7();
         } else if (var0) {
            var5.startReplaceGroup(803987658);
            ComposerKt.sourceInformation(var5, "156@5873L6");
            long var10 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).recovered$090a6e753d82e539();
            var5.endReplaceGroup();
            var10000 = var10;
         } else {
            var5.startReplaceGroup(803989128);
            ComposerKt.sourceInformation(var5, "157@5919L6");
            long var44 = IntercomTheme.INSTANCE.getColors(var5, IntercomTheme.$stable).recovered$202acdf3dbb5c020();
            var5.endReplaceGroup();
            var10000 = var44;
         }

         long var8 = var10000;
         Modifier var58 = Modifier.Companion;
         int var45 = 8;
         int var11 = 0;
         Modifier var59 = PaddingKt.recovered$03b481d31d62885a(var58, Dp.recovered$95b3d103663bf060(var45), 0.0F, 0.0F, 0.0F, 14, null);
         var45 = 0;
         var11 = 0;
         CornerSize var10002 = CornerSizeKt.recovered$ea3a4e3d7147c535(Dp.recovered$95b3d103663bf060(var45));
         var45 = 0;
         var11 = 0;
         Modifier var60 = SizeKt.fillMaxHeight$default(
            BackgroundKt.recovered$42a3174180b3fb7a(
               ClipKt.clip(
                  var59,
                  CornerBasedShape.copy$default(
                     var3, var10002, null, null, CornerSizeKt.recovered$ea3a4e3d7147c535(Dp.recovered$95b3d103663bf060(var45)), 6, null
                  )
               ),
               var8,
               null,
               2,
               null
            ),
            0.0F,
            1,
            null
         );
         var45 = 40;
         var11 = 0;
         Modifier var49 = ClickableKt.recovered$1c9660c41fd850dc(
            SizeKt.recovered$4f2dac42b6e775a9(var60, Dp.recovered$95b3d103663bf060(var45)), !var1 && !var2 && !var0, null, null, null, var4, 14, null
         );
         Alignment var53 = Alignment.Companion.getCenter();
         byte var13 = 48;
         int var14 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var5, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var12 = false;
         MeasurePolicy var15 = BoxKt.maybeCachedBoxMeasurePolicy(var53, var12);
         Modifier var16 = var49;
         MeasurePolicy var17 = var15;
         Composer var18 = var5;
         int var19 = 112 & var13 << 3;
         int var20 = 0;
         ComposerKt.sourceInformationMarkerStart(var18, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var21 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var18, 0));
         CompositionLocalMap var22 = var18.getCurrentCompositionLocalMap();
         Modifier var23 = ComposedModifierKt.materializeModifier(var18, var16);
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
         ComposerKt.sourceInformationMarkerStart(var31, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var33 = 6 | 112 & var13 >> 6;
         Composer var34 = var31;
         BoxScope var35 = BoxScopeInstance.INSTANCE;
         int var36 = 0;
         ComposerKt.sourceInformationMarkerStart(var34, 305495919, "C:TextAttributeCollector.kt#h0e4f2");
         if (var1) {
            var34.startReplaceGroup(1118233872);
            ComposerKt.sourceInformation(var34, "179@6588L65,181@6735L6,178@6556L207");
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(drawable.intercom_attribute_verified_tick, var34, 0),
               null,
               null,
               IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$b549188d2697e772(),
               var34,
               48 | Painter.$stable,
               4
            );
            var34.endReplaceGroup();
         } else if (var2) {
            var34.startReplaceGroup(305740973);
            ComposerKt.sourceInformation(var34, "185@6853L6,184@6788L180");
            long var37 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
            Modifier var61 = Modifier.Companion;
            int $this$dp$ivx = 20;
            int $i$f$getDpx = 0;
            Modifier var41 = SizeKt.recovered$111ef94c93e78c04(var61, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            $i$f$getDpx = 3;
            int $i$f$getDpxx = 0;
            float var56 = Dp.recovered$95b3d103663bf060($i$f$getDpx);
            ProgressIndicatorKt.recovered$276fbc1f701bb35f(var41, var37, var56, 0L, 0, 0.0F, var34, 390, 56);
            var34.endReplaceGroup();
         } else {
            var34.startReplaceGroup(1118247800);
            ComposerKt.sourceInformation(var34, "191@7022L49,190@6990L247");
            Painter var62 = PainterResources_androidKt.painterResource(drawable.intercom_chevron, var34, 0);
            long var10003;
            if (var0) {
               var34.startReplaceGroup(1118253609);
               ComposerKt.sourceInformation(var34, "193@7167L6");
               long var54 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).getText().recovered$090a6e753d82e539();
               var34.endReplaceGroup();
               var10003 = var54;
            } else {
               var34.startReplaceGroup(1118254729);
               ComposerKt.sourceInformation(var34, "193@7207L6");
               long var55 = IntercomTheme.INSTANCE.getColors(var34, IntercomTheme.$stable).recovered$8a5d9ad2993bccce();
               var34.endReplaceGroup();
               var10003 = var55;
            }

            IconKt.recovered$e00adffb7fe45b02(var62, null, null, var10003, var34, 48 | Painter.$stable, 4);
            var34.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var34);
         ComposerKt.sourceInformationMarkerEnd(var31);
         var25.endNode();
         ComposerKt.sourceInformationMarkerEnd(var25);
         ComposerKt.sourceInformationMarkerEnd(var18);
         ComposerKt.sourceInformationMarkerEnd(var5);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var63 = var5.endRestartGroup();
      if (var63 != null) {
         final boolean recoveredCapture_1667_0 = var0;
         final boolean recoveredCapture_1667_1 = var1;
         final boolean recoveredCapture_1667_2 = var2;
         final androidx.compose.foundation.shape.CornerBasedShape recoveredCapture_1667_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_1667_4 = var4;
         final int recoveredCapture_1667_5 = var6;
         var63.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1667_0, recoveredArgument_1667_1) -> TextAttributeCollectorKt.TextAttributeTrailingComponent$lambda$16(recoveredCapture_1667_0, recoveredCapture_1667_1, recoveredCapture_1667_2, recoveredCapture_1667_3, recoveredCapture_1667_4, recoveredCapture_1667_5, (androidx.compose.runtime.Composer)recoveredArgument_1667_0, ((java.lang.Integer)recoveredArgument_1667_1).intValue())));
      }
   }

   private static final CountryAreaCode getCountryAreaCodeFromText(String var0) {
      String var1 = PhoneNumberValidator.normalizeNumber(var0);
      var1 = PhoneNumberValidator.stripPrefix(var1);
      CountryAreaCode var10000 = PhoneNumberValidator.getCountryAreaCodeFromNumber(var1);
      Intrinsics.checkNotNullExpressionValue(var10000, "getCountryAreaCodeFromNumber(...)");
      return var10000;
   }

   private static final String getHint(AttributeData var0, CountryAreaCode var1) {
      String var2 = var0.getAttribute().getRenderType();
      String var10000;
      if (Intrinsics.areEqual(var2, "email")) {
         var10000 = "email@domain.com";
      } else if (Intrinsics.areEqual(var2, "phone")) {
         String var3 = Intrinsics.areEqual(var1, CountryAreaCode.UNKNOWN) ? "+1" : '+' + var1.getDialCode();
         var10000 = var3 + " 123 456 7890";
      } else {
         var10000 = "";
      }

      return var10000;
   }

   private static final int getKeyboardType(AttributeData var0) {
      String var1 = var0.getAttribute().getRenderType();
      switch (var1.hashCode()) {
         case -1034364087:
            if (var1.equals("number")) {
               return KeyboardType.Companion.recovered$b13b0fa1ee2a59aa();
            }
            break;
         case 96619420:
            if (var1.equals("email")) {
               return KeyboardType.Companion.recovered$690918277adbb689();
            }
            break;
         case 97526364:
            if (var1.equals("float")) {
               return KeyboardType.Companion.recovered$1bffe4c8ddb4d721();
            }
            break;
         case 106642798:
            if (var1.equals("phone")) {
               return KeyboardType.Companion.recovered$3f6427f56eed8f7b();
            }
      }

      return KeyboardType.Companion.recovered$29477a01a04c8fe0();
   }

   private static final boolean isPhoneType(AttributeData var0) {
      return Intrinsics.areEqual(var0.getAttribute().getRenderType(), "phone");
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void FilledTextAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1421911931);
      ComposerKt.sourceInformation(var0, "C(FilledTextAttributePreview)234@8425L568:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1421911931, var1, -1, "io.intercom.android.sdk.views.compose.FilledTextAttributePreview (TextAttributeCollector.kt:233)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1657_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1657_0, recoveredArgument_1657_1) -> TextAttributeCollectorKt.FilledTextAttributePreview$lambda$17(recoveredCapture_1657_0, (androidx.compose.runtime.Composer)recoveredArgument_1657_0, ((java.lang.Integer)recoveredArgument_1657_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void EmptyTextAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(990171980);
      ComposerKt.sourceInformation(var0, "C(EmptyTextAttributePreview)257@9074L526:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(990171980, var1, -1, "io.intercom.android.sdk.views.compose.EmptyTextAttributePreview (TextAttributeCollector.kt:256)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1656_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1656_0, recoveredArgument_1656_1) -> TextAttributeCollectorKt.EmptyTextAttributePreview$lambda$18(recoveredCapture_1656_0, (androidx.compose.runtime.Composer)recoveredArgument_1656_0, ((java.lang.Integer)recoveredArgument_1656_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedTextAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(914016734);
      ComposerKt.sourceInformation(var0, "C(SubmittedTextAttributePreview)279@9685L570:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               914016734, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedTextAttributePreview (TextAttributeCollector.kt:278)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1660_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1660_0, recoveredArgument_1660_1) -> TextAttributeCollectorKt.SubmittedTextAttributePreview$lambda$19(recoveredCapture_1660_0, (androidx.compose.runtime.Composer)recoveredArgument_1660_0, ((java.lang.Integer)recoveredArgument_1660_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void DisabledTextAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1615951967);
      ComposerKt.sourceInformation(var0, "C(DisabledTextAttributePreview)302@10339L569:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1615951967, var1, -1, "io.intercom.android.sdk.views.compose.DisabledTextAttributePreview (TextAttributeCollector.kt:301)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$a427d7fccaa25a17(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1655_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1655_0, recoveredArgument_1655_1) -> TextAttributeCollectorKt.DisabledTextAttributePreview$lambda$20(recoveredCapture_1655_0, (androidx.compose.runtime.Composer)recoveredArgument_1655_0, ((java.lang.Integer)recoveredArgument_1655_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void SubmittedAndDisabledTextAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1140989915);
      ComposerKt.sourceInformation(var0, "C(SubmittedAndDisabledTextAttributePreview)325@11004L582:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1140989915, var1, -1, "io.intercom.android.sdk.views.compose.SubmittedAndDisabledTextAttributePreview (TextAttributeCollector.kt:324)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$abc66e26750555d7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1659_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1659_0, recoveredArgument_1659_1) -> TextAttributeCollectorKt.SubmittedAndDisabledTextAttributePreview$lambda$21(recoveredCapture_1659_0, (androidx.compose.runtime.Composer)recoveredArgument_1659_0, ((java.lang.Integer)recoveredArgument_1659_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void PhoneAttributePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(2075517560);
      ComposerKt.sourceInformation(var0, "C(PhoneAttributePreview)348@11663L539:TextAttributeCollector.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(2075517560, var1, -1, "io.intercom.android.sdk.views.compose.PhoneAttributePreview (TextAttributeCollector.kt:347)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TextAttributeCollectorKt.INSTANCE.recovered$ce33619ac1c358ef(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1658_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1658_0, recoveredArgument_1658_1) -> TextAttributeCollectorKt.PhoneAttributePreview$lambda$22(recoveredCapture_1658_0, (androidx.compose.runtime.Composer)recoveredArgument_1658_0, ((java.lang.Integer)recoveredArgument_1658_1).intValue())));
      }
   }

   private static final Unit TextAttributeCollector$lambda$1$lambda$0(String var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit TextAttributeCollector$lambda$3$lambda$2(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final MutableState TextAttributeCollector$lambda$5$lambda$4(AttributeData var0) {
      String var10000 = var0.getAttribute().getValue();
      if (var10000 == null) {
         var10000 = "";
      }

      return SnapshotStateKt.mutableStateOf$default(var10000, null, 2, null);
   }

   private static final String TextAttributeCollector$lambda$6(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void TextAttributeCollector$lambda$7(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final MutableState TextAttributeCollector$lambda$9$lambda$8(AttributeData var0, CountryAreaCode var1) {
      return SnapshotStateKt.mutableStateOf$default(isPhoneType(var0) ? var1.getEmoji() : "", null, 2, null);
   }

   private static final String TextAttributeCollector$lambda$10(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (String)var1.getValue();
   }

   private static final void TextAttributeCollector$lambda$11(MutableState var0, String var1) {
      MutableState var2 = var0;
      Object var3 = null;
      KProperty var4 = null;
      int var5 = 0;
      var2.setValue(var1);
   }

   private static final Unit TextAttributeCollector$lambda$13$lambda$12(AttributeData var0, MutableState var1, MutableState var2, String var3) {
      Intrinsics.checkNotNullParameter(var3, "it");
      TextAttributeCollector$lambda$7(var1, var3);
      if (isPhoneType(var0)) {
         TextAttributeCollector$lambda$11(var2, getCountryAreaCodeFromText(var3).getEmoji());
      }

      return Unit.INSTANCE;
   }

   private static final Unit TextAttributeCollector$lambda$14(
      Modifier var0, AttributeData var1, boolean var2, Function1 var3, Function1 var4, int var5, int var6, Composer var7, int var8
   ) {
      TextAttributeCollector(var0, var1, var2, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }

   private static final Unit TextAttributeTrailingComponent$lambda$16(
      boolean var0, boolean var1, boolean var2, CornerBasedShape var3, Function0 var4, int var5, Composer var6, int var7
   ) {
      TextAttributeTrailingComponent(var0, var1, var2, var3, var4, var6, RecomposeScopeImplKt.updateChangedFlags(var5 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit FilledTextAttributePreview$lambda$17(int var0, Composer var1, int var2) {
      FilledTextAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmptyTextAttributePreview$lambda$18(int var0, Composer var1, int var2) {
      EmptyTextAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedTextAttributePreview$lambda$19(int var0, Composer var1, int var2) {
      SubmittedTextAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DisabledTextAttributePreview$lambda$20(int var0, Composer var1, int var2) {
      DisabledTextAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit SubmittedAndDisabledTextAttributePreview$lambda$21(int var0, Composer var1, int var2) {
      SubmittedAndDisabledTextAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit PhoneAttributePreview$lambda$22(int var0, Composer var1, int var2) {
      PhoneAttributePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final String access$TextAttributeCollector$lambda$10(MutableState var0) {
      return TextAttributeCollector$lambda$10(var0);
   }

   // $VF: synthetic method
   public static final String access$getHint(AttributeData var0, CountryAreaCode var1) {
      return getHint(var0, var1);
   }

   // $VF: synthetic method
   public static final void access$TextAttributeTrailingComponent(
      boolean var0, boolean var1, boolean var2, CornerBasedShape var3, Function0 var4, Composer var5, int var6
   ) {
      TextAttributeTrailingComponent(var0, var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static final String access$TextAttributeCollector$lambda$6(MutableState var0) {
      return TextAttributeCollector$lambda$6(var0);
   }
}
