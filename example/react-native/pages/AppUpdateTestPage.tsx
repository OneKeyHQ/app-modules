import { useState, useRef, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Alert,
  TouchableOpacity,
  Animated,
  ActivityIndicator,
  Platform,
} from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeAppUpdate } from '@onekeyfe/react-native-app-update';
import type { DownloadEvent } from '@onekeyfe/react-native-app-update';

// --- Types ---

type StepStatus = 'pending' | 'active' | 'completed' | 'error';

interface StepState {
  status: StepStatus;
  errorMessage?: string;
}

interface WorkflowState {
  download: StepState;
  downloadAsc: StepState;
  verifyAsc: StepState;
  verify: StepState;
  install: StepState;
  downloadProgress: number;
}

type StepId = 'download' | 'downloadAsc' | 'verifyAsc' | 'verify' | 'install';

// --- Color Palette ---

const C = {
  cardBg: '#1c1c1e',
  cardBgElevated: '#2c2c2e',
  surfaceBg: '#0a0a0a',
  accentCyan: '#00d4aa',
  accentCyanDim: 'rgba(0, 212, 170, 0.15)',
  accentBlue: '#007AFF',
  errorRed: '#ff453a',
  textPrimary: '#ffffff',
  textSecondary: '#8e8e93',
  textTertiary: '#636366',
  border: '#38383a',
};

const mono = Platform.OS === 'ios' ? 'Menlo' : 'monospace';

// --- Sub-components ---

function StepRow({
  stepNumber,
  label,
  status,
  errorMessage,
  onAction,
  actionLabel,
  disabled,
  children,
}: {
  stepNumber: number;
  label: string;
  status: StepStatus;
  errorMessage?: string;
  onAction: () => void;
  actionLabel: string;
  disabled: boolean;
  children?: React.ReactNode;
}) {
  const btnLabel =
    status === 'active' ? '...' : status === 'error' ? 'Retry' : actionLabel;

  return (
    <View style={s.stepRow}>
      <View style={s.stepHeader}>
        <View
          style={[
            s.stepCircle,
            status === 'active' && s.stepCircleActive,
            status === 'completed' && s.stepCircleCompleted,
            status === 'error' && s.stepCircleError,
          ]}
        >
          {status === 'completed' ? (
            <Text style={s.stepCheck}>{'✓'}</Text>
          ) : status === 'error' ? (
            <Text style={s.stepBang}>!</Text>
          ) : status === 'active' ? (
            <ActivityIndicator size="small" color={C.accentCyan} />
          ) : (
            <Text style={s.stepNum}>{stepNumber}</Text>
          )}
        </View>

        <View style={s.stepInfo}>
          <Text
            style={[s.stepLabel, status === 'pending' && s.stepLabelDim]}
          >
            {label}
          </Text>
          {status === 'active' && (
            <Text style={s.stepSub}>In progress...</Text>
          )}
          {status === 'completed' && (
            <Text style={[s.stepSub, { color: C.accentCyan }]}>
              Completed
            </Text>
          )}
          {status === 'error' && errorMessage && (
            <Text style={[s.stepSub, { color: C.errorRed }]} numberOfLines={2}>
              {errorMessage}
            </Text>
          )}
        </View>

        <TouchableOpacity
          style={[
            s.stepBtn,
            disabled && s.stepBtnDisabled,
            status === 'completed' && s.stepBtnDone,
          ]}
          onPress={onAction}
          disabled={disabled || status === 'completed'}
          activeOpacity={0.7}
        >
          <Text
            style={[s.stepBtnText, disabled && s.stepBtnTextDisabled]}
          >
            {btnLabel}
          </Text>
        </TouchableOpacity>
      </View>
      {children}
    </View>
  );
}

function StepConnector({ active }: { active: boolean }) {
  return (
    <View style={s.connectorWrap}>
      <View style={[s.connectorLine, active && s.connectorLineActive]} />
    </View>
  );
}

function ProgressBar({
  progress,
  percentage,
}: {
  progress: Animated.Value;
  percentage: number;
}) {
  const barWidth = progress.interpolate({
    inputRange: [0, 1],
    outputRange: ['0%', '100%'],
  });

  return (
    <View style={s.progressWrap}>
      <View style={s.progressTrack}>
        <Animated.View style={[s.progressFill, { width: barWidth }]} />
      </View>
      <Text style={s.progressText}>{`${Math.round(percentage)}%`}</Text>
    </View>
  );
}

// --- Main Component ---

interface AppUpdateTestPageProps {
}

const INITIAL_WORKFLOW: WorkflowState = {
  download: { status: 'pending' },
  downloadAsc: { status: 'pending' },
  verifyAsc: { status: 'pending' },
  verify: { status: 'pending' },
  install: { status: 'pending' },
  downloadProgress: 0,
};

export function AppUpdateTestPage({
}: AppUpdateTestPageProps) {
  // --- Workflow state ---
  const [workflow, setWorkflow] = useState<WorkflowState>(INITIAL_WORKFLOW);
  const progressAnim = useRef(new Animated.Value(0)).current;
  const listenerIdRef = useRef<number | null>(null);

  // --- Input state ---
  const [downloadUrl, setDownloadUrl] = useState(
    'https://web.onekey-asset.com/app-monorepo/v6.0.0/OneKey-Wallet-6.0.0-android.apk',
  );

  // --- Utility state ---
  const [utilExpanded, setUtilExpanded] = useState(false);
  const [utilResult, setUtilResult] = useState<any>(null);
  const [utilError, setUtilError] = useState<string | null>(null);

  // --- Cleanup listener on unmount ---
  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeAppUpdate.removeDownloadListener(listenerIdRef.current);
      }
    };
  }, []);

  // --- Helpers ---
  const updateStep = useCallback(
    (step: StepId, update: Partial<StepState>) => {
      setWorkflow((prev) => ({
        ...prev,
        [step]: { ...prev[step], ...update },
      }));
    },
    [],
  );

  const resetWorkflow = useCallback(() => {
    setWorkflow(INITIAL_WORKFLOW);
    progressAnim.setValue(0);
  }, [progressAnim]);

  const clearUtil = () => {
    setUtilResult(null);
    setUtilError(null);
  };

  // --- Step Handlers ---

  const handleDownload = useCallback(async () => {
    if (!downloadUrl) {
      updateStep('download', {
        status: 'error',
        errorMessage: 'Please enter a download URL',
      });
      return;
    }

    // Reset all
    setWorkflow({
      ...INITIAL_WORKFLOW,
      download: { status: 'active' },
    });
    progressAnim.setValue(0);

    // Register listener for progress
    const lid = ReactNativeAppUpdate.addDownloadListener(
      (event: DownloadEvent) => {
        if (event.type === 'update/downloading') {
          setWorkflow((prev) => ({
            ...prev,
            downloadProgress: event.progress,
          }));
          Animated.timing(progressAnim, {
            toValue: event.progress / 100,
            duration: 200,
            useNativeDriver: false,
          }).start();
        }
      },
    );
    listenerIdRef.current = lid;

    try {
      await ReactNativeAppUpdate.downloadAPK({
        downloadUrl,
        notificationTitle: 'Downloading Update',
        fileSize: 183670673,
      });

      setWorkflow((prev) => ({
        ...prev,
        download: { status: 'completed' },
        downloadProgress: 100,
      }));
      Animated.timing(progressAnim, {
        toValue: 1,
        duration: 300,
        useNativeDriver: false,
      }).start();
    } catch (err) {
      updateStep('download', {
        status: 'error',
        errorMessage: err instanceof Error ? err.message : 'Download failed',
      });
      setWorkflow((prev) => ({ ...prev, downloadProgress: 0 }));
    } finally {
      if (listenerIdRef.current !== null) {
        ReactNativeAppUpdate.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
    }
  }, [downloadUrl, progressAnim, updateStep]);

  const handleDownloadAsc = useCallback(async () => {
    if (!downloadUrl) {
      updateStep('downloadAsc', {
        status: 'error',
        errorMessage: 'Please enter a download URL',
      });
      return;
    }
    updateStep('downloadAsc', { status: 'active' });
    try {
      await ReactNativeAppUpdate.downloadASC({ downloadUrl });
      updateStep('downloadAsc', { status: 'completed' });
    } catch (err) {
      updateStep('downloadAsc', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'ASC download failed',
      });
    }
  }, [downloadUrl, updateStep]);

  const handleVerifyAsc = useCallback(async () => {
    if (!downloadUrl) {
      updateStep('verifyAsc', {
        status: 'error',
        errorMessage: 'Please enter a download URL',
      });
      return;
    }
    updateStep('verifyAsc', { status: 'active' });
    try {
      await ReactNativeAppUpdate.verifyASC({ downloadUrl });
      updateStep('verifyAsc', { status: 'completed' });
    } catch (err) {
      updateStep('verifyAsc', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'ASC verification failed',
      });
    }
  }, [downloadUrl, updateStep]);

  const handleVerify = useCallback(async () => {
    if (!downloadUrl) {
      updateStep('verify', {
        status: 'error',
        errorMessage: 'Please enter a download URL',
      });
      return;
    }
    updateStep('verify', { status: 'active' });
    try {
      await ReactNativeAppUpdate.verifyAPK({ downloadUrl });
      updateStep('verify', { status: 'completed' });
    } catch (err) {
      updateStep('verify', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'Verification failed',
      });
    }
  }, [downloadUrl, updateStep]);

  const handleInstall = useCallback(async () => {
    if (!downloadUrl) {
      updateStep('install', {
        status: 'error',
        errorMessage: 'Please enter a download URL',
      });
      return;
    }
    updateStep('install', { status: 'active' });
    try {
      await ReactNativeAppUpdate.installAPK({ downloadUrl });
      updateStep('install', { status: 'completed' });
    } catch (err) {
      updateStep('install', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'Installation failed',
      });
    }
  }, [downloadUrl, updateStep]);

  // --- Utility handlers ---

  const utilClearCache = async () => {
    clearUtil();
    try {
      await ReactNativeAppUpdate.clearCache();
      setUtilResult({ cacheCleared: true });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  const utilRegisterListener = () => {
    clearUtil();
    try {
      if (listenerIdRef.current !== null) {
        Alert.alert('Info', 'Listener already registered');
        return;
      }
      const id = ReactNativeAppUpdate.addDownloadListener((event) => {
        setUtilResult({
          downloadEvent: event,
          timestamp: new Date().toLocaleTimeString(),
        });
      });
      listenerIdRef.current = id;
      setUtilResult({ listenerRegistered: true, listenerId: id });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };

  // --- Render ---

  const showProgress =
    workflow.download.status === 'active' ||
    workflow.download.status === 'completed';

  return (
    <TestPageBase
      title="App Update"
    >
      {/* URL Input */}
      <View style={s.card}>
        <Text style={s.cardTitle}>APK PARAMETERS</Text>
        <TestInput
          placeholder="Download URL"
          value={downloadUrl}
          onChangeText={setDownloadUrl}
        />
      </View>

      {/* Pipeline */}
      <View style={s.card}>
        <Text style={s.cardTitle}>UPDATE PIPELINE</Text>

        <StepRow
          stepNumber={1}
          label="Download APK"
          status={workflow.download.status}
          errorMessage={workflow.download.errorMessage}
          onAction={handleDownload}
          actionLabel="Download"
          disabled={workflow.download.status === 'active'}
        >
          {showProgress && (
            <ProgressBar
              progress={progressAnim}
              percentage={workflow.downloadProgress}
            />
          )}
        </StepRow>

        <StepConnector active={workflow.download.status === 'completed'} />

        <StepRow
          stepNumber={2}
          label="Download ASC"
          status={workflow.downloadAsc.status}
          errorMessage={workflow.downloadAsc.errorMessage}
          onAction={handleDownloadAsc}
          actionLabel="Download"
          disabled={
            workflow.download.status !== 'completed' ||
            workflow.downloadAsc.status === 'active'
          }
        />

        <StepConnector active={workflow.downloadAsc.status === 'completed'} />

        <StepRow
          stepNumber={3}
          label="Verify ASC"
          status={workflow.verifyAsc.status}
          errorMessage={workflow.verifyAsc.errorMessage}
          onAction={handleVerifyAsc}
          actionLabel="Verify"
          disabled={
            workflow.downloadAsc.status !== 'completed' ||
            workflow.verifyAsc.status === 'active'
          }
        />

        <StepConnector active={workflow.verifyAsc.status === 'completed'} />

        <StepRow
          stepNumber={4}
          label="Verify APK"
          status={workflow.verify.status}
          errorMessage={workflow.verify.errorMessage}
          onAction={handleVerify}
          actionLabel="Verify"
          disabled={
            workflow.verifyAsc.status !== 'completed' ||
            workflow.verify.status === 'active'
          }
        />

        <StepConnector active={workflow.verify.status === 'completed'} />

        <StepRow
          stepNumber={5}
          label="Install APK"
          status={workflow.install.status}
          errorMessage={workflow.install.errorMessage}
          onAction={handleInstall}
          actionLabel="Install"
          disabled={
            workflow.verify.status !== 'completed' ||
            workflow.install.status === 'active'
          }
        />
      </View>

      {/* Reset */}
      <TouchableOpacity style={s.resetBtn} onPress={resetWorkflow}>
        <Text style={s.resetBtnText}>Reset Pipeline</Text>
      </TouchableOpacity>

      {/* Platform Info */}
      <View style={s.card}>
        <Text style={s.cardTitle}>PLATFORM INFO</Text>
        <Text style={s.infoText}>
          {Platform.OS === 'ios'
            ? 'iOS: All APK operations are no-ops on this platform.\nApp updates are handled through the App Store.'
            : 'Android: APK download, verification, and installation\nare supported on this platform.'}
        </Text>
      </View>

      {/* Utilities */}
      <TouchableOpacity
        style={[s.card, s.utilHeader]}
        onPress={() => setUtilExpanded(!utilExpanded)}
        activeOpacity={0.7}
      >
        <Text style={s.cardTitle}>UTILITIES & DEBUG</Text>
        <Text style={s.chevron}>{utilExpanded ? '▲' : '▼'}</Text>
      </TouchableOpacity>
      {utilExpanded && (
        <View style={[s.card, { borderTopLeftRadius: 0, borderTopRightRadius: 0, marginTop: -12 }]}>
          <TestButton title="Clear Cache" onPress={utilClearCache} />
          <TestButton title="Register Download Listener" onPress={utilRegisterListener} />
          <TestResult result={utilResult} error={utilError} />
        </View>
      )}
    </TestPageBase>
  );
}

// --- Styles ---

const s = StyleSheet.create({
  // Card
  card: {
    backgroundColor: C.cardBg,
    borderRadius: 12,
    padding: 16,
    marginBottom: 14,
    borderWidth: 1,
    borderColor: C.border,
  },
  cardTitle: {
    fontSize: 11,
    fontWeight: '700',
    color: C.textSecondary,
    letterSpacing: 1.5,
    marginBottom: 12,
  },

  // Steps
  stepRow: {
    marginBottom: 4,
  },
  stepHeader: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  stepCircle: {
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: C.cardBgElevated,
    borderWidth: 2,
    borderColor: C.border,
    alignItems: 'center',
    justifyContent: 'center',
  },
  stepCircleActive: {
    borderColor: C.accentCyan,
    backgroundColor: C.accentCyanDim,
  },
  stepCircleCompleted: {
    borderColor: C.accentCyan,
    backgroundColor: C.accentCyan,
  },
  stepCircleError: {
    borderColor: C.errorRed,
    backgroundColor: 'rgba(255,69,58,0.15)',
  },
  stepNum: {
    fontSize: 14,
    fontWeight: '700',
    color: C.textTertiary,
  },
  stepCheck: {
    fontSize: 16,
    fontWeight: '700',
    color: '#000',
  },
  stepBang: {
    fontSize: 16,
    fontWeight: '700',
    color: C.errorRed,
  },
  stepInfo: {
    flex: 1,
    marginLeft: 12,
  },
  stepLabel: {
    fontSize: 15,
    fontWeight: '600',
    color: C.textPrimary,
  },
  stepLabelDim: {
    color: C.textTertiary,
  },
  stepSub: {
    fontSize: 12,
    color: C.accentCyan,
    marginTop: 2,
  },
  stepBtn: {
    paddingHorizontal: 16,
    paddingVertical: 8,
    borderRadius: 8,
    backgroundColor: C.accentBlue,
    minWidth: 80,
    alignItems: 'center',
  },
  stepBtnDisabled: {
    backgroundColor: C.cardBgElevated,
  },
  stepBtnDone: {
    backgroundColor: 'transparent',
    borderWidth: 1,
    borderColor: C.accentCyan,
  },
  stepBtnText: {
    fontSize: 13,
    fontWeight: '600',
    color: '#fff',
  },
  stepBtnTextDisabled: {
    color: C.textTertiary,
  },

  // Connector
  connectorWrap: {
    paddingLeft: 15,
    height: 24,
    justifyContent: 'center',
  },
  connectorLine: {
    width: 2,
    height: '100%',
    backgroundColor: C.border,
  },
  connectorLineActive: {
    backgroundColor: C.accentCyan,
  },

  // Progress
  progressWrap: {
    marginTop: 10,
    marginLeft: 44,
  },
  progressTrack: {
    height: 6,
    backgroundColor: C.surfaceBg,
    borderRadius: 3,
    overflow: 'hidden',
  },
  progressFill: {
    height: '100%',
    backgroundColor: C.accentCyan,
    borderRadius: 3,
  },
  progressText: {
    fontSize: 11,
    color: C.textSecondary,
    marginTop: 4,
    fontFamily: mono,
  },

  // Reset
  resetBtn: {
    alignSelf: 'center',
    paddingHorizontal: 20,
    paddingVertical: 10,
    borderRadius: 8,
    borderWidth: 1,
    borderColor: C.border,
    marginBottom: 14,
  },
  resetBtnText: {
    fontSize: 13,
    fontWeight: '500',
    color: C.textSecondary,
  },

  // Info text
  infoText: {
    fontSize: 11,
    color: C.textTertiary,
    fontFamily: mono,
    backgroundColor: C.cardBgElevated,
    padding: 10,
    borderRadius: 6,
    lineHeight: 16,
  },

  // Utilities
  utilHeader: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 2,
  },
  chevron: {
    fontSize: 12,
    color: C.textSecondary,
  },
});
