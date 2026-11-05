package io.intercom.android.sdk.blocks;

class Video$1$1 implements Runnable {
   // $VF: synthetic field
   final String val$videoUrl;
   // $VF: synthetic field
   final String val$thumbnailUrl;
   // $VF: synthetic field
   final Video$1 this$1;

   Video$1$1(Video$1 var1, String var2, String var3) {
      this.this$1 = var1;
      this.val$videoUrl = var2;
      this.val$thumbnailUrl = var3;
   }

   @Override
   public void run() {
      this.this$1.this$0.createThumbnail(this.this$1.val$previewView, this.val$videoUrl, this.val$thumbnailUrl);
   }
}
