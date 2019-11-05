export default class Name {
    constructor({firstName, lastName}) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    toString() {
        return `${this.lastName} ${this.firstName}`;
    }

    toJsonString() {
        return JSON.stringify(this);
    }
}