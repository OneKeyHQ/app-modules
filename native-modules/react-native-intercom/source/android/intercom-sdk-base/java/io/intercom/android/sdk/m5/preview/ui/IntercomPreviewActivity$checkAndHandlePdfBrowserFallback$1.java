package io.intercom.android.sdk.m5.preview.ui;

import android.content.Context;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile;
import io.intercom.android.sdk.m5.preview.util.PdfUtilsKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(
   f = "IntercomPreviewActivity.kt",
   l = 90,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final IntercomPreviewFile $file;
   // $VF: synthetic field
   final IntercomPreviewActivity this$0;

   IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1(IntercomPreviewFile var1, IntercomPreviewActivity var2, Continuation var3) {
      super(2, var3);
      this.$file = var1;
      this.this$0 = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      boolean var6;
      label29: {
         Object var4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
         Object var10000;
         switch (this.label) {
            case 0:
               ResultKt.throwOnFailure(var1);
               IntercomPreviewFile var3 = this.$file;
               if (var3 instanceof IntercomPreviewFile$LocalFile) {
                  var6 = false;
                  break label29;
               }

               if (!(var3 instanceof IntercomPreviewFile$NetworkFile)) {
                  throw new NoWhenBranchMatchedException();
               }

               Context var5 = this.this$0;
               IntercomPreviewFile var10001 = this.$file;
               Continuation var10002 = this;
               this.label = 1;
               var10000 = PdfUtilsKt.shouldOpenPdfInBrowser(var5, var10001, var10002);
               if (var10000 == var4) {
                  return var4;
               }
               break;
            case 1:
               ResultKt.throwOnFailure(var1);
               var10000 = var1;
               break;
            default:
               throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
         }

         var6 = (Boolean)var10000;
      }

      boolean var2 = var6;
      if (var2) {
         IntercomPreviewActivity.access$openPdfInBrowser(this.this$0, this.$file);
         IntercomPreviewActivity.access$finishWithResult(this.this$0, 0, CollectionsKt.emptyList());
      } else {
         IntercomPreviewActivity.access$setupPreviewUI(this.this$0);
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1(this.$file, this.this$0, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPreviewActivity$checkAndHandlePdfBrowserFallback$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
