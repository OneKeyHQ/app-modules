// Source recovery modifications: see docs/IOS_PROVENANCE.md.
import Foundation
import MetricKit

enum DiagnosticMetadata {
    case crash(exceptionType: Int?, signal: Int?, exceptionCode: Int?, terminationReason: String?)
    case hang(durationMs: Double)
    case cpu(totalCPUTimeMs: Double, totalSampledTimeMs: Double)
    case diskWrite(totalWritesCaused: Int)
}

struct ExtractedDiagnostic {
    let type: DiagnosticType
    let callStackJSON: Data
    let metadata: DiagnosticMetadata
}

struct ExtractedPayload {
    let timeStampEnd: Date
    let diagnostics: [ExtractedDiagnostic]
}

func extractDiagnosticPayload(_ payload: MXDiagnosticPayload) -> ExtractedPayload {
    var diagnostics: [ExtractedDiagnostic] = []
    for diagnostic in payload.crashDiagnostics ?? [] {
        diagnostics.append(ExtractedDiagnostic(
            type: .crash, callStackJSON: diagnostic.callStackTree.jsonRepresentation(),
            metadata: .crash(exceptionType: diagnostic.exceptionType?.intValue,
                             signal: diagnostic.signal?.intValue,
                             exceptionCode: diagnostic.exceptionCode?.intValue,
                             terminationReason: diagnostic.terminationReason)))
    }
    for diagnostic in payload.hangDiagnostics ?? [] {
        diagnostics.append(ExtractedDiagnostic(
            type: .hang, callStackJSON: diagnostic.callStackTree.jsonRepresentation(),
            metadata: .hang(durationMs: diagnostic.hangDuration.converted(to: .milliseconds).value)))
    }
    for diagnostic in payload.cpuExceptionDiagnostics ?? [] {
        diagnostics.append(ExtractedDiagnostic(
            type: .cpuException, callStackJSON: diagnostic.callStackTree.jsonRepresentation(),
            metadata: .cpu(totalCPUTimeMs: diagnostic.totalCPUTime.converted(to: .milliseconds).value,
                           totalSampledTimeMs: diagnostic.totalSampledTime.converted(to: .milliseconds).value)))
    }
    for diagnostic in payload.diskWriteExceptionDiagnostics ?? [] {
        diagnostics.append(ExtractedDiagnostic(
            type: .diskWriteException, callStackJSON: diagnostic.callStackTree.jsonRepresentation(),
            metadata: .diskWrite(totalWritesCaused: Int(diagnostic.totalWritesCaused.converted(to: .bytes).value))))
    }
    return ExtractedPayload(timeStampEnd: payload.timeStampEnd, diagnostics: diagnostics)
}
