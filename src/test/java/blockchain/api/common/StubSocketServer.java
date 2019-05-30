package blockchain.api.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO : re
 */
@Slf4j
public class StubSocketServer extends Thread {

    private Optional<Function<String, String>> inputHandlerOptional;
    private ServerSocket serverSocket;
    private CountDownLatch startLatch = new CountDownLatch(1);

    public StubSocketServer(Optional<Function<String, String>> inputHandlerOptional) {
        this.inputHandlerOptional = inputHandlerOptional;
    }

    @Override
    public void start() {
        if (startLatch.getCount() == 0) {
            startLatch = new CountDownLatch(1);
        }

        super.start();
        try {
            startLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            StubSocketServer.this.startLatch.countDown();

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                final String read = input.readLine();

                if (inputHandlerOptional.isPresent()) {
                    try {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        String output = inputHandlerOptional.get().apply(read);
                        out.println(output);
                        out.flush();
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.warn("Exception occur while writing output", e);
                    }
                }

                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {

                }
            }
        }
    }

    public int getPort() {
        if (serverSocket == null) {
            return -1;
        }

        return serverSocket.getLocalPort();
    }
}
