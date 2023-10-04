import React, { useContext, useRef } from "react";
import { Button, Form, Input } from "antd";
import { useNavigate } from "react-router-dom";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { proxy, useSnapshot } from "valtio";
import MemberLoginModel from "../../entity/viewmodel/member/MemberLoginModel";
import ApiClient from "../../api/ApiClient";
import { API } from "../../api/url";

function MemberLogin() {
  const { setAuth } = useContext(AuthContext);
  const { setHeaders } = useContext(HttpHeaderTokenContext);

  const model = useRef(proxy(new MemberLoginModel())).current;
  const state = useSnapshot(model);

  const changeName = (event) => {
    model.name = event.target.value;
  };

  const changePassword = (event) => {
    model.password = event.target.value;
  };

  const navigate = useNavigate();

  const login = () => {
    new ApiClient().post("/login", state, null).then((response) => {
      sessionStorage.setItem("username", state.name);
      sessionStorage.setItem("access_token", response.accessToken);
      sessionStorage.setItem("refresh_token", response.refreshToken);

      setAuth(state.name);
      setHeaders({ Authorization: `Bearer ${response.accessToken}` });

      alert(`안녕하세요, ${state.name}님!`);
      navigate(-1);
    });
  };

  const oauthLogin = () => {
    new ApiClient(false).get(API.OAUTH_LOGIN, [], null);
  };

  return (
    <div>
      <Form
        name="basic"
        labelCol={{
          span: 8,
        }}
        wrapperCol={{
          span: 16,
        }}
        style={{
          maxWidth: 600,
        }}
        initialValues={{
          remember: true,
        }}
        autoComplete="off"
      >
        <Form.Item
          label="Username"
          name="username"
          rules={[
            {
              required: true,
              message: "이름을 입력해주세요.",
            },
          ]}
          value={state.name}
          onChange={changeName}
        >
          <Input />
        </Form.Item>

        <Form.Item
          label="Password"
          name="password"
          rules={[
            {
              required: true,
              message: "비밀번호를 입력해주세요.",
            },
          ]}
          value={state.password}
          onChange={changePassword}
        >
          <Input.Password />
        </Form.Item>

        <Button onClick={login}>로그인</Button>
        <Button onClick={oauthLogin}>구글 로그인</Button>
      </Form>
    </div>
  );
}

MemberLogin.propTypes = {};

export default MemberLogin;
