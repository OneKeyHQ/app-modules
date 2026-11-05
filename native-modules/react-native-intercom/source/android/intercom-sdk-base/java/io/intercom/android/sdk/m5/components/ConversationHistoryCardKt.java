package io.intercom.android.sdk.m5.components;

import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001aC\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u0014\b\u0002\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\nH\u0001¢\u0006\u0002\u0010\u000b\u001a\r\u0010\f\u001a\u00020\u0001H\u0001¢\u0006\u0002\u0010\r¨\u0006\u000e",
   d2 = {
         "ConversationHistoryCard",
         "",
         "modifier",
         "Landroidx/compose/ui/Modifier;",
         "cardTitle",
         "",
         "conversations",
         "",
         "Lio/intercom/android/sdk/models/Conversation;",
         "onConversationClick",
         "Lkotlin/Function1;",
         "(Landroidx/compose/ui/Modifier;Ljava/lang/String;Ljava/util/List;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;II)V",
         "ConversationHistoryCardPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationHistoryCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ConversationHistoryCardKt\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,112:1\n1047#2,6:113\n*S KotlinDebug\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ConversationHistoryCardKt\n*L\n24#1:113,6\n*E\n"
)
public final class ConversationHistoryCardKt {
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationHistoryCard(
      @Nullable Modifier var0, @NotNull String var1, @NotNull List var2, @Nullable Function1 var3, @Nullable Composer var4, int var5, int var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "cardTitle");
      Intrinsics.checkNotNullParameter(var2, "conversations");
      var4 = var4.startRestartGroup(-1629591433);
      ComposerKt.sourceInformation(var4, "C(ConversationHistoryCard)P(2)23@995L2,28@1091L1013,25@1007L1097:ConversationHistoryCard.kt#arqjag");
      int var7 = var5;
      if ((var6 & 1) != 0) {
         var7 |= 6;
      } else if ((var5 & 6) == 0) {
         var7 |= var4.changed(var0) ? 4 : 2;
      }

      if ((var6 & 2) != 0) {
         var7 |= 48;
      } else if ((var5 & 48) == 0) {
         var7 |= var4.changed(var1) ? 32 : 16;
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
         if ((var6 & 1) != 0) {
            var0 = Modifier.Companion;
         }

         if ((var6 & 8) != 0) {
            var4.startReplaceGroup(-1056310787);
            ComposerKt.sourceInformation(var4, "CC(remember):ConversationHistoryCard.kt#9igjgp");
            Composer var9 = var4;
            boolean var10 = false;
            int var11 = 0;
            Object var12 = var9.rememberedValue();
            int var13 = 0;
            Object var10000;
            if (var12 == Composer.Companion.getEmpty()) {
               int var14 = 0;
               Object var16 = ((kotlin.jvm.functions.Function1) (recoveredArgument_182_0) -> ConversationHistoryCardKt.ConversationHistoryCard$lambda$1$lambda$0((io.intercom.android.sdk.models.Conversation)recoveredArgument_182_0));
               var9.updateRememberedValue(var16);
               var10000 = var16;
            } else {
               var10000 = var12;
            }

            Function1 var8 = (Function1)var10000;
            var4.endReplaceGroup();
            var3 = var8;
         }

         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(-1629591433, var7, -1, "io.intercom.android.sdk.m5.components.ConversationHistoryCard (ConversationHistoryCard.kt:24)");
         }

         HomeCardScaffoldKt.HomeCardScaffold(
            var0,
            var1,
            ComposableLambdaKt.rememberComposableLambda(1614953259, true, new ConversationHistoryCardKt$ConversationHistoryCard$2(var2, var3), var4, 54),
            var4,
            384 | 14 & var7 | 112 & var7,
            0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var17 = var4.endRestartGroup();
      if (var17 != null) {
         final androidx.compose.ui.Modifier recoveredCapture_183_0 = var0;
         final java.lang.String recoveredCapture_183_1 = var1;
         final java.util.List recoveredCapture_183_2 = var2;
         final kotlin.jvm.functions.Function1 recoveredCapture_183_3 = var3;
         final int recoveredCapture_183_4 = var5;
         final int recoveredCapture_183_5 = var6;
         var17.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_183_0, recoveredArgument_183_1) -> ConversationHistoryCardKt.ConversationHistoryCard$lambda$2(recoveredCapture_183_0, recoveredCapture_183_1, recoveredCapture_183_2, recoveredCapture_183_3, recoveredCapture_183_4, recoveredCapture_183_5, (androidx.compose.runtime.Composer)recoveredArgument_183_0, ((java.lang.Integer)recoveredArgument_183_1).intValue())));
      }
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public static final void ConversationHistoryCardPreview(@Nullable Composer var0, int var1) {
      var0 = var0.startRestartGroup(1923666474);
      ComposerKt.sourceInformation(var0, "C(ConversationHistoryCardPreview)56@2190L2489:ConversationHistoryCard.kt#arqjag");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               1923666474, var1, -1, "io.intercom.android.sdk.m5.components.ConversationHistoryCardPreview (ConversationHistoryCard.kt:55)"
            );
         }

         IntercomThemeKt.IntercomTheme(null, null, null, ComposableSingletons$ConversationHistoryCardKt.INSTANCE.recovered$eac478aa65045e5d(), var0, 3072, 7);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var10000 = var0.endRestartGroup();
      if (var10000 != null) {
         final int recoveredCapture_184_0 = var1;
         var10000.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_184_0, recoveredArgument_184_1) -> ConversationHistoryCardKt.ConversationHistoryCardPreview$lambda$3(recoveredCapture_184_0, (androidx.compose.runtime.Composer)recoveredArgument_184_0, ((java.lang.Integer)recoveredArgument_184_1).intValue())));
      }
   }

   private static final Unit ConversationHistoryCard$lambda$1$lambda$0(Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit ConversationHistoryCard$lambda$2(
      Modifier var0, String var1, List var2, Function1 var3, int var4, int var5, Composer var6, int var7
   ) {
      ConversationHistoryCard(var0, var1, var2, var3, var6, RecomposeScopeImplKt.updateChangedFlags(var4 | 1), var5);
      return Unit.INSTANCE;
   }

   private static final Unit ConversationHistoryCardPreview$lambda$3(int var0, Composer var1, int var2) {
      ConversationHistoryCardPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
