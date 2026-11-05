package io.intercom.android.sdk.lightcompressor.video;

import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0014\n\u0002\b\t\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b\u0000\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0017\u001a\u00020\u0005J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bJ\u0006\u0010\u001c\u001a\u00020\u0019J\u0018\u0010\u001d\u001a\u00020\u00052\u0006\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u001f\u001a\u00020\fH\u0002J\b\u0010 \u001a\u00020\u0005H\u0002J\u000e\u0010!\u001a\u00020\u00192\u0006\u0010\"\u001a\u00020\fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082D¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006#",
   d2 = {
         "Lio/intercom/android/sdk/lightcompressor/video/TextureRenderer;",
         "",
         "<init>",
         "()V",
         "floatSizeBytes",
         "",
         "triangleVerticesDataStrideBytes",
         "triangleVerticesDataPosOffset",
         "triangleVerticesDataUvOffset",
         "mTriangleVertices",
         "Ljava/nio/FloatBuffer;",
         "vertexShader",
         "",
         "fragmentShader",
         "mMVPMatrix",
         "",
         "mSTMatrix",
         "mProgram",
         "mTextureID",
         "muMVPMatrixHandle",
         "muSTMatrixHandle",
         "maPositionHandle",
         "maTextureHandle",
         "getTextureId",
         "drawFrame",
         "",
         "st",
         "Landroid/graphics/SurfaceTexture;",
         "surfaceCreated",
         "loadShader",
         "shaderType",
         "source",
         "createProgram",
         "checkGlError",
         "op",
         "intercom-sdk-lightcompressor_release"
   }
)
@SourceDebugExtension(
   "SMAP\nTextureRenderer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TextureRenderer.kt\nio/intercom/android/sdk/lightcompressor/video/TextureRenderer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,214:1\n1#2:215\n*E\n"
)
public final class TextureRenderer {
   private final int floatSizeBytes;
   private final int triangleVerticesDataStrideBytes;
   private int triangleVerticesDataPosOffset;
   private final int triangleVerticesDataUvOffset;
   @NotNull
   private FloatBuffer mTriangleVertices;
   @NotNull
   private final String vertexShader;
   @NotNull
   private final String fragmentShader;
   @NotNull
   private final float[] mMVPMatrix;
   @NotNull
   private final float[] mSTMatrix;
   private int mProgram;
   private int mTextureID;
   private int muMVPMatrixHandle;
   private int muSTMatrixHandle;
   private int maPositionHandle;
   private int maTextureHandle;

   public TextureRenderer() {
      this.floatSizeBytes = 4;
      this.triangleVerticesDataStrideBytes = 5 * this.floatSizeBytes;
      this.triangleVerticesDataUvOffset = 3;
      this.vertexShader = "uniform mat4 uMVPMatrix;\nuniform mat4 uSTMatrix;\nattribute vec4 aPosition;\nattribute vec4 aTextureCoord;\nvarying vec2 vTextureCoord;\nvoid main() {\n  gl_Position = uMVPMatrix * aPosition;\n  vTextureCoord = (uSTMatrix * aTextureCoord).xy;\n}\n";
      this.fragmentShader = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 vTextureCoord;\nuniform samplerExternalOES sTexture;\nvoid main() {\n  gl_FragColor = texture2D(sTexture, vTextureCoord);\n}\n";
      this.mMVPMatrix = new float[16];
      this.mSTMatrix = new float[16];
      this.mTextureID = -12345;
      float[] var2 = new float[]{-1.0F, -1.0F, 0.0F, 0.0F, 0.0F, 1.0F, -1.0F, 0.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F};
      float[] var1 = var2;
      this.mTriangleVertices = ByteBuffer.allocateDirect(var1.length * this.floatSizeBytes).order(ByteOrder.nativeOrder()).asFloatBuffer();
      ((Buffer)this.mTriangleVertices.put(var1)).position(0);
      Matrix.setIdentityM(this.mSTMatrix, 0);
   }

   public final int getTextureId() {
      return this.mTextureID;
   }

   public final void drawFrame(@NotNull SurfaceTexture var1) {
      Intrinsics.checkNotNullParameter(var1, "st");
      this.checkGlError("onDrawFrame start");
      var1.getTransformMatrix(this.mSTMatrix);
      GLES20.glClearColor(0.0F, 1.0F, 0.0F, 1.0F);
      GLES20.glClear(16640);
      GLES20.glUseProgram(this.mProgram);
      this.checkGlError("glUseProgram");
      GLES20.glActiveTexture(33984);
      GLES20.glBindTexture(36197, this.mTextureID);
      ((Buffer)this.mTriangleVertices).position(this.triangleVerticesDataPosOffset);
      GLES20.glVertexAttribPointer(this.maPositionHandle, 3, 5126, false, this.triangleVerticesDataStrideBytes, this.mTriangleVertices);
      this.checkGlError("glVertexAttribPointer maPosition");
      GLES20.glEnableVertexAttribArray(this.maPositionHandle);
      this.checkGlError("glEnableVertexAttribArray maPositionHandle");
      ((Buffer)this.mTriangleVertices).position(this.triangleVerticesDataUvOffset);
      GLES20.glVertexAttribPointer(this.maTextureHandle, 2, 5126, false, this.triangleVerticesDataStrideBytes, this.mTriangleVertices);
      this.checkGlError("glVertexAttribPointer maTextureHandle");
      GLES20.glEnableVertexAttribArray(this.maTextureHandle);
      this.checkGlError("glEnableVertexAttribArray maTextureHandle");
      Matrix.setIdentityM(this.mMVPMatrix, 0);
      GLES20.glUniformMatrix4fv(this.muMVPMatrixHandle, 1, false, this.mMVPMatrix, 0);
      GLES20.glUniformMatrix4fv(this.muSTMatrixHandle, 1, false, this.mSTMatrix, 0);
      GLES20.glDrawArrays(5, 0, 4);
      this.checkGlError("glDrawArrays");
      GLES20.glFinish();
   }

   public final void surfaceCreated() {
      this.mProgram = this.createProgram();
      if (this.mProgram == 0) {
         throw new RuntimeException("failed creating program");
      }

      this.maPositionHandle = GLES20.glGetAttribLocation(this.mProgram, "aPosition");
      this.checkGlError("glGetAttribLocation aPosition");
      if (this.maPositionHandle == -1) {
         throw new RuntimeException("Could not get attrib location for aPosition");
      }

      this.maTextureHandle = GLES20.glGetAttribLocation(this.mProgram, "aTextureCoord");
      this.checkGlError("glGetAttribLocation aTextureCoord");
      if (this.maTextureHandle == -1) {
         throw new RuntimeException("Could not get attrib location for aTextureCoord");
      }

      this.muMVPMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uMVPMatrix");
      this.checkGlError("glGetUniformLocation uMVPMatrix");
      if (this.muMVPMatrixHandle == -1) {
         throw new RuntimeException("Could not get attrib location for uMVPMatrix");
      }

      this.muSTMatrixHandle = GLES20.glGetUniformLocation(this.mProgram, "uSTMatrix");
      this.checkGlError("glGetUniformLocation uSTMatrix");
      if (this.muSTMatrixHandle == -1) {
         throw new RuntimeException("Could not get attrib location for uSTMatrix");
      }

      int[] var1 = new int[1];
      GLES20.glGenTextures(1, var1, 0);
      this.mTextureID = var1[0];
      GLES20.glBindTexture(36197, this.mTextureID);
      this.checkGlError("glBindTexture mTextureID");
      GLES20.glTexParameterf(36197, 10241, 9728.0F);
      GLES20.glTexParameterf(36197, 10240, 9729.0F);
      GLES20.glTexParameteri(36197, 10242, 33071);
      GLES20.glTexParameteri(36197, 10243, 33071);
      this.checkGlError("glTexParameter");
   }

   private final int loadShader(int var1, String var2) {
      int var3 = GLES20.glCreateShader(var1);
      this.checkGlError("glCreateShader type=" + var1);
      GLES20.glShaderSource(var3, var2);
      GLES20.glCompileShader(var3);
      int[] var4 = new int[1];
      GLES20.glGetShaderiv(var3, 35713, var4, 0);
      if (var4[0] == 0) {
         GLES20.glDeleteShader(var3);
         var3 = 0;
      }

      return var3;
   }

   private final int createProgram() {
      int var1 = this.loadShader(35633, this.vertexShader);
      if (var1 == 0) {
         return 0;
      }

      int var2 = this.loadShader(35632, this.fragmentShader);
      if (var2 == 0) {
         return 0;
      }

      int var3 = GLES20.glCreateProgram();
      this.checkGlError("glCreateProgram");
      if (var3 == 0) {
         return 0;
      }

      GLES20.glAttachShader(var3, var1);
      this.checkGlError("glAttachShader");
      GLES20.glAttachShader(var3, var2);
      this.checkGlError("glAttachShader");
      GLES20.glLinkProgram(var3);
      int[] var4 = new int[1];
      GLES20.glGetProgramiv(var3, 35714, var4, 0);
      if (var4[0] != 1) {
         GLES20.glDeleteProgram(var3);
         var3 = 0;
      }

      return var3;
   }

   public final void checkGlError(@NotNull String var1) {
      Intrinsics.checkNotNullParameter(var1, "op");
      int var2 = 0;
      int var3 = GLES20.glGetError();
      int var4 = var3;
      int var5 = 0;
      var2 = var4;
      if (var3 != 0) {
         throw new RuntimeException(var1 + ": glError " + var2);
      }
   }
}
