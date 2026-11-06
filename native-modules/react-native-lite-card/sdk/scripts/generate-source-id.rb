# Generate a content identity without incorporating build paths or environment data.
require 'digest'
require 'fileutils'

package_root = File.expand_path('../..', __dir__)
manifest = File.join(package_root, 'sdk/source-id-manifest.txt')
paths = File.readlines(manifest, chomp: true).flat_map do |entry|
  if entry.end_with?('/')
    Dir.glob(File.join(package_root, entry, '**', '*')).select { |path| File.file?(path) }
      .map { |path| path.delete_prefix("#{package_root}/") }
      .select { |path| path.match?(/\.(c|cpp|h|hpp|inc|table|m|mm|swift|kt|java)\z/) }
      .reject { |path| path.split('/').any? { |part| part.start_with?('.') || %w[build generated CMakeFiles node_modules __pycache__].include?(part) } }
  else
    [entry]
  end
end.uniq.sort
content = paths.map { |path| "#{path} #{Digest::SHA256.file(File.join(package_root, path)).hexdigest}\n" }.join
source_id = "source-v1:#{Digest::SHA256.hexdigest(content)}"
header = "#define GPCHANNEL_NATIVE_SOURCE_ID \"#{source_id}\"\n"
output = ARGV.fetch(0)
FileUtils.mkdir_p(File.dirname(output))
File.write(output, header) unless File.file?(output) && File.read(output) == header
