import { useState, useRef, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Alert,
  Switch,
  TouchableOpacity,
  Animated,
  ActivityIndicator,
  Platform,
} from 'react-native';
import { TestPageBase, TestButton, TestInput, TestResult } from './TestPageBase';
import { ReactNativeBundleUpdate } from '@onekeyfe/react-native-bundle-update';
import type {
  BundleDownloadEvent,
  BundleDownloadResult,
} from '@onekeyfe/react-native-bundle-update';
import { createMMKV } from 'react-native-mmkv';

const devSettingsMmkv = createMMKV({ id: 'onekey-app-dev-setting' });
const KEY_DEV_MODE = 'onekey_developer_mode_enabled';
const KEY_SKIP_GPG = 'onekey_bundle_skip_gpg_verification';

// --- Types ---

type StepStatus = 'pending' | 'active' | 'completed' | 'error';

interface StepState {
  status: StepStatus;
  errorMessage?: string;
}

interface WorkflowState {
  download: StepState;
  verify: StepState;
  install: StepState;
  downloadProgress: number;
  downloadResult: BundleDownloadResult | null;
}

type StepId = 'download' | 'verify' | 'install';

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

function ToggleRow({
  label,
  subLabel,
  value,
  onToggle,
}: {
  label: string;
  subLabel: string;
  value: boolean;
  onToggle: (v: boolean) => void;
}) {
  return (
    <View style={s.toggleRow}>
      <View style={s.toggleInfo}>
        <Text style={s.toggleLabel}>{label}</Text>
        <Text style={s.toggleKey}>{subLabel}</Text>
      </View>
      <Switch
        value={value}
        onValueChange={onToggle}
        trackColor={{ false: '#3a3a3c', true: C.accentCyan }}
        thumbColor={value ? '#ffffff' : '#8e8e93'}
      />
    </View>
  );
}

// --- Main Component ---

interface BundleUpdateTestPageProps {
}

const INITIAL_WORKFLOW: WorkflowState = {
  download: { status: 'pending' },
  verify: { status: 'pending' },
  install: { status: 'pending' },
  downloadProgress: 0,
  downloadResult: null,
};

export function BundleUpdateTestPage({
}: BundleUpdateTestPageProps) {
  // --- Workflow state ---
  const [workflow, setWorkflow] = useState<WorkflowState>(INITIAL_WORKFLOW);
  const progressAnim = useRef(new Animated.Value(0)).current;
  const listenerIdRef = useRef<number | null>(null);

  // --- Input state ---
  const defaultParams = Platform.select({
      ios: {
        downloadUrl: 'https://uni-test.onekey-asset.com/dashboard/version-update/5016000/upload_1761581017500.0.842474996421545.0.zip',
        latestVersion: '5.16.0',
        bundleVersion: '200',
        fileSize: 51617503,
        sha256: 'c0beb980fc113bc21ea510b778933ed488dc685c2216105bd146df8e9f791a3d',
         signature: `-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "metadata.json",
  "sha256": "bf3734ac6e59388fe23c40ce2960b6fd197c596af05dd08b3ccc8b201b78c52b",
  "size": 167265,
  "generatedAt": "2026-03-31T03:25:05.000Z",
  "appVersion": "6.1.0",
  "buildNumber": "2026032032",
  "bundleVersion": "7701116",
  "appType": "electron"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmnLXs0OHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHtUkhAAoMZQc/Z1slPudePNjgO33XZwhWJNQkLeyPRL
Evz6JowioGdQjk1yJ+2jleSDDHRCceh6BzeqZqCFP58oRqug3MS4x1/7Egvza3l8
5vW+NeX9Ai8l4PniUDcC9IwBITsVz/wzjQdhOuVbtYcP4y/48JvctBNBj5cG7cG7
pMvOiXffUWjrBHToAKJec6V1N5L2b/2K3dutp10o3+tkfOznsHaD1vCpwxaeWcMx
W2I2SsH3uBDRYisY5W5mb5mDPbEuyqL+M+TLxHAGPwRe3+ExeipakPIJFfYsf5zi
6AnlllUv/QBH+1VZ7KauadPLD1HfMCPSbqQuTsgay56H7fvUe9khp2ysftgQ2tpc
NzTtQyZqIUeiUwBSTGqUvuLMCRChfGo7OBJE7Ec/VRzUIwGmN4Je+nY1JTYW+iR5
cRQ9j+aNAhLYLPkdUr9hMXaDjpSdGCBM0YpEoqSOzbuZEVCD92tzdfMUI+bdC6a/
I5cI5w1KTRKJ8irMfzm/TDcIenoUTvhzwqm+v69vFSR1LqWQMXnRvhONNTa9haov
+s+6KSUKPMH4Pa5AgRu5dkoj3UrbZUwt3tOIao97PXVXaFuSBLNhFEjS5yV+uOgK
Wfi3u5D2NWfhq0ZaV25yC16xDIe7SOXgHjNnR1vtt5L9ThZ2deidyiBJA6BFHZK6
RNAOJKE=
=JKzr
-----END PGP SIGNATURE-----`
      },
      android: {
        downloadUrl: 'https://uni.onekey-asset.com/dashboard/version-update/5019002/upload_1767687090638.0.6528223946398171.0.zip',
        latestVersion: '5.19.0',
        bundleVersion: '2',
        fileSize: 55586119,
        sha256: 'cdff9d4b37f2940e5ce73141e384f412e1e4e4bf3046b0cdb4d79b0c974de742',
        signature: `-----BEGIN PGP SIGNED MESSAGE-----
Hash: SHA256

{
  "fileName": "metadata.json",
  "sha256": "bf3734ac6e59388fe23c40ce2960b6fd197c596af05dd08b3ccc8b201b78c52b",
  "size": 167265,
  "generatedAt": "2026-03-31T03:25:05.000Z",
  "appVersion": "6.1.0",
  "buildNumber": "2026032032",
  "bundleVersion": "7701116",
  "appType": "electron"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmnLXs0OHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHtUkhAAoMZQc/Z1slPudePNjgO33XZwhWJNQkLeyPRL
Evz6JowioGdQjk1yJ+2jleSDDHRCceh6BzeqZqCFP58oRqug3MS4x1/7Egvza3l8
5vW+NeX9Ai8l4PniUDcC9IwBITsVz/wzjQdhOuVbtYcP4y/48JvctBNBj5cG7cG7
pMvOiXffUWjrBHToAKJec6V1N5L2b/2K3dutp10o3+tkfOznsHaD1vCpwxaeWcMx
W2I2SsH3uBDRYisY5W5mb5mDPbEuyqL+M+TLxHAGPwRe3+ExeipakPIJFfYsf5zi
6AnlllUv/QBH+1VZ7KauadPLD1HfMCPSbqQuTsgay56H7fvUe9khp2ysftgQ2tpc
NzTtQyZqIUeiUwBSTGqUvuLMCRChfGo7OBJE7Ec/VRzUIwGmN4Je+nY1JTYW+iR5
cRQ9j+aNAhLYLPkdUr9hMXaDjpSdGCBM0YpEoqSOzbuZEVCD92tzdfMUI+bdC6a/
I5cI5w1KTRKJ8irMfzm/TDcIenoUTvhzwqm+v69vFSR1LqWQMXnRvhONNTa9haov
+s+6KSUKPMH4Pa5AgRu5dkoj3UrbZUwt3tOIao97PXVXaFuSBLNhFEjS5yV+uOgK
Wfi3u5D2NWfhq0ZaV25yC16xDIe7SOXgHjNnR1vtt5L9ThZ2deidyiBJA6BFHZK6
RNAOJKE=
=JKzr
-----END PGP SIGNATURE-----`
      }
    }) ?? {
      downloadUrl: '',
      latestVersion: '1.0.0',
      bundleVersion: '1',
      fileSize: 1,
      sha256: '',
      signature: '',
    };
  const [downloadUrl, setDownloadUrl] = useState(defaultParams.downloadUrl);

  // --- Dev settings ---
  const [devModeEnabled, setDevModeEnabled] = useState(
    () => devSettingsMmkv.getBoolean(KEY_DEV_MODE) ?? false,
  );
  const [skipGPGEnabled, setSkipGPGEnabled] = useState(
    () => devSettingsMmkv.getBoolean(KEY_SKIP_GPG) ?? false,
  );
  const toggleDevMode = useCallback((v: boolean) => {
    devSettingsMmkv.set(KEY_DEV_MODE, v);
    setDevModeEnabled(v);
  }, []);
  const toggleSkipGPG = useCallback((v: boolean) => {
    devSettingsMmkv.set(KEY_SKIP_GPG, v);
    setSkipGPGEnabled(v);
  }, []);

  // --- Utility state ---
  const [utilExpanded, setUtilExpanded] = useState(false);
  const [utilResult, setUtilResult] = useState<any>(null);
  const [utilError, setUtilError] = useState<string | null>(null);

  // --- Cleanup listener on unmount ---
  useEffect(() => {
    return () => {
      if (listenerIdRef.current !== null) {
        ReactNativeBundleUpdate.removeDownloadListener(listenerIdRef.current);
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
    const lid = ReactNativeBundleUpdate.addDownloadListener(
      (event: BundleDownloadEvent) => {
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
      const result = await ReactNativeBundleUpdate.downloadBundle({
        downloadUrl: defaultParams.downloadUrl,
        latestVersion: defaultParams.latestVersion,
        bundleVersion: defaultParams.bundleVersion,
        fileSize: defaultParams.fileSize,
        sha256: defaultParams.sha256,
      });

      setWorkflow((prev) => ({
        ...prev,
        download: { status: 'completed' },
        downloadProgress: 100,
        downloadResult: result,
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
        ReactNativeBundleUpdate.removeDownloadListener(listenerIdRef.current);
        listenerIdRef.current = null;
      }
    }
  }, [defaultParams.bundleVersion, defaultParams.downloadUrl, defaultParams.fileSize, defaultParams.latestVersion, defaultParams.sha256, downloadUrl, progressAnim, updateStep]);

  const handleVerify = useCallback(async () => {
    const dr = workflow.downloadResult;
    if (!dr) return;
    updateStep('verify', { status: 'active' });
    try {
      await ReactNativeBundleUpdate.verifyBundleASC({
        downloadedFile: dr.downloadedFile,
        sha256: dr.sha256,
        latestVersion: dr.latestVersion,
        bundleVersion: dr.bundleVersion,
        signature: defaultParams.signature
      });
      updateStep('verify', { status: 'completed' });
    } catch (err) {
      updateStep('verify', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'Verification failed',
      });
    }
  }, [workflow.downloadResult, updateStep, defaultParams.signature]);

  const handleInstall = useCallback(async () => {
    const dr = workflow.downloadResult;
    if (!dr) return;
    updateStep('install', { status: 'active' });
    try {
      await ReactNativeBundleUpdate.installBundle({
        downloadedFile: dr.downloadedFile,
        latestVersion: dr.latestVersion,
        bundleVersion: dr.bundleVersion,
        signature: defaultParams.signature,
      });
      updateStep('install', { status: 'completed' });
    } catch (err) {
      updateStep('install', {
        status: 'error',
        errorMessage:
          err instanceof Error ? err.message : 'Installation failed',
      });
    }
  }, [workflow.downloadResult, updateStep, defaultParams.signature]);

  // --- Utility handlers ---

  const utilGetWebEmbedPath = () => {
    clearUtil();
    try {
      setUtilResult({ webEmbedPath: ReactNativeBundleUpdate.getWebEmbedPath() || '(empty)' });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilGetJsBundlePath = async () => {
    clearUtil();
    try {
      setUtilResult({ jsBundlePath: (await ReactNativeBundleUpdate.getJsBundlePathAsync()) || '(empty)' });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilGetNativeAppVersion = async () => {
    clearUtil();
    try {
      setUtilResult({ nativeAppVersion: await ReactNativeBundleUpdate.getNativeAppVersion() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilGetNativeBuildNumber = async () => {
    clearUtil();
    try {
      setUtilResult({ nativeBuildNumber: await ReactNativeBundleUpdate.getNativeBuildNumber() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilGetBuiltinBundleVersion = async () => {
    clearUtil();
    try {
      setUtilResult({ builtinBundleVersion: await ReactNativeBundleUpdate.getBuiltinBundleVersion() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilGetFallbackData = async () => {
    clearUtil();
    try {
      setUtilResult({ fallbackBundleData: await ReactNativeBundleUpdate.getFallbackUpdateBundleData() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilListLocal = async () => {
    clearUtil();
    try {
      setUtilResult({ localBundles: await ReactNativeBundleUpdate.listLocalBundles() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilListAscFiles = async () => {
    clearUtil();
    try {
      setUtilResult({ ascFiles: await ReactNativeBundleUpdate.listAscFiles() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilTestVerification = async () => {
    clearUtil();
    try {
      setUtilResult({ verificationTest: await ReactNativeBundleUpdate.testVerification() });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilClearBundle = async () => {
    clearUtil();
    try {
      await ReactNativeBundleUpdate.clearBundle();
      setUtilResult({ cleared: true });
    } catch (err) {
      setUtilError(err instanceof Error ? err.message : 'Unknown error');
    }
  };
  const utilClearAll = () => {
    Alert.alert('Confirm', 'Clear ALL JS bundle data?', [
      { text: 'Cancel', style: 'cancel' },
      {
        text: 'Clear All',
        style: 'destructive',
        onPress: async () => {
          clearUtil();
          try {
            setUtilResult({ clearAll: await ReactNativeBundleUpdate.clearAllJSBundleData() });
          } catch (err) {
            setUtilError(err instanceof Error ? err.message : 'Unknown error');
          }
        },
      },
    ]);
  };
  const utilVerifyAllLocal = async () => {
    clearUtil();
    try {
      const bundles = await ReactNativeBundleUpdate.listLocalBundles();
      if (bundles.length === 0) {
        setUtilResult({ message: 'No local bundles found', bundles: [] });
        return;
      }
      const results: Record<string, string> = {};
      for (const b of bundles) {
        try {
          await ReactNativeBundleUpdate.verifyExtractedBundle(b.appVersion, b.bundleVersion);
          results[`${b.appVersion}-${b.bundleVersion}`] = 'OK';
        } catch (err) {
          results[`${b.appVersion}-${b.bundleVersion}`] =
            err instanceof Error ? err.message : 'error';
        }
      }
      setUtilResult({ verifyResults: results });
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
      title="Bundle Update"
    >
      {/* URL Input */}
      <View style={s.card}>
        <Text style={s.cardTitle}>BUNDLE URL</Text>
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
          label="Download Bundle"
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
          label="Verify Integrity"
          status={workflow.verify.status}
          errorMessage={workflow.verify.errorMessage}
          onAction={handleVerify}
          actionLabel="Verify"
          disabled={
            workflow.download.status !== 'completed' ||
            workflow.verify.status === 'active'
          }
        />

        <StepConnector active={workflow.verify.status === 'completed'} />

        <StepRow
          stepNumber={3}
          label="Install Bundle"
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

      {/* Dev Settings */}
      <View style={s.card}>
        <Text style={s.cardTitle}>DEVELOPER SETTINGS</Text>
        <ToggleRow
          label="Developer Mode"
          subLabel={KEY_DEV_MODE}
          value={devModeEnabled}
          onToggle={toggleDevMode}
        />
        <ToggleRow
          label="Skip GPG Verification"
          subLabel={KEY_SKIP_GPG}
          value={skipGPGEnabled}
          onToggle={toggleSkipGPG}
        />
        <Text style={s.infoText}>
          Both switches must be ON to skip GPG.{'\n'}Check native logs for:{'\n'}
          {'  '}GPG check: devSettings=X, skipGPGToggle=Y, skipGPG=Z
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
          <TestButton title="Get Web Embed Path" onPress={utilGetWebEmbedPath} />
          <TestButton title="Get JS Bundle Path" onPress={utilGetJsBundlePath} />
          <TestButton title="Get Native App Version" onPress={utilGetNativeAppVersion} />
          <TestButton title="Get Native Build Number" onPress={utilGetNativeBuildNumber} />
          <TestButton title="Get Builtin Bundle Version" onPress={utilGetBuiltinBundleVersion} />
          <TestButton title="Get Fallback Bundle Data" onPress={utilGetFallbackData} />
          <TestButton title="List Local Bundles" onPress={utilListLocal} />
          <TestButton title="List ASC Files" onPress={utilListAscFiles} />
          <TestButton title="Test Verification" onPress={utilTestVerification} />
          <TestButton title="Verify All Local Bundles" onPress={utilVerifyAllLocal} />
          <TestButton title="Clear Bundle" onPress={utilClearBundle} />
          <TestButton title="Clear All JS Bundle Data" onPress={utilClearAll} />
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

  // Toggle
  toggleRow: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingVertical: 10,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: C.border,
  },
  toggleInfo: {
    flex: 1,
    marginRight: 12,
  },
  toggleLabel: {
    fontSize: 14,
    fontWeight: '500',
    color: C.textPrimary,
  },
  toggleKey: {
    fontSize: 10,
    color: C.textTertiary,
    fontFamily: mono,
    marginTop: 2,
  },

  // Info text
  infoText: {
    fontSize: 11,
    color: C.textTertiary,
    fontFamily: mono,
    backgroundColor: C.cardBgElevated,
    padding: 10,
    borderRadius: 6,
    marginTop: 10,
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
