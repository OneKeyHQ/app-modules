package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Uploading;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.Job.DefaultImpls;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0001\u0018\u0000 %2\u00020\u0001:\u0002$%B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J&\u0010\u0014\u001a\u00020\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\b2\u0006\u0010\u0018\u001a\u00020\u00192\b\b\u0002\u0010\u0012\u001a\u00020\u0013J\u0016\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u0019J\u000e\u0010\u001d\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u0010J\u0006\u0010\u001e\u001a\u00020\u001bJ \u0010\u001f\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0012\u001a\u00020\u0013H\u0002J\u0018\u0010!\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00102\u0006\u0010\"\u001a\u00020#H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001d\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\b0\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001a\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006&",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository;",
         "",
         "mediaUploadRepository",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/data/MediaUploadRepository;)V",
         "_uploadStates",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem;",
         "uploadStates",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getUploadStates",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "uploadJobs",
         "",
         "",
         "Lkotlinx/coroutines/Job;",
         "transparencyBackgroundColor",
         "",
         "addAndStartUploading",
         "Lio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository$AddResult;",
         "mediaItems",
         "Lio/intercom/android/sdk/m5/upload/data/MediaData$Media;",
         "scope",
         "Lkotlinx/coroutines/CoroutineScope;",
         "retryUpload",
         "",
         "instanceId",
         "removeMediaItem",
         "cancelAllUploads",
         "startUploadJob",
         "uploadItem",
         "updateItemState",
         "newState",
         "Lio/intercom/android/sdk/m5/conversation/states/MediaUploadItem$UploadState;",
         "AddResult",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nMediaUploadStateRepository.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaUploadStateRepository.kt\nio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,174:1\n230#2,3:175\n233#2,2:182\n230#2,3:187\n233#2,2:193\n230#2,3:197\n233#2,2:204\n1563#3:178\n1634#3,3:179\n1869#3,2:184\n827#3:190\n855#3,2:191\n1869#3,2:195\n1563#3:200\n1634#3,3:201\n1#4:186\n*S KotlinDebug\n*F\n+ 1 MediaUploadStateRepository.kt\nio/intercom/android/sdk/m5/conversation/data/MediaUploadStateRepository\n*L\n61#1:175,3\n61#1:182,2\n107#1:187,3\n107#1:193,2\n152#1:197,3\n152#1:204,2\n71#1:178\n71#1:179,3\n81#1:184,2\n108#1:190\n108#1:191,2\n113#1:195,2\n153#1:200\n153#1:201,3\n*E\n"
)
public final class MediaUploadStateRepository {
   @NotNull
   public static final MediaUploadStateRepository$Companion Companion = new MediaUploadStateRepository$Companion(null);
   @NotNull
   private final MediaUploadRepository mediaUploadRepository;
   @NotNull
   private final MutableStateFlow _uploadStates;
   @NotNull
   private final StateFlow uploadStates;
   @NotNull
   private final Map uploadJobs;
   private int transparencyBackgroundColor;
   public static final int $stable;

   static { $stable = 8; }
   public static final int MAX_MEDIA_ITEMS = 10;

   public MediaUploadStateRepository(@NotNull MediaUploadRepository var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "mediaUploadRepository");
      this.mediaUploadRepository = var1;
      this._uploadStates = StateFlowKt.MutableStateFlow(CollectionsKt.emptyList());
      this.uploadStates = FlowKt.asStateFlow(this._uploadStates);
      this.uploadJobs = new ConcurrentHashMap();
      this.transparencyBackgroundColor = -1;
   }

   @NotNull
   public final StateFlow getUploadStates() {
      return this.uploadStates;
   }

   @NotNull
   public final MediaUploadStateRepository$AddResult addAndStartUploading(@NotNull List var1, @NotNull CoroutineScope var2, int var3) {
      Intrinsics.checkNotNullParameter(var1, "mediaItems");
      Intrinsics.checkNotNullParameter(var2, "scope");
      this.transparencyBackgroundColor = var3;
      ObjectRef var4 = new ObjectRef();
      var4.element = CollectionsKt.emptyList();
      int var5 = 0;
      MutableStateFlow var6 = this._uploadStates;
      int var7 = 0;

      Object var8;
      Object var24;
      do {
         var8 = var6.getValue();
         List var9 = (List)var8;
         int var10 = 0;
         int var11 = 10 - var9.size();
         List var12 = var11 <= 0 ? CollectionsKt.emptyList() : CollectionsKt.take(var1, var11);
         var5 = var1.size() - var12.size();
         Iterable var13 = var12;
         ObjectRef var14 = var4;
         int var15 = 0;
         Iterable var16 = var13;
         ArrayList var17 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var13, 10));
         int var18 = 0;

         for (Object var20 : var16) {
            MediaData$Media var21 = (MediaData$Media)var20;
            Collection var22 = var17;
            int var23 = 0;
            var22.add(new MediaUploadItem(null, var21, MediaUploadItem$UploadState$Uploading.INSTANCE, 1, null));
         }

         var14.element = (List)var17;
         var24 = CollectionsKt.plus(var9, (Iterable)var4.element);
      } while (!var6.compareAndSet(var8, var24));

      Iterable var26 = (Iterable)var4.element;
      var7 = 0;

      for (Object var29 : var26) {
         MediaUploadItem var30 = (MediaUploadItem)var29;
         int var31 = 0;
         this.startUploadJob(var30, var2, var3);
      }

      return new MediaUploadStateRepository$AddResult(((List)var4.element).size(), var5);
   }

   // $VF: synthetic method
   public static MediaUploadStateRepository$AddResult addAndStartUploading$default(
      MediaUploadStateRepository var0, List var1, CoroutineScope var2, int var3, int var4, Object var5
   ) {
      if ((var4 & 4) != 0) {
         var3 = -1;
      }

      return var0.addAndStartUploading(var1, var2, var3);
   }

   public final void retryUpload(@NotNull String var1, @NotNull CoroutineScope var2) {
      Intrinsics.checkNotNullParameter(var1, "instanceId");
      Intrinsics.checkNotNullParameter(var2, "scope");
      Job var10000 = (Job)this.uploadJobs.get(var1);
      if (var10000 != null) {
         DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
      }

      this.uploadJobs.remove(var1);
      Iterable var4 = (Iterable)this._uploadStates.getValue();
      Iterator var5 = var4.iterator();
      Object var9;

      while (true) {
         if (var5.hasNext()) {
            Object var6 = var5.next();
            MediaUploadItem var7 = (MediaUploadItem)var6;
            int var8 = 0;
            if (!Intrinsics.areEqual(var7.getInstanceId(), var1)) {
               continue;
            }

            var9 = var6;
            break;
         }

         var9 = null;
         break;
      }

      MediaUploadItem var10 = (MediaUploadItem)var9;
      if ((MediaUploadItem)var9 != null) {
         MediaUploadItem var3 = var10;
         this.updateItemState(var1, MediaUploadItem$UploadState$Uploading.INSTANCE);
         this.startUploadJob(var3, var2, this.transparencyBackgroundColor);
      }
   }

   public final void removeMediaItem(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "instanceId");
      Job var10000 = (Job)this.uploadJobs.get(var1);
      if (var10000 != null) {
         DefaultImpls.cancel$default(var10000, (CancellationException)null, 1, null);
      }

      this.uploadJobs.remove(var1);
      MutableStateFlow var2 = this._uploadStates;
      int var3 = 0;

      Object var4;
      Iterable var16;
      do {
         var4 = var2.getValue();
         var16 = (List)var4;
         int var6 = 0;
         Iterable var7 = (Iterable)var16;
         int var8 = 0;
         Iterable var9 = var7;
         ArrayList var10 = new ArrayList();
         int var11 = 0;

         for (Object var13 : var9) {
            MediaUploadItem var14 = (MediaUploadItem)var13;
            int var15 = 0;
            if (!Intrinsics.areEqual(var14.getInstanceId(), var1)) {
               var10.add(var13);
            }
         }

         var16 = (List)var10;
      } while (!var2.compareAndSet(var4, var16));
   }

   public final void cancelAllUploads() {
      Iterable var1 = this.uploadJobs.values();
      int var2 = 0;

      for (Object var4 : var1) {
         Job var5 = (Job)var4;
         int var6 = 0;
         DefaultImpls.cancel$default(var5, (CancellationException)null, 1, null);
      }

      this.uploadJobs.clear();
      this._uploadStates.setValue(CollectionsKt.emptyList());
   }

   private final void startUploadJob(MediaUploadItem var1, CoroutineScope var2, int var3) {
      Job var4 = BuildersKt.launch$default(var2, null, null, new MediaUploadStateRepository$startUploadJob$job$1(this, var1, var3, null), 3, null);
      this.uploadJobs.put(var1.getInstanceId(), var4);
   }

   private final void updateItemState(String var1, MediaUploadItem$UploadState var2) {
      MutableStateFlow var3 = this._uploadStates;
      int var4 = 0;

      Object var5;
      Object var18;
      do {
         var5 = var3.getValue();
         List var6 = (List)var5;
         int var7 = 0;
         Iterable var8 = var6;
         int var9 = 0;
         Iterable var10 = var8;
         ArrayList var11 = new ArrayList(CollectionsKt.collectionSizeOrDefault(var8, 10));
         int var12 = 0;

         for (Object var14 : var10) {
            MediaUploadItem var15 = (MediaUploadItem)var14;
            Collection var16 = var11;
            int var17 = 0;
            var16.add(Intrinsics.areEqual(var15.getInstanceId(), var1) ? MediaUploadItem.copy$default(var15, null, null, var2, 3, null) : var15);
         }

         var18 = (List)var11;
      } while (!var3.compareAndSet(var5, var18));
   }

   // $VF: synthetic method
   public static final MediaUploadRepository access$getMediaUploadRepository$p(MediaUploadStateRepository var0) {
      return var0.mediaUploadRepository;
   }

   // $VF: synthetic method
   public static final void access$updateItemState(MediaUploadStateRepository var0, String var1, MediaUploadItem$UploadState var2) {
      var0.updateItemState(var1, var2);
   }

   // $VF: synthetic method
   public static final Map access$getUploadJobs$p(MediaUploadStateRepository var0) {
      return var0.uploadJobs;
   }
}
