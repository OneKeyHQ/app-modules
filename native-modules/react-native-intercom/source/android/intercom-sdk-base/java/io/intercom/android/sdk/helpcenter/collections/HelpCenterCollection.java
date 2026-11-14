package io.intercom.android.sdk.helpcenter.collections;

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
   d1 = "\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 02\u00020\u0001:\u0002/0B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0007¢\u0006\u0004\b\t\u0010\nBI\b\u0010\u0012\u0006\u0010\u000b\u001a\u00020\u0007\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\t\u0010\u000eJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0007HÆ\u0003J\t\u0010 \u001a\u00020\u0007HÆ\u0003J;\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010%\u001a\u00020\u0007H×\u0001J\t\u0010&\u001a\u00020\u0003H×\u0001J%\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u00002\u0006\u0010*\u001a\u00020+2\u0006\u0010,\u001a\u00020-H\u0001¢\u0006\u0002\b.R\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u000f\u0010\u0010\u001a\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0013\u0010\u0010\u001a\u0004\b\u0014\u0010\u0012R\u001c\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0015\u0010\u0010\u001a\u0004\b\u0016\u0010\u0012R\u001c\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0017\u0010\u0010\u001a\u0004\b\u0018\u0010\u0019R\u001c\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u001a\u0010\u0010\u001a\u0004\b\u001b\u0010\u0019¨\u00061",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/collections/HelpCenterCollection;",
         "",
         "summary",
         "",
         "id",
         "title",
         "articlesCount",
         "",
         "collectionsCount",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;II)V",
         "seen0",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;IILkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getSummary$annotations",
         "()V",
         "getSummary",
         "()Ljava/lang/String;",
         "getId$annotations",
         "getId",
         "getTitle$annotations",
         "getTitle",
         "getArticlesCount$annotations",
         "getArticlesCount",
         "()I",
         "getCollectionsCount$annotations",
         "getCollectionsCount",
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
         "$serializer",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HelpCenterCollection {
   @NotNull
   public static final HelpCenterCollection$Companion Companion = new HelpCenterCollection$Companion(null);
   @NotNull
   private final String summary;
   @NotNull
   private final String id;
   @NotNull
   private final String title;
   private final int articlesCount;
   private final int collectionsCount;
   public static int $stable;

   static {}

   public HelpCenterCollection(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, int var5) {
      super();
      Intrinsics.checkNotNullParameter(var1, "summary");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "title");
      this.summary = var1;
      this.id = var2;
      this.title = var3;
      this.articlesCount = var4;
      this.collectionsCount = var5;
   }

   // $VF: synthetic method
   public HelpCenterCollection(String var1, String var2, String var3, int var4, int var5, int var6, DefaultConstructorMarker var7) {
      this((((var6 & 1) != 0) ? (String) ("") : var1), var2, (((var6 & 4) != 0) ? (String) ("") : var3), (((var6 & 8) != 0) ? (int) (0) : var4), (((var6 & 16) != 0) ? (int) (0) : var5));
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
   public final String getId() {
      return this.id;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("id")
   public static void getId$annotations() {
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

   public final int getArticlesCount() {
      return this.articlesCount;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("article_count")
   public static void getArticlesCount$annotations() {
   }

   public final int getCollectionsCount() {
      return this.collectionsCount;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("collection_count")
   public static void getCollectionsCount$annotations() {
   }

   @NotNull
   public final String component1() {
      return this.summary;
   }

   @NotNull
   public final String component2() {
      return this.id;
   }

   @NotNull
   public final String component3() {
      return this.title;
   }

   public final int component4() {
      return this.articlesCount;
   }

   public final int component5() {
      return this.collectionsCount;
   }

   @NotNull
   public final HelpCenterCollection copy(@NotNull String var1, @NotNull String var2, @NotNull String var3, int var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "summary");
      Intrinsics.checkNotNullParameter(var2, "id");
      Intrinsics.checkNotNullParameter(var3, "title");
      return new HelpCenterCollection(var1, var2, var3, var4, var5);
   }

   // $VF: synthetic method
   public static HelpCenterCollection copy$default(HelpCenterCollection var0, String var1, String var2, String var3, int var4, int var5, int var6, Object var7) {
      if ((var6 & 1) != 0) {
         var1 = var0.summary;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.id;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.title;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.articlesCount;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.collectionsCount;
      }

      return var0.copy(var1, var2, var3, var4, var5);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterCollection(summary="
         + this.summary
         + ", id="
         + this.id
         + ", title="
         + this.title
         + ", articlesCount="
         + this.articlesCount
         + ", collectionsCount="
         + this.collectionsCount
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.summary.hashCode();
      var1 = var1 * 31 + this.id.hashCode();
      var1 = var1 * 31 + this.title.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.articlesCount);
      return var1 * 31 + Integer.hashCode(this.collectionsCount);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HelpCenterCollection)) {
         return false;
      } else {
         HelpCenterCollection var2 = (HelpCenterCollection)var1;
         if (!Intrinsics.areEqual(this.summary, var2.summary)) {
            return false;
         } else if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.title, var2.title)) {
            return false;
         } else {
            return this.articlesCount != var2.articlesCount ? false : this.collectionsCount == var2.collectionsCount;
         }
      }
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(HelpCenterCollection var0, CompositeEncoder var1, SerialDescriptor var2) {
      if (var1.shouldEncodeElementDefault(var2, 0) ? true : !Intrinsics.areEqual(var0.summary, "")) {
         var1.encodeStringElement(var2, 0, var0.summary);
      }

      var1.encodeStringElement(var2, 1, var0.id);
      if (var1.shouldEncodeElementDefault(var2, 2) ? true : !Intrinsics.areEqual(var0.title, "")) {
         var1.encodeStringElement(var2, 2, var0.title);
      }

      if (var1.shouldEncodeElementDefault(var2, 3) ? true : var0.articlesCount != 0) {
         var1.encodeIntElement(var2, 3, var0.articlesCount);
      }

      if (var1.shouldEncodeElementDefault(var2, 4) ? true : var0.collectionsCount != 0) {
         var1.encodeIntElement(var2, 4, var0.collectionsCount);
      }
   }

   // $VF: synthetic method
   public HelpCenterCollection(int var1, String var2, String var3, String var4, int var5, int var6, SerializationConstructorMarker var7) {
      super();
      if (2 != (2 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 2, HelpCenterCollection$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      if ((var1 & 1) == 0) {
         this.summary = "";
      } else {
         this.summary = var2;
      }

      this.id = var3;
      if ((var1 & 4) == 0) {
         this.title = "";
      } else {
         this.title = var4;
      }

      if ((var1 & 8) == 0) {
         this.articlesCount = 0;
      } else {
         this.articlesCount = var5;
      }

      if ((var1 & 16) == 0) {
         this.collectionsCount = 0;
      } else {
         this.collectionsCount = var6;
      }
   }
}
