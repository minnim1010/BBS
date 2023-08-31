import React, {useContext, useState} from "react";
import {UserOutlined} from "@ant-design/icons";

import CommentWriteView from "./CommentWriteView";
import {AuthContext} from "../../context/AuthProvider";
import {HttpHeaderTokenContext} from "../../context/HttpHeaderTokenProvider";
import CommentUpdateView from "./CommentUpdateView";
import {API} from "../../api/url";
import {Button, Divider} from "antd";
import ApiClient from "../../api/ApiClient";
import ReplyListElementView from "./ReplyListElementView";

function CommentListElementView(prop) {
  const {index, comment, postId, refreshCommentList, depth} = prop;

  const {auth} = useContext(AuthContext);
  const {headers} = useContext(HttpHeaderTokenContext);

  const [showReplyWrite, setShowReplyWrite] = useState(false);
  const [ReplyWriteText, setReplyWriteText] = useState("답글 달기");
  const [isModify, setIsModify] = useState(false);

  const paddingValue = depth > 0 ? 3 : 0;
  const paddingStyle = {
    paddingLeft: `${paddingValue}rem`
  };

  const deleteComment = (commentId, headers) => {
    new ApiClient()
      .delete(`${API.COMMENT}/${commentId}`, headers)
      .then(() => {
        alert("댓글이 삭제되었습니다.");
        refreshCommentList();
      })
  };

  const toggleShowReplyWrite = () => {
    if (showReplyWrite) {
      setShowReplyWrite(false);
      setReplyWriteText("답글 달기");
    }
    else {
      setShowReplyWrite(true);
      setReplyWriteText("숨기기");
    }
  };

  const showModifyComment = () => {
    setIsModify(true);
  };

  return (
    <div key={index} style={paddingStyle}>
      <div className="comment-list-item">
        <div className="comment">
          <div className="flex-between-elem">
            <div className="profile">
              <div className="img">
                <UserOutlined style={{fontSize: '200%'}}/>
              </div>
              <div className="comment-info">
                <div className="username">{comment.author.name}</div>
                <div className="date">
                  {comment.createdTime === comment.modifiedTime ? (
                    <div>{comment.createdTime}</div>
                  ) : (
                    <div>
                      {comment.createdTime}(최근 수정 {comment.modifiedTime})
                    </div>
                  )}
                </div>
              </div>
            </div>
            {auth === comment.author.name && (
              <div className="comment-action-btn flex">
                <div className="comment-update-btn">
                  <Button onClick={showModifyComment}>
                    수정
                  </Button>
                </div>
                <div className="comment-delete-btn">
                  <Button onClick={() => deleteComment(comment.id, headers)}>
                    삭제
                  </Button>
                </div>
              </div>
            )}
          </div>

          {isModify ? (
            <CommentUpdateView
              refreshCommentList={refreshCommentList}
              commentId={comment.id}
              prevContent={comment.content}
              setIsModify={setIsModify}
            />
          ) : (
            <div className="content">
              {comment.content}
            </div>
          )}

        </div>
        <div>
          {
            comment.repliesCount === 0 && auth &&
            <Button type="primary" onClick={toggleShowReplyWrite}>
              {ReplyWriteText}
            </Button>
          }
          {
            comment.repliesCount > 0 &&
            <ReplyListElementView
              postId={postId}
              commentId={comment.id}
              repliesCount={comment.repliesCount}
              refreshCommentList={refreshCommentList}
              depth={depth + 1}
            />
          }
        </div>
      </div>
      {showReplyWrite && (
        <CommentWriteView
          postId={postId}
          parentCommentId={comment.id}
          refresh={refreshCommentList}
        />
      )}
      <Divider></Divider>
    </div>
  );
}

export default CommentListElementView;
