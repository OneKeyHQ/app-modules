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
@SerialName("text")
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0087\b\u0018\u0000 \u001e2\u00020\u0001:\u0002\u001d\u001eB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B%\b\u0010\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u0004\u0010\nJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0007H×\u0001J\t\u0010\u0014\u001a\u00020\u0003H×\u0001J%\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00002\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0001¢\u0006\u0002\b\u001cR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Text;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "message",
         "",
         "<init>",
         "(Ljava/lang/String;)V",
         "seen0",
         "",
         "serializationConstructorMarker",
         "Lkotlinx/serialization/internal/SerializationConstructorMarker;",
         "(ILjava/lang/String;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V",
         "getMessage",
         "()Ljava/lang/String;",
         "component1",
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
public final class IntercomPushData$ConversationPushData$MessageData$Text implements IntercomPushData$ConversationPushData$MessageData {
   @NotNull
   public static final IntercomPushData$ConversationPushData$MessageData$Text$Companion Companion = new IntercomPushData$ConversationPushData$MessageData$Text$Companion(
      null
   );
   @NotNull
   private final String message;
   public static int $stable;

   static {}

   public IntercomPushData$ConversationPushData$MessageData$Text(@NotNull String var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "message");
      this.message = var1;
   }

   @NotNull
   public final String getMessage() {
      return this.message;
   }

   @NotNull
   public final String component1() {
      return this.message;
   }

   @NotNull
   public final IntercomPushData$ConversationPushData$MessageData$Text copy(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "message");
      return new IntercomPushData$ConversationPushData$MessageData$Text(var1);
   }

   // $VF: synthetic method
   public static IntercomPushData$ConversationPushData$MessageData$Text copy$default(
      IntercomPushData$ConversationPushData$MessageData$Text var0, String var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.message;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Text(message=" + this.message + ')';
   }

   @Override
   public int hashCode() {
      return this.message.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof IntercomPushData$ConversationPushData$MessageData$Text)) {
         return false;
      }

      IntercomPushData$ConversationPushData$MessageData$Text var2 = (IntercomPushData$ConversationPushData$MessageData$Text)var1;
      return Intrinsics.areEqual(this.message, var2.message);
   }

   // $VF: synthetic method
   @JvmStatic
   public static final void write$Self$intercom_sdk_base_release(
      IntercomPushData$ConversationPushData$MessageData$Text var0, CompositeEncoder var1, SerialDescriptor var2
   ) {
      var1.encodeStringElement(var2, 0, var0.message);
   }

   // $VF: synthetic method
   public IntercomPushData$ConversationPushData$MessageData$Text(int var1, String var2, SerializationConstructorMarker var3) {
      super();
      if (1 != (1 & var1)) {
         PluginExceptionsKt.throwMissingFieldException(
            var1, 1, IntercomPushData$ConversationPushData$MessageData$Text$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE.getDescriptor()
         );
      }

      
      this.message = var2;
   }
}
