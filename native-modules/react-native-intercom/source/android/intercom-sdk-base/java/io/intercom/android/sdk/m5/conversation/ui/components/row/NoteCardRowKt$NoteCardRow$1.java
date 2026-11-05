package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.unit.Dp;
import io.intercom.android.sdk.m5.components.avatar.AvatarWrapper;
import io.intercom.android.sdk.models.Avatar;
import io.intercom.android.sdk.models.Part;
import io.intercom.android.sdk.ui.theme.IntercomTheme;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nNoteCardRow.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NoteCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NoteCardRowKt$NoteCardRow$1\n+ 2 Dp.kt\nandroidx/compose/ui/unit/DpKt\n*L\n1#1,67:1\n118#2:68\n*S KotlinDebug\n*F\n+ 1 NoteCardRow.kt\nio/intercom/android/sdk/m5/conversation/ui/components/row/NoteCardRowKt$NoteCardRow$1\n*L\n38#1:68\n*E\n"
)
final class NoteCardRowKt$NoteCardRow$1 implements Function3 {
   // $VF: synthetic field
   final Part $part;
   // $VF: synthetic field
   final String $companyName;

   NoteCardRowKt$NoteCardRow$1(Part var1, String var2) {
      this.$part = var1;
      this.$companyName = var2;
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(ColumnScope var1, Composer var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "$this$IntercomCard");
      ComposerKt.sourceInformation(var2, "C34@1362L6,26@1001L488:NoteCardRow.kt#hr3bi5");
      if ((var3 & 17) == 16 && var2.getSkipping()) {
         var2.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               2124316578, var3, -1, "io.intercom.android.sdk.m5.conversation.ui.components.row.NoteCardRow.<anonymous> (NoteCardRow.kt:26)"
            );
         }

         List var10000 = this.$part.getBlocks();
         Intrinsics.checkNotNullExpressionValue(var10000, "getBlocks(...)");
         String var10001 = this.$part.getParticipant().getForename();
         Intrinsics.checkNotNullExpressionValue(var10001, "getForename(...)");
         String var10002 = this.$companyName;
         Avatar var10005 = this.$part.getParticipant().getAvatar();
         Intrinsics.checkNotNullExpressionValue(var10005, "getAvatar(...)");
         Boolean var10006 = this.$part.getParticipant().isBot();
         Intrinsics.checkNotNullExpressionValue(var10006, "isBot(...)");
         AvatarWrapper var10003 = new AvatarWrapper(var10005, var10006);
         long var10004 = IntercomTheme.INSTANCE.getColors(var2, IntercomTheme.$stable).getText().recovered$e01a1d3b7b68e21a();
         Modifier var6 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         int var4 = 16;
         int var5 = 0;
         PostCardRowKt.recovered$e9e5bdd615bcb374(
            var10000, var10001, var10002, var10003, var10004, PaddingKt.recovered$eb6a744cd98cdbca(var6, Dp.recovered$95b3d103663bf060(var4)), var2, 196608, 0
         );
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2, Object var3) {
      this.invoke((ColumnScope)var1, (Composer)var2, ((Number)var3).intValue());
      return Unit.INSTANCE;
   }
}
