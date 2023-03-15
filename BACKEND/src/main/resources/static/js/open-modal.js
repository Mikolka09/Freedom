$('#DeleteUserModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let action = "/user/delete/" + id;
    let label = "Are you sure you want to delete your account?";
    let text = "Select \"Delete\" below if you are ready to remove your account"
    let modal = $(this);
    modal.find('#deleteModalLabel').text(label);
    modal.find('#text').text(text);
    modal.find('#action').prop("href", action);
})

$('#AlertModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let name = button.data('name');
    let show = button.data('show');
    let alert = button.data('text');
    let date = button.data('date');
    let modal = $(this);
    $('#actionAcceptedModal').attr('data-id', id);
    $('#actionDenyModal').attr('data-id', id);
    $('#actionConfirmModal').attr('data-id', id);
    modal.find('#alertModalLabel').text(name);
    modal.find('#text').text(alert);
    modal.find('#date-alert').text(date);
    if (alert.split(' ')[2] === 'accepted' ||
        alert.split(' ')[2] === 'denied' ||
        alert.split(' ')[2] === 'read' ||
        alert.split(' ')[2] === 'broke')
        modal.find('#footer-confirm').hide();
    else
        modal.find('#footer-accepted').hide();
    changeStatusAlert(id, show);
})

$('#MessageModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let classButton = button.data('class');
    let modal = $(this);
    let show = button.data('show');
    let name = button.data('name');
    let alert = button.data('text');
    let date = button.data('date');
    if (classButton !== "viewMessage") {
        let id = button.data('id');
        let window = button.data('button');
        let idFrom = button.data('id-from');
        let idTo = button.data('id-to');
        let name = button.data('name');
        changeTextMessage(modal, alert);
        let date = button.data('date');
        let dies = button.data('dies');
        let accept = $('#messageAcceptedModal');
        if (typeof dies !== "undefined")
            if (dies === true)
                accept.hide();
            else
                accept.show();
        let answer = $('#messageAnswerModal');
        accept.attr('data-id', id);
        accept.attr('data-button', window);
        accept.attr('data-show', show);
        accept.attr('data-id-from', idFrom);
        accept.attr('data-id-to', idTo);
        answer.attr('data-id', id);
        answer.attr('data-id-from', idFrom);
        answer.attr('data-id-to', idTo);
        answer.attr('data-show', show);
        answer.attr('data-name', name);
        answer.attr('data-date', date);
        answer.attr('data-text', alert);
        modal.find('#messageModalLabel').text(name);
        modal.find('#date-message').text(date);
        if (typeof dies === "undefined")
            changeStatusMessage(id, show);
    } else {
        changeTextMessage(modal, alert);
        modal.find('#messageModalLabel').text(name);
        modal.find('#date-message').text(date);
        $('#messageAcceptedModal').hide();
        $('#messageAnswerModal').hide();
    }
})

function changeTextMessage(modal, alert) {
    let arrText = "";
    if (alert.indexOf(']') !== -1) {
        arrText = alert.split(']\n');
    }
    let alertAnswer = "";
    let text = "";
    if (arrText.length > 0) {
        alertAnswer = arrText[0] + ']';
        text = arrText[1];
        modal.find('#text-mess-answer').text(alertAnswer);
        modal.find('#text-mess').text(text);
    } else {
        modal.find('#text-mess').text(alert);
        modal.find('#text-mess-answer').text('');
    }
}

function changeStatusMessage(id, show) {
    $.get({
        url: '/user/messages/change/' + id,
        success: (data) => {
            printMessages(data, show);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

function printMessages(data, show) {
    if (data != null) {
        let container = document.getElementById('list-messages');
        document.getElementById('messages-size').innerText =
            data.length === 0 ? '' : (data.length > 6 ? '6+' : data.length);
        let h1 = document.createElement('h6');
        h1.className = "dropdown-header";
        h1.innerText = "Message Center";
        container.innerHTML = "";
        container.appendChild(h1);
        let i = 0;
        while (i < (data.length > 6 ? 6 : data.length)) {
            let a = document.createElement('a');
            a.className = "dropdown-item d-flex align-items-center";
            a.dataset.id = data[i].id;
            a.dataset.idFrom = data[i].invite.userFrom.id;
            a.dataset.idTo = data[i].invite.userTo.id;
            a.dataset.button = "toolbar";
            a.dataset.name = data[i].invite.userFrom.fullName;
            a.dataset.text = data[i].message;
            a.dataset.target = "#MessageModal";
            a.dataset.toggle = "modal";
            a.dataset.show = !!show;
            a.dataset.date = correctDate(data[i].createdAt);
            a.href = "#";
            let div1 = document.createElement('div');
            div1.className = "dropdown-list-image mr-3";
            let img1 = document.createElement('img');
            img1.className = "rounded-circle";
            img1.alt = "Avatar";
            img1.src = "/" + data[i].invite.userFrom.avatarUrl;
            div1.appendChild(img1);
            a.appendChild(div1);
            let div2 = document.createElement('div');
            div2.className = "status-indicator bg-success";
            div1.appendChild(div2);
            let div3 = document.createElement('div');
            div3.style.fontWeight = data[i].invite.status === "NOT_VIEWED" ? "normal" : "bold";
            let div4 = document.createElement('div');
            div4.className = "text-truncate";
            div4.id = "text-message";
            div4.innerText = data[i].message;
            div3.appendChild(div4);
            let div5 = document.createElement('div');
            div5.className = "small text-gray-500";
            div5.innerText = data[i].invite.userFrom.fullName + ' - ' + correctDate(data[i].createdAt);
            div3.appendChild(div5);
            a.appendChild(div3);
            container.appendChild(a);
            i++;
        }
        let a2 = document.createElement('a');
        a2.className = "dropdown-item text-center small text-gray-500";
        if (!show)
            a2.href = "/user/messages";
        else
            a2.href = "/admin/messages";
        a2.innerText = "Read More Messages";
        container.appendChild(a2);
    }
}

function changeStatusAlert(id, show) {
    $.get({
        url: '/user/alerts/change/' + id,
        success: (data) => {
            printAlerts(data, show);
        },
        error: (err) => {
            console.log("Error: " + err);
        }
    });
}

function printAlerts(data, show) {
    if (data != null) {
        let container = document.getElementById('list-alerts');
        document.getElementById('alerts-size').innerText =
            data.length === 0 ? '' : (data.length > 3 ? '3+' : data.length);
        let h1 = document.createElement('h6');
        h1.className = "dropdown-header";
        h1.innerText = "Alerts Center";
        container.innerHTML = "";
        container.appendChild(h1);
        let i = 0;
        while (i < (data.length > 3 ? 3 : data.length)) {
            let a = document.createElement('a');
            a.className = "dropdown-item d-flex align-items-center";
            a.dataset.id = data[i].id;
            a.dataset.name = data[i].invite.userFrom.fullName;
            a.dataset.text = data[i].text;
            a.dataset.target = "#AlertModal";
            a.dataset.toggle = "modal";
            a.dataset.show = !!show;
            a.dataset.date = correctDate(data[i].createdAt);
            a.href = "#";
            let div1 = document.createElement('div');
            div1.className = "mr-3";
            let div2 = document.createElement('div');
            div2.className = "icon-circle bg-warning";
            let i1 = document.createElement('i');
            i1.className = "fas fa-exclamation-triangle text-white";
            div2.appendChild(i1);
            div1.appendChild(div2);
            a.appendChild(div1);
            let div3 = document.createElement('div');
            div3.style.fontWeight = "normal";
            let div4 = document.createElement('div');
            div4.className = "small text-gray-500";
            div4.innerText = correctDate(data[i].createdAt);
            div3.appendChild(div4);
            let span = document.createElement('span');
            span.id = "text-alert";
            span.innerText = data[i].text;
            span.style.fontWeight = data[i].invite.status === "VIEWED" || data[i].invite.status === "DENIED" ? "normal" : "bold";
            div3.appendChild(span);
            a.appendChild(div3);
            container.appendChild(a);
            i++;
        }
        let a2 = document.createElement('a');
        a2.className = "dropdown-item text-center small text-gray-500";
        if (!show)
            a2.href = "/user/alerts";
        else
            a2.href = "/admin/alerts";
        a2.innerText = "Show All Alerts";
        container.appendChild(a2);
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: '2-digit', month: 'short', year: 'numeric'
    }).replace(/ /g, ' ');
}

$("#input-search-admin").keydown(function (event) {
    if (event.keyCode === 13) {
        $("#button-search").click();
        $(this).val('');
    }
});

$('button').on('click', function () {
    let id = $(this).attr("id");
    if (typeof id != "undefined") {
        let idAlert = $(this).attr('data-id');
        let window = "";
        let text = "";
        let url = "";
        if (id === "messageAcceptedModal") {
            let idFrom = $(this).data('id-from');
            let idTo = $(this).data('id-to');
            let show = $(this).data('show');
            window = $(this).data('button');
            text = "Notice read!";
            url = "/user/messages/accepted/" + idAlert;
            if (window === "mail")
                printEmails(idTo, idFrom, idAlert, url, text, show);
        }
        if (id === "actionAccepted" || id === "actionAcceptedModal") {
            text = "Notice read!";
            url = "/user/alerts/accepted/" + idAlert;
        }
        if (id === "actionDeny" || id === "actionDenyModal") {
            text = "Friend request denied!";
            url = "/user/alerts/deny/" + idAlert;
        }
        if (id === "actionConfirm" || id === "actionConfirmModal") {
            text = "Friend request accepted!";
            url = "/user/alerts/confirm/" + idAlert;
        }
        if (id !== "messageAnswerModal") {
            if (id !== "button-search") {
                if (id !== "cancelModal") {
                    if (id !== "send-user-message") {
                        if (window !== "mail") {
                            alertInfo(text);
                            setTimeout(function () {
                                sending(url);
                            }, 1000);
                        }
                    }
                }
            }
        }
    }
});

function printEmails(idFrom, idTo, idMail, url, text, show) {
    if (idMail !== "0") {
        $.get({
            url: url,
            success: (base) => {
                if (base === "OK") {
                    $.get({
                        url: "/user/messages/all-messages/" + idTo,
                        data: {
                            idTo: idFrom
                        },
                        success: (data) => {
                            $.get({
                                url: "/user/messages/all-senders/" + idFrom,
                                success: (senders) => {
                                    $.get({
                                        url: '/user/messages/mails/' + idMail,
                                        success: (mails) => {
                                            printMessages(mails, show);
                                            printAllSenders(senders, data[0].invite.userFrom.fullName, show);
                                            printAllUserMessages(data, show);
                                            alertInfo(text);
                                        },
                                        error: (err) => {
                                            console.log(err);
                                        }
                                    });
                                },
                                error: (err) => {
                                    console.log(err);
                                }
                            });
                        },
                        error: (err) => {
                            console.log(err);
                        }
                    });
                }
            },
            error: (err) => {
                console.log(err);
            }
        });
    } else
        alertInfo(text);
}

function printAllEmails(idFrom, idTo, url, text, show) {
    $.get({
        url: url,
        success: (base) => {
            if (base === "OK") {
                $.get({
                    url: "/user/messages/all-messages/" + idFrom,
                    data: {
                        idTo: idTo
                    },
                    success: (data) => {
                        $.get({
                            url: "/user/messages/all-senders/" + idTo,
                            success: (senders) => {
                                $.get({
                                    url: '/user/messages/all-mails/' + idTo,
                                    success: (mails) => {
                                        printMessages(mails, show);
                                        printAllSenders(senders, data[0].invite.userFrom.fullName, show);
                                        printAllUserMessages(data, show);
                                        alertInfo(text);
                                    },
                                    error: (err) => {
                                        console.log(err);
                                    }
                                });
                            },
                            error: (err) => {
                                console.log(err);
                            }
                        });
                    },
                    error: (err) => {
                        console.log(err);
                    }
                });
            }
        },
        error: (err) => {
            console.log(err);
        }
    });
}

function alertInfo(text) {
    Swal.fire({
        title: text,
        icon: 'info',
        allowOutsideClick: false,
        allowEscapeKey: false,
        allowEnterKey: false,
        showConfirmButton: false,
        showCancelButton: false,
        timer: 2000
    });
}

function sending(url) {
    $.get({
        url: url,
        success: (data) => {
            if (data === "OK")
                location.reload();
        },
        error: (err) => {
            console.log(err);
        }
    });
}

$('#messageAnswerModal').on('click', function () {
    answerData($(this));
});

//вызов модального окна на отправку нового сообщения или ответа на сообщение в Inbox
$('ul').on('click', '.answerMessage', function () {
    answerData($(this));
});

$('.newMessage').on('click', function () {
    answerData($(this));
});

function recoveryOneMessage(id) {
    $.get({
        url: "/user/messages/recovery/" + id,
        success: (data) => {
            if (data === "OK") {
                alertInfo('The message has been recovered!');
                setTimeout(function () {
                    location.reload();
                }, 1000);
            }
        },
        error: (err) => {
            console.log(err);
        }
    });
}

$('.recoveryMessage').on('click', function () {
    let id = $(this).attr('data-id');
    recoveryOneMessage(id);
});

$('#mark-all').on('click', function () {
    let data = $(this);
    let idFrom = data.attr('data-id-from');
    let show = data.attr('data-show');
    let idTo = data.attr('data-id-to');
    let text = "All messages have been read!";
    let url = "/user/messages/accepted-all/" + idTo + "/" + idFrom;
    printAllEmails(idFrom, idTo, url, text, show);
});

$('#action').on('click', function () {
    let data = $(this);
    let href = data.attr('href');
    if (href === "#") {
        let idFrom = data.attr('data-id-from');
        let idTo = data.attr('data-id-to');
        let show = data.attr('data-show');
        let admin = data.attr('data-admin');
        let text = "Message deleted!";
        let url = data.attr('data-url');
        if (typeof admin === "undefined")
            printAllEmails(idFrom, idTo, url, text, show);
        else {
            $.get({
                url: url,
                success: (data) => {
                    if (data === "OK") {
                        alertInfo(text);
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    }
                },
                error: (err) => {
                    console.log(err);
                }
            });
        }
    }
});

function openToModalSend(id, idFrom, idTo, out, name, text, date, show) {
    let answer = "";
    if (text === "false") {
        answer = text;
    } else {
        answer = "[reply to message from " + date + ", text: \"" + (text.substring(0, 80) + " ...") + "\"]";
    }
    let send = $('#send-user-message');
    send.attr("data-id", id);
    send.attr("data-out", out);
    send.attr("data-show", show);
    $('#userIdReq').val(idTo);
    $('#userIdRec').val(idFrom);
    $('#recipient-user-name').val(name);
    $('#userAnswerText').val(answer);
    if (text === "false") {
        $('#sendUserMessageLabel').text('New message to ' + name);
    } else {
        $('#sendUserMessageLabel').text('Answer to ' + name);
    }
    new bootstrap.Modal(document.getElementById("sendUserMessageModal")).show();

}

function getAttributesButton(data) {
    let id = data.getAttribute('data-id');
    let idFrom = data.getAttribute('data-id-from');
    let idTo = data.getAttribute('data-id-to');
    let out = data.getAttribute('data-out');
    let show = data.getAttribute('data-show');
    let name = data.getAttribute('data-name');
    let text = data.getAttribute('data-text');
    let date = data.getAttribute('data-date');
    openToModalSend(id, idFrom, idTo, out, name, text, date, show);
}


function answerData(data) {
    let id = data.attr('data-id');
    let idFrom = data.attr('data-id-from');
    let idTo = data.attr('data-id-to');
    let out = data.attr("data-out");
    let show = data.attr('data-show');
    let name = data.attr('data-name');
    let text = data.attr('data-text');
    let date = data.attr('data-date');
    openToModalSend(id, idFrom, idTo, out, name, text, date, show);
}

$('#send-user-message').on('click', function () {
    let button = $(this);
    let nameButton = button.text();
    if (nameButton !== "Save") {
        let idFrom = $('#userIdReq').val();
        let idTo = $('#userIdRec').val();
        let answer = $('#userAnswerText').val();
        let id = button.attr("data-id");
        let out = button.attr("data-out");
        let show = button.attr("data-show");
        let message = $('#message-user-text').val();
        if (message !== "") {
            $.get({
                url: '/user/messages/send/' + idTo,
                data: {
                    userId: idFrom,
                    text: message,
                    answer: answer
                },
                success: (data) => {
                    if (data === "OK") {
                        let text = "Your message has been sent!";
                        let url = "/user/messages/accepted/" + id;
                        if (typeof out === 'undefined')
                            printEmails(idFrom, idTo, id, url, text, show);
                        else {
                            alertInfo('Your message has been sent!');
                            setTimeout(function () {
                                location.reload();
                            }, 1000);
                        }
                    }
                },
                error: (err) => {
                    console.log(err);
                }
            });
        }
    } else {
        let id = button.attr("data-id");
        let text = $('#message-user-text').val();
        let url = "/admin/messages/edit-message/" + id;
        $.get({
            url: url,
            data: {
                text: text
            },
            success: (data) => {
                if (data === "OK") {
                    alertInfo('Message edited!!');
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
            },
            error: (err) => {
                console.log(err);
            }
        });
    }
});

function openToModalEditMessage(id, name, text) {
    let button = $('#send-user-message');
    button.attr('data-id', id);
    button.text('Save');
    $('#recipient-user-name').val(name);
    $('#sendUserMessageLabel').text('Edit message to ' + name);
    $('#message-user-text').val(text);
    new bootstrap.Modal(document.getElementById("sendUserMessageModal")).show();
}

$('.editMessage').on('click', function () {
    let data = $(this);
    let id = data.attr('data-id');
    let name = data.attr('data-name');
    let text = data.attr('data-text');
    openToModalEditMessage(id, name, text);
})

function searchWorldToString(text, txt) {
    let arrText = text.split(" ");
    return arrText.includes(txt);
}


