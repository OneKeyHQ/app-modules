package io.intercom.android.sdk.m5.preview.ui;

import androidx.compose.foundation.gestures.TapGestureDetectorKt;
import androidx.compose.foundation.gestures.TransformGestureDetectorKt;
import androidx.compose.ui.geometry.Offset;
import androidx.compose.ui.input.pointer.AwaitPointerEventScope;
import androidx.compose.ui.input.pointer.PointerEvent;
import androidx.compose.ui.input.pointer.PointerEventKt;
import androidx.compose.ui.input.pointer.PointerInputChange;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.SourceDebugExtension;

@DebugMetadata(
   f = "ZoomableImage.kt",
   l = {213, 215, 247},
   i = {0, 0, 0, 0, 0, 1, 1, 1, 1, 1},
   s = {"L$0", "F$0", "J$0", "I$0", "F$1", "L$0", "F$0", "J$0", "I$0", "F$1"},
   n = {"$this$awaitEachGesture", "zoom", "pan", "pastTouchSlop", "touchSlop", "$this$awaitEachGesture", "zoom", "pan", "pastTouchSlop", "touchSlop"},
   m = "invokeSuspend",
   c = "io.intercom.android.sdk.m5.preview.ui.ZoomableImageKt$detectZoomPanAndDoubleTap$2"
)
@Metadata(
   mv = {2, 0, 0},
   k = 3,
   xi = 48,
   d1 = "\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n",
   d2 = {"<anonymous>", "", "Landroidx/compose/ui/input/pointer/AwaitPointerEventScope;"}
)
@SourceDebugExtension(
   "SMAP\nZoomableImage.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$detectZoomPanAndDoubleTap$2\n+ 2 ListUtils.kt\nandroidx/compose/ui/util/ListUtilsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,277:1\n103#2:278\n35#2,5:279\n104#2:284\n35#2,5:289\n103#2:294\n35#2,5:295\n104#2:300\n1788#3,4:285\n*S KotlinDebug\n*F\n+ 1 ZoomableImage.kt\nio/intercom/android/sdk/m5/preview/ui/ZoomableImageKt$detectZoomPanAndDoubleTap$2\n*L\n216#1:278\n216#1:279,5\n216#1:284\n237#1:289,5\n243#1:294\n243#1:295,5\n243#1:300\n231#1:285,4\n*E\n"
)
final class ZoomableImageKt$detectZoomPanAndDoubleTap$2 extends RestrictedSuspendLambda implements Function2 {
   float F$0;
   float F$1;
   long J$0;
   int I$0;
   int label;
   // $VF: synthetic field
   private Object L$0;
   // $VF: synthetic field
   final Function0 $isZoomed;
   // $VF: synthetic field
   final Function3 $onGesture;
   // $VF: synthetic field
   final Function1 $onDoubleTap;

   ZoomableImageKt$detectZoomPanAndDoubleTap$2(Function0 var1, Function3 var2, Function1 var3, Continuation var4) {
      super(2, var4);
      this.$isZoomed = var1;
      this.$onGesture = var2;
      this.$onDoubleTap = var3;
   }

   // $VF: Irreducible bytecode was duplicated to produce valid code
   @Override
   public final Object invokeSuspend(Object var1) {
      Object var24 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
      AwaitPointerEventScope var2;
      float var3;
      long var4;
      int var6;
      float var9;
      List var13;
      int var15;
      int var16;
      PointerEvent var26;
      switch (this.label) {
         case 0:
            ResultKt.throwOnFailure(var1);
            var2 = (AwaitPointerEventScope)this.L$0;
            var3 = 1.0F;
            var4 = Offset.Companion.recovered$86a8752c359fef5c();
            var6 = 0;
            var9 = var2.getViewConfiguration().getTouchSlop();
            Continuation var10003 = this;
            this.L$0 = var2;
            this.F$0 = var3;
            this.J$0 = var4;
            this.I$0 = var6;
            this.F$1 = var9;
            this.label = 1;
            if (TapGestureDetectorKt.awaitFirstDown$default(var2, false, null, var10003, 2, null) == var24) {
               return var24;
            }

            Continuation var83 = this;
            this.L$0 = var2;
            this.F$0 = var3;
            this.J$0 = var4;
            this.I$0 = var6;
            this.F$1 = var9;
            this.label = 2;
            Object var78 = AwaitPointerEventScope.awaitPointerEvent$default(var2, null, var83, 1, null);
            if (var78 == var24) {
               return var24;
            }

            var26 = (PointerEvent)var78;
            List var29 = var26.getChanges();
            boolean var37 = false;
            var13 = var29;
            boolean var46 = false;
            var15 = 0;
            var16 = var13.size();
            break;
         case 1:
            var9 = this.F$1;
            var6 = this.I$0;
            var4 = this.J$0;
            var3 = this.F$0;
            var2 = (AwaitPointerEventScope)this.L$0;
            ResultKt.throwOnFailure(var1);
            Continuation var10002 = this;
            this.L$0 = var2;
            this.F$0 = var3;
            this.J$0 = var4;
            this.I$0 = var6;
            this.F$1 = var9;
            this.label = 2;
            Object var10000 = AwaitPointerEventScope.awaitPointerEvent$default(var2, null, var10002, 1, null);
            if (var10000 == var24) {
               return var24;
            }

            var26 = (PointerEvent)var10000;
            List var28 = var26.getChanges();
            boolean var36 = false;
            var13 = var28;
            boolean var45 = false;
            var15 = 0;
            var16 = var13.size();
            break;
         case 2:
            var9 = this.F$1;
            var6 = this.I$0;
            var4 = this.J$0;
            var3 = this.F$0;
            var2 = (AwaitPointerEventScope)this.L$0;
            ResultKt.throwOnFailure(var1);
            var26 = (PointerEvent)var1;
            List var11 = var26.getChanges();
            boolean var12 = false;
            var13 = var11;
            boolean var14 = false;
            var15 = 0;
            var16 = var13.size();
            break;
         case 3:
            ResultKt.throwOnFailure(var1);
            Offset var10 = (Offset)var1;
            if (var10 != null) {
               this.$onDoubleTap.invoke(var10);
            }

            return Unit.INSTANCE;
         default:
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
      }

      boolean var7;
      label182:
      while (true) {
         int var79;
         if (var15 < var16) {
            Object var17 = var13.get(var15);
            Object var18 = var17;
            int var19 = 0;
            PointerInputChange var20 = (PointerInputChange)var18;
            int var21 = 0;
            if (!var20.isConsumed()) {
               var15++;
               continue;
            }

            var79 = 1;
         } else {
            var79 = 0;
         }

         var7 = var79 != 0;
         if (!var7) {
            float var30 = TransformGestureDetectorKt.calculateZoom(var26);
            long var38 = TransformGestureDetectorKt.calculatePan(var26);
            if (var6 == 0) {
               var3 *= var30;
               var4 = Offset.recovered$968713ee027f1595(var4, var38);
               float var47 = TransformGestureDetectorKt.calculateCentroidSize(var26, false);
               float var54 = Math.abs(1 - var3) * var47;
               float var59 = Offset.recovered$404c0a0a35ea845b(var4);
               if (!(var54 > var9)) {
                  if (var59 > var9) {
                     var6 = 1;
                  }
               } else {
                  var6 = 1;
               }
            }

            label175:
            if (var6 != 0) {
               label194: {
                  var var55 = var26.getChanges();
                  var16 = 0;
                  int var62;
                  if (var55 instanceof Collection) {
                     if (((Collection)var55).isEmpty()) {
                        var79 = 0;
                        break label194;
                     }

                     var62 = 0;
                  } else {
                     var62 = 0;
                  }

                  for (Object var69 : var55) {
                     PointerInputChange var72 = (PointerInputChange)var69;
                     int var75 = 0;
                     if (var72.getPressed()) {
                        if (++var62 < 0) {
                           CollectionsKt.throwCountOverflow();
                        }
                     }
                  }

                  var79 = var62;
               }

               int var48 = var79;
               List var63;
               int var70;
               int var73;
               if (var48 <= 1) {
                  if (!(Boolean)this.$isZoomed.invoke()) {
                     List var31 = var26.getChanges();
                     boolean var39 = false;
                     var13 = var31;
                     boolean var49 = false;
                     var15 = 0;
                     var16 = var13.size();
                     break label175;
                  }

                  long var56 = TransformGestureDetectorKt.calculateCentroid(var26, true);
                  if (var30 == 1.0F) {
                     if (!Offset.recovered$9eab66ea8c46e11c(var38, Offset.Companion.recovered$86a8752c359fef5c())) {
                        this.$onGesture.invoke(Offset.recovered$ae98585edc0e5b3c(var56), Offset.recovered$ae98585edc0e5b3c(var38), Boxing.boxFloat(var30));
                     }
                  } else {
                     this.$onGesture.invoke(Offset.recovered$ae98585edc0e5b3c(var56), Offset.recovered$ae98585edc0e5b3c(var38), Boxing.boxFloat(var30));
                  }

                  var63 = var26.getChanges();
                  boolean var66 = false;
                  var70 = 0;
                  var73 = var63.size();
               } else {
                  long var58 = TransformGestureDetectorKt.calculateCentroid(var26, true);
                  if (var30 == 1.0F) {
                     if (!Offset.recovered$9eab66ea8c46e11c(var38, Offset.Companion.recovered$86a8752c359fef5c())) {
                        this.$onGesture.invoke(Offset.recovered$ae98585edc0e5b3c(var58), Offset.recovered$ae98585edc0e5b3c(var38), Boxing.boxFloat(var30));
                     }
                  } else {
                     this.$onGesture.invoke(Offset.recovered$ae98585edc0e5b3c(var58), Offset.recovered$ae98585edc0e5b3c(var38), Boxing.boxFloat(var30));
                  }

                  var63 = var26.getChanges();
                  int var67 = 0;
                  var70 = 0;
                  var73 = var63.size();
               }

               for (; var70 < var73; var70++) {
                  Object var76 = var63.get(var70);
                  PointerInputChange var22 = (PointerInputChange)var76;
                  int var23 = 0;
                  if (PointerEventKt.positionChanged(var22)) {
                     var22.consume();
                  }
               }

               List var32 = var26.getChanges();
               int var40 = 0;
               var13 = var32;
               var48 = 0;
               var15 = 0;
               var16 = var13.size();
            } else {
               List var33 = var26.getChanges();
               boolean var41 = false;
               var13 = var33;
               boolean var51 = false;
               var15 = 0;
               var16 = var13.size();
            }
         } else {
            List var34 = var26.getChanges();
            boolean var42 = false;
            var13 = var34;
            boolean var52 = false;
            var15 = 0;
            var16 = var13.size();
         }

         while (true) {
            if (var15 < var16) {
               Object var64 = var13.get(var15);
               Object var68 = var64;
               int var71 = 0;
               PointerInputChange var74 = (PointerInputChange)var68;
               int var77 = 0;
               if (!var74.getPressed()) {
                  var15++;
                  continue;
               }

               boolean var8 = true;
               if (var7 || !var8) {
                  break label182;
               }
            } else {
               boolean var25 = false;
               if (var7 || !var25) {
                  break label182;
               }
            }

            Continuation var84 = this;
            this.L$0 = var2;
            this.F$0 = var3;
            this.J$0 = var4;
            this.I$0 = var6;
            this.F$1 = var9;
            this.label = 2;
            Object var81 = AwaitPointerEventScope.awaitPointerEvent$default(var2, null, var84, 1, null);
            if (var81 == var24) {
               return var24;
            }

            var26 = (PointerEvent)var81;
            List var35 = var26.getChanges();
            int var43 = 0;
            var13 = var35;
            int var53 = 0;
            var15 = 0;
            var16 = var13.size();
            break;
         }
      }

      if (!var7 && var6 == 0) {
         long var10001 = var2.getViewConfiguration().getDoubleTapTimeoutMillis();
         Function2 var85 = new ZoomableImageKt$detectZoomPanAndDoubleTap$2$doubleTapPosition$1(var9, null);
         Continuation var86 = this;
         this.L$0 = null;
         this.label = 3;
         Object var82 = var2.withTimeoutOrNull(var10001, var85, var86);
         if (var82 == var24) {
            return var24;
         }

         Offset var27 = (Offset)var82;
         if (var27 != null) {
            this.$onDoubleTap.invoke(var27);
         }
      }

      return Unit.INSTANCE;
   }

   @Override
   public final Continuation create(Object var1, Continuation var2) {
      ZoomableImageKt$detectZoomPanAndDoubleTap$2 var3 = new ZoomableImageKt$detectZoomPanAndDoubleTap$2(
         this.$isZoomed, this.$onGesture, this.$onDoubleTap, var2
      );
      var3.L$0 = var1;
      return var3;
   }

   public final Object invoke(AwaitPointerEventScope var1, Continuation var2) {
      return ((ZoomableImageKt$detectZoomPanAndDoubleTap$2)this.create(var1, var2)).invokeSuspend(Unit.INSTANCE);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object invoke(Object var1, Object var2) {
      return this.invoke((AwaitPointerEventScope)var1, (Continuation)var2);
   }
}
