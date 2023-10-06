import React, { useContext, useEffect, useRef } from "react";

import CommentWriteView from "./CommentWriteView";
import { AuthContext } from "../../context/AuthProvider";
import { API } from "../../api/url";
import { proxy, useSnapshot } from "valtio";
import CommentListModel from "../../entity/viewmodel/comment/CommentListModel";
import Loading from "../../components/basic/Loading";
import CommentListElementView from "./CommentListElementView";
import ApiClient from "../../api/ApiClient";

function CommentView({ postId }) {
  const { auth } = useContext(AuthContext);

  const model = useRef(proxy(new CommentListModel())).current;
  const state = useSnapshot(model);

  const params = {
    postId: postId,
  };

  const getCommentList = (params) => {
    new ApiClient().get(API.COMMENT, params, null).then((response) => {
      model.comments = response;
      model.loading = false;
    });
  };

  const refreshCommentList = () => {
    void getCommentList(params);
  };

  useEffect(() => {
    void getCommentList(params);
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
            parentCommentId={0}
            refresh={refreshCommentList}
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
              depth={0}
            />
          );
        })
      )}
    </div>
  );
}

export default CommentView;
