import React, { useState } from 'react'
import axios from "axios";
import { useNavigate } from 'react-router-dom';

function MemberJoin() {
    const [name, setName] = useState("");
    const [password, setPassword] = useState("");
    const [checkPassword, setCheckPassword] = useState("");
    const [email, setEmail] = useState("");

    const navigate = useNavigate();

    const join = async () => {
        const params = { name, password, checkPassword, email };
        const url = "http://localhost:8081/api/v1/join";
        await axios.post(url, params)
            .then((res) => {
                alert(`${name}님, 회원가입이 완료되었습니다.`);
                navigate("/login");
            })
            .catch((err) => {
                console.log("error occured");
            })
    }

    const changeName = (event) => {
        setName(event.target.value);
    }

    const changePassword = (event) => {
        setPassword(event.target.value);
    }

    const changeCheckPassword = (event) => {
        setCheckPassword(event.target.value);
    }

    const changeEmail = (event) => {
        setEmail(event.target.value);
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
            <div>
                <span>비밀번호 확인</span>
                <input type="password" value={checkPassword} onChange={changeCheckPassword} />
            </div>
            <div>
                <span>이메일</span>
                <input type="text" value={email} onChange={changeEmail} />
            </div>
            <button onClick={join}>회원가입</button>
        </div >
    );
}

MemberJoin.propTypes = {};

export default MemberJoin;