import React, { useContext, useEffect, useState } from 'react'
import axios from "axios";

import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';
import { useLocation, useNavigate } from 'react-router-dom';

function PostWrite() {
    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const location = useLocation();
    const queryParams = new URLSearchParams(location.search);
    const postId = queryParams.get('id');

    const [post, setPost] = useState([]);
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [category, setCategory] = useState("string");
    const [isModify, setIsModify] = useState(false);

    const navigate = useNavigate();

    useEffect(() => {
        if (!auth) {
            alert("로그인된 사용자만 글을 작성할 수 있습니다.");
            navigate("/login");
        }

        if (postId) {
            getPost();
            console.log(post);
            setCategory(post.category);
            setTitle(post.title);
            setContent(post.content);
            console.log(title);
            console.log(content);
            console.log(category);
            setIsModify(true);
        }
    }, [])

    const getPost = async () => {
        const url = `http://localhost:8081/api/v1/posts/${postId}`;
        await axios.get(url)
            .then((res) => {
                setPost(res.data);
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const writePost = async () => {
        const url = `http://localhost:8081/api/v1/posts`;
        const params = { title, content, category };

        await axios.post(url, params, { headers })
            .then((res) => {
                alert("글이 작성되었습니다.");
                const {
                    data:
                    post
                } = res;
                navigate(`/posts/${post.id}`);
            })
            .catch((err) => {
                console.log("error occured");
                console.log(err);
            })
    }

    const modifyPost = async () => {
        const url = `http://localhost:8081/api/v1/posts/${postId}`;
        const params = { title, content, category };

        await axios.patch(url, params, { headers })
            .then((res) => {
                alert("글이 수정되었습니다.");
                navigate(`/posts/${postId}`);
            })
            .catch((err) => {
                console.log("error occured");
                console.log(err);
            })
    }

    const changeCategory = (event) => {
        setCategory(event.target.value);
    }

    const changeTitle = (event) => {
        setTitle(event.target.value);
    }

    const changeContent = (event) => {
        setContent(event.target.value);
    }

    return (
        <div>
            <div>
                <select name="category" id="category-select" onChange={changeCategory}>
                    <option value="string">string</option>
                    <option value="Java">Java</option>
                    <option value="Spring">Spring</option>
                </select>
            </div>
            <div>{isModify ?
                <input type="text" value={title || ""} placeholder='제목' onChange={changeTitle} ></input>
                :
                <input type="text" placeholder='제목' onChange={changeTitle}></input>}
            </div>
            <div>
                {
                    isModify ? <textarea type="text" placeholder='내용' onChange={changeContent} value={content || ""}></textarea>
                        :
                        <textarea type="text" placeholder='내용' onChange={changeContent}></textarea>
                }
            </div>
            <button onClick={() => {
                if (isModify)
                    modifyPost();
                else
                    writePost();

            }}>확인</button>
        </div >
    );
}

PostWrite.propTypes = {};

export default PostWrite;