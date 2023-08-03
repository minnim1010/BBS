import Header from "./components/app/Header";
import Footer from "./components/app/Footer";
import Router from "./router";
import AuthProvider from "./context/AuthProvider";
import HttpHeaderTokenProvider from "./context/HttpHeaderTokenProvider";
import Nav from "./components/app/Nav";


function App() {
  return (
    <div>
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
