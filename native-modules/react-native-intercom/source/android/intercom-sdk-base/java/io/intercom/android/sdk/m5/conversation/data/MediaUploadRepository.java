package io.intercom.android.sdk.m5.conversation.data;

import android.content.Context;
import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.blocks.lib.BlockType;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment;
import io.intercom.android.sdk.blocks.lib.models.BlockAttachment$Companion;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import io.intercom.android.sdk.models.AttachmentSettings;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$Success;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ClientError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$NetworkError;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse$ServerError;
import io.intercom.android.sdk.models.Upload;
import io.intercom.android.sdk.models.Upload$Builder;
import io.intercom.android.sdk.utilities.BitmapUtilsKt;
import io.intercom.android.sdk.m5.utils.MediaCompressionKt;
import kotlin.ResultKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScopeKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\b\u0001\u0018\u00002\u00020\u0001:\u0002\u001c\u001dB)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ,\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0086@¢\u0006\u0002\u0010\u0012J \u0010\u0013\u001a\u00020\r2\u0006\u0010\u0014\u001a\u00020\u000f2\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0086@¢\u0006\u0002\u0010\u0015J \u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;",
         "",
         "conversationRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;",
         "applicationContext",
         "Landroid/content/Context;",
         "attachmentSettings",
         "Lkotlin/Function0;",
         "Lio/intercom/android/sdk/models/AttachmentSettings;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/ConversationRepository;Landroid/content/Context;Lkotlin/jvm/functions/Function0;)V",
         "uploadMediaItems",
         "",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository$UploadResult;",
         "mediaItems",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "transparencyBackgroundColor",
         "",
         "(Ljava/util/List;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "uploadSingleMediaItem",
         "mediaData",
         "(Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;",
         "buildBlockForMedia",
         "Lio/intercom/android/sdk/blocks/lib/models/Block$Builder;",
         "publicUrl",
         "",
         "uploadId",
         "",
         "UploadResult",
         "UploadError",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nMediaUploadRepository.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaUploadRepository.kt\nio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,147:1\n1#2:148\n*E\n"
)
public final class MediaUploadRepository {
   @NotNull
   private final ConversationRepository conversationRepository;
   @NotNull
   private final Context applicationContext;
   @NotNull
   private final Function0 attachmentSettings;
   public static final int $stable;

   static { $stable = 8; }

   public MediaUploadRepository(@NotNull ConversationRepository var1, @NotNull Context var2, @NotNull Function0 var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "conversationRepository");
      Intrinsics.checkNotNullParameter(var2, "applicationContext");
      Intrinsics.checkNotNullParameter(var3, "attachmentSettings");
      this.conversationRepository = var1;
      this.applicationContext = var2;
      this.attachmentSettings = var3;
   }

   // $VF: synthetic method
   public MediaUploadRepository(ConversationRepository var1, Context var2, Function0 var3, int var4, DefaultConstructorMarker var5) {
      this(var1, (((var4 & 2) != 0) ? (Context) (Injector.get().getApplication()) : var2), (((var4 & 4) != 0) ? (Function0) (((kotlin.jvm.functions.Function0)()->MediaUploadRepository._init_$lambda$0())) : var3));
   }

   @Nullable
   public final Object uploadMediaItems(@NotNull List var1, int var2, @NotNull Continuation var3) {
      return CoroutineScopeKt.coroutineScope(new MediaUploadRepository$uploadMediaItems$2(var1, this, var2, null), var3);
   }

   // $VF: synthetic method
   public static Object uploadMediaItems$default(MediaUploadRepository var0, List var1, int var2, Continuation var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = -1;
      }

      return var0.uploadMediaItems(var1, var2, var3);
   }

   @Nullable
   public final Object uploadSingleMediaItem(@NotNull MediaData$Media media, int compressionQuality, @NotNull Continuation completion) {
      MediaUploadRepository$uploadSingleMediaItem$1 state;
      if (completion instanceof MediaUploadRepository$uploadSingleMediaItem$1
            && ((((MediaUploadRepository$uploadSingleMediaItem$1) completion).label & Integer.MIN_VALUE) != 0)) {
         state = (MediaUploadRepository$uploadSingleMediaItem$1) completion;
         state.label -= Integer.MIN_VALUE;
      } else {
         state = new MediaUploadRepository$uploadSingleMediaItem$1(this, completion);
      }
      Object result = state.result;
      Object suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      int resumeLabel = state.label;
      MediaUploadRepository owner;
      MediaData$Media compressed;
      if (resumeLabel == 0) {
         ResultKt.throwOnFailure(result);
         owner = this;
         compressed = null;
      } else if (resumeLabel == 1) {
         compressed = null;
         owner = (MediaUploadRepository) state.L$0;
      } else if (resumeLabel == 2) {
         compressed = (MediaData$Media) state.L$1;
         owner = (MediaUploadRepository) state.L$0;
      } else {
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
      MediaUploadRepository$UploadResult outcome;
      try {
         try {
            if (resumeLabel != 2) {
               Object compressedResult;
               if (resumeLabel == 1) {
                  ResultKt.throwOnFailure(result);
                  compressedResult = result;
               } else {
                  MediaData$Media input;
                  if (media instanceof MediaData$Media$Image && ((MediaData$Media$Image) media).getExifData().isEmpty()) {
                     java.util.Map exif = BitmapUtilsKt.extractExifData(owner.applicationContext, ((MediaData$Media$Image) media).getUri());
                     input = MediaData$Media$Image.copy$default((MediaData$Media$Image) media, null, 0, 0, 0L, null, null, exif, 63, null);
                  } else {
                     input = media;
                  }
                  state.L$0 = owner;
                  state.label = 1;
                  compressedResult = MediaCompressionKt.getCompressedMediaData(input, owner.applicationContext, compressionQuality, state);
                  // A suspended compression owns its own work; no completed media exists to delete.
                  if (compressedResult == suspended) {
                     return suspended;
                  }
               }
               compressed = (MediaData$Media) compressedResult;
            }
            if (resumeLabel != 2 && compressed.getSize() > ((AttachmentSettings) owner.attachmentSettings.invoke()).getUploadSizeLimit()) {
               // Preserve all three settings reads in the original size-limit branch.
               outcome = new MediaUploadRepository$UploadResult$Failure(new MediaUploadRepository$UploadError$SizeLimitExceeded(
                     compressed.getSize(), ((AttachmentSettings) owner.attachmentSettings.invoke()).getUploadSizeLimit(),
                     ((AttachmentSettings) owner.attachmentSettings.invoke()).getUploadSizeLimitMB()));
            } else {
               NetworkResponse response;
               if (resumeLabel == 2) {
                  ResultKt.throwOnFailure(result);
                  response = (NetworkResponse) result;
               } else {
                  state.L$0 = owner;
                  state.L$1 = compressed;
                  state.label = 2;
                  Object uploaded = owner.conversationRepository.uploadMedia(compressed, state);
                  // Suspension retains the compressed file for the resumed upload.
                  if (uploaded == suspended) {
                     return suspended;
                  }
                  response = (NetworkResponse) uploaded;
               }
               if (response instanceof NetworkResponse$Success) {
                  Upload upload = ((Upload$Builder) ((NetworkResponse$Success) response).getBody()).build();
                  String publicUrl = upload.getPublicUrl();
                  Intrinsics.checkNotNullExpressionValue(publicUrl, "getPublicUrl(...)");
                  Block$Builder block = owner.buildBlockForMedia(compressed, publicUrl, upload.getId());
                  outcome = new MediaUploadRepository$UploadResult$Success(block);
               } else if (response instanceof NetworkResponse$ClientError) {
                  outcome = new MediaUploadRepository$UploadResult$Failure(new MediaUploadRepository$UploadError$ClientError(((NetworkResponse$ClientError) response).getError()));
               } else if (response instanceof NetworkResponse$NetworkError) {
                  outcome = new MediaUploadRepository$UploadResult$Failure(new MediaUploadRepository$UploadError$NetworkError(((NetworkResponse$NetworkError) response).getError()));
               } else if (response instanceof NetworkResponse$ServerError) {
                  outcome = new MediaUploadRepository$UploadResult$Failure(new MediaUploadRepository$UploadError$ServerError(((NetworkResponse$ServerError) response).getCode()));
               } else {
                  throw new NoWhenBranchMatchedException();
               }
            }
         } catch (Exception failure) {
            outcome = new MediaUploadRepository$UploadResult$Failure(new MediaUploadRepository$UploadError$UnexpectedError(failure));
         }
      } catch (Throwable failure) {
         if (compressed != null) {
            MediaCompressionKt.deleteCompressedMedia(compressed.getUri());
         }
         throw failure;
      }
      // Cleanup failures propagate directly rather than being converted into upload errors.
      if (compressed != null) {
         MediaCompressionKt.deleteCompressedMedia(compressed.getUri());
      }
      return outcome;
   }

   // $VF: synthetic method
   public static Object uploadSingleMediaItem$default(MediaUploadRepository var0, MediaData$Media var1, int var2, Continuation var3, int var4, Object var5) {
      if ((var4 & 2) != 0) {
         var2 = -1;
      }

      return var0.uploadSingleMediaItem(var1, var2, var3);
   }

   private final Block$Builder buildBlockForMedia(MediaData$Media var1, String var2, long var3) {
      MediaData$Media var5 = var1;
      Block$Builder var10000;
      if (var5 instanceof MediaData$Media$Image) {
         Block$Builder var6 = new Block$Builder()
            .withUrl(var2)
            .withWidth(((MediaData$Media$Image)var1).getWidth())
            .withHeight(((MediaData$Media$Image)var1).getHeight())
            .withType(BlockType.IMAGE.getSerializedName());
         Intrinsics.checkNotNull(var6);
         var10000 = var6;
      } else {
         if (!(var5 instanceof MediaData$Media$Video) && !(var5 instanceof MediaData$Media$Other)) {
            throw new NoWhenBranchMatchedException();
         }

         BlockAttachment$Companion var8 = BlockAttachment.Companion;
         String var9 = var1.getFileName();
         long var10 = var1.getSize();
         String var12 = var1.getMimeType();
         BlockAttachment var7 = BlockAttachment$Companion.create$default(var8, var9, var2, var12, null, var10, var3, 8, null);
         Block$Builder var13 = new Block$Builder().withAttachments(CollectionsKt.listOf(var7)).withType(BlockType.ATTACHMENTLIST.getSerializedName());
         Intrinsics.checkNotNull(var13);
         var10000 = var13;
      }

      return var10000;
   }

   private static final AttachmentSettings _init_$lambda$0() {
      return ((AppConfig)Injector.get().getAppConfigProvider().get()).getAttachmentSettings();
   }
}
