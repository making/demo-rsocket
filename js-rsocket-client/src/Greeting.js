export default class Greeting {
    constructor({id, content}) {
        this.id = id;
        this.content = content;
    }

    toString() {
        return `[${this.id}] ${this.content}`;
    }

    toJsonString() {
        return JSON.stringify(this);
    }
}