package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.animation.AnimatedVisibilityKt;
import androidx.compose.animation.EnterExitTransitionKt;
import androidx.compose.animation.core.FiniteAnimationSpec;
import androidx.compose.animation.core.MutableTransitionState;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.FlowLayoutKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.Arrangement.Horizontal;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
import io.intercom.android.sdk.models.ReplyOption;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a9\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0001¢\u0006\u0002\u0010\t\u001a9\u0010\n\u001a\u00020\u00012\b\b\u0002\u0010\u0007\u001a\u00020\b2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0002\u0010\u000e\u001a9\u0010\u000f\u001a\u00020\u00012\b\b\u0002\u0010\u0007\u001a\u00020\b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\u00032\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0002\u0010\u000e\u001a/\u0010\u0013\u001a\u00020\u00012\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00032\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0006H\u0001¢\u0006\u0002\u0010\u0014\u001a\r\u0010\u0015\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\u0016¨\u0006\u0017",
   d2 = {
         "QuickReplies",
         "",
         "quickReplies",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/QuickReply;",
         "onQuickReplyClick",
         "Lkotlin/Function1;",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V",
         "ReplyOptions",
         "replyOptions",
         "Lio/intercom/android/sdk/models/ReplyOption;",
         "onReplyClicked",
         "(Landroidx/compose/ui/Modifier;Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "ComposerSuggestions",
         "suggestions",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "onSuggestionClick",
         "AnimatedQuickReplies",
         "(Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;I)V",
         "QuickRepliesPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nQuickReplies.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Composer.kt\nandroidx/compose/runtime/ComposerKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,144:1\n118#2:145\n118#2:146\n1563#3:147\n1634#3,3:148\n1563#3:157\n1634#3,3:158\n1047#4,6:151\n1047#4,6:161\n1047#4,6:167\n1047#4,6:173\n1#5:179\n*S KotlinDebug\n*F\n+ 1 QuickReplies.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/QuickRepliesKt\n*L\n37#1:145\n38#1:146\n72#1:147\n72#1:148,3\n87#1:157\n87#1:158,3\n73#1:151,6\n88#1:161,6\n100#1:167,6\n108#1:173,6\n*E\n"
)
public final class QuickRepliesKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void QuickReplies(@NotNull List var0, @NotNull Function1 var1, @Nullable Modifier var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var0, "quickReplies");
      Intrinsics.checkNotNullParameter(var1, "onQuickReplyClick");
      Composer var9 = var3.startRestartGroup(368433331);
      ComposerKt.sourceInformation(var9, "C(QuickReplies)P(2,1)38@1673L938,34@1463L1148:QuickReplies.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var9.changedInstance(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var9.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var9.changed(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var9.getSkipping()) {
         var9.skipToGroupEnd();
      } else {
         if ((var5 & 4) != 0) {
            var2 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(368433331, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReplies (QuickReplies.kt:33)");
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(var2, 0.0F, 1, null);
         int var7 = 8;
         int var8 = 0;
         Horizontal var10001 = Arrangement.INSTANCE.recovered$49b0bf381d0b9a78(Dp.recovered$95b3d103663bf060(var7), Alignment.Companion.getEnd());
         int $this$dp$ivx = 8;
         int $i$f$getDpx = 0;
         FlowLayoutKt.FlowRow(
            var10000,
            var10001,
            Arrangement.INSTANCE.recovered$6fba629a41b87d9d(Dp.recovered$95b3d103663bf060($this$dp$ivx), Alignment.Companion.getBottom()),
            null,
            0,
            0,
            ComposableLambdaKt.rememberComposableLambda(-458232018, true, new QuickRepliesKt$QuickReplies$1(var0, var1), var9, 54),
            var9,
            1573296,
            56
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var12 = var9.endRestartGroup();
      if (var12 != null) {
         final java.util.List recoveredExpressionCapture_774_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_774_1 = var1;
         final androidx.compose.ui.Modifier recoveredExpressionCapture_774_2 = var2;
         final int recoveredExpressionCapture_774_3 = var4;
         final int recoveredExpressionCapture_774_4 = var5;
         var12.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_774_0, recoveredExpressionArgument_774_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.QuickReplies$lambda$0(recoveredExpressionCapture_774_0, recoveredExpressionCapture_774_1, recoveredExpressionCapture_774_2, recoveredExpressionCapture_774_3, recoveredExpressionCapture_774_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_774_0), ((java.lang.Integer)recoveredExpressionArgument_774_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ReplyOptions(@Nullable Modifier var0, @NotNull List var1, @NotNull Function1 var2, @Nullable Composer var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "replyOptions");
      Intrinsics.checkNotNullParameter(var2, "onReplyClicked");
      Composer var17 = var3.startRestartGroup(-1003293676);
      ComposerKt.sourceInformation(var17, "C(ReplyOptions)P(!1,2)72@2918L124,69@2773L272:QuickReplies.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var17.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var17.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var17.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var17.getSkipping()) {
         var17.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1003293676, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.ReplyOptions (QuickReplies.kt:69)");
         }

         Iterable var7 = var1;
         int var8 = 0;
         Iterable var9 = var7;
         ArrayList var10 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
         int var11 = 0;

         for (Object var13 : var9) {
            ReplyOption var14 = (ReplyOption)var13;
            Collection var16 = var10;
            int var15 = 0;
            var16.add(new QuickReply(var14.getUuid(), var14.getText()));
         }

         List var10000 = (List)var10;
         var17.startReplaceGroup(748073467);
         ComposerKt.sourceInformation(var17, "CC(remember):QuickReplies.kt#9igjgp");
         Composer var19 = var17;
         boolean var20 = var17.changedInstance(var1) | (var6 & 896) == 256;
         int var21 = 0;
         Object var22 = var19.rememberedValue();
         int var23 = 0;
         Object var28;
         if (!var20 && var22 != Composer.Companion.getEmpty()) {
            var28 = var22;
         } else {
            List var26 = var10000;
            int var24 = 0;
            final java.util.List recoveredExpressionCapture_776_0 = var1;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_776_1 = var2;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_776_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.ReplyOptions$lambda$5$lambda$4(recoveredExpressionCapture_776_0, recoveredExpressionCapture_776_1, (io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply) ((io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply)recoveredExpressionArgument_776_0)));
            var10000 = var26;
            Object var25 = var10001;
            var19.updateRememberedValue(var25);
            var28 = var25;
         }

         Function1 var18 = (Function1)var28;
         var17.endReplaceGroup();
         QuickReplies(var10000, var18, var0, var17, 896 & var6 << 6, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var17.endRestartGroup();
      if (var27 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_777_0 = var0;
         final java.util.List recoveredExpressionCapture_777_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_777_2 = var2;
         final int recoveredExpressionCapture_777_3 = var4;
         final int recoveredExpressionCapture_777_4 = var5;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_777_0, recoveredExpressionArgument_777_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.ReplyOptions$lambda$6(recoveredExpressionCapture_777_0, recoveredExpressionCapture_777_1, recoveredExpressionCapture_777_2, recoveredExpressionCapture_777_3, recoveredExpressionCapture_777_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_777_0), ((java.lang.Integer)recoveredExpressionArgument_777_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ComposerSuggestions(
      @Nullable Modifier var0, @NotNull List var1, @NotNull Function1 var2, @Nullable Composer var3, int var4, int var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "suggestions");
      Intrinsics.checkNotNullParameter(var2, "onSuggestionClick");
      Composer var17 = var3.startRestartGroup(-719570861);
      ComposerKt.sourceInformation(var17, "C(ComposerSuggestions)P(!1,2)87@3364L124,84@3222L269:QuickReplies.kt#hr3bi5");
      int var6 = var4;
      if ((var5 & 1) != 0) {
         var6 |= 6;
      } else if ((var4 & 6) == 0) {
         var6 |= var17.changed(var0) ? 4 : 2;
      }

      if ((var5 & 2) != 0) {
         var6 |= 48;
      } else if ((var4 & 48) == 0) {
         var6 |= var17.changedInstance(var1) ? 32 : 16;
      }

      if ((var5 & 4) != 0) {
         var6 |= 384;
      } else if ((var4 & 384) == 0) {
         var6 |= var17.changedInstance(var2) ? 256 : 128;
      }

      if ((var6 & 147) == 146 && var17.getSkipping()) {
         var17.skipToGroupEnd();
      } else {
         if ((var5 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -719570861, var6, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.ComposerSuggestions (QuickReplies.kt:84)"
            );
         }

         Iterable var7 = var1;
         int var8 = 0;
         Iterable var9 = var7;
         ArrayList var10 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var7, 10));
         int var11 = 0;

         for (Object var13 : var9) {
            ReplySuggestion var14 = (ReplySuggestion)var13;
            Collection var16 = var10;
            int var15 = 0;
            var16.add(new QuickReply(var14.getId(), var14.getText()));
         }

         List var10000 = (List)var10;
         var17.startReplaceGroup(-1321220320);
         ComposerKt.sourceInformation(var17, "CC(remember):QuickReplies.kt#9igjgp");
         Composer var19 = var17;
         boolean var20 = var17.changedInstance(var1) | (var6 & 896) == 256;
         int var21 = 0;
         Object var22 = var19.rememberedValue();
         int var23 = 0;
         Object var28;
         if (!var20 && var22 != Composer.Companion.getEmpty()) {
            var28 = var22;
         } else {
            List var26 = var10000;
            int var24 = 0;
            final java.util.List recoveredExpressionCapture_772_0 = var1;
            final kotlin.jvm.functions.Function1 recoveredExpressionCapture_772_1 = var2;
            Function1 var10001 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_772_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.ComposerSuggestions$lambda$11$lambda$10(recoveredExpressionCapture_772_0, recoveredExpressionCapture_772_1, (io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply) ((io.intercom.android.sdk.m5.conversation.ui.components.row.QuickReply)recoveredExpressionArgument_772_0)));
            var10000 = var26;
            Object var25 = var10001;
            var19.updateRememberedValue(var25);
            var28 = var25;
         }

         Function1 var18 = (Function1)var28;
         var17.endReplaceGroup();
         QuickReplies(var10000, var18, var0, var17, 896 & var6 << 6, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var27 = var17.endRestartGroup();
      if (var27 != null) {
         final androidx.compose.ui.Modifier recoveredExpressionCapture_773_0 = var0;
         final java.util.List recoveredExpressionCapture_773_1 = var1;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_773_2 = var2;
         final int recoveredExpressionCapture_773_3 = var4;
         final int recoveredExpressionCapture_773_4 = var5;
         var27.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_773_0, recoveredExpressionArgument_773_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.ComposerSuggestions$lambda$12(recoveredExpressionCapture_773_0, recoveredExpressionCapture_773_1, recoveredExpressionCapture_773_2, recoveredExpressionCapture_773_3, recoveredExpressionCapture_773_4, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_773_0), ((java.lang.Integer)recoveredExpressionArgument_773_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void AnimatedQuickReplies(@NotNull List var0, @NotNull Function1 var1, @Nullable Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var0, "replyOptions");
      Intrinsics.checkNotNullParameter(var1, "onReplyClicked");
      Composer var20 = var2.startRestartGroup(-2072519615);
      ComposerKt.sourceInformation(var20, "C(AnimatedQuickReplies)P(1)99@3645L151,107@3907L10,109@3963L243,105@3801L405:QuickReplies.kt#hr3bi5");
      int var4 = var3;
      if ((var3 & 6) == 0) {
         var4 |= var20.changedInstance(var0) ? 4 : 2;
      }

      if ((var3 & 48) == 0) {
         var4 |= var20.changedInstance(var1) ? 32 : 16;
      }

      if ((var4 & 19) == 18 && var20.getSkipping()) {
         var20.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -2072519615, var4, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.AnimatedQuickReplies (QuickReplies.kt:98)"
            );
         }

         var20.startReplaceGroup(-407353664);
         ComposerKt.sourceInformation(var20, "CC(remember):QuickReplies.kt#9igjgp");
         Composer var7 = var20;
         boolean var8 = false;
         int var9 = 0;
         Object var10 = var7.rememberedValue();
         int var11 = 0;
         Object var10000;
         if (var10 == Composer.Companion.getEmpty()) {
            int var12 = 0;
            MutableTransitionState var13 = new MutableTransitionState(false);
            MutableTransitionState var14 = var13;
            int var15 = 0;
            var14.setTargetState(true);
            Object var27 = var13;
            var7.updateRememberedValue(var27);
            var10000 = var27;
         } else {
            var10000 = var10;
         }

         MutableTransitionState var6 = (MutableTransitionState)var10000;
         var20.endReplaceGroup();
         MutableTransitionState var5 = var6;
         MutableTransitionState var30 = var5;
         Object var10001 = null;
         Object var10002 = null;
         var20.startReplaceGroup(-407345421);
         ComposerKt.sourceInformation(var20, "CC(remember):QuickReplies.kt#9igjgp");
         Composer var22 = var20;
         boolean var23 = false;
         int $i$f$cachex = 0;
         Object it$ivx = var22.rememberedValue();
         int var26/* $VF was: $i$a$-let-ComposerKt$cache$1$ivx */ = 0;
         Object var10003;
         if (it$ivx == Composer.Companion.getEmpty()) {
            Object var18 = null;
            Object var17 = null;
            MutableTransitionState var16 = var5;
            int var28 = 0;
            Function1 var19 = ((kotlin.jvm.functions.Function1) (recoveredExpressionArgument_770_0) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.AnimatedQuickReplies$lambda$16$lambda$15(((java.lang.Integer)recoveredExpressionArgument_770_0).intValue()));
            var30 = var16;
            var10001 = var17;
            var10002 = var18;
            Object var29 = var19;
            var22.updateRememberedValue(var29);
            var10003 = var29;
         } else {
            var10003 = it$ivx;
         }

         Function1 var21 = (Function1)var10003;
         var20.endReplaceGroup();
         AnimatedVisibilityKt.AnimatedVisibility(
            var30,
            (Modifier)var10001,
            EnterExitTransitionKt.slideInVertically$default((FiniteAnimationSpec)var10002, var21, 1, null)
               .plus(EnterExitTransitionKt.fadeIn$default(null, 0.0F, 3, null)),
            EnterExitTransitionKt.fadeOut$default(null, 0.0F, 3, null),
            null,
            ComposableLambdaKt.rememberComposableLambda(992499481, true, new QuickRepliesKt$AnimatedQuickReplies$2(var0, var1), var20, 54),
            var20,
            200064 | MutableTransitionState.$stable,
            18
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var31 = var20.endRestartGroup();
      if (var31 != null) {
         final java.util.List recoveredExpressionCapture_771_0 = var0;
         final kotlin.jvm.functions.Function1 recoveredExpressionCapture_771_1 = var1;
         final int recoveredExpressionCapture_771_2 = var3;
         var31.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_771_0, recoveredExpressionArgument_771_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.AnimatedQuickReplies$lambda$17(recoveredExpressionCapture_771_0, recoveredExpressionCapture_771_1, recoveredExpressionCapture_771_2, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_771_0), ((java.lang.Integer)recoveredExpressionArgument_771_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void QuickRepliesPreview(@Nullable Composer var0, int var1) {
      Composer var2 = var0.startRestartGroup(1503246755);
      ComposerKt.sourceInformation(var2, "C(QuickRepliesPreview)128@4358L432:QuickReplies.kt#hr3bi5");
      if (var1 == 0 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1503246755, var1, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesPreview (QuickReplies.kt:127)"
            );
         }

         IntercomThemeKt.IntercomTheme(
            null,
            null,
            null,
            io.intercom.android.sdk.m5.conversation.ui.components.row.ComposableSingletons$QuickRepliesKt.INSTANCE.recovered$1400af5e34386102(),
            var2,
            3072,
            7
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var2.endRestartGroup();
      if (var10000 != null) {
         final int recoveredExpressionCapture_775_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredExpressionArgument_775_0, recoveredExpressionArgument_775_1) -> io.intercom.android.sdk.m5.conversation.ui.components.row.QuickRepliesKt.QuickRepliesPreview$lambda$18(recoveredExpressionCapture_775_0, (androidx.compose.runtime.Composer) ((androidx.compose.runtime.Composer)recoveredExpressionArgument_775_0), ((java.lang.Integer)recoveredExpressionArgument_775_1).intValue())));
      }
   }

   private static final Unit QuickReplies$lambda$0(List var0, Function1 var1, Modifier var2, int var3, int var4, Composer var5, int var6) {
      QuickReplies(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ReplyOptions$lambda$5$lambda$4(List var0, Function1 var1, QuickReply var2) {
      Intrinsics.checkNotNullParameter(var2, "quickReply");
      Iterable var3 = var0;
      Iterator var4 = var3.iterator();

      Object var10000;
      while (true) {
         if (var4.hasNext()) {
            Object var5 = var4.next();
            ReplyOption var6 = (ReplyOption)var5;
            int var7 = 0;
            if (!Intrinsics.areEqual(var6.getUuid(), var2.getId())) {
               continue;
            }

            var10000 = var5;
            break;
         }

         var10000 = null;
         break;
      }

      ReplyOption var10 = (ReplyOption)var10000;
      if ((ReplyOption)var10000 != null) {
         ReplyOption var8 = var10;
         int var9 = 0;
         var1.invoke(var8);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ReplyOptions$lambda$6(Modifier var0, List var1, Function1 var2, int var3, int var4, Composer var5, int var6) {
      ReplyOptions(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final Unit ComposerSuggestions$lambda$11$lambda$10(List var0, Function1 var1, QuickReply var2) {
      Intrinsics.checkNotNullParameter(var2, "quickReply");
      Iterable var3 = var0;
      Iterator var4 = var3.iterator();

      Object var10000;
      while (true) {
         if (var4.hasNext()) {
            Object var5 = var4.next();
            ReplySuggestion var6 = (ReplySuggestion)var5;
            int var7 = 0;
            if (!Intrinsics.areEqual(var6.getId(), var2.getId())) {
               continue;
            }

            var10000 = var5;
            break;
         }

         var10000 = null;
         break;
      }

      ReplySuggestion var10 = (ReplySuggestion)var10000;
      if ((ReplySuggestion)var10000 != null) {
         ReplySuggestion var8 = var10;
         int var9 = 0;
         var1.invoke(var8);
      }

      return Unit.INSTANCE;
   }

   private static final Unit ComposerSuggestions$lambda$12(Modifier var0, List var1, Function1 var2, int var3, int var4, Composer var5, int var6) {
      ComposerSuggestions(var0, var1, var2, var5, RecomposeScopeImplKt.updateChangedFlags(var3 | 1), var4);
      return Unit.INSTANCE;
   }

   private static final int AnimatedQuickReplies$lambda$16$lambda$15(int var0) {
      return var0 / 2;
   }

   private static final Unit AnimatedQuickReplies$lambda$17(List var0, Function1 var1, int var2, Composer var3, int var4) {
      AnimatedQuickReplies(var0, var1, var3, RecomposeScopeImplKt.updateChangedFlags(var2 | 1));
      return Unit.INSTANCE;
   }

   private static final Unit QuickRepliesPreview$lambda$18(int var0, Composer var1, int var2) {
      QuickRepliesPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
