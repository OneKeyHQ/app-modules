package io.intercom.android.sdk.m5.components.avatar;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b",
   d2 = {
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarShape$Companion;",
         "",
         "<init>",
         "()V",
         "fromName",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarShape;",
         "shape",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nAvatarIcon.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarShape$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,454:1\n295#2,2:455\n*S KotlinDebug\n*F\n+ 1 AvatarIcon.kt\nio/intercom/android/sdk/m5/components/avatar/AvatarShape$Companion\n*L\n63#1:455,2\n*E\n"
)
public final class AvatarShape$Companion {
   private AvatarShape$Companion() {
   }

   @NotNull
   public final AvatarShape fromName(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "shape");
      Iterable var2 = AvatarShape.getEntries();
      int var3 = 0;
      Iterator var4 = var2.iterator();

      Object var10000;
      while (true) {
         if (var4.hasNext()) {
            Object var5 = var4.next();
            AvatarShape var6 = (AvatarShape)var5;
            int var7 = 0;
            if (!StringsKt.equals(var6.name(), var1, true)) {
               continue;
            }

            var10000 = (AvatarShape)var5;
            break;
         }

         var10000 = null;
         break;
      }

      AvatarShape recoveredShape = (AvatarShape)var10000;
      if (recoveredShape == null) {
         recoveredShape = AvatarShape.CIRCLE;
      }

      return recoveredShape;
   }

   // $VF: synthetic method
   public AvatarShape$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
