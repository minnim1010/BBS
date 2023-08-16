const BASE_ORIGIN = "http://localhost:8081";
const BASE_API_PATH = "/api/v1"

export const API = {
    MEMBER: `${BASE_ORIGIN}${BASE_API_PATH}/members`,
    LOGIN: `${BASE_ORIGIN}${BASE_API_PATH}/login`,
    LOGOUT: `${BASE_ORIGIN}${BASE_API_PATH}/logout`,
    POST: `${BASE_ORIGIN}${BASE_API_PATH}/posts`,
    COMMENT: `${BASE_ORIGIN}${BASE_API_PATH}/comments`,
}