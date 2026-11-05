package io.intercom.android.sdk.identity;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/identity/JwtClaims;",
         "",
         "<init>",
         "()V",
         "expiresAtMillis",
         "",
         "jwt",
         "",
         "(Ljava/lang/String;)Ljava/lang/Long;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
@SourceDebugExtension(
   "SMAP\nJwtClaims.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JwtClaims.kt\nio/intercom/android/sdk/identity/JwtClaims\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,20:1\n1#2:21\n*E\n"
)
public final class JwtClaims {
   @NotNull
   public static final JwtClaims INSTANCE = new JwtClaims();
   public static int $stable;

   static {}

   private JwtClaims() {
   }

   @Nullable
   public final Long expiresAtMillis(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "jwt");
      String var10000 = var1;
      char[] var4 = new char[]{'.'};
      List var12 = StringsKt.split$default(var10000, var4, false, 0, 6, null);
      List var5 = var12;
      int var6 = 0;
      List var16 = var5.size() == 3 ? var12 : null;
      if (var16 != null) {
         var10000 = (String)var16.get(1);
         if (var10000 != null) {
            String var2 = var10000;

            try {
               ByteString var18 = ByteString.Companion.decodeBase64(var2);
               if (var18 != null) {
                  var10000 = var18.utf8();
                  if (var10000 != null) {
                     String var11 = var10000;
                     JsonElement var14 = JsonParser.parseString(var11).getAsJsonObject().get("exp");
                     if (var14 != null) {
                        JsonElement var7 = var14;
                        JsonElement itx = var7;
                        int var9 = 0;
                        JsonElement var15 = itx.isJsonPrimitive() && itx.getAsJsonPrimitive().isNumber() ? var7 : null;
                        if (var15 != null) {
                           JsonElement var13 = var15;
                           return var13.getAsLong() * 1000;
                        }
                     }

                     return null;
                  }
               }

               return null;
            } catch (Exception var10) {
               return null;
            }
         }
      }

      return null;
   }
}
