require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "AesCrypto"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-aes-crypto.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"

  s.frameworks = ['Security', 'CryptoKit']
  # Workaround: `AesCrypto.mm` imports the generated `AesCrypto-Swift.h`
  # bridging header for the `AesCryptoGcm` Swift helper. With Xcode 15+
  # explicit Swift modules turned on, that bridging header conflicts with
  # the explicit module map for `AesCryptoSpec` (the TurboModule C++ spec),
  # producing module-map redefinition errors at pod build time. Disabling
  # explicit modules on this pod target is the same pattern other RN modules
  # mixing ObjC++ + Swift use; revisit once the Swift toolchain handles the
  # combination natively.
  s.pod_target_xcconfig = {
    'SWIFT_ENABLE_EXPLICIT_MODULES' => 'NO'
  }

  install_modules_dependencies(s)
end
