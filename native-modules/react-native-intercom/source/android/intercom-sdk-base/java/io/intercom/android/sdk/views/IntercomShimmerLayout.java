package io.intercom.android.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import androidx.annotation.RestrictTo;
import androidx.annotation.RestrictTo.Scope;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.facebook.shimmer.Shimmer.AlphaHighlightBuilder;

@RestrictTo(Scope.LIBRARY)
public class IntercomShimmerLayout extends ShimmerFrameLayout {
   private static Shimmer buildShimmerConfig(boolean var0) {
      return ((AlphaHighlightBuilder)((AlphaHighlightBuilder)((AlphaHighlightBuilder)((AlphaHighlightBuilder)((AlphaHighlightBuilder)((AlphaHighlightBuilder)new AlphaHighlightBuilder()
                           .setTilt(0.0F))
                        .setIntensity(0.01F))
                     .setDuration(1500L))
                  .setHighlightAlpha(0.6F))
               .setRepeatDelay(100L))
            .setAutoStart(var0))
         .build();
   }

   public IntercomShimmerLayout(Context var1) {
      this(var1, true);
   }

   public IntercomShimmerLayout(Context var1, boolean var2) {
      super(var1);
      this.setShimmer(buildShimmerConfig(var2));
   }

   public IntercomShimmerLayout(Context var1, AttributeSet var2) {
      super(var1, var2);
      this.setShimmer(buildShimmerConfig(true));
   }

   public IntercomShimmerLayout(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
      this.setShimmer(buildShimmerConfig(true));
   }
}
