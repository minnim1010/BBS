export default class MemberJoinModel {
    name = "";
    password = "";
    checkPassword = "";
    email = "";

    checkSamePassword() {
        return this.password === this.checkPassword;
    }
}