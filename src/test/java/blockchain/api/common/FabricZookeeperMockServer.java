package blockchain.api.common;

import java.io.InputStream;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * Fabric zookeeper mock server
 *
 * default :  if request with "srvr" command, then write commandResult (default : "Mode: standalone")
 * optional : u can change Consumer<Socket> socketHandler if needed
 */
public class FabricZookeeperMockServer extends StubSocketServer {

    private String commandResult;
    private Consumer<Socket> socketHandler;

    public FabricZookeeperMockServer(String commandResult) {
        this(commandResult, null);
    }

    public FabricZookeeperMockServer(String commandResult, Consumer<Socket> socketHandler) {
        if (socketHandler == null && commandResult == null) {
            commandResult = "Mode: standalone";
        }

        this.commandResult = commandResult;
        this.socketHandler = socketHandler;
    }

    @Override
    public Consumer<Socket> getSocketHandler() {
        if (socketHandler != null) {
            return socketHandler;
        }

        // default fabric zookeeper mock server
        socketHandler = socket -> {
            try {
                InputStream is = socket.getInputStream();

                String command = "srvr";

                for (int i = 0; i < 4; i++) {
                    int read = is.read();
                    if (command.charAt(i) != (char) read) {
                        throw new Exception("Invalid command char at " + i + " = " + ((char) read));
                    }
                }

                if (commandResult != null) {
                    socket.getOutputStream().write(commandResult.getBytes());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        return socketHandler;
    }
}
