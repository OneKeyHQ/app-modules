package io.intercom.android.sdk.survey.block;

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
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B)\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0012\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\u000f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0015\u0010\u0010\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0003HÆ\u0003J/\u0010\u0011\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0014\b\u0002\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0003HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\fH×\u0001J\t\u0010\u0016\u001a\u00020\u0017H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001d\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u000b\u001a\u00020\f8F¢\u0006\u0006\u001a\u0004\b\r\u0010\u000e¨\u0006\u0018",
   d2 = {
         "Lio/intercom/android/sdk/survey/block/TableModel;",
         "",
         "header",
         "",
         "Lio/intercom/android/sdk/survey/block/Cell;",
         "rows",
         "<init>",
         "(Ljava/util/List;Ljava/util/List;)V",
         "getHeader",
         "()Ljava/util/List;",
         "getRows",
         "columnCount",
         "",
         "getColumnCount",
         "()I",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TableModel {
   @NotNull
   private final List header;
   @NotNull
   private final List rows;
   public static final int $stable;

   static { $stable = 8; }

   public TableModel(@NotNull List var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "header");
      Intrinsics.checkNotNullParameter(var2, "rows");
      this.header = var1;
      this.rows = var2;
   }

   @NotNull
   public final List getHeader() {
      return this.header;
   }

   @NotNull
   public final List getRows() {
      return this.rows;
   }

   public final int getColumnCount() {
      return this.header.size();
   }

   @NotNull
   public final List component1() {
      return this.header;
   }

   @NotNull
   public final List component2() {
      return this.rows;
   }

   @NotNull
   public final TableModel copy(@NotNull List var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "header");
      Intrinsics.checkNotNullParameter(var2, "rows");
      return new TableModel(var1, var2);
   }

   // $VF: synthetic method
   public static TableModel copy$default(TableModel var0, List var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.header;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.rows;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "TableModel(header=" + this.header + ", rows=" + this.rows + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.header.hashCode();
      return var1 * 31 + this.rows.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TableModel)) {
         return false;
      }

      TableModel var2 = (TableModel)var1;
      return !Intrinsics.areEqual(this.header, var2.header) ? false : Intrinsics.areEqual(this.rows, var2.rows);
   }
}
