import React from "react";

import { Button, Divider } from "antd";
import { Link } from "react-router-dom";
import DateFormatter from "../../util/DateFormatter";

function PostDetail(props) {
  const { post, isValidAuthor, deletePost } = props;

  const createdTime = DateFormatter(post.createdTime);
  const modifiedTime = DateFormatter(post.modifiedTime);

  return (
    <div className="post-body">
      <div className="post-category">{post.category}</div>
      <div className="flex-between-elem">
        <div className="post-title">
          <h1>{post.title}</h1>
        </div>
        {isValidAuthor && (
          <div className="post-action-btn">
            <div className="post-update-btn">
              <Link to="/update" state={{ prevPost: post }}>
                <Button>수정</Button>
              </Link>
            </div>
            <div className="post-delete-btn">
              <Button onClick={deletePost}>삭제</Button>
            </div>
          </div>
        )}
      </div>
      <div className="flex">
        <div className="post-author">{post.author.name}</div>
        <div className="post-time">
          {createdTime === modifiedTime ? (
            <div>{createdTime} </div>
          ) : (
            <div>
              {createdTime}(최근 수정 {modifiedTime})
            </div>
          )}
        </div>
      </div>

      <Divider />
      <p>{post.content}</p>
      <Divider />
    </div>
  );
}

export default PostDetail;
