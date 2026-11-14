package io.intercom.android.sdk.m5.conversation.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.m5.components.ErrorState$WithCTA;
import io.intercom.android.sdk.m5.components.ErrorState$WithoutCTA;
import io.intercom.android.sdk.m5.components.IntercomErrorScreenKt;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error;
import io.intercom.android.sdk.ui.IntercomPreviews;
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
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a-\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0001¢\u0006\u0002\u0010\b\u001a\r\u0010\t\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\n¨\u0006\u000b",
   d2 = {
         "ConversationErrorScreen",
         "",
         "state",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Error;",
         "onRetryClick",
         "Lkotlin/Function0;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Error;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "ConversationErrorScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class ConversationErrorScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationErrorScreen(
      @NotNull ConversationUiState$Error var0, @NotNull Function0 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var0, "state");
      Intrinsics.checkNotNullParameter(var1, "onRetryClick");
      var3 = var3.startRestartGroup(-1804211412);
      ComposerKt.sourceInformation(var3, "C(ConversationErrorScreen)P(2,1)17@664L166:ConversationErrorScreen.kt#fnibcj");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1804211412, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationErrorScreen (ConversationErrorScreen.kt:16)"
            );
         }

         IntercomErrorScreenKt.IntercomErrorScreen(
            var0.getShowCta() ? new ErrorState$WithCTA(0, 0, null, 0, var1, 15, null) : new ErrorState$WithoutCTA(0, 0, null, 7, null),
            var2,
            var3,
            112 & var6 >> 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error recoveredCapture_288_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_288_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_288_2 = var2;
         final int recoveredCapture_288_3 = var4;
         final int recoveredCapture_288_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_288_0, recoveredArgument_288_1) -> ConversationErrorScreenKt.ConversationErrorScreen$lambda$0(recoveredCapture_288_0, recoveredCapture_288_1, recoveredCapture_288_2, recoveredCapture_288_3, recoveredCapture_288_4, (androidx.compose.runtime.Composer)recoveredArgument_288_0, ((java.lang.Integer)recoveredArgument_288_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationErrorScreenPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1551706949);
      ComposerKt.sourceInformation(var0, "C(ConversationErrorScreenPreview)26@916L250:ConversationErrorScreen.kt#fnibcj");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1551706949, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.ConversationErrorScreenPreview (ConversationErrorScreen.kt:25)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationErrorScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_289_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_289_0, recoveredArgument_289_1) -> ConversationErrorScreenKt.ConversationErrorScreenPreview$lambda$1(recoveredCapture_289_0, (androidx.compose.runtime.Composer)recoveredArgument_289_0, ((java.lang.Integer)recoveredArgument_289_1).intValue())));
      }
   }

   private static final Unit ConversationErrorScreen$lambda$0(
      ConversationUiState$Error var0, Function0 var1, Modifier var2, int var3, int var4, Composer var5, int var6
   ) {
      ConversationErrorScreen(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationErrorScreenPreview$lambda$1(int var0, Composer var1, int var2) {
      ConversationErrorScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
