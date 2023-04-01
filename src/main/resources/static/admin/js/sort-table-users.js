$('.sort-table-user').on('click', function (e){
    let users = data;
    let column = $(this);
    let list = $("#table-list").attr("data-table");
    let img;
    let name = column.attr("data-name");
    let tag = column.attr("data-tag");
    switch (name){
        case "Username":
            img = $('#user');
            if(tag==="down") {
                let data = users.sort((x, y) => x.username.localeCompare(y.username));
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => y.username.localeCompare(x.username));
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "FullName":
            img = $('#full');
            if(tag==="down") {
                let data = users.sort((x, y) => x.fullName.localeCompare(y.fullName));
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => y.fullName.localeCompare(x.fullName));
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "Age":
            img = $('#age');
            if(tag==="down") {
                let data = users.sort((x, y) => x.age - y.age);
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => y.age - x.age);
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "Email":
            img = $('#eml');
            if(tag==="down") {
                let data = users.sort((x, y) => x.email.localeCompare(y.email));
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => y.email.localeCompare(x.email));
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "Rating":
            img = $('#rat');
            if(tag==="down") {
                let data = users.sort((x, y) => x.rating - y.rating);
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => y.rating - x.rating);
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "CreatedAt":
            img = $('#crt');
            if(tag==="down") {
                let data = users.sort((x, y) => new Date(x.createdAt) - new Date(y.createdAt));
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => new Date(y.createdAt) - new Date(x.createdAt));
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        case "UpdatedAt":
            img = $('#urt');
            if(tag==="down") {
                let data = users.sort((x, y) => new Date(x.updatedAt) - new Date(y.updatedAt));
                printTableUsers(data, list);
                column.attr("data-tag", "up");
                img.attr("src","/img/icon/up-arrow.png");
            }else{
                let data = users.sort((x, y) => new Date(y.updatedAt) - new Date(x.updatedAt));
                printTableUsers(data, list);
                column.attr("data-tag", "down");
                img.attr("src","/img/icon/down-arrow.png");
            }
            break;
        default:
            break;
    }
})

function printTableUsers(data, list){
    let container = document.getElementById('body-table');
    container.innerHTML = "";
    if(data!=null){
        for(let i=0; i<data.length; i++){
            let tr = document.createElement('tr');
            let td1 = document.createElement('td');
            let img1 = document.createElement('img');
            img1.src = data[i].avatarUrl;
            img1.style.borderRadius = "50%";
            img1.style.paddingBottom = "5px";
            img1.width = 50;
            img1.alt = "avatar";
            td1.appendChild(img1);
            let td2 = document.createElement('td');
            td2.innerText = data[i].username;
            let td3 = document.createElement('td');
            td3.innerText = data[i].fullName==null?"NULL":data[i].fullName;
            let td4 = document.createElement('td');
            td4.innerText = data[i].age;
            let td5 = document.createElement('td');
            td5.innerText = data[i].email;
            let td6 = document.createElement('td');
            td6.innerText = data[i].status;
            let td7 = document.createElement('td');
            td7.innerText = data[i].rating;
            let td8 = document.createElement('td');
            td8.innerText = correctDate(data[i].createdAt);
            let td9 = document.createElement('td');
            td9.innerText = correctDate(data[i].updatedAt);
            let td10 = document.createElement('td');
            let a2 = document.createElement('a');
            if(list==="users-list") {
                a2.href = "/admin/users/view/" + data[i].id;
                a2.title = "View";
                let img2 = document.createElement('img');
                img2.src = "/img/icon/view.png";
                img2.width = 30;
                a2.appendChild(img2);
                td10.appendChild(a2);
                let a3 = document.createElement('a');
                a3.href = "/admin/users/edit/" + data[i].id;
                a3.style.marginLeft = "4px";
                a3.title = "Edit";
                let img3 = document.createElement('img');
                img3.src = "/img/icon/edit.png";
                img3.width = 30;
                a3.appendChild(img3);
                td10.appendChild(a3);
                let a4 = document.createElement('a');
                a4.href = "/admin/users/newpass/" + data[i].id;
                a4.style.marginLeft = "3px";
                a4.title = "Edit Password";
                let img4 = document.createElement('img');
                img4.src = "/img/icon/password.png";
                img4.width = 30;
                a4.appendChild(img4);
                td10.appendChild(a4);
                let a5 = document.createElement('a');
                a5.href = "#";
                a5.style.marginLeft = "4px";
                a5.title = "Delete";
                a5.dataset.id = data[i].id;
                a5.dataset.name = data[i].username;
                a5.dataset.toggle = "modal";
                a5.dataset.target = "#DeleteModal";
                let img5 = document.createElement('img');
                img5.src = "/img/icon/delete.png";
                img5.width = 30;
                a5.appendChild(img5);
                td10.appendChild(a5);
            }else{
                a2.href = "/admin/users/recovery/" + data[i].id;
                a2.title = "Recovery";
                let img2 = document.createElement('img');
                img2.src = "/img/icon/restore.png";
                img2.width = 30;
                a2.appendChild(img2);
                td10.appendChild(a2);
            }
            tr.append(td1, td2, td3, td4, td5, td6, td7, td8, td9, td10);
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
        searchUser(text, list);
    }
})

function searchUser(text, list) {
    let users = data;
    let arrText = text.split(' ');
    let result = [];
    let answer = "";
    for (let txt of arrText) {
        for (let i = 0; i < users.length; i++) {
            if (users[i].username.toLowerCase().includes(txt.toLowerCase()) ||
                users[i].fullName.toLowerCase().includes(txt.toLowerCase()) ||
                users[i].email.toLowerCase().includes(txt.toLowerCase())) {
                if (result.length === 0)
                    result.push(users[i]);
                else {
                    if (result.filter(x => x.id === users[i].id).length === 0)
                        result.push(users[i]);
                }
            } else {
                if (users[i].age.toString()===txt || users[i].rating.toString()===txt) {
                    if (result.length === 0)
                        result.push(users[i]);
                    else {
                        if (result.filter(x => x.id === users[i].id).length === 0)
                            result.push(users[i]);
                    }
                }
            }
        }
    }
    if (result.length !== 0) {
        answer = "Search result - \"" + text.toUpperCase() + "\"";
        alertInfo(answer);
        printTableUsers(result, list);
    }else {
        answer = "Search result - \"" + text.toUpperCase() + "\" returned nothing!";
        alertInfo(answer);
    }
}