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
        alert.split(' ')[2] === 'read')
        modal.find('#footer-confirm').hide();
    else
        modal.find('#footer-accepted').hide();
    changeStatusAlert(id);
})

$('#MessageModal').on('show.bs.modal', function (event) {
    let button = $(event.relatedTarget);
    let id = button.data('id');
    let idFrom = button.data('id-from');
    let idTo = button.data('id-to');
    let name = button.data('name');
    let alert = button.data('text');
    let date = button.data('date');
    let dies = button.data('dies');
    let modal = $(this);
    let accept = $('#messageAcceptedModal');
    if (typeof dies !== "undefined")
        if (dies === true)
            accept.hide();
    let answer = $('#messageAnswerModal');
    accept.attr('data-id', id);
    answer.attr('data-id', id);
    answer.attr('data-id-from', idFrom);
    answer.attr('data-id-to', idTo);
    answer.attr('data-name', name);
    modal.find('#messageModalLabel').text(name);
    modal.find('#text-mess').text(alert);
    modal.find('#date-message').text(date);
    changeStatusMessage(id);
})

function changeStatusMessage(id) {
    $.get({
        url: '/user/messages/change/' + id,
        success: (data) => {
            printMessages(data);
        },
        error: (err) => {
            console.log(err);
        }
    });
}

function printMessages(data) {
    if (data != null) {
        let container = document.getElementById('list-messages');
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
            a.dataset.name = data[i].invite.userFrom.fullName;
            a.dataset.text = data[i].message;
            a.dataset.target = "#MessageModal";
            a.dataset.toggle = "modal";
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
        a2.href = "/user/messages";
        a2.innerText = "Read More Messages";
        container.appendChild(a2);
    }
}

function changeStatusAlert(id) {
    $.get({
        url: '/user/alerts/change/' + id,
        success: (data) => {
            printAlerts(data);
        },
        error: (err) => {
            console.log("Error: " + err);
        }
    });
}

function printAlerts(data) {
    if (data != null) {
        let container = document.getElementById('list-alerts');
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
        a2.href = "/user/alerts";
        a2.innerText = "Show All Alerts";
        container.appendChild(a2);
    }
}

function correctDate(date) {
    let data = new Date(date.toString());
    return data.toLocaleDateString('en-GB', {
        day: 'numeric', month: 'long', year: 'numeric'
    }).replace(/ /g, ' ');
}

$('button').on('click', function () {
    let id = $(this).attr("id");
    if (typeof id != "undefined") {
        let idAlert = $(this).attr('data-id');
        let text = "";
        let url = "";
        if (id === "messageAcceptedModal") {
            text = "Notice read!";
            url = "/user/messages/accepted/" + idAlert;
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
        if (id !== "messageAnswerModal" || id === "send-user-message") {
            alertInfo(text);
            setTimeout(function () {
                sending(url);
            }, 1000);
        }
    }
});

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
    let id = $(this).attr('data-id');
    let idFrom = $(this).attr('data-id-from');
    let idTo = $(this).attr('data-id-to');
    let name = $(this).attr('data-name');
    $('#send-user-message').attr("data-id", id);
    $('#userIdReq').val(idTo);
    $('#userIdRec').val(idFrom);
    $('#recipient-user-name').val(name);
    $('#sendUserMessageLabel').text('New message to ' + name);
    new bootstrap.Modal(document.getElementById("sendUserMessageModal")).show();
});

$('#answerMessage').on('click', function () {
    let id = $(this).attr('data-id');
    let idFrom = $(this).attr('data-id-from');
    let idTo = $(this).attr('data-id-to');
    let name = $(this).attr('data-name');
    $('#send-user-message').attr("data-id", id);
    $('#userIdReq').val(idTo);
    $('#userIdRec').val(idFrom);
    $('#recipient-user-name').val(name);
    $('#sendUserMessageLabel').text('New message to ' + name);
    new bootstrap.Modal(document.getElementById("sendUserMessageModal")).show();
});

$('#send-user-message').on('click', function () {
    let idFrom = $('#userIdReq').val();
    let idTo = $('#userIdRec').val();
    let id = $(this).attr("data-id");
    let message = $('#message-user-text').val();
    if (message !== "") {
        $.get({
            url: '/user/messages/send/' + idTo,
            data: {
                userId: idFrom,
                text: message
            },
            success: (data) => {
                if (data === "OK") {
                    let text = "Your message has been sent!";
                    let url = "/user/messages/accepted/" + id;
                    alertInfo(text);
                    setTimeout(function () {
                        sending(url);
                    }, 1000);
                }
            },
            error: (err) => {
                console.log(err);
            }
        });
    }
});




