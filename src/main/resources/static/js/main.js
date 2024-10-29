'use strict';

const chatListElement = document.querySelector('.chat-list');

function loadChatList(userId) {
    chatListElement.innerHTML = '';
    fetchChatList(userId).then(payload => {
        payload.forEach((chat) => {
            onChatReceived(chat);
        });
    });
}

function onChatReceived(chat) {
    let chatContainer = document.createElement('div');
    let chatElement = prepareChatElement(chat);

    chatContainer.classList.add('ListItem', 'Chat');

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
        fetchChatInfo(chat.id).then(payload => connect(payload));
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

document.addEventListener("DOMContentLoaded", function() {
    if (chatListElement && userId) {
        loadChatList(userId);
    }
    if (chatInfo) {
        connect(JSON.parse(chatInfo));
    }
});
