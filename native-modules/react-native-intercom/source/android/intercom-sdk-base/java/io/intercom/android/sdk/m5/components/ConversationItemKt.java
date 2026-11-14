package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.CanvasKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material3.SurfaceKt;
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
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.drawscope.DrawScope;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.semantics.Role;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.LastParticipatingAdmin$Builder;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Participant;
import io.intercom.android.sdk.models.TeamPresence;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\u001aM\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0001¢\u0006\u0002\u0010\u000e\u001a\u000e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010H\u0002\u001a\b\u0010\u0012\u001a\u00020\u0013H\u0002\u001a\b\u0010\u0014\u001a\u00020\u0013H\u0002\u001a\u0017\u0010\u0015\u001a\u00020\u00012\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u0001¢\u0006\u0002\u0010\u0016\u001a\r\u0010\u0017\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u0019\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001a\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001b\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\r\u0010\u001c\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0018\u001a\u0012\u0010\u001d\u001a\u00020\u00032\b\b\u0002\u0010\u001e\u001a\u00020\u000bH\u0002\u001a\b\u0010\u001f\u001a\u00020\u0003H\u0002¨\u0006 ",
   d2 = {
         "ConversationItem",
         "",
         "conversation",
         "Lio/intercom/android/sdk/models/Conversation;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "typingState",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "contentPadding",
         "Landroidx/compose/foundation/layout/PaddingValues;",
         "showUnreadIndicator",
         "",
         "onClick",
         "Lkotlin/Function0;",
         "(Lio/intercom/android/sdk/models/Conversation;Landroidx/compose/ui/Modifier;Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;Landroidx/compose/foundation/layout/PaddingValues;ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;II)V",
         "getActiveAdminsAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "getWorkspaceName",
         "",
         "getUserIntercomId",
         "UnreadIndicator",
         "(Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "UnreadConversationCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "UnreadConversationCardWithBotPreview",
         "ReadConversationCardPreview",
         "ConversationCardWithLongTitlePreview",
         "TypingConversationCardPreview",
         "sampleConversation",
         "isRead",
         "sampleConversationWithBot",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationItem.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ConversationItemKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 Box.kt\nandroidx/compose/foundation/layout/BoxKt\n+ 6 Layout.kt\nandroidx/compose/ui/layout/LayoutKt\n+ 7 Composables.kt\nandroidx/compose/runtime/ComposablesKt\n+ 8 Size.kt\nandroidx/compose/ui/geometry/Size\n+ 9 InlineClassHelper.kt\nandroidx/compose/ui/util/InlineClassHelperKt\n+ 10 InlineClassHelper.jvmAndAndroid.kt\nandroidx/compose/ui/util/InlineClassHelper_jvmKt\n+ 11 Offset.kt\nandroidx/compose/ui/geometry/OffsetKt\n*L\n1#1,287:1\n118#2:288\n118#2:299\n118#2:331\n1047#3,6:289\n1047#3,6:332\n1563#4:295\n1634#4,3:296\n70#5:300\n68#5,8:301\n77#5:341\n81#6,6:309\n88#6,6:324\n96#6:340\n402#7,9:315\n411#7:330\n412#7,2:338\n57#8:342\n61#8:345\n60#9:343\n70#9:346\n53#9,3:348\n23#10:344\n30#11:347\n*S KotlinDebug\n*F\n+ 1 ConversationItem.kt\nio/intercom/android/sdk/m5/components/ConversationItemKt\n*L\n49#1:288\n161#1:299\n165#1:331\n53#1:289,6\n166#1:332,6\n150#1:295\n150#1:296,3\n160#1:300\n160#1:301,8\n160#1:341\n160#1:309,6\n160#1:324,6\n160#1:340\n160#1:315,9\n160#1:330\n160#1:338,2\n169#1:342\n169#1:345\n169#1:343\n169#1:346\n169#1:348,3\n169#1:344\n169#1:347\n*E\n"
)
public final class ConversationItemKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationItem(
      @NotNull Conversation var0,
      @Nullable Modifier var1,
      @Nullable CurrentlyTypingState var2,
      @Nullable PaddingValues var3,
      boolean var4,
      @NotNull Function0 var5,
      @Nullable Composer var6,
      int var7,
      int var8
   ) {
      Intrinsics.checkNotNullParameter(var0, "conversation");
      Intrinsics.checkNotNullParameter(var5, "onClick");
      var6 = var6.startRestartGroup(-2074390483);
      ComposerKt.sourceInformation(var6, "C(ConversationItem)P(1,2,5!1,4)52@2415L20,52@2437L4142,52@2377L4202:ConversationItem.kt#arqjag");
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
         var9 |= var6.changedInstance(var2) ? 256 : 128;
      }

      if ((var8 & 8) != 0) {
         var9 |= 3072;
      } else if ((var7 & 3072) == 0) {
         var9 |= var6.changed(var3) ? 2048 : 1024;
      }

      if ((var7 & 24576) == 0) {
         var9 |= (var8 & 16) == 0 && var6.changed(var4) ? 16384 : 8192;
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
            if ((var8 & 16) != 0) {
               var9 &= -57345;
            }
         } else {
            if ((var8 & 2) != 0) {
               var1 = Modifier.Companion;
            }

            if ((var8 & 4) != 0) {
               var2 = null;
            }

            if ((var8 & 8) != 0) {
               int var10 = 0;
               int var11 = 0;
               var3 = PaddingKt.recovered$9fd290d401ad9a87(Dp.recovered$95b3d103663bf060(var10));
            }

            if ((var8 & 16) != 0) {
               var4 = !var0.isRead();
               var9 &= -57345;
            }
         }

         var6.endDefaults();
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-2074390483, var9, -1, "io.intercom.android.sdk.m5.components.ConversationItem (ConversationItem.kt:51)");
         }

         Modifier var10000 = var1;
         boolean var10001 = false;
         Object var10002 = null;
         Object var10003 = null;
         Object var10004 = null;
         var6.startReplaceGroup(-437489840);
         ComposerKt.sourceInformation(var6, "CC(remember):ConversationItem.kt#9igjgp");
         Composer var26 = var6;
         boolean var12 = (var9 & 458752) == 131072;
         int var13 = 0;
         Object var14 = var26.rememberedValue();
         int var15 = 0;
         Object var10005;
         if (!var12 && var14 != Composer.Companion.getEmpty()) {
            var10005 = var14;
         } else {
            Object var22 = null;
            Object var21 = null;
            Object var20 = null;
            boolean var19 = false;
            Modifier var18 = var1;
            int var16 = 0;
            final kotlin.jvm.functions.Function0 recoveredCapture_188_0 = var5;
            Function0 var23 = ((kotlin.jvm.functions.Function0) () -> ConversationItemKt.ConversationItem$lambda$1$lambda$0(recoveredCapture_188_0));
            var10000 = var18;
            var10001 = var19;
            var10002 = var20;
            var10003 = var21;
            var10004 = var22;
            Object var17 = var23;
            var26.updateRememberedValue(var17);
            var10005 = var17;
         }

         Function0 var25 = (Function0)var10005;
         var6.endReplaceGroup();
         SurfaceKt.recovered$ecd79a99e04a9534(
            ClickableKt.recovered$1c9660c41fd850dc(var10000, var10001, (String)var10002, (Role)var10003, (MutableInteractionSource)var10004, var25, 15, null),
            null,
            0L,
            0L,
            0.0F,
            0.0F,
            null,
            ComposableLambdaKt.rememberComposableLambda(774321672, true, new ConversationItemKt$ConversationItem$2(var3, var2, var0, var4), var6, 54),
            var6,
            12582912,
            126
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var6.endRestartGroup();
      if (var27 != null) {
         final io.intercom.android.sdk.models.Conversation recoveredCapture_189_0 = var0;
         final androidx.compose.ui.Modifier recoveredCapture_189_1 = var1;
         final io.intercom.android.sdk.m5.conversation.states.CurrentlyTypingState recoveredCapture_189_2 = var2;
         final androidx.compose.foundation.layout.PaddingValues recoveredCapture_189_3 = var3;
         final boolean recoveredCapture_189_4 = var4;
         final kotlin.jvm.functions.Function0 recoveredCapture_189_5 = var5;
         final int recoveredCapture_189_6 = var7;
         final int recoveredCapture_189_7 = var8;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_189_0, recoveredArgument_189_1) -> ConversationItemKt.ConversationItem$lambda$2(recoveredCapture_189_0, recoveredCapture_189_1, recoveredCapture_189_2, recoveredCapture_189_3, recoveredCapture_189_4, recoveredCapture_189_5, recoveredCapture_189_6, recoveredCapture_189_7, (androidx.compose.runtime.Composer)recoveredArgument_189_0, ((java.lang.Integer)recoveredArgument_189_1).intValue())));
      }
   }

   private static final List getActiveAdminsAvatars() {
      Iterable var0 = CollectionsKt.take(((TeamPresence)Injector.get().getDataLayer().getTeamPresence().getValue()).getBuiltActiveAdmins(), 3);
      int var1 = 0;
      Iterable var2 = var0;
      ArrayList var3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var0, 10));
      int var4 = 0;

      for (Object var6 : var2) {
         Participant var7 = (Participant)var6;
         Collection var9 = var3;
         int var8 = 0;
         Avatar var10002 = var7.getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10002, "getAvatar(...)");
         Boolean var10003 = var7.isBot();
         Intrinsics.checkNotNullExpressionValue(var10003, "isBot(...)");
         var9.add(new AvatarWrapper(var10002, var10003));
      }

      return (List)var3;
   }

   private static final String getWorkspaceName() {
      return ((AppConfig)Injector.get().getAppConfigProvider().get()).getName();
   }

   private static final String getUserIntercomId() {
      String var10000;
      if (Injector.isNotInitialised()) {
         var10000 = "";
      } else {
         var10000 = Injector.get().getUserIdentity().getIntercomId();
         Intrinsics.checkNotNullExpressionValue(var10000, "getIntercomId(...)");
      }

      return var10000;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnreadIndicator(@Nullable Modifier var0, @Nullable Composer var1, int var2, int var3) {
      var1 = var1.startRestartGroup(481161991);
      ComposerKt.sourceInformation(var1, "C(UnreadIndicator)159@7087L370:ConversationItem.kt#arqjag");
      int var4 = var2;
      if ((var3 & 1) != 0) {
         var4 |= 6;
      } else if ((var2 & 6) == 0) {
         var4 |= var1.changed(var0) ? 4 : 2;
      }

      if ((var4 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if ((var3 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(481161991, var4, -1, "io.intercom.android.sdk.m5.components.UnreadIndicator (ConversationItem.kt:158)");
         }

         int var5 = 16;
         int var6 = 0;
         Modifier var41 = SizeKt.recovered$111ef94c93e78c04(var0, Dp.recovered$95b3d103663bf060(var5));
         Alignment var42 = Alignment.Companion.getCenterEnd();
         byte var8 = 48;
         int var9 = 0;
         ComposerKt.sourceInformationMarkerStart(
            var1, 1042775818, "CC(Box)N(modifier,contentAlignment,propagateMinConstraints,content)71@3424L131:Box.kt#2w3rfo"
         );
         boolean var7 = false;
         MeasurePolicy var10 = BoxKt.maybeCachedBoxMeasurePolicy(var42, var7);
         Modifier var11 = var41;
         MeasurePolicy var12 = var10;
         Composer var13 = var1;
         int var14 = 112 & var8 << 3;
         int var15 = 0;
         ComposerKt.sourceInformationMarkerStart(var13, -1159599143, "CC(Layout)N(content,modifier,measurePolicy)81@3355L27,84@3521L415:Layout.kt#80mrfh");
         int var16 = Long.hashCode(ComposablesKt.getCurrentCompositeKeyHashCode(var13, 0));
         CompositionLocalMap var17 = var13.getCurrentCompositionLocalMap();
         Modifier var18 = ComposedModifierKt.materializeModifier(var13, var11);
         Function0 var19 = ComposeUiNode.Companion.getConstructor();
         Composer var20 = var13;
         int var21 = 6 | 896 & var14 << 6;
         int var22 = 0;
         ComposerKt.sourceInformationMarkerStart(var20, -553112988, "CC(ReusableComposeNode)N(factory,update,content)410@16187L9:Composables.kt#9igjgp");
         if (!(var20.getApplier() instanceof Applier)) {
            ComposablesKt.invalidApplier();
         }

         var20.startReusableNode();
         if (var20.getInserting()) {
            var20.createNode(var19);
         } else {
            var20.useNode();
         }

         Composer var23 = Updater.recovered$676aa17b44fd6280(var20);
         int var24 = 0;
         Updater.recovered$89682b199a142880(var23, var12, ComposeUiNode.Companion.getSetMeasurePolicy());
         Updater.recovered$89682b199a142880(var23, var17, ComposeUiNode.Companion.getSetResolvedCompositionLocals());
         Updater.recovered$89682b199a142880(var23, var16, ComposeUiNode.Companion.getSetCompositeKeyHash());
         Updater.recovered$3a70c9faca87a3d9(var23, ComposeUiNode.Companion.getApplyOnDeactivatedNodeAssertion());
         Updater.recovered$89682b199a142880(var23, var18, ComposeUiNode.Companion.getSetModifier());
         int var25 = 14 & var21 >> 6;
         Composer var26 = var20;
         int var27 = 0;
         ComposerKt.sourceInformationMarkerStart(var26, 1833054614, "C72@3469L9:Box.kt#2w3rfo");
         int var28 = 6 | 112 & var8 >> 6;
         Composer var29 = var26;
         BoxScope var30 = BoxScopeInstance.INSTANCE;
         int var31 = 0;
         ComposerKt.sourceInformationMarkerStart(var29, -1604911102, "C165@7260L180,163@7187L264:ConversationItem.kt#arqjag");
         Modifier var10000 = Modifier.Companion;
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         var10000 = SizeKt.recovered$111ef94c93e78c04(var10000, Dp.recovered$95b3d103663bf060($this$dp$ivx));
         var29.startReplaceGroup(-1160147731);
         ComposerKt.sourceInformation(var29, "CC(remember):ConversationItem.kt#9igjgp");
         Composer var44 = var29;
         boolean var34 = false;
         int var35 = 0;
         Object var36 = var44.rememberedValue();
         int var37 = 0;
         kotlin.jvm.functions.Function1 var48;
         if (var36 == Composer.Companion.getEmpty()) {
            Modifier var38 = var10000;
            int var39 = 0;
            var48 = ((kotlin.jvm.functions.Function1) (recoveredArgument_194_0) -> ConversationItemKt.UnreadIndicator$lambda$6$lambda$5$lambda$4((androidx.compose.ui.graphics.drawscope.DrawScope)recoveredArgument_194_0));
            var10000 = var38;
            Object var45 = var48;
            var44.updateRememberedValue(var45);
            var48 = (Function1)var45;
         } else {
            var48 = (Function1)var36;
         }

         Function1 var43 = var48;
         var29.endReplaceGroup();
         CanvasKt.Canvas(var10000, var43, var29, 54);
         ComposerKt.sourceInformationMarkerEnd(var29);
         ComposerKt.sourceInformationMarkerEnd(var26);
         var20.endNode();
         ComposerKt.sourceInformationMarkerEnd(var20);
         ComposerKt.sourceInformationMarkerEnd(var13);
         ComposerKt.sourceInformationMarkerEnd(var1);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var47 = var1.endRestartGroup();
      if (var47 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_195_0 = var0;
         final int recoveredCapture_195_1 = var2;
         final int recoveredCapture_195_2 = var3;
         var47.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_195_0, recoveredArgument_195_1) -> ConversationItemKt.UnreadIndicator$lambda$7(recoveredCapture_195_0, recoveredCapture_195_1, recoveredCapture_195_2, (androidx.compose.runtime.Composer)recoveredArgument_195_0, ((java.lang.Integer)recoveredArgument_195_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnreadConversationCardPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-1292079862);
      ComposerKt.sourceInformation(var0, "C(UnreadConversationCardPreview)178@7542L316:ConversationItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1292079862, var1, -1, "io.intercom.android.sdk.m5.components.UnreadConversationCardPreview (ConversationItem.kt:177)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationItemKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_192_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_192_0, recoveredArgument_192_1) -> ConversationItemKt.UnreadConversationCardPreview$lambda$8(recoveredCapture_192_0, (androidx.compose.runtime.Composer)recoveredArgument_192_0, ((java.lang.Integer)recoveredArgument_192_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void UnreadConversationCardWithBotPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-516742229);
      ComposerKt.sourceInformation(var0, "C(UnreadConversationCardWithBotPreview)192@7950L323:ConversationItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -516742229, var1, -1, "io.intercom.android.sdk.m5.components.UnreadConversationCardWithBotPreview (ConversationItem.kt:191)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationItemKt.INSTANCE.recovered$1400af5e34386102(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_193_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_193_0, recoveredArgument_193_1) -> ConversationItemKt.UnreadConversationCardWithBotPreview$lambda$9(recoveredCapture_193_0, (androidx.compose.runtime.Composer)recoveredArgument_193_0, ((java.lang.Integer)recoveredArgument_193_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReadConversationCardPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(167408241);
      ComposerKt.sourceInformation(var0, "C(ReadConversationCardPreview)206@8356L336:ConversationItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(167408241, var1, -1, "io.intercom.android.sdk.m5.components.ReadConversationCardPreview (ConversationItem.kt:205)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationItemKt.INSTANCE.recovered$fd838d44303763da(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_190_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_190_0, recoveredArgument_190_1) -> ConversationItemKt.ReadConversationCardPreview$lambda$10(recoveredCapture_190_0, (androidx.compose.runtime.Composer)recoveredArgument_190_0, ((java.lang.Integer)recoveredArgument_190_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationCardWithLongTitlePreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(-672592241);
      ComposerKt.sourceInformation(var0, "C(ConversationCardWithLongTitlePreview)220@8784L702:ConversationItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -672592241, var1, -1, "io.intercom.android.sdk.m5.components.ConversationCardWithLongTitlePreview (ConversationItem.kt:219)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationItemKt.INSTANCE.recovered$54bdcf11cafa5012(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_187_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_187_0, recoveredArgument_187_1) -> ConversationItemKt.ConversationCardWithLongTitlePreview$lambda$11(recoveredCapture_187_0, (androidx.compose.runtime.Composer)recoveredArgument_187_0, ((java.lang.Integer)recoveredArgument_187_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void TypingConversationCardPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1953914578);
      ComposerKt.sourceInformation(var0, "C(TypingConversationCardPreview)241@9571L588:ConversationItem.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1953914578, var1, -1, "io.intercom.android.sdk.m5.components.TypingConversationCardPreview (ConversationItem.kt:240)");
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationItemKt.INSTANCE.recovered$265b6f46b79d310a(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_191_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_191_0, recoveredArgument_191_1) -> ConversationItemKt.TypingConversationCardPreview$lambda$12(recoveredCapture_191_0, (androidx.compose.runtime.Composer)recoveredArgument_191_0, ((java.lang.Integer)recoveredArgument_191_1).intValue())));
      }
   }

   private static final Conversation sampleConversation(boolean var0) {
      LastParticipatingAdmin$Builder var10000 = new LastParticipatingAdmin$Builder()
         .withFirstName("Santhosh Kumar")
         .withAvatar(new Avatar$Builder().withInitials("SK"));
      Intrinsics.checkNotNullExpressionValue(var10000, "withAvatar(...)");
      LastParticipatingAdmin$Builder var1 = var10000;
      List var2 = CollectionsKt.listOf(new Part$Builder().withSummary("last message").withCreatedAt(System.currentTimeMillis()));
      return new Conversation(
         "123",
         "Dual credit guidance",
         var0,
         null,
         var2,
         null,
         var1,
         null,
         false,
         false,
         null,
         null,
         false,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         2097064,
         null
      );
   }

   // $VF: synthetic method
   static Conversation sampleConversation$default(boolean var0, int var1, Object var2) {
      if ((var1 & 1) != 0) {
         var0 = false;
      }

      return sampleConversation(var0);
   }

   private static final Conversation sampleConversationWithBot() {
      LastParticipatingAdmin$Builder var10000 = new LastParticipatingAdmin$Builder()
         .withFirstName("Operator")
         .withAvatar(new Avatar$Builder().withInitials("O"))
         .withIsBot(true);
      Intrinsics.checkNotNullExpressionValue(var10000, "withIsBot(...)");
      LastParticipatingAdmin$Builder var0 = var10000;
      List var1 = CollectionsKt.listOf(
         new Part$Builder().withSummary("This is the last message received/sent to/from this user").withCreatedAt(System.currentTimeMillis())
      );
      return new Conversation(
         "123",
         "Account setup assistance",
         false,
         null,
         var1,
         null,
         var0,
         null,
         false,
         false,
         null,
         null,
         false,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         null,
         2097068,
         null
      );
   }

   private static final Unit ConversationItem$lambda$1$lambda$0(Function0 var0) {
      var0.invoke();
      return Unit.INSTANCE;
   }

   private static final Unit ConversationItem$lambda$2(
      Conversation var0,
      Modifier var1,
      CurrentlyTypingState var2,
      PaddingValues var3,
      boolean var4,
      Function0 var5,
      int var6,
      int var7,
      Composer var8,
      int var9
   ) {
      ConversationItem(var0, var1, var2, var3, var4, var5, var8, RecomposeScopeImplKt.updateChangedFlags(var6 | 1), var7);
      return Unit.INSTANCE;
   }

   private static final Unit UnreadIndicator$lambda$6$lambda$5$lambda$4(DrawScope var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$Canvas");
      long var10001 = ColorKt.Color(4292544041L);
      long var1 = var0.recovered$d1ee80c43f6fa69a();
      int var3 = 0;
      long var4 = var1;
      int var6 = 0;
      int var7 = (int)(var4 >> 32);
      int var8 = 0;
      float var11 = Float.intBitsToFloat(var7) / 2.0F;
      long arg0$ivx = var0.recovered$d1ee80c43f6fa69a();
      int var14 = 0;
      long value$iv$ivx = arg0$ivx;
      var7 = 0;
      var8 = (int)(value$iv$ivx & 4294967295L);
      int $i$f$floatFromBitsx = 0;
      float var12 = Float.intBitsToFloat(var8) / 2.0F;
      var3 = 0;
      float var15 = var11;
      float var16 = var12;
      var6 = 0;
      long var19 = Float.floatToRawIntBits(var15);
      long var21 = Float.floatToRawIntBits(var16);
      DrawScope.recovered$34dce0a89fdcb963(
         var0, var10001, 0.0F, Offset.recovered$8c837f3e34208708(var19 << 32 | var21 & 4294967295L), 0.0F, null, null, 0, 122, null
      );
      return Unit.INSTANCE;
   }

   private static final Unit UnreadIndicator$lambda$7(Modifier var0, int var1, int var2, Composer var3, int var4) {
      UnreadIndicator(var0, var3, RecomposeScopeImplKt.updateChangedFlags(var1 | 1), var2);
      return Unit.INSTANCE;
   }

   private static final Unit UnreadConversationCardPreview$lambda$8(int var0, Composer var1, int var2) {
      UnreadConversationCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit UnreadConversationCardWithBotPreview$lambda$9(int var0, Composer var1, int var2) {
      UnreadConversationCardWithBotPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ReadConversationCardPreview$lambda$10(int var0, Composer var1, int var2) {
      ReadConversationCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit ConversationCardWithLongTitlePreview$lambda$11(int var0, Composer var1, int var2) {
      ConversationCardWithLongTitlePreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit TypingConversationCardPreview$lambda$12(int var0, Composer var1, int var2) {
      TypingConversationCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   public static final Conversation access$sampleConversationWithBot() {
      return sampleConversationWithBot();
   }

   // $VF: synthetic method
   public static final String access$getWorkspaceName() {
      return getWorkspaceName();
   }

   // $VF: synthetic method
   public static final String access$getUserIntercomId() {
      return getUserIntercomId();
   }

   // $VF: synthetic method
   public static final List access$getActiveAdminsAvatars() {
      return getActiveAdminsAvatars();
   }
}
