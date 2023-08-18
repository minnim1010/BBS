import React, { useContext, useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useParams } from "react-router-dom";

import Loading from "../../components/basic/Loading";
import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import PostDetail from "../../components/post/PostDetail";
import { API } from "../../config/url";
import CommentView from "../comment/CommentView";

function PostDetailView() {
  const { postId } = useParams();

  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const [post, setPost] = useState([]);
  const [afterLoad, setAfterLoad] = useState(false);

  const navigate = useNavigate();

  const getPost = async () => {
    const url = `http://localhost:8081/api/v1/posts/${postId}`;
    await axios
      .get(url)
      .then((res) => {
        setPost(res.data);
        setAfterLoad(true);
      })
      .catch((err) => {
        console.log("error occured");
      });
  };

  const deletePost = async (postId, headers) => {
    await axios
      .delete(`${API.POST}/${postId}`, { headers })
      .then((res) => {
        alert("게시글이 삭제되었습니다.");
        navigate("/");
      })
      .catch((err) => {
        console.log("error occured");
      });
  };

  useEffect(() => {
    void getPost();
  }, []);

  return (
    <div>
      <div>
        {afterLoad ? (
          <div>
            <PostDetail
              post={post}
              isValidAuthor={auth === post.author.name}
              deletePost={() => deletePost(post.id, headers)}
            />
          </div>
        ) : (
          <Loading />
        )}
      </div>
      <CommentView postId={postId} />
    </div>
  );
}

export default PostDetailView;
