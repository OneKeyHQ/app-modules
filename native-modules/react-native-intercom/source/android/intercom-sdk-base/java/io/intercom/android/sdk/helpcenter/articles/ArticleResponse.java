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
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0081\b\u0018\u0000  2\u00020\u0001:\u0002\u001f B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B%\b\u0010\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u0004\u0010\nJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0014\u001a\u00020\u0007H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J%\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u00002\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0001¢\u0006\u0002\b\u001eR\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000e¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleResponse;",
         "",
         "article",
         "Lio/intercom/android/sdk/helpcenter/articles/Article;",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/articles/Article;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILio/intercom/android/sdk/helpcenter/articles/Article;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getArticle$annotations",
         "()V",
         "getArticle",
         "()Lio/intercom/android/sdk/helpcenter/articles/Article;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "",
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
public final class ArticleResponse {
   @NotNull
   public static final ArticleResponse$Companion Companion = new ArticleResponse$Companion(null);
   @NotNull
   private final Article article;
   public static int $stable;

   static {}

   public ArticleResponse(@NotNull Article var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "article");
      this.article = var1;
   }

   @NotNull
   public final Article getArticle() {
      return this.article;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("article")
   public static void getArticle$annotations() {
   }

   @NotNull
   public final Article component1() {
      return this.article;
   }

   @NotNull
   public final ArticleResponse copy(@NotNull Article var1) {
      Intrinsics.checkNotNullParameter(var1, "article");
      return new ArticleResponse(var1);
   }

   // $VF: synthetic method
   public static ArticleResponse copy$default(ArticleResponse var0, Article var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.article;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "ArticleResponse(article=" + this.article + ')';
   }

   @Override
   public int hashCode() {
      return this.article.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ArticleResponse)) {
         return false;
      }

      ArticleResponse var2 = (ArticleResponse)var1;
      return Intrinsics.areEqual(this.article, var2.article);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(ArticleResponse var0, CompositeEncoder var1, SerialDescriptor var2) {
      var1.encodeSerializableElement(var2, 0, Article$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE, var0.article);
   }

   // $VF: synthetic method
   public ArticleResponse(int var1, Article var2, SerializationConstructorMarker var3) {
      super();
      if (1 != (1 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 1, ArticleResponse$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      this.article = var2;
   }
}
