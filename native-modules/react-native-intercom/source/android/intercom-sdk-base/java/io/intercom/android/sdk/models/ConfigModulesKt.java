package io.intercom.android.sdk.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\u001a\f\u0010\u0000\u001a\u00020\u0001*\u00020\u0001H\u0000\u001a\f\u0010\u0000\u001a\u00020\u0002*\u00020\u0002H\u0002\u001a\f\u0010\u0000\u001a\u00020\u0003*\u00020\u0003H\u0002\u001a\f\u0010\u0000\u001a\u00020\u0004*\u00020\u0004H\u0002\u001a\f\u0010\u0000\u001a\u00020\u0005*\u00020\u0005H\u0002\u001a\f\u0010\u0000\u001a\u00020\u0006*\u00020\u0006H\u0002\u001a\f\u0010\u0000\u001a\u00020\u0007*\u00020\u0007H\u0002¨\u0006\b",
   d2 = {
         "validated",
         "Lio/intercom/android/sdk/models/ConfigModules;",
         "Lio/intercom/android/sdk/models/CustomizationModel;",
         "Lio/intercom/android/sdk/models/HomeConfig;",
         "Lio/intercom/android/sdk/models/HeaderConfig;",
         "Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "Lio/intercom/android/sdk/models/HeaderContentModel;",
         "Lio/intercom/android/sdk/models/OpenConfig;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConfigModules.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConfigModules.kt\nio/intercom/android/sdk/models/ConfigModulesKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,224:1\n1869#2,2:225\n1869#2,2:227\n1563#2:229\n1634#2,3:230\n*S KotlinDebug\n*F\n+ 1 ConfigModules.kt\nio/intercom/android/sdk/models/ConfigModulesKt\n*L\n210#1:225,2\n211#1:227,2\n222#1:229\n222#1:230,3\n*E\n"
)
public final class ConfigModulesKt {
   @NotNull
   public static final ConfigModules validated(@NotNull ConfigModules var0) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return var0.copy(validated(var0.getCustomization()), validated(var0.getHome()));
   }

   private static final CustomizationModel validated(CustomizationModel var0) {
      CustomizationColorsModel var10001 = CustomizationColorsModel.copy$default(var0.getAction(), null, null, null, null, null, null, 63, null);
      CustomizationColorsModel var10002 = var0.getActionContrastWhite();
      var10002 = var10002 != null ? CustomizationColorsModel.copy$default(var10002, null, null, null, null, null, null, 63, null) : null;
      CustomizationColorsModel var10003 = var0.getActionContrastDark();
      return CustomizationModel.copy$default(
         var0,
         var10001,
         var10002,
         var10003 != null ? CustomizationColorsModel.copy$default(var10003, null, null, null, null, null, null, 63, null) : null,
         null,
         null,
         CustomizationColorsModel.copy$default(var0.getHeader(), null, null, null, null, null, null, 63, null),
         0,
         null,
         null,
         null,
         null,
         0,
         4056,
         null
      );
   }

   private static final HomeConfig validated(HomeConfig var0) {
      return var0.copy(validated(var0.getHeader()), validated(var0.getOpenConfig()));
   }

   private static final HeaderConfig validated(HeaderConfig var0) {
      return HeaderConfig.copy$default(var0, validated(var0.getBackground()), validated(var0.getContent()), false, 4, null);
   }

   private static final HeaderBackgroundModel validated(HeaderBackgroundModel var0) {
      List var10000 = var0.getGradient();
      if (var10000 != null) {
         Iterable var1 = var10000;
         int var2 = 0;

         for (Object var4 : var1) {
            String var5 = (String)var4;
            int var6 = 0;
            if (var5 == null) {
               String var7 = "Required value was null.";
               throw new IllegalArgumentException(var7.toString());
            }
         }
      }

      var10000 = var0.getGradientDark();
      if (var10000 != null) {
         Iterable var8 = var10000;
         int var9 = 0;

         for (Object var11 : var8) {
            String var12 = (String)var11;
            int var13 = 0;
            if (var12 == null) {
               String var14 = "Required value was null.";
               throw new IllegalArgumentException(var14.toString());
            }
         }
      }

      return HeaderBackgroundModel.copy$default(var0, null, null, null, null, null, null, false, null, null, null, false, 2047, null);
   }

   private static final HeaderContentModel validated(HeaderContentModel var0) {
      return HeaderContentModel.copy$default(
         var0,
         null,
         null,
         false,
         HeaderTextModel.copy$default(var0.getGreeting(), null, 0.0F, null, null, 15, null),
         HeaderTextModel.copy$default(var0.getIntro(), null, 0.0F, null, null, 15, null),
         CloseButtonModel.copy$default(var0.getCloseButton(), null, 0.0F, null, 7, null),
         null,
         null,
         199,
         null
      );
   }

   private static final OpenConfig validated(OpenConfig var0) {
      Iterable var1 = var0.getSpaces();
      Object var11 = null;
      OpenConfig var10 = var0;
      int var2 = 0;
      Iterable var3 = var1;
      ArrayList var4 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var1, 10));
      int var5 = 0;

      for (Object var7 : var3) {
         Space var8 = (Space)var7;
         Collection var12 = var4;
         int var9 = 0;
         var12.add(Space.copy$default(var8, null, null, 3, null));
      }

      List var13 = (List)var4;
      return OpenConfig.copy$default(var10, (OpenToSpace)var11, var13, null, 5, null);
   }
}
