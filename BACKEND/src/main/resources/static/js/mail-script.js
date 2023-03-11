$('ul').on('click', '.open-all-messages', function () {
    let idFrom = $(this).data('id-from');
    let idTo = $(this).data('id-to');
    if (typeof idFrom != "undefined" && typeof idTo != "undefined") {
        $.get({
            url: "/user/messages/all-messages/" + idFrom,
            data: {
                idTo: idTo
            },
            success: (data) => {
                $.get({
                    url: "/user/messages/all-senders/" + idTo,
                    success: (senders) => {
                        printAllSenders(senders, data[0].invite.userFrom.fullName);
                        printAllUserMessages(data);
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
});

function printAllSenders(map, user) {
    if (map != null) {
        let container = document.getElementById('container-senders');
        container.innerHTML = "";
        for (let elem of map) {
            let data = elem[0];
            let i = 0;
            while (i < 1) {
                let li = document.createElement('li');
                li.className = "list-group-item d-flex justify-content-between align-items-center";
                li.style.backgroundColor = data[i].invite.userFrom.fullName === user ? 'lightgray' : '';
                let div1 = document.createElement('div');
                div1.className = "d-flex align-items-center";
                let a1 = document.createElement('a');
                a1.title = "Open all messages";
                a1.href = "#";
                a1.className = "open-all-messages";
                a1.dataset.idFrom = data[i].invite.userFrom.id;
                a1.dataset.idTo = data[i].invite.userTo.id;
                let img1 = document.createElement('img');
                img1.className = "rounded-circle px-2";
                img1.src = "/" + data[i].invite.userFrom.avatarUrl;
                img1.alt = "avatar";
                img1.width = 80;
                a1.appendChild(img1);
                div1.appendChild(a1);
                let div2 = document.createElement('div');
                div2.className = "ms-3";
                let div3 = document.createElement('div');
                let div4 = document.createElement('div');
                div4.className = "row";
                let div5 = document.createElement('div');
                div5.className = "col-10";
                let p1 = document.createElement('p');
                p1.className = "mb-1";
                p1.style.textDecorationLine = "underline";
                p1.style.color = "#293c74";
                p1.style.fontWeight = "bold";
                p1.innerText = data[i].invite.userFrom.fullName;
                div5.appendChild(p1);
                div4.appendChild(div5);
                let div6 = document.createElement('div');
                div6.className = "col-2";
                let a2 = document.createElement('a');
                a2.title = "Send Message";
                a2.href = "#";
                a2.className = "answerMessage";
                a2.dataset.id = data[i].id;
                a2.dataset.idFrom = data[i].invite.userFrom.id;
                a2.dataset.idTo = data[i].invite.userTo.id;
                a2.dataset.name = data[i].invite.userFrom.fullName;
                a2.dataset.text = "false";
                a2.dataset.date = correctDate(data[i].createdAt);
                let img2 = document.createElement('img');
                img2.alt = "Mail";
                img2.width = 25;
                img2.src = "/img/icon/write-mail.png";
                a2.appendChild(img2);
                div6.appendChild(a2);
                div4.appendChild(div6);
                div3.appendChild(div4);
                let div7 = document.createElement('div');
                let p2 = document.createElement('p');
                p2.className = "text-dark mb-0";
                p2.innerText = data[i].invite.userFrom.email;
                div7.appendChild(p2);
                div3.appendChild(div7);
                div2.appendChild(div3);
                div1.appendChild(div2);
                li.appendChild(div1);
                let span1 = document.createElement('span');
                span1.className = "badge rounded-pill badge-primary";
                span1.innerText = elem[0].length > 1 ? ((elem[0].filter(x => x.invite.status !== "VIEWED")
                    .length)===0?"":(elem[0].filter(x => x.invite.status !== "VIEWED")
                    .length)) : (data[0].invite.status !== "VIEWED" ? "1" : "");
                li.appendChild(span1);
                container.appendChild(li);
                i++;
            }
        }
    }
}

function printAllUserMessages(data) {
    if (data != null) {
        document.getElementById('name-user-from').innerText = data[0].invite.userFrom.fullName;
        let container = document.getElementById('container-messages');
        container.innerHTML = "";
        for (let i = 0; i < data.length; i++) {
            let li = document.createElement('li');
            li.className = "list-group-item";
            li.style.fontWeight = data[i].invite.status === "VIEWED" ? "normal" : "bold";
            let div1 = document.createElement('div');
            div1.className = "row";
            let div2 = document.createElement('div');
            div2.className = "col-11";
            let div3 = document.createElement('div');
            div3.className = "col text-right";
            let span1 = document.createElement('span');
            span1.className = "badge rounded-pill " + ((data[i].invite.status === "NOT_VIEWED" ||
                data[i].invite.status === "REQUEST") ? "badge-warning" : "badge-success");
            span1.innerText = data[i].invite.status === "REQUEST" ? "NOT_VIEWED" : data[i].invite.status;
            div3.appendChild(span1);
            div2.appendChild(div3);
            let p1 = document.createElement('p');
            p1.className = "mb-2";
            p1.style.color = "#6a1a21";
            p1.style.textDecorationLine = "underline";
            p1.innerText = correctDate(data[i].createdAt);
            div2.appendChild(p1);
            let p2 = document.createElement('p');
            p2.className = "mb-0 text-dark";
            p2.innerText = data[i].message.length > 200 ? (data[i].message.substring(0, 200) + " ...") : data[i].message;
            div2.appendChild(p2);
            div1.appendChild(div2);
            let div4 = document.createElement('div');
            div4.className = "col-1 align-self-center";
            let div5 = document.createElement('div');
            div5.title = "Read";
            let a1 = document.createElement('a');
            a1.href = "#";
            a1.id = "readMessage";
            a1.dataset.target = "#MessageModal";
            a1.dataset.toggle = "modal";
            a1.dataset.dies = data[i].invite.status === "VIEWED";
            a1.dataset.id = data[i].id;
            a1.dataset.idFrom = data[i].invite.userFrom.id;
            a1.dataset.idTo = data[i].invite.userTo.id;
            a1.dataset.button = "mail";
            a1.dataset.name = data[i].invite.userFrom.fullName;
            a1.dataset.text = data[i].message;
            a1.dataset.date = correctDate(data[i].createdAt);
            let img1 = document.createElement('img');
            img1.src = "/img/icon/read.png";
            img1.width = 30;
            a1.appendChild(img1);
            div5.appendChild(a1);
            div4.appendChild(div5);
            let div6 = document.createElement('div');
            div6.style.paddingTop = "10px";
            div6.title = "Answer";
            let a2 = document.createElement('a');
            a2.href = "#";
            a2.className = "answerMessage";
            a2.dataset.id = data[i].id;
            a2.dataset.idFrom = data[i].invite.userFrom.id;
            a2.dataset.idTo = data[i].invite.userTo.id;
            a2.dataset.name = data[i].invite.userFrom.fullName;
            a2.dataset.text = data[i].message;
            a2.dataset.date = correctDate(data[i].createdAt);
            let img2 = document.createElement('img');
            img2.src = "/img/icon/answer.png";
            img2.width = 30;
            a2.appendChild(img2);
            div6.appendChild(a2);
            div4.appendChild(div6);
            let div7 = document.createElement('div');
            div7.style.paddingTop = "10px";
            div7.title = "Delete";
            let a3 = document.createElement('a');
            a3.href = "#";
            a3.dataset.title = "deleteMessage";
            a3.dataset.id = data[i].id;
            a3.dataset.idFrom = data[i].invite.userFrom.id;
            a3.dataset.idTo = data[i].invite.userTo.id;
            a3.dataset.toggle = "modal";
            a3.dataset.target = "#DeleteModal";
            let img3 = document.createElement('img');
            img3.src = "/img/icon/delete-message.png";
            img3.width = 30;
            a3.appendChild(img3);
            div7.appendChild(a3);
            div4.appendChild(div7);
            div1.appendChild(div4);
            li.appendChild(div1);
            container.appendChild(li);
        }
        let markAll = document.getElementById('mark-all');
        markAll.dataset.idFrom = data[0].invite.userFrom.id;
        markAll.dataset.idTo = data[0].invite.userTo.id;
    }
}

