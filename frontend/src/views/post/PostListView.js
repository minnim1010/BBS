import React, { useContext, useEffect, useRef, useState } from "react";

import Loading from "../../components/basic/Loading";
import { proxy, useSnapshot } from "valtio";
import PostListModel from "../../entity/viewmodel/post/PostListModel";
import { API } from "../../constants/url";
import { Pagination, Table } from "antd";
import { Link } from "react-router-dom";
import ApiClient from "../../api/ApiClient";
import DateFormatter from "../../util/DateFormatter";
import { USER_INFO_KEY } from "../../constants/LocalStorageKey";
import { AuthContext } from "../../context/AuthProvider";

function PostListView() {
  const { auth, setAuth } = useContext(AuthContext);

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

  const getUserInfo = () => {
    if (!auth) {
      new ApiClient().get(API.AUTH_INFO, null, null).then((response) => {
        const userInfo = JSON.stringify(response);
        localStorage.setItem(USER_INFO_KEY, userInfo);
        setAuth(userInfo);
        alert(`안녕하세요, ${response.username}님!`);
      });
    }
  };

  const getPostList = (params) => {
    new ApiClient().get(API.POST, params, null).then((response) => {
      model.posts = response.content;
      const { content, ...pageData } = response;
      model.page = pageData;
      model.loading = false;
    });
  };

  useEffect(() => {
    getUserInfo();
    void getPostList(params);
  }, [params]);

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
      render: (text) => DateFormatter(text),
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
