import Header from "./components/basic/Header";
import Footer from "./components/basic/Footer";
import Router from "./router";
import AuthProvider from "./context/AuthProvider";
import HttpHeaderTokenProvider from "./context/HttpHeaderTokenProvider";
import Nav from "./components/basic/Nav";

import "./style/basic.css";

function App() {
  return (
    <div className="basic">
      <Header />
      <AuthProvider>
        <HttpHeaderTokenProvider>
          <Nav />
          <Router />
        </HttpHeaderTokenProvider>
      </AuthProvider>
      <Footer />
    </div>
  );
}

export default App;
