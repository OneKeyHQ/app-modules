package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001:\u0001\u001cB-\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\t\u0010\nJ\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0006HÆ\u0003J\u0010\u0010\u0013\u001a\u0004\u0018\u00010\bHÆ\u0003¢\u0006\u0002\u0010\u000fJ4\u0010\u0014\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bHÇ\u0001¢\u0006\u0002\u0010\u0015J\u0013\u0010\u0016\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u001bH×\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\rR\u001a\u0010\u0007\u001a\u0004\u0018\u00010\b8\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/models/ComposerSuggestions;",
         "",
         "suggestions",
         "",
         "Lio/intercom/android/sdk/models/ComposerSuggestions$Suggestion;",
         "isComposerDisabled",
         "",
         "snapshotId",
         "",
         "<init>",
         "(Ljava/util/List;ZLjava/lang/Long;)V",
         "getSuggestions",
         "()Ljava/util/List;",
         "()Z",
         "getSnapshotId",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "component1",
         "component2",
         "component3",
         "copy",
         "(Ljava/util/List;ZLjava/lang/Long;)Lio/intercom/android/sdk/models/ComposerSuggestions;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "Suggestion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ComposerSuggestions {
   @SerializedName("suggestions")
   @NotNull
   private final List suggestions;
   @SerializedName("composer_disabled")
   private final boolean isComposerDisabled;
   @SerializedName("snapshot_id")
   @Nullable
   private final Long snapshotId;
   public static final int $stable;

   static { $stable = 8; }

   public ComposerSuggestions(@NotNull List var1, boolean var2, @Nullable Long var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "suggestions");
      this.suggestions = var1;
      this.isComposerDisabled = var2;
      this.snapshotId = var3;
   }

   // $VF: synthetic method
   public ComposerSuggestions(List var1, boolean var2, Long var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), (((var4 & 2) != 0) ? (boolean) (false) : var2), (((var4 & 4) != 0) ? (Long) (null) : var3));
   }

   @NotNull
   public final List getSuggestions() {
      return this.suggestions;
   }

   public final boolean isComposerDisabled() {
      return this.isComposerDisabled;
   }

   @Nullable
   public final Long getSnapshotId() {
      return this.snapshotId;
   }

   @NotNull
   public final List component1() {
      return this.suggestions;
   }

   public final boolean component2() {
      return this.isComposerDisabled;
   }

   @Nullable
   public final Long component3() {
      return this.snapshotId;
   }

   @NotNull
   public final ComposerSuggestions copy(@NotNull List var1, boolean var2, @Nullable Long var3) {
      Intrinsics.checkNotNullParameter(var1, "suggestions");
      return new ComposerSuggestions(var1, var2, var3);
   }

   // $VF: synthetic method
   public static ComposerSuggestions copy$default(ComposerSuggestions var0, List var1, boolean var2, Long var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.suggestions;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.isComposerDisabled;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.snapshotId;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "ComposerSuggestions(suggestions="
         + this.suggestions
         + ", isComposerDisabled="
         + this.isComposerDisabled
         + ", snapshotId="
         + this.snapshotId
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.suggestions.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isComposerDisabled);
      return var1 * 31 + (this.snapshotId == null ? 0 : this.snapshotId.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ComposerSuggestions)) {
         return false;
      } else {
         ComposerSuggestions var2 = (ComposerSuggestions)var1;
         if (!Intrinsics.areEqual(this.suggestions, var2.suggestions)) {
            return false;
         } else {
            return this.isComposerDisabled != var2.isComposerDisabled ? false : Intrinsics.areEqual(this.snapshotId, var2.snapshotId);
         }
      }
   }

   public ComposerSuggestions() {
      this(null, false, null, 7, null);
   }
}
