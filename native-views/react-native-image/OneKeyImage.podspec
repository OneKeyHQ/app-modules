require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "OneKeyImage"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]
  s.platforms    = { :ios => min_ios_version_supported }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules.git", :tag => "#{s.version}" }

  s.source_files = ["ios/**/*.{swift,m,mm}", "cpp/**/*.{hpp,cpp}"]
  s.exclude_files = "ios/tests/**/*"

  s.dependency "React-jsi"
  s.dependency "React-callinvoker"
  s.dependency "SDWebImage", "~> 5.21.7"
  s.dependency "SDWebImageSVGCoder", "~> 1.7.0"
  s.dependency "SDWebImageWebPCoder", "~> 0.14.6"
  s.dependency "Skeleton", package["version"]

  load "nitrogen/generated/ios/OneKeyImage+autolinking.rb"
  add_nitrogen_files(s)
  install_modules_dependencies(s)

  s.test_spec "Tests" do |test_spec|
    test_spec.source_files = "ios/tests/**/*.swift"
  end
end
