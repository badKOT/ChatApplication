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
    chatContainer.classList.add('ListItem', 'Chat', 'chat-item-clickable');

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

    let avatarContainer = document.createElement('div');
    avatarContainer.classList.add('status');
    let avatarElement = document.createElement('div');
    avatarElement.classList.add('Avatar');
    let avatarText = document.createTextNode(chat.title[0]);
    avatarElement.appendChild(avatarText);
    avatarElement.style['background-color'] = getAvatarColor(chat.title);
    avatarContainer.appendChild(avatarElement);

    let chatInfoContainer = document.createElement('div');
    chatInfoContainer.classList.add('info');
    let titleContainer = document.createElement('div');
    titleContainer.classList.add('title');
    let titleElement = document.createElement('span');
    let titleText = document.createTextNode(chat.title);
    titleElement.appendChild(titleText);
    titleContainer.appendChild(titleElement);
    chatInfoContainer.appendChild(titleContainer);

    chatElement.appendChild(avatarContainer);
    chatElement.appendChild(chatInfoContainer);
    chatContainer.appendChild(chatElement);
    chatContainer.style['top'] = `${72 * shift}px`;

    chatListElement.appendChild(chatContainer);
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
