package io.intercom.android.sdk.helpcenter.sections;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.collections.HelpCenterCollection$RecoveredInnere3b0c44298fc1c14$serializer;
import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerialName;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Serializable
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u001f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 B2\u00020\u0001:\u0002ABBk\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0007\u0012\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0007\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u0007¢\u0006\u0004\b\u0011\u0010\u0012B\u0081\u0001\b\u0010\u0012\u0006\u0010\u0013\u001a\u00020\u000e\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u000e\u0010\u0006\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0007\u0012\u000e\u0010\t\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u0007\u0012\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u0007\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u000e\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0010\u0018\u00010\u0007\u0012\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015¢\u0006\u0004\b\u0011\u0010\u0016J\t\u0010+\u001a\u00020\u0003HÆ\u0003J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\t\u0010-\u001a\u00020\u0003HÆ\u0003J\u000f\u0010.\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J\u000f\u0010/\u001a\b\u0012\u0004\u0012\u00020\n0\u0007HÆ\u0003J\u000f\u00100\u001a\b\u0012\u0004\u0012\u00020\f0\u0007HÆ\u0003J\t\u00101\u001a\u00020\u000eHÆ\u0003J\u000f\u00102\u001a\b\u0012\u0004\u0012\u00020\u00100\u0007HÆ\u0003Jq\u00103\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00072\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00072\u000e\b\u0002\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00072\b\b\u0002\u0010\r\u001a\u00020\u000e2\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u0007HÇ\u0001J\u0013\u00104\u001a\u0002052\b\u00106\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00107\u001a\u00020\u000eH×\u0001J\t\u00108\u001a\u00020\u0003H×\u0001J%\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020\u00002\u0006\u0010<\u001a\u00020=2\u0006\u0010>\u001a\u00020?H\u0001¢\u0006\u0002\b@R\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0017\u0010\u0018\u001a\u0004\b\u0019\u0010\u001aR\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u001b\u0010\u0018\u001a\u0004\b\u001c\u0010\u001aR\u001c\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u001d\u0010\u0018\u001a\u0004\b\u001e\u0010\u001aR\"\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u001f\u0010\u0018\u001a\u0004\b \u0010!R\"\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\"\u0010\u0018\u001a\u0004\b#\u0010!R\"\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b$\u0010\u0018\u001a\u0004\b%\u0010!R\u001c\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b&\u0010\u0018\u001a\u0004\b'\u0010(R\"\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b)\u0010\u0018\u001a\u0004\b*\u0010!¨\u0006C",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent;",
         "",
         "collectionId",
         "",
         "title",
         "summary",
         "helpCenterArticles",
         "",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterArticle;",
         "helpCenterSections",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterSection;",
         "subCollections",
         "Lio/intercom/android/sdk/helpcenter/collections/HelpCenterCollection;",
         "articlesCount",
         "",
         "authors",
         "Lio/intercom/android/sdk/helpcenter/sections/Author;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;ILjava/util/List;)V",
         "seen0",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/util/List;ILjava/util/List;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getCollectionId$annotations",
         "()V",
         "getCollectionId",
         "()Ljava/lang/String;",
         "getTitle$annotations",
         "getTitle",
         "getSummary$annotations",
         "getSummary",
         "getHelpCenterArticles$annotations",
         "getHelpCenterArticles",
         "()Ljava/util/List;",
         "getHelpCenterSections$annotations",
         "getHelpCenterSections",
         "getSubCollections$annotations",
         "getSubCollections",
         "getArticlesCount$annotations",
         "getArticlesCount",
         "()I",
         "getAuthors$annotations",
         "getAuthors",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
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
@StabilityInferred(parameters = 0)
public final class HelpCenterCollectionContent {
   @NotNull
   public static final HelpCenterCollectionContent$Companion Companion = new HelpCenterCollectionContent$Companion(null);
   @NotNull
   private final String collectionId;
   @NotNull
   private final String title;
   @NotNull
   private final String summary;
   @NotNull
   private final List helpCenterArticles;
   /** @deprecated */
   @NotNull
   private final List helpCenterSections;
   @NotNull
   private final List subCollections;
   private final int articlesCount;
   @NotNull
   private final List authors;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   private static final KSerializer[] $childSerializers;

   public HelpCenterCollectionContent(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull List var4,
      @NotNull List var5,
      @NotNull List var6,
      int var7,
      @NotNull List var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var4, "helpCenterArticles");
      Intrinsics.checkNotNullParameter(var5, "helpCenterSections");
      Intrinsics.checkNotNullParameter(var6, "subCollections");
      Intrinsics.checkNotNullParameter(var8, "authors");
      this.collectionId = var1;
      this.title = var2;
      this.summary = var3;
      this.helpCenterArticles = var4;
      this.helpCenterSections = var5;
      this.subCollections = var6;
      this.articlesCount = var7;
      this.authors = var8;
   }

   // $VF: synthetic method
   public HelpCenterCollectionContent(
      String var1, String var2, String var3, List var4, List var5, List var6, int var7, List var8, int var9, DefaultConstructorMarker var10
   ) {
      this(var1, (((var9 & 2) != 0) ? (String) ("") : var2), (((var9 & 4) != 0) ? (String) ("") : var3), (((var9 & 8) != 0) ? (List) (CollectionsKt.emptyList()) : var4), (((var9 & 16) != 0) ? (List) (CollectionsKt.emptyList()) : var5), (((var9 & 32) != 0) ? (List) (CollectionsKt.emptyList()) : var6), var7, (((var9 & 128) != 0) ? (List) (CollectionsKt.emptyList()) : var8));
   }

   @NotNull
   public final String getCollectionId() {
      return this.collectionId;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("id")
   public static void getCollectionId$annotations() {
   }

   @NotNull
   public final String getTitle() {
      return this.title;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("name")
   public static void getTitle$annotations() {
   }

   @NotNull
   public final String getSummary() {
      return this.summary;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("description")
   public static void getSummary$annotations() {
   }

   @NotNull
   public final List getHelpCenterArticles() {
      return this.helpCenterArticles;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("articles")
   public static void getHelpCenterArticles$annotations() {
   }

   /** @deprecated */
   @NotNull
   public final List getHelpCenterSections() {
      return this.helpCenterSections;
   }

   /** @deprecated */
   // $VF: synthetic method
   @Deprecated(
      message = "sections is deprecated and will be removed in a future release. Use collections instead",
      replaceWith = @ReplaceWith(expression = "collections", imports = {}),
      level = DeprecationLevel.WARNING
   )
   @SerialName("sections")
   public static void getHelpCenterSections$annotations() {
   }

   @NotNull
   public final List getSubCollections() {
      return this.subCollections;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("collections")
   public static void getSubCollections$annotations() {
   }

   public final int getArticlesCount() {
      return this.articlesCount;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("article_count")
   public static void getArticlesCount$annotations() {
   }

   @NotNull
   public final List getAuthors() {
      return this.authors;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("authors")
   public static void getAuthors$annotations() {
   }

   @NotNull
   public final String component1() {
      return this.collectionId;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final String component3() {
      return this.summary;
   }

   @NotNull
   public final List component4() {
      return this.helpCenterArticles;
   }

   @NotNull
   public final List component5() {
      return this.helpCenterSections;
   }

   @NotNull
   public final List component6() {
      return this.subCollections;
   }

   public final int component7() {
      return this.articlesCount;
   }

   @NotNull
   public final List component8() {
      return this.authors;
   }

   @NotNull
   public final HelpCenterCollectionContent copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull List var4,
      @NotNull List var5,
      @NotNull List var6,
      int var7,
      @NotNull List var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "collectionId");
      Intrinsics.checkNotNullParameter(var2, "title");
      Intrinsics.checkNotNullParameter(var3, "summary");
      Intrinsics.checkNotNullParameter(var4, "helpCenterArticles");
      Intrinsics.checkNotNullParameter(var5, "helpCenterSections");
      Intrinsics.checkNotNullParameter(var6, "subCollections");
      Intrinsics.checkNotNullParameter(var8, "authors");
      return new HelpCenterCollectionContent(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static HelpCenterCollectionContent copy$default(
      HelpCenterCollectionContent var0, String var1, String var2, String var3, List var4, List var5, List var6, int var7, List var8, int var9, Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.collectionId;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.title;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.summary;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.helpCenterArticles;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.helpCenterSections;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.subCollections;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.articlesCount;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.authors;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterCollectionContent(collectionId="
         + this.collectionId
         + ", title="
         + this.title
         + ", summary="
         + this.summary
         + ", helpCenterArticles="
         + this.helpCenterArticles
         + ", helpCenterSections="
         + this.helpCenterSections
         + ", subCollections="
         + this.subCollections
         + ", articlesCount="
         + this.articlesCount
         + ", authors="
         + this.authors
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.collectionId.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + this.summary.hashCode();
      var1 = var1 * 31 + this.helpCenterArticles.hashCode();
      var1 = var1 * 31 + this.helpCenterSections.hashCode();
      var1 = var1 * 31 + this.subCollections.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.articlesCount);
      return var1 * 31 + this.authors.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HelpCenterCollectionContent)) {
         return false;
      } else {
         HelpCenterCollectionContent var2 = (HelpCenterCollectionContent)var1;
         if (!Intrinsics.areEqual(this.collectionId, var2.collectionId)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else if (!Intrinsics.areEqual(this.summary, var2.summary)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterArticles, var2.helpCenterArticles)) {
            return false;
         } else if (!Intrinsics.areEqual(this.helpCenterSections, var2.helpCenterSections)) {
            return false;
         } else if (!Intrinsics.areEqual(this.subCollections, var2.subCollections)) {
            return false;
         } else {
            return this.articlesCount != var2.articlesCount ? false : Intrinsics.areEqual(this.authors, var2.authors);
         }
      }
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(HelpCenterCollectionContent var0, CompositeEncoder var1, SerialDescriptor var2) {
      KSerializer[] var3 = $childSerializers;
      var1.encodeStringElement(var2, 0, var0.collectionId);
      if (var1.shouldEncodeElementDefault(var2, 1) ? true : !Intrinsics.areEqual(var0.title, "")) {
         var1.encodeStringElement(var2, 1, var0.title);
      }

      if (var1.shouldEncodeElementDefault(var2, 2) ? true : !Intrinsics.areEqual(var0.summary, "")) {
         var1.encodeStringElement(var2, 2, var0.summary);
      }

      if (var1.shouldEncodeElementDefault(var2, 3) ? true : !Intrinsics.areEqual(var0.helpCenterArticles, CollectionsKt.emptyList())) {
         var1.encodeSerializableElement(var2, 3, var3[3], var0.helpCenterArticles);
      }

      if (var1.shouldEncodeElementDefault(var2, 4) ? true : !Intrinsics.areEqual(var0.helpCenterSections, CollectionsKt.emptyList())) {
         var1.encodeSerializableElement(var2, 4, var3[4], var0.helpCenterSections);
      }

      if (var1.shouldEncodeElementDefault(var2, 5) ? true : !Intrinsics.areEqual(var0.subCollections, CollectionsKt.emptyList())) {
         var1.encodeSerializableElement(var2, 5, var3[5], var0.subCollections);
      }

      var1.encodeIntElement(var2, 6, var0.articlesCount);
      if (var1.shouldEncodeElementDefault(var2, 7) ? true : !Intrinsics.areEqual(var0.authors, CollectionsKt.emptyList())) {
         var1.encodeSerializableElement(var2, 7, var3[7], var0.authors);
      }
   }

   // $VF: synthetic method
   public HelpCenterCollectionContent(
      int var1, String var2, String var3, String var4, List var5, List var6, List var7, int var8, List var9, SerializationConstructorMarker var10
   ) {
      super();
      if (65 != (65 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 65, HelpCenterCollectionContent$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      this.collectionId = var2;
      if ((var1 & 2) == 0) {
         this.title = "";
      } else {
         this.title = var3;
      }

      if ((var1 & 4) == 0) {
         this.summary = "";
      } else {
         this.summary = var4;
      }

      if ((var1 & 8) == 0) {
         this.helpCenterArticles = CollectionsKt.emptyList();
      } else {
         this.helpCenterArticles = var5;
      }

      if ((var1 & 16) == 0) {
         this.helpCenterSections = CollectionsKt.emptyList();
      } else {
         this.helpCenterSections = var6;
      }

      if ((var1 & 32) == 0) {
         this.subCollections = CollectionsKt.emptyList();
      } else {
         this.subCollections = var7;
      }

      this.articlesCount = var8;
      if ((var1 & 128) == 0) {
         this.authors = CollectionsKt.emptyList();
      } else {
         this.authors = var9;
      }
   }

   // $VF: synthetic method
   public static final KSerializer[] access$get$childSerializers$cp() {
      return $childSerializers;
   }

   static {
      KSerializer[] var0 = new KSerializer[]{
         null,
         null,
         null,
         new ArrayListSerializer(HelpCenterArticle$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE),
         new ArrayListSerializer(HelpCenterSection$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE),
         new ArrayListSerializer(HelpCenterCollection$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE),
         null,
         new ArrayListSerializer(Author$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE)
      };
      $childSerializers = var0;
   }
}
