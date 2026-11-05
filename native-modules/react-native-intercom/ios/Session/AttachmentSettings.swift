// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import os

@objc(_TtC18IntercomSDKPrivate18AttachmentSettings)
class AttachmentSettings: NSObject, NSSecureCoding {
    @objc let gifsEnabled: Bool
    @objc let cameraEnabled: Bool
    @objc let filesEnabled: Bool
    @objc let mediaEnabled: Bool
    @objc let voiceTranscriptionEnabled: Bool
    @objc let uploadSizeLimit: Int
    @objc let allowedFileExtensions: [String]
    @objc static var supportsSecureCoding: Bool { true }
    @objc static var defaultUploadSize: Int { 0x6400000 }

    @objc init(gifsEnabled: Bool, cameraEnabled: Bool, filesEnabled: Bool, mediaEnabled: Bool,
               voiceTranscriptionEnabled: Bool, uploadSizeLimit: Int, allowedFileExtensions: [String]) {
        self.gifsEnabled = gifsEnabled
        self.cameraEnabled = Self.checkCameraUsageDescriptionsPresent(cameraEnabled: cameraEnabled, utils: ICMUtils())
        self.filesEnabled = filesEnabled
        self.mediaEnabled = mediaEnabled
        self.voiceTranscriptionEnabled = Self.checkMicrophoneUsageDescriptionPresent(voiceTranscriptionEnabled: voiceTranscriptionEnabled, utils: ICMUtils())
        self.uploadSizeLimit = uploadSizeLimit
        self.allowedFileExtensions = allowedFileExtensions
        super.init()
    }

    @objc override convenience init() {
        self.init(gifsEnabled: true, cameraEnabled: true, filesEnabled: true, mediaEnabled: true,
                  voiceTranscriptionEnabled: false, uploadSizeLimit: 0x6400000, allowedFileExtensions: [])
    }

    @objc(initWithDictionary:)
    convenience init?(withDictionary dictionary: [String: Any]) {
        guard let gifs = dictionary["conversation_gifs_enabled"] as? Bool,
              let camera = dictionary["conversation_camera_enabled"] as? Bool,
              let files = dictionary["conversation_files_enabled"] as? Bool,
              let media = dictionary["conversation_media_enabled"] as? Bool,
              let limit = dictionary["upload_size_limit"] as? Int,
              let voice = dictionary["conversation_voice_notes_enabled"] as? Bool,
              let extensions = dictionary["file_upload_extension_trusted_list"] as? [String] else { return nil }
        self.init(gifsEnabled: gifs, cameraEnabled: camera, filesEnabled: files, mediaEnabled: media,
                  voiceTranscriptionEnabled: voice, uploadSizeLimit: limit, allowedFileExtensions: extensions)
    }

    required init?(coder: NSCoder) {
        gifsEnabled = coder.decodeBool(forKey: "conversation_gifs_enabled")
        cameraEnabled = coder.decodeBool(forKey: "conversation_camera_enabled")
        filesEnabled = coder.decodeBool(forKey: "conversation_files_enabled")
        mediaEnabled = coder.decodeBool(forKey: "conversation_media_enabled")
        let voice = coder.decodeBool(forKey: "conversation_voice_notes_enabled")
        voiceTranscriptionEnabled = Self.checkMicrophoneUsageDescriptionPresent(voiceTranscriptionEnabled: voice, utils: ICMUtils())
        uploadSizeLimit = coder.decodeInteger(forKey: "upload_size_limit")
        allowedFileExtensions = coder.decodeArrayOfObjects(ofClass: NSString.self, forKey: "file_upload_extension_trusted_list") as? [String] ?? []
        super.init()
    }

    func encode(with coder: NSCoder) {
        coder.encode(gifsEnabled, forKey: "conversation_gifs_enabled")
        coder.encode(cameraEnabled, forKey: "conversation_camera_enabled")
        coder.encode(filesEnabled, forKey: "conversation_files_enabled")
        coder.encode(mediaEnabled, forKey: "conversation_media_enabled")
        coder.encode(voiceTranscriptionEnabled, forKey: "conversation_voice_notes_enabled")
        coder.encode(uploadSizeLimit, forKey: "upload_size_limit")
        coder.encode(allowedFileExtensions, forKey: "file_upload_extension_trusted_list")
    }

    override func isEqual(_ object: Any?) -> Bool {
        guard let other = object as? Self else { return false }
        return gifsEnabled == other.gifsEnabled && cameraEnabled == other.cameraEnabled &&
            filesEnabled == other.filesEnabled && mediaEnabled == other.mediaEnabled &&
            voiceTranscriptionEnabled == other.voiceTranscriptionEnabled &&
            uploadSizeLimit == other.uploadSizeLimit && allowedFileExtensions == other.allowedFileExtensions
    }

    private static func checkCameraUsageDescriptionsPresent(cameraEnabled: Bool, utils: ICMUtils) -> Bool {
        _ = utils
        if Bundle.main.object(forInfoDictionaryKey: "NSCameraUsageDescription") == nil && cameraEnabled {
            ICMLogger.log("Camera input has been disabled because NSCameraUsageDescription is missing from your Info.plist. More information is available here: https://developers.intercom.com/installing-intercom/ios/installation/#step-2---update-info.plist", type: .error)
            return false
        }
        if Bundle.main.object(forInfoDictionaryKey: "NSCameraUsageDescription") != nil &&
            Bundle.main.object(forInfoDictionaryKey: "NSMicrophoneUsageDescription") == nil && cameraEnabled {
            ICMLogger.log("Photo capture only. To allow video capture please add NSMicrophoneUsageDescription to your Info.plist. More information is available here: https://developers.intercom.com/installing-intercom/ios/installation/#step-2---update-info.plist", type: .info)
            return true
        }
        return cameraEnabled
    }

    private static func checkMicrophoneUsageDescriptionPresent(voiceTranscriptionEnabled: Bool, utils: ICMUtils) -> Bool {
        _ = utils
        let override = UserDefaults.standard.bool(forKey: "isVoiceTranscriptionEnabled")
        if !voiceTranscriptionEnabled && !override { return false }
        if Bundle.main.object(forInfoDictionaryKey: "NSMicrophoneUsageDescription") != nil { return true }
        ICMLogger.log("Voice transcription has been disabled because NSMicrophoneUsageDescription is missing from your Info.plist.\nTo enable voice transcription, please add NSMicrophoneUsageDescription to your Info.plist.\nMore information is available here: https://developers.intercom.com/installing-intercom/ios/installation/#step-2---update-info.plist", type: .error)
        return false
    }
}
