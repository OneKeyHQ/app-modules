package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import android.net.Uri;
import androidx.compose.runtime.Composable;
import androidx.compose.runtime.ComposableTarget;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import io.intercom.android.sdk.blocks.lib.models.Block$Builder;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem;
import io.intercom.android.sdk.m5.conversation.states.MediaUploadItem$UploadState$Success;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Image;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Other;
import io.intercom.android.sdk.m5.upload.data.MediaData$Media$Video;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;

@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
@SourceDebugExtension(
   "SMAP\nMediaItems.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-3$1\n+ 2 Composer.kt\nandroidx/compose/runtime/ComposerKt\n*L\n1#1,438:1\n1047#2,6:439\n1047#2,6:445\n1047#2,6:451\n*S KotlinDebug\n*F\n+ 1 MediaItems.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/ComposableSingletons$MediaItemsKt$lambda-3$1\n*L\n384#1:439,6\n385#1:445,6\n386#1:451,6\n*E\n"
)
final class ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1 implements Function2 {
   public static final ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1 INSTANCE = new ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1();

   @Composable
   @ComposableTarget(applier = "androidx.compose.ui.UiComposable")
   public final void invoke(Composer var1, int var2) {
      ComposerKt.sourceInformation(var1, "C383@13405L2,384@13441L2,385@13477L2,368@12556L934:MediaItems.kt#yb6g9");
      if ((var2 & 3) == 2 && var1.getSkipping()) {
         var1.skipToGroupEnd();
      } else {
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart(
               795159307,
               var2,
               -1,
               "io.intercom.android.sdk.m5.conversation.ui.components.composer.ComposableSingletons$MediaItemsKt.lambda-3.<anonymous> (MediaItems.kt:368)"
            );
         }

         MediaUploadItem[] var3 = new MediaUploadItem[3];
         Uri var10012 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         var3[0] = new MediaUploadItem(
            null,
            new MediaData$Media$Image("image/png", 0, 0, 0L, "image1", var10012, null, 64, null),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         var10012 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         var3[1] = new MediaUploadItem(
            null,
            new MediaData$Media$Video("video/mp4", 0, 0, 0L, "video1", var10012, 0L, null),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         Uri var10010 = Uri.EMPTY;
         Intrinsics.checkNotNullExpressionValue(Uri.EMPTY, "EMPTY");
         var3[2] = new MediaUploadItem(
            null,
            new MediaData$Media$Other("application/pdf", 0L, "pdf_document.pdf", var10010),
            new MediaUploadItem$UploadState$Success(new Block$Builder()),
            1,
            null
         );
         List var10000 = CollectionsKt.listOf(var3);
         var1.startReplaceGroup(-780400417);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaItems.kt#9igjgp");
         Composer var4 = var1;
         boolean var5 = false;
         int var6 = 0;
         Object var7 = var4.rememberedValue();
         int var8 = 0;
         Object var36;
         if (var7 == Composer.Companion.getEmpty()) {
            List var10 = var10000;
            int var9 = 0;
            var36 = ((kotlin.jvm.functions.Function1) (recoveredArgument_547_0) -> ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$1$lambda$0(((java.lang.Integer)recoveredArgument_547_0).intValue()));
            var10000 = var10;
            Object var27 = var36;
            var4.updateRememberedValue(var27);
            var36 = (Function1)var27;
         } else {
            var36 = (Function1)var7;
         }

         Function1 var14 = (kotlin.jvm.functions.Function1) var36;
         var1.endReplaceGroup();
         var36 = var14;
         var1.startReplaceGroup(-780399265);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaItems.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10002;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var11 = var14;
            List var32 = var10000;
            int var28 = 0;
            Function1 var12 = ((kotlin.jvm.functions.Function1) (recoveredArgument_548_0) -> ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$3$lambda$2((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_548_0));
            var10000 = var32;
            var36 = var11;
            Object var29 = var12;
            var4.updateRememberedValue(var29);
            var10002 = (Function1)var29;
         } else {
            var10002 = (Function1)var7;
         }

         Function1 var15 = (kotlin.jvm.functions.Function1) var10002;
         var1.endReplaceGroup();
         var10002 = var15;
         var1.startReplaceGroup(-780398113);
         ComposerKt.sourceInformation(var1, "CC(remember):MediaItems.kt#9igjgp");
         var4 = var1;
         var5 = false;
         var6 = 0;
         var7 = var4.rememberedValue();
         var8 = 0;
         Object var10003;
         if (var7 == Composer.Companion.getEmpty()) {
            Function1 var35 = var15;
            Function1 var34 = (kotlin.jvm.functions.Function1) var36;
            List var33 = var10000;
            int var30 = 0;
            Function1 var13 = ((kotlin.jvm.functions.Function1) (recoveredArgument_549_0) -> ComposableSingletons$MediaItemsKt$RecoveredInnerb8048fd051efd9e2$1.invoke$lambda$5$lambda$4((io.intercom.android.sdk.m5.conversation.states.MediaUploadItem)recoveredArgument_549_0));
            var10000 = var33;
            var36 = var34;
            var10002 = var35;
            Object var31 = var13;
            var4.updateRememberedValue(var31);
            var10003 = var31;
         } else {
            var10003 = var7;
         }

         Function1 var16 = (Function1)var10003;
         var1.endReplaceGroup();
         MediaItemsKt.MediaItems(var10000, (kotlin.jvm.functions.Function1) var36, (kotlin.jvm.functions.Function1) var10002, var16, null, var1, 3504, 16);
         if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
         }
      }
   }

   private static final Unit invoke$lambda$1$lambda$0(int var0) {
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$3$lambda$2(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   private static final Unit invoke$lambda$5$lambda$4(MediaUploadItem var0) {
      Intrinsics.checkNotNullParameter(var0, "it");
      return Unit.INSTANCE;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      this.invoke((Composer)var1, ((Number)var2).intValue());
      return Unit.INSTANCE;
   }
}
