def ensure_pods_script_phase(target, name)
  existing = target.shell_script_build_phases.find { |phase| phase.name == name }
  return existing if existing

  # CocoaPods can restart its UUID counter after stabilizing target UUIDs.
  project = target.project
  uuid = project.generate_uuid
  uuid = project.generate_uuid while project.objects_by_uuid.key?(uuid)
  phase = Xcodeproj::Project::Object::PBXShellScriptBuildPhase.new(project, uuid)
  phase.initialize_defaults
  phase.name = name
  target.build_phases << phase
  phase
end
