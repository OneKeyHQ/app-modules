require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "ReactNativeImageCropPicker"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules/react-native-image-crop-picker.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{h,m,mm}",
    "cpp/**/*.{hpp,cpp}",
  ]

  # Vendored TOCropViewController 3.2.0 with the OK-51551 rotation fix, plus
  # color hooks on TOCropOverlayView. Only its TOCropView is used, hosted by
  # ImageCropperViewController. Its headers are public so the Swift sources can
  # use it. It replaces the standalone TOCropViewController pod, which must not
  # be installed alongside.
  s.public_header_files = ["ios/TOCropViewController/**/*.h"]
  # TOCropViewController looks up its strings in a bundle with exactly this name.
  s.resource_bundles = {
    "TOCropViewControllerBundle" => ["ios/TOCropViewController/Resources/**/*.{lproj,xcprivacy}"],
  }
  s.frameworks = "PhotosUI", "UniformTypeIdentifiers"

  s.dependency 'React-jsi'
  s.dependency 'React-callinvoker'
  s.dependency 'ReactNativeNativeLogger'

  load 'nitrogen/generated/ios/ReactNativeImageCropPicker+autolinking.rb'
  add_nitrogen_files(s)

  install_modules_dependencies(s)
end
