package blockchain.api.common;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */


@Slf4j
public abstract class StubSocketServer extends Thread {

    private ServerSocket serverSocket;
    private CountDownLatch startLatch = new CountDownLatch(1);

    protected abstract Consumer<Socket> getSocketHandler();

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
            startLatch.countDown();

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = serverSocket.accept();

                Consumer<Socket> socketHandler = getSocketHandler();
                socketHandler.accept(socket);

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
