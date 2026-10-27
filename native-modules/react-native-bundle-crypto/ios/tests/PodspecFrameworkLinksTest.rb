require 'fileutils'
require 'minitest/autorun'
require 'tmpdir'

# The layout repair runs before CocoaPods defines the package dependencies.
module Pod
  class Spec
    def self.new
    end
  end
end

class PodspecFrameworkLinksTest < Minitest::Test
  LINKS = {
    'Versions/Current' => 'A',
    'Gopenpgp' => 'Versions/Current/Gopenpgp',
    'Headers' => 'Versions/Current/Headers',
    'Modules' => 'Versions/Current/Modules',
    'Resources' => 'Versions/Current/Resources'
  }.freeze

  def setup
    @package = Dir.mktmpdir('bundle-crypto-podspec-')
    source = File.expand_path('../..', __dir__)
    @podspec = File.join(@package, 'ReactNativeBundleCrypto.podspec')
    FileUtils.cp(File.join(source, 'ReactNativeBundleCrypto.podspec'), @podspec)
    FileUtils.cp(File.join(source, 'package.json'), @package)
    @framework = File.join(@package, 'ios/Frameworks/Gopenpgp.xcframework/ios-arm64-maccatalyst/Gopenpgp.framework')
    FileUtils.mkdir_p(File.join(@framework, 'Versions/A'))
    %w[Headers Modules Resources].each do |entry|
      FileUtils.mkdir_p(File.join(@framework, 'Versions/A', entry))
      File.write(File.join(@framework, 'Versions/A', entry, 'content'), entry)
    end
    File.write(File.join(@framework, 'Versions/A/Gopenpgp'), 'packaged binary')
    @original_contents = versioned_contents
  end

  def teardown
    FileUtils.remove_entry(@package)
  end

  def test_restores_missing_links
    load @podspec
    assert_correct_links
  end

  def test_replaces_dangling_links
    LINKS.each_key { |entry| File.symlink('missing', File.join(@framework, entry)) }
    load @podspec
    assert_correct_links
  end

  def test_replaces_links_to_existing_stale_targets
    FileUtils.cp_r(File.join(@framework, 'Versions/A'), File.join(@framework, 'Versions/B'))
    File.symlink('B', File.join(@framework, 'Versions/Current'))
    LINKS.keys.drop(1).each do |entry|
      File.symlink("Versions/B/#{entry}", File.join(@framework, entry))
    end
    load @podspec
    assert_correct_links
  end

  def test_preserves_correct_links_on_repeated_evaluation
    load @podspec
    inodes = LINKS.keys.map { |entry| File.lstat(File.join(@framework, entry)).ino }
    load @podspec
    assert_equal inodes, LINKS.keys.map { |entry| File.lstat(File.join(@framework, entry)).ino }
    assert_correct_links
  end

  def test_preserves_materialized_entries
    FileUtils.cp(File.join(@framework, 'Versions/A/Gopenpgp'), File.join(@framework, 'Gopenpgp'))
    %w[Headers Modules Resources].each do |entry|
      FileUtils.cp_r(File.join(@framework, 'Versions/A', entry), File.join(@framework, entry))
    end
    load @podspec
    LINKS.keys.drop(1).each { |entry| refute File.symlink?(File.join(@framework, entry)) }
    assert_equal 'packaged binary', File.read(File.join(@framework, 'Gopenpgp'))
    assert_equal @original_contents, versioned_contents
  end

  def test_does_nothing_without_the_packaged_catalyst_slice
    FileUtils.remove_entry(File.join(@framework, 'Versions/A'))
    load @podspec
    LINKS.each_key { |entry| refute File.symlink?(File.join(@framework, entry)) }
  end

  private

  def versioned_contents
    Dir.glob(File.join(@framework, 'Versions/A/**/*')).select { |path| File.file?(path) }
       .to_h { |path| [path, File.binread(path)] }
  end

  def assert_correct_links
    LINKS.each do |entry, target|
      destination = File.join(@framework, entry)
      assert File.symlink?(destination), entry
      assert_equal target, File.readlink(destination), entry
      assert File.exist?(destination), entry
    end
    assert_equal @original_contents, versioned_contents
  end
end
