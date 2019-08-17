package actuator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import static actuator.SocketServer.*;


//执行器命令监听
class ReceiveCmd implements Runnable {
    ServerSocket serverSocket = null;
    public static BufferedReader reader;
    public void run() {
        try{
            serverSocket = new ServerSocket(5050); //端口号
            //System.out.println("执行器准备就绪等待指令:");

            //循环等待控制端命令和kafka队列
            while (true) {
                Socket socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));//读取客户端命令消息

                //读取控制端命令
                String cfg = reader.readLine();
                String[] strings = cfg.split(",");
                for (String string : strings) {
                    result.add(string);
                }
                //心跳判断反馈运行状态
                if (result.get(0) == "0") {
                    //获取本机ip地址
                    InetAddress ia = null;
                    ia = ia.getLocalHost();
                    String localip = ia.getHostAddress();
                    writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    writer.write("1" + "," + localip);
                }

                //执行器初始化
                if (result.get(0) == "1") {
                    Threadnum = Integer.parseInt(result.get(1));
                }

                //结束线程,断开kafka,关闭线程池
                if (result.get(0) == "stop") {
                    close="1";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }
}