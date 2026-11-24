import { createUrAssembler } from './assembler';

import type {
  ScanMode,
  ScanReceiveResult,
  ScanSession,
  UrLimits,
} from './types';

const UR_PREFIX = /^ur:/i;

/**
 * One scan attempt. Every backend feeds decoded frame text here, so mode
 * handling and UR assembly behave the same on all platforms.
 */
export function createScanSession(options: {
  mode: ScanMode;
  allowPlainText?: boolean;
  limits?: Partial<UrLimits>;
}): ScanSession {
  const { mode, allowPlainText = false } = options;
  const assembler = createUrAssembler({ limits: options.limits });
  let finished = false;

  const route = (text: string): ScanReceiveResult => {
    if (mode === 'static') {
      return { status: 'complete', result: { kind: 'text', text } };
    }
    if (UR_PREFIX.test(text)) return assembler.receive(text);
    // Another code in view must not end a UR message that is half collected.
    if (assembler.isBound()) return { status: 'ignored', reason: 'foreign' };
    if (mode === 'auto' || allowPlainText) {
      return { status: 'complete', result: { kind: 'text', text } };
    }
    return { status: 'ignored', reason: 'not-ur' };
  };

  const receive = (frameText: string): ScanReceiveResult => {
    if (finished) return { status: 'ignored', reason: 'finished' };
    const text = frameText.trim();
    if (text.length === 0) return { status: 'ignored', reason: 'malformed' };
    const result = route(text);
    if (result.status === 'complete' || result.status === 'rejected') {
      finished = true;
    }
    return result;
  };

  return {
    receive,
    reset: () => {
      finished = false;
      assembler.reset();
    },
    stats: () => assembler.stats(),
  };
}
