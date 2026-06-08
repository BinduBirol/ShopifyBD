import { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';

export function useRouteLoading() {
  const location = useLocation();
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);

    const timer = setTimeout(() => {
      setLoading(false);
    }, 300); // smooth UX delay

    return () => clearTimeout(timer);
  }, [location.pathname]);

  return loading;
}