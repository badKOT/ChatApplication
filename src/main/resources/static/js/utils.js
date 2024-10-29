'use strict';

const csrfToken = document.querySelector('meta[name="_csrf"]').content;
const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

const fetchChatList = async (userId) => {
    const response = await fetch('http://localhost:8080/fetch/chatList/sorted/' + userId, {
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
const addUserToChat = async (accountId, chatId, initiatorId) => {
    const response = await fetch('http://localhost:8080/write/user/to/chat', {
        headers: {
            "Content-Type": "application/json",
            [csrfHeader]: csrfToken
        },
        method: "POST",
        credentials: "same-origin",
        body: JSON.stringify({
            userToAddId: accountId,
            chatId: chatId,
            initiatorId: initiatorId
        })
    });
    return await response;
}
const searchByUsername = async (username) => {
    const response = await fetch('http://localhost:8080/fetch/users/search?' + new URLSearchParams({
        username: username
    }), {
        headers: {
            "Content-Type": "application/json",
        },
    });
    if (!response.ok) {
        const technicalErrorMessage = 'Something went wrong. We know about this error and are working on it!';
        renderUserListPlaceHolderWithMessage(technicalErrorMessage);
        throw new Error('Network response was not ok');
    }
    return await response.json();
}

const colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function getAvatarColor(messageSender) {
    let hash = 0;
    for (let i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    const index = Math.abs(hash % colors.length);
    return colors[index];
}

function isUserInChat(userId) {
    return !!chatInfo.participants
        .map(user => user.id.toString())
        .includes(userId.toString());
}

function isNullOrEmpty(value) {
    return value == null || (typeof value === 'string' && value.trim().length === 0);
}
