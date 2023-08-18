import React, { useContext, useEffect, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { useNavigate } from "react-router-dom";
import Post from "../../entity/Post";
import { proxy, useSnapshot } from "valtio";
import PostWithAction from "../../components/post/PostWithAction";
import axios from "axios";
import { API } from "../../config/url";

function PostWriteView() {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

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

  const writePost = async () => {
    const data = state;

    await axios
      .post(`${API.POST}`, data, { headers: headers })
      .then((res) => {
        alert("글이 작성되었습니다.");
        const { data: post } = res;
        navigate(`/${post.id}`);
      })
      .catch((err) => {
        console.log("error occured");
        console.log(err);
      });
  };

  return <PostWithAction model={model} state={state} action={writePost} />;
}

export default PostWriteView;
