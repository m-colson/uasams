package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"os/exec"
	"runtime"
	"time"
)

func killIfAlive(cmd *exec.Cmd) {
	if cmd == nil {
		return
	}

	if cmd.ProcessState == nil {
		return
	}

	if cmd.ProcessState.Exited() {
		return
	}

	if err := cmd.Process.Kill(); err != nil {
		_, file, line, _ := runtime.Caller(1)
		fmt.Fprintf(os.Stderr, "Failed to kill: %s:%d: %s\n", file, line, err.Error())
		return
	}

	cmd.Wait()
}

type QuitErrorKind uint8

const (
	QuitErrorKindNormal QuitErrorKind = iota
	QuitErrorKindRestart
	QuitErrorKindQuit
)

func (kind QuitErrorKind) With(err error) QuitError {
	return QuitError{error: err, Kind: kind}
}

type QuitError struct {
	error
	Kind QuitErrorKind
}

func sub(quit chan<- error, name string, logPath string, args ...string) *exec.Cmd {
	logFile, err := os.Create(logPath)
	if err != nil {
		panic(err)
	}

	bufFile := bufio.NewWriter(logFile)

	cmdChan := make(chan *exec.Cmd)

	go func() {
		defer logFile.Close()
		defer bufFile.Flush()

		log.Println("Starting " + name + "...")
		defer log.Println(name + " stopped!")

		cmd := exec.Command(args[0], args[1:]...)

		cmd.Stdout = bufFile
		cmd.Stderr = bufFile

		cmdChan <- cmd
		close(cmdChan)

		err := cmd.Start()

		if err != nil {
			panic(err)
		}

		cmd.Wait()
		quit <- QuitErrorKindNormal.With(err)
	}()

	return <-cmdChan
}

func main() {
	var database *exec.Cmd
	var matchengine *exec.Cmd
	var frontend *exec.Cmd

	defer killIfAlive(frontend)
	defer killIfAlive(matchengine)
	defer killIfAlive(database)

aliveLoop:
	for {
		quit := make(chan error)

		go func() {
			buf := make([]byte, 4096)

			for {
				n, err := os.Stdin.Read(buf)
				if err != nil {
					quit <- err
					return
				}

				switch buf[:n][0] {
				case 'r':
					quit <- QuitErrorKindRestart.With(nil)
					return
				case 'q':
					quit <- QuitErrorKindQuit.With(nil)
					return
				default:
					fmt.Fprintln(os.Stderr, "Unknown command: "+string(buf[:n]))
				}
			}
		}()

		database = sub(
			quit,
			"database",
			"./log/database.log",
			"./database/pocketbase.exe", "serve", "--http", "0.0.0.0:7001",
		)

		matchengine = sub(
			quit,
			"matchengine",
			"./log/matchengine.log",
			"java", "-cp", "./matchengine/lib/json-20230618.jar;./matchengine/bin", "App",
		)

		frontend = sub(
			quit,
			"frontend",
			"./log/frontend.log",
			"cpsnc", "-i", "./frontend", "-outDir", "./out/frontend", "-k", "-v", "-run",
		)

	quitLoop:
		for e := range quit {
			switch qe := e.(QuitError); qe.Kind {
			case QuitErrorKindNormal:
				log.Println("Normal quit")
				// break quitLoop
			case QuitErrorKindRestart:
				log.Println("Restarting...")
				break quitLoop
			case QuitErrorKindQuit:
				log.Println("Quit")
				break aliveLoop
			}
		}

		killIfAlive(frontend)
		killIfAlive(matchengine)
		killIfAlive(database)
		time.Sleep(3 * time.Second)
	}
}
