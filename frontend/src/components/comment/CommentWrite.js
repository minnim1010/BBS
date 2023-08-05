import React, { useContext, useState } from 'react'
import axios from "axios";

import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';

function CommentWrite({ postId, parentCommentId, refreshCommentList }) {
    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const [content, setContent] = useState("");

    const writePost = async () => {
        const url = `http://localhost:8081/api/v1/comments`;
        const params = { content, postId, parentCommentId };

        await axios.post(url, params, { headers })
            .then((res) => {
                alert("댓글이 작성되었습니다.");
                refreshCommentList();
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
                <div>
                    <textarea type="text" placeholder='내용' onChange={changeContent}></textarea>
                    <button onClick={writePost}>확인</button>
                </div>
                : null
        }</div>
    );
}

CommentWrite.propTypes = {};

export default CommentWrite;