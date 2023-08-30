import React, { useEffect, useRef, useState } from "react";

import Loading from "../../components/basic/Loading";
import { proxy, useSnapshot } from "valtio";
import PostListModel from "../../entity/viewmodel/post/PostListModel";
import { API } from "../../api/url";
import { Pagination, Table } from "antd";
import { Link } from "react-router-dom";
import ApiClient from "../../api/ApiClient";

function PostListView() {
  const model = useRef(proxy(new PostListModel())).current;
  const state = useSnapshot(model);

  const queryParameters = new URLSearchParams(window.location.search);
  const page = queryParameters.get("page");
  const category = queryParameters.get("category");
  const searchScope = queryParameters.get("searchScope");
  const searchKeyword = queryParameters.get("searchKeyword");

  const [params, setParams] = useState({
    page: page ? page : 1,
    category: category ? category : "string",
    searchScope: searchScope ? searchScope : "",
    searchKeyword: searchKeyword ? searchKeyword : "",
  });

  const changePage = (page) => {
    setParams((prevParams) => ({ ...prevParams, page }));
  };

  const getPostList = (params, headers) => {
    new ApiClient()
        .get(API.POST, params, headers)
        .then(response => {
          model.posts = response.content;
          const { content, ...pageData } = response;
          model.page = pageData
          console.log(pageData)
          model.loading = false;
        })
  };

  useEffect(() => {
    void getPostList(params, null);
  }, [params.page]);

  const columns = [
    {
      title: "번호",
      dataIndex: "id",
      key: "id",
    },
    {
      title: "제목",
      dataIndex: "title",
      key: "title",
      render: (text, record) => <Link to={`/${record.id}`}>{text}</Link>,
    },
    {
      title: "작성자",
      dataIndex: ["author", "name"],
      key: "author.name",
    },
    {
      title: "작성일",
      dataIndex: "createdTime",
      key: "createdTime",
    },
  ];

  return (
    <div>
      {state.loading ? (
        <Loading />
      ) : (
        <div>
          <div className="post-table">
            <Table
              dataSource={state.posts}
              columns={columns}
              pagination={false}
              rowKey={(record) => record.id}
            />
          </div>
          <div className="post-pagination">
            <Pagination
              current={params.page}
              onChange={changePage}
              defaultPageSize={10}
              total={state.page.totalElements}
            />
          </div>
        </div>
      )}
    </div>
  );
}

export default PostListView;
