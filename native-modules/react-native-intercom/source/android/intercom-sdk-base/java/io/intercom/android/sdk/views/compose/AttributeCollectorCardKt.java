package io.intercom.android.sdk.views.compose;

import android.content.Context;
import android.content.res.Resources;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import io.intercom.android.sdk.m5.conversation.states.AttributeData;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\u001am\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u000e\b\u0002\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\u00052\u0006\u0010\n\u001a\u00020\b2\b\b\u0002\u0010\u000b\u001a\u00020\f2\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\u000eH\u0001¢\u0006\u0002\u0010\u0010\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0012¨\u0006\u0016²\u0006\n\u0010\u0017\u001a\u00020\bX\u008a\u008e\u0002",
   d2 = {
         "AttributeCollectorCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "attributes",
         "",
         "Lio/intercom/android/sdk/models/Attribute;",
         "failedAttributeIdentifiers",
         "",
         "loadingAttributeIdentifiers",
         "partId",
         "isFormDisabled",
         "",
         "onSubmitAttribute",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/states/AttributeData;",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Ljava/util/List;Ljava/util/List;Ljava/lang/String;ZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "BooleanAttributeCard",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ListAttributeCard",
         "TextAttributeCard",
         "MultipleAttributeCard",
         "intercom-sdk-base_release",
         "validationError"
   }
)
@SourceDebugExtension(
   "SMAP\nAttributeCollectorCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/AttributeCollectorCardKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n*L\n1#1,238:1\n1047#2,6:239\n75#3:245\n*S KotlinDebug\n*F\n+ 1 AttributeCollectorCard.kt\nio/intercom/android/sdk/views/compose/AttributeCollectorCardKt\n*L\n38#1:239,6\n40#1:245\n*E\n"
)
public final class AttributeCollectorCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AttributeCollectorCard(
      @Nullable Modifier var0,
      @NotNull List var1,
      @Nullable List var2,
      @Nullable List var3,
      @NotNull String var4,
      boolean var5,
      @Nullable Function1 var6,
      @Nullable Composer var7,
      int var8,
      int var9
   ) {
      Intrinsics.checkNotNullParameter(var1, "attributes");
      Intrinsics.checkNotNullParameter(var4, "partId");
      var7 = var7.startRestartGroup(-136461083);
      ComposerKt.sourceInformation(
         var7, "C(AttributeCollectorCard)P(4!2,3,6)37@1642L2,39@1683L7,43@1783L23,44@1814L4712,41@1706L4820:AttributeCollectorCard.kt#h0e4f2"
      );
      int var10 = var8;
      if ((var9 & 1) != 0) {
         var10 |= 6;
      } else if ((var8 & 6) == 0) {
         var10 |= var7.changed(var0) ? 4 : 2;
      }

      if ((var9 & 2) != 0) {
         var10 |= 48;
      } else if ((var8 & 48) == 0) {
         var10 |= var7.changedInstance(var1) ? 32 : 16;
      }

      if ((var9 & 4) != 0) {
         var10 |= 384;
      } else if ((var8 & 384) == 0) {
         var10 |= var7.changedInstance(var2) ? 256 : 128;
      }

      if ((var9 & 8) != 0) {
         var10 |= 3072;
      } else if ((var8 & 3072) == 0) {
         var10 |= var7.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var9 & 16) != 0) {
         var10 |= 24576;
      } else if ((var8 & 24576) == 0) {
         var10 |= var7.changed(var4) ? 16384 : 8192;
      }

      if ((var9 & 32) != 0) {
         var10 |= 196608;
      } else if ((var8 & 196608) == 0) {
         var10 |= var7.changed(var5) ? 131072 : 65536;
      }

      if ((var9 & 64) != 0) {
         var10 |= 1572864;
      } else if ((var8 & 1572864) == 0) {
         var10 |= var7.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 599187) == 599186 && var7.getSkipping()) {
         var7.skipToGroupEnd();
      } else {
         if ((var9 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var9 & 4) != 0) {
            var2 = CollectionsKt.emptyList();
         }

         if ((var9 & 8) != 0) {
            var3 = CollectionsKt.emptyList();
         }

         if ((var9 & 32) != 0) {
            var5 = false;
         }

         if ((var9 & 64) != 0) {
            var7.startReplaceGroup(-1145807053);
            ComposerKt.sourceInformation(var7, "CC(remember):AttributeCollectorCard.kt#9igjgp");
            Composer var12 = var7;
            boolean var13 = false;
            int var14 = 0;
            Object var15 = var12.rememberedValue();
            int var16 = 0;
            Object var10000;
            if (var15 == Composer.Companion.getEmpty()) {
               int var17 = 0;
               Object var24 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1624_0) -> AttributeCollectorCardKt.AttributeCollectorCard$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.states.AttributeData)recoveredArgument_1624_0));
               var12.updateRememberedValue(var24);
               var10000 = var24;
            } else {
               var10000 = var15;
            }

            Function1 var11 = (Function1)var10000;
            var7.endReplaceGroup();
            var6 = var11;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-136461083, var10, -1, "io.intercom.android.sdk.views.compose.AttributeCollectorCard (AttributeCollectorCard.kt:38)");
         }

         CompositionLocal var20 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var21 = 0;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var7, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var23 = var7.consume(var20);
         ComposerKt.sourceInformationMarkerEnd(var7);
         Resources var19 = ((Context)var23).getResources();
         IntercomCardKt.IntercomCard(
            var0,
            IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var7, IntercomCardStyle.$stable << 15, 31),
            ComposableLambdaKt.rememberComposableLambda(
               -1007698855, true, new AttributeCollectorCardKt$AttributeCollectorCard$2(var1, var2, var19, var3, var4, var5, var6), var7, 54
            ),
            var7,
            384 | 14 & var10 | Style.$stable << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var25 = var7.endRestartGroup();
      if (var25 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1625_0 = var0;
         final java.util.List recoveredCapture_1625_1 = var1;
         final java.util.List recoveredCapture_1625_2 = var2;
         final java.util.List recoveredCapture_1625_3 = var3;
         final java.lang.String recoveredCapture_1625_4 = var4;
         final boolean recoveredCapture_1625_5 = var5;
         final kotlin.jvm.functions.Function1 recoveredCapture_1625_6 = var6;
         final int recoveredCapture_1625_7 = var8;
         final int recoveredCapture_1625_8 = var9;
         var25.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1625_0, recoveredArgument_1625_1) -> AttributeCollectorCardKt.AttributeCollectorCard$lambda$2(recoveredCapture_1625_0, recoveredCapture_1625_1, recoveredCapture_1625_2, recoveredCapture_1625_3, recoveredCapture_1625_4, recoveredCapture_1625_5, recoveredCapture_1625_6, recoveredCapture_1625_7, recoveredCapture_1625_8, (androidx.compose.runtime.Composer)recoveredArgument_1625_0, ((java.lang.Integer)recoveredArgument_1625_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void BooleanAttributeCard(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-96019153);
      ComposerKt.sourceInformation(var0, "C(BooleanAttributeCard)142@6602L467:AttributeCollectorCard.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-96019153, var1, -1, "io.intercom.android.sdk.views.compose.BooleanAttributeCard (AttributeCollectorCard.kt:141)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AttributeCollectorCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1626_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1626_0, recoveredArgument_1626_1) -> AttributeCollectorCardKt.BooleanAttributeCard$lambda$3(recoveredCapture_1626_0, (androidx.compose.runtime.Composer)recoveredArgument_1626_0, ((java.lang.Integer)recoveredArgument_1626_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ListAttributeCard(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-100505407);
      ComposerKt.sourceInformation(var0, "C(ListAttributeCard)163@7142L532:AttributeCollectorCard.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-100505407, var1, -1, "io.intercom.android.sdk.views.compose.ListAttributeCard (AttributeCollectorCard.kt:162)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AttributeCollectorCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1627_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1627_0, recoveredArgument_1627_1) -> AttributeCollectorCardKt.ListAttributeCard$lambda$4(recoveredCapture_1627_0, (androidx.compose.runtime.Composer)recoveredArgument_1627_0, ((java.lang.Integer)recoveredArgument_1627_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TextAttributeCard(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1807263952);
      ComposerKt.sourceInformation(var0, "C(TextAttributeCard)185@7747L465:AttributeCollectorCard.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1807263952, var1, -1, "io.intercom.android.sdk.views.compose.TextAttributeCard (AttributeCollectorCard.kt:184)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AttributeCollectorCardKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1629_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1629_0, recoveredArgument_1629_1) -> AttributeCollectorCardKt.TextAttributeCard$lambda$5(recoveredCapture_1629_0, (androidx.compose.runtime.Composer)recoveredArgument_1629_0, ((java.lang.Integer)recoveredArgument_1629_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void MultipleAttributeCard(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(327354419);
      ComposerKt.sourceInformation(var0, "C(MultipleAttributeCard)206@8289L1035:AttributeCollectorCard.kt#h0e4f2");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(327354419, var1, -1, "io.intercom.android.sdk.views.compose.MultipleAttributeCard (AttributeCollectorCard.kt:205)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$AttributeCollectorCardKt.INSTANCE.recovered$a427d7fccaa25a17(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1628_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1628_0, recoveredArgument_1628_1) -> AttributeCollectorCardKt.MultipleAttributeCard$lambda$6(recoveredCapture_1628_0, (androidx.compose.runtime.Composer)recoveredArgument_1628_0, ((java.lang.Integer)recoveredArgument_1628_1).intValue())));
      }
   }

   private static final Unit AttributeCollectorCard$lambda$1$lambda$0(AttributeData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit AttributeCollectorCard$lambda$2(
      Modifier var0, List var1, List var2, List var3, String var4, boolean var5, Function1 var6, int var7, int var8, Composer var9, int var10
   ) {
      AttributeCollectorCard(var0, var1, var2, var3, var4, var5, var6, var9, RecomposeScopeImplKt.updateChangedFlags(var7 | 1), var8);
      return Unit.INSTANCE;
   }

   private static final Unit BooleanAttributeCard$lambda$3(int var0, Composer var1, int var2) {
      BooleanAttributeCard(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ListAttributeCard$lambda$4(int var0, Composer var1, int var2) {
      ListAttributeCard(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TextAttributeCard$lambda$5(int var0, Composer var1, int var2) {
      TextAttributeCard(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MultipleAttributeCard$lambda$6(int var0, Composer var1, int var2) {
      MultipleAttributeCard(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
