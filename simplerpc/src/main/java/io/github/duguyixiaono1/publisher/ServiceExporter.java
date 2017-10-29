package io.github.duguyixiaono1.publisher;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by jliu1 on 2017/10/29.
 */
public class ServiceExporter {

    private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 发布到某个url
    public static void export(String host, int port) throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(host, port));

        try {
            while (true) {
                executor.execute(new ExporterTask(server.accept()));
            }
        } finally {
            server.close();
        }
    }

    private static class ExporterTask implements Runnable {
        private Socket socket;

        public ExporterTask(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {
                inputStream = new ObjectInputStream(socket.getInputStream());
                String servieName = inputStream.readUTF();
                Class<?> service = Class.forName(servieName);
                String methodName = inputStream.readUTF();
                Class<?>[] parameterTypes = (Class<?>[]) inputStream.readObject();
                Object[] arguments = (Object[]) inputStream.readObject();
                Method method = service.getMethod(methodName, parameterTypes);
                Object result = method.invoke(service.newInstance(), arguments);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(result);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (outputStream!=null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
