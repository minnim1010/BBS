import React, { useContext, useEffect, useRef } from "react";
import axios from "axios";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { useLocation, useNavigate } from "react-router-dom";
import { API } from "../../config/url";
import Post from "../../entity/Post";
import { proxy, useSnapshot } from "valtio";
import PostWithAction from "../../components/post/PostWithAction";

function PostUpdateView() {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

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

  const modifyPost = async () => {
    const url = `${API.POST}/${prevPost.id}`;
    const data = state;

    await axios
      .patch(url, data, { headers: headers })
      .then((res) => {
        alert("글이 수정되었습니다.");
        navigate(`/${prevPost.id}`);
      })
      .catch((err) => {
        console.log("error occured");
        console.log(err);
      });
  };

  return <PostWithAction model={model} state={state} action={modifyPost} />;
}

export default PostUpdateView;
