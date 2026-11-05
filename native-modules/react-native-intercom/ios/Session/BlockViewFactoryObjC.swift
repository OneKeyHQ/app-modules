import UIKit

@objc(_TtC18IntercomSDKPrivate20BlockViewFactoryObjC)
class BlockViewFactoryObjC: NSObject {
    override init() { super.init() }

    @objc(viewFor:displayContext:blockDelegate:)
    class func view(for block: ITBBlock, displayContext: ImageBlockDisplayContext, blockDelegate: OutboundContentBlockDelegate?) -> UIView? {
        let part = ICMConversationPart()
        part.blocks = [block]
        return BlockViewFactory.getViewsForBlocks(in: part, delegate: blockDelegate, citationDelegate: nil, maxWidth: nil, imageDisplayContext: displayContext).first
    }
}
