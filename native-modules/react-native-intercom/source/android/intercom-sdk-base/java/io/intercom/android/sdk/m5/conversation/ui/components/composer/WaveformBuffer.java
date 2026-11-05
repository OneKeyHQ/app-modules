package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\b\u0001\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\fJ\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\f0\u0014J\u0016\u0010\u0015\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0017J\u0006\u0010\u0019\u001a\u00020\u0003J\u000e\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\u0003R\u001e\u0010\u0007\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0003@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0018\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000bX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\rR\u000e\u0010\u000e\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001c",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformBuffer;",
         "",
         "initialCapacity",
         "",
         "<init>",
         "(I)V",
         "value",
         "capacity",
         "getCapacity",
         "()I",
         "elements",
         "",
         "Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformElement;",
         "[Lio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformElement;",
         "head",
         "size",
         "addElement",
         "",
         "element",
         "getAllElements",
         "",
         "cleanupOldElements",
         "currentTime",
         "",
         "maxAge",
         "getCurrentSize",
         "resizeBuffer",
         "newCapacity",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
@SourceDebugExtension(
   "SMAP\nSoundWaveVisualization.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SoundWaveVisualization.kt\nio/intercom/android/sdk/m5/conversation/ui/components/composer/WaveformBuffer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,591:1\n1#2:592\n*E\n"
)
public final class WaveformBuffer {
   private int capacity;
   @NotNull
   private WaveformElement[] elements;
   private int head;
   private int size;
   public static final int $stable;

   static { $stable = 8; }

   public WaveformBuffer(int var1) {
      this.capacity = var1;
      int var2 = 0;
      int var3 = this.capacity;
      WaveformElement[] var4 = new WaveformElement[var3];
      WaveformBuffer var6 = this;

      while (var2 < var3) {
         int var5 = var2;
         var4[var5] = null;
         var2++;
      }

      var6.elements = var4;
   }

   // $VF: synthetic method
   public WaveformBuffer(int var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (int) (70) : var1));
   }

   public final int getCapacity() {
      return this.capacity;
   }

   public final void addElement(@NotNull WaveformElement var1) {
      Intrinsics.checkNotNullParameter(var1, "element");
      this.elements[this.head] = var1;
      this.head = (this.head + 1) % this.capacity;
      if (this.size < this.capacity) {
         int var2 = this.size++;
      }
   }

   @NotNull
   public final List getAllElements() {
      List var1 = new ArrayList();
      int var2 = 0;

      for (int var3 = this.size; var2 < var3; var2++) {
         int var4 = (this.head - this.size + var2 + this.capacity) % this.capacity;
         WaveformElement var10000 = this.elements[var4];
         if (this.elements[var4] != null) {
            WaveformElement var5 = var10000;
            int var6 = 0;
            var1.add(var5);
         }
      }

      return var1;
   }

   public final void cleanupOldElements(long var1, long var3) {
      int var5 = (this.head - this.size + this.capacity) % this.capacity;
      int var6 = 0;
      int var7 = 0;

      for (int var8 = this.size; var7 < var8; var7++) {
         int var9 = (this.head - this.size + var7 + this.capacity) % this.capacity;
         WaveformElement var10 = this.elements[var9];
         if (var10 != null && var1 - var10.getTimestamp() <= var3) {
            if (var9 != var5) {
               this.elements[var5] = var10;
               this.elements[var9] = null;
            }

            var5 = (var5 + 1) % this.capacity;
            var6++;
         } else {
            this.elements[var9] = null;
         }
      }

      this.size = var6;
      this.head = var5;
   }

   public final int getCurrentSize() {
      return this.size;
   }

   public final void resizeBuffer(int var1) {
      if (var1 != this.capacity) {
         WaveformElement[] var2 = this.elements;
         int var3 = this.capacity;
         int var4 = this.head;
         int var5 = this.size;
         this.capacity = var1;
         int var6 = 0;
         int var7 = this.capacity;
         WaveformElement[] var8 = new WaveformElement[var7];
         WaveformBuffer var12 = this;

         while (var6 < var7) {
            int var9 = var6;
            var8[var9] = null;
            var6++;
         }

         var12.elements = var8;
         this.head = 0;
         this.size = 0;
         var6 = Math.min(var5, var1);
         var7 = var5 - var6;

         for (int var15 = 0; var15 < var6; var15++) {
            int var16 = (var4 - var5 + var7 + var15 + var3) % var3;
            WaveformElement var10000 = var2[var16];
            if (var2[var16] != null) {
               WaveformElement var10 = var10000;
               int var11 = 0;
               this.addElement(var10);
            }
         }
      }
   }

   public WaveformBuffer() {
      this(0, 1, null);
   }
}
