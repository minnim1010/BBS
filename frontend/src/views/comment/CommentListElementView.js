import React, { useContext, useState } from "react";
import axios from "axios";

import CommentWriteView from "./CommentWriteView";
import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import CommentUpdateView from "./CommentUpdateView";
import { API } from "../../config/url";
import { Button, Divider } from "antd";

function CommentListElementView(prop) {
  const { index, comment, postId, refreshCommentList } = prop;

  const { auth } = useContext(AuthContext);
  const { headers } = useContext(HttpHeaderTokenContext);

  const [showRecommentWrite, setShowRecommentWrite] = useState(false);
  const [isModify, setIsModify] = useState(false);

  const deleteComment = async (commentId, headers) => {
    await axios
      .delete(`${API.COMMENT}/${commentId}`, { headers: headers })
      .then((res) => {
        alert("댓글이 삭제되었습니다.");
        refreshCommentList();
      })
      .catch((err) => {
        console.log("error occured");
      });
  };

  const showReplyWrite = () => {
    if (showRecommentWrite) setShowRecommentWrite(false);
    else setShowRecommentWrite(true);
  };

  const showModifyComment = () => {
    setIsModify(true);
  };

  return (
    <div key={index}>
      <div className="comment-list">
        <div className="comment-additional-info">
          <div className="flex-between-elem">
            <div className="comment-author">{comment.author.name}</div>
            {auth === comment.author.name && (
              <div className="comment-action-btn flex">
                <div className="comment-update-btn">
                  <Button onClick={showModifyComment}>수정</Button>
                </div>
                <div className="comment-delete-btn">
                  <Button onClick={() => deleteComment(comment.id, headers)}>
                    삭제
                  </Button>
                </div>
              </div>
            )}
          </div>
          <div className="comment-time">
            {comment.createdTime === comment.modifiedTime ? (
              <div>{comment.createdTime}</div>
            ) : (
              <div>
                {comment.createdTime}(최근 수정 {comment.modifiedTime})
              </div>
            )}
          </div>
        </div>

        {isModify ? (
          <CommentUpdateView
            refreshCommentList={refreshCommentList}
            commentId={comment.id}
            prevContent={comment.content}
            setIsModify={setIsModify}
          />
        ) : (
          <div className="comment-content">{comment.content}</div>
        )}
        <div>
          {auth && (
            <Button type="primary" onClick={showReplyWrite}>
              답글 달기
            </Button>
          )}
        </div>
      </div>
      {showRecommentWrite && (
        <CommentWriteView
          postId={postId}
          parentCommentId={comment.id}
          refreshCommentList={refreshCommentList}
        />
      )}
      <Divider></Divider>
    </div>
  );
}

export default CommentListElementView;
