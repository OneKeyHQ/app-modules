package io.intercom.android.sdk.m5.upload.data;

import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.api.ExternalUploadApi;
import io.intercom.android.sdk.api.MessengerApi;
import io.intercom.android.sdk.api.MessengerApiHelper;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.models.Upload;
import io.intercom.android.sdk.models.Upload$Builder;
import java.util.Map;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.RequestBody;
import okhttp3.MultipartBody.Part;
import okhttp3.MultipartBody.Part.Companion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0001\u0018\u00002\u00020\u0001B/\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\u001c\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u00112\u0006\u0010\u0013\u001a\u00020\u0014H\u0086@¢\u0006\u0002\u0010\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/upload/data/UploadRepository;",
         "",
         "messengerApi",
         "Lio/intercom/android/sdk/api/MessengerApi;",
         "externalUploadApi",
         "Lio/intercom/android/sdk/api/ExternalUploadApi;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "context",
         "Landroid/content/Context;",
         "<init>",
         "(Lio/intercom/android/sdk/api/MessengerApi;Lio/intercom/android/sdk/api/ExternalUploadApi;Lio/intercom/android/sdk/identity/UserIdentity;Landroid/content/Context;)V",
         "getUserIdentity",
         "()Lio/intercom/android/sdk/identity/UserIdentity;",
         "getContext",
         "()Landroid/content/Context;",
         "uploadFile",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/models/Upload$Builder;",
         "imageData",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class UploadRepository {
   @NotNull
   private final MessengerApi messengerApi;
   @NotNull
   private final ExternalUploadApi externalUploadApi;
   @NotNull
   private final UserIdentity userIdentity;
   @NotNull
   private final Context context;
   public static final int $stable;

   static { $stable = 8; }

   public UploadRepository(@NotNull MessengerApi var1, @NotNull ExternalUploadApi var2, @NotNull UserIdentity var3, @NotNull Context var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "messengerApi");
      Intrinsics.checkNotNullParameter(var2, "externalUploadApi");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      Intrinsics.checkNotNullParameter(var4, "context");
      this.messengerApi = var1;
      this.externalUploadApi = var2;
      this.userIdentity = var3;
      this.context = var4;
   }

   // $VF: synthetic method
   public UploadRepository(MessengerApi var1, ExternalUploadApi var2, UserIdentity var3, Context var4, int var5, DefaultConstructorMarker var6) {
      this((((var5 & 1) != 0) ? (MessengerApi) (Injector.get().getMessengerApi()) : var1), (((var5 & 2) != 0) ? (ExternalUploadApi) (Injector.get().getExternalUploadApi()) : var2), (((var5 & 4) != 0) ? (UserIdentity) (Injector.get().getUserIdentity()) : var3), (((var5 & 8) != 0) ? (Context) (Injector.get().getApplication()) : var4));
   }

   @NotNull
   public final UserIdentity getUserIdentity() {
      return this.userIdentity;
   }

   @NotNull
   public final Context getContext() {
      return this.context;
   }

   @Nullable
   public final Object uploadFile(@NotNull MediaData$Media var1, @NotNull Continuation var2) {
      UploadRepository recoveredThis = this;
      UploadRepository$uploadFile$1 var9;
      label76: {
         if (var2 instanceof UploadRepository$uploadFile$1) {
            var9 = (UploadRepository$uploadFile$1)var2;
            if ((var9.label & -2147483648) != 0) {
               var9.label -= Integer.MIN_VALUE;
               break label76;
            }
         }

         var9 = new UploadRepository$uploadFile$1(recoveredThis, var2);
      }

      NetworkResponse var5;
      Object var10000;
      label80: {
         Object var8 = var9.result;
         Object var10 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         switch (var9.label) {
            case 0:
               ResultKt.throwOnFailure(var8);
               Map var4 = MapsKt.createMapBuilder();
               Map var12 = var4;
               int var6 = 0;
               var12.put("original_filename", var1.getFileName());
               var12.put("size_in_bytes", Boxing.boxLong(var1.getSize()));
               var12.put("content_type", var1.getMimeType());
               if (var1 instanceof MediaData$Media$Image) {
                  var12.put("width", Boxing.boxInt(((MediaData$Media$Image)var1).getWidth()));
                  var12.put("height", Boxing.boxInt(((MediaData$Media$Image)var1).getHeight()));
                  if (!((MediaData$Media$Image)var1).getExifData().isEmpty()) {
                     var12.put("image_exif", ((MediaData$Media$Image)var1).getExifData());
                  }
               }

               if (var1 instanceof MediaData$Media$Video) {
                  var12.put("width", Boxing.boxInt(((MediaData$Media$Video)var1).getWidth()));
                  var12.put("height", Boxing.boxInt(((MediaData$Media$Video)var1).getHeight()));
               }

               Map var3 = MapsKt.build(var4);
               MessengerApiHelper var16 = MessengerApiHelper.INSTANCE;
               Pair[] var13 = new Pair[]{
                  TuplesKt.to("upload", var3), TuplesKt.to("user", recoveredThis.userIdentity.toMap()), TuplesKt.to("include_metadata", Boxing.boxBoolean(true))
               };
               RequestBody var11 = var16.getDefaultRequestBody$intercom_sdk_base_release(MapsKt.mapOf(var13));
               MessengerApi var17 = recoveredThis.messengerApi;
               var9.L$0 = recoveredThis;
               var9.L$1 = var1;
               var9.label = 1;
               var10000 = var17.getUploadFileUrlSuspended(var11, var9);
               if (var10000 == var10) {
                  return var10;
               }
               break;
            case 1:
               var1 = (MediaData$Media)var9.L$1;
               recoveredThis = (UploadRepository)var9.L$0;
               ResultKt.throwOnFailure(var8);
               var10000 = var8;
               break;
            case 2:
               var5 = (NetworkResponse)var9.L$0;
               ResultKt.throwOnFailure(var8);
               var10000 = var8;
               break label80;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         var5 = (NetworkResponse)var10000;
         if (var5 instanceof NetworkResponse$ClientError || var5 instanceof NetworkResponse$NetworkError || var5 instanceof NetworkResponse$ServerError) {
            return var5;
         }

         if (!(var5 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         Upload var14 = ((Upload$Builder)((NetworkResponse$Success)var5).getBody()).build();
         ExternalUploadApi var18 = recoveredThis.externalUploadApi;
         String var10001 = var14.getUploadDestination();
         Companion var10002 = Part.Companion;
         String var10004 = var14.getKey();
         Intrinsics.checkNotNullExpressionValue(var10004, "getKey(...)");
         Part var20 = var10002.createFormData("key", var10004);
         Companion var10003 = Part.Companion;
         String var10005 = var14.getAcl();
         Intrinsics.checkNotNullExpressionValue(var10005, "getAcl(...)");
         Part var21 = var10003.createFormData("acl", var10005);
         Companion var22 = Part.Companion;
         String var10006 = var14.getContentType();
         Intrinsics.checkNotNullExpressionValue(var10006, "getContentType(...)");
         Part var23 = var22.createFormData("Content-Type", var10006);
         Companion var24 = Part.Companion;
         String var10007 = var14.getAwsAccessKey();
         Intrinsics.checkNotNullExpressionValue(var10007, "getAwsAccessKey(...)");
         Part var25 = var24.createFormData("AWSAccessKeyId", var10007);
         Companion var26 = Part.Companion;
         String var10008 = var14.getPolicy();
         Intrinsics.checkNotNullExpressionValue(var10008, "getPolicy(...)");
         Part var27 = var26.createFormData("policy", var10008);
         Companion var28 = Part.Companion;
         String var10009 = var14.getSignature();
         Intrinsics.checkNotNullExpressionValue(var10009, "getSignature(...)");
         Part var29 = var28.createFormData("signature", var10009);
         Companion var30 = Part.Companion;
         String var10010 = var14.getSuccessActionStatus();
         Intrinsics.checkNotNullExpressionValue(var10010, "getSuccessActionStatus(...)");
         Part var31 = var30.createFormData("success_action_status", var10010);
         Part var32 = Part.Companion.createFormData("x-amz-meta-safe_app_id", var14.getMetadata().getSafeAppId());
         Part var33 = Part.Companion.createFormData("file", var1.getFileName(), new UploadRequestBody(recoveredThis.context, var1));
         var9.L$0 = var5;
         var9.L$1 = null;
         var9.label = 2;
         var10000 = var18.uploadFileSuspended(var10001, var20, var21, var23, var25, var27, var29, var31, var32, var33, var9);
         if (var10000 == var10) {
            return var10;
         }
      }

      NetworkResponse var7 = (NetworkResponse)var10000;
      NetworkResponse var19;
      if (var7 instanceof NetworkResponse$ClientError) {
         var19 = var7;
      } else if (var7 instanceof NetworkResponse$NetworkError) {
         var19 = var7;
      } else if (var7 instanceof NetworkResponse$ServerError) {
         var19 = var7;
      } else {
         if (!(var7 instanceof NetworkResponse$Success)) {
            throw new NoWhenBranchMatchedException();
         }

         var19 = var5;
      }

      return var19;
   }

   public UploadRepository() {
      this(null, null, null, null, 15, null);
   }
}
