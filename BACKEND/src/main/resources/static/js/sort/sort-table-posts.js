$('.sort-table-post').on('click', function (e) {
    let posts = data;
    let column = $(this);
    let img;
    let list = $("#table-list").attr("data-table");
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Username":
            img = $('#user');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.user.username.localeCompare(y.user.username));
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.user.username.localeCompare(x.user.username));
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Category":
            img = $('#cat');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.category.name.localeCompare(y.category.name));
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.category.name.localeCompare(x.category.name));
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Likes":
            img = $('#like');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.likes - y.likes);
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.likes - x.likes);
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Comments":
            img = $('#com');
            if (tag === "down") {
                let data = posts.sort((x, y) => x.comments.length - y.comments.length);
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => y.comments.length - x.comments.length);
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "CreatedAt":
            img = $('#crt');
            if (tag === "down") {
                let data = posts.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "UpdatedAt":
            img = $('#urt');
            if (tag === "down") {
                let data = posts.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTablePosts(data, list);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = posts.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTablePosts(data, list);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTablePosts(data, list) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = "/" + data[i].imgUrl;
            img1.width = 100;
            img1.alt = "image";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            if (list === "posts-friend")
                td2.innerText = data[i].user.username;
            let td3 = document.createElement('td');
            td3.innerText = data[i].title;
            let td4 = document.createElement('td');
            td4.innerText = data[i].shortDescription;
            let td5 = document.createElement('td');
            td5.innerText = data[i].category.status !== "DELETED" ? data[i].category.name : "NULL";
            let td6 = document.createElement('td');
            let select1 = document.createElement('select');
            select1.className = "form-control";
            select1.name = "tag_id";
            select1.id = "tag_id";
            let tags = data[i].tags;
            for (let j = 0; j < tags.length; j++) {
                let opt = document.createElement("option");
                opt.value = tags[j].id;
                opt.text = tags[j].status !== "DELETED" ? tags[j].name : "NULL";
                select1.append(opt);
            }
            td6.append(select1);
            let td7 = document.createElement('td');
            td7.innerText = data[i].likes === null ? 0 : data[i].likes;
            let td8 = document.createElement('td');
            td8.innerText = data[i].comments.length;
            let td9 = document.createElement('td');
            if (list !== "posts-friend")
                td9.innerText = data[i].status;
            let td10 = document.createElement('td');
            td10.innerText = correctDate(data[i].createdAt);
            let td11 = document.createElement('td');
            td11.innerText = correctDate(data[i].updatedAt);
            let td12 = document.createElement('td');
            let a2 = document.createElement('a');
            if (list !== "posts-friend") {
                a2.href = "/user/posts/edit/" + data[i].id;
                a2.title = "Edit";
                let img2 = document.createElement('img');
                img2.src = "/img/icon/edit.png";
                img2.width = 30;
                a2.appendChild(img2);
                td12.appendChild(a2);
                let a3 = document.createElement('a');
                a3.href = "/user/posts/preview/" + data[i].id;
                a3.style.marginLeft = "3px";
                a3.title = "Preview";
                let img3 = document.createElement('img');
                img3.src = "/img/icon/preview.png";
                img3.width = 30;
                a3.appendChild(img3);
                td12.appendChild(a3);
                let a4 = document.createElement('a');
                a4.href = "#";
                a4.style.marginLeft = "4px";
                a4.title = "Delete";
                a4.dataset.id = data[i].id;
                a4.dataset.name = data[i].title;
                a4.dataset.toggle = "modal";
                a4.dataset.target = "#DeleteModal";
                let img4 = document.createElement('img');
                img4.src = "/img/icon/delete.png";
                img4.width = 30;
                a4.appendChild(img4);
                td12.appendChild(a4);
            } else {
                a2.href = "/view-post/" + data[i].id;
                a2.title = "Go to Post";
                let img2 = document.createElement('img');
                img2.src = "/img/icon/hyperlink.png";
                img2.width = 30;
                a2.appendChild(img2);
                td12.appendChild(a2);
            }
            if (list === "posts-friend")
                tr.append(td1, td2, td3, td4, td5, td6, td7, td8, td10, td11, td12);
            else
                tr.append(td1, td3, td4, td5, td6, td7, td8, td9, td10, td11, td12);
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
        searchPosts(text, list);
    }
})

function searchPosts(text, list) {
    let posts = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < posts.length; i++) {
            if (posts[i].user.username.toLowerCase().includes(txt.toLowerCase()) ||
                posts[i].user.fullName.toLowerCase().includes(txt.toLowerCase()) ||
                posts[i].likes.toString().toLowerCase() === txt.toLowerCase() ||
                searchPostForTags(posts[i], txt)) {
                if (result.length === 0)
                    result.push(posts[i]);
                else {
                    if (result.filter(x => x.id === posts[i].id).length === 0)
                        result.push(posts[i]);
                }
            } else {
                if (searchWorldToString(posts[i].title.toLowerCase(), txt.toLowerCase()) ||
                    searchWorldToString(posts[i].shortDescription.toLowerCase(), txt.toLowerCase()) ||
                    posts[i].category.name.toLowerCase() === txt.toLowerCase() ||
                    posts[i].comments.length.toString() === txt.toLowerCase()) {
                    if (result.length === 0)
                        result.push(posts[i]);
                    else {
                        if (result.filter(x => x.id === posts[i].id).length === 0)
                            result.push(posts[i]);
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTablePosts(result, list);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}

function searchPostForTags(posts, txt) {
    for (let i = 0; i < posts.length; i++) {
        let tags = posts[i].tags;
        for (let tag of tags) {
            if (tag.name.toLowerCase() === txt.toLowerCase()) {
                return true;
            }
        }
    }
    return false
}