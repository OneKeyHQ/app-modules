export interface ISharedStore {
  set(key: string, value: string | number | boolean): void;
  get(key: string): string | number | boolean | undefined;
  has(key: string): boolean;
  delete(key: string): boolean;
  keys(): string[];
  clear(): void;
  readonly size: number;
}

declare global {
  // eslint-disable-next-line no-var
  var sharedStore: ISharedStore | undefined;
}

export function getSharedStore(): ISharedStore | undefined {
  return globalThis.sharedStore;
}
