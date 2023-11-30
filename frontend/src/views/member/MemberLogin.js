import React, {useContext, useRef} from "react";
import {Button, Form, Input} from "antd";
import {useNavigate} from "react-router-dom";

import {AuthContext} from "../../context/AuthProvider";
import {proxy, useSnapshot} from "valtio";
import MemberLoginModel from "../../entity/viewmodel/member/MemberLoginModel";
import ApiClient from "../../api/ApiClient";
import {API, BASE_ORIGIN} from "../../constants/url";
import {USER_INFO_KEY} from "../../constants/LocalStorageKey";

function MemberLogin() {
    const {setAuth} = useContext(AuthContext);

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
        new ApiClient().post(API.LOGIN, state, null).then(() => {
            getUserInfo();
        });
    };

    const getUserInfo = () => {
        new ApiClient().get(API.AUTH_INFO, null, null).then((response) => {
            const userInfo = JSON.stringify(response);
            sessionStorage.setItem(USER_INFO_KEY, userInfo);
            setAuth(userInfo);
            alert(`안녕하세요, ${response.username}님!`);
            navigate(-1);
        });
    };

    return (<div>
        <Form
            name="basic"
            labelCol={{
                span: 8,
            }}
            wrapperCol={{
                span: 16,
            }}
            initialValues={{
                remember: true,
            }}
            autoComplete="off"
        >
            <Form.Item
                name="username"
                rules={[{
                    required: true, message: "아이디를 입력해주세요.",
                },]}
                value={state.name}
                onChange={changeName}
            >
                <Input placeholder="아이디를 입력해주세요."/>
            </Form.Item>

            <Form.Item
                name="password"
                rules={[{
                    required: true, message: "비밀번호를 입력해주세요.",
                },]}
                value={state.password}
                onChange={changePassword}
            >
                <Input.Password placeholder="비밀번호를 입력해주세요."/>
            </Form.Item>

            <Form.Item>
                <Button block onClick={login}>로그인</Button>
            </Form.Item>

            <div className="font-bold">
                <div className="social-media-text">소셜 미디어로 로그인하기</div>
                <a href={BASE_ORIGIN + API.GOOGLE_OAUTH_LOGIN}>
                    <img src="/images/google_login.png" alt="google_login"/>
                </a>
            </div>
        </Form>
    </div>);
}

MemberLogin.propTypes = {};

export default MemberLogin;
