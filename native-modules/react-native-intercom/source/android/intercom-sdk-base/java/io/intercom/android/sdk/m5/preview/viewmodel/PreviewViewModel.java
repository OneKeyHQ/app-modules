package io.intercom.android.sdk.m5.preview.viewmodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore.Downloads;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Images.Media;
import android.webkit.URLUtil;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.compose.runtime.internal.StabilityInferred;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import io.intercom.android.sdk.m5.preview.data.DeleteType;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.DeleteType$Delete;
import io.intercom.android.sdk.m5.preview.data.DeleteType$None;
import io.intercom.android.sdk.m5.preview.data.DeleteType$Remove;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile;
import io.intercom.android.sdk.m5.preview.model.PreviewUiState;
import io.intercom.android.sdk.m5.preview.util.MimeTypeResolverKt;
import io.intercom.android.sdk.ui.extension.ContentTypeExtensionKt;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000x\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\b\u0007\u0018\u0000 72\u00020\u0001:\u00017B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012H\u0000¢\u0006\u0002\b\u0013J\u001d\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u0016H\u0000¢\u0006\u0002\b\u0017J\u0018\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0018\u0010\u001a\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0018\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J(\u0010\u001c\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010 \u001a\u00020\u001eH\u0002J \u0010!\u001a\u00020\u001e2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\"\u001a\u00020#2\u0006\u0010\u001f\u001a\u00020\u001eH\u0002J(\u0010$\u001a\u00020\u001e2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010%\u001a\u00020&2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0003J \u0010'\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0003J \u0010(\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0003J \u0010)\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0015\u001a\u00020\u0016H\u0003J\u0018\u0010*\u001a\u00020\u00102\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010+\u001a\u00020,H\u0002J\u0010\u0010-\u001a\u0004\u0018\u00010\u001e*\u0004\u0018\u00010\u001eH\u0002J\u0010\u0010.\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010/\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0015\u00100\u001a\u00020\u00102\u0006\u00101\u001a\u000202H\u0000¢\u0006\u0002\b3J\u0010\u00104\u001a\u0002052\u0006\u00106\u001a\u000202H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\fX\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e¨\u00068",
   d2 = {
         "Lio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel;",
         "Landroidx/lifecycle/ViewModel;",
         "previewArgs",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewArgs;)V",
         "downloadJob",
         "Lkotlinx/coroutines/Job;",
         "_state",
         "Lkotlinx/coroutines/flow/MutableStateFlow;",
         "Lio/intercom/android/sdk/m5/preview/model/PreviewUiState;",
         "state",
         "Lkotlinx/coroutines/flow/StateFlow;",
         "getState$intercom_sdk_base_release",
         "()Lkotlinx/coroutines/flow/StateFlow;",
         "onDeleteClicked",
         "",
         "file",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile;",
         "onDeleteClicked$intercom_sdk_base_release",
         "saveFile",
         "context",
         "Landroid/content/Context;",
         "saveFile$intercom_sdk_base_release",
         "saveImage",
         "Lio/intercom/android/sdk/m5/preview/data/IntercomPreviewFile$NetworkFile;",
         "saveVideo",
         "saveDocument",
         "saveFileLegacy",
         "url",
         "",
         "mimeType",
         "directory",
         "getNonExistingFileNameFromUrlLegacy",
         "fileDirectory",
         "Ljava/io/File;",
         "getNonExistingFileNameFromUrl",
         "contentUri",
         "Landroid/net/Uri;",
         "saveImageOnApi29Above",
         "saveVideoOnApi29Above",
         "saveFileOnApi29Above",
         "saveFileToStream",
         "outputStream",
         "Ljava/io/OutputStream;",
         "toImageMimeType",
         "showFileSaveSuccess",
         "showFileSaveFailed",
         "onPageNavigated",
         "pageNo",
         "",
         "onPageNavigated$intercom_sdk_base_release",
         "showDeleteAction",
         "",
         "size",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nPreviewViewModel.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreviewViewModel.kt\nio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel\n+ 2 StateFlow.kt\nkotlinx/coroutines/flow/StateFlowKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,398:1\n230#2,5:399\n230#2,5:405\n1#3:404\n*S KotlinDebug\n*F\n+ 1 PreviewViewModel.kt\nio/intercom/android/sdk/m5/preview/viewmodel/PreviewViewModel\n*L\n67#1:399,5\n371#1:405,5\n*E\n"
)
public final class PreviewViewModel extends ViewModel {
   @NotNull
   public static final PreviewViewModel$Companion Companion = new PreviewViewModel$Companion(null);
   @NotNull
   private final IntercomPreviewArgs previewArgs;
   @Nullable
   private Job downloadJob;
   @NotNull
   private final MutableStateFlow _state;
   @NotNull
   private final StateFlow state;
   public static final int $stable;

   static { $stable = 8; }

   public PreviewViewModel(@NotNull IntercomPreviewArgs var1) {
      Intrinsics.checkNotNullParameter(var1, "previewArgs");
      super();
      this.previewArgs = var1;
      this._state = StateFlowKt.MutableStateFlow(new PreviewUiState(null, 0, false, false, null, false, null, null, null, 511, null));
      this.state = this._state;
      IntercomPreviewArgs var2 = this.previewArgs;
      MutableStateFlow var4 = this._state;
      int var3 = 0;
      var4.setValue(
         new PreviewUiState(
            var2.getUris(),
            var2.getUris().isEmpty() ? 0 : RangesKt.coerceIn(var2.getInitialIndex(), 0, var2.getUris().size() - 1),
            this.showDeleteAction(var2.getUris().size()),
            this.previewArgs.getShowSendCTA(),
            var2.getCtaText(),
            var2.getDownloadState().getShowDownloadAction(),
            var2.getDownloadState().getFileSavingText(),
            var2.getDownloadState().getFileSavedText(),
            var2.getDownloadState().getSaveFailedText()
         )
      );
   }

   @NotNull
   public final StateFlow getState$intercom_sdk_base_release() {
      return this.state;
   }

   public final void onDeleteClicked$intercom_sdk_base_release(@NotNull IntercomPreviewFile var1) {
      Intrinsics.checkNotNullParameter(var1, "file");
      MutableStateFlow var2 = this._state;
      int var3 = 0;

      Object var4;
      Object var8;
      do {
         var4 = var2.getValue();
         PreviewUiState var5 = (PreviewUiState)var4;
         int var6 = 0;
         List var7 = CollectionsKt.minus(var5.getFiles(), var1);
         var8 = PreviewUiState.copy$default(var5, var7, 0, this.showDeleteAction(var7.size()), false, null, false, null, null, null, 506, null);
      } while (!var2.compareAndSet(var4, var8));
   }

   public final void saveFile$intercom_sdk_base_release(@NotNull IntercomPreviewFile var1, @NotNull Context var2) {
      Intrinsics.checkNotNullParameter(var1, "file");
      Intrinsics.checkNotNullParameter(var2, "context");
      boolean var3 = this.downloadJob != null ? this.downloadJob.isActive() : false;
      if (var1 instanceof io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile && !var3) {
         Toast.makeText(var2, ((PreviewUiState)this._state.getValue()).getFileSavingText(), 0).show();
         String var4 = var1.getMimeType(var2);
         if (ContentTypeExtensionKt.isImage(var4)) {
            this.saveImage((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile)var1, var2);
         } else if (ContentTypeExtensionKt.isVideo(var4)) {
            this.saveVideo((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile)var1, var2);
         } else {
            this.saveDocument((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile)var1, var2);
         }
      }
   }

   private final void saveImage(io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile var1, Context var2) {
      this.downloadJob = BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), Dispatchers.getIO(), null, new PreviewViewModel$saveImage$1(this, var1, var2, null), 2, null
      );
   }

   private final void saveVideo(io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile var1, Context var2) {
      this.downloadJob = BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), Dispatchers.getIO(), null, new PreviewViewModel$saveVideo$1(this, var1, var2, null), 2, null
      );
   }

   private final void saveDocument(io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile var1, Context var2) {
      this.downloadJob = BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), Dispatchers.getIO(), null, new PreviewViewModel$saveDocument$1(this, var1, var2, null), 2, null
      );
   }

   private final void saveFileLegacy(String var1, String var2, Context var3, String var4) {
      File var5 = null;

      try {
         File var6 = Environment.getExternalStoragePublicDirectory(var4);
         var6.mkdir();
         Intrinsics.checkNotNull(var6);
         File var8 = var6;
         String var7 = this.getNonExistingFileNameFromUrlLegacy(var1, var8, var2);
         var5 = new File(var6, var7);
         this.saveFileToStream(var1, new FileOutputStream(var5));
         this.showFileSaveSuccess(var3);
      } catch (Exception var9) {
         var9.printStackTrace();
         if (var5 != null) {
            var5.delete();
         }

         this.showFileSaveFailed(var3);
      }
   }

   private final String getNonExistingFileNameFromUrlLegacy(String var1, File var2, String var3) {
      String var4 = URLUtil.guessFileName(var1, null, var3);
      Intrinsics.checkNotNull(var4);
      CharSequence var10000 = var4;
      String[] var6 = new String[]{"."};
      List var7 = StringsKt.split$default(var10000, var6, false, 2, 2, null);
      int var8 = 0;
      String var21 = (String)CollectionsKt.getOrNull(var7, 0);
      if (var21 == null) {
         var21 = "fileName";
      }

      Pair var5 = TuplesKt.to(var21, CollectionsKt.getOrNull(var7, 1));
      String var15 = (String)var5.component1();
      String var16 = (String)var5.component2();
      String var17 = var4;
      int var9 = 0;

      while (true) {
         boolean itx = new File(var2, var17).exists();
         int var12 = 0;
         var9++;
         if (!itx) {
            String var19 = var17;
            Intrinsics.checkNotNull(var19);
            return var19;
         }

         StringBuilder var18 = new StringBuilder();
         StringBuilder var11 = var18;
         int var20 = 0;
         var11.append(var15 + " (" + var9 + ')');
         if (var16 != null) {
            String itxx = var16;
            int var14 = 0;
            var11.append('.' + itxx);
         }

         var17 = var18.toString();
      }
   }

   @RequiresApi(29)
   private final String getNonExistingFileNameFromUrl(String var1, Uri var2, String var3, Context var4) {
      String var5 = URLUtil.guessFileName(var1, null, var3);
      Intrinsics.checkNotNull(var5);
      CharSequence var10000 = var5;
      String[] var7 = new String[]{"."};
      List var8 = StringsKt.split$default(var10000, var7, false, 2, 2, null);
      int var9 = 0;
      String var39 = (String)CollectionsKt.getOrNull(var8, 0);
      if (var39 == null) {
         var39 = "fileName";
      }

      Pair var6 = TuplesKt.to(var39, CollectionsKt.getOrNull(var8, 1));
      String var24 = (String)var6.component1();
      String var25 = (String)var6.component2();
      String[] var10 = new String[]{"_display_name"};
      String[] var26 = var10;
      String var27 = "_display_name LIKE ? AND _display_name LIKE ?";
      String[] var12 = new String[]{var24 + '%', "%." + var25};
      String[] var11 = var12;
      List var28 = new ArrayList();
      Cursor var13 = var4.getContentResolver().query(var2, var26, var27, var11, null);
      if (var13 != null) {
         var var14 = var13;
         Throwable var15 = null;

         try {
            Cursor var16 = (Cursor)var14;
            int var17 = 0;
            int var18 = var16.getColumnIndexOrThrow("_display_name");

            while (var16.moveToNext()) {
               String var10001 = var16.getString(var18);
               Intrinsics.checkNotNullExpressionValue(var10001, "getString(...)");
               var28.add(var10001);
            }

            Unit var34 = Unit.INSTANCE;
         } catch (Throwable var22) {
            var15 = var22;
            throw var22;
         } finally {
            CloseableKt.closeFinally(var14, var15);
         }
      }

      String var29 = var5;
      int var30 = 0;

      while (true) {
         boolean itx = var28.contains(var29);
         int var36 = 0;
         var30++;
         if (!itx) {
            String var33 = var29;
            Intrinsics.checkNotNull(var33);
            return var33;
         }

         StringBuilder var32 = new StringBuilder();
         StringBuilder var35 = var32;
         int var37 = 0;
         var35.append(var24 + " (" + var30 + ')');
         if (var25 != null) {
            String itxx = var25;
            int var19 = 0;
            var35.append('.' + itxx);
         }

         var29 = var32.toString();
      }
   }

   @RequiresApi(29)
   private final void saveImageOnApi29Above(String var1, String var2, Context var3) {
      Object var4 = null;

      try {
         URLConnection var5 = new URL(var1).openConnection();
         var var6 = var5.getInputStream();
         Throwable var7 = null;

         try {
            InputStream var35 = var6;
            int var37 = 0;
            String var43 = this.toImageMimeType(var5.getContentType());
            if (var43 == null) {
               var43 = MimeTypeResolverKt.resolveImageMimeType(var1, var2);
            }

            String var10 = var43;
            Uri var44 = Media.EXTERNAL_CONTENT_URI;
            Intrinsics.checkNotNullExpressionValue(Media.EXTERNAL_CONTENT_URI, "EXTERNAL_CONTENT_URI");
            Uri var11 = var44;
            String var12 = this.getNonExistingFileNameFromUrl(var1, var11, var10, var3);
            ContentValues var13 = new ContentValues();
            ContentValues var14 = var13;
            int var15 = 0;
            var14.put("_display_name", var12);
            var14.put("mime_type", var10);
            var14.put("relative_path", Environment.DIRECTORY_PICTURES);
            var14.put("is_pending", 1);
            ContentValues var38 = var13;
            Uri var45 = var3.getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, var38);
            if (var45 == null) {
               throw new IOException("MediaStore insert returned null");
            }

            Uri var39 = var45;
            var4 = var39;
            OutputStream var46 = var3.getContentResolver().openOutputStream(var39);
            if (var46 == null) {
               throw new IOException("MediaStore output stream is null");
            }

            OutputStream var40 = var46;
            var var41 = var40;
            Throwable var16 = null;

            try {
               OutputStream var17 = var41;
               int var19 = 0;
               Intrinsics.checkNotNull(var35);
               long var42 = ByteStreamsKt.copyTo$default(var35, var17, 0, 2, null);
            } catch (Throwable var30) {
               var16 = var30;
               throw var30;
            } finally {
               CloseableKt.closeFinally(var41, var16);
            }

            var38.clear();
            var38.put("is_pending", 0);
            var3.getContentResolver().update(var39, var38, null, null);
            this.showFileSaveSuccess(var3);
            Unit var36 = Unit.INSTANCE;
         } catch (Throwable var32) {
            var7 = var32;
            throw var32;
         } finally {
            CloseableKt.closeFinally(var6, var7);
         }
      } catch (Exception var34) {
         var34.printStackTrace();
         Uri var10000 = (Uri)var4;
         if ((Uri)var4 != null) {
            Uri var8 = var10000;
            int var9 = 0;
            var3.getContentResolver().delete(var8, null, null);
         }

         this.showFileSaveFailed(var3);
      }
   }

   @RequiresApi(29)
   private final void saveVideoOnApi29Above(String var1, String var2, Context var3) {
      try {
         String var4 = MimeTypeResolverKt.resolveVideoMimeType(var1, var2);
         Uri var10000 = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
         Intrinsics.checkNotNullExpressionValue(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "EXTERNAL_CONTENT_URI");
         Uri var6 = var10000;
         String var5 = this.getNonExistingFileNameFromUrl(var1, var6, var4, var3);
         ContentValues var7 = new ContentValues();
         ContentValues var8 = var7;
         int var9 = 0;
         var8.put("_display_name", var5);
         var8.put("mime_type", var4);
         var8.put("relative_path", Environment.DIRECTORY_MOVIES);
         var8.put("is_pending", 1);
         ContentValues var17 = var7;
         ContentResolver var18 = var3.getContentResolver();
         int var19 = 0;
         Uri var10 = var18.insert(android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI, var17);
         if (var10 != null) {
            Uri var11 = var10;
            int var12 = 0;
            ParcelFileDescriptor var20 = var18.openFileDescriptor(var11, "w");
            if (var20 != null) {
               ParcelFileDescriptor var13 = var20;
               int var14 = 0;
               FileOutputStream var15 = new FileOutputStream(var13.getFileDescriptor());
               this.saveFileToStream(var1, var15);
               var13.close();
               var17.clear();
               var17.put("is_pending", 0);
               var18.update(var11, var17, null, null);
               this.showFileSaveSuccess(var3);
            }
         }
      } catch (Exception var16) {
         var16.printStackTrace();
         this.showFileSaveFailed(var3);
      }
   }

   @RequiresApi(29)
   private final void saveFileOnApi29Above(String var1, String var2, Context var3) {
      try {
         String var4 = MimeTypeResolverKt.resolveFileMimeType(var1, var2);
         Uri var10000 = Downloads.EXTERNAL_CONTENT_URI;
         Intrinsics.checkNotNullExpressionValue(Downloads.EXTERNAL_CONTENT_URI, "EXTERNAL_CONTENT_URI");
         Uri var6 = var10000;
         String var5 = this.getNonExistingFileNameFromUrl(var1, var6, var4, var3);
         ContentValues var7 = new ContentValues();
         ContentValues var8 = var7;
         int var9 = 0;
         var8.put("_display_name", var5);
         var8.put("mime_type", var4);
         var8.put("relative_path", Environment.DIRECTORY_DOWNLOADS);
         ContentValues var17 = var7;
         ContentResolver var18 = var3.getContentResolver();
         int var19 = 0;
         Uri var10 = var18.insert(Files.getContentUri("external"), var17);
         if (var10 != null) {
            Uri var11 = var10;
            int var12 = 0;
            ParcelFileDescriptor var20 = var18.openFileDescriptor(var11, "w");
            if (var20 != null) {
               ParcelFileDescriptor var13 = var20;
               int var14 = 0;
               FileOutputStream var15 = new FileOutputStream(var13.getFileDescriptor());
               this.saveFileToStream(var1, var15);
               var13.close();
               var17.clear();
               var18.update(var11, var17, null, null);
               this.showFileSaveSuccess(var3);
            }
         }
      } catch (Exception var16) {
         var16.printStackTrace();
         this.showFileSaveFailed(var3);
      }
   }

   private final void saveFileToStream(String var1, OutputStream var2) throws IOException {
      var var3 = var2;
      Throwable var4 = null;

      try {
         OutputStream var5 = var3;
         int var7 = 0;
         Closeable var8 = new URL(var1).openConnection().getInputStream();
         Throwable var9 = null;

         try {
            InputStream var10 = (InputStream)var8;
            int var12 = 0;
            Intrinsics.checkNotNull(var10);
            long var25 = ByteStreamsKt.copyTo$default(var10, var5, 0, 2, null);
         } catch (Throwable var21) {
            var9 = var21;
            throw var21;
         } finally {
            CloseableKt.closeFinally(var8, var9);
         }
      } catch (Throwable var23) {
         var4 = var23;
         throw var23;
      } finally {
         CloseableKt.closeFinally(var3, var4);
      }
   }

   private final String toImageMimeType(String var1) {
      if (var1 != null) {
         String var2 = StringsKt.substringBefore$default(var1, ';', null, 2, null);
         if (var2 != null) {
            String var3 = StringsKt.trim(var2).toString();
            if (var3 != null) {
               String var10000 = var3.toLowerCase(Locale.ROOT);
               Intrinsics.checkNotNullExpressionValue(var10000, "toLowerCase(...)");
               String var4 = var10000;
               if (var4 != null) {
                  String var5 = var4;
                  String var6 = var5;
                  int var7 = 0;
                  return StringsKt.startsWith$default(var6, "image/", false, 2, null) && !StringsKt.contains$default(var6, '*', false, 2, null) ? var5 : null;
               }
            }
         }
      }

      return null;
   }

   private final void showFileSaveSuccess(Context var1) {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), Dispatchers.getMain(), null, new PreviewViewModel$showFileSaveSuccess$1(var1, this, null), 2, null
      );
   }

   private final void showFileSaveFailed(Context var1) {
      BuildersKt.launch$default(
         ViewModelKt.getViewModelScope(this), Dispatchers.getMain(), null, new PreviewViewModel$showFileSaveFailed$1(var1, this, null), 2, null
      );
   }

   public final void onPageNavigated$intercom_sdk_base_release(int var1) {
      MutableStateFlow var2 = this._state;
      int var3 = 0;

      Object var4;
      Object var7;
      do {
         var4 = var2.getValue();
         PreviewUiState var5 = (PreviewUiState)var4;
         int var6 = 0;
         var7 = PreviewUiState.copy$default(var5, null, var1, false, false, null, false, null, null, null, 509, null);
      } while (!var2.compareAndSet(var4, var7));
   }

   private final boolean showDeleteAction(int var1) {
      DeleteType var2 = this.previewArgs.getDeleteType();
      boolean var10000;
      if (Intrinsics.areEqual(var2, io.intercom.android.sdk.m5.preview.data.DeleteType$None.INSTANCE)) {
         var10000 = false;
      } else if (Intrinsics.areEqual(var2, io.intercom.android.sdk.m5.preview.data.DeleteType$Delete.INSTANCE)) {
         var10000 = true;
      } else {
         if (!Intrinsics.areEqual(var2, io.intercom.android.sdk.m5.preview.data.DeleteType$Remove.INSTANCE)) {
            throw new NoWhenBranchMatchedException();
         }

         var10000 = var1 > 1;
      }

      return var10000;
   }

   // $VF: synthetic method
   public static final void access$saveImageOnApi29Above(PreviewViewModel var0, String var1, String var2, Context var3) {
      var0.saveImageOnApi29Above(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$saveFileLegacy(PreviewViewModel var0, String var1, String var2, Context var3, String var4) {
      var0.saveFileLegacy(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static final void access$saveVideoOnApi29Above(PreviewViewModel var0, String var1, String var2, Context var3) {
      var0.saveVideoOnApi29Above(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final void access$saveFileOnApi29Above(PreviewViewModel var0, String var1, String var2, Context var3) {
      var0.saveFileOnApi29Above(var1, var2, var3);
   }

   // $VF: synthetic method
   public static final MutableStateFlow access$get_state$p(PreviewViewModel var0) {
      return var0._state;
   }
}
