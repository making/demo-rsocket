import BaseClient from "../BaseClient";
import Greeting from "./Greeting";

export default class GreetingClient extends BaseClient {
    async greet(name) {
        if (!this.socket) {
            throw new Error('RSocket is not connected!');
        }
        return await this.socket
            .requestResponse({
                data: JSON.stringify({}),
                metadata: this.routingMetadata(`greeting.${name}`)
            })
            .toPromise()
            .then(payload => new Greeting(JSON.parse(payload.data)));
    }
}