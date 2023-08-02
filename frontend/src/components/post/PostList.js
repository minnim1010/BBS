import React, { Component } from 'react';
import PropTypes from 'prop-types';
import axios from "axios";

import PostListElement from "./PostListElement"

class PostList extends Component {
    state = {
        isLoading: true,
        posts: [],
        page: [],
        request: {
            page: 1,
            category: "string",
            searchScope: "",
            searchKeyword: ""
        }
    };

    getPostList = async () => {
        const url = "http://localhost:8081/api/v1/posts";
        const {
            data:
            response
        } = await axios.get(url);
        const posts = response.content;
        const { content, ...pageData } = response;
        const page = pageData;
        this.setState({ posts, isLoading: false, page });
    }

    componentDidMount() {
        this.getPostList();
    }

    render() {
        const { isLoading, posts } = this.state;

        return (
            <div>
                {isLoading ? (
                    <div> <span> Loading...</span > </div>
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
                    </div>
                    )}
            </div>
        );
    }
}

PostList.propTypes = {

};

export default PostList;