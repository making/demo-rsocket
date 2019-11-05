export default class BaseClient {
    constructor({client}) {
        this.client = client;
    }

    connect() {
        return new Promise((resolve, reject) => {
            this.client.connect()
                .subscribe({
                    onComplete: socket => {
                        this.socket = socket;
                        resolve(this.socket);
                    },
                    onError: error => reject(error),
                    onSubscribe: cancel => {
                        this.cancel = cancel
                    }
                });
        });
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