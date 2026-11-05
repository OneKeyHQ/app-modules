package io.intercom.android.sdk.m5.inbox.ui;

import androidx.compose.foundation.lazy.LazyListScope;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.ScopeUpdateScope;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.paging.PagingData;
import androidx.paging.PagingData.Companion;
import androidx.paging.compose.LazyPagingItems;
import androidx.paging.compose.LazyPagingItemsKt;
import io.intercom.android.sdk.models.Avatar$Builder;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.LastParticipatingAdmin$Builder;
import io.intercom.android.sdk.models.Part$Builder;
import io.intercom.android.sdk.ui.IntercomPreviews;
import io.intercom.android.sdk.ui.theme.IntercomThemeKt;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u00000\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aS\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0014\b\u0002\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00072!\u0010\n\u001a\u001d\u0012\u0013\u0012\u00110\u0005¢\u0006\f\b\f\u0012\b\b\r\u0012\u0004\b\b(\u000e\u0012\u0004\u0012\u00020\u00010\u000bH\u0000\u001a\r\u0010\u000f\u001a\u00020\u0001H\u0003¢\u0006\u0002\u0010\u0010¨\u0006\u0011",
   d2 = {
         "inboxContentScreenItems",
         "",
         "Landroidx/compose/foundation/lazy/LazyListScope;",
         "inboxConversations",
         "Landroidx/paging/compose/LazyPagingItems;",
         "Lio/intercom/android/sdk/models/Conversation;",
         "typingConversations",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "onConversationClick",
         "Lkotlin/Function1;",
         "Lkotlin/ParameterName;",
         "name",
         "conversation",
         "InboxContentScreenPreview",
         "(Landroidx/compose/runtime/Composer;I)V",
         "intercom-sdk-base_release"
   }
)
public final class InboxContentScreenItemsKt {
   public static final void inboxContentScreenItems(@NotNull LazyListScope var0, @NotNull LazyPagingItems var1, @NotNull Map var2, @NotNull Function1 var3) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      Intrinsics.checkNotNullParameter(var1, "inboxConversations");
      Intrinsics.checkNotNullParameter(var2, "typingConversations");
      Intrinsics.checkNotNullParameter(var3, "onConversationClick");
      LazyListScope.items$default(
         var0,
         var1.getItemCount(),
         null,
         null,
         ComposableLambdaKt.composableLambdaInstance(328400605, true, new InboxContentScreenItemsKt$inboxContentScreenItems$1(var1, var3, var2)),
         6,
         null
      );
   }

   // $VF: synthetic method
   public static void inboxContentScreenItems$default(LazyListScope var0, LazyPagingItems var1, Map var2, Function1 var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = MapsKt.emptyMap();
      }

      inboxContentScreenItems(var0, var1, var2, var3);
   }

   @IntercomPreviews
   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxContentScreenPreview(Composer var0, int var1) {
      var0 = var0.startRestartGroup(1634106166);
      ComposerKt.sourceInformation(var0, "C(InboxContentScreenPreview)92@3920L34:InboxContentScreenItems.kt#po70xe");
      if (var1 == 0 && var0.getSkipping()) {
         var0.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(1634106166, var1, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContentScreenPreview (InboxContentScreenItems.kt:62)");
         }

         LastParticipatingAdmin$Builder var10000 = new LastParticipatingAdmin$Builder()
            .withFirstName("Santhosh Kumar")
            .withAvatar(new Avatar$Builder().withInitials("SK"));
         Intrinsics.checkNotNullExpressionValue(var10000, "withAvatar(...)");
         LastParticipatingAdmin$Builder var3 = var10000;
         List var4 = CollectionsKt.listOf(new Part$Builder().withSummary("This is the last message received/sent to/from this user").withCreatedAt(1659081886L));
         List var2 = CollectionsKt.listOf(
            new Conversation(
               "123",
               "Dual credit guidance",
               false,
               null,
               var4,
               null,
               var3,
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
            )
         );
         PagingData var6 = Companion.from$default(PagingData.Companion, var2, 0, 0, 6, null);
         MutableStateFlow var7 = StateFlowKt.MutableStateFlow(var6);
         InboxContentScreenPreview$DisplayPaging(var7, var0, 0);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }

      ScopeUpdateScope var8 = var0.endRestartGroup();
      if (var8 != null) {
         final int recoveredCapture_968_0 = var1;
         var8.updateScope(((kotlin.jvm.functions.Function2) (recoveredArgument_968_0, recoveredArgument_968_1) -> InboxContentScreenItemsKt.InboxContentScreenPreview$lambda$0(recoveredCapture_968_0, (androidx.compose.runtime.Composer)recoveredArgument_968_0, ((java.lang.Integer)recoveredArgument_968_1).intValue())));
      }
   }

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   private static final void InboxContentScreenPreview$DisplayPaging(Flow var0, Composer var1, int var2) {
      var1.startReplaceGroup(1509694910);
      ComposerKt.sourceInformation(var1, "C(DisplayPaging)65@2996L26,66@3045L213,66@3031L227:InboxContentScreenItems.kt#po70xe");
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventStart(
            1509694910, var2, -1, "io.intercom.android.sdk.m5.inbox.ui.InboxContentScreenPreview.DisplayPaging (InboxContentScreenItems.kt:64)"
         );
      }

      LazyPagingItems var3 = LazyPagingItemsKt.collectAsLazyPagingItems(var0, null, var1, 14 & var2, 1);
      IntercomThemeKt.IntercomTheme(
         null,
         null,
         null,
         ComposableLambdaKt.rememberComposableLambda(853574228, true, new InboxContentScreenItemsKt$InboxContentScreenPreview$DisplayPaging$1(var3), var1, 54),
         var1,
         3072,
         7
      );
      if (ComposerKt.isTraceInProgress()) {
         ComposerKt.traceEventEnd();
      }

      var1.endReplaceGroup();
   }

   private static final Unit InboxContentScreenPreview$lambda$0(int var0, Composer var1, int var2) {
      InboxContentScreenPreview(var1, RecomposeScopeImplKt.updateChangedFlags(var0 | 1));
      return Unit.INSTANCE;
   }
}
