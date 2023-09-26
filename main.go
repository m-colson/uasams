package main

import (
	"bufio"
	"log"
	"os"
	"os/exec"
)

func killIfAlive(cmd *exec.Cmd) {
	if cmd.Process != nil {
		cmd.Process.Kill()
		cmd.Process.Wait()
	}
}

func sub(quit chan error, name string, logPath string, args ...string) *exec.Cmd {
	logFile, err := os.Create(logPath)
	if err != nil {
		panic(err)
	}

	bufFile := bufio.NewWriter(logFile)
	defer bufFile.Flush()

	cmdChan := make(chan *exec.Cmd)

	go func() {
		log.Println("Starting " + name + "...")
		defer log.Println(name + " stopped!")

		cmd := exec.Command(args[0], args[1:]...)

		cmd.Stdout = bufFile
		cmd.Stderr = bufFile

		cmdChan <- cmd

		err := cmd.Run()
		if err != nil {
			quit <- err
		}

		close(quit)
	}()

	return <-cmdChan
}

func main() {
	quit := make(chan error)

	database := sub(
		quit,
		"database",
		"./log/database.log",
		"./database/pocketbase.exe", "serve", "--http", "0.0.0.0:7001",
	)
	defer killIfAlive(database)

	matchengine := sub(
		quit,
		"matchengine",
		"./log/matchengine.log",
		"java", "-cp", "./matchengine/lib/json-20230618.jar;./matchengine/bin", "App",
	)
	defer killIfAlive(matchengine)

	frontend := sub(
		quit,
		"frontend",
		"./log/frontend.log",
		"pnpm", "-C", "./uasams-frontend", "dev",
	)
	defer killIfAlive(frontend)

	for e := range quit {
		log.Println(e)
	}
}
