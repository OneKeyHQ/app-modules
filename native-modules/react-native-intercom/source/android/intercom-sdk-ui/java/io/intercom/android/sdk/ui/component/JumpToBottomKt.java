package io.intercom.android.sdk.ui.component;

import androidx.compose.foundation.DarkThemeKt;
import androidx.compose.material3.BadgeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomColorsKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.ui.theme.ThemeMode;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000.\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\u001a[\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\n2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00060\u0011H\u0007¢\u0006\u0004\b\u0012\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\nH\u0003¢\u0006\u0002\u0010\u0015\u001a\r\u0010\u0016\u001a\u00020\u0006H\u0003¢\u0006\u0002\u0010\u0017\u001a5\u0010\u0018\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\nH\u0003¢\u0006\u0004\b\u0019\u0010\u001a\"\u0016\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\n\n\u0002\u0010\u0004\u001a\u0004\b\u0002\u0010\u0003¨\u0006\u001b",
   d2 = {
         "JumpBottomPadding",
         "Landroidx/compose/ui/unit/Dp;",
         "getJumpBottomPadding",
         "()F",
         "F",
         "JumpToBottom",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "badgeText",
         "",
         "badgeColor",
         "badgeContentColor",
         "onClick",
         "Lkotlin/Function0;",
         "JumpToBottom-kNRdK3w",
         "(Landroidx/compose/ui/Modifier;JJLjava/lang/String;JJLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "getChevronColor",
         "(Landroidx/compose/runtime/Composer;I)J",
         "JumpToBottomPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UnreadBadge",
         "UnreadBadge-eaDK9VM",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;JJLandroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nJumpToBottom.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,137:1\n118#2:138\n*S KotlinDebug\n*F\n+ 1 JumpToBottom.kt\nio/intercom/android/sdk/ui/component/JumpToBottomKt\n*L\n34#1:138\n*E\n"
)
public final class JumpToBottomKt {
   private static final float JumpBottomPadding;

   public static final float getJumpBottomPadding() {
      return JumpBottomPadding;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$611c8eb088dfc7a6(
      @Nullable Modifier var0,
      long var1,
      long var3,
      @Nullable String var5,
      long var6,
      long var8,
      @NotNull Function0 var10,
      @Nullable Composer var11,
      int var12,
      int var13
   ) {
      Intrinsics.checkNotNullParameter(var10, "onClick");
      var11 = var11.startRestartGroup(1700007899);
      ComposerKt.sourceInformation(
         var11,
         "C(JumpToBottom)P(5,0:c#ui.graphics.Color,4:c#ui.graphics.Color,3,1:c#ui.graphics.Color,2:c#ui.graphics.Color)38@1626L6,39@1670L17,41@1758L6,42@1818L6,47@1924L340,57@2272L1449,45@1868L1853:JumpToBottom.kt#yr602f"
      );
      int var14 = var12;
      if ((var13 & 1) != 0) {
         var14 |= 6;
      } else if ((var12 & 6) == 0) {
         var14 |= var11.changed(var0) ? 4 : 2;
      }

      if ((var12 & 48) == 0) {
         var14 |= (var13 & 2) == 0 && var11.changed(var1) ? 32 : 16;
      }

      if ((var12 & 384) == 0) {
         var14 |= (var13 & 4) == 0 && var11.changed(var3) ? 256 : 128;
      }

      if ((var13 & 8) != 0) {
         var14 |= 3072;
      } else if ((var12 & 3072) == 0) {
         var14 |= var11.changed(var5) ? 2048 : 1024;
      }

      if ((var12 & 24576) == 0) {
         var14 |= (var13 & 16) == 0 && var11.changed(var6) ? 16384 : 8192;
      }

      if ((var12 & 196608) == 0) {
         var14 |= (var13 & 32) == 0 && var11.changed(var8) ? 131072 : 65536;
      }

      if ((var13 & 64) != 0) {
         var14 |= 1572864;
      } else if ((var12 & 1572864) == 0) {
         var14 |= var11.changedInstance(var10) ? 1048576 : 524288;
      }

      if ((var14 & 599187) == 599186 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         var11.startDefaults();
         if ((var12 & 1) != 0 && !var11.getDefaultsInvalid()) {
            var11.skipToGroupEnd();
            if ((var13 & 2) != 0) {
               var14 &= -113;
            }

            if ((var13 & 4) != 0) {
               var14 &= -897;
            }

            if ((var13 & 16) != 0) {
               var14 &= -57345;
            }

            if ((var13 & 32) != 0) {
               var14 &= -458753;
            }
         } else {
            if ((var13 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var13 & 2) != 0) {
               var1 = IntercomTheme.INSTANCE.getColors(var11, 6).getBase().recovered$00296b21a1edce05();
               var14 &= -113;
            }

            if ((var13 & 4) != 0) {
               var3 = getChevronColor(var11, 0);
               var14 &= -897;
            }

            if ((var13 & 8) != 0) {
               var5 = null;
            }

            if ((var13 & 16) != 0) {
               var6 = IntercomTheme.INSTANCE.getColors(var11, 6).recovered$202acdf3dbb5c020();
               var14 &= -57345;
            }

            if ((var13 & 32) != 0) {
               var8 = IntercomTheme.INSTANCE.getColors(var11, 6).recovered$8a5d9ad2993bccce();
               var14 &= -458753;
            }
         }

         var11.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1700007899, var14, -1, "io.intercom.android.sdk.ui.component.JumpToBottom (JumpToBottom.kt:44)");
         }

         BadgeKt.BadgedBox(
            ComposableLambdaKt.rememberComposableLambda(-1557616541, true, new JumpToBottomKt$JumpToBottom$1(var5, var6, var8), var11, 54),
            var0,
            ComposableLambdaKt.rememberComposableLambda(1814183329, true, new JumpToBottomKt$JumpToBottom$2(var10, var1, var3), var11, 54),
            var11,
            390 | 112 & var14 << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var11.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_25_0 = var0;
         final long recoveredCapture_25_1 = var1;
         final long recoveredCapture_25_2 = var3;
         final java.lang.String recoveredCapture_25_3 = var5;
         final long recoveredCapture_25_4 = var6;
         final long recoveredCapture_25_5 = var8;
         final kotlin.jvm.functions.Function0 recoveredCapture_25_6 = var10;
         final int recoveredCapture_25_7 = var12;
         final int recoveredCapture_25_8 = var13;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_25_0, recoveredArgument_25_1) -> JumpToBottomKt.JumpToBottom_kNRdK3w$lambda$0(recoveredCapture_25_0, recoveredCapture_25_1, recoveredCapture_25_2, recoveredCapture_25_3, recoveredCapture_25_4, recoveredCapture_25_5, recoveredCapture_25_6, recoveredCapture_25_7, recoveredCapture_25_8, (androidx.compose.runtime.Composer)recoveredArgument_25_0, ((java.lang.Integer)recoveredArgument_25_1).intValue())));
      }
   }

   @Composable
   private static final long getChevronColor(Composer var0, int var1) {
      var0.startReplaceGroup(179733836);
      ComposerKt.sourceInformation(var0, "C(getChevronColor):JumpToBottom.kt#yr602f");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(179733836, var1, -1, "io.intercom.android.sdk.ui.component.getChevronColor (JumpToBottom.kt:99)");
      }

      long var10000;
      switch (JumpToBottomKt$WhenMappings.$EnumSwitchMapping$0[((ThemeMode)IntercomColorsKt.getCurrentThemeMode().getValue()).ordinal()]) {
         case 1:
            var0.startReplaceGroup(-131337460);
            var0.endReplaceGroup();
            var10000 = Color.Companion.recovered$95989221dad41c4d();
            break;
         case 2:
            var0.startReplaceGroup(-131335846);
            ComposerKt.sourceInformation(var0, "101@3879L6");
            long var6 = IntercomTheme.INSTANCE.getColors(var0, 6).recovered$d5d3a0ddb8f4248d();
            var0.endReplaceGroup();
            var10000 = var6;
            break;
         case 3:
            var0.startReplaceGroup(223601735);
            ComposerKt.sourceInformation(var0, "102@3934L21,102@3988L6");
            long var4 = DarkThemeKt.isSystemInDarkTheme(var0, 0)
               ? Color.Companion.recovered$95989221dad41c4d()
               : IntercomTheme.INSTANCE.getColors(var0, 6).recovered$d5d3a0ddb8f4248d();
            var0.endReplaceGroup();
            var10000 = var4;
            break;
         default:
            var0.startReplaceGroup(-131339145);
            var0.endReplaceGroup();
            throw new NoWhenBranchMatchedException();
      }

      long var2 = var10000;
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var0.endReplaceGroup();
      return var2;
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void JumpToBottomPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1260816059);
      ComposerKt.sourceInformation(var0, "C(JumpToBottomPreview)108@4088L56:JumpToBottom.kt#yr602f");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1260816059, var1, -1, "io.intercom.android.sdk.ui.component.JumpToBottomPreview (JumpToBottom.kt:107)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$JumpToBottomKt.INSTANCE.recovered$a54a3cf5c9caf09f(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_23_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_23_0, recoveredArgument_23_1) -> JumpToBottomKt.JumpToBottomPreview$lambda$1(recoveredCapture_23_0, (androidx.compose.runtime.Composer)recoveredArgument_23_0, ((java.lang.Integer)recoveredArgument_23_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$38d77de8d6bb195b(Modifier var0, String var1, long var2, long var4, Composer var6, int var7, int var8) {
      var6 = var6.startRestartGroup(885989429);
      ComposerKt.sourceInformation(
         var6, "C(UnreadBadge)P(3,1,0:c#ui.graphics.Color,2:c#ui.graphics.Color)117@4281L6,118@4336L6,123@4440L331,120@4361L410:JumpToBottom.kt#yr602f"
      );
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changed(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var7 & 384) == 0) {
         var9 |= (var8 & 4) == 0 && var6.changed(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var9 |= (var8 & 8) == 0 && var6.changed(var4) ? 2048 : 1024;
      }

      if ((var9 & 1171) == 1170 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         var6.startDefaults();
         if ((var7 & 1) != 0 && !var6.getDefaultsInvalid()) {
            var6.skipToGroupEnd();
            if ((var8 & 4) != 0) {
               var9 &= -897;
            }

            if ((var8 & 8) != 0) {
               var9 &= -7169;
            }
         } else {
            if ((var8 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var8 & 4) != 0) {
               var2 = IntercomTheme.INSTANCE.getColors(var6, 6).recovered$202acdf3dbb5c020();
               var9 &= -897;
            }

            if ((var8 & 8) != 0) {
               var4 = IntercomTheme.INSTANCE.getColors(var6, 6).recovered$8a5d9ad2993bccce();
               var9 &= -7169;
            }
         }

         var6.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(885989429, var9, -1, "io.intercom.android.sdk.ui.component.UnreadBadge (JumpToBottom.kt:119)");
         }

         BadgeKt.recovered$d9697d11cb3615b2(
            var0,
            var2,
            0L,
            ComposableLambdaKt.rememberComposableLambda(-480925784, true, new JumpToBottomKt$UnreadBadge$1(var4, var1), var6, 54),
            var6,
            3072 | 14 & var9 | 112 & var9 >> 3,
            4
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var6.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_24_0 = var0;
         final java.lang.String recoveredCapture_24_1 = var1;
         final long recoveredCapture_24_2 = var2;
         final long recoveredCapture_24_3 = var4;
         final int recoveredCapture_24_4 = var7;
         final int recoveredCapture_24_5 = var8;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_24_0, recoveredArgument_24_1) -> JumpToBottomKt.UnreadBadge_eaDK9VM$lambda$2(recoveredCapture_24_0, recoveredCapture_24_1, recoveredCapture_24_2, recoveredCapture_24_3, recoveredCapture_24_4, recoveredCapture_24_5, (androidx.compose.runtime.Composer)recoveredArgument_24_0, ((java.lang.Integer)recoveredArgument_24_1).intValue())));
      }
   }

   private static final Unit JumpToBottom_kNRdK3w$lambda$0(
      Modifier var0, long var1, long var3, String var5, long var6, long var8, Function0 var10, int var11, int var12, Composer var13, int var14
   ) {
      recovered$611c8eb088dfc7a6(var0, var1, var3, var5, var6, var8, var10, var13, RecomposeScopeImplKt.updateChangedFlags(var11 | 1), var12);
      return Unit.INSTANCE;
   }

   private static final Unit JumpToBottomPreview$lambda$1(int var0, Composer var1, int var2) {
      JumpToBottomPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UnreadBadge_eaDK9VM$lambda$2(Modifier var0, String var1, long var2, long var4, int var6, int var7, Composer var8, int var9) {
      recovered$38d77de8d6bb195b(var0, var1, var2, var4, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void recovered$00a1b485d97ecfc4(Modifier var0, String var1, long var2, long var4, Composer var6, int var7, int var8) {
      recovered$38d77de8d6bb195b(var0, var1, var2, var4, var6, var7, var8);
   }

   static {
      int var0 = 12;
      int var1 = 0;
      JumpBottomPadding = Dp.recovered$95b3d103663bf060(var0);
   }
}
