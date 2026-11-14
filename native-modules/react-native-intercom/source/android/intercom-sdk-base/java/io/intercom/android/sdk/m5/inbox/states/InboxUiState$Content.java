package io.intercom.android.sdk.m5.inbox.states;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.ui.common.IntercomTopBarState;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0007\u0018\u00002\u00020\u0001BQ\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r¢\u0006\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0015R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001d\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000f0\r¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState$Content;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "intercomTopBarState",
         "Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "inboxConversations",
         "Landroidx/paging/compose/LazyPagingItems;",
         "Lio/intercom/android/sdk/models/Conversation;",
         "showSendMessageFab",
         "",
         "isLoadingMore",
         "errorState",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "typingConversations",
         "",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/IntercomTopBarState;Landroidx/paging/compose/LazyPagingItems;ZZLio/intercom/android/sdk/m5/components/ErrorState;Ljava/util/Map;)V",
         "getInboxConversations",
         "()Landroidx/paging/compose/LazyPagingItems;",
         "getShowSendMessageFab",
         "()Z",
         "getErrorState",
         "()Lio/intercom/android/sdk/m5/components/ErrorState;",
         "getTypingConversations",
         "()Ljava/util/Map;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class InboxUiState$Content extends InboxUiState {
   @NotNull
   private final LazyPagingItems inboxConversations;
   private final boolean showSendMessageFab;
   private final boolean isLoadingMore;
   @Nullable
   private final ErrorState errorState;
   @NotNull
   private final Map typingConversations;
   public static final int $stable;

   static { $stable = 8; }

   public InboxUiState$Content(
      @NotNull IntercomTopBarState var1, @NotNull LazyPagingItems var2, boolean var3, boolean var4, @Nullable ErrorState var5, @NotNull Map var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "intercomTopBarState");
      Intrinsics.checkNotNullParameter(var2, "inboxConversations");
      Intrinsics.checkNotNullParameter(var6, "typingConversations");
      super(var1);
      this.inboxConversations = var2;
      this.showSendMessageFab = var3;
      this.isLoadingMore = var4;
      this.errorState = var5;
      this.typingConversations = var6;
   }

   // $VF: synthetic method
   public InboxUiState$Content(
      IntercomTopBarState var1, LazyPagingItems var2, boolean var3, boolean var4, ErrorState var5, Map var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, var3, (((var7 & 8) != 0) ? (boolean) (false) : var4), (((var7 & 16) != 0) ? (ErrorState) (null) : var5), (((var7 & 32) != 0) ? (Map) (MapsKt.emptyMap()) : var6));
   }

   @NotNull
   public final LazyPagingItems getInboxConversations() {
      return this.inboxConversations;
   }

   public final boolean getShowSendMessageFab() {
      return this.showSendMessageFab;
   }

   public final boolean isLoadingMore() {
      return this.isLoadingMore;
   }

   @Nullable
   public final ErrorState getErrorState() {
      return this.errorState;
   }

   @NotNull
   public final Map getTypingConversations() {
      return this.typingConversations;
   }
}
