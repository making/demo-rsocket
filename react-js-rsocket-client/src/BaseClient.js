import RSocketWebSocketClient from "rsocket-websocket-client";
import {RSocketClient} from 'rsocket-core';
import {Single} from 'rsocket-flowable';

export default class BaseClient {
    constructor({client}) {
        this.client = client || this.createRSocketClient();
    }

    createRSocketClient() {
        return new RSocketClient({
            setup: {
                // ms btw sending keepalive to server
                keepAlive: 10000,
                // ms timeout if no keepalive response
                lifetime: 20000,
                dataMimeType: 'application/json',
                metadataMimeType: 'message/x.rsocket.routing.v0'
            },
            transport: new RSocketWebSocketClient({url: 'ws://localhost:7000/rsocket'}),
        });
    }

    async connect() {
        this.socket = await this.client.connect()
            .toPromise(cancel => this.cancel = cancel);
    }

    disconnect() {
        if (this.cancel) {
            this.cancel();
        }
    }

    routingMetadata(route) {
        return String.fromCharCode(route.length) + route;
    }
}

Single.prototype.toPromise = function (onSubscribe) {
    return new Promise((resolve, reject) => {
        this.subscribe({
            onComplete: data => resolve(data),
            onError: error => reject(error),
            onSubscribe: cancel => {
                if (onSubscribe) {
                    onSubscribe(cancel);
                }
            }
        });
    });
};