package cn.jpush.android.api;

public class CustomMessage {
   public String messageId;
   public String extra;
   public String message;
   public String contentType;
   public String title;
   public String senderId;
   public String appId;
   public byte platform;

   @Override
   public String toString() {
      return "CustomMessage{messageId='"
         + this.messageId
         + '\''
         + ", extra='"
         + this.extra
         + '\''
         + ", message='"
         + this.message
         + '\''
         + ", contentType='"
         + this.contentType
         + '\''
         + ", title='"
         + this.title
         + '\''
         + ", senderId='"
         + this.senderId
         + '\''
         + ", appId='"
         + this.appId
         + '\''
         + ", platform='"
         + this.platform
         + '\''
         + '}';
   }
}
