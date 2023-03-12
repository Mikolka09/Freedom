$('.sort-table-comment').on('click', function (e) {
    let comments = data;
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Username":
            img = $('#user');
            if (tag === "down") {
                let data = comments.sort((x, y) => x.post.user.username.localeCompare(y.post.user.username));
                printTableComments(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = comments.sort((x, y) => y.post.user.username.localeCompare(x.post.user.username));
                printTableComments(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Category":
            img = $('#cat');
            if (tag === "down") {
                let data = comments.sort((x, y) => x.post.category.name.localeCompare(y.post.category.name));
                printTableComments(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = comments.sort((x, y) => y.post.category.name.localeCompare(x.post.category.name));
                printTableComments(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Status":
            img = $('#stat');
            if (tag === "down") {
                let data = comments.sort((x, y) => x.status.localeCompare(y.status));
                printTableComments(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = comments.sort((x, y) => y.status.localeCompare(x.status));
                printTableComments(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "CreatedAt":
            img = $('#crt');
            if (tag === "down") {
                let data = comments.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableComments(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = comments.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableComments(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "UpdatedAt":
            img = $('#urt');
            if (tag === "down") {
                let data = comments.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTableComments(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = comments.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTableComments(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableComments(data) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = "/" + data[i].post.user.avatarUrl;
            img1.style.borderRadius = "50%";
            img1.style.paddingBottom = "5px";
            img1.width = 50;
            img1.alt = "avatar";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            td2.innerText = data[i].post.user.username;
            let td3 = document.createElement('td');
            td3.innerText = data[i].post.title;
            let td4 = document.createElement('td');
            td4.innerText = data[i].body;
            let td5 = document.createElement('td');
            td5.innerText = data[i].post.category.status !== "DELETED" ? data[i].post.category.name : "NULL";
            let td6 = document.createElement('td');
            td6.innerText = data[i].status;
            let td7 = document.createElement('td');
            td7.innerText = correctDate(data[i].createdAt);
            let td8 = document.createElement('td');
            td8.innerText = correctDate(data[i].updatedAt);
            let td9 = document.createElement('td');
            let a1 = document.createElement('a');
            a1.href = "#";
            a1.title = "Edit";
            a1.style.marginRight = "4px";
            a1.dataset.id = data[i].id;
            a1.dataset.username = data[i].post.user.username;
            a1.dataset.body = data[i].body;
            a1.dataset.titlePost = data[i].post.title;
            a1.dataset.category = data[i].post.category.name;
            a1.dataset.toggle = "modal";
            a1.dataset.target = "#EditCommentModal";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/edit.png";
            img2.width = 30;
            a1.appendChild(img2);
            td9.appendChild(a1);
            let a2 = document.createElement('a');
            a2.hidden = data[i].status !== "DELETED";
            a2.href = "#";
            a2.title = "Recovery";
            a2.dataset.id = data[i].id;
            a2.dataset.username = data[i].post.user.username;
            a2.dataset.body = data[i].body;
            a2.dataset.titlePost = data[i].post.title;
            a2.dataset.category = data[i].post.category.name;
            a2.dataset.toggle = "modal";
            a2.dataset.target = "#RecoveryCommentModal";
            let img3 = document.createElement('img');
            img3.src = "/img/icon/restore.png";
            img3.width = 30;
            a2.appendChild(img3);
            td9.appendChild(a2);
            let a3 = document.createElement('a');
            a3.hidden = data[i].status === "DELETED";
            a3.href = "#";
            a3.title = "Delete";
            a3.dataset.id = data[i].id;
            a3.dataset.name = data[i].body;
            a3.dataset.toggle = "modal";
            a3.dataset.target = "#DeleteModal";
            let img4 = document.createElement('img');
            img4.src = "/img/icon/delete.png";
            img4.width = 30;
            a3.appendChild(img4);
            td9.appendChild(a3);

            tr.append(td1, td2, td3, td4, td5, td6, td7, td8, td9);
            container.append(tr);
        }
    }
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    if (text !== "") {
        input.val('');
        searchComment(text);
    }
})

function searchComment(text) {
    let comments = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < comments.length; i++) {
            if (comments[i].post.user.username.toLowerCase().includes(txt.toLowerCase()) ||
               searchWorldToString(comments[i].post.title.toLowerCase(), txt.toLowerCase()) ||
                comments[i].status.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(comments[i]);
                else {
                    if (result.filter(x => x.id === comments[i].id).length === 0)
                        result.push(comments[i]);
                }
            } else {
                if (searchWorldToString(comments[i].body.toLowerCase(), txt.toLowerCase()) ||
                    comments[i].post.category.name.toLowerCase().includes(txt.toLowerCase())) {
                    if (result.length === 0)
                        result.push(comments[i]);
                    else {
                        if (result.filter(x => x.id === comments[i].id).length === 0)
                            result.push(comments[i]);
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableComments(result);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}
