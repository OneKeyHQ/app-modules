package io.intercom.android.sdk.m5.preview.viewmodel;

import android.content.Context;
import android.os.Environment;
import android.os.Build.VERSION;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "PreviewViewModel.kt",
   l = {},
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.viewmodel.PreviewViewModel$saveImage$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PreviewViewModel$saveImage$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final PreviewViewModel this$0;
   // $VF: synthetic field
   final IntercomPreviewFile$NetworkFile $file;
   // $VF: synthetic field
   final Context $context;

   PreviewViewModel$saveImage$1(PreviewViewModel var1, IntercomPreviewFile$NetworkFile var2, Context var3, Continuation var4) {
      super(2, var4);
      this.this$0 = var1;
      this.$file = var2;
      this.$context = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            if (VERSION.SDK_INT >= 29) {
               PreviewViewModel.access$saveImageOnApi29Above(this.this$0, this.$file.getUrl(), this.$file.getMimeType(), this.$context);
            } else {
               PreviewViewModel var10000 = this.this$0;
               String var10001 = this.$file.getUrl();
               String var10002 = this.$file.getMimeType();
               Context var10003 = this.$context;
               String var10004 = Environment.DIRECTORY_DOWNLOADS;
               Intrinsics.checkNotNullExpressionValue(Environment.DIRECTORY_DOWNLOADS, "DIRECTORY_DOWNLOADS");
               PreviewViewModel.access$saveFileLegacy(var10000, var10001, var10002, var10003, var10004);
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new PreviewViewModel$saveImage$1(this.this$0, this.$file, this.$context, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PreviewViewModel$saveImage$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
