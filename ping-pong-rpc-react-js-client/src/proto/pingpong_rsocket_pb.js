/* eslint-disable */
// GENERATED CODE -- DO NOT EDIT!

'use strict';
var rsocket_rpc_frames = require('rsocket-rpc-frames');
var rsocket_rpc_core = require('rsocket-rpc-core');
var rsocket_rpc_tracing = require('rsocket-rpc-tracing');
var rsocket_rpc_metrics = require('rsocket-rpc-metrics').Metrics;
var rsocket_flowable = require('rsocket-flowable');
var pingpong_pb = require('./pingpong_pb.js');
var google_protobuf_empty_pb = require('google-protobuf/google/protobuf/empty_pb.js');

var PingPongServiceClient = function () {
  function PingPongServiceClient(rs, tracer, meterRegistry) {
    this._rs = rs;
    this._tracer = tracer;
    this.pingTrace = rsocket_rpc_tracing.traceSingle(tracer, "PingPongService", {"rsocket.rpc.service": "com.example.demorsocketrpc.PingPongService"}, {"method": "ping"}, {"rsocket.rpc.role": "client"});
    this.pingMetrics = rsocket_rpc_metrics.timedSingle(meterRegistry, "PingPongService", {"service": "com.example.demorsocketrpc.PingPongService"}, {"method": "ping"}, {"role": "client"});
  }
  // Request / Response
  PingPongServiceClient.prototype.ping = function ping(message, metadata) {
    const map = {};
    return this.pingMetrics(
      this.pingTrace(map)(new rsocket_flowable.Single(subscriber => {
        var dataBuf = Buffer.from(message.serializeBinary());
        var tracingMetadata = rsocket_rpc_tracing.mapToBuffer(map);
        var metadataBuf = rsocket_rpc_frames.encodeMetadata('com.example.demorsocketrpc.PingPongService', 'Ping', tracingMetadata, metadata || Buffer.alloc(0));
          this._rs.requestResponse({
            data: dataBuf,
            metadata: metadataBuf
          }).map(function (payload) {
            //TODO: resolve either 'https://github.com/rsocket/rsocket-js/issues/19' or 'https://github.com/google/protobuf/issues/1319'
            var binary = !payload.data || payload.data.constructor === Buffer || payload.data.constructor === Uint8Array ? payload.data : new Uint8Array(payload.data);
            return pingpong_pb.PongResponse.deserializeBinary(binary);
          }).subscribe(subscriber);
        })
      )
    );
  };
  return PingPongServiceClient;
}();

exports.PingPongServiceClient = PingPongServiceClient;

var PingPongServiceServer = function () {
  function PingPongServiceServer(service, tracer, meterRegistry) {
    this._service = service;
    this._tracer = tracer;
    this.pingTrace = rsocket_rpc_tracing.traceSingleAsChild(tracer, "PingPongService", {"rsocket.rpc.service": "com.example.demorsocketrpc.PingPongService"}, {"method": "ping"}, {"rsocket.rpc.role": "server"});
    this.pingMetrics = rsocket_rpc_metrics.timedSingle(meterRegistry, "PingPongService", {"service": "com.example.demorsocketrpc.PingPongService"}, {"method": "ping"}, {"role": "server"});
    this._channelSwitch = (payload, restOfMessages) => {
      if (payload.metadata == null) {
        return rsocket_flowable.Flowable.error(new Error('metadata is empty'));
      }
      var method = rsocket_rpc_frames.getMethod(payload.metadata);
      var spanContext = rsocket_rpc_tracing.deserializeTraceData(this._tracer, payload.metadata);
      let deserializedMessages;
      switch(method){
        default:
          return rsocket_flowable.Flowable.error(new Error('unknown method'));
      }
    };
  }
  PingPongServiceServer.prototype.fireAndForget = function fireAndForget(payload) {
    throw new Error('fireAndForget() is not implemented');
  };
  PingPongServiceServer.prototype.requestResponse = function requestResponse(payload) {
    try {
      if (payload.metadata == null) {
        return rsocket_flowable.Single.error(new Error('metadata is empty'));
      }
      var method = rsocket_rpc_frames.getMethod(payload.metadata);
      var spanContext = rsocket_rpc_tracing.deserializeTraceData(this._tracer, payload.metadata);
      switch (method) {
        case 'Ping':
          return this.pingMetrics(
            this.pingTrace(spanContext)(new rsocket_flowable.Single(subscriber => {
              var binary = !payload.data || payload.data.constructor === Buffer || payload.data.constructor === Uint8Array ? payload.data : new Uint8Array(payload.data);
              return this._service
                .ping(pingpong_pb.PingRequest.deserializeBinary(binary), payload.metadata)
                .map(function (message) {
                  return {
                    data: Buffer.from(message.serializeBinary()),
                    metadata: Buffer.alloc(0)
                  }
                }).subscribe(subscriber);
              }
            )
          )
        );
        default:
          return rsocket_flowable.Single.error(new Error('unknown method'));
      }
    } catch (error) {
      return rsocket_flowable.Single.error(error);
    }
  };
  PingPongServiceServer.prototype.requestStream = function requestStream(payload) {
    return rsocket_flowable.Flowable.error(new Error('requestStream() is not implemented'));
  };
  PingPongServiceServer.prototype.requestChannel = function requestChannel(payloads) {
    return new rsocket_flowable.Flowable(s => payloads.subscribe(s)).lift(s =>
      new rsocket_rpc_core.SwitchTransformOperator(s, (payload, flowable) => this._channelSwitch(payload, flowable)),
    );
  };
  PingPongServiceServer.prototype.metadataPush = function metadataPush(payload) {
    return rsocket_flowable.Single.error(new Error('metadataPush() is not implemented'));
  };
  return PingPongServiceServer;
}();

exports.PingPongServiceServer = PingPongServiceServer;

