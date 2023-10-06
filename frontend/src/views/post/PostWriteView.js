import React, { useContext, useEffect, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { useNavigate } from "react-router-dom";
import Post from "../../entity/Post";
import { proxy, useSnapshot } from "valtio";
import PostWithAction from "../../components/post/PostWithAction";
import ApiClient from "../../api/ApiClient";
import { API } from "../../api/url";

function PostWriteView() {
  const { auth } = useContext(AuthContext);

  const initPost = {
    title: "",
    content: "",
    category: "string",
  };
  const model = useRef(proxy(new Post(initPost))).current;
  const state = useSnapshot(model);

  const navigate = useNavigate();

  useEffect(() => {
    if (!auth) {
      alert("로그인된 사용자만 글을 작성할 수 있습니다.");
      navigate("/login");
    }
  }, []);

  const writePost = () => {
    new ApiClient().post(API.POST, state, null).then((post) => {
      alert("글이 작성되었습니다.");
      navigate(`/${post.id}`);
    });
  };

  return <PostWithAction model={model} state={state} action={writePost} />;
}

export default PostWriteView;
