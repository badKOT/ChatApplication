'use strict';

const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const connectingElement = document.querySelector('.connecting');
const chatHeader = document.querySelector('.chat-header h2');
const addUserForm = document.querySelector('#addUserForm');
const userIdInput = document.querySelector('#userId_addUserForm');
const notificationElement = document.querySelector('#notification');
const chatPageSection = document.querySelector('#chat-page');

messageForm.addEventListener('submit', sendMessage, true);
addUserForm.addEventListener('submit', addUser, true);

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
        onMessageReceived(messageList[i], false);
    }
}

// TODO() clean up and refactor
function onMessageReceived(payload, removeHeaders = true) {
    const message = removeHeaders ? JSON.parse(payload.body) : payload;

    let messageElement = document.createElement('li');

    if (message.type === 'CHAT') {
        messageElement.classList.add('chat-message');

        let avatarElement = document.createElement('i');
        let avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var messageWrapper = document.createElement('div');
        messageWrapper.classList.add('message-wrapper');
        messageElement.appendChild(messageWrapper)

        let usernameElement = document.createElement('span');
        let usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageWrapper.appendChild(usernameElement);

        let textElement = document.createElement('p');
        let messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);

        messageWrapper.appendChild(textElement);
        let timeElement = document.createElement('span');
        timeElement.classList.add('message-time');
        let timeText = document.createTextNode(message.sent);
        timeElement.appendChild(timeText);
        messageWrapper.appendChild(timeElement);
    } else {
        messageElement.classList.add('event-message');

        if (message.type === 'JOIN') {
            message.content = message.sender + ' joined!';
        } else {
            message.content = message.send + ' left!';
        }

        let textElement = document.createElement('p');
        let messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);
        messageElement.appendChild(textElement);
    }

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

    // when someone pressed the "Add user" button first time, show that input form
    if (userIdInput.type === 'hidden') {
        userIdInput.type = 'text';
        userIdInput.focus();
        event.preventDefault();
        return;
    }

    const userId = userIdInput.value.trim();
    if (userId && isUserInChat(userId)) {
        notificationElement.style.opacity = '100';
        notificationElement.textContent = 'User is already in the chat.';

        setTimeout(function () {
            notificationElement.style.opacity = '0';
        }, 3000);
        event.preventDefault();

    } else if (userId && !isUserInChat(userId)) {
        userIdInput.value = '';
        userIdInput.type = 'hidden';
        event.preventDefault();

        // send event to the server
        stompClient.send(
            '/app/chat.addUser',
            {},
            JSON.stringify({
                sender: userId,
                type: 'JOIN',
                sent: new Date(Date.now()).toISOString(),
                chatId: chatInfo.id
            })
        );
    }
}

function isUserInChat(userId) {
    return !!chatInfo.participants
        .map(user => user.id.toString())
        .includes(userId);
}
