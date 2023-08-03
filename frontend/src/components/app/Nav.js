import { useContext } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";

import { AuthContext } from "../../context/AuthProvider";
import { HttpHeaderTokenContext } from "../../context/HttpHeaderTokenProvider";

function Nav() {
    const { auth, setAuth } = useContext(AuthContext);
    const { headers, setHeaders } = useContext(HttpHeaderTokenContext);

    const navigate = useNavigate();
    const logout = async () => {
        const url = "http://localhost:8081/api/v1/logout";

        console.log(headers);
        await axios.get(url, { headers })
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
                console.log(err);
            })
    }

    return (
        <div>
            <br />
            {
                (auth) ?
                    <button onClick={logout}>로그아웃</button>
                    :
                    <>
                        <Link to="/join" >
                            <button>회원가입</button>
                        </Link>
                        <Link to="/login" >
                            <button>로그인</button>
                        </Link>
                    </>
            }

            <Link to="/posts" >
                <button>글 목록</button>
            </Link>
            <br />

        </div>
    );
}

export default Nav;