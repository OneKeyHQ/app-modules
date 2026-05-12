Gem::Specification.new do |s|
  s.name        = 'kconv-shim'
  s.version     = '0.0.1'
  s.summary     = 'Empty Kconv module to satisfy CFPropertyList require on Ruby 3.4+'
  s.description = <<~DESC
    Ruby 3.4 removed the `kconv` stdlib but the cocoapods toolchain pulls
    CFPropertyList 3.x (capped by xcodeproj `< 4.0`), which has a leftover
    `require 'kconv'` at file head — even though it never calls any Kconv
    method. This shim provides an empty kconv.rb so the require succeeds
    and `bundle exec pod install` works on system Ruby 3.4+.
  DESC
  s.authors     = ['OneKey']
  s.files       = ['lib/kconv.rb']
  s.license     = 'MIT'
end
