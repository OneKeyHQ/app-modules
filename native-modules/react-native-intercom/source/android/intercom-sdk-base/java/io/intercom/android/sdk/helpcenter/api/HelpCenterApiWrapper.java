package io.intercom.android.sdk.helpcenter.api;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.search.HelpCenterArticleSearchResponse;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001e\u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\f2\u0006\u0010\u0011\u001a\u00020\u0012J\u001a\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00142\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0014J\f\u0010\u0018\u001a\u00020\f*\u00020\fH\u0002¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterApiWrapper;",
         "",
         "<init>",
         "()V",
         "fetchHelpCenterCollections",
         "",
         "metricTracker",
         "Lio/intercom/android/sdk/metrics/MetricTracker;",
         "collectionRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/CollectionRequestCallback;",
         "fetchHelpCenterResultsForSearchTerm",
         "searchTerm",
         "",
         "searchRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/SearchRequestCallback;",
         "fetchHelpCenterCollection",
         "collectionId",
         "collectionContentRequestCallback",
         "Lio/intercom/android/sdk/helpcenter/api/CollectionContentRequestCallback;",
         "transformSearchResponse",
         "",
         "Lio/intercom/android/sdk/helpcenter/api/HelpCenterArticleSearchResult;",
         "response",
         "Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse;",
         "removeHighlightTags",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nHelpCenterApiWrapper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HelpCenterApiWrapper.kt\nio/intercom/android/sdk/helpcenter/api/HelpCenterApiWrapper\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,146:1\n1563#2:147\n1634#2,3:148\n*S KotlinDebug\n*F\n+ 1 HelpCenterApiWrapper.kt\nio/intercom/android/sdk/helpcenter/api/HelpCenterApiWrapper\n*L\n127#1:147\n127#1:148,3\n*E\n"
)
public final class HelpCenterApiWrapper {
   @NotNull
   public static final HelpCenterApiWrapper INSTANCE = new HelpCenterApiWrapper();
   public static int $stable;

   static {}

   private HelpCenterApiWrapper() {
   }

   public final void fetchHelpCenterCollections(@NotNull MetricTracker var1, @NotNull CollectionRequestCallback var2) {
      Intrinsics.checkNotNullParameter(var1, "metricTracker");
      Intrinsics.checkNotNullParameter(var2, "collectionRequestCallback");
      BuildersKt.launch$default(
         GlobalScope.INSTANCE, Dispatchers.getIO(), null, new HelpCenterApiWrapper$fetchHelpCenterCollections$1(var1, var2, null), 2, null
      );
   }

   public final void fetchHelpCenterResultsForSearchTerm(@NotNull MetricTracker var1, @NotNull String var2, @NotNull SearchRequestCallback var3) {
      Intrinsics.checkNotNullParameter(var1, "metricTracker");
      Intrinsics.checkNotNullParameter(var2, "searchTerm");
      Intrinsics.checkNotNullParameter(var3, "searchRequestCallback");
      BuildersKt.launch$default(
         GlobalScope.INSTANCE, Dispatchers.getIO(), null, new HelpCenterApiWrapper$fetchHelpCenterResultsForSearchTerm$1(var1, var2, var3, null), 2, null
      );
   }

   public final void fetchHelpCenterCollection(@NotNull MetricTracker var1, @NotNull String var2, @NotNull CollectionContentRequestCallback var3) {
      Intrinsics.checkNotNullParameter(var1, "metricTracker");
      Intrinsics.checkNotNullParameter(var2, "collectionId");
      Intrinsics.checkNotNullParameter(var3, "collectionContentRequestCallback");
      BuildersKt.launch$default(
         GlobalScope.INSTANCE, Dispatchers.getIO(), null, new HelpCenterApiWrapper$fetchHelpCenterCollection$1(var1, var2, var3, null), 2, null
      );
   }

   @NotNull
   public final List transformSearchResponse(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "response");
      Iterable var2 = var1;
      int var3 = 0;
      Iterable var4 = var2;
      ArrayList var5 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var2, 10));
      int var6 = 0;

      for (Object var8 : var4) {
         HelpCenterArticleSearchResponse var9 = (HelpCenterArticleSearchResponse)var8;
         Collection var13 = var5;
         int var10 = 0;
         String var11 = var9.getHighlight().getTitle();
         String var12 = var11 == null || var11.length() == 0 ? var9.getTitle() : var9.getHighlight().getTitle();
         String var10000 = var9.getHighlight().getSummary();
         if (var10000 == null) {
            var10000 = "";
         }

         var11 = var10000;
         HelpCenterArticleSearchResult var15;
         String var10002 = var9.getArticleId();
         String var10003 = INSTANCE.removeHighlightTags(var12);
         String var10004 = var9.getSummary();
         if (var10004 == null) {
            var10004 = "";
         }

         var15 = new HelpCenterArticleSearchResult(var10002, var10003, var10004, INSTANCE.removeHighlightTags(var11));
         var13.add(var15);
      }

      return (List)var5;
   }

   private final String removeHighlightTags(String var1) {
      return StringsKt.replace$default(StringsKt.replace$default(var1, "<highlight>", "", false, 4, null), "</highlight>", "", false, 4, null);
   }
}
