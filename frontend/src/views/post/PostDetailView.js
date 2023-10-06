import React, { useContext, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import Loading from "../../components/basic/Loading";
import { AuthContext } from "../../context/AuthProvider";
import PostDetail from "../../components/post/PostDetail";
import { API } from "../../api/url";
import CommentView from "../comment/CommentView";
import ApiClient from "../../api/ApiClient";

function PostDetailView() {
  const { postId } = useParams();

  const { auth } = useContext(AuthContext);

  const [post, setPost] = useState([]);
  const [afterLoad, setAfterLoad] = useState(false);

  const navigate = useNavigate();

  const getPost = (postId, params) => {
    new ApiClient()
      .get(`${API.POST}/${postId}`, params, null)
      .then((response) => {
        setPost(response);
        setAfterLoad(true);
      });
  };

  const deletePost = (postId) => {
    new ApiClient().delete(`${API.POST}/${postId}`, null).then(() => {
      alert("게시글이 삭제되었습니다.");
      navigate("/");
    });
  };

  useEffect(() => {
    getPost(postId, null, null);
  }, []);

  return (
    <div>
      <div>
        {afterLoad ? (
          <div>
            <PostDetail
              post={post}
              isValidAuthor={auth === post.author.name}
              deletePost={() => deletePost(post.id)}
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
