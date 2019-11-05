import BaseClient from "../BaseClient";
import Greeting from "./Greeting";

export default class GreetingClient extends BaseClient {
    greet(name) {
        return new Promise((resolve, reject) => {
            if (!this.socket) {
                reject(new Error('RSocket is not connected!'));
                return;
            }
            this.socket.requestResponse({
                data: JSON.stringify({}),
                metadata: this.routingMetadata(`greeting.${name}`)
            }).subscribe({
                onComplete: payload => resolve(new Greeting(JSON.parse(payload.data))),
                onError: (e) => reject(e)
            });
        });
    }
}