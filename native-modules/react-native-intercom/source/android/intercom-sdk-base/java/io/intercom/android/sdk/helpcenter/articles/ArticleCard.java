package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0081\b\u0018\u0000 #2\u00020\u0001:\u0002\"#B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006B/\b\u0010\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0004\b\u0005\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\bH×\u0001J\t\u0010\u0019\u001a\u00020\u0003H×\u0001J%\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020 H\u0001¢\u0006\u0002\b!R\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\f\u0010\r\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0010\u0010\r\u001a\u0004\b\u0011\u0010\u000f¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleCard;",
         "",
         "articleId",
         "",
         "title",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getArticleId$annotations",
         "()V",
         "getArticleId",
         "()Ljava/lang/String;",
         "getTitle$annotations",
         "getTitle",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "write$Self",
         "",
         "self",
         "output",
         "Lkotlinx/serialization/encoding/CompositeEncoder;",
         "serialDesc",
         "Lkotlinx/serialization/descriptors/SerialDescriptor;",
         "write$Self$intercom_sdk_base_release",
         "$serializer",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class ArticleCard {
   @NotNull
   public static final ArticleCard$Companion Companion = new ArticleCard$Companion(null);
   @NotNull
   private final String articleId;
   @NotNull
   private final String title;
   public static int $stable;

   static {}

   public ArticleCard(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "title");
      this.articleId = var1;
      this.title = var2;
   }

   @NotNull
   public final String getArticleId() {
      return this.articleId;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("articleId")
   public static void getArticleId$annotations() {
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("title")
   public static void getTitle$annotations() {
   }

   @NotNull
   public final String component1() {
      return this.articleId;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final ArticleCard copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "title");
      return new ArticleCard(var1, var2);
   }

   // $VF: synthetic method
   public static ArticleCard copy$default(ArticleCard var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.articleId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.title;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleCard(articleId=" + this.articleId + ", title=" + this.title + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleId.hashCode();
      return var1 * 31 + this.title.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ArticleCard)) {
         return false;
      }

      ArticleCard var2 = (ArticleCard)var1;
      return !Intrinsics.areEqual(this.articleId, var2.articleId) ? false : Intrinsics.areEqual(this.title, var2.title);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(ArticleCard var0, CompositeEncoder var1, SerialDescriptor var2) {
      var1.encodeStringElement(var2, 0, var0.articleId);
      var1.encodeStringElement(var2, 1, var0.title);
   }

   // $VF: synthetic method
   public ArticleCard(int var1, String var2, String var3, SerializationConstructorMarker var4) {
      super();
      if (3 != (3 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 3, ArticleCard$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      this.articleId = var2;
      this.title = var3;
   }
}
