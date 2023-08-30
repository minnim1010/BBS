import React, { useContext, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { API } from "../../api/url";
import CommentWithAction from "../../components/comment/CommentWithAction";
import { proxy, useSnapshot } from "valtio";
import Comment from "../../entity/Comment";
import ApiClient from "../../api/ApiClient";

function CommentUpdateView({
  refreshCommentList,
  commentId,
  prevContent,
  setIsModify,
}) {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new Comment(prevContent))).current;
  const state = useSnapshot(model);

  const modifyComment = (content, commentId, headers) => {
    const data = { content };

    new ApiClient()
        .patch(`${API.COMMENT}/${commentId}`, data, headers)
        .then(() => {
          alert("댓글이 수정되었습니다.");
          refreshCommentList();
          setIsModify(false);
        })
  };

  return (
    <div>
      {auth ? (
        <CommentWithAction
          model={model}
          state={state}
          action={() => modifyComment(state.content, commentId, headers)}
        />
      ) : null}
    </div>
  );
}

CommentUpdateView.propTypes = {};

export default CommentUpdateView;
