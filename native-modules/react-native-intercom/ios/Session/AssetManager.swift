// Original asset data; approved source resource relocation: docs/IOS_PROVENANCE.md.
import UIKit

@objc(_TtC18IntercomSDKPrivate12AssetManager)
class AssetManager: NSObject {
    @objc(imageWithNamed:)
    class func image(named name: String) -> UIImage? {
        guard let bundle = ObjectIdentifier(self) == ObjectIdentifier(AssetManager.self) ? ICMUtils.intercomBundle() : Bundle(for: self) else { return nil }
        return UIImage(named: name, in: bundle, compatibleWith: nil)
    }

    @objc(dataAssetWithNamed:)
    class func dataAsset(named name: String) -> NSDataAsset? {
        guard let bundle = ObjectIdentifier(self) == ObjectIdentifier(AssetManager.self) ? ICMUtils.intercomBundle() : Bundle(for: self) else { return nil }
        return NSDataAsset(name: name, bundle: bundle)
    }
}
