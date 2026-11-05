package io.intercom.android.sdk.helpcenter.utils.networking;

import androidx.compose.runtime.internal.StabilityInferred;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.CallAdapter.Factory;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B\u001d\u0012\u0014\b\u0002\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003¢\u0006\u0004\b\u0006\u0010\u0007J6\u0010\b\u001a\f\u0012\u0002\b\u0003\u0012\u0002\b\u0003\u0018\u00010\t2\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u000f\u001a\u00020\u0010H\u0096\u0002¢\u0006\u0002\u0010\u0011R\u001a\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponseAdapterFactory;",
         "Lretrofit2/CallAdapter$Factory;",
         "onClientError",
         "Lkotlin/Function1;",
         "",
         "",
         "<init>",
         "(Lkotlin/jvm/functions/Function1;)V",
         "get",
         "Lretrofit2/CallAdapter;",
         "returnType",
         "Ljava/lang/reflect/Type;",
         "annotations",
         "",
         "",
         "retrofit",
         "Lretrofit2/Retrofit;",
         "(Ljava/lang/reflect/Type;[Ljava/lang/annotation/Annotation;Lretrofit2/Retrofit;)Lretrofit2/CallAdapter;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class NetworkResponseAdapterFactory extends Factory {
   @NotNull
   private final Function1 onClientError;
   public static int $stable;

   static {}

   public NetworkResponseAdapterFactory(@NotNull Function1 var1) {
      Intrinsics.checkNotNullParameter(var1, "onClientError");
      super();
      this.onClientError = var1;
   }

   // $VF: synthetic method
   public NetworkResponseAdapterFactory(Function1 var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Function1) (((kotlin.jvm.functions.Function1)(recoveredArgument_116_0)->NetworkResponseAdapterFactory._init_$lambda$0((java.lang.Throwable)recoveredArgument_116_0))) : var1));
   }

   @Nullable
   @Override
   public CallAdapter get(@NotNull Type var1, @NotNull Annotation[] var2, @NotNull Retrofit var3) {
      Intrinsics.checkNotNullParameter(var1, "returnType");
      Intrinsics.checkNotNullParameter(var2, "annotations");
      Intrinsics.checkNotNullParameter(var3, "retrofit");
      if (!Intrinsics.areEqual(Call.class, Factory.getRawType(var1))) {
         return null;
      } else if (!(var1 instanceof ParameterizedType)) {
         int var7 = 0;
         String var8 = "return type must be parameterized as Call<NetworkResponse<<Foo>>";
         throw new IllegalStateException(var8.toString());
      } else {
         Type var4 = Factory.getParameterUpperBound(0, (ParameterizedType)var1);
         if (!Intrinsics.areEqual(Factory.getRawType(var4), NetworkResponse.class)) {
            return null;
         } else if (!(var4 instanceof ParameterizedType)) {
            int var6 = 0;
            String var9 = "Response must be parameterized as NetworkResponse<Foo> or NetworkResponse<out Foo>";
            throw new IllegalStateException(var9.toString());
         } else {
            Type var5 = Factory.getParameterUpperBound(0, (ParameterizedType)var4);
            Intrinsics.checkNotNull(var5);
            return new NetworkResponseAdapter(var5, this.onClientError);
         }
      }
   }

   private static final Unit _init_$lambda$0(Throwable var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   public NetworkResponseAdapterFactory() {
      this(null, 1, null);
   }
}
