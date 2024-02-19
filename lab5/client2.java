import java.io.*;
import java.net.Socket;

public class client2 {
    public static void main(String[] args) {
        try {
            // Установка соединения с сервером
            Socket socket = new Socket("localhost", 5050);

            // Получение потоков ввода/вывода для обмена данными с сервером
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            // Получение потока ввода для чтения ответа от сервера
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Чтение ответов от сервера в отдельном потоке
            new Thread(new ServerResponseReader(reader)).start();

            // Чтение пользовательского ввода и отправка сообщений на сервер
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
            String userInput;
            while ((userInput = userInputReader.readLine()) != null) {
                writer.println(userInput);
            }

            // Закрытие соединения
            writer.close();
            outputStream.close();
            reader.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// Класс для чтения ответов от сервера в отдельном потоке
class ServerResponseReader2 implements Runnable {
    private BufferedReader reader;

    public ServerResponseReader2(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        try {
            String response;
            while ((response = reader.readLine()) != null) {
                System.out.println("Получено сообщение от сервера: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}