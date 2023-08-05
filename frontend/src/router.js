import React from 'react'
import { Route, Routes } from 'react-router-dom';

import PostList from "./components/post/PostList"
import PostDetail from "./components/post/PostDetail"
import NotFound from "./components/NotFound";
import Home from "./components/app/Home";
import PostWrite from "./components/post/PostWrite";
import MemberJoin from "./components/member/MemberJoin";
import MemberLogin from './components/member/MemberLogin';
import PostUpdate from './components/post/PostUpdate';

function Router() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />

            <Route path="/join" element={<MemberJoin />} />
            <Route path="/login" element={<MemberLogin />} />

            <Route path="/posts" element={<PostList />} />
            <Route path="/posts/write" element={<PostWrite />} />
            <Route path="/posts/update" element={<PostUpdate />} />
            <Route path="/posts/:postId" element={<PostDetail />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    );
}

Router.propTypes = {};

export default Router;