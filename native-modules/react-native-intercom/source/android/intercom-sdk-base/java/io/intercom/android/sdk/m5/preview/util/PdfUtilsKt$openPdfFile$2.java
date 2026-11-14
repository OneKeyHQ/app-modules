package io.intercom.android.sdk.m5.preview.util;

import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgsKt;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Error;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$PasswordProtected;
import io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Success;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref.ObjectRef;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;

@DebugMetadata(f = "PdfUtils.kt", l = {}, i = {}, s = {}, n = {}, m = "invokeSuspend", c = "io.intercom.android.sdk.m5.preview.util.PdfUtilsKt$openPdfFile$2")
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "Lio/intercom/android/sdk/m5/preview/util/PdfOpenResult;", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class PdfUtilsKt$openPdfFile$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final IntercomPreviewFile $file;
   // $VF: synthetic field
   final Context $context;

   PdfUtilsKt$openPdfFile$2(IntercomPreviewFile var1, Context var2, Continuation var3) {
      super(2, var3);
      this.$file = var1;
      this.$context = var2;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            CoroutineScope var2 = (CoroutineScope)this.L$0;
            ParcelFileDescriptor var3 = null;
            ObjectRef var4 = new ObjectRef();

            PdfOpenResult var5;
            try {
               IntercomPreviewFile var37 = this.$file;
               ParcelFileDescriptor var58;
               if (!(var37 instanceof io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile)) {
                  if (!(var37 instanceof io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile)) {
                     throw new NoWhenBranchMatchedException();
                  }

                  boolean var38 = IntercomPreviewArgsKt.isLocalContentUri(this.$file.getUri());
                  IntercomPreviewFile var40 = this.$file;
                  if (!var38) {
                     int var45 = 0;
                     String var46 = "Refusing to open non-content LocalFile (scheme=" + var40.getUri().getScheme() + ')';
                     throw new IllegalArgumentException(var46.toString());
                  }

                  var58 = this.$context.getContentResolver().openFileDescriptor(this.$file.getUri(), "r");
               } else {
                  URL var6;
                  boolean var57;
                  label278: {
                     var6 = new URL(((io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile)this.$file).getUrl());
                     if (StringsKt.equals(var6.getProtocol(), "https", true)) {
                        CharSequence var7 = var6.getHost();
                        if (var7 != null && !StringsKt.isBlank(var7)) {
                           var57 = true;
                           break label278;
                        }
                     }

                     var57 = false;
                  }

                  if (!var57) {
                     int var42 = 0;
                     String var43 = "Refusing to fetch non-https NetworkFile (scheme=" + var6.getProtocol() + ')';
                     throw new IllegalArgumentException(var43.toString());
                  }

                  var4.element = File.createTempFile("pdf_", ".pdf", this.$context.getCacheDir());
                  var var39 = var6.openConnection().getInputStream();
                  Throwable var8 = null;

                  try {
                     InputStream var9 = var39;
                     int var11 = 0;
                     FileOutputStream var12 = new FileOutputStream((File)var4.element);
                     Throwable var13 = null;

                     try {
                        FileOutputStream var14 = var12;
                        int var16 = 0;
                        Intrinsics.checkNotNull(var9);
                        long var53 = ByteStreamsKt.copyTo$default(var9, var14, 0, 2, null);
                     } catch (Throwable var31) {
                        var13 = var31;
                        throw var31;
                     } finally {
                        CloseableKt.closeFinally(var12, var13);
                     }
                  } catch (Throwable var33) {
                     var8 = var33;
                     throw var33;
                  } finally {
                     CloseableKt.closeFinally(var39, var8);
                  }

                  var58 = ParcelFileDescriptor.open((File)var4.element, 268435456);
               }

               PdfOpenResult var63;
               label291: {
                  var3 = var58;
                  if (var3 != null) {
                     ParcelFileDescriptor var41 = var3;
                     IntercomPreviewFile var44 = this.$file;
                     Context var47 = this.$context;
                     ParcelFileDescriptor var49 = var41;
                     int var50 = 0;

                     PdfOpenResult var51;
                     try {
                        PdfRenderer var52 = new PdfRenderer(var49);
                        var52.close();
                        var49.close();
                        IntercomPreviewFile var54 = var44;
                        ParcelFileDescriptor var60;
                        if (var54 instanceof io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$NetworkFile) {
                           var60 = ParcelFileDescriptor.open((File)var4.element, 268435456);
                        } else {
                           if (!(var54 instanceof io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile)) {
                              throw new NoWhenBranchMatchedException();
                           }

                           var60 = var47.getContentResolver().openFileDescriptor(var44.getUri(), "r");
                        }

                        ParcelFileDescriptor var55 = var60;
                        PdfOpenResult var61;
                        if (var55 != null) {
                           ParcelFileDescriptor var17 = var55;
                           int var18 = 0;
                           var61 = new io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Success(var17, (File)var4.element);
                        } else {
                           int var56 = 0;
                           File var62 = (File)var4.element;
                           if ((File)var4.element != null) {
                              Boxing.boxBoolean(var62.delete());
                           }

                           var61 = new io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Error(new IllegalStateException("Failed to reopen file descriptor"));
                        }

                        var51 = var61;
                     } catch (SecurityException var35) {
                        var49.close();
                        File var59 = (File)var4.element;
                        if ((File)var4.element != null) {
                           Boxing.boxBoolean(var59.delete());
                        }

                        var51 = io.intercom.android.sdk.m5.preview.util.PdfOpenResult$PasswordProtected.INSTANCE;
                     }

                     var63 = var51;
                     if (var51 != null) {
                        break label291;
                     }
                  }

                  int var48 = 0;
                  File var64 = (File)var4.element;
                  if ((File)var4.element != null) {
                     Boxing.boxBoolean(var64.delete());
                  }

                  var63 = new io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Error(new IllegalStateException("Failed to open file descriptor"));
               }

               var5 = var63;
            } catch (Exception var36) {
               if (var3 != null) {
                  var3.close();
               }

               File var10000 = (File)var4.element;
               if ((File)var4.element != null) {
                  Boxing.boxBoolean(var10000.delete());
               }

               var5 = new io.intercom.android.sdk.m5.preview.util.PdfOpenResult$Error(var36);
            }

            return var5;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      PdfUtilsKt$openPdfFile$2 var3 = new PdfUtilsKt$openPdfFile$2(this.$file, this.$context, var2);
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((PdfUtilsKt$openPdfFile$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
