package io.intercom.android.sdk.tickets.create.ui;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.tooling.preview.Preview;
import androidx.compose.ui.tooling.preview.Preview.Container;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import io.intercom.android.sdk.models.ConfigModules;
import io.intercom.android.sdk.models.CustomizationColorsModel;
import io.intercom.android.sdk.models.CustomizationModel;
import io.intercom.android.sdk.survey.block.BlockRenderData;
import io.intercom.android.sdk.survey.ui.components.SurveyComponentKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a9\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0010\b\u0002\u0010\b\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\tH\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\u0010\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\f\"\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000\"\u0018\u0010\u0011\u001a\n \u0013*\u0004\u0018\u00010\u00120\u0012X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0014¨\u0006\u0015",
   d2 = {
         "CreateTicketCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "blockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "enabled",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/BlockRenderData;ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "EnabledCreateTicketCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "DisabledCreateTicketCardPreview",
         "lightBrandNoDarkContrastAppConfig",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "CreateTicketCardActionContrastDarkFallbackPreview",
         "sampleBlock",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "kotlin.jvm.PlatformType",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "intercom-sdk-base_release"
   }
)
public final class CreateTicketCardKt {
   @NotNull
   private static final AppConfig lightBrandNoDarkContrastAppConfig = AppConfig.copy$default(
      SurveyComponentKt.getEmptyAppConfig(),
      null,
      0,
      0,
      0,
      false,
      false,
      false,
      0,
      0L,
      0L,
      0L,
      0L,
      false,
      false,
      null,
      null,
      false,
      false,
      null,
      null,
      null,
      null,
      null,
      false,
      false,
      false,
      false,
      false,
      new ConfigModules(
         new CustomizationModel(
            new CustomizationColorsModel("#FFEB3B", "#14161A", null, null, null, null, 60, null),
            new CustomizationColorsModel("#14161A", "#FFFFFF", null, null, null, null, 60, null),
            null,
            "",
            "",
            new CustomizationColorsModel("#FFEB3B", "#14161A", null, null, null, null, 60, null),
            0,
            null,
            null,
            "",
            null,
            0,
            1408,
            null
         ),
         null,
         2,
         null
      ),
      null,
      null,
      false,
      null,
      false,
      -268435457,
      3,
      null
   );
   private static final Block sampleBlock = new Block$Builder()
      .withType(BlockType.CREATETICKETCARD.getSerializedName())
      .withText("Admin has requested you to create a ticket")
      .withTitle("Create ticket")
      .withTicketTypeTitle("Bug")
      .withTicketType(new TicketType(1234L, "Bug", "\ud83c\udf9f", CollectionsKt.emptyList(), false))
      .build();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CreateTicketCard(
      @Nullable Modifier var0, @NotNull BlockRenderData var1, boolean var2, @Nullable Function0 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "blockRenderData");
      var4 = var4.startRestartGroup(-214450953);
      ComposerKt.sourceInformation(var4, "C(CreateTicketCard)P(2)50@2218L23,51@2249L1737,48@2126L1860:CreateTicketCard.kt#xh6pmz");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changed(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 8) != 0) {
            var3 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-214450953, var7, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketCard (CreateTicketCard.kt:47)");
         }

         IntercomCardKt.IntercomCard(
            SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null),
            IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var4, IntercomCardStyle.$stable << 15, 31),
            ComposableLambdaKt.rememberComposableLambda(1721044843, true, new CreateTicketCardKt$CreateTicketCard$1(var2, var3, var1), var4, 54),
            var4,
            384 | Style.$stable << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1587_0 = var0;
         final io.intercom.android.sdk.survey.block.BlockRenderData recoveredCapture_1587_1 = var1;
         final boolean recoveredCapture_1587_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_1587_3 = var3;
         final int recoveredCapture_1587_4 = var5;
         final int recoveredCapture_1587_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1587_0, recoveredArgument_1587_1) -> CreateTicketCardKt.CreateTicketCard$lambda$0(recoveredCapture_1587_0, recoveredCapture_1587_1, recoveredCapture_1587_2, recoveredCapture_1587_3, recoveredCapture_1587_4, recoveredCapture_1587_5, (androidx.compose.runtime.Composer)recoveredArgument_1587_0, ((java.lang.Integer)recoveredArgument_1587_1).intValue())));
      }
   }

   @Container({@Preview(name = "Enabled Light Mode", uiMode = 16), @Preview(name = "Enabled Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EnabledCreateTicketCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1535832576);
      ComposerKt.sourceInformation(var0, "C(EnabledCreateTicketCardPreview)100@4211L244:CreateTicketCard.kt#xh6pmz");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1535832576, var1, -1, "io.intercom.android.sdk.tickets.create.ui.EnabledCreateTicketCardPreview (CreateTicketCard.kt:99)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$CreateTicketCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1590_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1590_0, recoveredArgument_1590_1) -> CreateTicketCardKt.EnabledCreateTicketCardPreview$lambda$1(recoveredCapture_1590_0, (androidx.compose.runtime.Composer)recoveredArgument_1590_0, ((java.lang.Integer)recoveredArgument_1590_1).intValue())));
      }
   }

   @Container({@Preview(name = "Disabled Light Mode", uiMode = 16), @Preview(name = "Disabled Dark Mode", uiMode = 32)})
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void DisabledCreateTicketCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1443652823);
      ComposerKt.sourceInformation(var0, "C(DisabledCreateTicketCardPreview)116@4683L233:CreateTicketCard.kt#xh6pmz");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1443652823, var1, -1, "io.intercom.android.sdk.tickets.create.ui.DisabledCreateTicketCardPreview (CreateTicketCard.kt:115)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$CreateTicketCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1589_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1589_0, recoveredArgument_1589_1) -> CreateTicketCardKt.DisabledCreateTicketCardPreview$lambda$2(recoveredCapture_1589_0, (androidx.compose.runtime.Composer)recoveredArgument_1589_0, ((java.lang.Integer)recoveredArgument_1589_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CreateTicketCardActionContrastDarkFallbackPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1654710581);
      ComposerKt.sourceInformation(var0, "C(CreateTicketCardActionContrastDarkFallbackPreview)150@6082L1164:CreateTicketCard.kt#xh6pmz");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1654710581, var1, -1, "io.intercom.android.sdk.tickets.create.ui.CreateTicketCardActionContrastDarkFallbackPreview (CreateTicketCard.kt:149)"
            );
         }

         ConfigurableIntercomThemeKt.ConfigurableIntercomTheme(
            lightBrandNoDarkContrastAppConfig, ComposableSingletons$CreateTicketCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 48
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_1588_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1588_0, recoveredArgument_1588_1) -> CreateTicketCardKt.CreateTicketCardActionContrastDarkFallbackPreview$lambda$3(recoveredCapture_1588_0, (androidx.compose.runtime.Composer)recoveredArgument_1588_0, ((java.lang.Integer)recoveredArgument_1588_1).intValue())));
      }
   }

   private static final Unit CreateTicketCard$lambda$0(
      Modifier var0, BlockRenderData var1, boolean var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      CreateTicketCard(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit EnabledCreateTicketCardPreview$lambda$1(int var0, Composer var1, int var2) {
      EnabledCreateTicketCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit DisabledCreateTicketCardPreview$lambda$2(int var0, Composer var1, int var2) {
      DisabledCreateTicketCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CreateTicketCardActionContrastDarkFallbackPreview$lambda$3(int var0, Composer var1, int var2) {
      CreateTicketCardActionContrastDarkFallbackPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final Block access$getSampleBlock$p() {
      return sampleBlock;
   }
}
