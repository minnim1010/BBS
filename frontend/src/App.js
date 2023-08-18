import Header from "./components/basic/Header";
import Footer from "./components/basic/Footer";
import Router from "./router";
import AuthProvider from "./context/AuthProvider";
import HttpHeaderTokenProvider from "./context/HttpHeaderTokenProvider";
import Nav from "./components/basic/Nav";

import "./style/basic.css";

function App() {
  // const initializeUserInfo = async () => {
  //   const loggedInfo = localStorage.getItem("username");
  //   if (!loggedInfo) return;
  // };

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
