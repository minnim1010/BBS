import React, { useContext, useState } from "react";
import axios from "axios";
import { Link, useLocation, useNavigate } from "react-router-dom";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";

import Search from "antd/es/input/Search";
import { Button, Select } from "antd";
import { API } from "../../config/url";

function Nav() {
  const { auth, setAuth } = useContext(AuthContext);
  const { headers, setHeaders } = useContext(HttpHeaderTokenContext);

  const [searchScope, setSearchScope] = useState("전체");
  const [searchKeyword, setSearchKeyword] = useState("");

  const navigate = useNavigate();
  const location = useLocation();
  let currentPath = "";

  const searchScopeValue = [
    { value: "전체", label: "전체" },
    { value: "제목", label: "제목" },
    { value: "작성자", label: "작성자" },
  ];

  const logout = async (headers) => {
    console.log(headers);
    await axios
      .post(`${API.LOGOUT}`, null, { headers })
      .then((res) => {
        localStorage.removeItem("username");
        localStorage.removeItem("access_token");
        setAuth(null);
        setHeaders(null);

        alert("로그아웃되었습니다.");
        navigate("/");
      })
      .catch((err) => {
        console.log("error occured");
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
                <Button onClick={() => logout(headers)}>로그아웃</Button>
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
