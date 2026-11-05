import Foundation

// The original static function survives; its deleted nominal namespace shape is not an ABI claim.
enum VideoHTMLTemplateFactory {
    static func getHTMLTemplate(provider: String, videoId: String) -> String? {
        let format: String
        switch provider {
        case "youtube": format = #"<!DOCTYPE html><html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><div id="player"></div><script>var tag = document.createElement('script');tag.src = "https://www.youtube.com/iframe_api";var firstScriptTag = document.getElementsByTagName('script')[0];firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);var player;function onYouTubeIframeAPIReady() {player = new YT.Player('player', {videoId: '%@',width: '100%%',height: '100%%',playerVars: {modestbranding: 0,autohide: 1,controls: 1,showinfo: 0,}});}</script></body></html>"#
        case "vimeo": format = #"<html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><iframe src="https://player.vimeo.com/video/%@" width="100%%" height="100%%" frameborder="0" scrolling="no"</iframe></body></html>"#
        case "wistia": format = #"<html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><iframe class="wistia_embed" name="wistia_embed" src="https://fast.wistia.net/embed/iframe/%@?volumeControl=true&fullscreenButton=true&controlsVisibleOnLoad=true allowtransparency="true" frameborder="0" scrolling="no" width="100%%" height="100%%"></iframe></body></html>"#
        case "streamio": format = #"<html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><iframe class="streamio_embed" name="streamio_embed" src="https://streamio.com/api/v1/videos/%@/public_show" frameborder="0" scrolling="no" width="100%%" height="100%%"></iframe></body></html>"#
        case "loom": format = #"<html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><iframe src="https://www.loom.com/embed/%@" width="100%%" height="100%%" frameborder="0" webkitallowfullscreen allowfullscreen></iframe></body></html>"#
        case "vidyard": format = #"<html style="height:100%%;margin:0;"><head><meta name="viewport" content="initial-scale=1.0,user-scalable=no"/></head><body style="margin:0;height:100%%;"><iframe class="vidyard_iframe" src="https://play.vidyard.com/%@" width="100%%" height="100%%" frameborder="0" webkitallowfullscreen allowfullscreen></iframe></body></html>"#
        default: return nil
        }
        return String(format: format, videoId)
    }
}
