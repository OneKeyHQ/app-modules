interface Props {
    callback: ((args?: any) => any) | ((args?: any) => Promise<any>);
    delay?: number;
}
export declare function useDebounceCallback({ callback, delay }: Props): {
    debouncedCallback: (args?: any) => void;
    abort: () => void;
};
export {};
//# sourceMappingURL=useDebounceCallback.d.ts.map