import React, { useContext, useRef } from "react";

import { AuthContext } from "../../context/AuthProvider";
import { API } from "../../constants/url";
import { proxy, useSnapshot } from "valtio";
import Comment from "../../entity/Comment";
import CommentWithAction from "../../components/comment/CommentWithAction";
import ApiClient from "../../api/ApiClient";

function CommentWriteView(props) {
  const { postId, parentCommentId, refresh } = props;

  const { auth } = useContext(AuthContext);

  const model = useRef(proxy(new Comment(""))).current;
  const state = useSnapshot(model);

  const writeComment = (content, postId, parentCommentId) => {
    const params = { content, postId, parentCommentId };

    new ApiClient().post(API.COMMENT, params, null).then(() => {
      alert("댓글이 작성되었습니다.");
      model.content = "";
      refresh();
    });
  };

  return (
    <div>
      {auth && (
        <CommentWithAction
          model={model}
          state={state}
          action={() => writeComment(state.content, postId, parentCommentId)}
        />
      )}
    </div>
  );
}

CommentWriteView.propTypes = {};

export default CommentWriteView;
