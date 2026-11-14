package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0011\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0003HÆ\u0003J?\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0019\u001a\u00020\u001aH×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/models/InlineSource;",
         "",
         "dataEntityId",
         "",
         "dataEntityType",
         "dataSourceIndex",
         "href",
         "title",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getDataEntityId",
         "()Ljava/lang/String;",
         "getDataEntityType",
         "getDataSourceIndex",
         "getHref",
         "getTitle",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class InlineSource {
   @NotNull
   private final String dataEntityId;
   @Nullable
   private final String dataEntityType;
   @NotNull
   private final String dataSourceIndex;
   @NotNull
   private final String href;
   @Nullable
   private final String title;
   public static int $stable;

   static {}

   public InlineSource(@NotNull String var1, @Nullable String var2, @NotNull String var3, @NotNull String var4, @Nullable String var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "dataEntityId");
      Intrinsics.checkNotNullParameter(var3, "dataSourceIndex");
      Intrinsics.checkNotNullParameter(var4, "href");
      this.dataEntityId = var1;
      this.dataEntityType = var2;
      this.dataSourceIndex = var3;
      this.href = var4;
      this.title = var5;
   }

   @NotNull
   public final String getDataEntityId() {
      return this.dataEntityId;
   }

   @Nullable
   public final String getDataEntityType() {
      return this.dataEntityType;
   }

   @NotNull
   public final String getDataSourceIndex() {
      return this.dataSourceIndex;
   }

   @NotNull
   public final String getHref() {
      return this.href;
   }

   @Nullable
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String component1() {
      return this.dataEntityId;
   }

   @Nullable
   public final String component2() {
      return this.dataEntityType;
   }

   @NotNull
   public final String component3() {
      return this.dataSourceIndex;
   }

   @NotNull
   public final String component4() {
      return this.href;
   }

   @Nullable
   public final String component5() {
      return this.title;
   }

   @NotNull
   public final InlineSource copy(@NotNull String var1, @Nullable String var2, @NotNull String var3, @NotNull String var4, @Nullable String var5) {
      Intrinsics.checkNotNullParameter(var1, "dataEntityId");
      Intrinsics.checkNotNullParameter(var3, "dataSourceIndex");
      Intrinsics.checkNotNullParameter(var4, "href");
      return new InlineSource(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static InlineSource copy$default(InlineSource var0, String var1, String var2, String var3, String var4, String var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.dataEntityId;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.dataEntityType;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.dataSourceIndex;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.href;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.title;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "InlineSource(dataEntityId="
         + this.dataEntityId
         + ", dataEntityType="
         + this.dataEntityType
         + ", dataSourceIndex="
         + this.dataSourceIndex
         + ", href="
         + this.href
         + ", title="
         + this.title
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.dataEntityId.hashCode();
      var1 = var1 * 31 + (this.dataEntityType == null ? 0 : this.dataEntityType.hashCode());
      var1 = var1 * 31 + this.dataSourceIndex.hashCode();
      var1 = var1 * 31 + this.href.hashCode();
      return var1 * 31 + (this.title == null ? 0 : this.title.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof InlineSource)) {
         return false;
      } else {
         InlineSource var2 = (InlineSource)var1;
         if (!Intrinsics.areEqual(this.dataEntityId, var2.dataEntityId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.dataEntityType, var2.dataEntityType)) {
            return false;
         } else if (!Intrinsics.areEqual(this.dataSourceIndex, var2.dataSourceIndex)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.href, var2.href) ? false : Intrinsics.areEqual(this.title, var2.title);
         }
      }
   }
}
