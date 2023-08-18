import React, { useContext, useRef } from "react";
import axios from "axios";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { API } from "../../config/url";
import CommentWithAction from "../../components/comment/CommentWithAction";
import { proxy, useSnapshot } from "valtio";
import Comment from "../../entity/Comment";

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

  const modifyComment = async (content, commentId, headers) => {
    const params = { content };

    await axios
      .patch(`${API.COMMENT}/${commentId}`, params, { headers })
      .then((res) => {
        alert("댓글이 수정되었습니다.");
        refreshCommentList();
        setIsModify(false);
      })
      .catch((err) => {
        console.log("error occured");
        console.log(err);
      });
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
