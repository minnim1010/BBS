import React, { createContext, useState } from "react";
import { USER_INFO_KEY } from "../constants/LocalStorageKey";

export const AuthContext = createContext();

function AuthProvider({ children }) {
  const [auth, setAuth] = useState(localStorage.getItem(USER_INFO_KEY));
  const value = { auth, setAuth };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export default AuthProvider;
