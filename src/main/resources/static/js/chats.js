'use strict';

const messageInput = document.querySelector('#message');
const messageArea = document.querySelector('#messageArea');
const chatStatusBar = document.querySelector('.connecting');
const chatHeader = document.querySelector('.chat-header h2');
const usernameInput = document.querySelector('#username-input-field');
const notificationElement = document.querySelector('#notification');
const chatPageSection = document.querySelector('#chat-page');
const chatTitleInput = document.querySelector('#create-chat-title');
const createChatHolder = document.querySelector('#create-chat-holder');
const overlay = document.querySelector('#overlay');
const userListPlaceHolder = document.querySelector('#userListPlaceHolder');
const userList = document.querySelector('#userList');

let timeout;
usernameInput.addEventListener('input', function() {
    clearTimeout(timeout);
    timeout = setTimeout(function() {
        userList.style.display = 'none';
        userListPlaceHolder.style.display = 'block';
        checkAddUserInput();
    }, 1000);
});
createChatHolder.addEventListener('mouseenter', () => chatTitleInput.type = 'text');
createChatHolder.addEventListener('mouseleave', () => chatTitleInput.type = 'hidden');
overlay.addEventListener('click', () => hideAddUserAction());
document.querySelector('#messageForm').addEventListener('submit', sendMessage, true);
document.querySelector('#addUserForm').addEventListener('submit', addUser, true);
document.querySelector('#create-chat-form button').onclick = ((event) => createChatEvent(event));

let stompClient = null;

function connect(chat_info) {
    // Assign to global variable so we can use it everywhere
    chatInfo = chat_info;
    // if we're switching between chats, clear out the page
    if (chatPageSection.style.display === 'block') {
        chatHeader.innerText = '';
        messageArea.innerHTML = '';
        chatStatusBar.classList.remove('disabled');
    }

    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnect, onError);
    chatPageSection.style.display = 'block';

    chatHeader.innerText = chatInfo.title;
    // TODO() add chat info like participants and stuff
}

function onConnect() {
    // subscribe to the public topic
    stompClient.subscribe('/topic/chat/' + chatInfo.id, onMessageReceived);

    chatStatusBar.classList.add('disabled');
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
        let textElement = prepareTextElement(message.content + ' joined!');
        messageElement.appendChild(textElement);
    }

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function onError() {
    chatStatusBar.textContent = 'Could not connect to WebSocket server. Please, reload the page';
    chatStatusBar.classList.remove('disabled');
    chatStatusBar.style.color = 'red';
}

function prepareAvatarElement(sender) {
    let avatarElement = document.createElement('i');
    avatarElement.innerText = sender.username[0];
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
    usernameElement.innerText = sender;
    return usernameElement;
}
function prepareTextElement(content) {
    let textElement = document.createElement('p');
    textElement.innerText = content;
    return textElement;
}
function prepareTimeElement(timestamp, adjustTimeFormat) {
    let timeElement = document.createElement('span');
    timeElement.classList.add('message-time');
    const date = adjustTimeFormat ? new Date(timestamp * 1000) : new Date(timestamp);
    timeElement.innerText = date.toISOString();
    return timeElement;
}

function sendMessage(event) {
    event.preventDefault();
    const messageContent = messageInput.value.trim();

    if (messageContent && stompClient) {
        const chatMessage = {
            sender: {id: Number(userId), username: username},
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
}

function addUser(event) {
    event.preventDefault();
    usernameInput.type === 'hidden' ? triggerAddUserAction() : hideAddUserAction();
}

function createChatEvent(event) {
    event.preventDefault();

    const chatTitle = chatTitleInput.value.trim();
    if (isNullOrEmpty(chatTitle)) {
        throw new Error("Chat title cannot be empty!");
    }

    sendNewChatRequest({"title": chatTitle})
        .then(response => response.text())
        .then(newChatId => {
            loadChatList(userId);
            window.history.replaceState(null, null, `/chats/${newChatId}`);
            fetchChatInfo(newChatId)
                .then(payload => connect(payload));
    });
    chatTitleInput.value = '';
    chatTitleInput.type = 'hidden';
}

function triggerAddUserAction() {
    overlay.style.display = 'block';
    usernameInput.type = 'text';
    userListPlaceHolder.style.display = 'block';
    checkAddUserInput();
}
function hideAddUserAction() {
    overlay.style.display = 'none';
    usernameInput.type = 'hidden';
    userListPlaceHolder.style.display = 'none';
    userList.style.display = 'none';
}
function checkAddUserInput() {
    if (isNullOrEmpty(usernameInput.value) || usernameInput.value.trim().length < 3) {
        renderUserListPlaceHolderWithMessage('Start typing...');
    } else {
        renderUserListPlaceHolderWithMessage('Just a second...');
        searchByUsername(usernameInput.value.trim())
            .then(payload => renderSearchResults(payload));
    }
}

function renderUserListPlaceHolderWithMessage(text) {
    userListPlaceHolder.innerHTML = '';
    const message = document.createElement('p');
    message.style.textAlign = 'center';
    message.innerText = text;
    userListPlaceHolder.appendChild(message);
}

function renderSearchResults(accounts) {
    userListPlaceHolder.innerHTML = '';
    if (accounts.length === 0) {
        renderUserListPlaceHolderWithMessage('No users found');
        return;
    }
    userList.innerHTML = '';
    userListPlaceHolder.style.display = 'none';
    userList.style.display = 'block';
    accounts.forEach((acc) => {
        userList.appendChild(renderAccount(acc));
    });
}

function renderAccount(account) {
    const searchResultItem = document.createElement('li');
    let buttonElement = prepareAccountButtonElement(account);
    searchResultItem.appendChild(buttonElement);
    return searchResultItem;
}

function prepareAccountButtonElement(account) {
    const buttonElement = document.createElement('a');
    const accountAvatar = document.createElement('div');
    const accountInfoWrapper = prepareAccountInfoWrapperElement(account);

    buttonElement.role = 'button';
    buttonElement.classList.add('ListItem-button');
    accountAvatar.classList.add('account-avatar');

    buttonElement.appendChild(accountAvatar);
    buttonElement.appendChild(accountInfoWrapper);
    buttonElement.onclick = (event) => {
        event.preventDefault();
        if (isUserInChat(account.id)) {
            notificationElement.style.opacity = '100';
            notificationElement.textContent = `User ${account.username} is already in the chat!`;

            setTimeout(function () {
                notificationElement.style.opacity = '0';
            }, 3000);
            return;
        }
        addUserToChat(account.id, chatInfo.id, userId)
            .then(() => {
                hideAddUserAction();
                usernameInput.value = '';
                fetchChatInfo(chatInfo.id);
            });
    };
    return buttonElement;
}

function prepareAccountInfoWrapperElement(account) {
    const accountInfoWrapper = document.createElement('div');
    const accountUsername = document.createElement('span');
    const accountInChatFlag = document.createElement('span');

    accountInfoWrapper.classList.add('account-info-wrapper');
    accountUsername.innerText = account.username;
    if (isUserInChat(account.id)) {
        accountInChatFlag.innerText = 'in chat';
        accountInChatFlag.classList.add('account-in-chat-flag');
    }

    accountInfoWrapper.appendChild(accountUsername);
    accountInfoWrapper.appendChild(accountInChatFlag);
    return accountInfoWrapper;
}
