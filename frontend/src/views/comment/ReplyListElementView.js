import React, {useContext, useState} from "react";

import ApiClient from "../../api/ApiClient";
import CommentListElementView from "./CommentListElementView";

import {API} from "../../api/url";
import {AuthContext} from "../../context/AuthProvider";
import {Button} from "antd";
import CommentWriteView from "./CommentWriteView";

function ReplyListElementView(props) {

  const {auth} = useContext(AuthContext);

  const {postId, commentId, repliesCount, refreshCommentList, depth} = props;

  const [showReplies, setShowReplies] = useState(false);
  const [repliesText, setRepliesText] = useState(`답글 ${repliesCount}개`)
  const [replies, setReplies] = useState([]);
  const [showReplyWrite, setShowReplyWrite] = useState(false);

  const getReplies = (commentId) => {
    new ApiClient()
      .get(`${API.COMMENT}/${commentId}/replies`, null, null)
      .then((response) => {
          setReplies(response)
          setShowReplies(true)
        }
      )
  }

  const toggleShowReplies = (commentId) => {
    if(showReplies) {
      setShowReplies(false)
      setRepliesText(`답글 ${repliesCount}개`)
    }
    else{
      if(replies.length === 0){
        getReplies(commentId)
      }
      setShowReplies(true)
      setRepliesText("숨기기")
    }
  }

  const toggleShowReplyWrite = () => {
    if (showReplyWrite) setShowReplyWrite(false);
    else setShowReplyWrite(true);
  };

  return (
    <div>
      <div>
        <Button type="primary" onClick={() => toggleShowReplies(commentId)}>
          {repliesText}
        </Button>
      </div>
      <div>
        {
          showReplies && (
            replies.map((r) => {
              return (
                <CommentListElementView
                  key={postId}
                  comment={r}
                  postId={postId}
                  refreshCommentList={refreshCommentList}
                  depth = {depth}
                />
              )
            }))
        }
      </div>
      <div>
        {
          auth && showReplies && !showReplyWrite &&
          <Button type="primary" onClick={toggleShowReplyWrite} block>
            답글 달기
          </Button>
        }
        {showReplyWrite && (
          <CommentWriteView
            postId={postId}
            parentCommentId={commentId}
            refresh={() => getReplies(commentId)}
          />
        )}
      </div>
    </div>
  )
}

export default ReplyListElementView