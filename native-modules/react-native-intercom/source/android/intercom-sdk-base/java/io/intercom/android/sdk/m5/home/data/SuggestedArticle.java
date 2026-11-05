package io.intercom.android.sdk.m5.home.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J'\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/SuggestedArticle;",
         "",
         "id",
         "",
         "title",
         "url",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getId",
         "()Ljava/lang/String;",
         "getTitle",
         "getUrl",
         "component1",
         "component2",
         "component3",
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
public final class SuggestedArticle {
   @SerializedName("id")
   @NotNull
   private final String id;
   @SerializedName("title")
   @NotNull
   private final String title;
   @SerializedName("url")
   @NotNull
   private final String url;
   public static int $stable;

   static {}

   public SuggestedArticle(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "url");
      this.id = var1;
      this.title = var2;
      this.url = var3;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   @NotNull
   public final String getUrl() {
      return this.url;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final String component3() {
      return this.url;
   }

   @NotNull
   public final SuggestedArticle copy(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "url");
      return new SuggestedArticle(var1, var2, var3);
   }

   // $VF: synthetic method
   public static SuggestedArticle copy$default(SuggestedArticle var0, String var1, String var2, String var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.url;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "SuggestedArticle(id=" + this.id + ", title=" + this.title + ", url=" + this.url + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      return var1 * 31 + this.url.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof SuggestedArticle)) {
         return false;
      } else {
         SuggestedArticle var2 = (SuggestedArticle)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.title, var2.title) ? false : Intrinsics.areEqual(this.url, var2.url);
         }
      }
   }
}
