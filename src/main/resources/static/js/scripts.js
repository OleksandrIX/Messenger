var stompClient = null;
var notificationCount = 0;


var Message;
    Message = function (arg) {
        this.text = arg.text,
        this.sender=arg.sender,
        this.recipient=arg.recipient,
        this.time=arg.time;
        this.message_side = arg.message_side;
        this.chat_id = arg.chat_id;
        this.draw = function (_this) {
            return function () {
                var $message;
                $message = $($('.message_template').clone().html());
                $message.addClass(_this.message_side).find('.text').html(_this.text);
                $('.messages').append($message);
                return setTimeout(function () {
                    return $message.addClass('appeared');
                }, 0);
            };
        }(this);
        return this;
    };
$(document).ready(function() {

    console.log("Index page is ready");
    scrollHeight();
    connect();

    $("#send").click(function() {
        sendMessage();
    });

    $("#send-private").click(function() {
        sendPrivateMessage();
    });

    $("#notifications").click(function() {
        resetNotificationCount();
    });

});
$(document).keypress(function (e) {
    if (e.which == 13) {
     document.getElementById("send-private").click();
    }
});

function connect() {
    var socket = new SockJS('/our-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });

        stompClient.subscribe('/user/topic/private-messages', function (message) {
            showMessage(JSON.parse(message.body).content,
            JSON.parse(message.body).messageSide,
            JSON.parse(message.body).sender,
            JSON.parse(message.body).time,
            JSON.parse(message.body).chat_id);
        });

        stompClient.subscribe('/topic/global-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });

        stompClient.subscribe('/user/topic/private-notifications', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
    });
}
function scrollHeight(){
var $messages
            $messages = $('.messages');
            return $messages.animate({ scrollTop: $messages.prop('scrollHeight') }, 300);
}

function showMessage(mes,messageSide,sender,time) {
var $messages,message

            $('.message_input').val('');
            $messages = $('.messages');
//            message_side = message_side === 'left' ? 'right' : 'left';
              message = new Message({
                text: mes,
                message_side: messageSide
            });
            message.draw();
            return $messages.animate({ scrollTop: $messages.prop('scrollHeight') }, 300);

}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/ws/message", {}, JSON.stringify({'messageContent': $("#message").val()}));
}

function sendPrivateMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message", {}, JSON.stringify({'messageContent': $("#private-message").val(),
   'messageSide':'right',
   'recipient': $("#id_recipient").val(),
   'chat_id':$("#chat_id").val()
   }));
}

function updateNotificationDisplay() {
    if (notificationCount == 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}