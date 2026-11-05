package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\nH×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\n8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$ComposerSuggestionRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "suggestions",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ReplySuggestion;",
         "<init>",
         "(Ljava/util/List;)V",
         "getSuggestions",
         "()Ljava/util/List;",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ContentRow$ComposerSuggestionRow implements ContentRow {
   @NotNull
   private final List suggestions;
   public static final int $stable;

   static { $stable = 8; }

   public ContentRow$ComposerSuggestionRow(@NotNull List var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "suggestions");
      this.suggestions = var1;
   }

   @NotNull
   public final List getSuggestions() {
      return this.suggestions;
   }

   @NotNull
   @Override
   public String getKey() {
      return "composer_suggestion_row_" + this.suggestions.hashCode();
   }

   @NotNull
   public final List component1() {
      return this.suggestions;
   }

   @NotNull
   public final ContentRow$ComposerSuggestionRow copy(@NotNull List var1) {
      Intrinsics.checkNotNullParameter(var1, "suggestions");
      return new ContentRow$ComposerSuggestionRow(var1);
   }

   // $VF: synthetic method
   public static ContentRow$ComposerSuggestionRow copy$default(ContentRow$ComposerSuggestionRow var0, List var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.suggestions;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ComposerSuggestionRow(suggestions=" + this.suggestions + ')';
   }

   @Override
   public int hashCode() {
      return this.suggestions.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ContentRow$ComposerSuggestionRow)) {
         return false;
      }

      ContentRow$ComposerSuggestionRow var2 = (ContentRow$ComposerSuggestionRow)var1;
      return Intrinsics.areEqual(this.suggestions, var2.suggestions);
   }
}
