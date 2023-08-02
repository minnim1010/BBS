import { Route, Routes } from "react-router-dom"
import PostList from "./components/post/PostList"
import PostDetail from "./components/post/PostDetail"
import NotFound from "./components/NotFound";
import Home from "./components/Home";
import Header from "./components/layout/Header";
import Footer from "./components/layout/Footer";

function App() {
  return (
    <div>
      <Header />
      <Routes>
        <Route path="/" element={<PostList />} />
        <Route path="posts/" element={<PostList />} />
        <Route path="posts/*" element={<PostDetail />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
      <Footer />
    </div>
  );
}

export default App;
