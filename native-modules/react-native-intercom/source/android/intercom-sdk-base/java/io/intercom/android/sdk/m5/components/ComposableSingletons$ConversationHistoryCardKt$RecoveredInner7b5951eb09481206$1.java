package io.intercom.android.sdk.m5.components;

import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.ui.Modifier;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.LastParticipatingAdmin$Builder;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.models.Ticket;
import io.intercom.android.sdk.models.Ticket$Status;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nConversationHistoryCard.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationHistoryCardKt$lambda-1$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,112:1\n1047#2,6:113\n*S KotlinDebug\n*F\n+ 1 ConversationHistoryCard.kt\nio/intercom/android/sdk/m5/components/ComposableSingletons$ConversationHistoryCardKt$lambda-1$1\n*L\n108#1:113,6\n*E\n"
)
final class ComposableSingletons$ConversationHistoryCardKt$RecoveredInner7b5951eb09481206$1 implements Function2 {
   public static final ComposableSingletons$ConversationHistoryCardKt$RecoveredInner7b5951eb09481206$1 INSTANCE = new ComposableSingletons$ConversationHistoryCardKt$RecoveredInner7b5951eb09481206$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C107@4660L2,57@2214L2459:ConversationHistoryCard.kt#arqjag");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               -1617560620,
               var2,
               -1,
               "io.intercom.android.sdk.m5.components.ComposableSingletons$ConversationHistoryCardKt.lambda-1.<anonymous> (ConversationHistoryCard.kt:57)"
            );
         }

         Modifier var10000 = SizeKt.fillMaxWidth$default(Modifier.Companion, 0.0F, 1, null);
         List var3 = CollectionsKt.createListBuilder();
         List var4 = var3;
         String var15 = "Your recent conversations";
         Modifier var14 = var10000;
         int var5 = 0;
         int var6 = 2;

         for (int var7 = 0; var7 < var6; var7++) {
            int var8 = var7;
            int var9 = 0;
            boolean var10 = var8 == 0;
            Ticket$Status var11 = new Ticket$Status("Submitted", "submitted", null, false, 0L, 28, null);
            Ticket var12 = new Ticket(
               null, "1234", "Export button not working", null, null, null, var11, null, null, 0L, null, null, null, null, 0L, 32697, null
            );
            LastParticipatingAdmin$Builder var10001 = new LastParticipatingAdmin$Builder()
               .withFirstName("Santhosh Kumar")
               .withAvatar(new Avatar$Builder().withInitials("SK"));
            Intrinsics.checkNotNullExpressionValue(var10001, "withAvatar(...)");
            LastParticipatingAdmin$Builder var31 = var10001;
            List var13 = CollectionsKt.listOf(
               new Part$Builder().withSummary("This is the last message received/sent to/from this user").withCreatedAt(1659081886L)
            );
            var4.add(
               new Conversation(
                  "123",
                  "Export button not working",
                  var10,
                  null,
                  var13,
                  null,
                  var31,
                  null,
                  false,
                  false,
                  null,
                  null,
                  false,
                  var12,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  null,
                  2088872,
                  null
               )
            );
         }

         var6 = 2;

         for (int var23 = 0; var23 < var6; var23++) {
            int var25 = var23;
            int var27 = 0;
            boolean var30 = var25 == 0;
            LastParticipatingAdmin$Builder var38 = new LastParticipatingAdmin$Builder()
               .withFirstName("Santhosh Kumar")
               .withAvatar(new Avatar$Builder().withInitials("SK"));
            Intrinsics.checkNotNullExpressionValue(var38, "withAvatar(...)");
            LastParticipatingAdmin$Builder var32 = var38;
            List var33 = CollectionsKt.listOf(
               new Part$Builder().withSummary("This is the last message received/sent to/from this user").withCreatedAt(1659081886L)
            );
            var4.add(
               new Conversation(
                  "123",
                  "Account setup assistance",
                  var30,
                  null,
                  var33,
                  null,
                  var32,
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
               )
            );
         }

         Unit var16 = Unit.INSTANCE;
         var10000 = var14;
         String var39 = var15;
         List var10002 = CollectionsKt.build(var3);
         var1.startReplaceGroup(-1574017895);
         ComposerKt.sourceInformation(var1, "CC(remember):ConversationHistoryCard.kt#9igjgp");
         Composer var19 = var1;
         boolean var20 = false;
         var6 = 0;
         Object var24 = var19.rememberedValue();
         int var26 = 0;
         Object var10003;
         if (var24 == Composer.Companion.getEmpty()) {
            List var36 = var10002;
            var15 = var15;
            var14 = var14;
            int var28 = 0;
            Function1 var17 = ((kotlin.jvm.functions.Function1) (recoveredArgument_155_0) -> ComposableSingletons$ConversationHistoryCardKt$RecoveredInner7b5951eb09481206$1.invoke$lambda$4$lambda$3((io.intercom.android.sdk.models.Conversation)recoveredArgument_155_0));
            var10000 = var14;
            var39 = var15;
            var10002 = var36;
            Object var29 = var17;
            var19.updateRememberedValue(var29);
            var10003 = var29;
         } else {
            var10003 = var24;
         }

         Function1 var18 = (Function1)var10003;
         var1.endReplaceGroup();
         ConversationHistoryCardKt.ConversationHistoryCard(var10000, var39, var10002, var18, var1, 3126, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$4$lambda$3(Conversation var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
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
