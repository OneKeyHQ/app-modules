export const getMessage = ({
  walletName,
  declined,
  errorType,
  isWeb
}) => {
  if (declined || errorType === 'declined') {
    return {
      title: 'Connection declined',
      description: 'Connection can be declined if a previous request is still active'
    };
  }
  switch (errorType) {
    case 'not_installed':
      return {
        title: 'App not installed'
      };
    case 'default':
      return {
        title: 'Connection error',
        description: 'There was an unexpected connection error.'
      };
    default:
      return {
        title: `Continue in ${walletName ?? 'Wallet'}`,
        description: isWeb ? 'Open and continue in a browser tab' : 'Accept connection request in the wallet'
      };
  }
};
//# sourceMappingURL=utils.js.map