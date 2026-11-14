package io.intercom.android.sdk.m5.push;

import android.content.Context;
import android.graphics.Bitmap;
import io.intercom.android.sdk.identity.AppConfig;
import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Deferred;

@DebugMetadata(
   f = "IntercomPushBitmapUtils.kt",
   l = {100, 101},
   i = {0, 1},
   s = {"L$0", "L$0"},
   n = {"avatarDeferred", "contentBitmap"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.push.IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Lkotlinx/coroutines/CoroutineScope;"}
)
final class IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2 extends SuspendLambda implements Function2 {
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final boolean $hasContentImage;
   // $VF: synthetic field
   final boolean $hasAvatarUrl;
   // $VF: synthetic field
   final boolean $hasAuthorName;
   // $VF: synthetic field
   final Function2 $onUpdate;
   // $VF: synthetic field
   final Context $context;
   // $VF: synthetic field
   final String $contentImageUrl;
   // $VF: synthetic field
   final String $avatarImageUrl;
   // $VF: synthetic field
   final AppConfig $appConfig;
   // $VF: synthetic field
   final String $authorName;

   IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2(
      boolean var1, boolean var2, boolean var3, Function2 var4, Context var5, String var6, String var7, AppConfig var8, String var9, Continuation var10
   ) {
      super(2, var10);
      this.$hasContentImage = var1;
      this.$hasAvatarUrl = var2;
      this.$hasAuthorName = var3;
      this.$onUpdate = var4;
      this.$context = var5;
      this.$contentImageUrl = var6;
      this.$avatarImageUrl = var7;
      this.$appConfig = var8;
      this.$authorName = var9;
   }

   // $VF: Handled exception range with multiple entry points by splitting it
   // $VF: Duplicated exception handlers to handle obfuscated exceptions
   @Override
   public final Object invokeSuspend(Object var1) {
      Bitmap var5;
      Object var28;
      Bitmap recoveredContentBitmap;
      Bitmap recoveredAvatarBitmap;
      label116: {
         label133: {
            label121: {
               Deferred var4;
               Object var7;
               label112: {
                  label111: {
                     var7 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                     switch (this.label) {
                        case 0:
                           ResultKt.throwOnFailure(var1);
                           CoroutineScope var2 = (CoroutineScope)this.L$0;

                           try {
                              Deferred var3 = this.$hasContentImage
                                 ? BuildersKt.async$default(
                                    var2,
                                    null,
                                    null,
                                    new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$contentDeferred$1(this.$context, this.$contentImageUrl, null),
                                    3,
                                    null
                                 )
                                 : null;
                              var4 = this.$hasAvatarUrl
                                 ? BuildersKt.async$default(
                                    var2,
                                    null,
                                    null,
                                    new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$1(
                                       this.$context, this.$avatarImageUrl, this.$appConfig, null
                                    ),
                                    3,
                                    null
                                 )
                                 : (
                                    this.$hasAuthorName
                                       ? BuildersKt.async$default(
                                          var2,
                                          null,
                                          null,
                                          new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2$avatarDeferred$2(
                                             this.$context, this.$authorName, this.$appConfig, null
                                          ),
                                          3,
                                          null
                                       )
                                       : null
                                 );
                              if (var3 == null) {
                                 break label111;
                              }

                              Continuation var10001 = this;
                              this.L$0 = var4;
                              this.label = 1;
                              var28 = var3.await(var10001);
                           } catch (CancellationException var24) {
                              throw var24;
                           } catch (Exception var25) {
                              IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var25.getMessage());
                              return Unit.INSTANCE;
                           }

                           if (var28 == var7) {
                              return var7;
                           }
                           break;
                        case 1:
                           var4 = (Deferred)this.L$0;

                           try {
                              ResultKt.throwOnFailure(var1);
                              var28 = var1;
                              break;
                           } catch (CancellationException var20) {
                              throw var20;
                           } catch (Exception var21) {
                              IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var21.getMessage());
                              return Unit.INSTANCE;
                           }
                        case 2:
                           var5 = (Bitmap)this.L$0;

                           try {
                              ResultKt.throwOnFailure(var1);
                              var28 = var1;
                              break label121;
                           } catch (CancellationException var14) {
                              throw var14;
                           } catch (Exception var15) {
                              IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var15.getMessage());
                              return Unit.INSTANCE;
                           }
                        default:
                           throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                     }

                     try {
                        recoveredContentBitmap = (Bitmap)var28;
                        break label112;
                     } catch (CancellationException var18) {
                        throw var18;
                     } catch (Exception var19) {
                        IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var19.getMessage());
                        return Unit.INSTANCE;
                     }
                  }

                  try {
                     recoveredContentBitmap = null;
                  } catch (CancellationException var16) {
                     throw var16;
                  } catch (Exception var17) {
                     IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var17.getMessage());
                     return Unit.INSTANCE;
                  }
               }

               try {
                  var5 = recoveredContentBitmap;
                  if (var4 == null) {
                     break label133;
                  }

                  Continuation var29 = this;
                  this.L$0 = var5;
                  this.label = 2;
                  var28 = var4.await(var29);
               } catch (CancellationException var22) {
                  throw var22;
               } catch (Exception var23) {
                  IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var23.getMessage());
                  return Unit.INSTANCE;
               }

               if (var28 == var7) {
                  return var7;
               }
            }

            try {
               recoveredAvatarBitmap = (Bitmap)var28;
               break label116;
            } catch (CancellationException var12) {
               throw var12;
            } catch (Exception var13) {
               IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var13.getMessage());
               return Unit.INSTANCE;
            }
         }

         try {
            recoveredAvatarBitmap = null;
         } catch (CancellationException var10) {
            throw var10;
         } catch (Exception var11) {
            IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var11.getMessage());
            return Unit.INSTANCE;
         }
      }

      try {
         Bitmap var6 = recoveredAvatarBitmap;
         IntercomPushBitmapUtilsKt.access$getTwig$p().d("loadBitmaps result: contentBitmap=" + (var5 != null) + ", avatarBitmap=" + (var6 != null));
         if (var5 != null || var6 != null) {
            this.$onUpdate.invoke(var5, var6);
         }
      } catch (CancellationException var8) {
         throw var8;
      } catch (Exception var9) {
         IntercomPushBitmapUtilsKt.access$getTwig$p().w("Error loading push images: " + var9.getMessage());
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2 var3 = new IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2(
         this.$hasContentImage,
         this.$hasAvatarUrl,
         this.$hasAuthorName,
         this.$onUpdate,
         this.$context,
         this.$contentImageUrl,
         this.$avatarImageUrl,
         this.$appConfig,
         this.$authorName,
         var2
      );
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(CoroutineScope var1, Continuation var2) {
      return ((IntercomPushBitmapUtilsKt$loadAndApplyBitmaps$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((CoroutineScope)var1, (Continuation)var2);
   }
}
