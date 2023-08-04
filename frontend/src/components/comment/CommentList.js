import React, { useContext, useEffect, useState } from 'react'
import axios from "axios";

import CommentWrite from './CommentWrite';
import { AuthContext } from '../../context/AuthProvider';
import CommentListElement from './CommentListElement';

function CommentList({ postId }) {
    const { auth } = useContext(AuthContext);

    const [isLoading, setIsLoading] = useState(true);
    const [comments, setComments] = useState([]);

    const params = {
        "postId": postId,
        "page": 1,
        "keyword": ""
    }

    const getCommentList = async (postId) => {
        const url = `http://localhost:8081/api/v1/comments`;
        await axios.get(url, { params })
            .then((res) => {
                setComments(res.data);
                setIsLoading(false);
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const refreshCommentList = () => {
        getCommentList(postId);
    }

    useEffect(() => {
        getCommentList(postId);
    }, []);

    return (
        <div>
            <CommentWrite
                postId={postId}
                parentCommentId={0}
                refreshCommentList={refreshCommentList} />
            댓글 목록
            {
                comments.map((c, index) => {
                    return (
                        <CommentListElement key={index}
                            comment={c}
                            postId={postId}
                            refreshCommentList={refreshCommentList} />
                    )
                })
            }
        </div>
    );
}


export default CommentList;