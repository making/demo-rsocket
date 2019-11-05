import React, {Component} from 'react';
import {DefaultButton} from 'pivotal-ui/react/buttons';
import {SuccessAlert} from 'pivotal-ui/react/alerts';
import {DraggableList, DraggableListItem} from 'pivotal-ui/react/draggable-list';

import NameClient from "./NameClient";

export default class NameView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            items: [],
            cancelable: false
        };
        this.nameClient = new NameClient({});
    }

    render() {
        return <div>
            <SuccessAlert
                withIcon
                dismissable
                onDismiss={() => this.setState({greeting: null})}
                show={this.state.greeting != null}>{this.state.greeting && this.state.greeting.toString()}</SuccessAlert>
            <br/>
            <DefaultButton onClick={() => this.state.cancelable ? this.cancel() : this.names()}>
                {this.state.cancelable ? `Cancel` : `Generate names`}
            </DefaultButton>
            <br/>
            <br/>
            <DraggableList>
                {this.state.items.map(i => <DraggableListItem key={i.toString()}>{i.toString()}</DraggableListItem>)}
            </DraggableList>
        </div>
    }

    componentDidMount() {
        this.nameClient
            .connect()
            .catch(e => console.error('Failed to connect', e));
    }

    componentWillUnmount() {
        this.nameClient.disconnect();
    }

    cancel() {
        console.log('Cancelling...');
        this.subscription.cancel();
        this.setState({cancelable: false});
    }

    names() {
        this.nameClient.names({
            doOnSubscribe: subscription => {
                this.subscription = subscription;
                this.setState({cancelable: true});
            },
            doOnNext: name => {
                let items = this.state.items;
                items.unshift(name);
                while (items.length > 20) {
                    items.pop();
                }
                this.setState({
                    items: items
                });
            },
            doOnError: e => console.error('Failed to retrieve names', e),
            doOnLastOfBatch: (maxInFlight) => {
                setTimeout(() => {
                    console.log('request', maxInFlight);
                    this.subscription.request(maxInFlight);
                }, 100);
                return maxInFlight;
            }
        });
    }
}