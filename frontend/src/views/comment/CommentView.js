import React, { useContext, useEffect, useRef, useState } from "react";

import CommentWriteView from "./CommentWriteView";
import { AuthContext } from "../../context/AuthProvider";
import { API } from "../../api/url";
import { proxy, useSnapshot } from "valtio";
import CommentListModel from "../../entity/viewmodel/comment/CommentListModel";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import Loading from "../../components/basic/Loading";
import CommentListElementView from "./CommentListElementView";
import ApiClient from "../../api/ApiClient";

function CommentView({ postId }) {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new CommentListModel())).current;
  const state = useSnapshot(model);

  const params = {
    postId: postId,
  };

  const getCommentList = (params, headers) => {
      new ApiClient()
          .get(API.COMMENT, params, headers)
          .then(response => {
              model.comments = response;
              model.loading = false;
          })
  };

  const refreshCommentList = () => {
    void getCommentList(params, headers);
  };

  useEffect(() => {
    void getCommentList(params, headers);
  }, []);

  return (
    <div>
      <div>
        <h3>{state.comments.length}개의 댓글</h3>
      </div>
      {auth && (
        <div className="comment-write">
          <CommentWriteView
            postId={postId}
            commentId={null}
            parentCommentId={0}
            refreshCommentList={refreshCommentList}
            initialContent={""}
          />
        </div>
      )}
      {state.loading ? (
        <Loading />
      ) : (
        state.comments.map((c, index) => {
          return (
            <CommentListElementView
              key={index}
              comment={c}
              postId={postId}
              refreshCommentList={refreshCommentList}
            />
          );
        })
      )}
    </div>
  );
}

export default CommentView;
