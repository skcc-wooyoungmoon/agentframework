import { createContext, useContext, useState, type FC, type ReactNode } from 'react';

// Auth 관련 타입과 컨텍스트
type AuthContextProps = {
  currentUser: {
    username?: string;
    project?: {
      id: string;
      name: string;
    };
  } | null;
  setCurrentUser: (user: any) => void;
  logout: () => void;
};

const AuthContext = createContext<AuthContextProps>({
  currentUser: null,
  setCurrentUser: () => { },
  logout: () => { },
});

const AuthProvider: FC<{ children: ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUserState] = useState<any>(null);

  const setCurrentUser = (user: any) => {
    setCurrentUserState(user);
    if (user?.project) {
      localStorage.setItem('selectedProject', JSON.stringify(user.project));
    }
  };

  const logout = () => {
    setCurrentUserState(null);
    localStorage.removeItem('selectedProject');
    localStorage.removeItem('projectId');
  };

  return <AuthContext.Provider value={{ currentUser, setCurrentUser, logout }}>{children}</AuthContext.Provider>;
};

const useAuth = () => {
  return useContext(AuthContext);
};

export { AuthProvider, useAuth };
