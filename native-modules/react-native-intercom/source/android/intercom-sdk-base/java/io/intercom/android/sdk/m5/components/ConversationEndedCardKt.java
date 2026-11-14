package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.RectangleShapeKt;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a1\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\n\u001a\r\u0010\f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\n\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\n¨\u0006\u000e",
   d2 = {
         "ConversationEndedCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "onButtonClick",
         "Lkotlin/Function0;",
         "conversationEndedUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded;",
         "(Landroidx/compose/ui/Modifier;Lkotlin/jvm/functions/Function0;Lio/intercom/android/sdk/m5/conversation/states/ComposerState$ConversationEnded;Landroidx/compose/runtime/Composer;II)V",
         "ConversationEndedCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "ConversationEndedCardWithButtonPreview",
         "ConversationEndedCardWithButtonBotPreview",
         "ConversationMergedCardPreview",
         "intercom-sdk-base_release"
   }
)
public final class ConversationEndedCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationEndedCard(
      @Nullable Modifier var0, @Nullable Function0 var1, @NotNull ComposerState$ConversationEnded var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var2, "conversationEndedUiState");
      var3 = var3.startRestartGroup(-517544614);
      ComposerKt.sourceInformation(var3, "C(ConversationEndedCard)P(1,2)33@1349L6,32@1294L82,35@1384L893,29@1182L1095:ConversationEndedCard.kt#arqjag");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= ((var4 & 512) == 0 ? var3.changed(var2) : var3.changedInstance(var2)) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var5 & 2) != 0) {
            var1 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-517544614, var6, -1, "io.intercom.android.sdk.m5.components.ConversationEndedCard (ConversationEndedCard.kt:28)");
         }

         CardKt.Card(
            SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null),
            RectangleShapeKt.getRectangleShape(),
            CardDefaults.INSTANCE
               .recovered$da184985090a28d4(
                  IntercomTheme.INSTANCE.getColors(var3, IntercomTheme.$stable).getBase().recovered$00296b21a1edce05(),
                  0L,
                  0L,
                  0L,
                  var3,
                  CardDefaults.$stable << 12,
                  14
               ),
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-676087732, true, new ConversationEndedCardKt$ConversationEndedCard$1(var2, var1), var3, 54),
            var3,
            196656,
            24
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_176_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_176_1 = var1;
         final io.intercom.android.sdk.m5.conversation.states.ComposerState$ConversationEnded recoveredCapture_176_2 = var2;
         final int recoveredCapture_176_3 = var4;
         final int recoveredCapture_176_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_176_0, recoveredArgument_176_1) -> ConversationEndedCardKt.ConversationEndedCard$lambda$0(recoveredCapture_176_0, recoveredCapture_176_1, recoveredCapture_176_2, recoveredCapture_176_3, recoveredCapture_176_4, (androidx.compose.runtime.Composer)recoveredArgument_176_0, ((java.lang.Integer)recoveredArgument_176_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ConversationEndedCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-172245026);
      ComposerKt.sourceInformation(var0, "C(ConversationEndedCardPreview)63@2360L243:ConversationEndedCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-172245026, var1, -1, "io.intercom.android.sdk.m5.components.ConversationEndedCardPreview (ConversationEndedCard.kt:62)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationEndedCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_177_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_177_0, recoveredArgument_177_1) -> ConversationEndedCardKt.ConversationEndedCardPreview$lambda$1(recoveredCapture_177_0, (androidx.compose.runtime.Composer)recoveredArgument_177_0, ((java.lang.Integer)recoveredArgument_177_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ConversationEndedCardWithButtonPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1576691098);
      ComposerKt.sourceInformation(var0, "C(ConversationEndedCardWithButtonPreview)75@2696L519:ConversationEndedCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1576691098, var1, -1, "io.intercom.android.sdk.m5.components.ConversationEndedCardWithButtonPreview (ConversationEndedCard.kt:74)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationEndedCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_179_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_179_0, recoveredArgument_179_1) -> ConversationEndedCardKt.ConversationEndedCardWithButtonPreview$lambda$2(recoveredCapture_179_0, (androidx.compose.runtime.Composer)recoveredArgument_179_0, ((java.lang.Integer)recoveredArgument_179_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ConversationEndedCardWithButtonBotPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-317527071);
      ComposerKt.sourceInformation(var0, "C(ConversationEndedCardWithButtonBotPreview)92@3311L528:ConversationEndedCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -317527071, var1, -1, "io.intercom.android.sdk.m5.components.ConversationEndedCardWithButtonBotPreview (ConversationEndedCard.kt:91)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationEndedCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_178_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_178_0, recoveredArgument_178_1) -> ConversationEndedCardKt.ConversationEndedCardWithButtonBotPreview$lambda$3(recoveredCapture_178_0, (androidx.compose.runtime.Composer)recoveredArgument_178_0, ((java.lang.Integer)recoveredArgument_178_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ConversationMergedCardPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-233275222);
      ComposerKt.sourceInformation(var0, "C(ConversationMergedCardPreview)109@3923L591:ConversationEndedCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -233275222, var1, -1, "io.intercom.android.sdk.m5.components.ConversationMergedCardPreview (ConversationEndedCard.kt:108)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationEndedCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_180_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_180_0, recoveredArgument_180_1) -> ConversationEndedCardKt.ConversationMergedCardPreview$lambda$4(recoveredCapture_180_0, (androidx.compose.runtime.Composer)recoveredArgument_180_0, ((java.lang.Integer)recoveredArgument_180_1).intValue())));
      }
   }

   private static final Unit ConversationEndedCard$lambda$0(
      Modifier var0, Function0 var1, ComposerState$ConversationEnded var2, int var3, int var4, Composer var5, int var6
   ) {
      ConversationEndedCard(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationEndedCardPreview$lambda$1(int var0, Composer var1, int var2) {
      ConversationEndedCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationEndedCardWithButtonPreview$lambda$2(int var0, Composer var1, int var2) {
      ConversationEndedCardWithButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationEndedCardWithButtonBotPreview$lambda$3(int var0, Composer var1, int var2) {
      ConversationEndedCardWithButtonBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationMergedCardPreview$lambda$4(int var0, Composer var1, int var2) {
      ConversationMergedCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
