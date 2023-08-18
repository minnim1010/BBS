import React, { useRef } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

import { API } from "../../config/url";
import { proxy, useSnapshot } from "valtio";
import MemberJoinModel from "../../entity/viewmodel/member/MemberJoinModel";
import { Button, Form, Input } from "antd";

function MemberJoin() {
  const model = useRef(proxy(new MemberJoinModel())).current;
  const state = useSnapshot(model);

  const navigate = useNavigate();

  const join = async () => {
    if (!state.checkSamePassword()) {
      alert("비밀번호가 서로 다릅니다.");
      return;
    }
    const data = state;
    await axios
      .post(`${API.MEMBER}`, data)
      .then((res) => {
        alert(`${state.name}님, 회원가입이 완료되었습니다.`);
        navigate("/login");
      })
      .catch((err) => {
        console.log("error occured");
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
    // <div>
    //   <div>
    //     <span>아이디</span>
    //     <input type="text" value={state.name} onChange={changeName} />
    //   </div>
    //   <div>
    //     <span>비밀번호</span>
    //     <input
    //       type="password"
    //       value={state.password}
    //       onChange={changePassword}
    //     />
    //   </div>
    //   <div>
    //     <span>비밀번호 확인</span>
    //     <input
    //       type="password"
    //       value={state.checkPassword}
    //       onChange={changeCheckPassword}
    //     />
    //   </div>
    //   <div>
    //     <span>이메일</span>
    //     <input type="text" value={state.email} onChange={changeEmail} />
    //   </div>
    //   <button onClick={join}>회원 가입</button>
    // </div>
  );
}

export default MemberJoin;
