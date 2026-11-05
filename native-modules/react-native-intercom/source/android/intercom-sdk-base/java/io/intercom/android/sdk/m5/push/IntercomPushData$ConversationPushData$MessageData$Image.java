package io.intercom.android.sdk.m5.push;

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
@SerialName("image")
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 !2\u00020\u0001:\u0002 !B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006B/\b\u0010\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\u0004\b\u0005\u0010\u000bJ\t\u0010\u000f\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\u0011\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\bH×\u0001J\t\u0010\u0017\u001a\u00020\u0003H×\u0001J%\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u00002\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001eH\u0001¢\u0006\u0002\b\u001fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\r¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Image;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "title",
         "",
         "url",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Ljava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getTitle",
         "()Ljava/lang/String;",
         "getUrl",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "",
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
public final class IntercomPushData$ConversationPushData$MessageData$Image implements IntercomPushData$ConversationPushData$MessageData {
   @NotNull
   public static final IntercomPushData$ConversationPushData$MessageData$Image$Companion Companion = new IntercomPushData$ConversationPushData$MessageData$Image$Companion(
      null
   );
   @NotNull
   private final String title;
   @NotNull
   private final String url;
   public static int $stable;

   static {}

   public IntercomPushData$ConversationPushData$MessageData$Image(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "url");
      this.title = var1;
      this.url = var2;
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
      return this.title;
   }

   @NotNull
   public final String component2() {
      return this.url;
   }

   @NotNull
   public final IntercomPushData$ConversationPushData$MessageData$Image copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "title");
      Intrinsics.checkNotNullParameter(var2, "url");
      return new IntercomPushData$ConversationPushData$MessageData$Image(var1, var2);
   }

   // $VF: synthetic method
   public static IntercomPushData$ConversationPushData$MessageData$Image copy$default(
      IntercomPushData$ConversationPushData$MessageData$Image var0, String var1, String var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.title;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.url;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Image(title=" + this.title + ", url=" + this.url + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.title.hashCode();
      return var1 * 31 + this.url.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomPushData$ConversationPushData$MessageData$Image)) {
         return false;
      }

      IntercomPushData$ConversationPushData$MessageData$Image var2 = (IntercomPushData$ConversationPushData$MessageData$Image)var1;
      return !Intrinsics.areEqual(this.title, var2.title) ? false : Intrinsics.areEqual(this.url, var2.url);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(
      IntercomPushData$ConversationPushData$MessageData$Image var0, CompositeEncoder var1, SerialDescriptor var2
   ) {
      var1.encodeStringElement(var2, 0, var0.title);
      var1.encodeStringElement(var2, 1, var0.url);
   }

   // $VF: synthetic method
   public IntercomPushData$ConversationPushData$MessageData$Image(int var1, String var2, String var3, SerializationConstructorMarker var4) {
      super();
      if (3 != (3 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(
            var1, 3, IntercomPushData$ConversationPushData$MessageData$Image$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor()
         );
      }

      
      this.title = var2;
      this.url = var3;
   }
}
