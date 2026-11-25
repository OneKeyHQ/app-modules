import { Buffer } from 'buffer';

const g = globalThis as unknown as {
  Buffer?: typeof Buffer;
  process?: { env: Record<string, string> };
};
if (!g.Buffer) g.Buffer = Buffer;
if (!g.process) g.process = { env: {} };
