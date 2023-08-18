import React, { createContext, useState } from "react";

export const HttpHeaderTokenContext = createContext();

function HttpHeaderTokenProvider({ children }) {
  const [headers, setHeaders] = useState(
    `Authorization: Bearer ${localStorage.getItem("access_token")}`,
  );
  const value = { headers, setHeaders };

  return (
    <HttpHeaderTokenContext.Provider value={value}>
      {children}
    </HttpHeaderTokenContext.Provider>
  );
}

export default HttpHeaderTokenProvider;
