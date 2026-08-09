import { useState, useEffect } from 'react';

const CURRENT_USER_KEY = 'cinepick_active_user';
const ACCOUNTS_KEY = 'cinepick_registered_users';

export function useAuth() {
  // Kayıtlı hesaplar listesi
  const [accounts, setAccounts] = useState(() => {
    try {
      const saved = localStorage.getItem(ACCOUNTS_KEY);
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  });

  // Aktif oturum açmış kullanıcı
  const [user, setUser] = useState(() => {
    try {
      const saved = localStorage.getItem(CURRENT_USER_KEY);
      return saved ? JSON.parse(saved) : null;
    } catch {
      return null;
    }
  });

  useEffect(() => {
    try {
      localStorage.setItem(ACCOUNTS_KEY, JSON.stringify(accounts));
    } catch (err) {
      console.error('Kayıtlı hesaplar saklama hatası:', err);
    }
  }, [accounts]);

  useEffect(() => {
    try {
      if (user) {
        localStorage.setItem(CURRENT_USER_KEY, JSON.stringify(user));
        localStorage.setItem('token', user.token || 'mock-token');
      } else {
        localStorage.removeItem(CURRENT_USER_KEY);
        localStorage.removeItem('token');
      }
    } catch (err) {
      console.error('Aktif oturum saklama hatası:', err);
    }
  }, [user]);

  // Giriş Yap: Kayıtlı hesabı doğrula
  const login = (emailOrUsername, password) => {
    const cleanInput = emailOrUsername.trim().toLowerCase();
    
    // Kayıtlı hesaplarda ara
    const existingUser = accounts.find(
      (acc) => acc.email.toLowerCase() === cleanInput || acc.username.toLowerCase() === cleanInput
    );

    if (!existingUser) {
      throw new Error('Bu e-posta veya kullanıcı adına ait bir hesap bulunamadı. Lütfen önce kayıt olun.');
    }

    if (existingUser.password !== password) {
      throw new Error('Girdiğiniz şifre hatalı. Lütfen tekrar deneyin.');
    }

    // Başarılı Giriş: Sabit hesabın bilgilerini oturuma al
    const loggedInUser = {
      id: existingUser.id,
      email: existingUser.email,
      username: existingUser.username,
      token: existingUser.token || 'jwt_' + existingUser.id,
    };

    setUser(loggedInUser);
    return loggedInUser;
  };

  // Kayıt Ol: Yeni kalıcı hesap oluştur
  const register = (username, email, password) => {
    const cleanEmail = email.trim().toLowerCase();
    const cleanUsername = username.trim().toLowerCase();

    // Çakışma kontrolü
    const emailExists = accounts.some((acc) => acc.email.toLowerCase() === cleanEmail);
    if (emailExists) {
      throw new Error('Bu e-posta adresi zaten kullanımda. Giriş yapmayı deneyin.');
    }

    const usernameExists = accounts.some((acc) => acc.username.toLowerCase() === cleanUsername);
    if (usernameExists) {
      throw new Error('Bu kullanıcı adı zaten alınmış. Farklı bir isim deneyin.');
    }

    // Sabit benzersiz ID
    const newAccount = {
      id: 'usr_' + cleanUsername.replace(/\s+/g, '_') + '_' + Math.floor(Math.random() * 10000),
      username: username.trim(),
      email: email.trim(),
      password: password,
      token: 'jwt_' + Date.now(),
      createdAt: new Date().toISOString(),
    };

    // Yeni hesabı kayıtlı hesaplar listesine ekle
    setAccounts((prev) => [...prev, newAccount]);

    // Oturumu aç
    const loggedInUser = {
      id: newAccount.id,
      email: newAccount.email,
      username: newAccount.username,
      token: newAccount.token,
    };

    setUser(loggedInUser);
    return loggedInUser;
  };

  // Çıkış Yap: Sadece aktif oturumu kapat, hesap silinmez!
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
