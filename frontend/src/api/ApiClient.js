import axios from 'axios';
import {BASE_API_PATH} from "./url";

class ApiClient {

    api;

    constructor() {
        this.api = axios.create({
            baseURL: BASE_API_PATH
        });
    }

    async get(endpoint, params, headers) {
        try {
            const response = await this.api
                .get(endpoint, { params, headers });
            return response.data;
        } catch (error) {
            throw error;
        }
    }

    async post(endpoint, data, headers) {
        try {
            const response = await this.api
                .post(endpoint, data, {headers});
            return response.data;
        } catch (error) {
            throw error;
        }
    }

    async patch(endpoint, data, headers) {
        try {
            const response = await this.api
                .patch(endpoint, data, {headers});
            return response.data;
        } catch (error) {
            throw error;
        }
    }

    async delete(endpoint, headers) {
        try {
            const response = await this.api
                .delete(endpoint, {headers});
            return response.data;
        } catch (error) {
            throw error;
        }
    }
}

export default ApiClient;