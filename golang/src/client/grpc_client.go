package main

import (
	"bufio"
	"context"
	"fmt"
	"google.golang.org/grpc"
	hellowold "grpc-demo-go/out"
	"grpc-demo-go/src/common"
	"log"
	"os"
	"strconv"
	"strings"
	"sync"
)

func syncCall(client hellowold.GreeterClient, count int) {
	ctx := context.Background()
	for i := 0; i < count; i++ {
		reply, err := client.SayHello(ctx, common.MakeRequest(i+1))
		if err != nil {
			log.Fatal(err)
		}
		common.ProcessReply(reply)
	}
}

func asyncCall(client hellowold.GreeterClient, count int) {
	var wg sync.WaitGroup

	for i := 0; i < count; i++ {
		idx := i + 1
		wg.Add(1)
		go func() {
			defer wg.Done()
			reply, err := client.SayHello(context.Background(), common.MakeRequest(idx))
			if err != nil {
				log.Fatal(err)
			}
			common.ProcessReply(reply)
		}()
	}

	wg.Wait()
}

func main() {
	var opts []grpc.DialOption
	opts = append(opts, grpc.WithInsecure(), grpc.WithBlock())
	conn, err := grpc.Dial("localhost:"+strconv.Itoa(9999), opts...)
	//conn, err := grpc.Dial("localhost:"+strconv.Itoa(src.Port), opts...)
	if err != nil {
		log.Fatal(err)
	}
	defer conn.Close()

	client := hellowold.NewGreeterClient(conn)

	reader := bufio.NewReader(os.Stdin)
	fmt.Println("press any key")

	for {
		fmt.Print("-> ")
		text, _ := reader.ReadString('\n')
		// convert CRLF to LF
		text = strings.Replace(text, "\n", "", -1)

		asyncCall(client, 40)
	}
}
