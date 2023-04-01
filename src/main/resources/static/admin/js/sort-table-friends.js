$('.sort-table-friend').on('click', function (e) {
    let friends = data;
    let column = $(this);
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name) {
        case "Username":
            img = $('#user');
            if (tag === "down") {
                let data = friends.sort((x, y) => x.friendReceiver.username.localeCompare(y.friendReceiver.username));
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => y.friendReceiver.username.localeCompare(x.friendReceiver.username));
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "FullName":
            img = $('#full');
            if (tag === "down") {
                let data = friends.sort((x, y) => x.friendReceiver.fullName.localeCompare(y.friendReceiver.fullName));
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => y.friendReceiver.fullName.localeCompare(x.friendReceiver.fullName));
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Age":
            img = $('#age');
            if (tag === "down") {
                let data = friends.sort((x, y) => x.friendReceiver.age - y.friendReceiver.age);
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => y.friendReceiver.age - x.friendReceiver.age);
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Email":
            img = $('#eml');
            if (tag === "down") {
                let data = friends.sort((x, y) => x.friendReceiver.email.localeCompare(y.friendReceiver.email));
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => y.friendReceiver.email.localeCompare(x.friendReceiver.email));
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Rating":
            img = $('#rat');
            if (tag === "down") {
                let data = friends.sort((x, y) => x.friendReceiver.rating - y.friendReceiver.rating);
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => y.friendReceiver.rating - x.friendReceiver.rating);
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Friendship Days":
            img = $('#days');
            if (tag === "down") {
                let data = friends.sort((x, y) => termingDate(x.createdAt) - termingDate(y.createdAt));
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => termingDate(y.createdAt) - termingDate(x.createdAt));
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        case "Friendship Start Date":
            img = $('#date');
            if (tag === "down") {
                let data = friends.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableFriends(data);
                column.attr("data-tag", "up");
                img.attr("src", "/img/icon/up-arrow.png");
            } else {
                let data = friends.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableFriends(data);
                column.attr("data-tag", "down");
                img.attr("src", "/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableFriends(data) {
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if (data != null) {
        for (let i = 0; i < data.length; i++) {
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = data[i].friendReceiver.avatarUrl;
            img1.style.borderRadius = "50%";
            img1.style.paddingBottom = "5px";
            img1.width = 50;
            img1.alt = "avatar";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            td2.innerText = data[i].friendReceiver.username;
            let td3 = document.createElement('td');
            td3.innerText = data[i].friendReceiver.fullName == null ? "NULL" : data[i].friendReceiver.fullName;
            let td4 = document.createElement('td');
            td4.innerText = data[i].friendReceiver.age;
            let td5 = document.createElement('td');
            td5.innerText = data[i].friendReceiver.email;
            let td6 = document.createElement('td');
            td6.innerText = data[i].friendReceiver.status;
            let td7 = document.createElement('td');
            td7.innerText = data[i].friendReceiver.rating;
            let td8 = document.createElement('td');
            td8.innerText = termingDate(data[i].createdAt);
            let td9 = document.createElement('td');
            td9.innerText = correctDate(data[i].createdAt);
            let td10 = document.createElement('td');
            let a2 = document.createElement('a');
            a2.href = "/admin/friend/view/" + data[i].friendReceiver.id;
            a2.title = "View";
            let img2 = document.createElement('img');
            img2.src = "/img/icon/view.png";
            img2.width = 30;
            a2.appendChild(img2);
            td10.appendChild(a2);
            let a3 = document.createElement('a');
            a3.href = "#";
            a3.style.marginLeft = "4px";
            a3.title = "Send Message";
            a3.addEventListener('click', (event) => {
                getAttributesButton(event.currentTarget);
            })
            a3.dataset.id = "0";
            a3.dataset.idTo = admin.id;
            a3.dataset.name = data[i].friendReceiver.fullName;
            a3.dataset.idFrom = data[i].friendReceiver.id;
            a3.dataset.text = "false";
            a3.dataset.date = correctDate(data[i].createdAt)
            let img3 = document.createElement('img');
            img3.src = "/img/icon/send.png";
            img3.width = 30;
            a3.appendChild(img3);
            td10.appendChild(a3);
            let a4 = document.createElement('a');
            a4.href = "#";
            a4.style.marginLeft = "3px";
            a4.title = "Break Friendship";
            a4.dataset.idFriend = data[i].friendReceiver.id;
            a4.dataset.id = admin.id;
            a4.dataset.name = data[i].friendReceiver.username;
            a4.dataset.toggle = "modal";
            a4.dataset.target = "#DeleteModal";
            let img4 = document.createElement('img');
            img4.src = "/img/icon/delete-friend.png";
            img4.width = 30;
            a4.appendChild(img4);
            td10.appendChild(a4);

            tr.append(td1, td2, td3, td4, td5, td6, td7, td8, td9, td10);
            container.append(tr);
        }
    }
}

$('#button-search').on('click', function () {
    let input = $('#input-search-admin');
    let text = input.val();
    if (text !== "") {
        input.val('');
        searchFriend(text);
    }
})

function searchFriend(text) {
    let friends = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < friends.length; i++) {
            if (friends[i].friendReceiver.username.toLowerCase().includes(txt.toLowerCase()) ||
                friends[i].friendReceiver.fullName.toLowerCase().includes(txt.toLowerCase()) ||
                friends[i].friendReceiver.email.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(friends[i]);
                else {
                    if (result.filter(x => x.id === friends[i].id).length === 0)
                        result.push(friends[i]);
                }
            } else {
                if (friends[i].friendReceiver.age.toString() === txt.toLowerCase() ||
                    friends[i].friendReceiver.rating.toString() === txt.toLowerCase() ||
                    termingDate(friends[i].createdAt).toString() === txt.toLowerCase()) {
                    if (result.length === 0)
                        result.push(friends[i]);
                    else {
                        if (result.filter(x => x.id === friends[i].id).length === 0)
                            result.push(friends[i]);
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableFriends(result);
    } else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}