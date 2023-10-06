import Header from "./components/basic/Header";
import Footer from "./components/basic/Footer";
import Router from "./router";
import AuthProvider from "./context/AuthProvider";
import Nav from "./components/basic/Nav";

import "./style/basic.css";

function App() {
  return (
    <div className="basic">
      <Header />
      <AuthProvider>
        <Nav />
        <Router />
      </AuthProvider>
      <Footer />
    </div>
  );
}

export default App;
