'use strict';

const chatListElement = document.querySelector('.chat-list div');

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function loadChatList(userId) {
    fetchChatList(userId).then(payload => {
        payload.forEach((chat, index) => {
            onChatReceived(chat, index);
        });
    });
}

function onChatReceived(chat, shift) {
    let chatContainer = document.createElement('div');
    let chatElement = prepareChatElement(chat);

    chatContainer.style.top = `${72 * shift}px`;
    chatContainer.classList.add('ListItem', 'Chat', 'chat-item-clickable');

    chatContainer.appendChild(chatElement);
    chatListElement.appendChild(chatContainer);
}

function prepareChatElement(chat) {
    let chatElement = document.createElement('a');
    chatElement.role = 'button';
    chatElement.tabIndex = 0;
    chatElement.classList.add('ListItem-button');

    chatElement.onclick = (event) => {
        event.preventDefault();
        window.history.replaceState(null, null, `/chats/${chat.id}`);
        userIdInput.value = '';
        userIdInput.type = 'hidden';
        fetchChatInfo(chat.id)
            .then(payload => connect(payload));
    };

    let avatarElement = prepareChatAvatar(chat.title);
    let chatInfoContainer = prepareChatInfoElement(chat.title);

    chatElement.appendChild(avatarElement);
    chatElement.appendChild(chatInfoContainer);

    return chatElement;
}

function prepareChatAvatar(title) {
    let avatarContainer = document.createElement('div');
    let avatarElement = document.createElement('div');
    let avatarText = document.createTextNode(title[0]);

    avatarContainer.classList.add('status');
    avatarElement.classList.add('Avatar');
    avatarElement.style.backgroundColor = getAvatarColor(title);

    avatarElement.appendChild(avatarText);
    avatarContainer.appendChild(avatarElement);

    return avatarContainer;
}

function prepareChatInfoElement(title) {
    let chatInfoContainer = document.createElement('div');
    let titleContainer = document.createElement('div');
    let titleElement = document.createElement('span');
    let titleText = document.createTextNode(title);

    titleContainer.classList.add('title');
    chatInfoContainer.classList.add('info');

    titleElement.appendChild(titleText);
    titleContainer.appendChild(titleElement);
    chatInfoContainer.appendChild(titleContainer);

    return chatInfoContainer;
}

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
}

const fetchChatList = async (userId) => {
    const response = await fetch('http://localhost:8080/fetch/chatList/' + userId, {
        headers: {
            "Content-Type": "application/json",
        },
    });
    return await response.json();
}

const fetchChatInfo = async (chatId) => {
    const response = await fetch('http://localhost:8080/fetch/chatInfo/' + chatId, {
        headers: {
            "Content-Type": "application/json",
        },
    });
    return await response.json();
}

document.addEventListener("DOMContentLoaded", function() {
    if (chatListElement && userId) {
        loadChatList(userId);
    }
    if (chatInfo) {
        connect(JSON.parse(chatInfo));
    }
});
