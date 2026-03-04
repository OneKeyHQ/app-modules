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
  onGoHome: () => void;
  safeAreaInsets: any;
}

const INITIAL_WORKFLOW: WorkflowState = {
  download: { status: 'pending' },
  verify: { status: 'pending' },
  install: { status: 'pending' },
  downloadProgress: 0,
  downloadResult: null,
};

export function BundleUpdateTestPage({
  onGoHome,
  safeAreaInsets,
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
  "sha256": "8c71473ccb1c590e8c13642559600eb0c8e2649c9567236e2ac27e79e919a12c",
  "size": 23123,
  "generatedAt": "2025-10-22T09:50:50.446Z"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmj/ZF0OHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHuVZhAArMmwReTpiw+XoKTw7bwlVrz0OWHfAkdh6lFY
xQpGj+AsY38NKJImrK7IQLhcnTJIwycY0a5eh8Wnqs0sxtmmwwyWQs+RHSwIdlTJ
CLpTUGxowNiD0ldz0LVLjPFqZz3/fYKkpGW1+ejkMdRXBbUrFGTa+XsEd0k3TWj2
bxFrhy128SpQ1NJ8AXXWRzZaenFAADa5ZEJUMV4Q8sjV+C8OXtVKeW1IDXAvWEzx
x9SWU4HD4ciKYT6yRZ6RuHJ3YXFdIDPMrPXDSPTjcZUnhsadT0qFoRck6ya4uyQP
SNvEge9W9Kcup0XfKkK5SnIRyZeKgW5Zn39W8C5equqmrGy581E6R28KS3KHsE66
Pf6WmVE/XuAKt5F++TmC6RBZ9PISPdOVhWcPZ74ySsFOUQ0nswMg1GLQ/kfixXIl
8ejFGhzhCRDmxYZ1aEJeMAAQhBuXM5TKtY79TIT9lNlttM0J/hl3rTTVxt9xSsMW
MCduz+A1mdO8T/DPqvpJksOO/YOT4gzHT9OSXNsYdte1QJKHmQdeAfzi/m66Z2/L
1qqTvwH3byXreUAjXwAWZLIbAQJ6zeeIrVKiut7DCJOHE+kGS2vdQiM2NmRFE0hP
qxdzLH784DPCWB36Xd3VZfbUxKOc06+bHlFCEXyylWD3schXV9c8Amz4DoriYIdi
Ni3q+jg=
=BVQy
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
  "sha256": "cece2fccea3c3a43e0da7ab803f44e5a4850a8b06f2df06db3e7f16860080a40",
  "size": 28919,
  "generatedAt": "2026-01-06T05:42:35.407Z",
  "appType": "android",
  "appVersion": "5.19.2",
  "buildNumber": "2026010644",
  "bundleVersion": "2"
}
-----BEGIN PGP SIGNATURE-----

iQJCBAEBCAAsFiEE62iuVE8f3YzSZGJPs2mmepC/OHsFAmlcvy8OHGRldkBvbmVr
ZXkuc28ACgkQs2mmepC/OHtxlA//QEvclfq0X9isJXBHFsRZx+JhfGOao60Sl0rW
m11AU6utvOAXHwxnhtLENuB2cDhhKkDrN582R2QhsdRJngRqWafwuBaVBJx+ErZV
KqvAlTj9hcLACXBw/dOyQ4JwDwm2jwloH4H//eiQdFcp1MT/uiGf3Yu9fonEC6ap
URBiiA7wAPg2o9V6zJchv/CM/xGA9G/I337lR0yAID2Y6Oteu9CftCSGqav4va4O
C94nW/wWQdt+XllY+i46mXxKOOoaIxUMP5K6q1q5CcjZBNm6Pgkay5YEDmbershP
/DeSpHwTk2APOIoaw1JVeKP8HrhIg4iGjmrUkveBoHJrGu1x6FjZ0tGqVJkG7b8f
wuBBHoPlCULbR38eFudv6UBYsJ2Zb2MEwMEC4quBczU6wg4NnOSKFK03kqsStyxG
0F+HShqAPZZrvGUUOMBMWhyxpDmbslkXtQntPhaMM8+NGqegTMiLZQnWkvzCIdoc
EYJOSlAICF/VkFzo8+LSuUgCQbqXF5qnFhcsIdzR8rguFKHC9/8/umlVjua5ilnu
bxULNIeYztMeXB29J8JXpu4efz+v5r9/HsddXlY0wMrmEWNiw+bG+ruT3O9pC9k9
hGatKzsRToFrdoTaHG6xnKhiVH7MhQHGjvEK5KpyXIvQxy9SCIloAqs0oXrW4Yuz
H3bEFZ8=
=ZjEV
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
      onGoHome={onGoHome}
      safeAreaInsets={safeAreaInsets}
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
