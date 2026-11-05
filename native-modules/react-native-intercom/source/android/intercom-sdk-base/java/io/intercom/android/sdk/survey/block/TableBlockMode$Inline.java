package io.intercom.android.sdk.survey.block;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0011\u0010\t\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÆ\u0003J\u001b\u0010\n\u001a\u00020\u00002\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH×\u0003J\t\u0010\u000f\u001a\u00020\u0010H×\u0001J\t\u0010\u0011\u001a\u00020\u0012H×\u0001R\u0019\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/survey/block/TableBlockMode$Inline;",
         "Lio/intercom/android/sdk/survey/block/TableBlockMode;",
         "onExpand",
         "Lkotlin/Function0;",
         "",
         "<init>",
         "(Lkotlin/jvm/functions/Function0;)V",
         "getOnExpand",
         "()Lkotlin/jvm/functions/Function0;",
         "component1",
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
@StabilityInferred(parameters = 1)
public final class TableBlockMode$Inline implements TableBlockMode {
   @Nullable
   private final Function0 onExpand;
   public static int $stable;

   static {}

   public TableBlockMode$Inline(@Nullable Function0 var1) {
      this.onExpand = var1;
   }

   // $VF: synthetic method
   public TableBlockMode$Inline(Function0 var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Function0) (null) : var1));
   }

   @Nullable
   public final Function0 getOnExpand() {
      return this.onExpand;
   }

   @Nullable
   public final Function0 component1() {
      return this.onExpand;
   }

   @NotNull
   public final TableBlockMode$Inline copy(@Nullable Function0 var1) {
      return new TableBlockMode$Inline(var1);
   }

   // $VF: synthetic method
   public static TableBlockMode$Inline copy$default(TableBlockMode$Inline var0, Function0 var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.onExpand;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Inline(onExpand=" + this.onExpand + ')';
   }

   @Override
   public int hashCode() {
      return this.onExpand == null ? 0 : this.onExpand.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TableBlockMode$Inline)) {
         return false;
      }

      TableBlockMode$Inline var2 = (TableBlockMode$Inline)var1;
      return Intrinsics.areEqual(this.onExpand, var2.onExpand);
   }

   public TableBlockMode$Inline() {
      this(null, 1, null);
   }
}
