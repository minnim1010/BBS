import React, { useEffect, useState } from 'react';
import axios from "axios";
import { useParams } from "react-router-dom";

import Loading from '../app/Loading';
import CommentList from "../comment/CommentList";

function PostDetail() {
    const [isLoading, setIsLoading] = useState(true);
    const [post, setPost] = useState([]);

    const getPost = async (postId) => {
        const url = `http://localhost:8081/api/v1/posts/${postId}`;
        await axios.get(url)
            .then((res) => {
                setPost(res.data);
                setIsLoading(false);
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const { postId } = useParams(); // 파라미터 가져오기
    useEffect(() => {
        getPost(postId);
    }, []);

    return (
        <div>
            {isLoading ? (
                <Loading />
            ) :
                <div>
                    <div>제목: {post.title}</div>
                    <div>작성 시각: {post.createdTime}</div>
                    {
                        post.modifiedTime ?
                            <div>수정 시각: {post.modifiedTime}</div> :
                            null
                    }
                    <div>작성자: {post.authorResponse.name}</div>
                    <div><p>{post.content}</p></div>

                    <div>
                        댓글 목록
                        <CommentList
                            postId={postId} />
                    </div>
                </div>
            }
        </div>

    );

}

export default PostDetail;