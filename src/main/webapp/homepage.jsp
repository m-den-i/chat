<%@ page contentType="text/html; charset=UTF-8" %>
<!-- Some info here-->
<!DOCTYPE html>
<html>
<head>
    <title>Chat</title>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="resources/css/style.css">
    <script src = resources/js/scripts.js></script>
</head>
<body class="page" onload="run();">
<div class="appChat">
    <!--form id="chatForm" method="post" action="ChatServlet"-->
    <div class="sign">
        <input id="sign-name" type="text" maxlength="25">
        <button id="sign-in" class="sign-button">Sign in</button>
    </div>
    <div class="chat"></div>
    <div class="send-message">
        <textarea id="message-text" class="send-message message-text" name="message" enabled></textarea>
        <button type="submit" id="send-button" class="send-message send-button" enabled>Send</button>
    </div>
    <!--/form-->
</div>
</body>
</html>