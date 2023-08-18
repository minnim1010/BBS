const BASE_ORIGIN = "http://localhost:8081";
const BASE_API_PATH = BASE_ORIGIN + "/api/v1";

export const API = {
  MEMBER: `${BASE_API_PATH}/members`,
  LOGIN: `${BASE_API_PATH}/login`,
  LOGOUT: `${BASE_API_PATH}/logout`,
  POST: `${BASE_API_PATH}/posts`,
  COMMENT: `${BASE_API_PATH}/comments`,
};
