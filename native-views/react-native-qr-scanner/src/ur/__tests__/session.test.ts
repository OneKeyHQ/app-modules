import { UR, UREncoder } from '@ngraveio/bc-ur';

import { createScanSession } from '../session';

const cbor = Buffer.concat([Buffer.from([0x58, 200]), Buffer.alloc(200, 7)]);
const parts = (() => {
  const encoder = new UREncoder(new UR(cbor, 'bytes'), 50);
  return Array.from({ length: 6 }, () => encoder.nextPart());
})();
const singleUr = UREncoder.encodeSinglePart(
  new UR(Buffer.from([0x43, 1, 2, 3]), 'bytes')
);

describe('createScanSession', () => {
  it('static mode returns any text, UR or not', () => {
    expect(createScanSession({ mode: 'static' }).receive(` ${singleUr} `)).toEqual({
      status: 'complete',
      result: { kind: 'text', text: singleUr },
    });
  });

  it('auto mode returns plain text and assembles UR', () => {
    expect(createScanSession({ mode: 'auto' }).receive('lightning:LNBC1')).toEqual({
      status: 'complete',
      result: { kind: 'text', text: 'lightning:LNBC1' },
    });
    expect(
      createScanSession({ mode: 'auto' }).receive(singleUr.toUpperCase())
    ).toMatchObject({ status: 'complete', result: { kind: 'ur', type: 'bytes' } });
  });

  it('auto mode ignores other codes while a UR message is half collected', () => {
    const session = createScanSession({ mode: 'auto' });
    expect(session.receive(parts[0]!).status).toBe('progress');
    expect(session.receive('https://example.com')).toEqual({
      status: 'ignored',
      reason: 'foreign',
    });
  });

  it('ur mode ignores plain text unless allowed', () => {
    expect(createScanSession({ mode: 'ur' }).receive('0xabc')).toEqual({
      status: 'ignored',
      reason: 'not-ur',
    });
    expect(
      createScanSession({ mode: 'ur', allowPlainText: true }).receive('0xabc')
    ).toEqual({ status: 'complete', result: { kind: 'text', text: '0xabc' } });
  });

  it('stays finished after a rejection until reset', () => {
    const session = createScanSession({
      mode: 'auto',
      limits: { maxMessageBytes: 10 },
    });
    expect(session.receive(parts[0]!).status).toBe('rejected');
    expect(session.receive('plain text')).toEqual({
      status: 'ignored',
      reason: 'finished',
    });
    session.reset();
    expect(session.receive('plain text').status).toBe('complete');
  });

  it('ignores empty frames', () => {
    expect(createScanSession({ mode: 'static' }).receive('   ')).toEqual({
      status: 'ignored',
      reason: 'malformed',
    });
  });
});
