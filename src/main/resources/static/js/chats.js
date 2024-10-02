'use strict';

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const chatHeader = document.querySelector('.chat-header h2');
const addUserFormHolder = document.querySelector('.add-user-form-holder');
const addUserForm = document.querySelector('#addUserForm');
const userIdInput = document.querySelector('#userId_addUserForm');
const notificationElement = document.querySelector('#notification');
const chatPageSection = document.querySelector('#chat-page');
const createChatButton = document.querySelector('#create-chat-form button');
const chatTitleInput = document.querySelector('#create-chat-title');
const createChatHolder = document.querySelector('#create-chat-holder');

messageForm.addEventListener('submit', sendMessage, true);
addUserForm.addEventListener('submit', addUser, true);
createChatButton.onclick = ((event) => createChatEvent(event));
createChatHolder.addEventListener('mouseenter', () => revealTextInputField(chatTitleInput));
createChatHolder.addEventListener('mouseleave', () => hideTextInputField(chatTitleInput));
addUserFormHolder.addEventListener('mouseenter', () => revealTextInputField(userIdInput));
addUserFormHolder.addEventListener('mouseleave', () => hideTextInputField(userIdInput));

let stompClient = null;

function connect(chat_info) {
    // Assign to global variable so we can use it everywhere
    chatInfo = chat_info;
    // if we're switching between chats, clear out the page
    if (chatPageSection.style.display === 'block') {
        chatHeader.innerText = '';
        messageArea.innerHTML = '';
        connectingElement.classList.remove('hidden');
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect, onError);
    chatPageSection.style.display = 'block';
    loadChatHeader()
}

function loadChatHeader() {
    chatHeader.innerText = chatInfo.title;
    // TODO() add chat info like participants and stuff
}

function onConnect() {
    // subscribe to the public topic
    stompClient.subscribe('/topic/chat/' + chatInfo.id, onMessageReceived);

    connectingElement.classList.add('hidden')
    let messageList = chatInfo.messageList;
    for (let i = 0; i < messageList.length; i++) {
        onMessageReceived(messageList[i], false, true);
    }
}

function onMessageReceived(payload, removeHeaders = true, adjustTimeFormat = false) {

    const message = removeHeaders ? JSON.parse(payload.body) : payload;
    let messageElement = document.createElement('li');

    if (message.type === 'CHAT') {
        messageElement.classList.add('chat-message');

        let avatarElement = prepareAvatarElement(message.sender);
        let messageWrapper = prepareChatMessageElement(message, adjustTimeFormat);

        messageElement.appendChild(avatarElement);
        messageElement.appendChild(messageWrapper);

    } else {
        messageElement.classList.add('event-message');

        if (message.type === 'JOIN') {
            message.content = message.content + ' joined!';
        } else {
            message.content = message.sender + ' left!';
        }

        let textElement = prepareTextElement(message.content);
        messageElement.appendChild(textElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server'
    connectingElement.classList.remove('hidden');
    connectingElement.style.color = 'red';
}

function prepareAvatarElement(sender) {
    let avatarElement = document.createElement('i');
    let avatarText = document.createTextNode(sender.username[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style.backgroundColor = getAvatarColor(sender.username);
    return avatarElement;
}

function prepareChatMessageElement(message, adjustTimeFormat) {
    let usernameElement = prepareUsernameElement(message.sender.username);
    let textElement = prepareTextElement(message.content);
    let timeElement = prepareTimeElement(message.sent, adjustTimeFormat);

    var messageWrapper = document.createElement('div');
    messageWrapper.classList.add('message-wrapper');
    messageWrapper.appendChild(usernameElement);
    messageWrapper.appendChild(textElement);
    messageWrapper.appendChild(timeElement);

    return messageWrapper;
}

function prepareUsernameElement(sender) {
    let usernameElement = document.createElement('span');
    let usernameText = document.createTextNode(sender);
    usernameElement.appendChild(usernameText);
    return usernameElement;
}

function prepareTextElement(content) {
    let textElement = document.createElement('p');
    let messageText = document.createTextNode(content);
    textElement.appendChild(messageText);
    return textElement;
}

function prepareTimeElement(timestamp, adjustTimeFormat) {
    let timeElement = document.createElement('span');
    timeElement.classList.add('message-time');
    const date = adjustTimeFormat ? new Date(timestamp * 1000) : new Date(timestamp);
    let timeText = document.createTextNode(date.toISOString());
    timeElement.appendChild(timeText);
    return timeElement;
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {

        const chatMessage = {
            sender: {
                id: Number(userId),
                username: username
            },
            content: messageContent,
            type: 'CHAT',
            sent: new Date(Date.now()).toISOString(),
            chatId: chatInfo.id
        };
        stompClient.send(
            '/app/chat.sendMessage',
            {},
            JSON.stringify(chatMessage)
        );
        messageInput.value = '';
    }
    event.preventDefault();
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
}

function addUser(event) {
    event.preventDefault();

    const addedUserId = userIdInput.value;
    if (isNullOrEmpty(addedUserId)) {
        throw new Error("User id cannot be empty!");
    }

    if (isUserInChat(addedUserId)) {
        notificationElement.style.opacity = '100';
        notificationElement.textContent = 'User is already in the chat.';

        setTimeout(function () {
            notificationElement.style.opacity = '0';
        }, 3000);

    } else {
        userIdInput.value = '';
        userIdInput.type = 'hidden';

        // send event to the server
        stompClient.send(
            '/app/chat.addUser',
            {},
            JSON.stringify({
                sender: {
                    id: Number(userId),
                    username: username
                },
                type: 'JOIN',
                sent: new Date(Date.now()).toISOString(),
                chatId: chatInfo.id,
                content: addedUserId
            })
        );
    }
}

function isUserInChat(userId) {
    return !!chatInfo.participants
        .map(user => user.id.toString())
        .includes(userId);
}

function createChatEvent(event) {
    event.preventDefault();

    const chatTitle = chatTitleInput.value.trim();
    if (isNullOrEmpty(chatTitle)) {
        throw new Error("Chat title cannot be empty!");
    }

    sendNewChatRequest({"title": chatTitle}).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        loadChatList(userId);
    });
    // TODO() open created chat
    chatTitleInput.value = '';
    chatTitleInput.type = 'hidden';
}

const sendNewChatRequest = async (chatInfo) => {
    const response = await fetch('http://localhost:8080/write/new-chat', {
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        method: "POST",
        credentials: "same-origin",
        body: JSON.stringify(chatInfo)
    });
    return await response;
}

function isNullOrEmpty(value) {
    return value == null || (typeof value === 'string' && value.trim().length === 0);
}

function revealTextInputField(element) {
    element.type = 'text';
}

function hideTextInputField(element) {
    element.type = 'hidden';
}
