import React, { useContext, useState } from 'react'
import axios from "axios";
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthProvider';
import { HttpHeaderTokenContext } from '../../context/HttpHeaderTokenProvider';

function MemberLogin() {

    const { auth, setAuth } = useContext(AuthContext);
    const { headers, setHeaders } = useContext(HttpHeaderTokenContext);

    const [name, setName] = useState("");
    const [password, setPassword] = useState("");

    const changeName = (event) => {
        setName(event.target.value);
    }

    const changePassword = (event) => {
        setPassword(event.target.value);
    }

    const navigate = useNavigate();
    const login = async () => {
        const params = { name, password };
        const url = "http://localhost:8081/api/v1/login";
        await axios.post(url, params)
            .then((res) => {
                localStorage.setItem("username", name);
                localStorage.setItem("access_token", res.data.token);
                setAuth(name);
                setHeaders({ "Authorization": `Bearer ${res.data.token}` });

                alert("로그인되었습니다.");
                navigate(-1);
            })
            .catch((err) => {
                console.log("error occured");
                console.log(err);
            })
    }

    return (
        <div>
            <div>
                <span>아이디</span>
                <input type="text" value={name} onChange={changeName} />
            </div>
            <div>
                <span>비밀번호</span>
                <input type="password" value={password} onChange={changePassword} />
            </div>
            <button onClick={login}>로그인</button>
        </div >
    );
}

MemberLogin.propTypes = {};

export default MemberLogin;