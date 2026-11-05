package io.intercom.android.sdk.m5.inbox.data;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.paging.PagingSource;
import androidx.paging.PagingState;
import androidx.paging.PagingSource.LoadParams;
import androidx.paging.PagingSource.LoadResult;
import androidx.paging.PagingSource.LoadResult.Error;
import androidx.paging.PagingSource.LoadResult.Page;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.models.Config;
import io.intercom.android.sdk.models.Conversation;
import io.intercom.android.sdk.models.ConversationList;
import io.intercom.android.sdk.models.ConversationsResponse;
import io.intercom.android.sdk.models.ConversationsResponse$Builder;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.utilities.extensions.ConversationExtensionsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 \u001a2\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001:\u0001\u001aB;\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\"\u0010\b\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\u0006\u0012\u0004\u0018\u00010\r0\t¢\u0006\u0004\b\u000e\u0010\u000fJ(\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00020\u0014H\u0096@¢\u0006\u0002\u0010\u0015J#\u0010\u0016\u001a\u0004\u0018\u00010\u00022\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0018H\u0016¢\u0006\u0002\u0010\u0019R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R,\u0010\b\u001a\u001e\b\u0001\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b\u0012\u0006\u0012\u0004\u0018\u00010\r0\tX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0010¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/data/InboxPagingSource;",
         "Landroidx/paging/PagingSource;",
         "",
         "Lio/intercom/android/sdk/models/Conversation;",
         "inboxRepository",
         "Lio/intercom/android/sdk/m5/inbox/data/InboxRepository;",
         "intercomDataLayer",
         "Lio/intercom/android/sdk/m5/data/IntercomDataLayer;",
         "onEmptyState",
         "Lkotlin/Function2;",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "Lkotlin/coroutines/Continuation;",
         "",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/m5/inbox/data/InboxRepository;Lio/intercom/android/sdk/m5/data/IntercomDataLayer;Lkotlin/jvm/functions/Function2;)V",
         "Lkotlin/jvm/functions/Function2;",
         "load",
         "Landroidx/paging/PagingSource$LoadResult;",
         "params",
         "Landroidx/paging/PagingSource$LoadParams;",
         "(Landroidx/paging/PagingSource$LoadParams;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "getRefreshKey",
         "state",
         "Landroidx/paging/PagingState;",
         "(Landroidx/paging/PagingState;)Ljava/lang/Long;",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class InboxPagingSource extends PagingSource {
   @NotNull
   public static final InboxPagingSource$Companion Companion = new InboxPagingSource$Companion(null);
   @NotNull
   private final InboxRepository inboxRepository;
   @NotNull
   private final IntercomDataLayer intercomDataLayer;
   @NotNull
   private final Function2 onEmptyState;
   public static final int $stable;

   static { $stable = 8; }
   public static final int PAGE_SIZE = 20;

   public InboxPagingSource(@NotNull InboxRepository var1, @NotNull IntercomDataLayer var2, @NotNull Function2 var3) {
      Intrinsics.checkNotNullParameter(var1, "inboxRepository");
      Intrinsics.checkNotNullParameter(var2, "intercomDataLayer");
      Intrinsics.checkNotNullParameter(var3, "onEmptyState");
      super();
      this.inboxRepository = var1;
      this.intercomDataLayer = var2;
      this.onEmptyState = var3;
   }

   @Nullable
   @Override
   public Object load(@NotNull LoadParams var1, @NotNull Continuation var2) {
      InboxPagingSource recoveredThis = this;
      InboxPagingSource$load$1 var9;
      label65: {
         if (var2 instanceof InboxPagingSource$load$1) {
            var9 = (InboxPagingSource$load$1)var2;
            if ((var9.label & -2147483648) != 0) {
               var9.label -= Integer.MIN_VALUE;
               break label65;
            }
         }

         var9 = new InboxPagingSource$load$1(recoveredThis, var2);
      }

      int var4;
      ConversationList var6;
      label69: {
         Object var8 = var9.result;
         Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         Object var10000;
         switch (var9.label) {
            case 0:
               ResultKt.throwOnFailure(var8);
               InboxRepository var12 = recoveredThis.inboxRepository;
               Long var10001 = (Long)var1.getKey();
               var9.L$0 = recoveredThis;
               var9.L$1 = var1;
               var9.label = 1;
               var10000 = var12.getConversations(var10001, 20, var9);
               if (var10000 == var10) {
                  return var10;
               }
               break;
            case 1:
               var1 = (LoadParams)var9.L$1;
               recoveredThis = (InboxPagingSource)var9.L$0;
               ResultKt.throwOnFailure(var8);
               var10000 = var8;
               break;
            case 2:
               var4 = var9.I$0;
               var6 = (ConversationList)var9.L$1;
               recoveredThis = (InboxPagingSource)var9.L$0;
               ResultKt.throwOnFailure(var8);
               break label69;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         NetworkResponse var3 = (NetworkResponse)var10000;
         if (!(var3 instanceof NetworkResponse$Success)) {
            if (!(var3 instanceof NetworkResponse$NetworkError)) {
               LoadResult var17;
               if (var3 instanceof NetworkResponse$ClientError) {
                  var17 = new Error(((NetworkResponse$ClientError)var3).getError());
               } else {
                  if (!(var3 instanceof NetworkResponse$ServerError)) {
                     throw new NoWhenBranchMatchedException();
                  }

                  var17 = new Error(new java.lang.Error("Server error : code " + ((NetworkResponse$ServerError)var3).getCode()));
               }

               return var17;
            }

            return new Error(((NetworkResponse$NetworkError)var3).getError());
         }

         var4 = var1.getKey() == null ? 1 : 0;
         ConversationsResponse var5 = ((ConversationsResponse$Builder)((NetworkResponse$Success)var3).getBody()).build();
         var6 = var5.getConversationPage();
         IntercomDataLayer var13 = recoveredThis.intercomDataLayer;
         Config var7 = var5.getConfig();
         Intrinsics.checkNotNullExpressionValue(var7, "getConfig(...)");
         var13.updateConfig(var7);
         var13 = recoveredThis.intercomDataLayer;
         List var18 = var6.getConversations();
         Intrinsics.checkNotNullExpressionValue(var18, "getConversations(...)");
         var13.addConversations(var18);
         Function2 var15 = recoveredThis.onEmptyState;
         EmptyState var19 = var6.getEmptyState();
         Intrinsics.checkNotNullExpressionValue(var19, "getEmptyState(...)");
         var9.L$0 = recoveredThis;
         var9.L$1 = var6;
         var9.I$0 = var4;
         var9.label = 2;
         if (var15.invoke(var19, var9) == var10) {
            return var10;
         }
      }

      List var11 = var4 != 0 ? CollectionsKt.toList((Iterable)recoveredThis.intercomDataLayer.getConversations().getValue()) : var6.getConversations();
      Page var16;
      Intrinsics.checkNotNull(var11);
      Long var20;
      if (var6.hasMorePages()) {
         Conversation var10004 = (Conversation)CollectionsKt.lastOrNull(var11);
         var20 = var10004 != null ? Boxing.boxLong(ConversationExtensionsKt.lastActionCreatedAt(var10004)) : null;
      } else {
         var20 = null;
      }

      var16 = new Page(var11, null, var20);
      return var16;
   }

   @Nullable
   public Long getRefreshKey(@NotNull PagingState var1) {
      Intrinsics.checkNotNullParameter(var1, "state");
      return null;
   }

   // $VF: synthetic method
   // $VF: bridge method
   
}
