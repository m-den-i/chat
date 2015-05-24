'use strict';

var getId = function() {
    var currentDate = Date.now();
    var random = Math.random() * Math.random();
    return Math.floor(currentDate * random).toString();
};

var theMessage = function(sender, message,isDeleted, id) {
    return {
        id: id || getId(),
        senderName: sender,
        messageText: message,
        isDeleted: isDeleted
    };
};


var chatState = {
    chatUrl: 'http://localhost:8080/ChatServlet',
    currentUser: null,
    messageList: [],
    token: 'TN11EN',
    isAvailable: false
};

function run() {
    var appContainer = document.getElementsByClassName('appChat')[0];
    appContainer.addEventListener('click', delegateEvent);
    appContainer.addEventListener('keydown', delegateEvent);
    var currentUser = restoreCurrentUser();
    setCurrentUser(currentUser);
    restoreMessages();
}

function setCurrentUser(user) {
    if (user != null) {
        var userName = document.getElementById('sign-name');
        userName.value = user;
        onSignInClick();
    }
}

function delegateEvent(evtObj) {
    if (evtObj.type === 'click') {
        if (evtObj.target.classList.contains('sign-button')) {
            onSignClick(evtObj.target);
        }
        if (evtObj.target.classList.contains('send-button')) {
            onMessageSend();
        }
        if (evtObj.target.classList.contains('tools-button')) {
            onMessageEdit(evtObj.target);
        }
    }
    if (evtObj.type === 'keydown' && evtObj.ctrlKey && evtObj.keyCode == 13) {
        if (evtObj.target.classList.contains('send-message')) {
            onMessageSend();
        }
        if (evtObj.target.classList.contains('message-edit-text')) {
            onMessageEdit(evtObj.target);
        }
    }
}

function onSignClick(button) {
    if (button.id == 'sign-in' || button.id == 'sign-change') {
        onSignInClick();
    }
    if (button.id == 'sign-edit') {
        onSignEditClick();
    }
    if (button.id == 'sign-out') {
        onSignOutClick();
    }
}

function sendActivator(activate) {
    var sendMessage = document.getElementsByClassName('send-message')[0];
    var messageText = sendMessage.firstElementChild;
    messageText.disabled = !activate;
    if (activate == true) {
        messageText.value = '';
    }
    else {
        messageText.value = '';
    }
    sendMessage.lastElementChild.disabled = !activate;
}

function inputChecker(text) {
    if (text == '' || text.trim() == '') {
        alert("Check your input!");
        return false;
    }
    if (chatState.currentUser == null) alert("You should login!");
    return true;
}

function createSignStructure(type) {
    var sign = document.getElementsByClassName('sign')[0];
    var htmlAsText;
    if (type == 'read') {
        htmlAsText = '<xmp id="user-name">' + chatState.currentUser + '</xmp>'
        + '<button id="sign-edit" class="sign-button">Edit</button>'
        + '<button id="sign-out" class="sign-button">Sign Out</button>';
    }
    if (type == 'modify') {
        htmlAsText = '<input id="sign-name" type="text" maxlength="25">'
        + '<button id="sign-change" class="sign-button">OK</button>';
    }
    if (type == 'out') {
        htmlAsText = '<input id="sign-name" type="text" maxlength="25">'
        + '<button id="sign-in" class="sign-button">Sign in</button>';
    }
    sign.innerHTML = htmlAsText;
}

function onSignInClick() {
    var name = document.getElementById('sign-name');
    if (inputChecker(name.value) == true) {
        chatState.currentUser = name.value;
        storeCurrentUser(chatState.currentUser);
        createSignStructure('read');
        sendActivator(true);
        createOrUpdateMessages(chatState.messageList);
    }
    else {
        name.focus();
    }
}

function onSignEditClick() {
    createSignStructure('modify');
    var name = document.getElementById('sign-name');
    name.value = chatState.currentUser;
    name.focus();
    sendActivator(false);
}

function onSignOutClick() {
    createSignStructure('out');
    chatState.currentUser = null;
    localStorage.removeItem("Current user");
    createOrUpdateMessages(chatState.messageList);
    sendActivator(false);
}

function onMessageSend(continueWith) {
    var messageText = document.getElementById('message-text');
    if (inputChecker(messageText.value) == true) {
        var message = theMessage(chatState.currentUser, messageText.value.trim().replace(new RegExp("\n", 'g'), "\\n"),false);
        postRequest(chatState.chatUrl, JSON.stringify(message), function () {
            continueWith && continueWith();
        });
        messageText.value = '';
    }
    else {
        messageText.focus();
    }
}

function scrollDown() {
    var chatBox = document.getElementsByClassName('chat')[0];
    chatBox.scrollTop = chatBox.scrollHeight;
}

function addMessage(message) {
    var item = createMessage(message);
    var chatBox = document.getElementsByClassName('chat')[0];
    chatState.messageList.push(message);
    chatBox.appendChild(item);
    scrollDown();
}

function createMessage(message) {
    var item = document.createElement('div');
    item.innerHTML = '<div class="message sender-name">' + message.senderName + '</div>'
    + '<xmp class="message message-item">' + message.messageText + '</xmp>';
    item.setAttribute('class', 'message');
    item.setAttribute('id', message.id);
    updateMessage(item, message);
    return item;
}

function updateMessage(divMessage, message) {
    if (message.isDeleted == 'true') {
        setDelete(divMessage, message);
        return;
    }
    if (chatState.currentUser != undefined && message.senderName.toLowerCase() == chatState.currentUser.toLowerCase()) {
        addTool(divMessage);
    }
    else {
        removeTool(divMessage);
    }
}

function setDelete(divMessage, message) {
    divMessage.innerHTML = '<div class="message sender-name">' + ' '
    + message.senderName + '</div>' + '<p class="modify">deleted</p>';
}



function addTool(divMessage) {
    var tools = divMessage.getElementsByClassName('tools')[0];
    if (tools === undefined) {
        var positionAfter = divMessage.getElementsByClassName('message-item')[0];
        var item = document.createElement('div');
        item.innerHTML = '<button id="message-delete" class="message tools-button">delete</button>';
        item.setAttribute('class', 'message tools');
        divMessage.insertBefore(item, positionAfter);
    }
}

function removeTool(divMessage) {
    var tools = divMessage.getElementsByClassName('tools')[0];
    if (tools != undefined) {
        divMessage.removeChild(tools);
    }
}

function onMessageEdit(item) {
    if (item.id == 'message-delete') {
        onMessageDelete(item.parentElement.parentElement);
    }
}

function onMessageDelete(divMessage, continueWith) {
    var id = divMessage.attributes['id'].value;
    var deleteMessage = theMessage("", "", id);
    deleteRequest(chatState.chatUrl, JSON.stringify(deleteMessage), function () {
        continueWith && continueWith();
    });
}

function storeCurrentUser(user) {
    localStorage.removeItem("Current user");
    localStorage.setItem("Current user", JSON.stringify(user));
}

function restoreCurrentUser() {
    var currentUser = localStorage.getItem("Current user");
    return currentUser && JSON.parse(currentUser);
}

function restoreMessages(continueWith) {
    var url = chatState.chatUrl + '?token=' + chatState.token;
    getRequest(url, function (responseText) {
        getHistory(responseText, function () {
            setTimeout(function () {
                restoreMessages(continueWith);
            }, 1000);
        });
    });
}

function getHistory(responseText, continueWith) {
    var response = JSON.parse(responseText);
    chatState.token = response.token;
    createOrUpdateMessages(response.messages);
    continueWith && continueWith();
}

function createOrUpdateMessages(messages) {
    var chatBox = document.getElementsByClassName('chat')[0];
    if (!chatState.messageList.length) {
        chatBox.innerHTML = "";//this is when is server error; The information on server lost after error!
    }
    for (var i = 0; i < messages.length; i++) {
        var index = findMessageIndexById(messages[i].id);
        if (index > -1) {
            chatState.messageList[index] = messages[i];
            updateMessage(chatBox.children[index], messages[i]);
        }
        else {
            addMessage(messages[i]);
        }
    }
}

function findMessageIndexById(id) {
    for (var i = 0; i < chatState.messageList.length; i++) {
        if (chatState.messageList[i].id == id) {
            return i;
        }
    }
    return -1;
}

function getRequest(url, continueWith, continueWithError) {
    ajax('GET', url, continueWith, continueWithError);
}

function postRequest(url, continueWith, continueWithError) {
    ajax('POST', url, continueWith, continueWithError);
}

function deleteRequest(url, continueWith, continueWithError) {
    ajax('DELETE', url,  continueWith, continueWithError);
}


function defaultErrorHandler(message) {
    console.error(message);
    chatState.token = 'TN11EN';//this is when is server error; The information on server lost after error!
    chatState.messageList = [];//this is when is server error; The information on server lost after error!
    restoreMessages();
}

function isError(text) {
    if(text == "")
        return false;
    try {
        var obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }
    return !!obj.error;
}

function ajax(method, url, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();
    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);
    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;
        if (xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }
        if (isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }
        continueWith(xhr.responseText);
    };
    xhr.ontimeout = function () {
        continueWithError('Server timed out!');
    };
    xhr.onerror = function () {
        var errMsg = 'Server connection error!\n' +
            '\n' +
            'Check if \n' +
            '- server is active\n' +
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };
    xhr.send(continueWith);
}



