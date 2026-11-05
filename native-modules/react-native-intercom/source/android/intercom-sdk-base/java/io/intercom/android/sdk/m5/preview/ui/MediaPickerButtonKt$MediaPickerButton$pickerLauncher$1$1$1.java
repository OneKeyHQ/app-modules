package io.intercom.android.sdk.m5.preview.ui;

import android.net.Uri;
import androidx.activity.compose.ManagedActivityResultLauncher;
import io.intercom.android.sdk.m5.preview.data.DeleteType$Remove;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewArgs;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$Companion;
import io.intercom.android.sdk.m5.preview.data.IntercomPreviewFile$LocalFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.DelayKt;

@DebugMetadata(
   f = "MediaPickerButton.kt",
   l = 80,
   i = {},
   s = {},
   n = {},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
@SourceDebugExtension(
   "SMAP\nMediaPickerButton.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,258:1\n1617#2,9:259\n1869#2:268\n1870#2:270\n1626#2:271\n1#3:269\n*S KotlinDebug\n*F\n+ 1 MediaPickerButton.kt\nio/intercom/android/sdk/m5/preview/ui/MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1\n*L\n83#1:259,9\n83#1:268\n83#1:270\n83#1:271\n83#1:269\n*E\n"
)
final class MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   final ManagedActivityResultLauncher $previewLauncher;
   // $VF: synthetic field
   final List $it;
   // $VF: synthetic field
   final MediaPickerButtonCTAStyle $mediaPickerButtonCTAStyle;

   MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1(ManagedActivityResultLauncher var1, List var2, MediaPickerButtonCTAStyle var3, Continuation var4) {
      super(2, var4);
      this.$previewLauncher = var1;
      this.$it = var2;
      this.$mediaPickerButtonCTAStyle = var3;
   }

   @Override
   public final Object invokeSuspend(Object var1) {
      Object var21 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            Continuation var10001 = this;
            this.label = 1;
            if (DelayKt.delay(50L, var10001) == var21) {
               return var21;
            }
            break;
         case 1:
            ResultKt.throwOnFailure(var1);
            break;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      Iterable var3 = this.$it;
      IntercomPreviewFile$Companion var4 = IntercomPreviewFile.Companion;
      ManagedActivityResultLauncher var20 = this.$previewLauncher;
      int var5 = 0;
      Iterable var6 = var3;
      ArrayList var7 = new ArrayList();
      int var8 = 0;
      Iterable var9 = var6;
      int var10 = 0;

      for (Object var12 : var9) {
         Object var13 = var12;
         int var14 = 0;
         Uri var15 = (Uri)var13;
         int var16 = 0;
         int var17 = 0;
         IntercomPreviewFile$LocalFile var10000 = var4.local(var15);
         if (var10000 != null) {
            Object var18 = var10000;
            int var19 = 0;
            var7.add(var18);
         }
      }

      List var2 = (List)var7;
      String var22 = this.$mediaPickerButtonCTAStyle instanceof MediaPickerButtonCTAStyle$TextButton
         ? ((MediaPickerButtonCTAStyle$TextButton)this.$mediaPickerButtonCTAStyle).getCtaText()
         : null;
      boolean var23 = this.$mediaPickerButtonCTAStyle instanceof MediaPickerButtonCTAStyle$TopBarButton;
      DeleteType$Remove var24 = DeleteType$Remove.INSTANCE;
      var20.launch(new IntercomPreviewArgs(var2, var24, var22, var23, 0, null, 48, null));
      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      return new MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1(this.$previewLauncher, this.$it, this.$mediaPickerButtonCTAStyle, var2);
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((MediaPickerButtonKt$MediaPickerButton$pickerLauncher$1$1$1)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
