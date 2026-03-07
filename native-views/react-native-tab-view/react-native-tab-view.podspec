require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-tab-view"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.ios.deployment_target = "15.1"
  s.module_name  = "TabViewModule"

  s.source       = { :git => "https://github.com/nicholasxuu/app-modules.git", :tag => "#{s.version}" }

  s.source_files = [
    "ios/**/*.{swift}",
    "ios/**/*.{h,m,mm,cpp}",
  ]

  s.static_framework = true

  s.subspec "common" do |ss|
    ss.source_files         = "common/cpp/**/*.{cpp,h}"
    ss.pod_target_xcconfig  = { "HEADER_SEARCH_PATHS" => "\"$(PODS_TARGET_SRCROOT)/common/cpp\"" }
  end

  s.pod_target_xcconfig = {
    'PRODUCT_MODULE_NAME' => 'TabViewModule',
    'DEFINES_MODULE' => 'YES'
  }

  install_modules_dependencies(s)
end
