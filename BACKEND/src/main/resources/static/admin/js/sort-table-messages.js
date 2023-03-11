$('.sort-table-message').on('click', function (e) {
    let messages = data;
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "UsernameSender":
            img = $('#userSend');
            if (tag === "down") {
                let data = messages.sort((x, y) => x.invite.userFrom.username.localeCompare(y.invite.userFrom.username));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => y.invite.userFrom.username.localeCompare(x.invite.userFrom.username));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "FullNameSender":
            img = $('#fullSend');
            if (tag === "down") {
                let data = messages.sort((x, y) => x.invite.userFrom.fullName.localeCompare(y.invite.userFrom.fullName));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => y.invite.userFrom.fullName.localeCompare(x.invite.userFrom.fullName));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "UsernameRecipient":
            img = $('#userRec');
            if (tag === "down") {
                let data = messages.sort((x, y) => x.invite.userTo.username.localeCompare(y.invite.userTo.username));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => y.invite.userTo.username.localeCompare(x.invite.userTo.username));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Status":
            img = $('#stat');
            if (tag === "down") {
                let data = messages.sort((x, y) => x.status.localeCompare(y.status));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => y.status.localeCompare(x.status));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "DateViewed":
            img = $('#dataV');
            if (tag === "down") {
                let data = messages.sort((x, y) => new Date(x.invite.updatedAt) - new Date(y.invite.updatedAt));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => new Date(y.invite.updatedAt) - new Date(x.invite.updatedAt));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "DateSent":
            img = $('#dataS');
            if (tag === "down") {
                let data = messages.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableMessages(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = messages.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableMessages(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableMessages(data) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            td1.innerText = data[i].invite.userFrom.username;
            let td2 = document.createElement('td');
            td2.innerText = data[i].invite.userFrom.fullName;
            let td3 = document.createElement('td');
            td3.innerText = data[i].invite.userTo.username;
            let td4 = document.createElement('td');
            td4.innerText = data[i].message.length > 70 ? (data[i].message.substring(0, 70) + ' ...') : (data[i].message);
            let td5 = document.createElement('td');
            td5.innerText = data[i].status;
            let td6 = document.createElement('td');
            td6.innerText = data[i].invite.status !== "VIEWED" ? "NOT_VIEWED" : correctDate(data[i].invite.updatedAt);
            let td7 = document.createElement('td');
            td7.innerText = correctDate(data[i].createdAt);
            let td8 = document.createElement('td');
            let a1 = document.createElement('a');
            a1.href = "#";
            a1.title = "View Message";
            a1.dataset.id = data[i].id;
            a1.dataset.class = "viewMessage";
            a1.dataset.button = "mail";
            a1.dataset.idFrom = data[i].invite.userFrom.id;
            a1.dataset.idTo = data[i].invite.userTo.id;
            a1.dataset.name = data[i].invite.userFrom.fullName;
            a1.dataset.text = data[i].message;
            a1.dataset.date = correctDate(data[i].createdAt);
            a1.dataset.toggle = "modal";
            a1.dataset.target = "#MessageModal";
            let img1 = document.createElement('img');
            img1.src = "/img/icon/read.png";
            img1.width = 30;
            a1.appendChild(img1);
            td8.appendChild(a1);
            let a2 = document.createElement('a');
            a2.href = "#";
            a2.title = "Edit Message";
            a2.addEventListener('click', () => {
                let id = data[i].id;
                let name = data[i].invite.userTo.fullName;
                let text = data[i].message;
                openToModalEditMessage(id, name, text);
            })
            a2.style.marginLeft = "4px";
            a2.dataset.id = data[i].id;
            a2.dataset.name = data[i].invite.userTo.fullName;
            a2.dataset.text = data[i].message;
            let img2 = document.createElement('img');
            img2.src = "/img/icon/edit.png";
            img2.width = 30;
            a2.appendChild(img2);
            td8.appendChild(a2);
            let a3 = document.createElement('a');
            a3.hidden = data[i].status !== "DELETED";
            a3.href = "#";
            a3.style.marginLeft = "4px";
            a3.title = "Recovery Message";
            a3.addEventListener('click', (event) => {
                recoveryOneMessage(event.currentTarget.getAttribute('data-id'));
            })
            a3.dataset.id = data[i].id;
            let img3 = document.createElement('img');
            img3.src = "/img/icon/restore.png";
            img3.width = 30;
            a3.appendChild(img3);
            td8.appendChild(a3);
            let a4 = document.createElement('a');
            a4.hidden = data[i].status === "DELETED";
            a4.href = "#";
            a4.style.marginLeft = "4px";
            a4.title = "Delete Message";
            a4.dataset.id = data[i].id;
            a4.dataset.title = "deleteMessage";
            a4.dataset.idFrom = data[i].invite.userFrom.id;
            a4.dataset.idTo = data[i].invite.userTo.id;
            a4.dataset.admin = "admin";
            a4.dataset.toggle = "modal";
            a4.dataset.target = "#DeleteModal";
            let img4 = document.createElement('img');
            img4.src = "/img/icon/delete-message.png";
            img4.width = 30;
            a4.appendChild(img4);
            td8.appendChild(a4);
            tr.append(td1, td2, td3, td4, td5, td6, td7, td8);
            container.append(tr);
        }
    }
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    if (text !== "") {
        input.val('');
        searchMessage(text);
    }
})

function searchMessage(text) {
    let messages = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < messages.length; i++) {
            if (messages[i].invite.userFrom.username.toLowerCase().includes(txt.toLowerCase()) ||
                messages[i].invite.userFrom.fullName.toLowerCase().includes(txt.toLowerCase()) ||
                messages[i].invite.userTo.username.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(messages[i]);
                else {
                    if (result.filter(x => x.id === messages[i].id).length === 0)
                        result.push(messages[i]);
                }
            } else {
                if (messages[i].message.toLowerCase().includes(txt.toLowerCase()) ||
                    messages[i].status.toLowerCase().includes(txt.toLowerCase())) {
                    if (result.length === 0)
                        result.push(messages[i]);
                    else {
                        if (result.filter(x => x.id === messages[i].id).length === 0)
                            result.push(messages[i]);
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableMessages(result);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}
