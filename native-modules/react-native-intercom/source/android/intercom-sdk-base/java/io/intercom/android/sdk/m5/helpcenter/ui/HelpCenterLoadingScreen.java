package io.intercom.android.sdk.m5.helpcenter.ui;

import android.content.Context;
import android.util.AttributeSet;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.platform.AbstractComposeView;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\r\u0010\n\u001a\u00020\u000bH\u0017¢\u0006\u0002\u0010\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/m5/helpcenter/ui/HelpCenterLoadingScreen;",
         "Landroidx/compose/ui/platform/AbstractComposeView;",
         "context",
         "Landroid/content/Context;",
         "attrs",
         "Landroid/util/AttributeSet;",
         "defStyle",
         "",
         "<init>",
         "(Landroid/content/Context;Landroid/util/AttributeSet;I)V",
         "Content",
         "",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HelpCenterLoadingScreen extends AbstractComposeView {
   public static int $stable;

   static {}

   @JvmOverloads
   public HelpCenterLoadingScreen(@NotNull Context var1, @Nullable AttributeSet var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "context");
      super(var1, var2, var3);
   }

   // $VF: synthetic method
   public HelpCenterLoadingScreen(Context var1, AttributeSet var2, int var3, int var4, DefaultConstructorMarker var5) {
      this(var1, (((var4 & 2) != 0) ? (AttributeSet) (null) : var2), (((var4 & 4) != 0) ? (int) (0) : var3));
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   @Override
   public void Content(@Nullable Composer var1, int var2) {
      var1 = var1.startRestartGroup(1926895347);
      ComposerKt.sourceInformation(var1, "C(Content)45@1500L63:HelpCenterLoadingScreen.kt#ln9mxm");
      if ((var2 & 1) == 0 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1926895347, var2, -1, "io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterLoadingScreen.Content (HelpCenterLoadingScreen.kt:44)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HelpCenterLoadingScreenKt.INSTANCE.recovered$eac478aa65045e5d(), var1, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var1.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.helpcenter.ui.HelpCenterLoadingScreen recoveredCapture_827_0 = this;
         final int recoveredCapture_827_1 = var2;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_827_0, recoveredArgument_827_1) -> HelpCenterLoadingScreen.Content$lambda$0(recoveredCapture_827_0, recoveredCapture_827_1, (androidx.compose.runtime.Composer)recoveredArgument_827_0, ((java.lang.Integer)recoveredArgument_827_1).intValue())));
      }
   }

   @JvmOverloads
   public HelpCenterLoadingScreen(@NotNull Context var1, @Nullable AttributeSet var2) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, var2, 0, 4, null);
   }

   @JvmOverloads
   public HelpCenterLoadingScreen(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      this(var1, null, 0, 6, null);
   }

   private static final Unit Content$lambda$0(HelpCenterLoadingScreen var0, int var1, Composer var2, int var3) {
      var0.Content(var2, RecomposeScopeImplKt.updateChangedFlags(var1 | 1));
      return Unit.INSTANCE;
   }
}
