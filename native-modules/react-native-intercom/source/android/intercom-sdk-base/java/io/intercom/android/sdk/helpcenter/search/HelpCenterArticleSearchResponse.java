package io.intercom.android.sdk.helpcenter.search;

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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0081\b\u0018\u0000 22\u00020\u0001:\u0003012B7\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nBM\b\u0010\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e¢\u0006\u0004\b\t\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\t\u0010!\u001a\u00020\bHÆ\u0003J;\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010&\u001a\u00020\fH×\u0001J\t\u0010'\u001a\u00020\u0003H×\u0001J%\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\u00002\u0006\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.H\u0001¢\u0006\u0002\b/R\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0014\u0010\u0011\u001a\u0004\b\u0015\u0010\u0013R\u001c\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0016\u0010\u0011\u001a\u0004\b\u0017\u0010\u0013R\u001c\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0018\u0010\u0011\u001a\u0004\b\u0019\u0010\u0013R\u001c\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u001a\u0010\u0011\u001a\u0004\b\u001b\u0010\u001c¨\u00063",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse;",
         "",
         "articleId",
         "",
         "summary",
         "title",
         "url",
         "highlight",
         "Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse$Highlight;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse$Highlight;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse$Highlight;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getArticleId$annotations",
         "()V",
         "getArticleId",
         "()Ljava/lang/String;",
         "getSummary$annotations",
         "getSummary",
         "getTitle$annotations",
         "getTitle",
         "getUrl$annotations",
         "getUrl",
         "getHighlight$annotations",
         "getHighlight",
         "()Lio/intercom/android/sdk/helpcenter/search/HelpCenterArticleSearchResponse$Highlight;",
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
         "toString",
         "write$Self",
         "",
         "self",
         "output",
         "Lkotlinx/serialization/encoding/CompositeEncoder;",
         "serialDesc",
         "Lkotlinx/serialization/descriptors/SerialDescriptor;",
         "write$Self$intercom_sdk_base_release",
         "Highlight",
         "$serializer",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HelpCenterArticleSearchResponse {
   @NotNull
   public static final HelpCenterArticleSearchResponse$Companion Companion = new HelpCenterArticleSearchResponse$Companion(null);
   @NotNull
   private final String articleId;
   @NotNull
   private final String summary;
   @NotNull
   private final String title;
   @NotNull
   private final String url;
   @NotNull
   private final HelpCenterArticleSearchResponse$Highlight highlight;
   public static int $stable;

   static {}

   public HelpCenterArticleSearchResponse(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull HelpCenterArticleSearchResponse$Highlight var5
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "summary");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "url");
      Intrinsics.checkNotNullParameter(var5, "highlight");
      this.articleId = var1;
      this.summary = var2;
      this.title = var3;
      this.url = var4;
      this.highlight = var5;
   }

   // $VF: synthetic method
   public HelpCenterArticleSearchResponse(
      String var1, String var2, String var3, String var4, HelpCenterArticleSearchResponse$Highlight var5, int var6, DefaultConstructorMarker var7
   ) {
      this(var1, (((var6 & 2) != 0) ? (String) ("") : var2), (((var6 & 4) != 0) ? (String) ("") : var3), (((var6 & 8) != 0) ? (String) ("") : var4), (((var6 & 16) != 0) ? (HelpCenterArticleSearchResponse$Highlight) (new HelpCenterArticleSearchResponse$Highlight(null, null, 3, null)) : var5));
   }

   @NotNull
   public final String getArticleId() {
      return this.articleId;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("id")
   public static void getArticleId$annotations() {
   }

   @NotNull
   public final String getSummary() {
      return this.summary;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("summary")
   public static void getSummary$annotations() {
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
   public final String getUrl() {
      return this.url;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("url")
   public static void getUrl$annotations() {
   }

   @NotNull
   public final HelpCenterArticleSearchResponse$Highlight getHighlight() {
      return this.highlight;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("highlight")
   public static void getHighlight$annotations() {
   }

   @NotNull
   public final String component1() {
      return this.articleId;
   }

   @NotNull
   public final String component2() {
      return this.summary;
   }

   @NotNull
   public final String component3() {
      return this.title;
   }

   @NotNull
   public final String component4() {
      return this.url;
   }

   @NotNull
   public final HelpCenterArticleSearchResponse$Highlight component5() {
      return this.highlight;
   }

   @NotNull
   public final HelpCenterArticleSearchResponse copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @NotNull String var4, @NotNull HelpCenterArticleSearchResponse$Highlight var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "articleId");
      Intrinsics.checkNotNullParameter(var2, "summary");
      Intrinsics.checkNotNullParameter(var3, "title");
      Intrinsics.checkNotNullParameter(var4, "url");
      Intrinsics.checkNotNullParameter(var5, "highlight");
      return new HelpCenterArticleSearchResponse(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static HelpCenterArticleSearchResponse copy$default(
      HelpCenterArticleSearchResponse var0,
      String var1,
      String var2,
      String var3,
      String var4,
      HelpCenterArticleSearchResponse$Highlight var5,
      int var6,
      Object var7
   ) {
      if ((var6 & 1) != 0) {
         var1 = var0.articleId;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.summary;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.title;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.url;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.highlight;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterArticleSearchResponse(articleId="
         + this.articleId
         + ", summary="
         + this.summary
         + ", title="
         + this.title
         + ", url="
         + this.url
         + ", highlight="
         + this.highlight
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.articleId.hashCode();
      var1 = var1 * 31 + this.summary.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.url.hashCode();
      return var1 * 31 + this.highlight.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HelpCenterArticleSearchResponse)) {
         return false;
      } else {
         HelpCenterArticleSearchResponse var2 = (HelpCenterArticleSearchResponse)var1;
         if (!Intrinsics.areEqual(this.articleId, var2.articleId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.summary, var2.summary)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.url, var2.url) ? false : Intrinsics.areEqual(this.highlight, var2.highlight);
         }
      }
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(HelpCenterArticleSearchResponse var0, CompositeEncoder var1, SerialDescriptor var2) {
      var1.encodeStringElement(var2, 0, var0.articleId);
      if (var1.shouldEncodeElementDefault(var2, 1) ? true : !Intrinsics.areEqual(var0.summary, "")) {
         var1.encodeStringElement(var2, 1, var0.summary);
      }

      if (var1.shouldEncodeElementDefault(var2, 2) ? true : !Intrinsics.areEqual(var0.title, "")) {
         var1.encodeStringElement(var2, 2, var0.title);
      }

      if (var1.shouldEncodeElementDefault(var2, 3) ? true : !Intrinsics.areEqual(var0.url, "")) {
         var1.encodeStringElement(var2, 3, var0.url);
      }

      if (var1.shouldEncodeElementDefault(var2, 4)
         ? true
         : !Intrinsics.areEqual(var0.highlight, new HelpCenterArticleSearchResponse$Highlight(null, null, 3, null))) {
         var1.encodeSerializableElement(var2, 4, HelpCenterArticleSearchResponse$Highlight$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE, var0.highlight);
      }
   }

   // $VF: synthetic method
   public HelpCenterArticleSearchResponse(
      int var1, String var2, String var3, String var4, String var5, HelpCenterArticleSearchResponse$Highlight var6, SerializationConstructorMarker var7
   ) {
      super();
      if (1 != (1 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(
            var1, 1, HelpCenterArticleSearchResponse$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor()
         );
      }

      
      this.articleId = var2;
      if ((var1 & 2) == 0) {
         this.summary = "";
      } else {
         this.summary = var3;
      }

      if ((var1 & 4) == 0) {
         this.title = "";
      } else {
         this.title = var4;
      }

      if ((var1 & 8) == 0) {
         this.url = "";
      } else {
         this.url = var5;
      }

      if ((var1 & 16) == 0) {
         this.highlight = new HelpCenterArticleSearchResponse$Highlight(null, null, 3, null);
      } else {
         this.highlight = var6;
      }
   }
}
