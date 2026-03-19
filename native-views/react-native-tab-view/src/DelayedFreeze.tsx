import React from 'react';
import { Freeze } from 'react-freeze';

const ANIMATION_DELAY = 200;

interface FreezeWrapperProps {
  freeze: boolean;
  delayedFreeze?: boolean;
  children: React.ReactNode;
}

function DelayedFreeze({ freeze, delayedFreeze, children }: FreezeWrapperProps) {
  const [freezeState, setFreezeState] = React.useState(false);

  React.useEffect(() => {
    if (!delayedFreeze) {
      return;
    }
    const id = setTimeout(() => {
      setFreezeState(freeze);
    }, ANIMATION_DELAY);

    return () => {
      clearTimeout(id);
    };
  }, [freeze, delayedFreeze]);

  const shouldFreeze = delayedFreeze ? (freeze ? freezeState : false) : freeze;

  return <Freeze freeze={shouldFreeze}>{children}</Freeze>;
}

export default DelayedFreeze;
