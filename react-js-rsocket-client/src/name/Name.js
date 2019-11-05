export default class Name {
    constructor({firstName, lastName}) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = generateUuid();
    }

    toString() {
        return `${this.lastName} ${this.firstName}`;
    }

    getId() {
        return this.id;
    }

    toJsonString() {
        return JSON.stringify(this);
    }
}

const UUID_FORMAT = "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx";

/**
 * https://qiita.com/psn/items/d7ac5bdb5b5633bae165
 */
function generateUuid() {
    // https://github.com/GoogleChrome/chrome-platform-analytics/blob/master/src/internal/identifier.js
    let chars = UUID_FORMAT.split("");
    for (let i = 0, len = chars.length; i < len; i++) {
        switch (chars[i]) {
            case "x":
                chars[i] = Math.floor(Math.random() * 16).toString(16);
                break;
            case "y":
                chars[i] = (Math.floor(Math.random() * 4) + 8).toString(16);
                break;
            default:
        }
    }
    return chars.join("");
}