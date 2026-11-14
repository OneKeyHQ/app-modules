package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.m5.conversation.states.ContentRow;
import io.intercom.android.sdk.m5.conversation.states.ContentRow$ComposerSuggestionRow;
import io.intercom.android.sdk.m5.conversation.states.ReplySuggestion;
import io.intercom.android.sdk.models.ComposerSuggestions;
import io.intercom.android.sdk.models.ComposerSuggestions$Suggestion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0012\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\"\u001e\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005*\u00020\u00038BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\b¨\u0006\t",
   d2 = {
         "reduceComposerSuggestions",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "composerSuggestions",
         "Lio/intercom/android/sdk/models/ComposerSuggestions;",
         "replySuggestions",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "getReplySuggestions",
         "(Lio/intercom/android/sdk/models/ComposerSuggestions;)Ljava/util/List;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nComposerSuggestionsReducer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ComposerSuggestionsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ComposerSuggestionsReducerKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,24:1\n1563#2:25\n1634#2,3:26\n*S KotlinDebug\n*F\n+ 1 ComposerSuggestionsReducer.kt\nio/intercom/android/sdk/m5/conversation/reducers/ComposerSuggestionsReducerKt\n*L\n23#1:25\n23#1:26,3\n*E\n"
)
public final class ComposerSuggestionsReducerKt {
   @Nullable
   public static final ContentRow reduceComposerSuggestions(@NotNull ComposerSuggestions var0) {
      Intrinsics.checkNotNullParameter(var0, "composerSuggestions");
      return !var0.getSuggestions().isEmpty() ? new ContentRow$ComposerSuggestionRow(getReplySuggestions(var0)) : null;
   }

   private static final List getReplySuggestions(ComposerSuggestions var0) {
      Iterable var1 = var0.getSuggestions();
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         ComposerSuggestions$Suggestion var8 = (ComposerSuggestions$Suggestion)var7;
         Collection var10 = var4;
         int var9 = 0;
         var10.add(new ReplySuggestion(var8.getUuid(), var8.getText()));
      }

      return (List)var4;
   }
}
