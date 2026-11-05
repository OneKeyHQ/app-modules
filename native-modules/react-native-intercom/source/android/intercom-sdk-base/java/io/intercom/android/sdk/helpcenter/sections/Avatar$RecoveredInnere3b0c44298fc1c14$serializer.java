package io.intercom.android.sdk.helpcenter.sections;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.UnknownFieldException;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeDecoder;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.encoding.Decoder;
import kotlinx.serialization.encoding.Encoder;
import kotlinx.serialization.internal.GeneratedSerializer;
import kotlinx.serialization.internal.PluginGeneratedSerialDescriptor;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.internal.GeneratedSerializer.DefaultImpls;
import org.jetbrains.annotations.NotNull;

/** @deprecated */
// $VF: synthetic class
@Deprecated(message = "This synthesized declaration should not be used directly", level = DeprecationLevel.HIDDEN)
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\bÇ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0003\u0010\u0004J\u0017\u0010\u0005\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00070\u0006H\u0007¢\u0006\u0002\u0010\bJ\u0010\u0010\t\u001a\u00020\u00022\u0006\u0010\n\u001a\u00020\u000bH\u0007J\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0002H\u0007R\u0016\u0010\u0011\u001a\u00020\u00128\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015",
   d2 = {
         "io/intercom/android/sdk/helpcenter/sections/Avatar.$serializer",
         "Lkotlinx/serialization/internal/GeneratedSerializer;",
         "Lio/intercom/android/sdk/helpcenter/sections/Avatar;",
         "<init>",
         "()V",
         "childSerializers",
         "",
         "Lkotlinx/serialization/KSerializer;",
         "()[Lkotlinx/serialization/KSerializer;",
         "deserialize",
         "decoder",
         "Lkotlinx/serialization/encoding/Decoder;",
         "serialize",
         "",
         "encoder",
         "Lkotlinx/serialization/encoding/Encoder;",
         "value",
         "descriptor",
         "Lkotlinx/serialization/descriptors/SerialDescriptor;",
         "getDescriptor",
         "()Lkotlinx/serialization/descriptors/SerialDescriptor;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Avatar$RecoveredInnere3b0c44298fc1c14$serializer implements GeneratedSerializer {
   @NotNull
   public static final Avatar$RecoveredInnere3b0c44298fc1c14$serializer INSTANCE = new Avatar$RecoveredInnere3b0c44298fc1c14$serializer();
   @NotNull
   private static final SerialDescriptor descriptor;
   public static final int $stable;

   static { $stable = 8; }

   private Avatar$RecoveredInnere3b0c44298fc1c14$serializer() {
   }

   public final void serialize(@NotNull Encoder var1, @NotNull Avatar var2) {
      Intrinsics.checkNotNullParameter(var1, "encoder");
      Intrinsics.checkNotNullParameter(var2, "value");
      SerialDescriptor var3 = descriptor;
      CompositeEncoder var4 = var1.beginStructure(var3);
      Avatar.write$Self$intercom_sdk_base_release(var2, var4, var3);
      var4.endStructure(var3);
   }

   @NotNull
   public final Avatar deserialize(@NotNull Decoder var1) {
      Intrinsics.checkNotNullParameter(var1, "decoder");
      SerialDescriptor var2 = descriptor;
      boolean var3 = true;
      int var5 = 0;
      String var6 = null;
      String var7 = null;
      CompositeDecoder var8 = var1.beginStructure(var2);
      if (var8.decodeSequentially()) {
         var6 = var8.decodeStringElement(var2, 0);
         var5 |= 1;
         var7 = var8.decodeStringElement(var2, 1);
         var5 |= 2;
      } else {
         while (var3) {
            int var4 = var8.decodeElementIndex(var2);
            switch (var4) {
               case -1:
                  var3 = false;
                  break;
               case 0:
                  var6 = var8.decodeStringElement(var2, 0);
                  var5 |= 1;
                  break;
               case 1:
                  var7 = var8.decodeStringElement(var2, 1);
                  var5 |= 2;
                  break;
               default:
                  throw new UnknownFieldException(var4);
            }
         }
      }

      var8.endStructure(var2);
      return new Avatar(var5, var6, var7, null);
   }

   @NotNull
   @Override
   public final SerialDescriptor getDescriptor() {
      return descriptor;
   }

   @NotNull
   @Override
   public final KSerializer[] childSerializers() {
      return new KSerializer[]{StringSerializer.INSTANCE, StringSerializer.INSTANCE};
   }

   @NotNull
   @Override
   public KSerializer[] typeParametersSerializers() {
      return DefaultImpls.typeParametersSerializers(this);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Encoder var1, Object var2) {
      this.serialize(var1, (Avatar)var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   static {
      PluginGeneratedSerialDescriptor var0 = new PluginGeneratedSerialDescriptor("io.intercom.android.sdk.helpcenter.sections.Avatar", INSTANCE, 2);
      var0.addElement("initials", true);
      var0.addElement("image_url", true);
      descriptor = var0;
   }
}
