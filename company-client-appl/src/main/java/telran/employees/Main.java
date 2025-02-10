package telran.employees;

import telran.view.*;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import telran.net.*;

public class Main {
    private static final String HOST = "localhost";
    private static final int PORT = 4000;
    private static final String CLIENT_TYPE = "TcpClient";
    private static final String PACKAGE = "telran.net.";

    public static void main(String[] args) {
        InputOutput io = new StandardInputOutput();
        String host = args.length > 0 ? args[0] : HOST;
        int port = args.length > 1 ? Integer.parseInt(args[1]) : PORT;
        String clientType = args.length > 2 ? args[2] : CLIENT_TYPE;
        NetworkClient netClient = null;
        try {
            netClient = getNetClient(clientType, host, port);
        } catch (Exception e) {
            io.writeLine(e.getMessage());
        }
        Company company = new CompanyNetProxy(netClient);
        Item[] items = CompanyItems.getItems(company);
        items = addExitItem(items, netClient);
        Menu menu = new Menu("Company Network Application", items);
        menu.perform(io);
        io.writeLine("Application is finished");
    }

    @SuppressWarnings("unchecked")
    private static NetworkClient getNetClient(String clientType, String host, int port) throws Exception {

        Class<NetworkClient> clazz = (Class<NetworkClient>) Class.forName(PACKAGE + clientType);
        Constructor<NetworkClient> constructor = clazz.getConstructor(String.class, int.class);
        return constructor.newInstance(host, port);
    }

    private static Item[] addExitItem(Item[] items, NetworkClient netClient) {
        Item[] res = Arrays.copyOf(items, items.length + 1);
        res[items.length] = Item.of("Exit", io -> {
            try {
                if (netClient instanceof Closeable closeable)
                    closeable.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, true);
        return res;
    }
}