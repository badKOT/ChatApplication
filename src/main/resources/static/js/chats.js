'use strict';

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const chatHeader = document.querySelector('.chat-header');
const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

messageForm.addEventListener('submit', sendMessage, true);
chatInfo = JSON.parse(chatInfo);

let stompClient = null;
connect();

function connect() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect, onError);
    loadChatInfo()
}

function loadChatInfo() {
    const headerElement = document.createElement('h2');
    const headerText = document.createTextNode(chatInfo.chat.title);
    headerElement.appendChild(headerText);
    chatHeader.appendChild(headerElement);
}

function onConnect() {
    // subscribe to the public topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // pass username to the server
    stompClient.send(
        '/app/chat.addUser',
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    connectingElement.classList.add('hidden')
    let messageList = chatInfo.messageList;
    for (let i = 0; i < messageList.length; i++) {
        onMessageReceived(messageList[i], false);
    }
}

function onMessageReceived(payload, removeHeaders = true) { // TODO() чаты пересекаются. fix it
    const message = removeHeaders ? JSON.parse(payload.body) : payload;

    let messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');

        let avatarElement = document.createElement('i');
        let avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    let textElement = document.createElement('p');
    let messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server'
    connectingElement.style.color = 'red';
}

function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {

        const chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT',
            sent: new Date(Date.now()).toISOString(),
            chatId: chatInfo.chat.id
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
