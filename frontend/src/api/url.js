export const BASE_ORIGIN = "http://localhost:8081";
export const BASE_API_PATH = BASE_ORIGIN + "/api/v1";

export const API = {
  LOGIN: `/auth/login`,
  GOOGLE_OAUTH_LOGIN: `/oauth2/authorization/google`,
  LOGOUT: `/auth/logout`,
  AUTH_INFO: `/members//auth-info`,

  MEMBER: `/members`,
  POST: `/posts`,
  COMMENT: `/comments`,
};
