package io.intercom.android.sdk.helpcenter.sections;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
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
import kotlinx.serialization.internal.IntSerializer;
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
         "io/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent.$serializer",
         "Lkotlinx/serialization/internal/GeneratedSerializer;",
         "Lio/intercom/android/sdk/helpcenter/sections/HelpCenterCollectionContent;",
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
public final class HelpCenterCollectionContent$RecoveredInnere3b0c44298fc1c14$serializer implements GeneratedSerializer {
   @NotNull
   public static final HelpCenterCollectionContent$RecoveredInnere3b0c44298fc1c14$serializer INSTANCE = new HelpCenterCollectionContent$RecoveredInnere3b0c44298fc1c14$serializer();
   @NotNull
   private static final SerialDescriptor descriptor;
   public static final int $stable;

   static { $stable = 8; }

   private HelpCenterCollectionContent$RecoveredInnere3b0c44298fc1c14$serializer() {
   }

   public final void serialize(@NotNull Encoder var1, @NotNull HelpCenterCollectionContent var2) {
      Intrinsics.checkNotNullParameter(var1, "encoder");
      Intrinsics.checkNotNullParameter(var2, "value");
      SerialDescriptor var3 = descriptor;
      CompositeEncoder var4 = var1.beginStructure(var3);
      HelpCenterCollectionContent.write$Self$intercom_sdk_base_release(var2, var4, var3);
      var4.endStructure(var3);
   }

   @NotNull
   public final HelpCenterCollectionContent deserialize(@NotNull Decoder var1) {
      Intrinsics.checkNotNullParameter(var1, "decoder");
      SerialDescriptor var2 = descriptor;
      boolean var3 = true;
      int var5 = 0;
      String var6 = null;
      String var7 = null;
      String var8 = null;
      List var9 = null;
      List var10 = null;
      List var11 = null;
      int var12 = 0;
      List var13 = null;
      CompositeDecoder var14 = var1.beginStructure(var2);
      KSerializer[] var15 = HelpCenterCollectionContent.access$get$childSerializers$cp();
      if (var14.decodeSequentially()) {
         var6 = var14.decodeStringElement(var2, 0);
         var5 |= 1;
         var7 = var14.decodeStringElement(var2, 1);
         var5 |= 2;
         var8 = var14.decodeStringElement(var2, 2);
         var5 |= 4;
         var9 = (List)var14.decodeSerializableElement(var2, 3, var15[3], var9);
         var5 |= 8;
         var10 = (List)var14.decodeSerializableElement(var2, 4, var15[4], var10);
         var5 |= 16;
         var11 = (List)var14.decodeSerializableElement(var2, 5, var15[5], var11);
         var5 |= 32;
         var12 = var14.decodeIntElement(var2, 6);
         var5 |= 64;
         var13 = (List)var14.decodeSerializableElement(var2, 7, var15[7], var13);
         var5 |= 128;
      } else {
         while (var3) {
            int var4 = var14.decodeElementIndex(var2);
            switch (var4) {
               case -1:
                  var3 = false;
                  break;
               case 0:
                  var6 = var14.decodeStringElement(var2, 0);
                  var5 |= 1;
                  break;
               case 1:
                  var7 = var14.decodeStringElement(var2, 1);
                  var5 |= 2;
                  break;
               case 2:
                  var8 = var14.decodeStringElement(var2, 2);
                  var5 |= 4;
                  break;
               case 3:
                  var9 = (List)var14.decodeSerializableElement(var2, 3, var15[3], var9);
                  var5 |= 8;
                  break;
               case 4:
                  var10 = (List)var14.decodeSerializableElement(var2, 4, var15[4], var10);
                  var5 |= 16;
                  break;
               case 5:
                  var11 = (List)var14.decodeSerializableElement(var2, 5, var15[5], var11);
                  var5 |= 32;
                  break;
               case 6:
                  var12 = var14.decodeIntElement(var2, 6);
                  var5 |= 64;
                  break;
               case 7:
                  var13 = (List)var14.decodeSerializableElement(var2, 7, var15[7], var13);
                  var5 |= 128;
                  break;
               default:
                  throw new UnknownFieldException(var4);
            }
         }
      }

      var14.endStructure(var2);
      return new HelpCenterCollectionContent(var5, var6, var7, var8, var9, var10, var11, var12, var13, null);
   }

   @NotNull
   @Override
   public final SerialDescriptor getDescriptor() {
      return descriptor;
   }

   @NotNull
   @Override
   public final KSerializer[] childSerializers() {
      KSerializer[] var1 = HelpCenterCollectionContent.access$get$childSerializers$cp();
      return new KSerializer[]{
         StringSerializer.INSTANCE, StringSerializer.INSTANCE, StringSerializer.INSTANCE, var1[3], var1[4], var1[5], IntSerializer.INSTANCE, var1[7]
      };
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
      this.serialize(var1, (HelpCenterCollectionContent)var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   

   static {
      PluginGeneratedSerialDescriptor var0 = new PluginGeneratedSerialDescriptor(
         "io.intercom.android.sdk.helpcenter.sections.HelpCenterCollectionContent", INSTANCE, 8
      );
      var0.addElement("id", false);
      var0.addElement("name", true);
      var0.addElement("description", true);
      var0.addElement("articles", true);
      var0.addElement("sections", true);
      var0.addElement("collections", true);
      var0.addElement("article_count", false);
      var0.addElement("authors", true);
      descriptor = var0;
   }
}
