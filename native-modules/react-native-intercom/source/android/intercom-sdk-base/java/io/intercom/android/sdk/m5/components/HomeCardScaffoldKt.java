package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a2\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0011\u0010\u0006\u001a\r\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\bH\u0001¢\u0006\u0002\u0010\t\u001a\r\u0010\n\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u000b¨\u0006\f",
   d2 = {
         "HomeCardScaffold",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "cardTitle",
         "",
         "content",
         "Lkotlin/Function0;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;II)V",
         "HomeCardScaffoldPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class HomeCardScaffoldKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void HomeCardScaffold(
      @Nullable Modifier var0, @NotNull String var1, @NotNull Function2 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "content");
      var3 = var3.startRestartGroup(-1721620037);
      ComposerKt.sourceInformation(var3, "C(HomeCardScaffold)P(2)21@712L397,19@663L446:HomeCardScaffold.kt#arqjag");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1721620037, var6, -1, "io.intercom.android.sdk.m5.components.HomeCardScaffold (HomeCardScaffold.kt:18)");
         }

         IntercomCardKt.IntercomCard(
            var0,
            null,
            ComposableLambdaKt.rememberComposableLambda(1218435015, true, new HomeCardScaffoldKt$HomeCardScaffold$1(var1, var2), var3, 54),
            var3,
            384 | 14 & var6,
            2
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var3.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_208_0 = var0;
         final java.lang.String recoveredCapture_208_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_208_2 = var2;
         final int recoveredCapture_208_3 = var4;
         final int recoveredCapture_208_4 = var5;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_208_0, recoveredArgument_208_1) -> HomeCardScaffoldKt.HomeCardScaffold$lambda$0(recoveredCapture_208_0, recoveredCapture_208_1, recoveredCapture_208_2, recoveredCapture_208_3, recoveredCapture_208_4, (androidx.compose.runtime.Composer)recoveredArgument_208_0, ((java.lang.Integer)recoveredArgument_208_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void HomeCardScaffoldPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1294989986);
      ComposerKt.sourceInformation(var0, "C(HomeCardScaffoldPreview)40@1187L416:HomeCardScaffold.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1294989986, var1, -1, "io.intercom.android.sdk.m5.components.HomeCardScaffoldPreview (HomeCardScaffold.kt:39)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HomeCardScaffoldKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_209_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_209_0, recoveredArgument_209_1) -> HomeCardScaffoldKt.HomeCardScaffoldPreview$lambda$1(recoveredCapture_209_0, (androidx.compose.runtime.Composer)recoveredArgument_209_0, ((java.lang.Integer)recoveredArgument_209_1).intValue())));
      }
   }

   private static final Unit HomeCardScaffold$lambda$0(Modifier var0, String var1, Function2 var2, int var3, int var4, Composer var5, int var6) {
      HomeCardScaffold(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit HomeCardScaffoldPreview$lambda$1(int var0, Composer var1, int var2) {
      HomeCardScaffoldPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
