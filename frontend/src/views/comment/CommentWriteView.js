import React, { useContext, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { API } from "../../api/url";
import { proxy, useSnapshot } from "valtio";
import Comment from "../../entity/Comment";
import CommentWithAction from "../../components/comment/CommentWithAction";
import ApiClient from "../../api/ApiClient";

function CommentWriteView(props) {
  const { postId, parentCommentId, refreshCommentList } = props;

  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new Comment(""))).current;
  const state = useSnapshot(model);

  const writeComment = (content, postId, parentCommentId, headers) => {
    const params = { content, postId, parentCommentId };

    new ApiClient()
        .post(API.COMMENT, params, headers)
        .then(() => {
          alert("댓글이 작성되었습니다.");
          model.content = "";
          refreshCommentList();
        })
  };

  return (
    <div>
      {auth ? (
        <CommentWithAction
          model={model}
          state={state}
          action={() => writeComment(state.content, postId, parentCommentId, headers)}
        />
      ) : null}
    </div>
  );
}

CommentWriteView.propTypes = {};

export default CommentWriteView;
