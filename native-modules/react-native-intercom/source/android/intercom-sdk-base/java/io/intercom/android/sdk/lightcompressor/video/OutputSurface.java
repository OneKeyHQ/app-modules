package io.intercom.android.sdk.lightcompressor.video;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.view.Surface;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u000e\u001a\u00020\u000fH\u0002J\u0006\u0010\u0010\u001a\u00020\u000fJ\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007J\u0006\u0010\u0012\u001a\u00020\u000fJ\u0006\u0010\u0013\u001a\u00020\u000fJ\u0012\u0010\u0014\u001a\u00020\u000f2\b\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u0016R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/video/OutputSurface;",
         "Landroid/graphics/SurfaceTexture$OnFrameAvailableListener;",
         "<init>",
         "()V",
         "mSurfaceTexture",
         "Landroid/graphics/SurfaceTexture;",
         "mSurface",
         "Landroid/view/Surface;",
         "mFrameSyncObject",
         "Ljava/lang/Object;",
         "mFrameAvailable",
         "",
         "mTextureRender",
         "Lio/intercom/android/sdk/lightcompressor/video/TextureRenderer;",
         "setup",
         "",
         "release",
         "getSurface",
         "awaitNewImage",
         "drawImage",
         "onFrameAvailable",
         "p0",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class OutputSurface implements OnFrameAvailableListener {
   @Nullable
   private SurfaceTexture mSurfaceTexture;
   @Nullable
   private Surface mSurface;
   @NotNull
   private final Object mFrameSyncObject = new Object();
   private boolean mFrameAvailable;
   @Nullable
   private TextureRenderer mTextureRender;

   public OutputSurface() {
      this.setup();
   }

   private final void setup() {
      this.mTextureRender = new TextureRenderer();
      if (this.mTextureRender != null) {
         TextureRenderer var1 = this.mTextureRender;
         int var2 = 0;
         var1.surfaceCreated();
         this.mSurfaceTexture = new SurfaceTexture(var1.getTextureId());
         if (this.mSurfaceTexture != null) {
            SurfaceTexture var3 = this.mSurfaceTexture;
            int var4 = 0;
            var3.setOnFrameAvailableListener(this);
            this.mSurface = new Surface(this.mSurfaceTexture);
         }
      }
   }

   public final void release() {
      if (this.mSurface != null) {
         this.mSurface.release();
      }

      this.mTextureRender = null;
      this.mSurface = null;
      this.mSurfaceTexture = null;
   }

   @Nullable
   public final Surface getSurface() {
      return this.mSurface;
   }

   public final void awaitNewImage() {
      int var1 = 100;
      synchronized (this.mFrameSyncObject) {
         int var3 = 0;

         while (!this.mFrameAvailable) {
            try {
               this.mFrameSyncObject.wait(var1);
               if (!this.mFrameAvailable) {
                  throw new RuntimeException("Surface frame wait timed out");
               }
            } catch (InterruptedException var5) {
               throw new RuntimeException(var5);
            }
         }

         this.mFrameAvailable = false;
         Unit var7 = Unit.INSTANCE;
      }

      if (this.mTextureRender != null) {
         this.mTextureRender.checkGlError("before updateTexImage");
      }

      if (this.mSurfaceTexture != null) {
         this.mSurfaceTexture.updateTexImage();
      }
   }

   public final void drawImage() {
      TextureRenderer var10000 = this.mTextureRender;
      if (this.mTextureRender != null) {
         SurfaceTexture var10001 = this.mSurfaceTexture;
         Intrinsics.checkNotNull(this.mSurfaceTexture);
         var10000.drawFrame(var10001);
      }
   }

   @Override
   public void onFrameAvailable(@Nullable SurfaceTexture var1) {
      synchronized (this.mFrameSyncObject) {
         int var3 = 0;
         if (this.mFrameAvailable) {
            throw new RuntimeException("mFrameAvailable already set, frame could be dropped");
         }

         this.mFrameAvailable = true;
         this.mFrameSyncObject.notifyAll();
         Unit var6 = Unit.INSTANCE;
      }
   }
}
