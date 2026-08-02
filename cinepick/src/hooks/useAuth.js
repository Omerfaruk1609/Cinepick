import { useState, useEffect } from 'react';

const AUTH_KEY = 'cinepick_user';

export function useAuth() {
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem(AUTH_KEY);
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    try {
      if (user) {
        localStorage.setItem(AUTH_KEY, JSON.stringify(user));
      } else {
        localStorage.removeItem(AUTH_KEY);
      }
    } catch (err) {
      console.error('Auth kaydetme hatası:', err);
    }
  }, [user]);

  const login = (email, password, username) => {
    const newUser = {
      id: 'usr_' + Date.now(),
      email,
      username: username || email.split('@')[0],
      token: 'jwt_mock_token_' + Date.now(),
    };
    setUser(newUser);
    return newUser;
  };

  const register = (username, email, password) => {
    return login(email, password, username);
  };

  const logout = () => {
    setUser(null);
  };

  return {
    user,
    isAuthenticated: !!user,
    login,
    register,
    logout,
  };
}
