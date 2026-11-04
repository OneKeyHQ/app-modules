require 'digest'
require 'fileutils'
require 'tmpdir'

helper, fixture = ARGV
raise 'Usage: ruby patch_test.rb /unpacked/sdk/ios/apply_emascurl_no_replay.rb /clean/EMASCurl' unless helper && fixture
require File.expand_path(helper)

def assert(condition, message)
  raise message unless condition
end

def rejects_unchanged(source)
  before = [File.binread(source), File.mtime(source)]
  rejected = false
  begin
    yield
  rescue StandardError
    rejected = true
  end
  assert(rejected, 'Expected a rejected patch operation')
  assert(before == [File.binread(source), File.mtime(source)], 'Rejected operation modified the target')
end

Dir.mktmpdir('onekey-patch-test-') do |directory|
  FileUtils.mkdir_p(File.join(directory, 'EMASCurl'))
  %w[EMASCurlProtocol.m EMASCurlProtocol.h].each do |name|
    FileUtils.cp(File.join(fixture, 'EMASCurl', name), File.join(directory, 'EMASCurl', name))
  end
  source = File.join(directory, OneKeyEmascurlNoReplay::SOURCE)
  rejects_unchanged(source) do
    OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.5', verify_only: true)
  end
  puts 'PASS unpatched verify rejects without writing'
  rejects_unchanged(source) do
    OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.6')
  end
  puts 'PASS unknown version rejects without writing'
  assert(OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.5') == :applied, 'Clean apply failed')
  assert(Digest::SHA256.file(source).hexdigest == OneKeyEmascurlNoReplay::PATCHED_SHA, 'Wrong patched contents')
  puts 'PASS clean apply matches the pinned patched hash'
  before = [File.binread(source), File.mtime(source)]
  assert(OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.5') == :already_applied, 'Reapply failed')
  assert(OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.5', verify_only: true) == :verified, 'Verify failed')
  assert(before == [File.binread(source), File.mtime(source)], 'Reapply or verify rewrote the target')
  puts 'PASS reapply and verify preserve contents and mtime'
  File.chmod(0o644, source)
  File.open(source, 'ab') { |file| file.write("\n// unexpected source drift\n") }
  [false, true].each do |verify_only|
    rejects_unchanged(source) do
      OneKeyEmascurlNoReplay.apply!(pod_root: directory, version: '1.5.5', verify_only: verify_only)
    end
  end
  puts 'PASS drifted apply and verify both reject without writing'
  manifest = File.join(directory, 'Manifest.lock')
  File.write(manifest, "PODS:\n  - EMASCurl (1.5.5):\n    - EMASCurl/HTTP2 (= 1.5.5)\n  - EMASCurl/HTTP2 (1.5.5)\n")
  assert(OneKeyEmascurlNoReplay.version_from_manifest(manifest) == '1.5.5', 'Manifest version parsing failed')
  puts 'PASS resolved manifest version is read without modifying the dependency'
end
