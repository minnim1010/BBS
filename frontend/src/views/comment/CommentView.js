import React, { useContext, useEffect, useRef, useState } from "react";
import axios from "axios";

import CommentWriteView from "./CommentWriteView";
import { AuthContext } from "../../context/AuthProvider";
import { API } from "../../config/url";
import { proxy, useSnapshot } from "valtio";
import CommentListModel from "../../entity/viewmodel/comment/CommentListModel";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import Loading from "../../components/basic/Loading";
import CommentListElementView from "./CommentListElementView";
import { Pagination } from "antd";

function CommentView({ postId }) {
  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState("");
  const model = useRef(proxy(new CommentListModel())).current;
  const state = useSnapshot(model);

  const params = {
    postId: postId,
    page: page,
    keyword: keyword,
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

  const onChange = (page) => {
    setPage(page);
  };

  useEffect(() => {
    void getCommentList(params, headers);
  }, [page, keyword]);

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
      <div className="comment-pagination">
        <Pagination
          current={page}
          onChange={onChange}
          defaultPageSize={20}
          total={state.page.totalElements}
        />
      </div>
    </div>
  );
}

export default CommentView;
