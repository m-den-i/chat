package messaging;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server implements HttpHandler {
    private List<Message> requestsHistory = new ArrayList<Message>();
    private MessageExchange messageExchange = new MessageExchange();

    public static void main(String[] args) {

        if (args.length != 1)
            System.out.println("Usage: java messaging.Server port");
        else {
            try {
                System.out.println("messaging.Server is starting...");
                Integer port = Integer.parseInt(args[0]);
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                System.out.println("messaging.Server started.");
                String serverHost = InetAddress.getLocalHost().getHostAddress();
                
                server.createContext("/chat", new Server());
                server.setExecutor(null);
                server.start();
            } catch (IOException e) {
                System.out.println("Error creating http server: " + e);


            }
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String response = "";

        if ("GET".equals(httpExchange.getRequestMethod())) {
            response = doGet(httpExchange);
        } else if ("POST".equals(httpExchange.getRequestMethod())) {
            doPost(httpExchange);
        } else if("DELETE".equals(httpExchange.getRequestMethod())) {
            doDelete(httpExchange);
        } else {
            response = "Unsupported http method: " + httpExchange.getRequestMethod();
        }

        sendResponse(httpExchange, response);
    }

    private String doGet(HttpExchange httpExchange) {

        String query = httpExchange.getRequestURI().getQuery();

        if (query != null) {
            Map<String, String> map = queryToMap(query);

            String token = map.get("token");
            if (token != null && !"".equals(token)) {
                int index = messageExchange.getIndex(token);
				int historySize = requestsHistory.size();
                return messageExchange.getServerResponse(requestsHistory.subList(index, historySize), historySize);
            } else {
                return "Token query parameter is absent in url: " + query;
            }
        }
        return  "Absent query in url";
    }

    private void doPost(HttpExchange httpExchange) {
        try {
            Message message = messageExchange.getClientMessage(httpExchange.getRequestBody());
            System.out.println("Get " + message.toString());
            Message requestToStorage = new Message(message);
            requestsHistory.add(requestToStorage);
        } catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }

    private void doDelete(HttpExchange httpExchange) {
        try {
            Message messageId = messageExchange.getClientMessage(httpExchange.getRequestBody());
            boolean check = false;
            for (Message history : requestsHistory) {
                Message historyMessage = history;
                if (messageId.getId().equals(historyMessage.getId())) {
                    if (!historyMessage.isDeleted()) {
                        check = true;
                        Message message = new Message(historyMessage);
                        System.out.println("Delete " + message.getReadableView());
                        message.delete();
                        Message requestToStorage = new Message(message);
                        requestsHistory.add(requestToStorage);
                        break;
                    }
                }
            }
            if (!check) {
                System.err.println("messaging.Message with id : " + messageId.getId() + " doesn't exist or was deleted");
            }
        } catch (ParseException e) {
            System.err.println("Invalid user message: " + httpExchange.getRequestBody() + " " + e.getMessage());
        }
    }


    private void sendResponse(HttpExchange httpExchange, String response) {
        try {
            byte[] bytes = response.getBytes();
            Headers headers = httpExchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin","*");
            httpExchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write( bytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<String, String>();

        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }
}
