package io.intercom.android.sdk.survey.block;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.runtime.saveable.RememberSaveableKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.blocks.CarouselImageClickListener;
import io.intercom.android.sdk.blocks.LinkOpeningButtonClickListener;
import io.intercom.android.sdk.blocks.UploadingImageCache;
import io.intercom.android.sdk.blocks.ViewHolderGenerator;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.Blocks;
import io.intercom.android.sdk.blocks.lib.models.Block;
import io.intercom.android.sdk.blocks.lib.models.TicketType;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.ConfigurableIntercomThemeKt;
import io.intercom.android.sdk.m5.home.ui.components.LegacyMessengerAppCardKt;
import io.intercom.android.sdk.tickets.create.ui.CreateTicketCardKt;
import io.intercom.android.sdk.ui.component.IntercomCardStyle;
import io.intercom.android.sdk.ui.component.IntercomCardStyle.Style;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000p\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aÛ\u0001\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u00072\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\u0010\b\u0002\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00122\u0016\b\u0002\u0010\u0013\u001a\u0010\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u00142\b\b\u0002\u0010\u0016\u001a\u00020\u00072\u001a\b\u0002\u0010\u0017\u001a\u0014\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00190\u0018\u0012\u0004\u0012\u00020\u00010\u00142\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\b\b\u0002\u0010\u001c\u001a\u00020\u00072\u0016\b\u0002\u0010\u001d\u001a\u0010\u0012\u0004\u0012\u00020\u001e\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0014H\u0001¢\u0006\u0004\b\u001f\u0010 \u001a3\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020#2\u0006\u0010$\u001a\u00020\u00102\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0001¢\u0006\u0004\b%\u0010&\u001a\u001f\u0010'\u001a\u00020\u00012\u0006\u0010(\u001a\u00020)2\u0006\u0010$\u001a\u00020\u0010H\u0002¢\u0006\u0004\b*\u0010+¨\u0006,²\u0006\n\u0010-\u001a\u00020\u0007X\u008a\u008e\u0002²\u0006\n\u0010-\u001a\u00020\u0007X\u008a\u008e\u0002",
   d2 = {
         "BlockView",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "blockRenderData",
         "Lio/intercom/android/sdk/survey/block/BlockRenderData;",
         "isAttachmentFromAdmin",
         "",
         "suffixText",
         "Lio/intercom/android/sdk/survey/block/SuffixText;",
         "enabled",
         "conversationId",
         "",
         "imageRenderType",
         "Lio/intercom/android/sdk/survey/block/ImageRenderType;",
         "imageBackgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "onClick",
         "Lkotlin/Function0;",
         "onCreateTicket",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/blocks/lib/models/TicketType;",
         "inlineSourcesVisible",
         "onInlineSourcesClick",
         "",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "codeBlockShape",
         "Landroidx/compose/ui/graphics/Shape;",
         "isTableStreaming",
         "onTextLayout",
         "Landroidx/compose/ui/text/TextLayoutResult;",
         "BlockView-7eQnmaA",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/survey/block/BlockRenderData;ZLio/intercom/android/sdk/survey/block/SuffixText;ZLjava/lang/String;Lio/intercom/android/sdk/survey/block/ImageRenderType;JLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;ZLkotlin/jvm/functions/Function1;Landroidx/compose/ui/graphics/Shape;ZLkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;III)V",
         "RenderLegacyBlocks",
         "block",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "textColor",
         "RenderLegacyBlocks-sW7UJKQ",
         "(Lio/intercom/android/sdk/blocks/lib/models/Block;JLandroidx/compose/ui/Modifier;Ljava/lang/String;Landroidx/compose/runtime/Composer;II)V",
         "updateTextColorRecursively",
         "view",
         "Landroid/view/View;",
         "updateTextColorRecursively-4WTKRHQ",
         "(Landroid/view/View;J)V",
         "intercom-sdk-base_release",
         "showExpanded"
   }
)
@SourceDebugExtension(
   "SMAP\nBlockView.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BlockView.kt\nio/intercom/android/sdk/survey/block/BlockViewKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 CompositionLocal.kt\nandroidx/compose/runtime/CompositionLocal\n+ 5 Color.kt\nandroidx/core/graphics/ColorKt\n+ 6 ViewGroup.kt\nandroidx/core/view/ViewGroupKt\n+ 7 SnapshotState.kt\nandroidx/compose/runtime/SnapshotStateKt__SnapshotStateKt\n*L\n1#1,247:1\n1047#2,6:248\n1047#2,6:254\n1047#2,6:260\n1047#2,6:266\n1047#2,6:272\n1047#2,6:279\n1047#2,6:285\n1047#2,6:291\n1047#2,6:298\n1#3:278\n75#4:297\n105#5:304\n114#5:305\n123#5:306\n55#6,4:307\n85#7:311\n117#7,2:312\n85#7:314\n117#7,2:315\n*S KotlinDebug\n*F\n+ 1 BlockView.kt\nio/intercom/android/sdk/survey/block/BlockViewKt\n*L\n56#1:248,6\n100#1:254,6\n122#1:260,6\n126#1:266,6\n132#1:272,6\n157#1:279,6\n164#1:285,6\n171#1:291,6\n217#1:298,6\n203#1:297\n234#1:304\n235#1:305\n236#1:306\n242#1:307,4\n122#1:311\n122#1:312,2\n157#1:314\n157#1:315,2\n*E\n"
)
public final class BlockViewKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$5709084683a88fd9(
      @Nullable Modifier var0,
      @NotNull BlockRenderData var1,
      boolean var2,
      @Nullable SuffixText var3,
      boolean var4,
      @Nullable String var5,
      @Nullable ImageRenderType var6,
      long var7,
      @Nullable Function0 var9,
      @Nullable Function1 var10,
      boolean var11,
      @Nullable Function1 var12,
      @Nullable Shape var13,
      boolean var14,
      @Nullable Function1 var15,
      @Nullable Composer var16,
      int var17,
      int var18,
      int var19
   ) {
      Intrinsics.checkNotNullParameter(var1, "blockRenderData");
      var16 = var16.startRestartGroup(364771452);
      ComposerKt.sourceInformation(var16, "C(BlockView)P(9!1,7,14,3,2,5,4:c#ui.graphics.Color,10,11,6,12)51@2284L6,55@2496L2:BlockView.kt#jxhu3x");
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
         var20 |= var16.changedInstance(var1) ? 32 : 16;
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
         var20 |= var16.changed(var4) ? 16384 : 8192;
      }

      if ((var19 & 32) != 0) {
         var20 |= 196608;
      } else if ((var17 & 196608) == 0) {
         var20 |= var16.changed(var5) ? 131072 : 65536;
      }

      if ((var19 & 64) != 0) {
         var20 |= 1572864;
      } else if ((var17 & 1572864) == 0) {
         var20 |= var16.changed(var6) ? 1048576 : 524288;
      }

      if ((var17 & 12582912) == 0) {
         var20 |= (var19 & 128) == 0 && var16.changed(var7) ? 8388608 : 4194304;
      }

      if ((var19 & 256) != 0) {
         var20 |= 100663296;
      } else if ((var17 & 100663296) == 0) {
         var20 |= var16.changedInstance(var9) ? 67108864 : 33554432;
      }

      if ((var19 & 512) != 0) {
         var20 |= 805306368;
      } else if ((var17 & 805306368) == 0) {
         var20 |= var16.changedInstance(var10) ? 536870912 : 268435456;
      }

      if ((var19 & 1024) != 0) {
         var21 |= 6;
      } else if ((var18 & 6) == 0) {
         var21 |= var16.changed(var11) ? 4 : 2;
      }

      if ((var19 & 2048) != 0) {
         var21 |= 48;
      } else if ((var18 & 48) == 0) {
         var21 |= var16.changedInstance(var12) ? 32 : 16;
      }

      if ((var19 & 4096) != 0) {
         var21 |= 384;
      } else if ((var18 & 384) == 0) {
         var21 |= var16.changed(var13) ? 256 : 128;
      }

      if ((var19 & 8192) != 0) {
         var21 |= 3072;
      } else if ((var18 & 3072) == 0) {
         var21 |= var16.changed(var14) ? 2048 : 1024;
      }

      if ((var19 & 16384) != 0) {
         var21 |= 24576;
      } else if ((var18 & 24576) == 0) {
         var21 |= var16.changedInstance(var15) ? 16384 : 8192;
      }

      if ((var20 & 306783379) == 306783378 && (var21 & 9363) == 9362 && var16.getSkipping()) {
         var16.skipToGroupEnd();
      } else {
         var16.startDefaults();
         if ((var17 & 1) != 0 && !var16.getDefaultsInvalid()) {
            var16.skipToGroupEnd();
            if ((var19 & 128) != 0) {
               var20 &= -29360129;
            }
         } else {
            if ((var19 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var19 & 4) != 0) {
               var2 = false;
            }

            if ((var19 & 8) != 0) {
               var3 = SuffixText.Companion.getNO_SUFFIX();
            }

            if ((var19 & 16) != 0) {
               var4 = true;
            }

            if ((var19 & 32) != 0) {
               var5 = "";
            }

            if ((var19 & 64) != 0) {
               var6 = ImageRenderType.CROPPED;
            }

            if ((var19 & 128) != 0) {
               var7 = IntercomTheme.INSTANCE.getColors(var16, IntercomTheme.$stable).recovered$f6e7a825ec69c94c();
               var20 &= -29360129;
            }

            if ((var19 & 256) != 0) {
               var9 = null;
            }

            if ((var19 & 512) != 0) {
               var10 = null;
            }

            if ((var19 & 1024) != 0) {
               var11 = false;
            }

            if ((var19 & 2048) != 0) {
               var16.startReplaceGroup(-1208249485);
               ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
               Composer var23 = var16;
               boolean var24 = false;
               int var25 = 0;
               Object var26 = var23.rememberedValue();
               int var27 = 0;
               Object var10000;
               if (var26 == Composer.Companion.getEmpty()) {
                  int var28 = 0;
                  Object var48 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1236_0) -> BlockViewKt.BlockView_7eQnmaA$lambda$1$lambda$0((java.util.List)recoveredArgument_1236_0));
                  var23.updateRememberedValue(var48);
                  var10000 = var48;
               } else {
                  var10000 = var26;
               }

               Function1 var22 = (Function1)var10000;
               var16.endReplaceGroup();
               var12 = var22;
            }

            if ((var19 & 4096) != 0) {
               var13 = null;
            }

            if ((var19 & 8192) != 0) {
               var14 = false;
            }

            if ((var19 & 16384) != 0) {
               var15 = null;
            }
         }

         var16.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(364771452, var20, var21, "io.intercom.android.sdk.survey.block.BlockView (BlockView.kt:59)");
         }

         Color var120 = var1.recovered$1b39cf3894296c39();
         long var45 = var120 != null ? var120.recovered$45f9de7b85e0e0b5() : Color.Companion.recovered$235b30c090a399c5();
         Block var46 = var1.getBlock();
         if (!Injector.isNotInitialised() && ((AppConfig)Injector.get().getAppConfigProvider().get()).hasFeature("new-block-rendering-killswitch")) {
            var16.startReplaceGroup(1199362355);
            ComposerKt.sourceInformation(var16, "66@2898L46");
            recovered$3dc71cc6f7fc8f68(var46, var45, var0, null, var16, 896 & var20 << 6, 8);
            var16.endReplaceGroup();
         } else {
            var16.startReplaceGroup(1199586392);
            ComposerKt.sourceInformation(var16, "68@3006L344");
            Function2 var47 = ComposableLambdaKt.rememberComposableLambda(
               -1601042320, true, new BlockViewKt$BlockView$textBlock$1(var0, var1, var3, var11, var12, var15), var16, 54
            );
            BlockType var121 = var46.getType();
            switch (var121 == null ? -1 : BlockViewKt$WhenMappings.$EnumSwitchMapping$0[var121.ordinal()]) {
               case 1:
                  var16.startReplaceGroup(-1208220044);
                  ComposerKt.sourceInformation(var16, "79@3410L195");
                  ImageBlockKt.recovered$5b15bdef32cfadf4(
                     var46, var0, null, false, var6, var7, var16, 112 & var20 << 3 | 57344 & var20 >> 6 | 458752 & var20 >> 6, 12
                  );
                  var16.endReplaceGroup();
                  break;
               case 2:
               case 3:
               case 4:
               case 5:
               case 6:
                  var16.startReplaceGroup(-1208208004);
                  ComposerKt.sourceInformation(var16, "91@3792L11");
                  var47.invoke(var16, 6);
                  var16.endReplaceGroup();
                  break;
               case 7:
                  var16.startReplaceGroup(1200329741);
                  ComposerKt.sourceInformation(var16, "99@4144L250,95@3947L466");
                  boolean var52 = var4 && !var46.getTicketType().getArchived();
                  Modifier var131 = Modifier.Companion;
                  BlockRenderData var144 = var1;
                  boolean var146 = var52;
                  var16.startReplaceGroup(-1208196501);
                  ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                  Composer var70 = var16;
                  boolean var76 = var16.changed(var52) | (var20 & 1879048192) == 536870912 | var16.changedInstance(var46) | (var20 & 234881024) == 67108864;
                  int var82 = 0;
                  Object var88 = var70.rememberedValue();
                  int var94 = 0;
                  Object var10003;
                  if (!var76 && var88 != Composer.Companion.getEmpty()) {
                     var10003 = var88;
                  } else {
                     boolean var118 = var52;
                     BlockRenderData var117 = var1;
                     Modifier var114 = var131;
                     int var102 = 0;
                     final boolean recoveredCapture_1228_0 = var52;
                     final kotlin.jvm.functions.Function1 recoveredCapture_1228_1 = var10;
                     final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_1228_2 = var46;
                     final kotlin.jvm.functions.Function0 recoveredCapture_1228_3 = var9;
                     Function0 var40 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$3$lambda$2(recoveredCapture_1228_0, recoveredCapture_1228_1, recoveredCapture_1228_2, recoveredCapture_1228_3));
                     var131 = var114;
                     var144 = var117;
                     var146 = var118;
                     Object var106 = var40;
                     var70.updateRememberedValue(var106);
                     var10003 = var106;
                  }

                  Function0 var62 = (Function0)var10003;
                  var16.endReplaceGroup();
                  CreateTicketCardKt.CreateTicketCard(var131, var144, var146, var62, var16, 6 | 112 & var20, 0);
                  var16.endReplaceGroup();
                  break;
               case 8:
                  var16.startReplaceGroup(1200939573);
                  ComposerKt.sourceInformation(var16, "");
                  String var129 = var46.getFallbackUrl();
                  Intrinsics.checkNotNullExpressionValue(var129, "getFallbackUrl(...)");
                  if (var129.length() > 0) {
                     var16.startReplaceGroup(1200989266);
                     ComposerKt.sourceInformation(var16, "113@4667L23,111@4544L215");
                     String var130 = var46.getFallbackUrl();
                     Intrinsics.checkNotNullExpressionValue(var130, "getFallbackUrl(...)");
                     LegacyMessengerAppCardKt.LegacyMessengerAppCard(
                        var130,
                        IntercomCardStyle.INSTANCE.recovered$b8822edb5fda39fd(null, 0L, 0L, 0.0F, null, var16, IntercomCardStyle.$stable << 15, 31),
                        false,
                        var16,
                        384 | Style.$stable << 3
                     );
                     var16.endReplaceGroup();
                  } else {
                     var16.startReplaceGroup(1201241854);
                     ComposerKt.sourceInformation(var16, "117@4805L11");
                     var47.invoke(var16, 6);
                     var16.endReplaceGroup();
                  }

                  var16.endReplaceGroup();
                  break;
               case 9:
                  var16.startReplaceGroup(1201353671);
                  ComposerKt.sourceInformation(var16, "121@4946L25,121@4917L54,125@5142L23,122@4988L241,131@5386L24,129@5286L147");
                  Object[] var58 = new Object[]{var46.getText()};
                  Object[] var126 = var58;
                  var16.startReplaceGroup(-1208171062);
                  ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                  Composer var67 = var16;
                  boolean var73 = false;
                  int var79 = 0;
                  Object var85 = var67.rememberedValue();
                  int var91 = 0;
                  Object var140;
                  if (var85 == Composer.Companion.getEmpty()) {
                     Object[] var111 = var58;
                     int var98 = 0;
                     var140 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$5$lambda$4());
                     var126 = var111;
                     Object var99 = var140;
                     var67.updateRememberedValue(var99);
                     var140 = (Function0)var99;
                  } else {
                     var140 = (Function0)var85;
                  }

                  Function0 var59 = (kotlin.jvm.functions.Function0) var140;
                  var16.endReplaceGroup();
                  MutableState var51 = (MutableState)RememberSaveableKt.rememberSaveable(var126, var59, var16, 48);
                  Block var127 = var46;
                  Modifier var141 = var0;
                  var16.startReplaceGroup(-1208164792);
                  ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                  var67 = var16;
                  var73 = var16.changed(var51);
                  var79 = 0;
                  var85 = var67.rememberedValue();
                  var91 = 0;
                  Object var145;
                  if (!var73 && var85 != Composer.Companion.getEmpty()) {
                     var145 = var85;
                  } else {
                     Modifier var116 = var0;
                     Block var112 = var46;
                     int var100 = 0;
                     final androidx.compose.runtime.MutableState recoveredCapture_1230_0 = var51;
                     Function0 var119 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$9$lambda$8(recoveredCapture_1230_0));
                     var127 = var112;
                     var141 = var116;
                     Object var104 = var119;
                     var67.updateRememberedValue(var104);
                     var145 = var104;
                  }

                  Function0 var60 = (Function0)var145;
                  var16.endReplaceGroup();
                  Function0 var42 = var60;
                  EnhancedCodeBlockKt.EnhancedCodeBlock(var127, var141, new CodeBlockMode$Inline(var42), var13, var16, 112 & var20 << 3 | 7168 & var21 << 3, 0);
                  if (BlockView_7eQnmaA$lambda$6(var51)) {
                     Block var128 = var46;
                     var16.startReplaceGroup(-1208156983);
                     ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                     var67 = var16;
                     var73 = var16.changed(var51);
                     var79 = 0;
                     var85 = var67.rememberedValue();
                     var91 = 0;
                     if (!var73 && var85 != Composer.Companion.getEmpty()) {
                        var140 = (Function0)var85;
                     } else {
                        Block var113 = var46;
                        int var101 = 0;
                        final androidx.compose.runtime.MutableState recoveredCapture_1231_0 = var51;
                        var140 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$11$lambda$10(recoveredCapture_1231_0));
                        var128 = var113;
                        Object var105 = var140;
                        var67.updateRememberedValue(var105);
                        var140 = (Function0)var105;
                     }

                     Function0 var61 = (kotlin.jvm.functions.Function0) var140;
                     var16.endReplaceGroup();
                     EnhancedCodeBlockKt.ExpandedCodeBlockDialog(var128, var61, var16, 0);
                  }

                  var16.endReplaceGroup();
                  break;
               case 10:
                  var16.startReplaceGroup(-1208153102);
                  ComposerKt.sourceInformation(var16, "135@5506L65");
                  AttachmentBlockKt.AttachmentBlock(var0, var1, var2, var16, 14 & var20 | 112 & var20 | 896 & var20, 0);
                  var16.endReplaceGroup();
                  break;
               case 11:
                  var16.startReplaceGroup(-1208149646);
                  ComposerKt.sourceInformation(var16, "136@5614L65");
                  AttachmentBlockKt.AttachmentBlock(var0, var1, var2, var16, 14 & var20 | 112 & var20 | 896 & var20, 0);
                  var16.endReplaceGroup();
                  break;
               case 12:
                  var16.startReplaceGroup(-1208146014);
                  ComposerKt.sourceInformation(var16, "137@5724L177");
                  ConversationRatingBlockKt.recovered$cde28140c86f70af(null, var1, var45, var5, var16, 112 & var20 | 7168 & var20 >> 6, 1);
                  var16.endReplaceGroup();
                  break;
               case 13:
                  var16.startReplaceGroup(-1208139228);
                  ComposerKt.sourceInformation(var16, "143@5937L147");
                  LinkListBlockKt.recovered$82efcc70bb4f4526(null, var46, var45, var5, var16, 7168 & var20 >> 6, 1);
                  var16.endReplaceGroup();
                  break;
               case 14:
                  var16.startReplaceGroup(-1208133278);
                  ComposerKt.sourceInformation(var16, "149@6121L209");
                  Modifier var125 = var0;
                  CharSequence var56 = var46.getUrl();
                  CharSequence var137;
                  if (var56.length() == 0) {
                     Modifier var109 = var0;
                     int var65 = 0;
                     var137 = var46.getLocalUri().toString();
                     var125 = var109;
                  } else {
                     var137 = var56;
                  }

                  Intrinsics.checkNotNullExpressionValue(var137, "ifEmpty(...)");
                  String recoveredVideoUrl = (String)var137;
                  String var50 = var46.getThumbnailUrl();
                  var56 = var50;
                  String var115 = recoveredVideoUrl;
                  Modifier var110 = var125;
                  int var66 = 0;
                  Intrinsics.checkNotNull(var56);
                  boolean var39 = var56.length() > 0;
                  VideoFileBlockKt.VideoFileBlock(var110, var115, var39 ? var50 : null, var16, 14 & var20, 0);
                  var16.endReplaceGroup();
                  break;
               case 15:
                  var16.startReplaceGroup(1202839098);
                  ComposerKt.sourceInformation(var16, "156@6549L25,156@6517L57,163@7033L23,157@6591L581,170@7330L24,168@7229L148");
                  if (ConfigurableIntercomThemeKt.getFinTableBlocksEnabled()) {
                     Object[] var29 = new Object[]{var46.getCaption()};
                     Object[] var122 = var29;
                     var16.startReplaceGroup(-1208119766);
                     ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                     Composer var30 = var16;
                     boolean var31 = false;
                     int var32 = 0;
                     Object var33 = var30.rememberedValue();
                     int var34 = 0;
                     kotlin.jvm.functions.Function0 var133;
                     if (var33 == Composer.Companion.getEmpty()) {
                        Object[] var37 = var29;
                        int var35 = 0;
                        var133 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$15$lambda$14());
                        var122 = var37;
                        Object var95 = var133;
                        var30.updateRememberedValue(var95);
                        var133 = (Function0)var95;
                     } else {
                        var133 = (Function0)var33;
                     }

                     Function0 var53 = var133;
                     var16.endReplaceGroup();
                     MutableState var49 = (MutableState)RememberSaveableKt.rememberSaveable(var122, var53, var16, 48);
                     Block var123 = var46;
                     Modifier var134 = var0;
                     var16.startReplaceGroup(-1208104280);
                     ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                     var30 = var16;
                     var31 = var16.changed(var49);
                     var32 = 0;
                     var33 = var30.rememberedValue();
                     var34 = 0;
                     Object var10002;
                     if (!var31 && var33 != Composer.Companion.getEmpty()) {
                        var10002 = var33;
                     } else {
                        Modifier var38 = var0;
                        Block var107 = var46;
                        int var96 = 0;
                        final androidx.compose.runtime.MutableState recoveredCapture_1233_0 = var49;
                        Function0 var41 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$19$lambda$18(recoveredCapture_1233_0));
                        var123 = var107;
                        var134 = var38;
                        Object var36 = var41;
                        var30.updateRememberedValue(var36);
                        var10002 = var36;
                     }

                     Function0 var54 = (Function0)var10002;
                     var16.endReplaceGroup();
                     Function0 var43 = var54;
                     TableBlockKt.TableBlock(
                        var123, var134, new TableBlockMode$Inline(var43), var13, var14, var16, 112 & var20 << 3 | 7168 & var21 << 3 | 57344 & var21 << 3, 0
                     );
                     if (BlockView_7eQnmaA$lambda$16(var49)) {
                        Block var124 = var46;
                        var16.startReplaceGroup(-1208094775);
                        ComposerKt.sourceInformation(var16, "CC(remember):BlockView.kt#9igjgp");
                        var30 = var16;
                        var31 = var16.changed(var49);
                        var32 = 0;
                        var33 = var30.rememberedValue();
                        var34 = 0;
                        if (!var31 && var33 != Composer.Companion.getEmpty()) {
                           var133 = (Function0)var33;
                        } else {
                           Block var108 = var46;
                           int var97 = 0;
                           final androidx.compose.runtime.MutableState recoveredCapture_1234_0 = var49;
                           var133 = ((kotlin.jvm.functions.Function0) () -> BlockViewKt.BlockView_7eQnmaA$lambda$21$lambda$20(recoveredCapture_1234_0));
                           var124 = var108;
                           Object var103 = var133;
                           var30.updateRememberedValue(var103);
                           var133 = (Function0)var103;
                        }

                        Function0 var55 = var133;
                        var16.endReplaceGroup();
                        TableBlockKt.ExpandedTableBlockDialog(var124, var55, var16, 0);
                     }
                  }

                  var16.endReplaceGroup();
                  break;
               default:
                  var16.startReplaceGroup(1204270771);
                  ComposerKt.sourceInformation(var16, "185@7883L156");
                  if (!Injector.isNotInitialised()) {
                     recovered$3dc71cc6f7fc8f68(var46, var45, var0, null, var16, 896 & var20 << 6, 8);
                  }

                  var16.endReplaceGroup();
            }

            var16.endReplaceGroup();
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var132 = var16.endRestartGroup();
      if (var132 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_1235_0 = var0;
         final io.intercom.android.sdk.survey.block.BlockRenderData recoveredCapture_1235_1 = var1;
         final boolean recoveredCapture_1235_2 = var2;
         final io.intercom.android.sdk.survey.block.SuffixText recoveredCapture_1235_3 = var3;
         final boolean recoveredCapture_1235_4 = var4;
         final java.lang.String recoveredCapture_1235_5 = var5;
         final io.intercom.android.sdk.survey.block.ImageRenderType recoveredCapture_1235_6 = var6;
         final long recoveredCapture_1235_7 = var7;
         final kotlin.jvm.functions.Function0 recoveredCapture_1235_8 = var9;
         final kotlin.jvm.functions.Function1 recoveredCapture_1235_9 = var10;
         final boolean recoveredCapture_1235_10 = var11;
         final kotlin.jvm.functions.Function1 recoveredCapture_1235_11 = var12;
         final androidx.compose.ui.graphics.Shape recoveredCapture_1235_12 = var13;
         final boolean recoveredCapture_1235_13 = var14;
         final kotlin.jvm.functions.Function1 recoveredCapture_1235_14 = var15;
         final int recoveredCapture_1235_15 = var17;
         final int recoveredCapture_1235_16 = var18;
         final int recoveredCapture_1235_17 = var19;
         var132.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1235_0, recoveredArgument_1235_1) -> BlockViewKt.BlockView_7eQnmaA$lambda$22(recoveredCapture_1235_0, recoveredCapture_1235_1, recoveredCapture_1235_2, recoveredCapture_1235_3, recoveredCapture_1235_4, recoveredCapture_1235_5, recoveredCapture_1235_6, recoveredCapture_1235_7, recoveredCapture_1235_8, recoveredCapture_1235_9, recoveredCapture_1235_10, recoveredCapture_1235_11, recoveredCapture_1235_12, recoveredCapture_1235_13, recoveredCapture_1235_14, recoveredCapture_1235_15, recoveredCapture_1235_16, recoveredCapture_1235_17, (androidx.compose.runtime.Composer)recoveredArgument_1235_0, ((java.lang.Integer)recoveredArgument_1235_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$3dc71cc6f7fc8f68(
      @NotNull Block var0, long var1, @Nullable Modifier var3, @Nullable String var4, @Nullable Composer var5, int var6, int var7
   ) {
      Intrinsics.checkNotNullParameter(var0, "block");
      var5 = var5.startRestartGroup(-119170784);
      ComposerKt.sourceInformation(var5, "C(RenderLegacyBlocks)P(!1,3:c#ui.graphics.Color,2)202@8268L7,216@8763L282,214@8703L349:BlockView.kt#jxhu3x");
      int var8 = var6;
      if ((var7 & 1) != 0) {
         var8 |= 6;
      } else if ((var6 & 6) == 0) {
         var8 |= var5.changedInstance(var0) ? 4 : 2;
      }

      if ((var7 & 2) != 0) {
         var8 |= 48;
      } else if ((var6 & 48) == 0) {
         var8 |= var5.changed(var1) ? 32 : 16;
      }

      if ((var7 & 4) != 0) {
         var8 |= 384;
      } else if ((var6 & 384) == 0) {
         var8 |= var5.changed(var3) ? 256 : 128;
      }

      if ((var7 & 8) != 0) {
         var8 |= 3072;
      } else if ((var6 & 3072) == 0) {
         var8 |= var5.changed(var4) ? 2048 : 1024;
      }

      if ((var8 & 1171) == 1170 && var5.getSkipping()) {
         var5.skipToGroupEnd();
      } else {
         if ((var7 & 4) != 0) {
            var3 = Modifier.Companion;
         }

         if ((var7 & 8) != 0) {
            var4 = "";
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-119170784, var8, -1, "io.intercom.android.sdk.survey.block.RenderLegacyBlocks (BlockView.kt:201)");
         }

         CompositionLocal var10 = AndroidCompositionLocals_androidKt.getLocalContext();
         int var11 = 0;
         int var12 = 0;
         ComposerKt.sourceInformationMarkerStart(var5, 2023513938, "CC(<get-current>):CompositionLocal.kt#9igjgp");
         Object var13 = var5.consume(var10);
         ComposerKt.sourceInformationMarkerEnd(var5);
         Context var9 = (Context)var13;
         Blocks var21 = new Blocks(var9, LumberMill.getBlocksTwig());
         UploadingImageCache var10002 = new UploadingImageCache();
         Api var10003 = Injector.get().getApi();
         Provider var10004 = Injector.get().getAppConfigProvider();
         Api var10008 = Injector.get().getApi();
         Intrinsics.checkNotNullExpressionValue(var10008, "getApi(...)");
         ViewHolderGenerator var22 = new ViewHolderGenerator(
            var10002,
            var10003,
            var10004,
            var4,
            new CarouselImageClickListener(var10008),
            new LinkOpeningButtonClickListener(Injector.get().getApi()),
            Injector.get().getGson(),
            Injector.get().getMetricTracker()
         );
         var5.startReplaceGroup(1148652128);
         ComposerKt.sourceInformation(var5, "CC(remember):BlockView.kt#9igjgp");
         Composer var24 = var5;
         boolean var14 = var5.changedInstance(var21) | var5.changedInstance(var0) | var5.changedInstance(var22) | (var8 & 112) == 32;
         int var15 = 0;
         Object var16 = var24.rememberedValue();
         int var17 = 0;
         Object var10000;
         if (!var14 && var16 != Composer.Companion.getEmpty()) {
            var10000 = var16;
         } else {
            int var18 = 0;
            final io.intercom.android.sdk.blocks.lib.Blocks recoveredCapture_1226_0 = var21;
            final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_1226_1 = var0;
            final io.intercom.android.sdk.blocks.ViewHolderGenerator recoveredCapture_1226_2 = var22;
            final long recoveredCapture_1226_3 = var1;
            Object var19 = ((kotlin.jvm.functions.Function1) (recoveredArgument_1226_0) -> BlockViewKt.RenderLegacyBlocks_sW7UJKQ$lambda$24$lambda$23(recoveredCapture_1226_0, recoveredCapture_1226_1, recoveredCapture_1226_2, recoveredCapture_1226_3, (android.content.Context)recoveredArgument_1226_0));
            var24.updateRememberedValue(var19);
            var10000 = var19;
         }

         Function1 var23 = (Function1)var10000;
         var5.endReplaceGroup();
         AndroidView_androidKt.AndroidView(var23, var3, null, var5, 112 & var8 >> 3, 4);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var25 = var5.endRestartGroup();
      if (var25 != null) {
         final io.intercom.android.sdk.blocks.lib.models.Block recoveredCapture_1227_0 = var0;
         final long recoveredCapture_1227_1 = var1;
         final androidx.compose.ui.Modifier recoveredCapture_1227_2 = var3;
         final java.lang.String recoveredCapture_1227_3 = var4;
         final int recoveredCapture_1227_4 = var6;
         final int recoveredCapture_1227_5 = var7;
         var25.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_1227_0, recoveredArgument_1227_1) -> BlockViewKt.RenderLegacyBlocks_sW7UJKQ$lambda$25(recoveredCapture_1227_0, recoveredCapture_1227_1, recoveredCapture_1227_2, recoveredCapture_1227_3, recoveredCapture_1227_4, recoveredCapture_1227_5, (androidx.compose.runtime.Composer)recoveredArgument_1227_0, ((java.lang.Integer)recoveredArgument_1227_1).intValue())));
      }
   }

   private static final void recovered$d9292e173c7aa98b(View var0, long var1) {
      if (var0 instanceof TextView && !(var0 instanceof Button)) {
         TextView var10000 = (TextView)var0;
         int var3 = ColorKt.recovered$81a7e7de546cbf77(var1);
         int var4 = 0;
         int var10001 = var3 >> 16 & 0xFF;
         var3 = ColorKt.recovered$81a7e7de546cbf77(var1);
         var4 = 0;
         int var10002 = var3 >> 8 & 0xFF;
         var3 = ColorKt.recovered$81a7e7de546cbf77(var1);
         var4 = 0;
         var10000.setTextColor(android.graphics.Color.rgb(var10001, var10002, var3 & 0xFF));
      }

      if (var0 instanceof ViewGroup) {
         ViewGroup var11 = (ViewGroup)var0;
         int var14 = 0;
         int var5 = 0;

         for (int var6 = var11.getChildCount(); var5 < var6; var5++) {
            View var7 = var11.getChildAt(var5);
            int var8 = 0;
            recovered$d9292e173c7aa98b(var7, var1);
         }
      }
   }

   private static final Unit BlockView_7eQnmaA$lambda$1$lambda$0(List var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit BlockView_7eQnmaA$lambda$3$lambda$2(boolean var0, Function1 var1, Block var2, Function0 var3) {
      if (var0) {
         if (var1 != null) {
            TicketType var10001 = var2.getTicketType();
            Intrinsics.checkNotNullExpressionValue(var10001, "getTicketType(...)");
            var1.invoke(var10001);
         }
      } else if (var3 != null) {
         var3.invoke();
      }

      return Unit.INSTANCE;
   }

   private static final MutableState BlockView_7eQnmaA$lambda$5$lambda$4() {
      return SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
   }

   private static final boolean BlockView_7eQnmaA$lambda$6(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void BlockView_7eQnmaA$lambda$7(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit BlockView_7eQnmaA$lambda$9$lambda$8(MutableState var0) {
      BlockView_7eQnmaA$lambda$7(var0, true);
      return Unit.INSTANCE;
   }

   private static final Unit BlockView_7eQnmaA$lambda$11$lambda$10(MutableState var0) {
      BlockView_7eQnmaA$lambda$7(var0, false);
      return Unit.INSTANCE;
   }

   private static final MutableState BlockView_7eQnmaA$lambda$15$lambda$14() {
      return SnapshotStateKt.mutableStateOf$default(false, null, 2, null);
   }

   private static final boolean BlockView_7eQnmaA$lambda$16(MutableState var0) {
      State var1 = var0;
      Object var2 = null;
      KProperty var3 = null;
      int var4 = 0;
      return (Boolean)var1.getValue();
   }

   private static final void BlockView_7eQnmaA$lambda$17(MutableState var0, boolean var1) {
      MutableState var2 = var0;
      Object var3 = null;
      Object var4 = null;
      Object var5 = var1;
      int var6 = 0;
      var2.setValue(var5);
   }

   private static final Unit BlockView_7eQnmaA$lambda$19$lambda$18(MutableState var0) {
      BlockView_7eQnmaA$lambda$17(var0, true);
      return Unit.INSTANCE;
   }

   private static final Unit BlockView_7eQnmaA$lambda$21$lambda$20(MutableState var0) {
      BlockView_7eQnmaA$lambda$17(var0, false);
      return Unit.INSTANCE;
   }

   private static final Unit BlockView_7eQnmaA$lambda$22(
      Modifier var0,
      BlockRenderData var1,
      boolean var2,
      SuffixText var3,
      boolean var4,
      String var5,
      ImageRenderType var6,
      long var7,
      Function0 var9,
      Function1 var10,
      boolean var11,
      Function1 var12,
      Shape var13,
      boolean var14,
      Function1 var15,
      int var16,
      int var17,
      int var18,
      Composer var19,
      int var20
   ) {
      recovered$5709084683a88fd9(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var19,
         RecomposeScopeImplKt.updateChangedFlags(var16 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var17),
         var18
      );
      return Unit.INSTANCE;
   }

   private static final LinearLayout RenderLegacyBlocks_sW7UJKQ$lambda$24$lambda$23(Blocks var0, Block var1, ViewHolderGenerator var2, long var3, Context var5) {
      Intrinsics.checkNotNullParameter(var5, "it");
      LinearLayout var6 = var0.createBlocks(CollectionsKt.listOf(var1), var2.getPostHolder());
      Intrinsics.checkNotNull(var6);
      recovered$d9292e173c7aa98b(var6, var3);
      var6.setLayoutParams(new LayoutParams(-2, -2));
      return var6;
   }

   private static final Unit RenderLegacyBlocks_sW7UJKQ$lambda$25(
      Block var0, long var1, Modifier var3, String var4, int var5, int var6, Composer var7, int var8
   ) {
      recovered$3dc71cc6f7fc8f68(var0, var1, var3, var4, var7, RecomposeScopeImplKt.updateChangedFlags(var5 | 1), var6);
      return Unit.INSTANCE;
   }
}
