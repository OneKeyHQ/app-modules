package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.EmptyStateKt;
import io.intercom.android.sdk.models.EmptyState;
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
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a5\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0001¢\u0006\u0002\u0010\n\u001a\r\u0010\u000b\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\r\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\u000e\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\f¨\u0006\u0010",
   d2 = {
         "InboxEmptyScreen",
         "",
         "emptyState",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "showActionButton",
         "",
         "onActionButtonClick",
         "Lkotlin/Function0;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Lio/intercom/android/sdk/models/EmptyState;ZLkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "EmptyScreenMessagePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "EmptyScreenHelpPreview",
         "EmptyScreenWithoutActionPreview",
         "EmptyScreenBotPreview",
         "intercom-sdk-base_release"
   }
)
public final class InboxEmptyScreenKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void InboxEmptyScreen(
      @NotNull EmptyState var0, boolean var1, @NotNull Function0 var2, @Nullable Modifier var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "emptyState");
      Intrinsics.checkNotNullParameter(var2, "onActionButtonClick");
      var4 = var4.startRestartGroup(-727293785);
      ComposerKt.sourceInformation(var4, "C(InboxEmptyScreen)P(!1,3,2)26@933L877,21@761L1049:InboxEmptyScreen.kt#po70xe");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 8) != 0) {
            var3 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-727293785, var7, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxEmptyScreen (InboxEmptyScreen.kt:20)");
         }

         String var8 = var0.getTitle();
         String var9 = var0.getText();
         int var10 = drawable.intercom_messages_icon;
         EmptyStateKt.EmptyState(
            var8,
            var3,
            var9,
            var10,
            ComposableLambdaKt.rememberComposableLambda(2045450098, true, new InboxEmptyScreenKt$InboxEmptyScreen$1(var1, var0, var2), var4, 54),
            var4,
            24576 | 112 & var7 >> 6,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.models.EmptyState recoveredCapture_977_0 = var0;
         final boolean recoveredCapture_977_1 = var1;
         final kotlin.jvm.functions.Function0 recoveredCapture_977_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_977_3 = var3;
         final int recoveredCapture_977_4 = var5;
         final int recoveredCapture_977_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_977_0, recoveredArgument_977_1) -> InboxEmptyScreenKt.InboxEmptyScreen$lambda$0(recoveredCapture_977_0, recoveredCapture_977_1, recoveredCapture_977_2, recoveredCapture_977_3, recoveredCapture_977_4, recoveredCapture_977_5, (androidx.compose.runtime.Composer)recoveredArgument_977_0, ((java.lang.Integer)recoveredArgument_977_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyScreenMessagePreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1317218099);
      ComposerKt.sourceInformation(var0, "C(EmptyScreenMessagePreview)51@1890L607:InboxEmptyScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1317218099, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.EmptyScreenMessagePreview (InboxEmptyScreen.kt:50)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxEmptyScreenKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_975_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_975_0, recoveredArgument_975_1) -> InboxEmptyScreenKt.EmptyScreenMessagePreview$lambda$1(recoveredCapture_975_0, (androidx.compose.runtime.Composer)recoveredArgument_975_0, ((java.lang.Integer)recoveredArgument_975_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyScreenHelpPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1522245405);
      ComposerKt.sourceInformation(var0, "C(EmptyScreenHelpPreview)73@2574L561:InboxEmptyScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1522245405, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.EmptyScreenHelpPreview (InboxEmptyScreen.kt:72)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxEmptyScreenKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_974_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_974_0, recoveredArgument_974_1) -> InboxEmptyScreenKt.EmptyScreenHelpPreview$lambda$2(recoveredCapture_974_0, (androidx.compose.runtime.Composer)recoveredArgument_974_0, ((java.lang.Integer)recoveredArgument_974_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyScreenWithoutActionPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-132232118);
      ComposerKt.sourceInformation(var0, "C(EmptyScreenWithoutActionPreview)94@3221L558:InboxEmptyScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-132232118, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.EmptyScreenWithoutActionPreview (InboxEmptyScreen.kt:93)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxEmptyScreenKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_976_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_976_0, recoveredArgument_976_1) -> InboxEmptyScreenKt.EmptyScreenWithoutActionPreview$lambda$3(recoveredCapture_976_0, (androidx.compose.runtime.Composer)recoveredArgument_976_0, ((java.lang.Integer)recoveredArgument_976_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void EmptyScreenBotPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(862447475);
      ComposerKt.sourceInformation(var0, "C(EmptyScreenBotPreview)115@3855L599:InboxEmptyScreen.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(862447475, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.EmptyScreenBotPreview (InboxEmptyScreen.kt:114)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$InboxEmptyScreenKt.INSTANCE.recovered$a427d7fccaa25a17(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_973_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_973_0, recoveredArgument_973_1) -> InboxEmptyScreenKt.EmptyScreenBotPreview$lambda$4(recoveredCapture_973_0, (androidx.compose.runtime.Composer)recoveredArgument_973_0, ((java.lang.Integer)recoveredArgument_973_1).intValue())));
      }
   }

   private static final Unit InboxEmptyScreen$lambda$0(
      EmptyState var0, boolean var1, Function0 var2, Modifier var3, int var4, int var5, Composer var6, int var7
   ) {
      InboxEmptyScreen(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit EmptyScreenMessagePreview$lambda$1(int var0, Composer var1, int var2) {
      EmptyScreenMessagePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmptyScreenHelpPreview$lambda$2(int var0, Composer var1, int var2) {
      EmptyScreenHelpPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmptyScreenWithoutActionPreview$lambda$3(int var0, Composer var1, int var2) {
      EmptyScreenWithoutActionPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit EmptyScreenBotPreview$lambda$4(int var0, Composer var1, int var2) {
      EmptyScreenBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
