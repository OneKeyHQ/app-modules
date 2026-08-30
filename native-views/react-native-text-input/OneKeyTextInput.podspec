require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "OneKeyTextInput"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]
  s.platforms    = { :ios => "15.5" }
  s.source       = { :git => "https://github.com/OneKeyHQ/app-modules.git", :tag => "#{s.version}" }
  s.source_files = "ios/**/*.{h,m,mm}"
  s.frameworks   = "UIKit", "UniformTypeIdentifiers"

  s.dependency "React-Core"
end
