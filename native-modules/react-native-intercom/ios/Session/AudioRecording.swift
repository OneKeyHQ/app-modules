import Foundation

struct RecordingProgress {
    var duration: Double
    var audioLevels: [Float]
}

protocol AudioRecording: Actor {
    func requestPermission() async -> Bool
    func preWarmAudioSession() async
    func startRecording(onProgressUpdate: @escaping @Sendable (RecordingProgress) -> Void) async throws
    func stopRecording() async throws -> Data
    func clearRecording() async
}

struct AudioLevelProcessor {
    struct Configuration {
        var silenceThreshold: Float
        var defaultLevel: Float
        var minimumLevelThreshold: Float
    }
    let configuration: Configuration
}

struct TranscriptionResult { let audioData: Data }

enum TranscriptionError: Error, LocalizedError, Equatable {
    case recordingFailed(String), transcriptionFailed(String), networkError(String), fileTooLarge, noPermission
    var errorDescription: String? {
        switch self {
        case .recordingFailed(let message): return "Recording failed: " + message
        case .transcriptionFailed(let message): return "Transcription failed: " + message
        case .networkError(let message): return "Network error: " + message
        case .fileTooLarge: return "File too large"
        case .noPermission: return "Microphone permission required"
        }
    }
}

enum VoiceTranscriptionViewState: Equatable {
    case error(TranscriptionError), starting, recordingAudio, transcribingAudio
}
