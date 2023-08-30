import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import Loading from "../../components/basic/Loading";
import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import PostDetail from "../../components/post/PostDetail";
import { API } from "../../api/url";
import CommentView from "../comment/CommentView";
import ApiClient from "../../api/ApiClient";

function PostDetailView() {
  const { postId } = useParams();

  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const [post, setPost] = useState([]);
  const [afterLoad, setAfterLoad] = useState(false);

  const navigate = useNavigate();

  const getPost = (postId, params, headers) => {
      new ApiClient()
          .get(`${API.POST}/${postId}`, params, headers)
          .then(response => {
              setPost(response)
              setAfterLoad(true)
          })
  };

  const deletePost = (postId, headers) => {
      new ApiClient()
          .delete(`${API.POST}/${postId}`, headers)
          .then(() => {
              alert("게시글이 삭제되었습니다.");
              navigate("/");
          })
  };

  useEffect(() => {
    getPost(postId, null, headers);
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
