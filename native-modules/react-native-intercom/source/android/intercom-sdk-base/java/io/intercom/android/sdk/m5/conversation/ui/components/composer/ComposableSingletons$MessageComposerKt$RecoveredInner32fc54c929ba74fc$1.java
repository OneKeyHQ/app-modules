package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.R.string;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$GifInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$MediaInsert;
import io.intercom.android.sdk.m5.conversation.states.BottomBarUiState$BottomBarButton$VoiceNotesInsert;
import io.intercom.android.sdk.m5.conversation.states.ComposerState$TextInput;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.ui.common.StringProvider;
import io.intercom.android.sdk.ui.common.StringProvider.StringRes;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMessageComposer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MessageComposerKt$lambda-7$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,677:1\n1047#2,6:678\n*S KotlinDebug\n*F\n+ 1 MessageComposer.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MessageComposerKt$lambda-7$1\n*L\n617#1:678,6\n*E\n"
)
final class ComposableSingletons$MessageComposerKt$RecoveredInner32fc54c929ba74fc$1 implements Function2 {
   public static final ComposableSingletons$MessageComposerKt$RecoveredInner32fc54c929ba74fc$1 INSTANCE = new ComposableSingletons$MessageComposerKt$RecoveredInner32fc54c929ba74fc$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C616@25297L11,615@25252L1113:MessageComposer.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1801146651,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MessageComposerKt.lambda-7.<anonymous> (MessageComposer.kt:615)"
            );
         }

         Object var10000 = null;
         var1.startReplaceGroup(1440481713);
         ComposerKt.sourceInformation(var1, "CC(remember):MessageComposer.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         kotlin.jvm.functions.Function2 var14;
         if (var7 == Composer.Companion.getEmpty()) {
            Object var10 = null;
            int var9 = 0;
            var14 = ((kotlin.jvm.functions.Function2) (recoveredArgument_553_0, recoveredArgument_553_1) -> ComposableSingletons$MessageComposerKt$RecoveredInner32fc54c929ba74fc$1.invoke$lambda$1$lambda$0((java.lang.String)recoveredArgument_553_0, (java.util.List)recoveredArgument_553_1));
            var10000 = var10;
            Object var13 = var14;
            var4.updateRememberedValue(var13);
            var14 = (Function2)var13;
         } else {
            var14 = (Function2)var7;
         }

         Function2 var3 = var14;
         var1.endReplaceGroup();
         StringProvider var10005 = new StringRes(string.intercom_send_us_a_message, null, 2, null);
         BottomBarUiState$BottomBarButton[] var11 = new BottomBarUiState$BottomBarButton[]{
            BottomBarUiState$BottomBarButton$MediaInsert.INSTANCE,
            BottomBarUiState$BottomBarButton$GifInsert.INSTANCE,
            BottomBarUiState$BottomBarButton$VoiceNotesInsert.INSTANCE
         };
         List var10007 = CollectionsKt.listOf(var11);
         MediaUploadItem[] var12 = new MediaUploadItem[2];
         Uri var10018 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         var12[0] = new MediaUploadItem(
            null,
            new MediaData$Media$Other("application/pdf", 0L, "pdf_document.pdf", var10018),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         var10018 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         var12[1] = new MediaUploadItem(
            null,
            new MediaData$Media$Other("text/plain", 0L, "txt_document.txt", var10018),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         MessageComposerKt.MessageComposer(
            (Modifier)var10000,
            var3,
            new ComposerState$TextInput("", var10005, false, var10007, CollectionsKt.listOf(var12), null, 36, null),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            var1,
            48,
            0,
            262137
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(String var0, List var1) {
      Intrinsics.checkNotNullParameter(var0, "<unused var>");
      Intrinsics.checkNotNullParameter(var1, "<unused var>");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
