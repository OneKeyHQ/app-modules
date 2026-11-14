package io.intercom.android.sdk.lightcompressor.video;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.view.Surface;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001B\u0011\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0010\u001a\u00020\u0011H\u0002J\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u0011J\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00112\u0006\u0010\u0017\u001a\u00020\u0018J\b\u0010\u0019\u001a\u00020\u0011H\u0002R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082D¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/video/InputSurface;",
         "",
         "surface",
         "Landroid/view/Surface;",
         "<init>",
         "(Landroid/view/Surface;)V",
         "eglRecordableAndroid",
         "",
         "eglOpenGlES2Bit",
         "mEGLDisplay",
         "Landroid/opengl/EGLDisplay;",
         "mEGLContext",
         "Landroid/opengl/EGLContext;",
         "mEGLSurface",
         "Landroid/opengl/EGLSurface;",
         "mSurface",
         "eglSetup",
         "",
         "release",
         "makeCurrent",
         "swapBuffers",
         "",
         "setPresentationTime",
         "nsecs",
         "",
         "checkEglError",
         "intercom-sdk-lightcompressor_release"
   }
)
public final class InputSurface {
   private final int eglRecordableAndroid;
   private final int eglOpenGlES2Bit;
   @Nullable
   private EGLDisplay mEGLDisplay;
   @Nullable
   private EGLContext mEGLContext;
   @Nullable
   private EGLSurface mEGLSurface;
   @Nullable
   private Surface mSurface;

   public InputSurface(@Nullable Surface var1) {
      this.eglRecordableAndroid = 12610;
      this.eglOpenGlES2Bit = 4;
      if (var1 == null) {
         throw new NullPointerException();
      }

      this.mSurface = var1;
      this.eglSetup();
   }

   private final void eglSetup() {
      this.mEGLDisplay = EGL14.eglGetDisplay(0);
      if (this.mEGLDisplay == EGL14.EGL_NO_DISPLAY) {
         throw new RuntimeException("unable to get EGL14 display");
      }

      int[] var1 = new int[2];
      if (!EGL14.eglInitialize(this.mEGLDisplay, var1, 0, var1, 1)) {
         this.mEGLDisplay = null;
         throw new RuntimeException("unable to initialize EGL14");
      }

      int[] var3 = new int[]{12324, 8, 12323, 8, 12322, 8, 12352, this.eglOpenGlES2Bit, this.eglRecordableAndroid, 1, 12344};
      int[] var2 = var3;
      EGLConfig[] var8 = new EGLConfig[1];
      int[] var4 = new int[1];
      if (!EGL14.eglChooseConfig(this.mEGLDisplay, var2, 0, var8, 0, var8.length, var4, 0)) {
         throw new RuntimeException("unable to find RGB888+recordable ES2 EGL config");
      }

      int[] var6 = new int[]{12440, 2, 12344};
      int[] var5 = var6;
      this.mEGLContext = EGL14.eglCreateContext(this.mEGLDisplay, var8[0], EGL14.EGL_NO_CONTEXT, var5, 0);
      this.checkEglError();
      if (this.mEGLContext == null) {
         throw new RuntimeException("null context");
      }

      int[] var7 = new int[]{12344};
      var6 = var7;
      this.mEGLSurface = EGL14.eglCreateWindowSurface(this.mEGLDisplay, var8[0], this.mSurface, var6, 0);
      this.checkEglError();
      if (this.mEGLSurface == null) {
         throw new RuntimeException("surface was null");
      }
   }

   public final void release() {
      if (Intrinsics.areEqual(EGL14.eglGetCurrentContext(), this.mEGLContext)) {
         EGL14.eglMakeCurrent(this.mEGLDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT);
      }

      EGL14.eglDestroySurface(this.mEGLDisplay, this.mEGLSurface);
      EGL14.eglDestroyContext(this.mEGLDisplay, this.mEGLContext);
      if (this.mSurface != null) {
         this.mSurface.release();
      }

      this.mEGLDisplay = null;
      this.mEGLContext = null;
      this.mEGLSurface = null;
      this.mSurface = null;
   }

   public final void makeCurrent() {
      if (!EGL14.eglMakeCurrent(this.mEGLDisplay, this.mEGLSurface, this.mEGLSurface, this.mEGLContext)) {
         throw new RuntimeException("eglMakeCurrent failed");
      }
   }

   public final boolean swapBuffers() {
      return EGL14.eglSwapBuffers(this.mEGLDisplay, this.mEGLSurface);
   }

   public final void setPresentationTime(long var1) {
      EGLExt.eglPresentationTimeANDROID(this.mEGLDisplay, this.mEGLSurface, var1);
   }

   private final void checkEglError() {
      boolean var1 = false;

      while (EGL14.eglGetError() != 12288) {
         var1 = true;
      }

      if (var1) {
         throw new RuntimeException("EGL error encountered (see log)");
      }
   }
}
