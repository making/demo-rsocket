import React from 'react';

import 'pivotal-ui/css/alignment';
import 'pivotal-ui/css/positioning';
import 'pivotal-ui/css/selection';
import 'pivotal-ui/css/typography';
import 'pivotal-ui/css/vertical-alignment';
import 'pivotal-ui/css/whitespace';

import './App.css';
import GreetingView from "./greeting/GreetingView";
import NameView from "./name/NameView";

export default function App() {
    return (
        <div>
            <GreetingView/>
            <br/>
            <NameView/>
        </div>);
}