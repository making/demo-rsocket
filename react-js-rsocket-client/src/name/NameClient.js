import BaseClient from "../BaseClient";
import Name from './Name';

export default class NameClient extends BaseClient {
    constructor({client, maxInFlight}) {
        super({client});
        this.maxInFlight = maxInFlight || 20;
    }

    names({doOnSubscribe, doOnNext, doOnError, doOnLastOfBatch}) {
        let current = this.maxInFlight;
        let subscription;
        if (!this.socket) {
            throw new Error('RSocket is not connected!');
        }
        this.socket.requestStream({
            data: JSON.stringify({}),
            metadata: this.routingMetadata('name')
        }).subscribe({
            onSubscribe: sub => {
                subscription = sub;
                console.log('request', this.maxInFlight);
                subscription.request(this.maxInFlight);
                doOnSubscribe(subscription);
            },
            onNext: (payload) => {
                let body = JSON.parse(payload.data);
                doOnNext(new Name(body));
                current--;
                if (current === 0) {
                    current = doOnLastOfBatch(this.maxInFlight);
                }
            },
            onError: (e) => doOnError(e)
        });
    }
}