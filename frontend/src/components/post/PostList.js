import React, { useContext, useEffect, useState } from 'react';
import axios from "axios";

import Loading from '../app/Loading';
import { Link } from 'react-router-dom';
import { AuthContext } from '../../context/AuthProvider';

function PostList() {
    const { auth, setAuth } = useContext(AuthContext);

    const [isLoading, setIsLoading] = useState(true);
    const [posts, setPosts] = useState([]);
    const [page, setPage] = useState([]);
    const [params, setParams] = useState({
        page: 1,
        category: "string",
        searchScope: "",
        searchKeyword: ""
    });

    const getPostList = async () => {
        const url = "http://localhost:8081/api/v1/posts";
        const {
            data:
            response
        } = await axios.get(url, { params });
        setPosts(response.content);
        const { content, ...pageData } = response;
        setPage(pageData)
        setIsLoading(false);
    }

    useEffect(() => {
        getPostList();
    }, []);

    return (
        <div>
            {isLoading ? (
                <Loading />
            ) :
                (<div>
                    {posts.map((post, index) => {
                        return (
                            <PostListElement
                                key={index}
                                id={post.id}
                                title={post.title}
                                createdTime={post.createdTime}
                                author={post.authorResponse}
                            />
                        );
                    })}

                    {
                        (auth) ?
                            <Link to="/posts/write"><button>글쓰기</button></Link>
                            : null
                    }

                </div>
                )}
        </div>
    );
}

function PostListElement({ id, title, createdTime, author }) {
    return (
        <Link to={{ pathname: `/posts/${id}` }}>
            <div>
                <td>{id}</td>
                <td>{title}</td>
                <td>{author.name}</td>
                <td>{createdTime}</td>
            </div>
        </Link>
    );
}

export default PostList;