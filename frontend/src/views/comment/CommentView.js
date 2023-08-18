import React, { useContext, useEffect, useRef } from "react";
import axios from "axios";

import CommentWriteView from "./CommentWriteView";
import { AuthContext } from "../../context/AuthProvider";
import { API } from "../../config/url";
import { proxy, useSnapshot } from "valtio";
import CommentListModel from "../../entity/viewmodel/comment/CommentListModel";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import Loading from "../../components/basic/Loading";
import CommentListElementView from "./CommentListElementView";

function CommentView({ postId }) {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new CommentListModel())).current;
  const state = useSnapshot(model);

  const params = {
    postId: postId,
    page: 1,
    keyword: "",
  };

  const getCommentList = async (params, headers) => {
    await axios
      .get(`${API.COMMENT}`, { params, headers: headers })
      .then((response) => {
        const data = response.data;
        model.comments = data.content;
        const { content, ...pageData } = data;
        model.page = pageData;
        model.loading = false;
      })
      .catch((err) => {
        console.log("error occured");
      });
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
        <h3>{state.page.totalElements}개의 댓글</h3>
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
