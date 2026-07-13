import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

import api from 'src/api/axios';
import LoadingScreen from 'src/components/loading/loadingScreen';
import { RouteLoading } from 'src/routes/components/route-loading';

type Props = {
  children: React.ReactNode;
};

export default function ProtectedRoute({ children }: Props) {

  const [loading, setLoading] = useState(true);
  const [authenticated, setAuthenticated] = useState(false);


  useEffect(() => {

    const verify = async () => {

      try {

        await api.get('/v1/me');

        setAuthenticated(true);

      } catch {

        setAuthenticated(false);

      } finally {

        setLoading(false);

      }

    };


    verify();

  }, []);



  if (loading) {
    return <LoadingScreen />;

  }



  if (!authenticated) {

    return (
      <Navigate
        to="/sign-in?reason=session_expired"
        replace
      />
    );

  }



  return <>{children}</>;

}