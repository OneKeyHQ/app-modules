#import "ICMAssets.h"
#import "ICMUtils+Integration.h"
@implementation ICMAssets (OriginalImageGetters)
+ (UIImage *)streamingCursorImage { return [self imageNamed:@"streaming_cursor"]; }
+ (UIImage *)messageFailedImage { return [self imageNamed:@"message_failed"]; }
+ (UIImage *)intercomLogoImage { return [self tintableImageNamed:@"intercom_logo"]; }
+ (UIImage *)poweredByFinAIAgentIcon { return [self tintableImageNamed:@"powered_by_fin_ai_agent_icon"]; }
+ (UIImage *)closeImage { return [self tintableImageNamed:@"close"]; }
+ (UIImage *)closeImageWithBackground { return [self imageNamed:@"close_with_background"]; }
+ (UIImage *)closeChevronImage { return [self tintableImageNamed:@"close_chevron"]; }
+ (UIImage *)linkIconImage { return [self imageNamed:@"link_icon"]; }
+ (UIImage *)articleCitationIconImage { return [self imageNamed:@"article_citation_icon"]; }
+ (UIImage *)plusIcon { return [self tintableSystemImageNamed:@"plus"]; }
+ (UIImage *)smallPlusIcon { return [self tintableImageNamed:@"icon_plus"]; }
+ (UIImage *)startConversationImage { return [self tintableImageNamed:@"startConversationIcon"]; }
+ (UIImage *)submitButtonBackgroundImage { return [self tintableImageNamed:@"submit_button_background"]; }
+ (UIImage *)submitButtonBackgroundImageClear { return [self imageNamed:@"submit_button_background_clear"]; }
+ (UIImage *)submitImage { return [self tintableImageNamed:@"icon_submit"]; }
+ (UIImage *)pickerSelector { return [self imageNamed:@"picker_selector"]; }
+ (UIImage *)dateSelector { return [self imageNamed:@"date_selector"]; }
+ (UIImage *)greenCheckImage { return [self tintableImageNamed:@"icon_green_check"]; }
+ (UIImage *)buttonLoadingImage { return [self tintableImageNamed:@"icon_loading"]; }
+ (UIImage *)screenActionCheckmark { return [self tintableImageNamed:@"screen_action_checkmark"]; }
+ (UIImage *)carouselLoadingImage { return [self imageNamed:@"carousel_loading_state"]; }
+ (UIImage *)articleLoadingImage { return [self imageNamed:@"article_loading_state"]; }
+ (UIImage *)launcherImage { return [self tintableImageNamed:@"launcher"]; }
+ (UIImage *)infoIcon { return [self imageNamed:@"info_icon"]; }
+ (UIImage *)snoozeIcon { return [self tintableImageNamed:@"snooze_icon"]; }
+ (UIImage *)sendIcon { return [self tintableImageNamed:@"send_icon"]; }
+ (UIImage *)copiedIcon { return [self tintableImageNamed:@"copied_icon"]; }
+ (UIImage *)expandIcon { return [self tintableImageNamed:@"expand_icon"]; }
+ (UIImage *)closeIcon { return [self tintableImageNamed:@"close_icon"]; }
+ (UIImage *)homescreenFailedImage { return [self tintableImageNamed:@"homescreenFailed"]; }
+ (UIImage *)textInputIcon { return [self imageNamed:@"icon_text_input"]; }
+ (UIImage *)textInputSelectedIcon { return [self tintableImageNamed:@"icon_text_input"]; }
+ (UIImage *)photoInputIcon { return [self imageNamed:@"icon_photo_input"]; }
+ (UIImage *)photoInputSelectedIcon { return [self tintableImageNamed:@"icon_photo_input"]; }
+ (UIImage *)expandPhotosIcon { return [self tintableImageNamed:@"icon_expand_photos"]; }
+ (UIImage *)videoInputIcon { return [self imageNamed:@"icon_video_input"]; }
+ (UIImage *)cameraIcon { return [self imageNamed:@"camera_icon"]; }
+ (UIImage *)attachmentIcon { return [self imageNamed:@"icon_attachment"]; }
+ (UIImage *)gif { return [self imageNamed:@"gif"]; }
+ (UIImage *)paperclip { return [self imageNamed:@"paperclip"]; }
+ (UIImage *)mic { return [self imageNamed:@"mic"]; }
+ (UIImage *)footerLinkIcon { return [self tintableImageNamed:@"footer_link_icon"]; }
+ (UIImage *)teammateTimeIcon { return [self tintableImageNamed:@"teammate_location"]; }
+ (UIImage *)searchIcon { return [self tintableImageNamed:@"search_icon"]; }
+ (UIImage *)composeIcon { return [self tintableImageNamed:@"compose"]; }
+ (UIImage *)inboxIcon { return [self tintableImageNamed:@"inbox_icon"]; }
+ (UIImage *)inboxNavIcon { return [self tintableImageNamed:@"nav_icon_inbox"]; }
+ (UIImage *)chevronNavIcon { return [self tintableImageNamed:@"nav_icon_chevron"]; }
+ (UIImage *)closeNavIcon { return [self tintableImageNamed:@"nav_icon_close"]; }
+ (UIImage *)menuNavIcon { return [self tintableImageNamed:@"nav_icon_menu"]; }
+ (UIImage *)searchBrowseNavIcon { return [self tintableImageNamed:@"nav_icon_search_browse"]; }
+ (UIImage *)informationIcon { return [self tintableImageNamed:@"information_icon"]; }
+ (UIImage *)messagesLoadingImage { return [self imageNamed:@"messages_loading_state"]; }
+ (UIImage *)starFillImage { return [self imageNamed:@"star_fill"]; }
+ (UIImage *)starUnselectedImage { return [self imageNamed:@"star_unselected_border"]; }
+ (UIImage *)starSelectedImage { return [self imageNamed:@"star_selected_border"]; }
+ (UIImage *)externalLinkIcon { return [self tintableImageNamed:@"external_link_icon"]; }
+ (UIImage *)surveyLoadingImage { return [self imageNamed:@"survey_loading_state"]; }
+ (UIImage *)bellIcon { return [self tintableImageNamed:@"bell"]; }
+ (UIImage *)descriptionIcon { return [self tintableImageNamed:@"description"]; }
+ (UIImage *)ticketIcon { return [self tintableImageNamed:@"ticket_icon"]; }
+ (UIImage *)ticketSubmittedIcon { return [self tintableImageNamed:@"ticket_submitted_icon"]; }
+ (UIImage *)ticketWaitingOnYouIcon { return [self tintableImageNamed:@"ticket_waiting_on_you_icon"]; }
+ (UIImage *)ticketResolvedIcon { return [self tintableImageNamed:@"ticket_resolved_icon"]; }
+ (UIImage *)stopwatchIcon { return [self tintableImageNamed:@"stopwatch"]; }
+ (UIImage *)successImage { return [self imageNamed:@"success_image"]; }
+ (UIImage *)trashIcon { return [self tintableSystemImageNamed:@"trash"]; }
+ (UIImage *)playIcon { return [self tintableImageNamed:@"play_icon@3x"]; }
+ (UIImage *)messageSpaceIcon { return [self tintableImageNamed:@"message_space_icon"]; }
+ (UIImage *)articlesIcon { return [self tintableImageNamed:@"articles"]; }
+ (UIImage *)helpIcon { return [self tintableImageNamed:@"help_icon"]; }
+ (UIImage *)homeExternalLinkIcon { return [self tintableImageNamed:@"home_external_link_icon"]; }
+ (UIImage *)roundedChevronDown { return [self tintableImageNamed:@"rounded_chevron_down"]; }
+ (UIImage *)finNameIcon { return [self imageNamed:@"icon_fin_name"]; }
+ (UIImage *)booleanButtonImage { return [[self imageNamed:@"boolean_button"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)booleanButtonImageSelected { return [[self imageNamed:@"boolean_button_selected"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)booleanButtonLeftImage { return [[self imageNamed:@"boolean_button_left"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)booleanButtonLeftImageSelected { return [[self imageNamed:@"boolean_button_left_selected"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)booleanButtonRightImage { return [[self imageNamed:@"boolean_button_right"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)booleanButtonRightImageSelected { return [[self imageNamed:@"boolean_button_right_selected"] resizableImageWithCapInsets:UIEdgeInsetsMake(4, 4, 0, 4)]; }
+ (UIImage *)copyIcon { return [self tintableImageNamed:@"copy_icon"]; }
+ (UIImage *)createMessageIcon { return [UIImage systemImageNamed:@"square.and.pencil"]; }
+ (UIImage *)tintableSystemImageNamed:(NSString *)name { return [[UIImage systemImageNamed:name] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate]; }
+ (NSURL *)urlForFilename:(NSString *)filename {
    NSBundle *bundle = [ICMUtils intercomBundle];
    if (bundle == nil) {
        NSLog(@"[Intercom] ERROR - 💥 There was an error trying to access the Intercom.bundle but it is not present. Check to see that it is included in your copy resources build phase.");
        return nil;
    }
    NSURL *url = [bundle URLForResource:[NSString stringWithFormat:@"sound/%@", filename] withExtension:@"caf"];
    if (url == nil) NSLog(@"[Intercom] ERROR - 💥 Unable to play sounds as the file inside the Intercom.bundle can't be found.");
    return url;
}
+ (NSURL *)soundForMessageSending { return [ICMAssets urlForFilename:@"message_sending"]; }
+ (NSURL *)soundForMessageSendingFailure { return [ICMAssets urlForFilename:@"message_failed"]; }
+ (NSURL *)soundForIncomingAdminMessage { return [ICMAssets urlForFilename:@"admin_reply_delivered"]; }
+ (NSURL *)soundForIncomingOperatorMessage { return [ICMAssets urlForFilename:@"message_operator"]; }
@end
