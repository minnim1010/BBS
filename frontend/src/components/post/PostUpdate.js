import React, { useContext, useEffect, useState } from 'react'
import axios from "axios";

import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';
import { useLocation, useNavigate } from 'react-router-dom';


function PostUpdate() {
    const { auth } = useContext(AuthContext);
    const { headers } = useContext(HttpHeaderTokenContext);

    const location = useLocation();
    const { prevPost } = location.state;

    const [title, setTitle] = useState(prevPost.title);
    const [content, setContent] = useState(prevPost.content);
    const [category, setCategory] = useState(prevPost.category);

    const navigate = useNavigate();

    useEffect(() => {
        if (!auth) {
            alert("로그인된 사용자만 글을 작성할 수 있습니다.");
            navigate("/login");
        }
    }, [])

    const modifyPost = async () => {
        const url = `http://localhost:8081/api/v1/posts/${prevPost.id}`;
        const params = { title, content, category };

        await axios.patch(url, params, { headers })
            .then((res) => {
                alert("글이 수정되었습니다.");
                navigate(`/posts/${prevPost.id}`);
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
            <div>
                <input type="text" value={title} placeholder='제목' onChange={changeTitle} ></input>
            </div>
            <div>
                <textarea type="text" placeholder='내용' onChange={changeContent} value={content}></textarea>
            </div>
            <button onClick={modifyPost}>확인</button>
        </div >);
}

PostUpdate.propTypes = {};

export default PostUpdate;