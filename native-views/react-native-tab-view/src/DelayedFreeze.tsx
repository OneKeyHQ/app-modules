import React from 'react';
import { Freeze } from 'react-freeze';

interface FreezeWrapperProps {
  freeze: boolean;
  children: React.ReactNode;
}

const ANIMATION_DELAY = 200;

function DelayedFreeze({ freeze, children }: FreezeWrapperProps) {
  const [freezeState, setFreezeState] = React.useState(false);

  React.useEffect(() => {
    const id = setTimeout(() => {
      setFreezeState(freeze);
    }, ANIMATION_DELAY);

    return () => {
      clearTimeout(id);
    };
  }, [freeze]);

  return <Freeze freeze={freeze ? freezeState : false}>{children}</Freeze>;
}

export default DelayedFreeze;
