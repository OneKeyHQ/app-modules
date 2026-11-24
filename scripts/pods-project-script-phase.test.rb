require 'cocoapods'
require 'tmpdir'
require_relative '../example/react-native/ios/pods-project-script-phase'

Dir.mktmpdir do |dir|
  path = File.join(dir, 'Pods.xcodeproj')
  project = Pod::Project.new(path)
  target = project.new_target(:static_library, 'Fixture', :ios, '16.4')
  root_uuid = project.root_object.uuid
  project.instance_variable_get(:@available_uuids).clear
  Pod::Installer::TargetUUIDGenerator.new([project]).generate!

  phase = ensure_pods_script_phase(target, 'Bundle SDK icon fixtures')
  abort 'The script phase reused the project root UUID' if phase.uuid == root_uuid
  abort 'The script phase was duplicated' unless ensure_pods_script_phase(target, phase.name) == phase
  project.save

  reopened = Xcodeproj::Project.open(path)
  actual = reopened.targets.fetch(0).shell_script_build_phases.map(&:name)
  abort 'The script phase was not saved' unless actual == [phase.name]
  puts 'CocoaPods script phase regression passed'
end
