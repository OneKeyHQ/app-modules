package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import androidx.compose.runtime.ProduceStateScope;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Success;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$PasswordProtected;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Error;
import io.intercom.android.sdk.m5.preview.util.PdfUtilsKt;
import java.io.File;
import java.util.List;
import kotlin.ResultKt;
import kotlin.NoWhenBranchMatchedException;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;

@DebugMetadata(
   f = "PreviewUri.kt",
   l = {293, 297},
   i = {0, 1, 1},
   s = {"L$0", "L$0", "L$1"},
   n = {"$this$produceState", "$this$produceState", "result"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.PreviewUriKt$loadPdfState$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\b\u0012\u0004\u0012\u00020\u00030\u0002H\n",
   d2 = {"<anonymous>", "", "Landroidx/compose/runtime/ProduceStateScope;", "Lio/intercom/android/sdk/m5/preview/ui/PdfLoadState;"}
)
final class PreviewUriKt$loadPdfState$1$1 extends SuspendLambda implements Function2 {
   Object L$1;
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final IntercomPreviewFile $file;

   PreviewUriKt$loadPdfState$1$1(Context var1, IntercomPreviewFile var2, Continuation var3) {
      super(2, var3);
      this.$context = var1;
      this.$file = var2;
   }

   @Override
   public final Object invokeSuspend(Object result) {
      Object suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      ProduceStateScope scope;
      PdfOpenResult opened;
      int resumeLabel = this.label;
      if (resumeLabel == 0) {
         ResultKt.throwOnFailure(result);
         scope = (ProduceStateScope) this.L$0;
         this.L$0 = scope;
         this.label = 1;
         Object value = PdfUtilsKt.openPdfFile(this.$context, this.$file, this);
         if (value == suspended) {
            return suspended;
         }
         opened = (PdfOpenResult) value;
      } else if (resumeLabel == 1) {
         scope = (ProduceStateScope) this.L$0;
         // Opening errors propagate without entering the rendering cleanup region.
         ResultKt.throwOnFailure(result);
         opened = (PdfOpenResult) result;
      } else if (resumeLabel == 2) {
         opened = (PdfOpenResult) this.L$1;
         scope = (ProduceStateScope) this.L$0;
      } else {
         throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
      if (resumeLabel == 2 || opened instanceof PdfOpenResult$Success) {
         try {
            try {
               Object pages;
               if (resumeLabel == 2) {
                  ResultKt.throwOnFailure(result);
                  pages = result;
               } else {
                  android.os.ParcelFileDescriptor descriptor = ((PdfOpenResult$Success) opened).getFileDescriptor();
                  this.L$0 = scope;
                  this.L$1 = opened;
                  this.label = 2;
                  pages = PdfUtilsKt.renderPdfPages(descriptor, this);
                  // The descriptor remains open while rendering is suspended.
                  if (pages == suspended) {
                     return suspended;
                  }
               }
               scope.setValue(new PdfLoadState$Success((List) pages));
            } catch (Exception renderingFailure) {
               scope.setValue(PdfLoadState$Error.INSTANCE);
            }
         } catch (Throwable failure) {
            // Cleanup errors replace the pending failure, as in the original exception table.
            ((PdfOpenResult$Success) opened).getFileDescriptor().close();
            File temporary = ((PdfOpenResult$Success) opened).getTempFile();
            if (temporary != null) {
               Boxing.boxBoolean(temporary.delete());
            }
            throw failure;
         }
         // close() is outside the protected rendering region; failure skips deletion.
         ((PdfOpenResult$Success) opened).getFileDescriptor().close();
         File temporary = ((PdfOpenResult$Success) opened).getTempFile();
         if (temporary != null) {
            Boxing.boxBoolean(temporary.delete());
         }
      } else if (opened instanceof PdfOpenResult$PasswordProtected || opened instanceof PdfOpenResult$Error) {
         scope.setValue(PdfLoadState$Error.INSTANCE);
      } else {
         throw new NoWhenBranchMatchedException();
      }
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      PreviewUriKt$loadPdfState$1$1 var3 = new PreviewUriKt$loadPdfState$1$1(this.$context, this.$file, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(ProduceStateScope var1, Continuation var2) {
      return ((PreviewUriKt$loadPdfState$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((ProduceStateScope)var1, (Continuation)var2);
   }
}
