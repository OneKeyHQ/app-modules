package io.intercom.android.sdk.m5.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.Window;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.layout.WindowInsetsPadding_androidKt;
import androidx.compose.foundation.layout.Arrangement.Vertical;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableInferredTarget;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.Alignment.Horizontal;
import androidx.compose.ui.graphics.painter.Painter;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.res.PainterResources_androidKt;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.style.TextAlign;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.core.view.WindowCompat;
import io.intercom.android.sdk.m5.components.avatar.AvatarIconKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.R.drawable;
import io.intercom.android.sdk.ui.component.IntercomDividerKt;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import io.intercom.android.sdk.utilities.ColorExtensionsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000P\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\f\u001aÃ\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010\u0007\u001a\u0004\u0018\u00010\b2\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\u0010\b\u0002\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\r2\n\b\u0003\u0010\u000e\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u00122\b\b\u0002\u0010\u0013\u001a\u00020\u00122\b\b\u0002\u0010\u0014\u001a\u00020\u00122\u000e\b\u0002\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\r2 \b\u0002\u0010\u0016\u001a\u001a\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0017¢\u0006\u0002\b\u0019¢\u0006\u0002\b\u001aH\u0001¢\u0006\u0004\b\u001b\u0010\u001c\u001a\r\u0010\u001d\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010\u001f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010 \u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010!\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010\"\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010#\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010$\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e\u001a\r\u0010%\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u001e¨\u0006&",
   d2 = {
         "TopActionBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "title",
         "",
         "subtitle",
         "subtitleIcon",
         "",
         "avatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "onBackClick",
         "Lkotlin/Function0;",
         "navIcon",
         "isActive",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "contentColor",
         "subtitleColor",
         "onTitleClicked",
         "menuItems",
         "Lkotlin/Function1;",
         "Landroidx/compose/foundation/layout/RowScope;",
         "Lkotlin/ExtensionFunctionType;",
         "Landroidx/compose/runtime/Composable;",
         "TopActionBar-NpQZenA",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/util/List;Lkotlin/jvm/functions/Function0;Ljava/lang/Integer;ZJJJLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function3;Landroidx/compose/runtime/Composer;III)V",
         "TopActionBarAvatarPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "TopActionBarGroupChatPreview",
         "TopActionBarTeamReplyTimePreview",
         "TopActionBarMultipleAvatarsPreview",
         "TopActionBarAvatarNoBackButtonPreview",
         "TopActionBarBackButtonPreview",
         "TopActionBarTitlePreview",
         "TopActionBarEndContentPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTopActionBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/TopActionBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 4 Column.kt\nandroidx/compose/foundation/layout/ColumnKt\n+ 5 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 6 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 7 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 8 Row.kt\nandroidx/compose/foundation/layout/RowKt\n*L\n1#1,255:1\n1047#2,6:256\n1047#2,6:263\n1047#2,6:304\n75#3:262\n87#4:269\n83#4,10:270\n87#4:346\n84#4,9:347\n94#4:420\n94#4:429\n81#5,6:280\n88#5,6:295\n81#5,6:320\n88#5,6:335\n81#5,6:356\n88#5,6:371\n81#5,6:389\n88#5,6:404\n96#5:415\n96#5:419\n96#5:424\n96#5:428\n402#6,9:286\n411#6:301\n402#6,9:326\n411#6:341\n402#6,9:362\n411#6:377\n402#6,9:395\n411#6:410\n412#6,2:413\n412#6,2:417\n412#6,2:422\n412#6,2:426\n118#7:302\n118#7:303\n118#7:342\n118#7:343\n118#7:344\n118#7:345\n118#7:411\n118#7:412\n118#7:421\n99#8:310\n96#8,9:311\n99#8:378\n95#8,10:379\n106#8:416\n106#8:425\n*S KotlinDebug\n*F\n+ 1 TopActionBar.kt\nio/intercom/android/sdk/m5/components/TopActionBarKt\n*L\n56#1:256,6\n60#1:263,6\n72#1:304,6\n59#1:262\n64#1:269\n64#1:270,10\n103#1:346\n103#1:347,9\n103#1:420\n64#1:429\n64#1:280,6\n64#1:295,6\n65#1:320,6\n65#1:335,6\n103#1:356,6\n103#1:371,6\n113#1:389,6\n113#1:404,6\n113#1:415\n103#1:419\n65#1:424\n64#1:428\n64#1:286,9\n64#1:301\n65#1:326,9\n65#1:341\n103#1:362,9\n103#1:377\n113#1:395,9\n113#1:410\n113#1:413,2\n103#1:417,2\n65#1:422,2\n64#1:426,2\n70#1:302\n71#1:303\n87#1:342\n92#1:343\n99#1:344\n102#1:345\n119#1:411\n121#1:412\n134#1:421\n65#1:310\n65#1:311,9\n113#1:378\n113#1:379,10\n113#1:416\n65#1:425\n*E\n"
)
public final class TopActionBarKt {
   @Composable
   @ComposableInferredTarget(scheme = "[androidx.compose.ui.UiComposable[androidx.compose.ui.UiComposable]]")
   public static final void recovered$e7704aa1c1b2200b(
      @Nullable Modifier var0,
      @Nullable String var1,
      @Nullable String var2,
      @DrawableRes @Nullable Integer var3,
      @Nullable List var4,
      @Nullable Function0 var5,
      @DrawableRes @Nullable Integer var6,
      boolean var7,
      long var8,
      long var10,
      long var12,
      @Nullable Function0 var14,
      @Nullable Function3 var15,
      @Nullable Composer var16,
      int var17,
      int var18,
      int var19
   ) {
      var16 = var16.startRestartGroup(-1160200926);
      ComposerKt.sourceInformation(
         var16,
         "C(TopActionBar)P(5,12,9,11!1,7,6,3,1:c#ui.graphics.Color,2:c#ui.graphics.Color,10:c#ui.graphics.Color,8)52@2277L6,53@2332L6,54@2390L6,55@2440L2,58@2521L7,63@2745L3200:TopActionBar.kt#arqjag"
      );
      int var20 = var17;
      int var21 = var18;
      if ((var19 & 1) != 0) {
         var20 |= 6;
      } else if ((var17 & 6) == 0) {
         var20 |= var16.changed(var0) ? 4 : 2;
      }

      if ((var19 & 2) != 0) {
         var20 |= 48;
      } else if ((var17 & 48) == 0) {
         var20 |= var16.changed(var1) ? 32 : 16;
      }

      if ((var19 & 4) != 0) {
         var20 |= 384;
      } else if ((var17 & 384) == 0) {
         var20 |= var16.changed(var2) ? 256 : 128;
      }

      if ((var19 & 8) != 0) {
         var20 |= 3072;
      } else if ((var17 & 3072) == 0) {
         var20 |= var16.changed(var3) ? 2048 : 1024;
      }

      if ((var19 & 16) != 0) {
         var20 |= 24576;
      } else if ((var17 & 24576) == 0) {
         var20 |= var16.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var19 & 32) != 0) {
         var20 |= 196608;
      } else if ((var17 & 196608) == 0) {
         var20 |= var16.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var17 & 1572864) == 0) {
         var20 |= (var19 & 64) == 0 && var16.changed(var6) ? 1048576 : 524288;
      }

      if ((var19 & 128) != 0) {
         var20 |= 12582912;
      } else if ((var17 & 12582912) == 0) {
         var20 |= var16.changed(var7) ? 8388608 : 4194304;
      }

      if ((var17 & 100663296) == 0) {
         var20 |= (var19 & 256) == 0 && var16.changed(var8) ? 67108864 : 33554432;
      }

      if ((var17 & 805306368) == 0) {
         var20 |= (var19 & 512) == 0 && var16.changed(var10) ? 536870912 : 268435456;
      }

      if ((var18 & 6) == 0) {
         var21 |= (var19 & 1024) == 0 && var16.changed(var12) ? 4 : 2;
      }

      if ((var19 & 2048) != 0) {
         var21 |= 48;
      } else if ((var18 & 48) == 0) {
         var21 |= var16.changedInstance(var14) ? 32 : 16;
      }

      if ((var19 & 4096) != 0) {
         var21 |= 384;
      } else if ((var18 & 384) == 0) {
         var21 |= var16.changedInstance(var15) ? 256 : 128;
      }

      if ((var20 & 306783379) == 306783378 && (var21 & 147) == 146 && var16.getSkipping()) {
         var16.skipToGroupEnd();
      } else {
         var16.startDefaults();
         if ((var17 & 1) != 0 && !var16.getDefaultsInvalid()) {
            var16.skipToGroupEnd();
            if ((var19 & 64) != 0) {
               var20 &= -3670017;
            }

            if ((var19 & 256) != 0) {
               var20 &= -234881025;
            }

            if ((var19 & 512) != 0) {
               var20 &= -1879048193;
            }

            if ((var19 & 1024) != 0) {
               var21 &= -15;
            }
         } else {
            if ((var19 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var19 & 2) != 0) {
               var1 = "";
            }

            if ((var19 & 4) != 0) {
               var2 = null;
            }

            if ((var19 & 8) != 0) {
               var3 = null;
            }

            if ((var19 & 16) != 0) {
               var4 = CollectionsKt.emptyList();
            }

            if ((var19 & 32) != 0) {
               var5 = null;
            }

            if ((var19 & 64) != 0) {
               var6 = drawable.intercom_ic_back;
               var20 &= -3670017;
            }

            if ((var19 & 128) != 0) {
               var7 = false;
            }

            if ((var19 & 256) != 0) {
               var8 = IntercomTheme.INSTANCE.getColors(var16, IntercomTheme.$stable).recovered$a4be33b4cee7fcc4();
               var20 &= -234881025;
            }

            if ((var19 & 512) != 0) {
               var10 = IntercomTheme.INSTANCE.getColors(var16, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
               var20 &= -1879048193;
            }

            if ((var19 & 1024) != 0) {
               var12 = IntercomTheme.INSTANCE.getColors(var16, IntercomTheme.$stable).recovered$156c30ff4ea0f1e5();
               var21 &= -15;
            }

            if ((var19 & 2048) != 0) {
               var16.startReplaceGroup(-1152545072);
               ComposerKt.sourceInformation(var16, "CC(remember):TopActionBar.kt#9igjgp");
               Composer var23 = var16;
               boolean var24 = false;
               int var25 = 0;
               Object var26 = var23.rememberedValue();
               int var27 = 0;
               Object var10000;
               if (var26 == Composer.Companion.getEmpty()) {
                  int var28 = 0;
                  Object var160 = ((kotlin.jvm.functions.Function0) () -> TopActionBarKt.TopActionBar_NpQZenA$lambda$1$lambda$0());
                  var23.updateRememberedValue(var160);
                  var10000 = var160;
               } else {
                  var10000 = var26;
               }

               Function0 var22 = (Function0)var10000;
               var16.endReplaceGroup();
               var14 = var22;
            }

            if ((var19 & 4096) != 0) {
               var15 = null;
            }
         }

         var16.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1160200926, var20, var21, "io.intercom.android.sdk.m5.components.TopActionBar (TopActionBar.kt:57)");
         }

         CompositionLocal var151 = AndroidCompositionLocals_androidKt.getLocalView();
         int var154 = 0;
         int var157 = 0;
         ComposerKt.sourceInformationMarkerStart(var16, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var161 = var16.consume(var151);
         ComposerKt.sourceInformationMarkerEnd(var16);
         Context var149 = ((View)var161).getContext();
         Window var146 = (var149 instanceof Activity ? (Activity)var149 : null) != null
            ? (var149 instanceof Activity ? (Activity)var149 : null).getWindow()
            : null;
         var16.startReplaceGroup(-1152541054);
         ComposerKt.sourceInformation(var16, "*59@2595L139,59@2584L150");
         if (var146 == null) {
            Object var203 = null;
         } else {
            Window var152 = var146;
            var154 = 0;
            var16.startReplaceGroup(369270712);
            ComposerKt.sourceInformation(var16, "CC(remember):TopActionBar.kt#9igjgp");
            Composer var158 = var16;
            boolean var162 = var16.changedInstance(var152)
               | ((var20 & 1879048192 ^ 805306368) > 536870912 && var16.changed(var10) || (var20 & 805306368) == 536870912);
            int var29 = 0;
            Object var30 = var158.rememberedValue();
            int var31 = 0;
            Object var202;
            if (!var162 && var30 != Composer.Companion.getEmpty()) {
               var202 = var30;
            } else {
               int var32 = 0;
               final android.view.Window recoveredCapture_264_0 = var152;
               final long recoveredCapture_264_1 = var10;
               Object var33 = ((kotlin.jvm.functions.Function0) () -> TopActionBarKt.TopActionBar_NpQZenA$lambda$4$lambda$3$lambda$2(recoveredCapture_264_0, recoveredCapture_264_1));
               var158.updateRememberedValue(var33);
               var202 = var33;
            }

            Function0 var34 = (Function0)var202;
            var16.endReplaceGroup();
            EffectsKt.SideEffect(var34, var16, 0);
         }

         var16.endReplaceGroup();
         byte var153 = 0;
         var154 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var16, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Modifier var147 = Modifier.Companion;
         Vertical var148 = Arrangement.INSTANCE.getTop();
         Horizontal var150 = Alignment.Companion.getStart();
         MeasurePolicy var159 = ColumnKt.columnMeasurePolicy(var148, var150, var16, 14 & var153 >> 3 | 112 & var153 >> 3);
         Modifier var163 = var147;
         MeasurePolicy var164 = var159;
         Composer var165 = var16;
         int var166 = 112 & var153 << 3;
         int var167 = 0;
         ComposerKt.sourceInformationMarkerStart(var165, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var168 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var165, 0));
         CompositionLocalMap var169 = var165.getCurrentCompositionLocalMap();
         Modifier var35 = ComposedModifierKt.materializeModifier(var165, var163);
         Function0 var36 = ComposeUiNode.Companion.getConstructor();
         Composer var37 = var165;
         int var38 = 6 | 896 & var166 << 6;
         int var39 = 0;
         ComposerKt.sourceInformationMarkerStart(var37, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var37.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var37.startReusableNode();
         if (var37.getInserting()) {
            var37.createNode(var36);
         } else {
            var37.useNode();
         }

         Composer var40 = Updater.recovered$676aa17b44fd6280(var37);
         int var41 = 0;
         Updater.recovered$89682b199a142880(var40, var164, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var40, var169, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var40, var168, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var40, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var40, var35, ComposeUiNode.Companion.getSetModifier());
         int var42 = 14 & var38 >> 6;
         Composer var43 = var37;
         int var44 = 0;
         ComposerKt.sourceInformationMarkerStart(var43, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var45 = 6 | 112 & var153 >> 6;
         Composer var46 = var43;
         ColumnScope var47 = ColumnScopeInstance.INSTANCE;
         int var48 = 0;
         ComposerKt.sourceInformationMarkerStart(var46, -1437249971, "C71@3008L20,64@2762L3103:TopActionBar.kt#arqjag");
         Modifier var204 = WindowInsetsPadding_androidKt.statusBarsPadding(
            BackgroundKt.recovered$42a3174180b3fb7a(SizeKt.fillMaxWidth$default(var0, 0.0F, 1, null), var8, null, 2, null)
         );
         int var49 = 56;
         int var50 = 0;
         var204 = SizeKt.recovered$3f6c6a76aa292717(var204, Dp.recovered$95b3d103663bf060(var49));
         var49 = 4;
         var50 = 0;
         var204 = PaddingKt.recovered$03b481d31d62885a(var204, 0.0F, 0.0F, Dp.recovered$95b3d103663bf060(var49), 0.0F, 11, null);
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var46.startReplaceGroup(369283809);
         ComposerKt.sourceInformation(var46, "CC(remember):TopActionBar.kt#9igjgp");
         Composer var174 = var46;
         boolean var51 = (var21 & 112) == 32;
         int var52 = 0;
         Object var53 = var174.rememberedValue();
         int var54 = 0;
         Object var10005;
         if (!var51 && var53 != Composer.Companion.getEmpty()) {
            var10005 = var53;
         } else {
            Object var55 = null;
            Object var56 = null;
            Object var57 = null;
            boolean var58 = false;
            Modifier var59 = var204;
            int var60 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_261_0 = var14;
            Function0 var61 = ((kotlin.jvm.functions.Function0) () -> TopActionBarKt.TopActionBar_NpQZenA$lambda$11$lambda$6$lambda$5(recoveredCapture_261_0));
            var204 = var59;
            var10001 = var58;
            var10002 = var57;
            var10003 = var56;
            var10004 = var55;
            Object var62 = var61;
            var174.updateRememberedValue(var62);
            var10005 = var62;
         }

         Function0 var171 = (Function0)var10005;
         var46.endReplaceGroup();
         Modifier var172 = ClickableKt.recovered$1c9660c41fd850dc(
            var204, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var171, 15, null
         );
         androidx.compose.ui.Alignment.Vertical var176 = Alignment.Companion.getCenterVertically();
         short var177 = 384;
         var53 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var46, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
         );
         androidx.compose.foundation.layout.Arrangement.Horizontal var175 = Arrangement.INSTANCE.getStart();
         MeasurePolicy measurePolicy$ivx = RowKt.rowMeasurePolicy(var175, var176, var46, 14 & var177 >> 3 | 112 & var177 >> 3);
         Modifier var180 = var172;
         MeasurePolicy var181 = measurePolicy$ivx;
         Composer var63 = var46;
         int var64 = 112 & var177 << 3;
         int $i$f$Layoutx = 0;
         ComposerKt.sourceInformationMarkerStart(var63, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var63, 0));
         CompositionLocalMap localMap$iv$ivx = var63.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivx = ComposedModifierKt.materializeModifier(var63, var180);
         Function0 var69 = ComposeUiNode.Companion.getConstructor();
         Composer var70 = var63;
         int var71 = 6 | 896 & var64 << 6;
         int $i$f$ReusableComposeNodex = 0;
         ComposerKt.sourceInformationMarkerStart(var70, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var70.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var70.startReusableNode();
         if (var70.getInserting()) {
            var70.createNode(var69);
         } else {
            var70.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivx = Updater.recovered$676aa17b44fd6280(var70);
         int var74/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, var181, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, localMap$iv$ivx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, compositeKeyHash$iv$ivx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivx, materialized$iv$ivx, ComposeUiNode.Companion.getSetModifier());
         int var75 = 14 & var71 >> 6;
         Composer $composer$ivx = var70;
         int var77 = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
         int var78 = 6 | 112 & var177 >> 6;
         Composer var79 = $composer$ivx;
         RowScope var80 = RowScopeInstance.INSTANCE;
         int var81 = 0;
         ComposerKt.sourceInformationMarkerStart(var79, -1567963422, "C101@4269L39,102@4321L1445,133@5779L40:TopActionBar.kt#arqjag");
         var79.startReplaceGroup(-1851697040);
         ComposerKt.sourceInformation(var79, "77@3249L335,75@3176L408");
         if (var5 != null && var6 != null) {
            IconButtonKt.IconButton(
               var5,
               null,
               false,
               null,
               null,
               null,
               ComposableLambdaKt.rememberComposableLambda(-1893533585, true, new TopActionBarKt$TopActionBar$3$2$1(var6, var10), var79, 54),
               var79,
               1572864 | 14 & var20 >> 15,
               62
            );
         }

         var79.endReplaceGroup();
         float var207;
         if (var5 != null && var6 != null) {
            int $this$dp$ivx = 0;
            int $i$f$getDpx = 0;
            var207 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         } else {
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var207 = Dp.recovered$95b3d103663bf060($this$dp$ivx);
         }

         float var84 = var207;
         if (var4.size() == 1) {
            var79.startReplaceGroup(-1567427712);
            ComposerKt.sourceInformation(var79, "88@3749L53,89@3819L176");
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(Modifier.Companion, var84), var79, 0);
            AvatarWrapper var183 = (AvatarWrapper)CollectionsKt.first(var4);
            var204 = Modifier.Companion;
            int $this$dp$ivx = 32;
            int $i$f$getDpx = 0;
            AvatarIconKt.recovered$f5ec3c81e3146789(
               SizeKt.recovered$111ef94c93e78c04(var204, Dp.recovered$95b3d103663bf060($this$dp$ivx)),
               var183,
               null,
               var7,
               0L,
               null,
               var79,
               6 | 7168 & var20 >> 12,
               52
            );
            var79.endReplaceGroup();
         } else if (var4.size() > 1) {
            var79.startReplaceGroup(-1567125989);
            ComposerKt.sourceInformation(var79, "95@4055L53,96@4125L117");
            SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(Modifier.Companion, var84), var79, 0);
            List var209 = CollectionsKt.take(var4, 3);
            int $this$dp$ivx = 32;
            int $i$f$getDpx = 0;
            AvatarGroupKt.recovered$813c81fac421be56(var209, null, Dp.recovered$95b3d103663bf060($this$dp$ivx), 0L, var79, 384, 10);
            var79.endReplaceGroup();
         } else {
            var79.startReplaceGroup(-1566915530);
            var79.endReplaceGroup();
         }

         var204 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var204, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var79, 6);
         Modifier var186 = RowScope.weight$default(var80, Modifier.Companion, 1.0F, false, 2, null);
         Horizontal var193 = Alignment.Companion.getStart();
         int $changed$ivx = 384;
         int var87 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var79, 1341605231, "CC(Column)N(modifier,verticalArrangement,horizontalAlignment,content)87@4443L61,88@4509L134:Column.kt#2w3rfo"
         );
         Vertical verticalArrangement$ivx = Arrangement.INSTANCE.getTop();
         MeasurePolicy measurePolicy$ivxx = ColumnKt.columnMeasurePolicy(
            verticalArrangement$ivx, var193, var79, 14 & $changed$ivx >> 3 | 112 & $changed$ivx >> 3
         );
         Modifier var89 = var186;
         MeasurePolicy var90 = measurePolicy$ivxx;
         Composer var91 = var79;
         int var92 = 112 & $changed$ivx << 3;
         int $i$f$Layoutxx = 0;
         ComposerKt.sourceInformationMarkerStart(var91, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int compositeKeyHash$iv$ivxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var91, 0));
         CompositionLocalMap localMap$iv$ivxx = var91.getCurrentCompositionLocalMap();
         Modifier materialized$iv$ivxx = ComposedModifierKt.materializeModifier(var91, var89);
         Function0 var97 = ComposeUiNode.Companion.getConstructor();
         Composer var98 = var91;
         int var99 = 6 | 896 & var92 << 6;
         int $i$f$ReusableComposeNodexx = 0;
         ComposerKt.sourceInformationMarkerStart(var98, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var98.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var98.startReusableNode();
         if (var98.getInserting()) {
            var98.createNode(var97);
         } else {
            var98.useNode();
         }

         Composer $this$Layout_u24lambda_u240$iv$ivxx = Updater.recovered$676aa17b44fd6280(var98);
         int var102/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxx */ = 0;
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, var90, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, localMap$iv$ivxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, compositeKeyHash$iv$ivxx, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxx, materialized$iv$ivxx, ComposeUiNode.Companion.getSetModifier());
         int var103 = 14 & var99 >> 6;
         Composer $composer$ivxx = var98;
         int var105/* $VF was: $i$a$-Layout-ColumnKt$Column$1$ivx */ = 0;
         ComposerKt.sourceInformationMarkerStart($composer$ivxx, 2093002350, "C89@4557L9:Column.kt#2w3rfo");
         int var106 = 6 | 112 & $changed$ivx >> 6;
         Composer var107 = $composer$ivxx;
         ColumnScope var108 = ColumnScopeInstance.INSTANCE;
         int var109 = 0;
         ComposerKt.sourceInformationMarkerStart(var107, -1078581855, "C105@4499L10,103@4417L332:TopActionBar.kt#arqjag");
         TextStyle var110 = TextStyle.recovered$cf01d8b71690b5c5(
            IntercomTheme.INSTANCE.getTypography(var107, IntercomTheme.$stable).getType04SemiBold(),
            var10,
            0L,
            null,
            null,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            null,
            null,
            0,
            0,
            0L,
            null,
            null,
            null,
            0,
            0,
            null,
            16777214,
            null
         );
         int var111 = TextAlign.Companion.recovered$4fce64874a0d5cf7();
         int var112 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
         TextKt.recovered$5b114ab32a4f4190(
            var1,
            null,
            var10,
            null,
            0L,
            null,
            null,
            null,
            0L,
            null,
            TextAlign.recovered$18ab2fc4c81b19cd(var111),
            0L,
            var112,
            false,
            1,
            0,
            null,
            var110,
            var107,
            14 & var20 >> 3 | 896 & var20 >> 21,
            24960,
            109562
         );
         var107.startReplaceGroup(1212143845);
         ComposerKt.sourceInformation(var107, "112@4819L915");
         CharSequence var195 = var2;
         if (var195 != null && var195.length() != 0) {
            androidx.compose.ui.Alignment.Vertical var198 = Alignment.Companion.getCenterVertically();
            int $changed$ivxx = 384;
            int var114 = 0;
            ComposerKt.sourceInformationMarkerStart(
               var107, 844473419, "CC(Row)N(modifier,horizontalArrangement,verticalAlignment,content)99@5125L58,100@5188L131:Row.kt#2w3rfo"
            );
            Modifier modifier$ivx = Modifier.Companion;
            androidx.compose.foundation.layout.Arrangement.Horizontal horizontalArrangement$ivx = Arrangement.INSTANCE.getStart();
            MeasurePolicy measurePolicy$ivxxx = RowKt.rowMeasurePolicy(
               horizontalArrangement$ivx, var198, var107, 14 & $changed$ivxx >> 3 | 112 & $changed$ivxx >> 3
            );
            Modifier var116 = modifier$ivx;
            MeasurePolicy var117 = measurePolicy$ivxxx;
            Composer var118 = var107;
            int var119 = 112 & $changed$ivxx << 3;
            int $i$f$Layoutxxx = 0;
            ComposerKt.sourceInformationMarkerStart(var118, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
            int compositeKeyHash$iv$ivxxx = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var118, 0));
            CompositionLocalMap localMap$iv$ivxxx = var118.getCurrentCompositionLocalMap();
            Modifier materialized$iv$ivxxx = ComposedModifierKt.materializeModifier(var118, var116);
            Function0 var124 = ComposeUiNode.Companion.getConstructor();
            Composer var125 = var118;
            int var126 = 6 | 896 & var119 << 6;
            int $i$f$ReusableComposeNodexxx = 0;
            ComposerKt.sourceInformationMarkerStart(var125, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
            if (!(var125.getApplier() instanceof Applier)) {
               ComposablesKt.invalidApplier();
            }

            var125.startReusableNode();
            if (var125.getInserting()) {
               var125.createNode(var124);
            } else {
               var125.useNode();
            }

            Composer $this$Layout_u24lambda_u240$iv$ivxxx = Updater.recovered$676aa17b44fd6280(var125);
            int var129/* $VF was: $i$a$-ReusableComposeNode-LayoutKt$Layout$1$iv$ivxxx */ = 0;
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, var117, ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, localMap$iv$ivxxx, ComposeUiNode.Companion.getSetResolvedCompositionLocals()
            );
            Updater.recovered$89682b199a142880(
               $this$Layout_u24lambda_u240$iv$ivxxx, compositeKeyHash$iv$ivxxx, ComposeUiNode.Companion.getSetCompositeKeyHash()
            );
            Updater.recovered$3a70c9faca87a3d9($this$Layout_u24lambda_u240$iv$ivxxx, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
            Updater.recovered$89682b199a142880($this$Layout_u24lambda_u240$iv$ivxxx, materialized$iv$ivxxx, ComposeUiNode.Companion.getSetModifier());
            int var130 = 14 & var126 >> 6;
            Composer $composer$ivxxx = var125;
            int var132/* $VF was: $i$a$-Layout-RowKt$Row$1$ivx */ = 0;
            ComposerKt.sourceInformationMarkerStart($composer$ivxxx, 1456264949, "C101@5233L9:Row.kt#2w3rfo");
            int var133 = 6 | 112 & $changed$ivxx >> 6;
            Composer var134 = $composer$ivxxx;
            RowScope var135 = RowScopeInstance.INSTANCE;
            int var136 = 0;
            ComposerKt.sourceInformationMarkerStart(var134, 150974730, "C124@5456L10,122@5355L357:TopActionBar.kt#arqjag");
            var134.startReplaceGroup(559059533);
            ComposerKt.sourceInformation(var134, "*115@4993L34,114@4945L291,120@5265L39");
            if (var3 == null) {
               Object var211 = null;
            } else {
               int var137 = var3.intValue();
               int var138 = 0;
               Painter var139 = PainterResources_androidKt.painterResource(var3, var134, 14 & var20 >> 9);
               var204 = Modifier.Companion;
               int $this$dp$ivxx = 12;
               int $i$f$getDpxx = 0;
               Modifier var142 = SizeKt.recovered$111ef94c93e78c04(var204, Dp.recovered$95b3d103663bf060($this$dp$ivxx));
               IconKt.recovered$e00adffb7fe45b02(var139, null, var142, var12, var134, 432 | Painter.$stable | 7168 & var21 << 9, 0);
               var204 = Modifier.Companion;
               int $this$dp$ivxxx = 4;
               int $i$f$getDpxxx = 0;
               SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var204, Dp.recovered$95b3d103663bf060($this$dp$ivxxx)), var134, 6);
            }

            var134.endReplaceGroup();
            TextStyle var143 = IntercomTheme.INSTANCE.getTypography(var134, IntercomTheme.$stable).getType05();
            int var144 = TextAlign.Companion.recovered$4fce64874a0d5cf7();
            int var199 = TextOverflow.Companion.recovered$0ef33c02f8eeacd2();
            TextKt.recovered$5b114ab32a4f4190(
               var2,
               null,
               var12,
               null,
               0L,
               null,
               null,
               null,
               0L,
               null,
               TextAlign.recovered$18ab2fc4c81b19cd(var144),
               0L,
               var199,
               false,
               1,
               0,
               null,
               var143,
               var134,
               14 & var20 >> 6 | 896 & var21 << 6,
               24960,
               109562
            );
            ComposerKt.sourceInformationMarkerEnd(var134);
            ComposerKt.sourceInformationMarkerEnd($composer$ivxxx);
            var125.endNode();
            ComposerKt.sourceInformationMarkerEnd(var125);
            ComposerKt.sourceInformationMarkerEnd(var118);
            ComposerKt.sourceInformationMarkerEnd(var107);
         }

         var107.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var107);
         ComposerKt.sourceInformationMarkerEnd($composer$ivxx);
         var98.endNode();
         ComposerKt.sourceInformationMarkerEnd(var98);
         ComposerKt.sourceInformationMarkerEnd(var91);
         ComposerKt.sourceInformationMarkerEnd(var79);
         var204 = Modifier.Companion;
         $this$dp$ivx = 16;
         $i$f$getDpx = 0;
         SpacerKt.Spacer(SizeKt.recovered$4f2dac42b6e775a9(var204, Dp.recovered$95b3d103663bf060($this$dp$ivx)), var79, 6);
         var79.startReplaceGroup(-1851610184);
         ComposerKt.sourceInformation(var79, "134@5843L12");
         if (var15 == null) {
            Object var215 = null;
         } else {
            var15.invoke(var80, var79, 14 & var78 | 112 & var21 >> 3);
         }

         var79.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var79);
         ComposerKt.sourceInformationMarkerEnd($composer$ivx);
         var70.endNode();
         ComposerKt.sourceInformationMarkerEnd(var70);
         ComposerKt.sourceInformationMarkerEnd(var63);
         ComposerKt.sourceInformationMarkerEnd(var46);
         var46.startReplaceGroup(369375566);
         ComposerKt.sourceInformation(var46, "137@5912L17");
         if (!StringsKt.isBlank(var1)) {
            IntercomDividerKt.IntercomDivider(null, var46, 0, 1);
         }

         var46.endReplaceGroup();
         ComposerKt.sourceInformationMarkerEnd(var46);
         ComposerKt.sourceInformationMarkerEnd(var43);
         var37.endNode();
         ComposerKt.sourceInformationMarkerEnd(var37);
         ComposerKt.sourceInformationMarkerEnd(var165);
         ComposerKt.sourceInformationMarkerEnd(var16);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var216 = var16.endRestartGroup();
      if (var216 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_262_0 = var0;
         final java.lang.String recoveredCapture_262_1 = var1;
         final java.lang.String recoveredCapture_262_2 = var2;
         final java.lang.Integer recoveredCapture_262_3 = var3;
         final java.util.List recoveredCapture_262_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_262_5 = var5;
         final java.lang.Integer recoveredCapture_262_6 = var6;
         final boolean recoveredCapture_262_7 = var7;
         final long recoveredCapture_262_8 = var8;
         final long recoveredCapture_262_9 = var10;
         final long recoveredCapture_262_10 = var12;
         final kotlin.jvm.functions.Function0 recoveredCapture_262_11 = var14;
         final kotlin.jvm.functions.Function3 recoveredCapture_262_12 = var15;
         final int recoveredCapture_262_13 = var17;
         final int recoveredCapture_262_14 = var18;
         final int recoveredCapture_262_15 = var19;
         var216.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_262_0, recoveredArgument_262_1) -> TopActionBarKt.TopActionBar_NpQZenA$lambda$12(recoveredCapture_262_0, recoveredCapture_262_1, recoveredCapture_262_2, recoveredCapture_262_3, recoveredCapture_262_4, recoveredCapture_262_5, recoveredCapture_262_6, recoveredCapture_262_7, recoveredCapture_262_8, recoveredCapture_262_9, recoveredCapture_262_10, recoveredCapture_262_11, recoveredCapture_262_12, recoveredCapture_262_13, recoveredCapture_262_14, recoveredCapture_262_15, (androidx.compose.runtime.Composer)recoveredArgument_262_0, ((java.lang.Integer)recoveredArgument_262_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarAvatarPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-876643721);
      ComposerKt.sourceInformation(var0, "C(TopActionBarAvatarPreview)145@6026L252:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-876643721, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarAvatarPreview (TopActionBar.kt:144)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_254_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_254_0, recoveredArgument_254_1) -> TopActionBarKt.TopActionBarAvatarPreview$lambda$13(recoveredCapture_254_0, (androidx.compose.runtime.Composer)recoveredArgument_254_0, ((java.lang.Integer)recoveredArgument_254_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarGroupChatPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(86306281);
      ComposerKt.sourceInformation(var0, "C(TopActionBarGroupChatPreview)160@6362L288:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(86306281, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarGroupChatPreview (TopActionBar.kt:159)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_257_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_257_0, recoveredArgument_257_1) -> TopActionBarKt.TopActionBarGroupChatPreview$lambda$14(recoveredCapture_257_0, (androidx.compose.runtime.Composer)recoveredArgument_257_0, ((java.lang.Integer)recoveredArgument_257_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarTeamReplyTimePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(500144364);
      ComposerKt.sourceInformation(var0, "C(TopActionBarTeamReplyTimePreview)176@6738L476:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(500144364, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarTeamReplyTimePreview (TopActionBar.kt:175)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_259_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_259_0, recoveredArgument_259_1) -> TopActionBarKt.TopActionBarTeamReplyTimePreview$lambda$15(recoveredCapture_259_0, (androidx.compose.runtime.Composer)recoveredArgument_259_0, ((java.lang.Integer)recoveredArgument_259_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarMultipleAvatarsPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1436789380);
      ComposerKt.sourceInformation(var0, "C(TopActionBarMultipleAvatarsPreview)195@7304L380:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1436789380, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarMultipleAvatarsPreview (TopActionBar.kt:194)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_258_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_258_0, recoveredArgument_258_1) -> TopActionBarKt.TopActionBarMultipleAvatarsPreview$lambda$16(recoveredCapture_258_0, (androidx.compose.runtime.Composer)recoveredArgument_258_0, ((java.lang.Integer)recoveredArgument_258_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarAvatarNoBackButtonPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(943010993);
      ComposerKt.sourceInformation(var0, "C(TopActionBarAvatarNoBackButtonPreview)212@7777L222:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(943010993, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarAvatarNoBackButtonPreview (TopActionBar.kt:211)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_253_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_253_0, recoveredArgument_253_1) -> TopActionBarKt.TopActionBarAvatarNoBackButtonPreview$lambda$17(recoveredCapture_253_0, (androidx.compose.runtime.Composer)recoveredArgument_253_0, ((java.lang.Integer)recoveredArgument_253_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarBackButtonPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1611748073);
      ComposerKt.sourceInformation(var0, "C(TopActionBarBackButtonPreview)226@8084L78:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1611748073, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarBackButtonPreview (TopActionBar.kt:225)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$5a1850a8ff371bfb(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_255_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_255_0, recoveredArgument_255_1) -> TopActionBarKt.TopActionBarBackButtonPreview$lambda$18(recoveredCapture_255_0, (androidx.compose.runtime.Composer)recoveredArgument_255_0, ((java.lang.Integer)recoveredArgument_255_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarTitlePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1648201034);
      ComposerKt.sourceInformation(var0, "C(TopActionBarTitlePreview)234@8242L60:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1648201034, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarTitlePreview (TopActionBar.kt:233)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$3faf0e22fa74c6a7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_260_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_260_0, recoveredArgument_260_1) -> TopActionBarKt.TopActionBarTitlePreview$lambda$19(recoveredCapture_260_0, (androidx.compose.runtime.Composer)recoveredArgument_260_0, ((java.lang.Integer)recoveredArgument_260_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TopActionBarEndContentPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1992822204);
      ComposerKt.sourceInformation(var0, "C(TopActionBarEndContentPreview)242@8387L354:TopActionBar.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1992822204, var1, -1, "io.intercom.android.sdk.m5.components.TopActionBarEndContentPreview (TopActionBar.kt:241)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$TopActionBarKt.INSTANCE.recovered$abc66e26750555d7(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_256_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_256_0, recoveredArgument_256_1) -> TopActionBarKt.TopActionBarEndContentPreview$lambda$20(recoveredCapture_256_0, (androidx.compose.runtime.Composer)recoveredArgument_256_0, ((java.lang.Integer)recoveredArgument_256_1).intValue())));
      }
   }

   private static final Unit TopActionBar_NpQZenA$lambda$1$lambda$0() {
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBar_NpQZenA$lambda$4$lambda$3$lambda$2(Window var0, long var1) {
      WindowCompat.getInsetsController(var0, var0.getDecorView()).setAppearanceLightStatusBars(ColorExtensionsKt.recovered$41e561cae61044c5(var1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBar_NpQZenA$lambda$11$lambda$6$lambda$5(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBar_NpQZenA$lambda$12(
      Modifier var0,
      String var1,
      String var2,
      Integer var3,
      List var4,
      Function0 var5,
      Integer var6,
      boolean var7,
      long var8,
      long var10,
      long var12,
      Function0 var14,
      Function3 var15,
      int var16,
      int var17,
      int var18,
      Composer var19,
      int var20
   ) {
      recovered$e7704aa1c1b2200b(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var10,
         var12,
         var14,
         var15,
         var19,
         RecomposeScopeImplKt.updateChangedFlags(var16 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var17),
         var18
      );
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarAvatarPreview$lambda$13(int var0, Composer var1, int var2) {
      TopActionBarAvatarPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarGroupChatPreview$lambda$14(int var0, Composer var1, int var2) {
      TopActionBarGroupChatPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarTeamReplyTimePreview$lambda$15(int var0, Composer var1, int var2) {
      TopActionBarTeamReplyTimePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarMultipleAvatarsPreview$lambda$16(int var0, Composer var1, int var2) {
      TopActionBarMultipleAvatarsPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarAvatarNoBackButtonPreview$lambda$17(int var0, Composer var1, int var2) {
      TopActionBarAvatarNoBackButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarBackButtonPreview$lambda$18(int var0, Composer var1, int var2) {
      TopActionBarBackButtonPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarTitlePreview$lambda$19(int var0, Composer var1, int var2) {
      TopActionBarTitlePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TopActionBarEndContentPreview$lambda$20(int var0, Composer var1, int var2) {
      TopActionBarEndContentPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
