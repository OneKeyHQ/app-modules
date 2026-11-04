require 'digest'
require 'fileutils'
require 'open3'
require 'optparse'
require 'tempfile'
require 'tmpdir'
require 'yaml'

module OneKeyEmascurlNoReplay
  VERSION = '1.5.5'.freeze
  ORIGINAL_SHA = '56969066a34ec9ed8e3cc7510a69ed3f209bac8a8b16713e8c3300185f367258'.freeze
  PATCHED_SHA = '4c59dc1c471bc1cdc94278c91c5122acba5f04bf473045c24fb31ced9d2ad61e'.freeze
  SOURCE = 'EMASCurl/EMASCurlProtocol.m'.freeze
  PATCH = File.join(__dir__, 'patches/emascurl-1.5.5-no-replay.patch').freeze

  def self.version_from_manifest(path)
    manifest = YAML.safe_load(File.read(path), aliases: true)
    versions = Array(manifest.fetch('PODS')).flat_map do |entry|
      entry.is_a?(Hash) ? entry.keys : [entry]
    end.map do |entry|
      match = /\AEMASCurl(?:\/[^ ]+)? \(([^)]+)\)\z/.match(entry)
      match && match[1]
    end.compact.uniq
    raise 'Manifest must resolve exactly one EMASCurl version' unless versions.length == 1
    versions.first
  end

  def self.apply!(pod_root:, version:, verify_only: false)
    raise "Unsupported EMASCurl version: #{version}" unless version.to_s == VERSION
    source = File.join(pod_root, SOURCE)
    header = File.join(pod_root, 'EMASCurl/EMASCurlProtocol.h')
    raise 'EMASCurl header version mismatch' unless File.read(header).include?("#define EMASCURL_SDK_VERSION @\"#{VERSION}\"")
    original = File.binread(source)
    sha = Digest::SHA256.hexdigest(original)
    if sha == PATCHED_SHA
      return verify_only ? :verified : :already_applied
    end
    raise "Unknown EMASCurl source SHA256: #{sha}" unless sha == ORIGINAL_SHA
    raise 'EMASCurl replay patch missing; call the SDK helper from post_install' if verify_only

    # Validate the complete result in isolation before replacing an installed file.
    patched = Dir.mktmpdir('onekey-emascurl-') do |directory|
      candidate = File.join(directory, SOURCE)
      FileUtils.mkdir_p(File.dirname(candidate))
      File.binwrite(candidate, original)
      stdout, stderr, status = Open3.capture3(
        'patch', '-p1', '--batch', '--forward', '--fuzz=0', '--input', PATCH,
        chdir: directory
      )
      raise "EMASCurl patch failed: #{stdout}#{stderr}" unless status.success?
      result = File.binread(candidate)
      raise 'EMASCurl patched SHA256 mismatch' unless Digest::SHA256.hexdigest(result) == PATCHED_SHA
      result
    end

    raise 'EMASCurl source changed while preparing the patch' unless File.binread(source) == original
    mode = File.stat(source).mode & 0o777
    Tempfile.create(['onekey-emascurl-', '.m'], File.dirname(source)) do |file|
      file.binmode
      file.write(patched)
      file.flush
      file.fsync
      file.chmod(mode)
      File.rename(file.path, source)
    end
    :applied
  end
end

if $PROGRAM_NAME == __FILE__
  begin
    options = {}
    OptionParser.new do |parser|
      parser.on('--pod-root PATH') { |value| options[:pod_root] = value }
      parser.on('--version VERSION') { |value| options[:version] = value }
      parser.on('--manifest PATH') { |value| options[:manifest] = value }
      parser.on('--verify-only') { options[:verify_only] = true }
    end.parse!
    raise 'Expected --pod-root and either --version or --manifest' unless options[:pod_root] && (options[:version] || options[:manifest])
    manifest = options.delete(:manifest)
    if manifest
      resolved = OneKeyEmascurlNoReplay.version_from_manifest(manifest)
      raise 'Explicit and manifest versions differ' if options[:version] && options[:version] != resolved
      options[:version] = resolved
    end
    result = OneKeyEmascurlNoReplay.apply!(**options)
    puts "EMASCurl #{OneKeyEmascurlNoReplay::VERSION} no-replay: #{result}"
  rescue StandardError => error
    warn "EMASCurl no-replay: #{error.message}"
    exit 1
  end
end
