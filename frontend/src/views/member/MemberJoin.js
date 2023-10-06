import React, { useRef } from "react";
import { useNavigate } from "react-router-dom";

import { proxy, useSnapshot } from "valtio";
import MemberJoinModel from "../../entity/viewmodel/member/MemberJoinModel";
import { Button, Form, Input } from "antd";
import ApiClient from "../../api/ApiClient";
import { API } from "../../constants/url";

function MemberJoin() {
  const model = useRef(proxy(new MemberJoinModel())).current;
  const state = useSnapshot(model);

  const navigate = useNavigate();

  const join = () => {
    if (!state.checkSamePassword()) {
      alert("비밀번호가 서로 다릅니다.");
      return;
    }

    new ApiClient().post(API.MEMBER, state, null).then((response) => {
      alert(`${response.name}님, 회원가입이 완료되었습니다.`);
      navigate("/login");
    });
  };

  const changeName = (event) => {
    model.name = event.target.value;
  };

  const changePassword = (event) => {
    model.password = event.target.value;
  };

  const changeCheckPassword = (event) => {
    model.checkPassword = event.target.value;
  };

  const changeEmail = (event) => {
    model.email = event.target.value;
  };

  const validateMessages = {
    required: "${label}은 필수 항목입니다.",
    types: {
      email: "유효한 이메일 주소가 아닙니다.",
    },
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
        validateMessages={validateMessages}
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

        <Form.Item
          label="check password"
          name="checkPassword"
          rules={[
            {
              required: true,
              message: "비밀번호를 다시 입력해주세요.",
            },
          ]}
          value={state.checkPassword}
          onChange={changeCheckPassword}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          name={["email"]}
          label="Email"
          rules={[
            {
              required: true,
              type: "email",
            },
          ]}
        >
          <Input value={state.email} onChange={changeEmail} />
        </Form.Item>

        <Button onClick={join}>회원가입</Button>
      </Form>
    </div>
  );
}

export default MemberJoin;
