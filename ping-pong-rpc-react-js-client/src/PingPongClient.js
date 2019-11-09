import {RequestHandlingRSocket, RpcClient} from "rsocket-rpc-core";
import RSocketWebSocketClient from "rsocket-websocket-client";
import {BufferEncoders} from "rsocket-core";
import {PingPongServiceClient} from "./proto/pingpong_rsocket_pb";

export default class PingPongClient {
    constructor() {
        const responder = new RequestHandlingRSocket();
        const keepAlive = 60000 /* 60s in ms */;
        const lifetime = 360000 /* 360s in ms */;
        const transport = new RSocketWebSocketClient({url: 'ws://localhost:9999'}, BufferEncoders);
        this.client = new RpcClient({setup: {keepAlive, lifetime}, transport, responder});
    }

    connect() {
        return new Promise((resolve, reject) => {
            this.client.connect()
                .subscribe({
                    onComplete: socket => {
                        this.serviceClient = new PingPongServiceClient(socket);
                        resolve(this.serviceClient);
                    },
                    onError: error => reject(error),
                    onSubscribe: cancel => {
                        this.cancel = cancel
                    }
                });
        });
    }

    ping(request) {
        return new Promise((resolve, reject) => {
            this.serviceClient.ping(request).subscribe({
                onComplete: data => resolve(data),
                onError: e => reject(e),
            });
        });
    }
}