package io.intercom.android.sdk.api;

import androidx.compose.runtime.internal.StabilityInferred;
import com.jakewharton.retrofit2.converter.kotlinx.serialization.KotlinSerializationConverterFactory;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.json.JsonBuilder;
import kotlinx.serialization.json.JsonKt;
import okhttp3.MediaType;
import org.jetbrains.annotations.NotNull;
import retrofit2.Converter.Factory;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\bÁ\u0002\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/api/KotlinXConvertorFactory;",
         "",
         "<init>",
         "()V",
         "getConvertorFactory",
         "Lretrofit2/Converter$Factory;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class KotlinXConvertorFactory {
   @NotNull
   public static final KotlinXConvertorFactory INSTANCE = new KotlinXConvertorFactory();
   public static int $stable;

   static {}

   private KotlinXConvertorFactory() {
   }

   @NotNull
   public final Factory getConvertorFactory() {
      MediaType var1 = MediaType.Companion.get("application/json");
      return KotlinSerializationConverterFactory.create(JsonKt.Json$default(null, ((kotlin.jvm.functions.Function1) (recoveredArgument_51_0) -> KotlinXConvertorFactory.getConvertorFactory$lambda$0((kotlinx.serialization.json.JsonBuilder)recoveredArgument_51_0)), 1, null), var1);
   }

   private static final Unit getConvertorFactory$lambda$0(JsonBuilder var0) {
      Intrinsics.checkNotNullParameter(var0, "$this$Json");
      var0.setLenient(true);
      var0.setIgnoreUnknownKeys(true);
      return Unit.INSTANCE;
   }
}
