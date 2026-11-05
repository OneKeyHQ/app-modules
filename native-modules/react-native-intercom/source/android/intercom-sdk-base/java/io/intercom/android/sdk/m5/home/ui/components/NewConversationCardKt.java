package io.intercom.android.sdk.m5.home.ui.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.components.HomeItemKt;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData;
import io.intercom.android.sdk.m5.home.data.IconType;
import io.intercom.android.sdk.models.OpenMessengerResponse$IconType;
import io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$HomeCard;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.component.IntercomCardKt;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Deprecated;
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
   d1 = "\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a=\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0001¢\u0006\u0002\u0010\n\u001a=\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\n\u001a#\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003¢\u0006\u0002\u0010\u0010\u001a\r\u0010\u0011\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0013\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0014\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0012¨\u0006\u0016",
   d2 = {
         "NewConversationCard",
         "",
         "newConversation",
         "Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeNewConversationData;",
         "adminAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "botAvatar",
         "onNewConversationClicked",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/m5/home/data/HomeCards$HomeNewConversationData;Ljava/util/List;Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "NewConversationCardV1",
         "newConversationData",
         "NewConversationCardV2",
         "homeCard",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;",
         "(Lio/intercom/android/sdk/models/OpenMessengerResponse$NewConversationData$HomeCard;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V",
         "NewConversationCardTeammatePreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "NewConversationCardBotPreview",
         "NewConversationCardFinWithHumanAccessPreview",
         "NewConversationCardFinWithoutHumanAccessPreview",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNewConversationCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,248:1\n118#2:249\n118#2:250\n118#2:251\n118#2:252\n*S KotlinDebug\n*F\n+ 1 NewConversationCard.kt\nio/intercom/android/sdk/m5/home/ui/components/NewConversationCardKt\n*L\n59#1:249\n67#1:250\n104#1:251\n107#1:252\n*E\n"
)
public final class NewConversationCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void NewConversationCard(
      @NotNull HomeCards$HomeNewConversationData var0,
      @NotNull List var1,
      @Nullable AvatarWrapper var2,
      @NotNull Function0 var3,
      @Nullable Composer var4,
      int var5,
      int var6
   ) {
      Intrinsics.checkNotNullParameter(var0, "newConversation");
      Intrinsics.checkNotNullParameter(var1, "adminAvatars");
      Intrinsics.checkNotNullParameter(var3, "onNewConversationClicked");
      var4 = var4.startRestartGroup(-773584515);
      ComposerKt.sourceInformation(var4, "C(NewConversationCard)P(2)32@1521L510,32@1508L523:NewConversationCard.kt#utrtal");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-773584515, var7, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCard (NewConversationCard.kt:31)");
         }

         IntercomCardKt.IntercomCard(
            null,
            null,
            ComposableLambdaKt.rememberComposableLambda(-1464624655, true, new NewConversationCardKt$NewConversationCard$1(var0, var3, var1, var2), var4, 54),
            var4,
            384,
            3
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var4.endRestartGroup();
      if (var10000 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData recoveredCapture_919_0 = var0;
         final java.util.List recoveredCapture_919_1 = var1;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredCapture_919_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_919_3 = var3;
         final int recoveredCapture_919_4 = var5;
         final int recoveredCapture_919_5 = var6;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_919_0, recoveredArgument_919_1) -> NewConversationCardKt.NewConversationCard$lambda$0(recoveredCapture_919_0, recoveredCapture_919_1, recoveredCapture_919_2, recoveredCapture_919_3, recoveredCapture_919_4, recoveredCapture_919_5, (androidx.compose.runtime.Composer)recoveredArgument_919_0, ((java.lang.Integer)recoveredArgument_919_1).intValue())));
      }
   }

   /** @deprecated */
   @Deprecated(message = "Use NewConversationCardV2 instead")
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardV1(
      HomeCards$HomeNewConversationData var0, List var1, AvatarWrapper var2, Function0 var3, Composer var4, int var5, int var6
   ) {
      var4 = var4.startRestartGroup(-1141879848);
      ComposerKt.sourceInformation(var4, "C(NewConversationCardV1)P(2)69@2854L790,57@2308L1498:NewConversationCard.kt#utrtal");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changedInstance(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changedInstance(var1) ? 32 : 16;
      }

      if ((var6 & 4) != 0) {
         var7 |= 384;
      } else if ((var5 & 384) == 0) {
         var7 |= var4.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 8) != 0) {
         var7 |= 3072;
      } else if ((var5 & 3072) == 0) {
         var7 |= var4.changedInstance(var3) ? 2048 : 1024;
      }

      if ((var7 & 1171) == 1170 && var4.getSkipping()) {
         var4.skipToGroupEnd();
      } else {
         if ((var6 & 4) != 0) {
            var2 = null;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1141879848, var7, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardV1 (NewConversationCard.kt:56)");
         }

         Modifier var10000 = Modifier.Companion;
         int var9 = 64;
         int var10 = 0;
         Modifier var8 = SizeKt.recovered$da9aba2b92020277(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var9), 1, null);
         IconType var18 = var0.getAction().getIcon();
         Integer var19;
         switch (var18 == null ? -1 : NewConversationCardKt$WhenMappings.$EnumSwitchMapping$0[var18.ordinal()]) {
            case 1:
               var19 = drawable.intercom_send_message_icon;
               break;
            case 2:
               var19 = drawable.intercom_conversation_card_question;
               break;
            case 3:
               var19 = drawable.intercom_chevron;
               break;
            default:
               var19 = null;
         }

         Integer var16 = var19;
         IconType var20 = var0.getAction().getIcon();
         if ((var20 == null ? -1 : NewConversationCardKt$WhenMappings.$EnumSwitchMapping$0[var20.ordinal()]) == 1) {
            var10000 = Modifier.Companion;
            int var13 = 16;
            int var14 = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060(var13));
         } else {
            var10000 = Modifier.Companion;
         }

         Modifier var17 = var10000;
         String var11 = var0.getAction().getLabel();
         String var12 = var0.getAction().getSubtitle();
         HomeItemKt.HomeItem(
            var8,
            var16,
            ComposableLambdaKt.rememberComposableLambda(1023934521, true, new NewConversationCardKt$NewConversationCardV1$1(var0, var2, var1), var4, 54),
            var17,
            null,
            var11,
            var12,
            null,
            var3,
            var4,
            390 | 234881024 & var7 << 15,
            144
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var23 = var4.endRestartGroup();
      if (var23 != null) {
         final io.intercom.android.sdk.m5.home.data.HomeCards$HomeNewConversationData recoveredCapture_924_0 = var0;
         final java.util.List recoveredCapture_924_1 = var1;
         final io.intercom.android.sdk.m5.components.avatar.AvatarWrapper recoveredCapture_924_2 = var2;
         final kotlin.jvm.functions.Function0 recoveredCapture_924_3 = var3;
         final int recoveredCapture_924_4 = var5;
         final int recoveredCapture_924_5 = var6;
         var23.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_924_0, recoveredArgument_924_1) -> NewConversationCardKt.NewConversationCardV1$lambda$1(recoveredCapture_924_0, recoveredCapture_924_1, recoveredCapture_924_2, recoveredCapture_924_3, recoveredCapture_924_4, recoveredCapture_924_5, (androidx.compose.runtime.Composer)recoveredArgument_924_0, ((java.lang.Integer)recoveredArgument_924_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardV2(OpenMessengerResponse$NewConversationData$HomeCard var0, Function0 var1, Composer var2, int var3) {
      var2 = var2.startRestartGroup(341363796);
      ComposerKt.sourceInformation(var2, "C(NewConversationCardV2)109@4200L543,102@3931L937:NewConversationCard.kt#utrtal");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var2.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var2.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(341363796, var4, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardV2 (NewConversationCard.kt:101)");
         }

         Modifier var10000 = Modifier.Companion;
         int var6 = 64;
         int var7 = 0;
         Modifier var5 = SizeKt.recovered$da9aba2b92020277(var10000, 0.0F, Dp.recovered$95b3d103663bf060(var6), 1, null);
         OpenMessengerResponse$IconType var15 = var0.getIcon();
         Integer var13 = var15 != null ? var15.getIcon() : null;
         OpenMessengerResponse$IconType var16 = var0.getIcon();
         if ((var16 == null ? -1 : NewConversationCardKt$WhenMappings.$EnumSwitchMapping$1[var16.ordinal()]) == 1) {
            var10000 = Modifier.Companion;
            int $this$dp$ivx = 16;
            int $i$f$getDpx = 0;
            var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         } else {
            var10000 = Modifier.Companion;
         }

         Modifier var14 = var10000;
         String var8 = var0.getText();
         String var9 = var0.getSubtitle();
         HomeItemKt.HomeItem(
            var5,
            var13,
            ComposableLambdaKt.rememberComposableLambda(94824693, true, new NewConversationCardKt$NewConversationCardV2$1(var0), var2, 54),
            var14,
            null,
            var8,
            var9,
            null,
            var1,
            var2,
            390 | 234881024 & var4 << 21,
            144
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var19 = var2.endRestartGroup();
      if (var19 != null) {
         final io.intercom.android.sdk.models.OpenMessengerResponse$NewConversationData$HomeCard recoveredCapture_925_0 = var0;
         final kotlin.jvm.functions.Function0 recoveredCapture_925_1 = var1;
         final int recoveredCapture_925_2 = var3;
         var19.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_925_0, recoveredArgument_925_1) -> NewConversationCardKt.NewConversationCardV2$lambda$2(recoveredCapture_925_0, recoveredCapture_925_1, recoveredCapture_925_2, (androidx.compose.runtime.Composer)recoveredArgument_925_0, ((java.lang.Integer)recoveredArgument_925_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardTeammatePreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(605107279);
      ComposerKt.sourceInformation(var0, "C(NewConversationCardTeammatePreview)133@4957L969:NewConversationCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               605107279, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardTeammatePreview (NewConversationCard.kt:132)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$NewConversationCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_923_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_923_0, recoveredArgument_923_1) -> NewConversationCardKt.NewConversationCardTeammatePreview$lambda$3(recoveredCapture_923_0, (androidx.compose.runtime.Composer)recoveredArgument_923_0, ((java.lang.Integer)recoveredArgument_923_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardBotPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(-322151692);
      ComposerKt.sourceInformation(var0, "C(NewConversationCardBotPreview)162@6010L961:NewConversationCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -322151692, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardBotPreview (NewConversationCard.kt:161)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$NewConversationCardKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_920_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_920_0, recoveredArgument_920_1) -> NewConversationCardKt.NewConversationCardBotPreview$lambda$4(recoveredCapture_920_0, (androidx.compose.runtime.Composer)recoveredArgument_920_0, ((java.lang.Integer)recoveredArgument_920_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardFinWithHumanAccessPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1635839473);
      ComposerKt.sourceInformation(var0, "C(NewConversationCardFinWithHumanAccessPreview)191@7070L1018:NewConversationCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1635839473, var1, -1, "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardFinWithHumanAccessPreview (NewConversationCard.kt:190)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$NewConversationCardKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_921_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_921_0, recoveredArgument_921_1) -> NewConversationCardKt.NewConversationCardFinWithHumanAccessPreview$lambda$5(recoveredCapture_921_0, (androidx.compose.runtime.Composer)recoveredArgument_921_0, ((java.lang.Integer)recoveredArgument_921_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void NewConversationCardFinWithoutHumanAccessPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1289284327);
      ComposerKt.sourceInformation(var0, "C(NewConversationCardFinWithoutHumanAccessPreview)221@8190L991:NewConversationCard.kt#utrtal");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1289284327,
               var1,
               -1,
               "io.intercom.android.sdk.m5.home.ui.components.NewConversationCardFinWithoutHumanAccessPreview (NewConversationCard.kt:220)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$NewConversationCardKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_922_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_922_0, recoveredArgument_922_1) -> NewConversationCardKt.NewConversationCardFinWithoutHumanAccessPreview$lambda$6(recoveredCapture_922_0, (androidx.compose.runtime.Composer)recoveredArgument_922_0, ((java.lang.Integer)recoveredArgument_922_1).intValue())));
      }
   }

   private static final Unit NewConversationCard$lambda$0(
      HomeCards$HomeNewConversationData var0, List var1, AvatarWrapper var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      NewConversationCard(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardV1$lambda$1(
      HomeCards$HomeNewConversationData var0, List var1, AvatarWrapper var2, Function0 var3, int var4, int var5, Composer var6, int var7
   ) {
      NewConversationCardV1(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardV2$lambda$2(
      OpenMessengerResponse$NewConversationData$HomeCard var0, Function0 var1, int var2, Composer var3, int var4
   ) {
      NewConversationCardV2(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardTeammatePreview$lambda$3(int var0, Composer var1, int var2) {
      NewConversationCardTeammatePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardBotPreview$lambda$4(int var0, Composer var1, int var2) {
      NewConversationCardBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardFinWithHumanAccessPreview$lambda$5(int var0, Composer var1, int var2) {
      NewConversationCardFinWithHumanAccessPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit NewConversationCardFinWithoutHumanAccessPreview$lambda$6(int var0, Composer var1, int var2) {
      NewConversationCardFinWithoutHumanAccessPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final void access$NewConversationCardV2(OpenMessengerResponse$NewConversationData$HomeCard var0, Function0 var1, Composer var2, int var3) {
      NewConversationCardV2(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$NewConversationCardV1(
      HomeCards$HomeNewConversationData var0, List var1, AvatarWrapper var2, Function0 var3, Composer var4, int var5, int var6
   ) {
      NewConversationCardV1(var0, var1, var2, var3, var4, var5, var6);
   }
}
