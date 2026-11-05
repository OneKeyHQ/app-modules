// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation
import Combine
import os

private let placeholderAudioLevels = Array(repeating: Float(-1), count: 500)

@_objcRuntimeName(_TtC18IntercomSDKPrivate22AudioRecorderViewModel)
class AudioRecorderViewModel: ObservableObject {
    @Published var state: VoiceTranscriptionViewState = .starting
    @Published var audioLevels: [Float] = []
    @Published var hasRecordingPermission = false
    private let audioRecorder: any AudioRecording
    private let onTranscriptionResult: ((Result<TranscriptionResult, TranscriptionError>) -> Void)?

    init(audioRecorder: any AudioRecording,
         onTranscriptionResult: ((Result<TranscriptionResult, TranscriptionError>) -> Void)?) {
        self.audioRecorder = audioRecorder
        self.onTranscriptionResult = onTranscriptionResult
    }

    @MainActor func requestPermission() async {
        hasRecordingPermission = await audioRecorder.requestPermission()
        if hasRecordingPermission { await audioRecorder.preWarmAudioSession() }
    }

    @MainActor func startRecording() async {
        if !hasRecordingPermission { handleError(.noPermission); return }
        state = .starting
        audioLevels = placeholderAudioLevels
        do {
            try await audioRecorder.startRecording { [weak self] progress in
                Task { @MainActor [weak self, progress] in self?.audioLevels = progress.audioLevels }
            }
            state = .recordingAudio
        } catch {
            ICMLogger.log("startRecording failed - Error: \(error), Type: \(type(of: error))", type: .error)
            audioLevels = []
            handleError(.recordingFailed(error.localizedDescription))
        }
    }

    @MainActor func stopRecording() async throws {
        let current = state
        switch current {
        case .starting, .recordingAudio: break
        case .error, .transcribingAudio: return
        }
        state = .transcribingAudio
        do {
            let data = try await audioRecorder.stopRecording()
            if let callback = onTranscriptionResult { callback(.success(TranscriptionResult(audioData: data))) }
            else {
                state = .error(.transcriptionFailed("No callback configured"))
                ICMLogger.log("stopRecording failed - No callback configured", type: .error)
            }
        } catch {
            ICMLogger.log("stopRecording failed - Error: \(error), Type: \(type(of: error))", type: .error)
            await audioRecorder.clearRecording()
            handleError(.recordingFailed(error.localizedDescription))
            throw error
        }
    }

    @MainActor func clearRecording() async {
        state = .starting
        audioLevels = []
        await audioRecorder.clearRecording()
    }

    @MainActor func handleError(_ error: TranscriptionError) {
        state = .error(error)
        onTranscriptionResult?(.failure(error))
    }
}
