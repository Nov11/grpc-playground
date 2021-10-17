package main

import (
	"context"
	"google.golang.org/grpc"
	hellowold "grpc-demo-go/out"
	"grpc-demo-go/src"
	"grpc-demo-go/src/common"
	"log"
	"net"
	"strconv"
)

var _ hellowold.GreeterServer = (*impl)(nil)

type impl struct {
	hellowold.UnimplementedGreeterServer
}

func (i impl) SayHello(ctx context.Context, request *hellowold.HelloRequest) (*hellowold.HelloReply, error) {
	return common.MakeRelay(request), nil
}

func main() {
	lis, err := net.Listen("tcp", "localhost:"+strconv.Itoa(src.Port))
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	var opts []grpc.ServerOption
	grpcServer := grpc.NewServer(opts...)

	handler := &impl{}
	hellowold.RegisterGreeterServer(grpcServer, handler)

	log.Println("registered")
	err = grpcServer.Serve(lis)
	if err != nil {
		log.Fatal(err)
	}
	log.Println("started")
}
