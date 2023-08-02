import React from 'react'
import PropTypes from 'prop-types'

function PostListElement({ id, title, createdTime, author }) {
    return (
        <div>
            {id}
            {title}
            {createdTime}
            {author.name}
        </div>
    );
}

PostListElement.propTypes = {
};

export default PostListElement;