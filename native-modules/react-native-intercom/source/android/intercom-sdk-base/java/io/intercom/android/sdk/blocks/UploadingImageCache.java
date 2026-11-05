package io.intercom.android.sdk.blocks;

import android.net.Uri;
import io.intercom.android.sdk.GalleryImage;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;

public class UploadingImageCache {
   private final Map localImageUris;

   public UploadingImageCache() {
      this(new HashMap());
   }

   UploadingImageCache(Map var1) {
      this.localImageUris = var1;
   }

   @Nullable
   Uri getLocalImageUriForRemoteUrl(String var1) {
      return (Uri)this.localImageUris.get(var1);
   }

   public void put(String var1, GalleryImage var2) {
      this.localImageUris.put(var1, var2.getUri());
   }
}
