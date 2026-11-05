package io.intercom.android.sdk.m5.conversation.ui.components;

import androidx.annotation.DrawableRes;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.material3.BadgeKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Vertical;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.ConversationItemKt;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import kotlin.Metadata;
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
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\u001aw\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00052\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u00052\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0001¢\u0006\u0004\b\u0012\u0010\u0013\u001a[\u0010\u0014\u001a\u00020\u00012\b\b\u0002\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0003¢\u0006\u0004\b\u0015\u0010\u0016\u001aY\u0010\u0017\u001a\u00020\u00012\b\b\u0002\u0010\b\u001a\u00020\t2\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0006\u001a\u00020\u00072\b\u0010\n\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u00052\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0006\u0010\u0004\u001a\u00020\u0005H\u0003¢\u0006\u0002\u0010\u0018\u001a\u001f\u0010\u0019\u001a\u00020\u00012\b\b\u0002\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0003H\u0003¢\u0006\u0002\u0010\u001a\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001c\u001a\r\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001c¨\u0006\u001e",
   d2 = {
         "HeaderMenuItemRow",
         "",
         "label",
         "",
         "enabled",
         "",
         "icon",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "badgeText",
         "showBadge",
         "showUnreadDot",
         "isExpanded",
         "onClick",
         "Lkotlin/Function0;",
         "tint",
         "Landroidx/compose/ui/graphics/Color;",
         "HeaderMenuItemRow-6RhP_wg",
         "(Ljava/lang/String;ZILandroidx/compose/ui/Modifier;Ljava/lang/String;ZZZLkotlin/jvm/functions/Function0;JLandroidx/compose/runtime/Composer;II)V",
         "CollapsedHeaderMenuItemRow",
         "CollapsedHeaderMenuItemRow-88mDfTA",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;ILjava/lang/String;ZZLkotlin/jvm/functions/Function0;JLandroidx/compose/runtime/Composer;II)V",
         "ExpandedHeaderMenuItemRow",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;ILjava/lang/String;ZZLkotlin/jvm/functions/Function0;ZLandroidx/compose/runtime/Composer;II)V",
         "UnreadBadge",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "CollapsedHeaderMenuItemPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "CollapsedHeaderMenuItemDotPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nHeaderMenuItemRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 4 Row.kt\nandroidx/compose/foundation/layout/RowKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n*L\n1#1,208:1\n1047#2,6:209\n1047#2,6:215\n118#3:221\n118#3:250\n118#3:251\n99#4,6:222\n106#4:255\n81#5,6:228\n88#5,6:243\n96#5:254\n402#6,9:234\n411#6:249\n412#6,2:252\n*S KotlinDebug\n*F\n+ 1 HeaderMenuItemRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/HeaderMenuItemRowKt\n*L\n42#1:209,6\n125#1:215,6\n126#1:221\n134#1:250\n139#1:251\n122#1:222,6\n122#1:255\n122#1:228,6\n122#1:243,6\n122#1:254\n122#1:234,9\n122#1:249\n122#1:252,2\n*E\n"
)
public final class HeaderMenuItemRowKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$04990d299bee3c60(
      @NotNull String var0,
      boolean var1,
      @DrawableRes int var2,
      @Nullable Modifier var3,
      @Nullable String var4,
      boolean var5,
      boolean var6,
      boolean var7,
      @Nullable Function0 var8,
      long var9,
      @Nullable Composer var11,
      int var12,
      int var13
   ) {
      Intrinsics.checkNotNullParameter(var0, "label");
      var11 = var11.startRestartGroup(-1015144426);
      ComposerKt.sourceInformation(var11, "C(HeaderMenuItemRow)P(4,1,2,5!1,7,8!,9:c#ui.graphics.Color)41@1627L2,42@1663L6:HeaderMenuItemRow.kt#o19g89");
      int var14 = var12;
      if ((var13 & 1) != 0) {
         var14 |= 6;
      } else if ((var12 & 6) == 0) {
         var14 |= var11.changed(var0) ? 4 : 2;
      }

      if ((var13 & 2) != 0) {
         var14 |= 48;
      } else if ((var12 & 48) == 0) {
         var14 |= var11.changed(var1) ? 32 : 16;
      }

      if ((var13 & 4) != 0) {
         var14 |= 384;
      } else if ((var12 & 384) == 0) {
         var14 |= var11.changed(var2) ? 256 : 128;
      }

      if ((var13 & 8) != 0) {
         var14 |= 3072;
      } else if ((var12 & 3072) == 0) {
         var14 |= var11.changed(var3) ? 2048 : 1024;
      }

      if ((var13 & 16) != 0) {
         var14 |= 24576;
      } else if ((var12 & 24576) == 0) {
         var14 |= var11.changed(var4) ? 16384 : 8192;
      }

      if ((var13 & 32) != 0) {
         var14 |= 196608;
      } else if ((var12 & 196608) == 0) {
         var14 |= var11.changed(var5) ? 131072 : 65536;
      }

      if ((var13 & 64) != 0) {
         var14 |= 1572864;
      } else if ((var12 & 1572864) == 0) {
         var14 |= var11.changed(var6) ? 1048576 : 524288;
      }

      if ((var13 & 128) != 0) {
         var14 |= 12582912;
      } else if ((var12 & 12582912) == 0) {
         var14 |= var11.changed(var7) ? 8388608 : 4194304;
      }

      if ((var13 & 256) != 0) {
         var14 |= 100663296;
      } else if ((var12 & 100663296) == 0) {
         var14 |= var11.changedInstance(var8) ? 67108864 : 33554432;
      }

      if ((var12 & 805306368) == 0) {
         var14 |= (var13 & 512) == 0 && var11.changed(var9) ? 536870912 : 268435456;
      }

      if ((var14 & 306783379) == 306783378 && var11.getSkipping()) {
         var11.skipToGroupEnd();
      } else {
         var11.startDefaults();
         if ((var12 & 1) != 0 && !var11.getDefaultsInvalid()) {
            var11.skipToGroupEnd();
            if ((var13 & 512) != 0) {
               var14 &= -1879048193;
            }
         } else {
            if ((var13 & 8) != 0) {
               var3 = Modifier.Companion;
            }

            if ((var13 & 16) != 0) {
               var4 = null;
            }

            if ((var13 & 32) != 0) {
               var5 = true;
            }

            if ((var13 & 64) != 0) {
               var6 = false;
            }

            if ((var13 & 128) != 0) {
               var7 = false;
            }

            if ((var13 & 256) != 0) {
               var11.startReplaceGroup(1618517830);
               ComposerKt.sourceInformation(var11, "CC(remember):HeaderMenuItemRow.kt#9igjgp");
               Composer var16 = var11;
               boolean var17 = false;
               int var18 = 0;
               Object var19 = var16.rememberedValue();
               int var20 = 0;
               Object var10000;
               if (var19 == Composer.Companion.getEmpty()) {
                  int var21 = 0;
                  Object var23 = ((kotlin.jvm.functions.Function0) () -> HeaderMenuItemRowKt.HeaderMenuItemRow_6RhP_wg$lambda$1$lambda$0());
                  var16.updateRememberedValue(var23);
                  var10000 = var23;
               } else {
                  var10000 = var19;
               }

               Function0 var15 = (Function0)var10000;
               var11.endReplaceGroup();
               var8 = var15;
            }

            if ((var13 & 512) != 0) {
               var9 = IntercomTheme.INSTANCE.getColors(var11, IntercomTheme.$stable).getIcon().recovered$e01a1d3b7b68e21a();
               var14 &= -1879048193;
            }
         }

         var11.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1015144426, var14, -1, "io.intercom.android.sdk.m5.conversation.ui.components.HeaderMenuItemRow (HeaderMenuItemRow.kt:43)"
            );
         }

         if (var7) {
            var11.startReplaceGroup(-1365464858);
            ComposerKt.sourceInformation(var11, "45@1718L296");
            ExpandedHeaderMenuItemRow(
               var3,
               var0,
               var2,
               var4,
               var5,
               var6,
               var8,
               var1,
               var11,
               14 & var14 >> 9
                  | 112 & var14 << 3
                  | 896 & var14
                  | 7168 & var14 >> 3
                  | 57344 & var14 >> 3
                  | 458752 & var14 >> 3
                  | 3670016 & var14 >> 6
                  | 29360128 & var14 << 18,
               0
            );
            var11.endReplaceGroup();
         } else {
            var11.startReplaceGroup(-1365149557);
            ComposerKt.sourceInformation(var11, "56@2036L291");
            recovered$5f6dc434e4f38de4(
               var3,
               var0,
               var2,
               var4,
               var5,
               var6,
               var8,
               var9,
               var11,
               14 & var14 >> 9
                  | 112 & var14 << 3
                  | 896 & var14
                  | 7168 & var14 >> 3
                  | 57344 & var14 >> 3
                  | 458752 & var14 >> 3
                  | 3670016 & var14 >> 6
                  | 29360128 & var14 >> 6,
               0
            );
            var11.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var24 = var11.endRestartGroup();
      if (var24 != null) {
         final java.lang.String recoveredCapture_438_0 = var0;
         final boolean recoveredCapture_438_1 = var1;
         final int recoveredCapture_438_2 = var2;
         final androidx.compose.ui.Modifier recoveredCapture_438_3 = var3;
         final java.lang.String recoveredCapture_438_4 = var4;
         final boolean recoveredCapture_438_5 = var5;
         final boolean recoveredCapture_438_6 = var6;
         final boolean recoveredCapture_438_7 = var7;
         final kotlin.jvm.functions.Function0 recoveredCapture_438_8 = var8;
         final long recoveredCapture_438_9 = var9;
         final int recoveredCapture_438_10 = var12;
         final int recoveredCapture_438_11 = var13;
         var24.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_438_0, recoveredArgument_438_1) -> HeaderMenuItemRowKt.HeaderMenuItemRow_6RhP_wg$lambda$2(recoveredCapture_438_0, recoveredCapture_438_1, recoveredCapture_438_2, recoveredCapture_438_3, recoveredCapture_438_4, recoveredCapture_438_5, recoveredCapture_438_6, recoveredCapture_438_7, recoveredCapture_438_8, recoveredCapture_438_9, recoveredCapture_438_10, recoveredCapture_438_11, (androidx.compose.runtime.Composer)recoveredArgument_438_0, ((java.lang.Integer)recoveredArgument_438_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void recovered$5f6dc434e4f38de4(
      Modifier var0,
      String var1,
      @DrawableRes int var2,
      String var3,
      boolean var4,
      boolean var5,
      Function0 var6,
      long var7,
      Composer var9,
      int var10,
      int var11
   ) {
      var9 = var9.startRestartGroup(955883181);
      ComposerKt.sourceInformation(
         var9, "C(CollapsedHeaderMenuItemRow)P(3,2,1!1,5,6!,7:c#ui.graphics.Color)82@2653L480,96@3141L288,80@2597L832:HeaderMenuItemRow.kt#o19g89"
      );
      int var12 = var10;
      if ((var11 & 1) != 0) {
         var12 |= 6;
      } else if ((var10 & 6) == 0) {
         var12 |= var9.changed(var0) ? 4 : 2;
      }

      if ((var11 & 2) != 0) {
         var12 |= 48;
      } else if ((var10 & 48) == 0) {
         var12 |= var9.changed(var1) ? 32 : 16;
      }

      if ((var11 & 4) != 0) {
         var12 |= 384;
      } else if ((var10 & 384) == 0) {
         var12 |= var9.changed(var2) ? 256 : 128;
      }

      if ((var11 & 8) != 0) {
         var12 |= 3072;
      } else if ((var10 & 3072) == 0) {
         var12 |= var9.changed(var3) ? 2048 : 1024;
      }

      if ((var11 & 16) != 0) {
         var12 |= 24576;
      } else if ((var10 & 24576) == 0) {
         var12 |= var9.changed(var4) ? 16384 : 8192;
      }

      if ((var11 & 32) != 0) {
         var12 |= 196608;
      } else if ((var10 & 196608) == 0) {
         var12 |= var9.changed(var5) ? 131072 : 65536;
      }

      if ((var11 & 64) != 0) {
         var12 |= 1572864;
      } else if ((var10 & 1572864) == 0) {
         var12 |= var9.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var11 & 128) != 0) {
         var12 |= 12582912;
      } else if ((var10 & 12582912) == 0) {
         var12 |= var9.changed(var7) ? 8388608 : 4194304;
      }

      if ((var12 & 4793491) == 4793490 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var11 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               955883181, var12, -1, "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemRow (HeaderMenuItemRow.kt:79)"
            );
         }

         BadgeKt.BadgedBox(
            ComposableLambdaKt.rememberComposableLambda(-415284443, true, new HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$1(var5, var3, var4), var9, 54),
            var0,
            ComposableLambdaKt.rememberComposableLambda(
               1598560935, true, new HeaderMenuItemRowKt$CollapsedHeaderMenuItemRow$2(var6, var2, var1, var7), var9, 54
            ),
            var9,
            390 | 112 & var12 << 3,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var9.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_439_0 = var0;
         final java.lang.String recoveredCapture_439_1 = var1;
         final int recoveredCapture_439_2 = var2;
         final java.lang.String recoveredCapture_439_3 = var3;
         final boolean recoveredCapture_439_4 = var4;
         final boolean recoveredCapture_439_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_439_6 = var6;
         final long recoveredCapture_439_7 = var7;
         final int recoveredCapture_439_8 = var10;
         final int recoveredCapture_439_9 = var11;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_439_0, recoveredArgument_439_1) -> HeaderMenuItemRowKt.CollapsedHeaderMenuItemRow_88mDfTA$lambda$3(recoveredCapture_439_0, recoveredCapture_439_1, recoveredCapture_439_2, recoveredCapture_439_3, recoveredCapture_439_4, recoveredCapture_439_5, recoveredCapture_439_6, recoveredCapture_439_7, recoveredCapture_439_8, recoveredCapture_439_9, (androidx.compose.runtime.Composer)recoveredArgument_439_0, ((java.lang.Integer)recoveredArgument_439_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void ExpandedHeaderMenuItemRow(
      Modifier var0,
      String var1,
      @DrawableRes int var2,
      String var3,
      boolean var4,
      boolean var5,
      Function0 var6,
      boolean var7,
      Composer var8,
      int var9,
      int var10
   ) {
      var8 = var8.startRestartGroup(1007952458);
      ComposerKt.sourceInformation(var8, "C(ExpandedHeaderMenuItemRow)P(4,3,2!1,6,7,5)124@3790L13,121@3697L942:HeaderMenuItemRow.kt#o19g89");
      int var11 = var9;
      if ((var10 & 1) != 0) {
         var11 |= 6;
      } else if ((var9 & 6) == 0) {
         var11 |= var8.changed(var0) ? 4 : 2;
      }

      if ((var10 & 2) != 0) {
         var11 |= 48;
      } else if ((var9 & 48) == 0) {
         var11 |= var8.changed(var1) ? 32 : 16;
      }

      if ((var10 & 4) != 0) {
         var11 |= 384;
      } else if ((var9 & 384) == 0) {
         var11 |= var8.changed(var2) ? 256 : 128;
      }

      if ((var10 & 8) != 0) {
         var11 |= 3072;
      } else if ((var9 & 3072) == 0) {
         var11 |= var8.changed(var3) ? 2048 : 1024;
      }

      if ((var10 & 16) != 0) {
         var11 |= 24576;
      } else if ((var9 & 24576) == 0) {
         var11 |= var8.changed(var4) ? 16384 : 8192;
      }

      if ((var10 & 32) != 0) {
         var11 |= 196608;
      } else if ((var9 & 196608) == 0) {
         var11 |= var8.changed(var5) ? 131072 : 65536;
      }

      if ((var10 & 64) != 0) {
         var11 |= 1572864;
      } else if ((var9 & 1572864) == 0) {
         var11 |= var8.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var10 & 128) != 0) {
         var11 |= 12582912;
      } else if ((var9 & 12582912) == 0) {
         var11 |= var8.changed(var7) ? 8388608 : 4194304;
      }

      if ((var11 & 4793491) == 4793490 && var8.getSkipping()) {
         var8.skipToGroupEnd();
      } else {
         if ((var10 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1007952458, var11, -1, "io.intercom.android.sdk.m5.conversation.ui.components.ExpandedHeaderMenuItemRow (HeaderMenuItemRow.kt:120)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null);
         boolean var10001 = var7;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var8.startReplaceGroup(-1310319816);
         ComposerKt.sourceInformation(var8, "CC(remember):HeaderMenuItemRow.kt#9igjgp");
         Composer var13 = var8;
         boolean var14 = (var11 & 3670016) == 1048576;
         int var15 = 0;
         Object var16 = var13.rememberedValue();
         int var17 = 0;
         Object var10005;
         if (!var14 && var16 != Composer.Companion.getEmpty()) {
            var10005 = var16;
         } else {
            Object var50 = null;
            Object var49 = null;
            Object var48 = null;
            boolean var47 = var7;
            Modifier var46 = var10000;
            int var18 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_434_0 = var6;
            Function0 var51 = ((kotlin.jvm.functions.Function0) () -> HeaderMenuItemRowKt.ExpandedHeaderMenuItemRow$lambda$5$lambda$4(recoveredCapture_434_0));
            var10000 = var46;
            var10001 = var47;
            var10002 = var48;
            var10003 = var49;
            var10004 = var50;
            Object var19 = var51;
            var13.updateRememberedValue(var19);
            var10005 = var19;
         }

         Function0 var12 = (Function0)var10005;
         var8.endReplaceGroup();
         var10000 = ClickableKt.recovered$1c9660c41fd850dc(
            var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var12, 14, null
         );
         int var53 = 16;
         int var55 = 0;
         Modifier var54 = PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060(var53));
         Horizontal var56 = Arrangement.INSTANCE.getSpaceBetween();
         Vertical var57 = Alignment.Companion.getCenterVertically();
         short var58 = 432;
         var16 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var8, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         MeasurePolicy var60 = RowKt.rowMeasurePolicy(var56, var57, var8, 14 & var58 >> 3 | 112 & var58 >> 3);
         Modifier var61 = var54;
         MeasurePolicy var62 = var60;
         Composer var20 = var8;
         int var21 = 112 & var58 << 3;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var23 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var20, 0));
         CompositionLocalMap var24 = var20.getCurrentCompositionLocalMap();
         Modifier var25 = ComposedModifierKt.materializeModifier(var20, var61);
         Function0 var26 = ComposeUiNode.Companion.getConstructor();
         Composer var27 = var20;
         int var28 = 6 | 896 & var21 << 6;
         int var29 = 0;
         ComposerKt.sourceInformationMarkerStart(var27, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var27.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var27.startReusableNode();
         if (var27.getInserting()) {
            var27.createNode(var26);
         } else {
            var27.useNode();
         }

         Composer var30 = Updater.recovered$676aa17b44fd6280(var27);
         int var31 = 0;
         Updater.recovered$89682b199a142880(var30, var62, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var30, var24, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var30, var23, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var30, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var30, var25, ComposeUiNode.Companion.getSetModifier());
         int var32 = 14 & var28 >> 6;
         Composer var33 = var27;
         int var34 = 0;
         ComposerKt.sourceInformationMarkerStart(var33, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var35 = 6 | 112 & var58 >> 6;
         Composer var36 = var33;
         RowScope var37 = RowScopeInstance.INSTANCE;
         int var38 = 0;
         ComposerKt.sourceInformationMarkerStart(var36, -1879314405, "C130@4051L33:HeaderMenuItemRow.kt#o19g89");
         var36.startReplaceGroup(1463397389);
         ComposerKt.sourceInformation(var36, "129@4002L6");
         long var39 = var7
            ? IntercomTheme.INSTANCE.getColors(var36, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a()
            : Color.Companion.recovered$06265ebb6179c32c();
         var36.endReplaceGroup();
         long var41 = var39;
         TextKt.recovered$5b114ab32a4f4190(
            var1, null, var41, null, 0L, null, null, null, 0L, null, null, 0L, 0, false, 0, 0, null, null, var36, 14 & var11 >> 3, 0, 262138
         );
         if (var5) {
            var36.startReplaceGroup(-1879184113);
            ComposerKt.sourceInformation(var36, "132@4126L81");
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 4;
            int $i$f$getDpx = 0;
            ConversationItemKt.UnreadIndicator(PaddingKt.recovered$eb6a744cd98cdbca(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var36, 6, 0);
            var36.endReplaceGroup();
         } else if (var3 != null && var4) {
            var36.startReplaceGroup(-1878791622);
            ComposerKt.sourceInformation(var36, "144@4521L102");
            UnreadBadge(Modifier.Companion, var3, var36, 6 | 112 & var11 >> 6, 0);
            var36.endReplaceGroup();
         } else {
            var36.startReplaceGroup(-1879033081);
            ComposerKt.sourceInformation(var36, "139@4376L26,136@4274L217");
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            Modifier var43 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
            IconKt.recovered$e00adffb7fe45b02(
               PainterResources_androidKt.painterResource(var2, var36, 14 & var11 >> 6), var1, var43, var41, var36, 384 | Painter.$stable | 112 & var11, 0
            );
            var36.endReplaceGroup();
         }

         ComposerKt.sourceInformationMarkerEnd(var36);
         ComposerKt.sourceInformationMarkerEnd(var33);
         var27.endNode();
         ComposerKt.sourceInformationMarkerEnd(var27);
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var8);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var68 = var8.endRestartGroup();
      if (var68 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_435_0 = var0;
         final java.lang.String recoveredCapture_435_1 = var1;
         final int recoveredCapture_435_2 = var2;
         final java.lang.String recoveredCapture_435_3 = var3;
         final boolean recoveredCapture_435_4 = var4;
         final boolean recoveredCapture_435_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_435_6 = var6;
         final boolean recoveredCapture_435_7 = var7;
         final int recoveredCapture_435_8 = var9;
         final int recoveredCapture_435_9 = var10;
         var68.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_435_0, recoveredArgument_435_1) -> HeaderMenuItemRowKt.ExpandedHeaderMenuItemRow$lambda$7(recoveredCapture_435_0, recoveredCapture_435_1, recoveredCapture_435_2, recoveredCapture_435_3, recoveredCapture_435_4, recoveredCapture_435_5, recoveredCapture_435_6, recoveredCapture_435_7, recoveredCapture_435_8, recoveredCapture_435_9, (androidx.compose.runtime.Composer)recoveredArgument_435_0, ((java.lang.Integer)recoveredArgument_435_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void UnreadBadge(Modifier var0, String var1, Composer var2, int var3, int var4) {
      var2 = var2.startRestartGroup(1917800334);
      ComposerKt.sourceInformation(var2, "C(UnreadBadge)P(1)154@4766L6,159@4945L298,156@4866L377:HeaderMenuItemRow.kt#o19g89");
      int var5 = var3;
      if ((var4 & 1) != 0) {
         var5 |= 6;
      } else if ((var3 & 6) == 0) {
         var5 |= var2.changed(var0) ? 4 : 2;
      }

      if ((var4 & 2) != 0) {
         var5 |= 48;
      } else if ((var3 & 48) == 0) {
         var5 |= var2.changed(var1) ? 32 : 16;
      }

      if ((var5 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if ((var4 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1917800334, var5, -1, "io.intercom.android.sdk.m5.conversation.ui.components.UnreadBadge (HeaderMenuItemRow.kt:153)");
         }

         long var6 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).recovered$ff4dc34320f68c71();
         long var8 = ColorExtensionsKt.recovered$4886b56a29c1c487(var6)
            ? Color.Companion.recovered$235b30c090a399c5()
            : Color.Companion.recovered$95989221dad41c4d();
         BadgeKt.recovered$d9697d11cb3615b2(
            var0,
            var6,
            0L,
            ComposableLambdaKt.rememberComposableLambda(-1370135743, true, new HeaderMenuItemRowKt$UnreadBadge$1(var1, var8), var2, 54),
            var2,
            3072 | 14 & var5,
            4
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_436_0 = var0;
         final java.lang.String recoveredCapture_436_1 = var1;
         final int recoveredCapture_436_2 = var3;
         final int recoveredCapture_436_3 = var4;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_436_0, recoveredArgument_436_1) -> HeaderMenuItemRowKt.UnreadBadge$lambda$8(recoveredCapture_436_0, recoveredCapture_436_1, recoveredCapture_436_2, recoveredCapture_436_3, (androidx.compose.runtime.Composer)recoveredArgument_436_0, ((java.lang.Integer)recoveredArgument_436_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CollapsedHeaderMenuItemPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1230888234);
      ComposerKt.sourceInformation(var0, "C(CollapsedHeaderMenuItemPreview)175@5329L353:HeaderMenuItemRow.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1230888234, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemPreview (HeaderMenuItemRow.kt:174)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HeaderMenuItemRowKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_433_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_433_0, recoveredArgument_433_1) -> HeaderMenuItemRowKt.CollapsedHeaderMenuItemPreview$lambda$9(recoveredCapture_433_0, (androidx.compose.runtime.Composer)recoveredArgument_433_0, ((java.lang.Integer)recoveredArgument_433_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void CollapsedHeaderMenuItemDotPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(591640881);
      ComposerKt.sourceInformation(var0, "C(CollapsedHeaderMenuItemDotPreview)193@5771L352:HeaderMenuItemRow.kt#o19g89");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               591640881, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.CollapsedHeaderMenuItemDotPreview (HeaderMenuItemRow.kt:192)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$HeaderMenuItemRowKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_432_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_432_0, recoveredArgument_432_1) -> HeaderMenuItemRowKt.CollapsedHeaderMenuItemDotPreview$lambda$10(recoveredCapture_432_0, (androidx.compose.runtime.Composer)recoveredArgument_432_0, ((java.lang.Integer)recoveredArgument_432_1).intValue())));
      }
   }

   private static final Unit HeaderMenuItemRow_6RhP_wg$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit HeaderMenuItemRow_6RhP_wg$lambda$2(
      String var0,
      boolean var1,
      int var2,
      Modifier var3,
      String var4,
      boolean var5,
      boolean var6,
      boolean var7,
      Function0 var8,
      long var9,
      int var11,
      int var12,
      Composer var13,
      int var14
   ) {
      recovered$04990d299bee3c60(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var13, RecomposeScopeImplKt.updateChangedFlags(var11 | 1), var12);
      return Unit.INSTANCE;
   }

   private static final Unit CollapsedHeaderMenuItemRow_88mDfTA$lambda$3(
      Modifier var0, String var1, int var2, String var3, boolean var4, boolean var5, Function0 var6, long var7, int var9, int var10, Composer var11, int var12
   ) {
      recovered$5f6dc434e4f38de4(var0, var1, var2, var3, var4, var5, var6, var7, var11, RecomposeScopeImplKt.updateChangedFlags(var9 | 1), var10);
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedHeaderMenuItemRow$lambda$5$lambda$4(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ExpandedHeaderMenuItemRow$lambda$7(
      Modifier var0,
      String var1,
      int var2,
      String var3,
      boolean var4,
      boolean var5,
      Function0 var6,
      boolean var7,
      int var8,
      int var9,
      Composer var10,
      int var11
   ) {
      ExpandedHeaderMenuItemRow(var0, var1, var2, var3, var4, var5, var6, var7, var10, RecomposeScopeImplKt.updateChangedFlags(var8 | 1), var9);
      return Unit.INSTANCE;
   }

   private static final Unit UnreadBadge$lambda$8(Modifier var0, String var1, int var2, int var3, Composer var4, int var5) {
      UnreadBadge(var0, var1, var4, RecomposeScopeImplKt.updateChangedFlags(var2 | 1), var3);
      return Unit.INSTANCE;
   }

   private static final Unit CollapsedHeaderMenuItemPreview$lambda$9(int var0, Composer var1, int var2) {
      CollapsedHeaderMenuItemPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit CollapsedHeaderMenuItemDotPreview$lambda$10(int var0, Composer var1, int var2) {
      CollapsedHeaderMenuItemDotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$UnreadBadge(Modifier var0, String var1, Composer var2, int var3, int var4) {
      UnreadBadge(var0, var1, var2, var3, var4);
   }
}
