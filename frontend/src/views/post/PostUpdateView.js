import React, { useContext, useEffect, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { useLocation, useNavigate } from "react-router-dom";
import { API } from "../../api/url";
import Post from "../../entity/Post";
import { proxy, useSnapshot } from "valtio";
import PostWithAction from "../../components/post/PostWithAction";
import ApiClient from "../../api/ApiClient";

function PostUpdateView() {
  const { auth } = useContext(AuthContext);

  const { prevPost } = useLocation().state;

  const model = useRef(proxy(new Post(prevPost))).current;
  const state = useSnapshot(model);

  const navigate = useNavigate();

  useEffect(() => {
    if (!auth) {
      alert("로그인된 사용자만 글을 작성할 수 있습니다.");
      navigate("/login");
    }
  }, []);

  const modifyPost = () => {
    new ApiClient()
      .patch(`${API.POST}/${prevPost.id}`, state, null)
      .then(() => {
        alert("글이 수정되었습니다.");
        navigate(`/${prevPost.id}`);
      });
  };

  return <PostWithAction model={model} state={state} action={modifyPost} />;
}

export default PostUpdateView;
