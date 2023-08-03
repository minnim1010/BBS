import React, { useEffect, useState } from 'react'
import axios from "axios";

function CommentList({ postId }) {
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

    useEffect(() => {
        getCommentList(postId);
    }, []);

    return (
        <div>
            {
                comments.map((c, index) => {
                    return (
                        <div>
                            <CommentListElement
                                key={index}
                                id={c.id}
                                content={c.content}
                                createdTime={c.createdTime}
                                modifiedTime={c.modifiedTime}
                                author={c.authorResponse}
                            />
                        </div>
                    );
                })
            }
        </div>
    );
}

function CommentListElement({ id, content, createdTime, modifiedTime, author }) {
    return (
        <div>
            <td>{id}</td>
            <td>{content}</td>
            <td>{author.name}</td>
            <td>{createdTime}</td>
            {
                modifiedTime ?
                    <td>{modifiedTime}</td>
                    : null
            }
        </div>
    )
}

CommentList.propTypes = {};

export default CommentList;