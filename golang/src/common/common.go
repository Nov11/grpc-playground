package common

import (
	hellowold "grpc-demo-go/out"
	"log"
	"strconv"
	"time"
)

func MakeRequest(i int) *hellowold.HelloRequest {
	return &hellowold.HelloRequest{
		Name:           "name" + strconv.Itoa(i),
		Id:             int64(i),
		BuildTimeStamp: currentMillionSecond(),
	}
}

func MakeRelay(req *hellowold.HelloRequest) *hellowold.HelloReply {
	enterTS := currentMillionSecond()
	return &hellowold.HelloReply{
		Message:         "Hello " + req.GetName(),
		Id:              req.Id,
		NetworkDelay:    enterTS - req.GetBuildTimeStamp(),
		ProcessDuration: currentMillionSecond() - enterTS,
		ReplayTimeStamp: currentMillionSecond(),
	}
}

func ProcessReply(resp *hellowold.HelloReply) {
	log.Printf("id :%d, network delay(client->server): %d process duration: %d network delay(server->client) %d",
		resp.GetId(),
		resp.GetNetworkDelay(),
		resp.GetProcessDuration(),
		currentMillionSecond()-resp.GetReplayTimeStamp(),
	)
}

func currentMillionSecond() int64 {
	return time.Now().UnixNano() / 1e6
}
