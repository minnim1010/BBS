import axios from "axios";
import { BASE_API_PATH, BASE_ORIGIN } from "./url";

class ApiClient {
  static api;

  constructor(hasBaseUrl = true) {
    if (hasBaseUrl) {
      this.api = axios.create({
        baseURL: BASE_API_PATH,
      });
    } else {
      this.api = axios.create({
        baseURL: BASE_ORIGIN,
      });
    }
  }

  async get(endpoint, params, headers) {
    try {
      const response = await this.api.get(endpoint, { params, headers });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async post(endpoint, data, headers) {
    try {
      const response = await this.api.post(endpoint, data, { headers });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async patch(endpoint, data, headers) {
    try {
      const response = await this.api.patch(endpoint, data, { headers });
      return response.data;
    } catch (error) {
      throw error;
    }
  }

  async delete(endpoint, headers) {
    try {
      const response = await this.api.delete(endpoint, { headers });
      return response.data;
    } catch (error) {
      throw error;
    }
  }
}

export default ApiClient;
