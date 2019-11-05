import React, {Component} from 'react';
import {DefaultButton} from 'pivotal-ui/react/buttons';
import {DraggableList, DraggableListItem} from 'pivotal-ui/react/draggable-list';
import {Input} from 'pivotal-ui/react/inputs';

import NameClient from "./NameClient";

export default class NameView extends Component {
    constructor(props) {
        super(props);
        this.state = {
            items: [],
            cancelable: false,
            maxInFlight: 8
        };
        this.nameClient = new NameClient({maxInFlight: this.state.maxInFlight});
    }

    render() {
        return <div>
            Request: <Input type="number"
                            pattern="\d*"
                            style={{width: "4em"}}
                            value={this.state.maxInFlight}
                            onChange={(e) => this.setState({maxInFlight: Math.max(1, Math.min(Number(e.target.value), 256))})}/>
            <br/>
            <DefaultButton onClick={() => this.state.cancelable ? this.cancel() : this.names()}>
                {this.state.cancelable ? `Cancel` : `Generate names`}
            </DefaultButton>
            <br/>
            <br/>
            <DraggableList>
                {this.state.items.map(name => <DraggableListItem key={name.getId()}>{name.toString()}</DraggableListItem>)}
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
            doOnLastOfBatch: () => {
                this.subscription.request(this.state.maxInFlight);
                return this.state.maxInFlight;
            }
        });
    }
}