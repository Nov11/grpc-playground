#!/bin/bash

export PATH=$PATH:$(go env GOPATH)/bin
protoc --proto_path=../src/main/proto/ \
--go_out=out --go_opt=paths=source_relative \
--go-grpc_out=out --go-grpc_opt=paths=source_relative \
helloworld.proto