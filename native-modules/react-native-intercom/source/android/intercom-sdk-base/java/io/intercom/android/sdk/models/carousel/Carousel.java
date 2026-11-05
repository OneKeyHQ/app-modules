package io.intercom.android.sdk.models.carousel;

import com.google.auto.value.AutoValue;
import java.util.List;

@AutoValue
public abstract class Carousel {
   public static final Carousel$Builder NULL_BUILDER = new Carousel$Builder();
   public static final Carousel NULL = NULL_BUILDER.build();
   public static final int ENTITY_TYPE = 44;

   static Carousel create(List var0, boolean var1, String var2) {
      return new AutoValue_Carousel(var0, var1, var2);
   }

   public abstract List getScreens();

   public abstract boolean isDismissible();

   public abstract String getInstanceId();
}
