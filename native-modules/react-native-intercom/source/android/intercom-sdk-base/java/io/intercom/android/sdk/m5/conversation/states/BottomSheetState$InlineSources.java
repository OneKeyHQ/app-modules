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
   d1 = "\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B#\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003HÆ\u0003J)\u0010\u000e\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\n¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState$InlineSources;",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "selectedSources",
         "",
         "Lio/intercom/android/sdk/models/InlineSource;",
         "sources",
         "Lio/intercom/android/sdk/models/Source;",
         "<init>",
         "(Ljava/util/List;Ljava/util/List;)V",
         "getSelectedSources",
         "()Ljava/util/List;",
         "getSources",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class BottomSheetState$InlineSources implements BottomSheetState {
   @NotNull
   private final List selectedSources;
   @NotNull
   private final List sources;
   public static final int $stable;

   static { $stable = 8; }

   public BottomSheetState$InlineSources(@NotNull List var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "selectedSources");
      Intrinsics.checkNotNullParameter(var2, "sources");
      this.selectedSources = var1;
      this.sources = var2;
   }

   @NotNull
   public final List getSelectedSources() {
      return this.selectedSources;
   }

   @NotNull
   public final List getSources() {
      return this.sources;
   }

   @NotNull
   public final List component1() {
      return this.selectedSources;
   }

   @NotNull
   public final List component2() {
      return this.sources;
   }

   @NotNull
   public final BottomSheetState$InlineSources copy(@NotNull List var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "selectedSources");
      Intrinsics.checkNotNullParameter(var2, "sources");
      return new BottomSheetState$InlineSources(var1, var2);
   }

   // $VF: synthetic method
   public static BottomSheetState$InlineSources copy$default(BottomSheetState$InlineSources var0, List var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.selectedSources;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.sources;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "InlineSources(selectedSources=" + this.selectedSources + ", sources=" + this.sources + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.selectedSources.hashCode();
      return var1 * 31 + this.sources.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof BottomSheetState$InlineSources)) {
         return false;
      }

      BottomSheetState$InlineSources var2 = (BottomSheetState$InlineSources)var1;
      return !Intrinsics.areEqual(this.selectedSources, var2.selectedSources) ? false : Intrinsics.areEqual(this.sources, var2.sources);
   }
}
