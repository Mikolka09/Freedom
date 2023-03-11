$('.sort-table-mail').on('click', function (e) {
    let mails;
    let column = $(this);
    let list = $("#table-list").attr("data-table");
    if (list === "out-list")
        mails = dataOut;
    else
        mails = dataDelete;
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Username":
            img = $('#user');
            if (tag === "down") {
                let data = mails.sort((x, y) => list === "out-list" ? x.invite.userTo.username.localeCompare(y.invite.userTo.username) :
                    x.invite.userFrom.username.localeCompare(y.invite.userFrom.username));
                printTableMails(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = mails.sort((x, y) => list === "out-list" ? y.invite.userTo.username.localeCompare(x.invite.userTo.username) :
                    y.invite.userFrom.username.localeCompare(x.invite.userFrom.username));
                printTableMails(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "FullName":
            img = $('#full');
            if (tag === "down") {
                let data = mails.sort((x, y) => list === "out-list" ? x.invite.userTo.fullName.localeCompare(y.invite.userTo.fullName) :
                    x.invite.userFrom.fullName.localeCompare(y.invite.userFrom.fullName));
                printTableMails(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = mails.sort((x, y) => list === "out-list" ? y.invite.userTo.fullName.localeCompare(x.invite.userTo.fullName) :
                    y.invite.userFrom.fullName.localeCompare(x.invite.userFrom.fullName));
                printTableMails(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;

        case "Date Viewed":
            img = $('#view');
            if (tag === "down") {
                let data = mails.sort((x, y) => new Date(x.invite.updatedAt) - new Date(y.invite.updatedAt));
                printTableMails(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = mails.sort((x, y) => new Date(y.invite.updatedAt) - new Date(x.invite.updatedAt));
                printTableMails(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Date Sent":
            img = $('#sent');
            if (tag === "down") {
                let data = mails.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableMails(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = mails.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableMails(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableMails(data, list) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.style.borderRadius = "50%";
            img1.style.paddingBottom = "5px";
            img1.width = 50;
            img1.alt = "avatar";
            let td2 = document.createElement('td');
            let td3 = document.createElement('td');
            if (list === "out-list") {
                img1.src = "/" + data[i].invite.userTo.avatarUrl;
                td2.innerText = data[i].invite.userTo.username;
                td3.innerText = data[i].invite.userTo.fullName === null ? 'NULL' : data[i].invite.userTo.fullName;
            } else {
                img1.src = "/" + data[i].invite.userFrom.avatarUrl;
                td2.innerText = data[i].invite.userFrom.username;
                td3.innerText = data[i].invite.userFrom.fullName === null ? 'NULL' : data[i].invite.userFrom.fullName;
            }
            td1.appendChild(img1);
            let td4 = document.createElement('td');
            td4.innerText = data[i].message.length > 70 ? (data[i].message.substring(0, 70) + ' ...') : (data[i].message);
            let td5 = document.createElement('td');
            td5.innerText = data[i].status;
            let td6 = document.createElement('td');
            td6.innerText = data[i].invite.status !== "VIEWED" ? "NOT_VIEWED" : correctDate(data[i].invite.updatedAt);
            let td7 = document.createElement('td');
            td7.innerText = correctDate(data[i].createdAt);
            let td8 = document.createElement('td');
            let a2 = document.createElement('a');
            a2.href = "#";
            a2.title = "View Message";
            a2.dataset.id = data[i].id;
            a2.dataset.class = "viewMessage";
            a2.dataset.dies = data[i].invite.status === "VIEWED";
            a2.dataset.button = "mail";
            a2.dataset.idFrom = data[i].invite.userFrom.id;
            a2.dataset.idTo = data[i].invite.userTo.id;
            a2.dataset.name = data[i].invite.userFrom.fullName;
            a2.dataset.text = data[i].message;
            a2.dataset.date = correctDate(data[i].createdAt);
            a2.dataset.toggle = "modal";
            a2.dataset.target = "#MessageModal";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/read.png";
            img2.width = 30;
            a2.appendChild(img2);
            td8.appendChild(a2);
            let a3 = document.createElement('a');
            let img3 = document.createElement('img');
            if (list === "out-list") {
                a3.href = "#";
                a3.style.marginLeft = "4px";
                a3.title = "Send Message";
                a3.className="newMessage";
                a3.dataset.id = "0";
                a3.dataset.out = "true";
                a3.dataset.idTo = (user === null) ? admin.id : user.id;
                a3.dataset.name = data[i].invite.userTo.fullName;
                a3.dataset.idFrom = data[i].invite.userTo.id;
                a3.dataset.text = "false";
                a3.dataset.date = correctDate(data[i].createdAt)
                img3.src = "/img/icon/send.png";
                img3.width = 30;

            } else {
                a3.style.marginLeft = "4px";
                a3.href = "#";
                a3.className = "recoveryMessage";
                a3.title = "Recovery Message";
                a3.dataset.id = data[i].id;
                img3.src = "/img/icon/restore.png";
                img3.width = 30;
            }
            a3.appendChild(img3);
            td8.appendChild(a3);

            tr.append(td1, td2, td3, td4, td5, td6, td7, td8);
            container.append(tr);
        }
    }
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    let list = $("#table-list").attr("data-table");
    if (text !== "") {
        input.val('');
        searchMails(text, list);
    }
})

$('.newMessageMail').on('click', function (){
    $('.newMessage').click();
})

function searchMails(text, list) {
    let mails;
    if (list === "out-list")
        mails = dataOut;
    else
        mails = dataDelete;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < mails.length; i++) {
            if (mails[i].invite.userTo.username.toLowerCase().includes(txt.toLowerCase()) ||
                mails[i].invite.userTo.fullName.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(mails[i]);
                else {
                    if (result.filter(x => x.id === mails[i].id).length === 0)
                        result.push(mails[i]);
                }
            } else {
                if (mails[i].invite.userFrom.username.toLowerCase().includes(txt.toLowerCase()) ||
                    mails[i].invite.userFrom.fullName.toLowerCase().includes(txt.toLowerCase())) {
                    if (result.length === 0)
                        result.push(mails[i]);
                    else {
                        if (result.filter(x => x.id === mails[i].id).length === 0)
                            result.push(mails[i]);
                    }
                } else {
                    if (mails[i].message.toLowerCase().includes(txt.toLowerCase())) {
                        if (result.length === 0)
                            result.push(mails[i]);
                        else {
                            if (result.filter(x => x.id === mails[i].id).length === 0)
                                result.push(mails[i]);
                        }
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableMails(result, list);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}