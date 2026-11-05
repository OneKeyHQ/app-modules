package io.intercom.android.sdk.helpcenter.sections;

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
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 )2\u00020\u0001:\u0002()B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bB9\b\u0010\u0012\u0006\u0010\t\u001a\u00020\n\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\u0007\u0010\rJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0006HÆ\u0003J'\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\nH×\u0001J\t\u0010\u001f\u001a\u00020\u0003H×\u0001J%\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u00002\u0006\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&H\u0001¢\u0006\u0002\b'R\u001c\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u000e\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0012\u0010\u000f\u001a\u0004\b\u0013\u0010\u0011R\u001c\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0014\u0010\u000f\u001a\u0004\b\u0015\u0010\u0016¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/sections/Author;",
         "",
         "id",
         "",
         "name",
         "avatar",
         "Lio/intercom/android/sdk/helpcenter/sections/Avatar;",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/sections/Avatar;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/helpcenter/sections/Avatar;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getId$annotations",
         "()V",
         "getId",
         "()Ljava/lang/String;",
         "getName$annotations",
         "getName",
         "getAvatar$annotations",
         "getAvatar",
         "()Lio/intercom/android/sdk/helpcenter/sections/Avatar;",
         "component1",
         "component2",
         "component3",
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
public final class Author {
   @NotNull
   public static final Author$Companion Companion = new Author$Companion(null);
   @NotNull
   private final String id;
   @NotNull
   private final String name;
   @NotNull
   private final Avatar avatar;
   public static int $stable;

   static {}

   public Author(@NotNull String var1, @NotNull String var2, @NotNull Avatar var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      this.id = var1;
      this.name = var2;
      this.avatar = var3;
   }

   // $VF: synthetic method
   public Author(String var1, String var2, Avatar var3, int var4, DefaultConstructorMarker var5) {
      this(var1, (((var4 & 2) != 0) ? (String) ("") : var2), (((var4 & 4) != 0) ? (Avatar) (new Avatar(null, null, 3, null)) : var3));
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
   public final String getName() {
      return this.name;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("display_name")
   public static void getName$annotations() {
   }

   @NotNull
   public final Avatar getAvatar() {
      return this.avatar;
   }

   /** @deprecated */
   // $VF: synthetic method
   @SerialName("avatar")
   public static void getAvatar$annotations() {
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final String component2() {
      return this.name;
   }

   @NotNull
   public final Avatar component3() {
      return this.avatar;
   }

   @NotNull
   public final Author copy(@NotNull String var1, @NotNull String var2, @NotNull Avatar var3) {
      Intrinsics.checkNotNullParameter(var1, "id");
      Intrinsics.checkNotNullParameter(var2, "name");
      Intrinsics.checkNotNullParameter(var3, "avatar");
      return new Author(var1, var2, var3);
   }

   // $VF: synthetic method
   public static Author copy$default(Author var0, String var1, String var2, Avatar var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.id;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.name;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.avatar;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Author(id=" + this.id + ", name=" + this.name + ", avatar=" + this.avatar + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.id.hashCode();
      var1 = var1 * 31 + this.name.hashCode();
      return var1 * 31 + this.avatar.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Author)) {
         return false;
      } else {
         Author var2 = (Author)var1;
         if (!Intrinsics.areEqual(this.id, var2.id)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.name, var2.name) ? false : Intrinsics.areEqual(this.avatar, var2.avatar);
         }
      }
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(Author var0, CompositeEncoder var1, SerialDescriptor var2) {
      var1.encodeStringElement(var2, 0, var0.id);
      if (var1.shouldEncodeElementDefault(var2, 1) ? true : !Intrinsics.areEqual(var0.name, "")) {
         var1.encodeStringElement(var2, 1, var0.name);
      }

      if (var1.shouldEncodeElementDefault(var2, 2) ? true : !Intrinsics.areEqual(var0.avatar, new Avatar(null, null, 3, null))) {
         var1.encodeSerializableElement(var2, 2, Avatar$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE, var0.avatar);
      }
   }

   // $VF: synthetic method
   public Author(int var1, String var2, String var3, Avatar var4, SerializationConstructorMarker var5) {
      super();
      if (1 != (1 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(var1, 1, Author$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor());
      }

      
      this.id = var2;
      if ((var1 & 2) == 0) {
         this.name = "";
      } else {
         this.name = var3;
      }

      if ((var1 & 4) == 0) {
         this.avatar = new Avatar(null, null, 3, null);
      } else {
         this.avatar = var4;
      }
   }
}
