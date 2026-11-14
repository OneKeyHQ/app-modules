package io.intercom.android.sdk.helpcenter.articles;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0081\b\u0018\u0000 %2\u00020\u0001:\u0002$%B\u001b\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B/\b\u0010\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\u0004\b\u0006\u0010\fJ\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u001f\u0010\u0016\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001a\u001a\u00020\tH×\u0001J\t\u0010\u001b\u001a\u00020\u0003H×\u0001J%\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00002\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\"H\u0001¢\u0006\u0002\b#R\u001e\u0010\u0002\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\r\u0010\u000e\u001a\u0004\b\u000f\u0010\u0010R\u001c\u0010\u0004\u001a\u00020\u00058\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0011\u0010\u000e\u001a\u0004\b\u0012\u0010\u0013¨\u0006&",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/articles/Article;",
         "",
         "relatedConversationId",
         "",
         "card",
         "Lio/intercom/android/sdk/helpcenter/articles/ArticleCard;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/articles/ArticleCard;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Lio/intercom/android/sdk/helpcenter/articles/ArticleCard;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getRelatedConversationId$annotations",
         "()V",
         "getRelatedConversationId",
         "()Ljava/lang/String;",
         "getCard$annotations",
         "getCard",
         "()Lio/intercom/android/sdk/helpcenter/articles/ArticleCard;",
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
public final class Article {
   @NotNull
   public static final Article$Companion Companion = new Article$Companion(null);
   @Nullable
   private final String relatedConversationId;
   @NotNull
   private final ArticleCard card;
   public static int $stable;

   static {}

   public Article(@Nullable String var1, @NotNull ArticleCard var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "card");
      this.relatedConversationId = var1;
      this.card = var2;
   }

   // $VF: synthetic method
   public Article(String var1, ArticleCard var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) (null) : var1), var2);
   }

   @Nullable
   public final String getRelatedConversationId() {
      return this.relatedConversationId;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("related_conversation_id")
   public static void getRelatedConversationId$annotations() {
   }

   @NotNull
   public final ArticleCard getCard() {
      return this.card;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("card")
   public static void getCard$annotations() {
   }

   @Nullable
   public final String component1() {
      return this.relatedConversationId;
   }

   @NotNull
   public final ArticleCard component2() {
      return this.card;
   }

   @NotNull
   public final Article copy(@Nullable String var1, @NotNull ArticleCard var2) {
      Intrinsics.checkNotNullParameter(var2, "card");
      return new Article(var1, var2);
   }

   // $VF: synthetic method
   public static Article copy$default(Article var0, String var1, ArticleCard var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.relatedConversationId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.card;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Article(relatedConversationId=" + this.relatedConversationId + ", card=" + this.card + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.relatedConversationId == null ? 0 : this.relatedConversationId.hashCode();
      return var1 * 31 + this.card.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof Article)) {
         return false;
      }

      Article var2 = (Article)var1;
      return !Intrinsics.areEqual(this.relatedConversationId, var2.relatedConversationId) ? false : Intrinsics.areEqual(this.card, var2.card);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(Article var0, CompositeEncoder var1, SerialDescriptor var2) {
      if (var1.shouldEncodeElementDefault(var2, 0) ? true : var0.relatedConversationId != null) {
         var1.encodeNullableSerializableElement(var2, 0, StringSerializer.INSTANCE, var0.relatedConversationId);
      }

      var1.encodeSerializableElement(var2, 1, ArticleCard$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE, var0.card);
   }

   // $VF: synthetic method
   public Article(int var1, String var2, ArticleCard var3, SerializationConstructorMarker var4) {
      super();
      if (2 != (2 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 2, Article$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      if ((var1 & 1) == 0) {
         this.relatedConversationId = null;
      } else {
         this.relatedConversationId = var2;
      }

      this.card = var3;
   }
}
