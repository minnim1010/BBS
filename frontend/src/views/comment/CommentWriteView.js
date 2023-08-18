import React, { useContext, useRef } from "react";
import axios from "axios";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { API } from "../../config/url";
import { proxy, useSnapshot } from "valtio";
import Comment from "../../entity/Comment";
import CommentWithAction from "../../components/comment/CommentWithAction";

function CommentWriteView(props) {
  const { postId, parentCommentId, refreshCommentList } = props;

  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new Comment(""))).current;
  const state = useSnapshot(model);

  const writeComment = async (content, headers) => {
    const params = { content, postId, parentCommentId };

    await axios
      .post(`${API.COMMENT}`, params, { headers })
      .then((res) => {
        alert("댓글이 작성되었습니다.");
        model.content = "";
        refreshCommentList();
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
          action={() => writeComment(state.content, headers)}
        />
      ) : null}
    </div>
  );
}

CommentWriteView.propTypes = {};

export default CommentWriteView;
