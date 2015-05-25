import dbaccess.DAO;
import messaging.Message;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by denis on 24.5.15.
 */
@WebServlet(name = "ChatServlet")
public class ChatServlet extends HttpServlet {
    private DAO db;
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        String b, a = "";

        while ((b = reader.readLine()) != null)
            a += b;
        try {
            Message message = new Message(a);
            boolean res = db.addMessage(message);
            int s = 0;
        } catch (Exception ex){
            a = "";
            //throw new ServletException(ex);
        }
        return;
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    public void init(ServletConfig config) throws ServletException {
        db = new DAO();
    }

}
