import React, { useContext, useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import { AuthContext } from "../../context/AuthProvider";

import Search from "antd/es/input/Search";
import { Button, Select } from "antd";
import { API } from "../../api/url";
import ApiClient from "../../api/ApiClient";

function Nav() {
  const { auth, setAuth } = useContext(AuthContext);

  const [searchScope, setSearchScope] = useState("전체");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();

  const searchScopeValue = [
    { value: "전체", label: "전체" },
    { value: "제목", label: "제목" },
    { value: "작성자", label: "작성자" },
  ];

  const logout = () => {
    new ApiClient().post(API.LOGOUT, null, null).then(() => {
      localStorage.removeItem("username");
      setAuth(null);

      alert("로그아웃되었습니다.");
      navigate("/");
    });
  };

  const onSearch = async () => {
    navigate(
      `/?page=1&category=string&searchScope=${searchScope}&searchKeyword=${searchKeyword}`,
    );
    window.location.reload();
  };

  const changeSearchScope = (value) => {
    setSearchScope(value);
  };

  const changeSearchKeyword = (event) => {
    setSearchKeyword(event.target.value);
  };

  return (
    <div className="nav">
      <div className="logo">
        <Link to="/">
          <h2>BBS</h2>
        </Link>
      </div>
      <div className="flex-vertical-center">
        <div className="searchPost">
          <Select
            defaultValue="전체"
            style={{ width: 120 }}
            onChange={changeSearchScope}
            options={searchScopeValue}
          />
          <Search
            placeholder="게시글 검색"
            onChange={changeSearchKeyword}
            onSearch={onSearch}
            style={{ width: 200 }}
          />
        </div>
        <div className="auth">
          {auth ? (
            <div className="auth">
              <div className="writeBtn">
                <Link to="/write">
                  <Button type="primary">글쓰기</Button>
                </Link>
              </div>
              <div className="logoutBtn">
                <Button onClick={() => logout}>로그아웃</Button>
              </div>
            </div>
          ) : (
            <div className="noAuth">
              <div className="joinBtn">
                <Link to="/join">
                  <Button>회원가입</Button>
                </Link>
              </div>
              <div className="loginBtn">
                <Link to="/login">
                  <Button>로그인</Button>
                </Link>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Nav;
