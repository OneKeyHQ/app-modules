// Source recovery modifications: see docs/CONVERSATION_RECOVERY.md.
import Foundation
import AVFoundation
import Accelerate
import os

@_objcRuntimeName(_TtC18IntercomSDKPrivate13AudioRecorder)
actor AudioRecorder: AudioRecording {
    var formatOverride: AVAudioFormat?
    private var isRecording = false
    private var recordingDuration: Double = 0
    private var audioLevels: [Float] = []
    private var audioEngine: AVAudioEngine?
    private var preWarmedEngine: AVAudioEngine?
    private var audioFile: AVAudioFile?
    private var recordingURL: URL
    private var durationTask: Task<Void, Never>?
    private var progressHandler: (@Sendable (RecordingProgress) -> Void)?
    private let fileManager: FileManager
    private let audioSession: AVAudioSession
    private let levelProcessor: AudioLevelProcessor

    init(fileManager: FileManager, audioSession: AVAudioSession,
         levelProcessor: AudioLevelProcessor = AudioLevelProcessor(configuration: .init(
            silenceThreshold: -60, defaultLevel: -80, minimumLevelThreshold: 0.05))) {
        self.fileManager = fileManager
        self.audioSession = audioSession
        self.levelProcessor = levelProcessor
        let directory = fileManager.urls(for: .documentDirectory, in: .userDomainMask).first ?? fileManager.temporaryDirectory
        recordingURL = directory.appendingPathComponent("recording.m4a")
    }

    func requestPermission() async -> Bool {
        if audioSession.recordPermission == .granted { return true }
        return await withCheckedContinuation { continuation in
            audioSession.requestRecordPermission { continuation.resume(returning: $0) }
        }
    }

    func preWarmAudioSession() async {
        do {
            try audioSession.setCategory(.playAndRecord, mode: .default, options: [])
            try audioSession.setActive(true, options: [])
            let engine = AVAudioEngine()
            _ = engine.inputNode
            preWarmedEngine = engine
        } catch {}
    }
    func startRecording(onProgressUpdate: @escaping @Sendable (RecordingProgress) -> Void) async throws {
        if isRecording { return }
        let engine = preWarmedEngine ?? AVAudioEngine()
        audioEngine = engine
        preWarmedEngine = nil
        let input = engine.inputNode
        let format = formatOverride ?? input.outputFormat(forBus: 0)
        if !(format.sampleRate > 0) || !format.sampleRate.isFinite || format.channelCount == 0 {
            tearDownEngine()
            throw TranscriptionError.recordingFailed("Invalid audio format: sampleRate=\(format.sampleRate), channelCount=\(format.channelCount)")
        }
        let settings: [String: Any] = [AVFormatIDKey: kAudioFormatMPEG4AAC,
            AVSampleRateKey: format.sampleRate, AVNumberOfChannelsKey: format.channelCount, AVEncoderAudioQualityKey: 96]
        do {
            audioFile = try AVAudioFile(forWriting: recordingURL, settings: settings)
            input.installTap(onBus: 0, bufferSize: 1024, format: format) { [weak self] buffer, _ in
                Task { [weak self, buffer] in
                    await self?.processAudioBuffer(buffer)
                    try? await self?.audioFile?.write(from: buffer)
                }
            }
            try engine.start()
        } catch {
            tearDownEngine()
            throw error
        }
        isRecording = true
        recordingDuration = 0
        audioLevels = Array(repeating: -1, count: 500)
        progressHandler = onProgressUpdate
        durationTask = Task {
            while !Task.isCancelled && self.isRecording {
                try? await Task.sleep(nanoseconds: 100_000_000)
                self.recordingDuration += 0.1
                self.progressHandler?(RecordingProgress(duration: self.recordingDuration, audioLevels: self.audioLevels))
            }
        }
    }
    private func tearDownEngine() {
        durationTask?.cancel()
        if let engine = audioEngine {
            engine.stop()
            engine.inputNode.removeTap(onBus: 0)
        }
        audioEngine = nil
        audioFile = nil
    }

    private func processAudioBuffer(_ buffer: AVAudioPCMBuffer) {
        guard let channels = buffer.floatChannelData else { return }
        let samples = channels[0]
        guard buffer.frameLength != 0 else { return }
        var rms: Float = 0
        vDSP_rmsqv(samples, 1, &rms, vDSP_Length(buffer.frameLength))
        let configuration = levelProcessor.configuration
        let level: Float
        if !(rms > 0) { level = 0 }
        else {
            let db = 20 * log10f(rms)
            if db < configuration.silenceThreshold { level = 0 }
            else {
                let normalized = (db - configuration.silenceThreshold) / -configuration.silenceThreshold
                level = normalized < configuration.minimumLevelThreshold ? 0 : normalized
            }
        }
        audioLevels.append(level)
        progressHandler?(RecordingProgress(duration: recordingDuration, audioLevels: audioLevels))
    }

    func stopRecording() async throws -> Data {
        if !isRecording { return Data() }
        isRecording = false
        tearDownEngine()
        progressHandler = nil
        if !fileManager.fileExists(atPath: recordingURL.path) {
            throw TranscriptionError.transcriptionFailed("No audio data available")
        }
        let data = try Data(contentsOf: recordingURL, options: [])
        do { try audioSession.setActive(false, options: .notifyOthersOnDeactivation) }
        catch { ICMLogger.log("Error deactivating audio session: \(error)", type: .error) }
        return data
    }

    func clearRecording() async {
        if isRecording {
            isRecording = false
            tearDownEngine()
            progressHandler = nil
            try? audioSession.setActive(false, options: [])
        }
        recordingDuration = 0
        audioLevels = []
        if fileManager.fileExists(atPath: recordingURL.path) { try? fileManager.removeItem(at: recordingURL) }
    }
}
