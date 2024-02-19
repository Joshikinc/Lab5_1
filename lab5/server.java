import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class server {
    private List<ClientHandler> clients;

    public static void main(String[] args) {
        server server = new server();
        server.start(5050);
    }

    public void start(int port) {
        clients = new ArrayList<>();

        try {
            // Создание серверного сокета
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Сервер запущен на порту " + port);

            while (true) {
                // Ожидание подключения клиента
                Socket clientSocket = serverSocket.accept();
                System.out.println("Новый клиент подключился");

                // Создание и запуск обработчика клиента
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Отправка сообщения всем клиентам, кроме отправителя
    public synchronized void broadcastMessage(ClientHandler sender, String message) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Удаление клиента из списка
    public synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
        System.out.println("Клиент отключился");
    }

    // Обработчик клиента
    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader reader;
        private PrintWriter writer;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                // Получение потоков ввода/вывода для обмена данными с клиентом
                InputStream inputStream = clientSocket.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                OutputStream outputStream = clientSocket.getOutputStream();
                writer = new PrintWriter(outputStream, true);

                String message;
                while ((message = reader.readLine()) != null) {
                    // Отправка сообщения всем клиентам
                    broadcastMessage(this, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Закрытие соединения и удалениеклиента из списка при отключении
                try {
                    reader.close();
                    writer.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                removeClient(this);
            }
        }

        // Отправка сообщения клиенту
        public void sendMessage(String message) {
            writer.println(message);
        }
    }
}