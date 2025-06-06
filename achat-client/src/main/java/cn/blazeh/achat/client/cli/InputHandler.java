package cn.blazeh.achat.client.cli;

import cn.blazeh.achat.client.manager.MessageManager;
import cn.blazeh.achat.client.manager.SessionManager;
import cn.blazeh.achat.client.service.ChatService;
import cn.blazeh.achat.client.service.MessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

public class InputHandler implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(InputHandler.class);

    private final InputStream input;

    public InputHandler(InputStream input) {
        this.input = input;
    }

    @Override
    public void run() {
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            while(true) {
                String line = reader.readLine();
                String[] args = line.split("\\s+");
                if(args[0].equalsIgnoreCase("exit"))
                    break;
                switch(args[0].toLowerCase()) {
                    case "msg" -> {
                        if(args.length < 3)
                            break;
                        ChatService.getInstance().sendMessage(
                                MessageService.getInstance().newTempMessage(args[1], args[2])
                        );
                    }
                    case "history" -> {
                        if(args.length < 3)
                            break;

                        String target = args[1];
                        int page = 0;
                        try {
                            page = Integer.parseInt(args[2]);
                        } catch (NumberFormatException e) {
                            LOGGER.error("{}不是一个有效的数字", args[2], e);
                        }

                        MessageManager.INSTANCE.getConversationMessages(
                                SessionManager.INSTANCE.getSession().getUserId(),
                                target,
                                10, page*10
                        ).forEach(msg -> LOGGER.info("[{}][{} -> {}] {}",
                                MessageService.parseTimestamp(msg.getTimestamp()),
                                msg.getSender(),
                                msg.getReceiver(),
                                msg.getContent()
                        ));
                    }
                    default -> {}
                }
            }
        } catch (IOException e) {
            LOGGER.error("命令输入时出现异常", e);
        }
    }
}
