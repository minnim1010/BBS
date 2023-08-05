import React, { useContext, useState } from 'react'
import axios from "axios";
import { Link } from 'react-router-dom';

import CommentWrite from './CommentWrite';
import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';
import CommentUpdate from './CommentUpdate';


function CommentListElement(prop) {
    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const { index, comment, postId, refreshCommentList } = prop;

    const [showRecommentWrite, setShowRecommentWrite] = useState(false)
    const [isModify, setIsModify] = useState(false);

    const deleteComment = async (id) => {
        const url = `http://localhost:8081/api/v1/comments/${id}`;
        await axios.delete(url, { headers })
            .then((res) => {
                alert("댓글이 삭제되었습니다.");
                refreshCommentList();
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const showRecommentWriteComponent = () => {
        if (showRecommentWrite)
            setShowRecommentWrite(false);
        else
            setShowRecommentWrite(true);
    }

    const showModifyComment = () => {
        setIsModify(true);
    }

    return (
        <div>
            <div key={index}>
                <div>{comment.createdTime}</div>
                <div>{comment.authorResponse.name}</div>
                {comment.modifiedTime && <div>{comment.modifiedTime}</div>}
                {
                    isModify ? <CommentUpdate
                        refreshCommentList={refreshCommentList}
                        commentId={comment.id}
                        prevContent={comment.content}
                        setIsModify={setIsModify} /> :
                        <div>{comment.content}</div>
                }
                {auth && <button onClick={showRecommentWriteComponent}>대댓글 달기</button>}
                {showRecommentWrite &&
                    <CommentWrite
                        postId={postId}
                        parentCommentId={comment.id}
                        refreshCommentList={refreshCommentList} />}
                {auth && <div>
                    <button onClick={showModifyComment}>수정</button>
                    <button onClick={() => deleteComment(comment.id)}>삭제</button>
                </div>}
            </div>
        </div>

    )
}

export default CommentListElement;