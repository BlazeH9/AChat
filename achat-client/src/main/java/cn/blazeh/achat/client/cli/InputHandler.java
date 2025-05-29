package cn.blazeh.achat.client.cli;

import cn.blazeh.achat.client.manager.ConnectionManager;
import cn.blazeh.achat.client.service.ChatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class InputHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(InputHandler.class);

    private final ChatService chat;
    private final InputStream input;

    public InputHandler(ChatService chat, InputStream input) {
        this.chat = chat;
        this.input = input;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            while(true) {
                String line = reader.readLine();
                if(line == null || line.isEmpty())
                    continue;
                if(line.equals("exit")) {
                    ConnectionManager.INSTANCE.disconnect();
                    break;
                }
                String[] args = line.split("\\s+", 2);
                if(args.length <= 1)
                    LOGGER.info("格式错误，用法：<用户id> <消息>");
                else
                    chat.sendMessage(args[0], args[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
