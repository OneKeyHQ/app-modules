package io.intercom.android.sdk.helpcenter.sections;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
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
   d1 = "\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 &2\u00020\u0001:\u0002%&B!\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bB5\b\u0010\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u000e\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\u0007\u0010\rJ\u000f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0006HÆ\u0003J#\u0010\u0017\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001b\u001a\u00020\nH×\u0001J\t\u0010\u001c\u001a\u00020\u0006H×\u0001J%\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00002\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0001¢\u0006\u0002\b$R\"\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0012\u0010\u000f\u001a\u0004\b\u0013\u0010\u0014¨\u0006'",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterSection;",
         "",
         "helpCenterArticles",
         "",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterArticle;",
         "title",
         "",
         "<init>",
         "(Ljava/util/List;Ljava/lang/String;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/util/List;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getHelpCenterArticles$annotations",
         "()V",
         "getHelpCenterArticles",
         "()Ljava/util/List;",
         "getTitle$annotations",
         "getTitle",
         "()Ljava/lang/String;",
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
@StabilityInferred(parameters = 0)
public final class HelpCenterSection {
   @NotNull
   public static final HelpCenterSection$Companion Companion = new HelpCenterSection$Companion(null);
   @NotNull
   private final List helpCenterArticles;
   @NotNull
   private final String title;
   public static final int $stable;

   static { $stable = 8; }
   @JvmField
   @NotNull
   private static final KSerializer[] $childSerializers;

   public HelpCenterSection(@NotNull List var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "helpCenterArticles");
      Intrinsics.checkNotNullParameter(var2, "title");
      this.helpCenterArticles = var1;
      this.title = var2;
   }

   // $VF: synthetic method
   public HelpCenterSection(List var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (List) (CollectionsKt.emptyList()) : var1), (((var3 & 2) != 0) ? (String) ("") : var2));
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
   public final List component1() {
      return this.helpCenterArticles;
   }

   @NotNull
   public final String component2() {
      return this.title;
   }

   @NotNull
   public final HelpCenterSection copy(@NotNull List var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "helpCenterArticles");
      Intrinsics.checkNotNullParameter(var2, "title");
      return new HelpCenterSection(var1, var2);
   }

   // $VF: synthetic method
   public static HelpCenterSection copy$default(HelpCenterSection var0, List var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.helpCenterArticles;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.title;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "HelpCenterSection(helpCenterArticles=" + this.helpCenterArticles + ", title=" + this.title + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.helpCenterArticles.hashCode();
      return var1 * 31 + this.title.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HelpCenterSection)) {
         return false;
      }

      HelpCenterSection var2 = (HelpCenterSection)var1;
      return !Intrinsics.areEqual(this.helpCenterArticles, var2.helpCenterArticles) ? false : Intrinsics.areEqual(this.title, var2.title);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(HelpCenterSection var0, CompositeEncoder var1, SerialDescriptor var2) {
      KSerializer[] var3 = $childSerializers;
      if (var1.shouldEncodeElementDefault(var2, 0) ? true : !Intrinsics.areEqual(var0.helpCenterArticles, CollectionsKt.emptyList())) {
         var1.encodeSerializableElement(var2, 0, var3[0], var0.helpCenterArticles);
      }

      if (var1.shouldEncodeElementDefault(var2, 1) ? true : !Intrinsics.areEqual(var0.title, "")) {
         var1.encodeStringElement(var2, 1, var0.title);
      }
   }

   // $VF: synthetic method
   public HelpCenterSection(int var1, List var2, String var3, SerializationConstructorMarker var4) {
      super();
      if ((0 & var1) != 0) {
         PluginExceptionsKt.throwMissingFieldException(var1, 0, HelpCenterSection$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      if ((var1 & 1) == 0) {
         this.helpCenterArticles = CollectionsKt.emptyList();
      } else {
         this.helpCenterArticles = var2;
      }

      if ((var1 & 2) == 0) {
         this.title = "";
      } else {
         this.title = var3;
      }
   }

   public HelpCenterSection() {
      this(null, null, 3, null);
   }

   // $VF: synthetic method
   public static final KSerializer[] access$get$childSerializers$cp() {
      return $childSerializers;
   }

   static {
      KSerializer[] var0 = new KSerializer[]{new ArrayListSerializer(HelpCenterArticle$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE), null};
      $childSerializers = var0;
   }
}
