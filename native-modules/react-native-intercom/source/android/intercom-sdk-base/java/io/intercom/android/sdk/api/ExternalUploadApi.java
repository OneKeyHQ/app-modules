package io.intercom.android.sdk.api;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\b`\u0018\u00002\u00020\u0001Jz\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0001\u0010\u0005\u001a\u0004\u0018\u00010\u00062\b\b\u0001\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\t\u001a\u00020\b2\b\b\u0001\u0010\n\u001a\u00020\b2\b\b\u0001\u0010\u000b\u001a\u00020\b2\b\b\u0001\u0010\f\u001a\u00020\b2\b\b\u0001\u0010\r\u001a\u00020\b2\b\b\u0001\u0010\u000e\u001a\u00020\b2\b\b\u0001\u0010\u000f\u001a\u00020\b2\b\b\u0001\u0010\u0010\u001a\u00020\bH§@¢\u0006\u0002\u0010\u0011¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/api/ExternalUploadApi;",
         "",
         "uploadFileSuspended",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "",
         "url",
         "",
         "key",
         "Lokhttp3/MultipartBody$Part;",
         "acl",
         "contentType",
         "accessKey",
         "policy",
         "signature",
         "successActionStatus",
         "metadata",
         "filePart",
         "(Ljava/lang/String;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
public interface ExternalUploadApi {
   @Multipart
   @POST
   @Nullable
   Object uploadFileSuspended(
      @Url @Nullable String var1,
      @Part @NotNull okhttp3.MultipartBody.Part var2,
      @Part @NotNull okhttp3.MultipartBody.Part var3,
      @Part @NotNull okhttp3.MultipartBody.Part var4,
      @Part @NotNull okhttp3.MultipartBody.Part var5,
      @Part @NotNull okhttp3.MultipartBody.Part var6,
      @Part @NotNull okhttp3.MultipartBody.Part var7,
      @Part @NotNull okhttp3.MultipartBody.Part var8,
      @Part @NotNull okhttp3.MultipartBody.Part var9,
      @Part @NotNull okhttp3.MultipartBody.Part var10,
      @NotNull Continuation var11
   );
}
