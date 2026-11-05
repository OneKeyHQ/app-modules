package io.intercom.android.sdk.ui.component;

import android.os.Build.VERSION;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.material3.CardColors;
import androidx.compose.material3.CardDefaults;
import androidx.compose.material3.CardKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.draw.ShadowKt;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.unit.Dp;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000<\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a?\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u001c\u0010\u0006\u001a\u0018\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\t¢\u0006\u0002\b\nH\u0007¢\u0006\u0002\u0010\u000b\u001aa\u0010\u0000\u001a\u00020\u00012\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u000f2\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0010\u001a\u00020\u00112\u001c\u0010\u0006\u001a\u0018\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007¢\u0006\u0002\b\t¢\u0006\u0002\b\nH\u0007¢\u0006\u0002\u0010\u0012¨\u0006\u0013",
   d2 = {
         "IntercomCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "style",
         "Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;",
         "content",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/ColumnScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "onClick",
         "Lkotlin/Function0;",
         "enabled",
         "",
         "interactionSource",
         "Landroidx/compose/foundation/interaction/MutableInteractionSource;",
         "(Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;ZLio/intercom/android/sdk/ui/component/IntercomCardStyle$Style;Landroidx/compose/foundation/interaction/MutableInteractionSource;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;II)V",
         "intercom-sdk-ui_release"
   }
)
@SourceDebugExtension(
   "SMAP\nIntercomCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntercomCard.kt\nio/intercom/android/sdk/ui/component/IntercomCardKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,128:1\n118#2:129\n118#2:130\n1047#3,6:131\n*S KotlinDebug\n*F\n+ 1 IntercomCard.kt\nio/intercom/android/sdk/ui/component/IntercomCardKt\n*L\n32#1:129\n41#1:130\n57#1:131,6\n*E\n"
)
public final class IntercomCardKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void IntercomCard(
      @Nullable Modifier var0, @Nullable IntercomCardStyle$Style var1, @NotNull Function3 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var2, "content");
      var3 = var3.startRestartGroup(1083788517);
      ComposerKt.sourceInformation(var3, "C(IntercomCard)P(1,2)25@997L14,36@1509L120,40@1664L38,28@1070L754:IntercomCard.kt#yr602f");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var3.changed(var0) ? 4 : 2;
      }

      if ((var4 & 48) == 0) {
         var6 |= (var5 & 2) == 0 && var3.changed(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var3.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var3.getSkipping()) {
         var3.skipToGroupEnd();
      } else {
         var3.startDefaults();
         if ((var4 & 1) != 0 && !var3.getDefaultsInvalid()) {
            var3.skipToGroupEnd();
            if ((var5 & 2) != 0) {
               var6 &= -113;
            }
         } else {
            if ((var5 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var5 & 2) != 0) {
               var1 = IntercomCardStyle.INSTANCE.recovered$24b4efdd2276d096(null, 0L, 0L, 0.0F, null, 0L, var3, 1572864, 63);
               var6 &= -113;
            }
         }

         var3.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1083788517, var6, -1, "io.intercom.android.sdk.ui.component.IntercomCard (IntercomCard.kt:27)");
         }

         float var10001;
         if (VERSION.SDK_INT >= 28) {
            var10001 = var1.recovered$a23e2bd502420b69();
         } else {
            int var7 = 2;
            int var8 = 0;
            var10001 = Dp.recovered$95b3d103663bf060(var7);
         }

         Color var10005 = var1.recovered$2197b6b5348c465a();
         Modifier var10000 = ShadowKt.recovered$04904c26f7ded95f(
            var0,
            var10001,
            var1.getShape(),
            false,
            0L,
            var10005 != null ? var10005.recovered$45f9de7b85e0e0b5() : Color.Companion.recovered$235b30c090a399c5(),
            12,
            null
         );
         Shape var13 = var1.getShape();
         CardColors var10002 = CardDefaults.INSTANCE
            .recovered$da184985090a28d4(var1.recovered$934f6183e8132565(), var1.recovered$6557fe71dfd7d063(), 0L, 0L, var3, CardDefaults.$stable << 12, 12);
         int var10 = 0;
         int var11 = 0;
         CardKt.Card(
            var10000,
            var13,
            var10002,
            CardDefaults.INSTANCE
               .recovered$9b4df60457ff5e1b(Dp.recovered$95b3d103663bf060(var10), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var3, 6 | CardDefaults.$stable << 18, 62),
            var1.getBorder(),
            var2,
            var3,
            458752 & var6 << 9,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var3.endRestartGroup();
      if (var12 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_14_0 = var0;
         final io.intercom.android.sdk.ui.component.IntercomCardStyle$Style recoveredCapture_14_1 = var1;
         final kotlin.jvm.functions.Function3 recoveredCapture_14_2 = var2;
         final int recoveredCapture_14_3 = var4;
         final int recoveredCapture_14_4 = var5;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_14_0, recoveredArgument_14_1) -> IntercomCardKt.IntercomCard$lambda$0(recoveredCapture_14_0, recoveredCapture_14_1, recoveredCapture_14_2, recoveredCapture_14_3, recoveredCapture_14_4, (androidx.compose.runtime.Composer)recoveredArgument_14_0, ((java.lang.Integer)recoveredArgument_14_1).intValue())));
      }
   }

   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void IntercomCard(
      @NotNull Function0 var0,
      @Nullable Modifier var1,
      boolean var2,
      @Nullable IntercomCardStyle$Style var3,
      @Nullable MutableInteractionSource var4,
      @NotNull Function3 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var0, "onClick");
      Intrinsics.checkNotNullParameter(var5, "content");
      var6 = var6.startRestartGroup(254499300);
      ComposerKt.sourceInformation(var6, "C(IntercomCard)P(4,3,1,5,2)55@2164L14,56@2230L39,64@2476L120,68@2631L72,59@2328L487:IntercomCard.kt#yr602f");
      int var9 = var7;
      if ((var8 & 1) != 0) {
         var9 |= 6;
      } else if ((var7 & 6) == 0) {
         var9 |= var6.changedInstance(var0) ? 4 : 2;
      }

      if ((var8 & 2) != 0) {
         var9 |= 48;
      } else if ((var7 & 48) == 0) {
         var9 |= var6.changed(var1) ? 32 : 16;
      }

      if ((var8 & 4) != 0) {
         var9 |= 384;
      } else if ((var7 & 384) == 0) {
         var9 |= var6.changed(var2) ? 256 : 128;
      }

      if ((var7 & 3072) == 0) {
         var9 |= (var8 & 8) == 0 && var6.changed(var3) ? 2048 : 1024;
      }

      if ((var8 & 16) != 0) {
         var9 |= 24576;
      } else if ((var7 & 24576) == 0) {
         var9 |= var6.changed(var4) ? 16384 : 8192;
      }

      if ((var8 & 32) != 0) {
         var9 |= 196608;
      } else if ((var7 & 196608) == 0) {
         var9 |= var6.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var9 & 74899) == 74898 && var6.getSkipping()) {
         var6.skipToGroupEnd();
      } else {
         var6.startDefaults();
         if ((var7 & 1) != 0 && !var6.getDefaultsInvalid()) {
            var6.skipToGroupEnd();
            if ((var8 & 8) != 0) {
               var9 &= -7169;
            }
         } else {
            if ((var8 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var8 & 4) != 0) {
               var2 = true;
            }

            if ((var8 & 8) != 0) {
               var3 = IntercomCardStyle.INSTANCE.recovered$24b4efdd2276d096(null, 0L, 0L, 0.0F, null, 0L, var6, 1572864, 63);
               var9 &= -7169;
            }

            if ((var8 & 16) != 0) {
               var6.startReplaceGroup(-2091443309);
               ComposerKt.sourceInformation(var6, "CC(remember):IntercomCard.kt#9igjgp");
               Composer var11 = var6;
               boolean var12 = false;
               int var13 = 0;
               Object var14 = var11.rememberedValue();
               int var15 = 0;
               Object var10000;
               if (var14 == Composer.Companion.getEmpty()) {
                  int var16 = 0;
                  Object var18 = InteractionSourceKt.MutableInteractionSource();
                  var11.updateRememberedValue(var18);
                  var10000 = var18;
               } else {
                  var10000 = var14;
               }

               MutableInteractionSource var10 = (MutableInteractionSource)var10000;
               var6.endReplaceGroup();
               var4 = var10;
            }
         }

         var6.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(254499300, var9, -1, "io.intercom.android.sdk.ui.component.IntercomCard (IntercomCard.kt:58)");
         }

         CardKt.Card(
            var0,
            var1,
            var2,
            var3.getShape(),
            CardDefaults.INSTANCE
               .recovered$da184985090a28d4(var3.recovered$934f6183e8132565(), var3.recovered$6557fe71dfd7d063(), 0L, 0L, var6, CardDefaults.$stable << 12, 12),
            CardDefaults.INSTANCE
               .recovered$9b4df60457ff5e1b(var3.recovered$a23e2bd502420b69(), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, var6, CardDefaults.$stable << 18, 62),
            var3.getBorder(),
            var4,
            var5,
            var6,
            14 & var9 | 112 & var9 | 896 & var9 | 29360128 & var9 << 9 | 234881024 & var9 << 9,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var19 = var6.endRestartGroup();
      if (var19 != null) {
         final kotlin.jvm.functions.Function0 recoveredCapture_15_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_15_1 = var1;
         final boolean recoveredCapture_15_2 = var2;
         final io.intercom.android.sdk.ui.component.IntercomCardStyle$Style recoveredCapture_15_3 = var3;
         final androidx.compose.foundation.interaction.MutableInteractionSource recoveredCapture_15_4 = var4;
         final kotlin.jvm.functions.Function3 recoveredCapture_15_5 = var5;
         final int recoveredCapture_15_6 = var7;
         final int recoveredCapture_15_7 = var8;
         var19.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_15_0, recoveredArgument_15_1) -> IntercomCardKt.IntercomCard$lambda$2(recoveredCapture_15_0, recoveredCapture_15_1, recoveredCapture_15_2, recoveredCapture_15_3, recoveredCapture_15_4, recoveredCapture_15_5, recoveredCapture_15_6, recoveredCapture_15_7, (androidx.compose.runtime.Composer)recoveredArgument_15_0, ((java.lang.Integer)recoveredArgument_15_1).intValue())));
      }
   }

   private static final Unit IntercomCard$lambda$0(Modifier var0, IntercomCardStyle$Style var1, Function3 var2, int var3, int var4, Composer var5, int var6) {
      IntercomCard(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit IntercomCard$lambda$2(
      Function0 var0,
      Modifier var1,
      boolean var2,
      IntercomCardStyle$Style var3,
      MutableInteractionSource var4,
      Function3 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      IntercomCard(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }
}
