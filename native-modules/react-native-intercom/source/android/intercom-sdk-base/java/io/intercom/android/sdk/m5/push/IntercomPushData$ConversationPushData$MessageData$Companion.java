package io.intercom.android.sdk.m5.push;

import java.lang.annotation.Annotation;
import kotlin.Metadata;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SealedClassSerializer;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¨\u0006\u0007",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Companion;",
         "",
         "<init>",
         "()V",
         "serializer",
         "Lkotlinx/serialization/KSerializer;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPushData$ConversationPushData$MessageData$Companion {
   // $VF: synthetic field
   static final IntercomPushData$ConversationPushData$MessageData$Companion $$INSTANCE = new IntercomPushData$ConversationPushData$MessageData$Companion();

   private IntercomPushData$ConversationPushData$MessageData$Companion() {
   }

   @NotNull
   public final KSerializer serializer() {
      KClass var10003 = Reflection.getOrCreateKotlinClass(IntercomPushData$ConversationPushData$MessageData.class);
      KClass[] var1 = new KClass[]{
         Reflection.getOrCreateKotlinClass(IntercomPushData$ConversationPushData$MessageData$Attachment.class),
         Reflection.getOrCreateKotlinClass(IntercomPushData$ConversationPushData$MessageData$Image.class),
         Reflection.getOrCreateKotlinClass(IntercomPushData$ConversationPushData$MessageData$Text.class)
      };
      KSerializer[] var2 = new KSerializer[]{
         IntercomPushData$ConversationPushData$MessageData$Attachment$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE,
         IntercomPushData$ConversationPushData$MessageData$Image$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE,
         IntercomPushData$ConversationPushData$MessageData$Text$RecoveredInnere3b0c44298fc1c14$serializer.INSTANCE
      };
      return new SealedClassSerializer(
         "io.intercom.android.sdk.m5.push.IntercomPushData.ConversationPushData.MessageData", var10003, var1, var2, new Annotation[0]
      );
   }
}
