import React, { useContext, useEffect, useState } from 'react';
import axios from "axios";
import { Link, useNavigate, useParams } from "react-router-dom";

import Loading from '../app/Loading';
import CommentList from "../comment/CommentList";
import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';

function PostDetail() {
    const { postId } = useParams();

    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const [post, setPost] = useState([]);
    const [afterLoad, setAfterLoad] = useState(false);

    const navigate = useNavigate();

    const getPost = async () => {
        const url = `http://localhost:8081/api/v1/posts/${postId}`;
        await axios.get(url)
            .then((res) => {
                setPost(res.data);
                setAfterLoad(true);
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const deletePost = async () => {
        const url = `http://localhost:8081/api/v1/posts/${postId}`;
        await axios.delete(url, { headers })
            .then((res) => {
                alert("게시글이 삭제되었습니다.");
                navigate(-1);
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    useEffect(() => {
        getPost();
    }, []);

    const updatePost = {
        id: post.id,
        title: post.title,
        content: post.content,
        category: post.category
    }

    return (
        <div>
            <div>
                {
                    afterLoad ?
                        <div>
                            < div > 제목: {post.title}</div >
                            <div>작성 시각: {post.createdTime}</div>
                            {post.modifiedTime && <div>수정 시각: {post.modifiedTime}</div>}
                            <div>작성자: {post.author.name}</div>
                            <div><p>{post.content}</p></div>
                            {
                                auth === post.author.name &&
                                <div>
                                    <Link to={`/posts/update`} state={{ prevPost: updatePost }}><button>수정</button></Link>
                                    <button onClick={deletePost}>삭제</button>
                                </div>
                            }
                        </div > :
                        <Loading />
                }
            </div>
            <CommentList postId={postId} />
        </div>
    );
}



export default PostDetail;