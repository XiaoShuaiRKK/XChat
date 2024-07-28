var stompClient = null;
var username = null;

function connect(event) {
    username = $("#username").val().trim();

    if(username) {
        var socket = new SockJS('/chat-websocket');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);

            stompClient.subscribe('/topic/public', function (messageOutput) {
                showMessageOutput(JSON.parse(messageOutput.body));
            });

            stompClient.send("/app/chat.addUser",
                {},
                JSON.stringify({sender: username, type: 'JOIN'})
            );

            $("#username-page").addClass('d-none');
            $("#chat-page").removeClass('d-none');
        }, function (error) {
            alert('Could not connect to WebSocket server. Please refresh this page to try again!');
        });
    }
    event.preventDefault();
}

function sendMessage(event) {
    var messageContent = $("#messageInput").val().trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        $("#messageInput").val("");
    }
    event.preventDefault();
}

function showMessageOutput(messageOutput) {
    var messageElement = $("<li>");

    if(messageOutput.type === 'JOIN') {
        messageElement.append("<strong>" + messageOutput.sender + "</strong> joined the chat");
    } else if (messageOutput.type === 'LEAVE') {
        messageElement.append("<strong>" + messageOutput.sender + "</strong> left the chat");
    } else {
        messageElement.append("<strong>" + messageOutput.sender + ":</strong> " + messageOutput.content);
    }

    $("#messageArea").append(messageElement);
}

$(document).ready(function () {
    $("#connectButton").click(connect);
    $("#sendButton").click(sendMessage);
    $("#messageInput").keypress(function(e) {
        if (e.keyCode === 13) { // Enter key pressed
            sendMessage(e);
        }
    });
});
