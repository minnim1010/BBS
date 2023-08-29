import React, { useContext, useRef } from "react";
import { Button, Form, Input } from "antd";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";
import { API } from "../../config/url";
import { proxy, useSnapshot } from "valtio";
import MemberLoginModel from "../../entity/viewmodel/member/MemberLoginModel";

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

  const login = async () => {
    const params = state;
    await axios
      .post(`${API.LOGIN}`, params)
      .then((res) => {
        localStorage.setItem("username", state.name);
        localStorage.setItem("access_token", res.data.accessToken);
        localStorage.setItem("refresh_token", res.data.refreshToken);
        setAuth(state.name);
        setHeaders({ Authorization: `Bearer ${res.data.accessToken}` });

        alert("로그인되었습니다.");
        navigate(-1);
      })
      .catch((err) => {
        console.log("error occured");
        console.log(err);
      });
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
      </Form>
    </div>
  );
}

MemberLogin.propTypes = {};

export default MemberLogin;