import React from "react";
import { Route, Routes } from "react-router-dom";

import PostListView from "./views/post/PostListView";
import PostDetail from "./views/post/PostDetailView";
import NotFound from "./views/NotFound";
import PostWrite from "./views/post/PostWriteView";
import MemberJoin from "./views/member/MemberJoin";
import MemberLogin from "./views/member/MemberLogin";
import PostUpdateView from "./views/post/PostUpdateView";

function Router() {
  return (
    <Routes>
      <Route path="/" element={<PostListView />} />

      <Route path="/join" element={<MemberJoin />} />
      <Route path="/login" element={<MemberLogin />} />

      <Route path="/write" element={<PostWrite />} />
      <Route path="/update" element={<PostUpdateView />} />
      <Route path="/:postId" element={<PostDetail />} />
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

Router.propTypes = {};

export default Router;
