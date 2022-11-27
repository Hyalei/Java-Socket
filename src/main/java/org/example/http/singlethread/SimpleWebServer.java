package org.example.http.singlethread;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*快速请求http://127.0.0.1:8080/image.jpg接口将报错
 * Exception in thread "main" java.net.SocketException: Software caused connection abort: socket write error
 * 具体原因未知
 *
 */
public class SimpleWebServer {

    public static void main(String[] args) throws IOException {
        SimpleWebServer.start();
    }

    public static void start() throws IOException {

        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("服务器已启动");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("接受到请求");

            try (InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream()
            ) {

                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                /*获取请求头第一行的请求方法，路径和协议名称*/
                String[] s = br.readLine().split(" ");
                if ("/image.jpg".equals(s[1])) {
                    FileInputStream fileInputStream = new FileInputStream("src/main/resources/demo1.jpg");
                    int responseLength = fileInputStream.available();

                    byte[] buf = new byte[responseLength];
                    fileInputStream.read(buf);

                    outputStream.write("HTTP/1.0 200 OK".getBytes());
                    outputStream.write("\r\n".getBytes());
                    outputStream.write("Content-Type:image/jpg".getBytes());
                    outputStream.write("\r\n".getBytes());
                    outputStream.write("Content-Length:".getBytes());
                    outputStream.write(String.valueOf(responseLength).getBytes());
                    outputStream.write("\r\n".getBytes());
                    outputStream.write("\r\n".getBytes());
                    outputStream.write(buf);

                    fileInputStream.close();
                } else {
                    System.out.println("404");
                    outputStream.write("HTTP/1.0 404 Not found".getBytes());
                    outputStream.write("\r\n".getBytes());
                    outputStream.write("\r\n".getBytes());
                }

                socket.close();
            }

        }
    }

}
