import React, { useContext, useState } from 'react'
import axios from "axios";

import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';

function CommentUpdate({ refreshCommentList, commentId, prevContent, setIsModify }) {
    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const [content, setContent] = useState(prevContent);

    const modifyComment = async () => {
        const url = `http://localhost:8081/api/v1/comments/${commentId}`;
        const params = { content };

        await axios.patch(url, params, { headers })
            .then((res) => {
                alert("댓글이 수정되었습니다.");
                refreshCommentList();
                setIsModify(false);
            })
            .catch((err) => {
                console.log("error occured");
                console.log(err);
            })
    }

    const changeContent = (event) => {
        setContent(event.target.value);
    }

    return (
        <div>{
            auth ?
                <div><textarea type="text" placeholder='내용' value={content} onChange={changeContent}></textarea>
                    <button onClick={modifyComment}>확인</button>
                </div>
                : null
        }</div>
    );
}

CommentUpdate.propTypes = {};

export default CommentUpdate;