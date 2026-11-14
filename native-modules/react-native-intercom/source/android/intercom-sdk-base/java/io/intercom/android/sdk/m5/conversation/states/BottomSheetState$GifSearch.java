package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState$GifSearch;",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "gifs",
         "",
         "Lio/intercom/android/sdk/blocks/lib/models/Block;",
         "<init>",
         "(Ljava/util/List;)V",
         "getGifs",
         "()Ljava/util/List;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class BottomSheetState$GifSearch implements BottomSheetState {
   @NotNull
   private final List gifs;
   public static final int $stable;

   static { $stable = 8; }

   public BottomSheetState$GifSearch(@NotNull List var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "gifs");
      this.gifs = var1;
   }

   @NotNull
   public final List getGifs() {
      return this.gifs;
   }
}
