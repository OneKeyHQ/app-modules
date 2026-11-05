package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.foundation.layout.BoxWithConstraintsKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.metrics.MetricData;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState;
import io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState$Idle;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.flow.SharedFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000l\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\u001aù\u0002\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u001e\u0010\u0006\u001a\u001a\u0012\u0004\u0012\u00020\b\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0014\b\u0002\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0002\u0010\u0014\u001a\u00020\u00152\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u001b\u0018\u00010\u001a2\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\u000e\b\u0002\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u000e\b\u0002\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u000e\b\u0002\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\u0014\b\u0002\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\f2\u0014\b\u0002\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u00010\f2\u000e\b\u0002\u0010'\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fH\u0001¢\u0006\u0004\b(\u0010)\u001a\r\u0010*\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010+\u001a\r\u0010,\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010+¨\u0006-²\u0006\n\u0010.\u001a\u00020/X\u008a\u0084\u0002²\u0006\n\u00100\u001a\u000201X\u008a\u0084\u0002",
   d2 = {
         "ConversationBottomBar",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "bottomBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "onSendMessage",
         "Lkotlin/Function2;",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "onInputChange",
         "Lkotlin/Function1;",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposerInputType;",
         "onGifInputSelected",
         "Lkotlin/Function0;",
         "onNewConversationClicked",
         "onMediaInputSelected",
         "trackMetric",
         "Lio/intercom/android/sdk/m5/conversation/metrics/MetricData;",
         "topSpacing",
         "Landroidx/compose/ui/unit/Dp;",
         "navigateToAnotherConversation",
         "onPrivacyNoticeDismissed",
         "onTyping",
         "composerUiEffect",
         "Lkotlinx/coroutines/flow/SharedFlow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerUiEffect;",
         "voiceTranscriptionState",
         "Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "amplitudeLevel",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "",
         "onStartVoiceRecording",
         "onStopVoiceRecording",
         "onCancelVoiceRecording",
         "onTranscribedTextConsumed",
         "onDeleteMediaUploadItem",
         "onFailedMediaUploadItem",
         "onClearMediaUploadItems",
         "ConversationBottomBar-HPAO-JA",
         "(Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;FLkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlinx/coroutines/flow/SharedFlow;Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;Lkotlinx/coroutines/flow/StateFlow;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;IIII)V",
         "MessageComposerPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "MessageComposerLongTextPreview",
         "intercom-sdk-base_release",
         "keyboardAsState",
         "Lio/intercom/android/sdk/m5/conversation/utils/KeyboardState;",
         "bottomBadgeVisibility",
         ""
   }
)
@SourceDebugExtension(
   "SMAP\nConversationBottomBar.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 3 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,297:1\n1047#2,6:298\n1047#2,6:305\n1047#2,6:311\n1047#2,6:317\n1047#2,6:323\n1047#2,6:329\n1047#2,6:335\n1047#2,6:341\n1047#2,6:347\n118#3:304\n*S KotlinDebug\n*F\n+ 1 ConversationBottomBar.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ConversationBottomBarKt\n*L\n74#1:298,6\n78#1:305,6\n82#1:311,6\n83#1:317,6\n84#1:323,6\n85#1:329,6\n86#1:335,6\n87#1:341,6\n88#1:347,6\n75#1:304\n*E\n"
)
public final class ConversationBottomBarKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void recovered$3e50179ac1c3f351(
      @Nullable Modifier var0,
      @NotNull BottomBarUiState var1,
      @NotNull Function2 var2,
      @NotNull Function1 var3,
      @NotNull Function0 var4,
      @NotNull Function0 var5,
      @NotNull Function0 var6,
      @Nullable Function1 var7,
      float var8,
      @NotNull Function1 var9,
      @NotNull Function0 var10,
      @Nullable Function0 var11,
      @Nullable SharedFlow var12,
      @Nullable VoiceTranscriptionState var13,
      @Nullable StateFlow var14,
      @Nullable Function0 var15,
      @Nullable Function0 var16,
      @Nullable Function0 var17,
      @Nullable Function0 var18,
      @Nullable Function1 var19,
      @Nullable Function1 var20,
      @Nullable Function0 var21,
      @Nullable Composer var22,
      int var23,
      int var24,
      int var25,
      int var26
   ) {
      Intrinsics.checkNotNullParameter(var1, "bottomBarUiState");
      Intrinsics.checkNotNullParameter(var2, "onSendMessage");
      Intrinsics.checkNotNullParameter(var3, "onInputChange");
      Intrinsics.checkNotNullParameter(var4, "onGifInputSelected");
      Intrinsics.checkNotNullParameter(var5, "onNewConversationClicked");
      Intrinsics.checkNotNullParameter(var6, "onMediaInputSelected");
      Intrinsics.checkNotNullParameter(var9, "navigateToAnotherConversation");
      Intrinsics.checkNotNullParameter(var10, "onPrivacyNoticeDismissed");
      var22 = var22.startRestartGroup(2082834787);
      ComposerKt.sourceInformation(
         var22,
         "C(ConversationBottomBar)P(3,1,14,10,9,12,11,20,19:c#ui.unit.Dp,4,13,18,2,21!1,15,16!1,17,7,8)73@3746L2,77@3899L2,81@4149L2,82@4192L2,83@4237L2,84@4285L2,85@4346L2,86@4407L2,87@4453L2,91@4520L6852,89@4465L6907:ConversationBottomBar.kt#yb6g9"
      );
      int var27 = var23;
      int var28 = var24;
      int var29 = var25;
      if ((var26 & 1) != 0) {
         var27 |= 6;
      } else if ((var23 & 6) == 0) {
         var27 |= var22.changed(var0) ? 4 : 2;
      }

      if ((var26 & 2) != 0) {
         var27 |= 48;
      } else if ((var23 & 48) == 0) {
         var27 |= var22.changedInstance(var1) ? 32 : 16;
      }

      if ((var26 & 4) != 0) {
         var27 |= 384;
      } else if ((var23 & 384) == 0) {
         var27 |= var22.changedInstance(var2) ? 256 : 128;
      }

      if ((var26 & 8) != 0) {
         var27 |= 3072;
      } else if ((var23 & 3072) == 0) {
         var27 |= var22.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var26 & 16) != 0) {
         var27 |= 24576;
      } else if ((var23 & 24576) == 0) {
         var27 |= var22.changedInstance(var4) ? 16384 : 8192;
      }

      if ((var26 & 32) != 0) {
         var27 |= 196608;
      } else if ((var23 & 196608) == 0) {
         var27 |= var22.changedInstance(var5) ? 131072 : 65536;
      }

      if ((var26 & 64) != 0) {
         var27 |= 1572864;
      } else if ((var23 & 1572864) == 0) {
         var27 |= var22.changedInstance(var6) ? 1048576 : 524288;
      }

      if ((var26 & 128) != 0) {
         var27 |= 12582912;
      } else if ((var23 & 12582912) == 0) {
         var27 |= var22.changedInstance(var7) ? 8388608 : 4194304;
      }

      if ((var26 & 256) != 0) {
         var27 |= 100663296;
      } else if ((var23 & 100663296) == 0) {
         var27 |= var22.changed(var8) ? 67108864 : 33554432;
      }

      if ((var26 & 512) != 0) {
         var27 |= 805306368;
      } else if ((var23 & 805306368) == 0) {
         var27 |= var22.changedInstance(var9) ? 536870912 : 268435456;
      }

      if ((var26 & 1024) != 0) {
         var28 |= 6;
      } else if ((var24 & 6) == 0) {
         var28 |= var22.changedInstance(var10) ? 4 : 2;
      }

      if ((var26 & 2048) != 0) {
         var28 |= 48;
      } else if ((var24 & 48) == 0) {
         var28 |= var22.changedInstance(var11) ? 32 : 16;
      }

      if ((var26 & 4096) != 0) {
         var28 |= 384;
      } else if ((var24 & 384) == 0) {
         var28 |= var22.changedInstance(var12) ? 256 : 128;
      }

      if ((var26 & 8192) != 0) {
         var28 |= 3072;
      } else if ((var24 & 3072) == 0) {
         var28 |= ((var24 & 4096) == 0 ? var22.changed(var13) : var22.changedInstance(var13)) ? 2048 : 1024;
      }

      if ((var24 & 24576) == 0) {
         var28 |= (var26 & 16384) == 0 && var22.changedInstance(var14) ? 16384 : 8192;
      }

      if ((var26 & 32768) != 0) {
         var28 |= 196608;
      } else if ((var24 & 196608) == 0) {
         var28 |= var22.changedInstance(var15) ? 131072 : 65536;
      }

      if ((var26 & 65536) != 0) {
         var28 |= 1572864;
      } else if ((var24 & 1572864) == 0) {
         var28 |= var22.changedInstance(var16) ? 1048576 : 524288;
      }

      if ((var26 & 131072) != 0) {
         var28 |= 12582912;
      } else if ((var24 & 12582912) == 0) {
         var28 |= var22.changedInstance(var17) ? 8388608 : 4194304;
      }

      if ((var26 & 262144) != 0) {
         var28 |= 100663296;
      } else if ((var24 & 100663296) == 0) {
         var28 |= var22.changedInstance(var18) ? 67108864 : 33554432;
      }

      if ((var26 & 524288) != 0) {
         var28 |= 805306368;
      } else if ((var24 & 805306368) == 0) {
         var28 |= var22.changedInstance(var19) ? 536870912 : 268435456;
      }

      if ((var26 & 1048576) != 0) {
         var29 |= 6;
      } else if ((var25 & 6) == 0) {
         var29 |= var22.changedInstance(var20) ? 4 : 2;
      }

      if ((var26 & 2097152) != 0) {
         var29 |= 48;
      } else if ((var25 & 48) == 0) {
         var29 |= var22.changedInstance(var21) ? 32 : 16;
      }

      if ((var27 & 306783379) == 306783378 && (var28 & 306783379) == 306783378 && (var29 & 19) == 18 && var22.getSkipping()) {
         var22.skipToGroupEnd();
      } else {
         var22.startDefaults();
         if ((var23 & 1) != 0 && !var22.getDefaultsInvalid()) {
            var22.skipToGroupEnd();
            if ((var26 & 16384) != 0) {
               var28 &= -57345;
            }
         } else {
            if ((var26 & 1) != 0) {
               var0 = Modifier.Companion;
            }

            if ((var26 & 128) != 0) {
               var22.startReplaceGroup(-521063632);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var31 = var22;
               boolean var32 = false;
               int var33 = 0;
               Object var34 = var31.rememberedValue();
               int var35 = 0;
               Object var10000;
               if (var34 == Composer.Companion.getEmpty()) {
                  int var36 = 0;
                  Object var88 = ((kotlin.jvm.functions.Function1) (recoveredArgument_568_0) -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$1$lambda$0((io.intercom.android.sdk.m5.conversation.metrics.MetricData)recoveredArgument_568_0));
                  var31.updateRememberedValue(var88);
                  var10000 = var88;
               } else {
                  var10000 = var34;
               }

               Function1 var30 = (Function1)var10000;
               var22.endReplaceGroup();
               var7 = var30;
            }

            if ((var26 & 256) != 0) {
               int var38 = 0;
               int var47 = 0;
               var8 = Dp.recovered$95b3d103663bf060(var38);
            }

            if ((var26 & 2048) != 0) {
               var22.startReplaceGroup(-521058736);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var48 = var22;
               boolean var56 = false;
               int var64 = 0;
               Object var72 = var48.rememberedValue();
               int var80 = 0;
               Object var105;
               if (var72 == Composer.Companion.getEmpty()) {
                  int var89 = 0;
                  Object var90 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$3$lambda$2());
                  var48.updateRememberedValue(var90);
                  var105 = var90;
               } else {
                  var105 = var72;
               }

               Function0 var39 = (Function0)var105;
               var22.endReplaceGroup();
               var11 = var39;
            }

            if ((var26 & 4096) != 0) {
               var12 = null;
            }

            if ((var26 & 8192) != 0) {
               var13 = VoiceTranscriptionState$Idle.INSTANCE;
            }

            if ((var26 & 16384) != 0) {
               var14 = StateFlowKt.MutableStateFlow(0.0F);
               var28 &= -57345;
            }

            if ((var26 & 32768) != 0) {
               var22.startReplaceGroup(-521050736);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var49 = var22;
               boolean var57 = false;
               int var65 = 0;
               Object var73 = var49.rememberedValue();
               int var81 = 0;
               Object var106;
               if (var73 == Composer.Companion.getEmpty()) {
                  int var91 = 0;
                  Object var92 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$5$lambda$4());
                  var49.updateRememberedValue(var92);
                  var106 = var92;
               } else {
                  var106 = var73;
               }

               Function0 var40 = (Function0)var106;
               var22.endReplaceGroup();
               var15 = var40;
            }

            if ((var26 & 65536) != 0) {
               var22.startReplaceGroup(-521049360);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var50 = var22;
               boolean var58 = false;
               int var66 = 0;
               Object var74 = var50.rememberedValue();
               int var82 = 0;
               Object var107;
               if (var74 == Composer.Companion.getEmpty()) {
                  int var93 = 0;
                  Object var94 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$7$lambda$6());
                  var50.updateRememberedValue(var94);
                  var107 = var94;
               } else {
                  var107 = var74;
               }

               Function0 var41 = (Function0)var107;
               var22.endReplaceGroup();
               var16 = var41;
            }

            if ((var26 & 131072) != 0) {
               var22.startReplaceGroup(-521047920);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var51 = var22;
               boolean var59 = false;
               int var67 = 0;
               Object var75 = var51.rememberedValue();
               int var83 = 0;
               Object var108;
               if (var75 == Composer.Companion.getEmpty()) {
                  int var95 = 0;
                  Object var96 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$9$lambda$8());
                  var51.updateRememberedValue(var96);
                  var108 = var96;
               } else {
                  var108 = var75;
               }

               Function0 var42 = (Function0)var108;
               var22.endReplaceGroup();
               var17 = var42;
            }

            if ((var26 & 262144) != 0) {
               var22.startReplaceGroup(-521046384);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var52 = var22;
               boolean var60 = false;
               int var68 = 0;
               Object var76 = var52.rememberedValue();
               int var84 = 0;
               Object var109;
               if (var76 == Composer.Companion.getEmpty()) {
                  int var97 = 0;
                  Object var98 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$11$lambda$10());
                  var52.updateRememberedValue(var98);
                  var109 = var98;
               } else {
                  var109 = var76;
               }

               Function0 var43 = (Function0)var109;
               var22.endReplaceGroup();
               var18 = var43;
            }

            if ((var26 & 524288) != 0) {
               var22.startReplaceGroup(-521044432);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var53 = var22;
               boolean var61 = false;
               int var69 = 0;
               Object var77 = var53.rememberedValue();
               int var85 = 0;
               Object var110;
               if (var77 == Composer.Companion.getEmpty()) {
                  int var99 = 0;
                  Object var100 = ((kotlin.jvm.functions.Function1) (recoveredArgument_564_0) -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$13$lambda$12((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_564_0));
                  var53.updateRememberedValue(var100);
                  var110 = var100;
               } else {
                  var110 = var77;
               }

               Function1 var44 = (Function1)var110;
               var22.endReplaceGroup();
               var19 = var44;
            }

            if ((var26 & 1048576) != 0) {
               var22.startReplaceGroup(-521042480);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var54 = var22;
               boolean var62 = false;
               int var70 = 0;
               Object var78 = var54.rememberedValue();
               int var86 = 0;
               Object var111;
               if (var78 == Composer.Companion.getEmpty()) {
                  int var101 = 0;
                  Object var102 = ((kotlin.jvm.functions.Function1) (recoveredArgument_565_0) -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$15$lambda$14((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_565_0));
                  var54.updateRememberedValue(var102);
                  var111 = var102;
               } else {
                  var111 = var78;
               }

               Function1 var45 = (Function1)var111;
               var22.endReplaceGroup();
               var20 = var45;
            }

            if ((var26 & 2097152) != 0) {
               var22.startReplaceGroup(-521041008);
               ComposerKt.sourceInformation(var22, "CC(remember):ConversationBottomBar.kt#9igjgp");
               Composer var55 = var22;
               boolean var63 = false;
               int var71 = 0;
               Object var79 = var55.rememberedValue();
               int var87 = 0;
               Object var112;
               if (var79 == Composer.Companion.getEmpty()) {
                  int var103 = 0;
                  Object var104 = ((kotlin.jvm.functions.Function0) () -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$17$lambda$16());
                  var55.updateRememberedValue(var104);
                  var112 = var104;
               } else {
                  var112 = var79;
               }

               Function0 var46 = (Function0)var112;
               var22.endReplaceGroup();
               var21 = var46;
            }
         }

         var22.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2082834787, var27, var28, "io.intercom.android.sdk.m5.conversation.ui.components.composer.ConversationBottomBar (ConversationBottomBar.kt:88)"
            );
         }

         BoxWithConstraintsKt.BoxWithConstraints(
            var0,
            null,
            false,
            ComposableLambdaKt.rememberComposableLambda(
               -1308906311,
               true,
               new ConversationBottomBarKt$ConversationBottomBar$10(
                  var8, var1, var2, var4, var6, var3, var7, var11, var12, var13, var14, var15, var16, var17, var18, var19, var20, var21, var5, var9, var10
               ),
               var22,
               54
            ),
            var22,
            3072 | 14 & var27,
            6
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var113 = var22.endRestartGroup();
      if (var113 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_567_0 = var0;
         final io.intercom.android.sdk.m5.conversation.states.BottomBarUiState recoveredCapture_567_1 = var1;
         final kotlin.jvm.functions.Function2 recoveredCapture_567_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_567_3 = var3;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_5 = var5;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_6 = var6;
         final kotlin.jvm.functions.Function1 recoveredCapture_567_7 = var7;
         final float recoveredCapture_567_8 = var8;
         final kotlin.jvm.functions.Function1 recoveredCapture_567_9 = var9;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_10 = var10;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_11 = var11;
         final kotlinx.coroutines.flow.SharedFlow recoveredCapture_567_12 = var12;
         final io.intercom.android.sdk.m5.conversation.states.VoiceTranscriptionState recoveredCapture_567_13 = var13;
         final kotlinx.coroutines.flow.StateFlow recoveredCapture_567_14 = var14;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_15 = var15;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_16 = var16;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_17 = var17;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_18 = var18;
         final kotlin.jvm.functions.Function1 recoveredCapture_567_19 = var19;
         final kotlin.jvm.functions.Function1 recoveredCapture_567_20 = var20;
         final kotlin.jvm.functions.Function0 recoveredCapture_567_21 = var21;
         final int recoveredCapture_567_22 = var23;
         final int recoveredCapture_567_23 = var24;
         final int recoveredCapture_567_24 = var25;
         final int recoveredCapture_567_25 = var26;
         var113.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_567_0, recoveredArgument_567_1) -> ConversationBottomBarKt.ConversationBottomBar_HPAO_JA$lambda$18(recoveredCapture_567_0, recoveredCapture_567_1, recoveredCapture_567_2, recoveredCapture_567_3, recoveredCapture_567_4, recoveredCapture_567_5, recoveredCapture_567_6, recoveredCapture_567_7, recoveredCapture_567_8, recoveredCapture_567_9, recoveredCapture_567_10, recoveredCapture_567_11, recoveredCapture_567_12, recoveredCapture_567_13, recoveredCapture_567_14, recoveredCapture_567_15, recoveredCapture_567_16, recoveredCapture_567_17, recoveredCapture_567_18, recoveredCapture_567_19, recoveredCapture_567_20, recoveredCapture_567_21, recoveredCapture_567_22, recoveredCapture_567_23, recoveredCapture_567_24, recoveredCapture_567_25, (androidx.compose.runtime.Composer)recoveredArgument_567_0, ((java.lang.Integer)recoveredArgument_567_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MessageComposerPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-961451097);
      ComposerKt.sourceInformation(var0, "C(MessageComposerPreview)234@11449L973:ConversationBottomBar.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -961451097, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerPreview (ConversationBottomBar.kt:233)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationBottomBarKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_559_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_559_0, recoveredArgument_559_1) -> ConversationBottomBarKt.MessageComposerPreview$lambda$19(recoveredCapture_559_0, (androidx.compose.runtime.Composer)recoveredArgument_559_0, ((java.lang.Integer)recoveredArgument_559_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void MessageComposerLongTextPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1582182192);
      ComposerKt.sourceInformation(var0, "C(MessageComposerLongTextPreview)263@12507L4797:ConversationBottomBar.kt#yb6g9");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1582182192,
               var1,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.MessageComposerLongTextPreview (ConversationBottomBar.kt:262)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationBottomBarKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_558_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_558_0, recoveredArgument_558_1) -> ConversationBottomBarKt.MessageComposerLongTextPreview$lambda$20(recoveredCapture_558_0, (androidx.compose.runtime.Composer)recoveredArgument_558_0, ((java.lang.Integer)recoveredArgument_558_1).intValue())));
      }
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$1$lambda$0(MetricData var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$3$lambda$2() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$5$lambda$4() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$7$lambda$6() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$9$lambda$8() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$11$lambda$10() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$13$lambda$12(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$15$lambda$14(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$17$lambda$16() {
      return Unit.INSTANCE;
   }

   private static final Unit ConversationBottomBar_HPAO_JA$lambda$18(
      Modifier var0,
      BottomBarUiState var1,
      Function2 var2,
      Function1 var3,
      Function0 var4,
      Function0 var5,
      Function0 var6,
      Function1 var7,
      float var8,
      Function1 var9,
      Function0 var10,
      Function0 var11,
      SharedFlow var12,
      VoiceTranscriptionState var13,
      StateFlow var14,
      Function0 var15,
      Function0 var16,
      Function0 var17,
      Function0 var18,
      Function1 var19,
      Function1 var20,
      Function0 var21,
      int var22,
      int var23,
      int var24,
      int var25,
      Composer var26,
      int var27
   ) {
      recovered$3e50179ac1c3f351(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var9,
         var10,
         var11,
         var12,
         var13,
         var14,
         var15,
         var16,
         var17,
         var18,
         var19,
         var20,
         var21,
         var26,
         RecomposeScopeImplKt.updateChangedFlags(var22 | 1),
         RecomposeScopeImplKt.updateChangedFlags(var23),
         RecomposeScopeImplKt.updateChangedFlags(var24),
         var25
      );
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposerPreview$lambda$19(int var0, Composer var1, int var2) {
      MessageComposerPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit MessageComposerLongTextPreview$lambda$20(int var0, Composer var1, int var2) {
      MessageComposerLongTextPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
