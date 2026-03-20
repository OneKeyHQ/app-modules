export const filterOutRecentWallets = (recentWallets, wallets, resentCount) => {
  const recentIds = recentWallets?.slice(0, resentCount ?? 1).map(wallet => wallet.id);
  if (!recentIds?.length) return wallets ?? [];
  const filtered = wallets?.filter(wallet => !recentIds.includes(wallet.id)) || [];
  return filtered;
};
//# sourceMappingURL=utils.js.map